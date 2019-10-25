package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.SmsService;
import io.choerodon.notify.domain.*;
import io.choerodon.notify.infra.asserts.SendSettingAssertHelper;
import io.choerodon.notify.infra.asserts.SmsConfigAssertHelper;
import io.choerodon.notify.infra.asserts.TemplateAssertHelper;
import io.choerodon.notify.infra.dto.SmsConfigDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SmsSendType;
import io.choerodon.notify.infra.mapper.RecordMapper;
import io.choerodon.notify.infra.mapper.SmsConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author superlee
 * @since 2019-07-24
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsServiceImpl.class);

    private static final String FAILED = "FAILED";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Pattern pattern = Pattern.compile("(\\$\\{)(.+)\\}");

    private RestTemplate restTemplate = new RestTemplate();

    private final SendSettingAssertHelper sendSettingAssertHelper;

    private final TemplateAssertHelper templateAssertHelper;

    private final SmsConfigAssertHelper smsConfigAssertHelper;

    private final SmsConfigMapper smsConfigMapper;

    private final RecordMapper recordMapper;

    public SmsServiceImpl(SendSettingAssertHelper sendSettingAssertHelper,
                          TemplateAssertHelper templateAssertHelper,
                          SmsConfigAssertHelper smsConfigAssertHelper,
                          RecordMapper recordMapper,
                          SmsConfigMapper smsConfigMapper) {
        this.sendSettingAssertHelper = sendSettingAssertHelper;
        this.templateAssertHelper = templateAssertHelper;
        this.smsConfigAssertHelper = smsConfigAssertHelper;
        this.recordMapper = recordMapper;
        this.smsConfigMapper = smsConfigMapper;
    }

    @Override
    public void send(NoticeSendDTO noticeSendDTO) {
        Long organizationId = noticeSendDTO.getSourceId();
        if (organizationId == null) {
            throw new FeignException("error.send.sms.organizationId.null");
        }
        String code = noticeSendDTO.getCode();
        if (code == null) {
            throw new FeignException("error.send.sms.code.null");
        }
        SendSetting sendSetting = sendSettingAssertHelper.sendSettingNotExisted(code);

        Long templateId = sendSetting.getSmsTemplateId();
        Template template = templateAssertHelper.templateNotExisted(templateId);
        String content = template.getSmsContent();
        if (!"sms".equals(template.getMessageType()) || StringUtils.isEmpty(content)) {
            LOGGER.warn("illegal sms template, id: {}", templateId);
            throw new FeignException("error.illegal.sms.template");
        }

        SmsConfigDTO smsConfig =
                smsConfigAssertHelper.smsConfigNotExisted(SmsConfigAssertHelper.WhichColumn.ORGANIZATION_ID, organizationId);
        Map<String, Object> variable = noticeSendDTO.getParams();
        variable.put("secretKey", smsConfig.getSecretKey());
        variable.put("source", smsConfig.getSignature());
        JsonNode node = null;
        try {
            node = objectMapper.readTree(content);
            substitutionVariable(variable, node);
        } catch (IOException e) {
            throw new FeignException("error.parse.sms.template.json");
        }
        sendSms(smsConfig, node, template, variable);
    }

    @Override
    public SmsConfigDTO queryConfig(Long organizationId) {
        SmsConfigDTO dto = new SmsConfigDTO();
        dto.setOrganizationId(organizationId);
        return smsConfigMapper.selectOne(dto);
    }

    @Override
    public SmsConfigDTO updateConfig(Long id, SmsConfigDTO smsConfigDTO) {
        SmsConfigDTO dto = smsConfigMapper.selectByPrimaryKey(id);
        boolean doInsert = (dto == null);
        validateSendType(smsConfigDTO);
        smsConfigDTO.setOrganizationId(0L);
        if (doInsert) {
            smsConfigMapper.insertSelective(smsConfigDTO);
        } else {
            smsConfigDTO.setId(id);
            if (smsConfigDTO.getObjectVersionNumber() == null) {
                throw new CommonException("error.sms.objectVersionNumber.null");
            }
            smsConfigMapper.updateByPrimaryKeySelective(smsConfigDTO);
        }
        return smsConfigMapper.selectByPrimaryKey(id);
    }

    private void validateSendType(SmsConfigDTO smsConfigDTO) {
        String sendType = smsConfigDTO.getSendType();
        if (!SmsSendType.contains(sendType)) {
            throw new CommonException("error.sms.config.illegal.sendType");
        }
        if (SmsSendType.isSingle(sendType) && StringUtils.isEmpty(smsConfigDTO.getSingleSendApi())) {
            throw new CommonException("error.sms.config.singleSendApi.empty");
        }
        if (SmsSendType.isBatch(sendType) && StringUtils.isEmpty(smsConfigDTO.getBatchSendApi())) {
            throw new CommonException("error.sms.config.batchSendApi.empty");
        }
        if (SmsSendType.isAsync(sendType) && StringUtils.isEmpty(smsConfigDTO.getAsyncSendApi())) {
            throw new CommonException("error.sms.config.asyncSendApi.empty");
        }
    }

    private void sendSms(SmsConfigDTO smsConfig, JsonNode node, Template template, Map<String, Object> variable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JsonNode> entity = new HttpEntity<>(node, headers);

        StringBuilder builder = new StringBuilder();
        builder.append(smsConfig.getHostAddress()).append(StringUtils.isEmpty(smsConfig.getHostPort()) ? "" : smsConfig.getHostPort());
        String sendType = smsConfig.getSendType();

        switch (SmsSendType.get(sendType)) {
            case SINGLE:
                sendSingleSms(smsConfig, template, variable, entity, builder);
                break;
            case BATCH:
                sendBatchSms(smsConfig, template, variable, entity, builder);
                break;
            case ASYNC:
                sendAsyncSms(smsConfig, entity, builder);
                break;
            default:
                throw new FeignException("error.illegal.sendType");
        }

    }

    private void sendSingleSms(SmsConfigDTO smsConfig, Template template, Map<String, Object> variable, HttpEntity<JsonNode> entity, StringBuilder builder) {
        Record record = initRecord(template, variable);
        builder.append(smsConfig.getSingleSendApi());
        try {
            ResponseEntity<CrlandSmsResponse> response = restTemplate.postForEntity(builder.toString(), entity, CrlandSmsResponse.class);
            CrlandSmsResponse crlandSmsResponse = response.getBody();
            processRecordByResponse(record, crlandSmsResponse);
        } catch (Exception e) {
            record.setReceiveAccount((String) variable.get("mobile"));
            record.setStatus(FAILED);
            record.setFailedReason("调用远程接口发短信异常");
            String message = ((HttpServerErrorException) e).getResponseBodyAsString();
            LOGGER.error("invoke single sms api failed, exception: {}", message);
            throw new FeignException("error.invoke.single.sms.api", message);
        } finally {
            recordMapper.insertSelective(record);
        }
    }

    private void processRecordByResponse(Record record, CrlandSmsResponse crlandSmsResponse) {
        record.setReceiveAccount(crlandSmsResponse.getMobile());
        if (CrlandSmsResponse.SendStatus.isSuccess(crlandSmsResponse.getSendStatus())) {
            record.setStatus("COMPLETED");
        } else if (CrlandSmsResponse.SendStatus.isFail(crlandSmsResponse.getSendStatus())) {
            record.setStatus(FAILED);
            record.setFailedReason(crlandSmsResponse.getDescription());
        }
    }

    private Record initRecord(Template template, Map<String, Object> variable) {
        String businessType = template.getBusinessType();
        Record record = new Record();
        record.setBusinessType(businessType);
        record.setRetryCount(0);
        try {
            record.setVariables(objectMapper.writeValueAsString(variable));
        } catch (JsonProcessingException e) {
            throw new FeignException("error.parse.object.to.string");
        }
        record.setTemplateId(template.getId());
        record.setMessageType("sms");
        return record;
    }

    private void sendBatchSms(SmsConfigDTO smsConfig, Template template, Map<String, Object> variable, HttpEntity<JsonNode> entity, StringBuilder builder) {
        builder.append(smsConfig.getBatchSendApi());
        Map<String, Object> map = new HashMap<>(5);
        map.put("variable", variable);
        map.put("template", template);
        try {
            ResponseEntity<List<CrlandSmsResponse>> response = restTemplate.exchange(builder.toString(), HttpMethod.POST, entity, new ParameterizedTypeReference<List<CrlandSmsResponse>>() {
            });
            map.put("success", "true");
            map.put("responses", response.getBody());
            Observable
                    .just(map)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::recordPersistent);
        } catch (Exception e) {
            map.put("success", "false");
            Observable
                    .just(map)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::recordPersistent);
            String message = ((HttpServerErrorException) e).getResponseBodyAsString();
            LOGGER.error("invoke single sms api failed, exception: {}", message);
            throw new FeignException("error.invoke.batch.sms.api", message);
        }
    }

    private void recordPersistent(Map<String, Object> map) {
        boolean success = Boolean.parseBoolean((String) map.get("success"));
        Template template = (Template) map.get("template");
        Map<String, Object> variable = (Map<String, Object>) map.get("variable");
        if (success) {
            List<CrlandSmsResponse> crlandSmsResponses = (List<CrlandSmsResponse>) map.get("responses");
            crlandSmsResponses.forEach(resp -> {
                Record record = initRecord(template, variable);
                processRecordByResponse(record, resp);
                recordMapper.insertSelective(record);
            });
        } else {
            String mobile = (String) variable.get("mobile");
            List<String> mobiles = Arrays.asList(mobile.split(","));
            mobiles.forEach(m -> {
                Record record = initRecord(template, variable);
                record.setReceiveAccount(m);
                record.setStatus(FAILED);
                record.setFailedReason("调用远程接口发短信异常");
                recordMapper.insertSelective(record);
            });
        }
    }

    private void sendAsyncSms(SmsConfigDTO smsConfig, HttpEntity<JsonNode> entity, StringBuilder builder) {
        builder.append(smsConfig.getAsyncSendApi());
        try {
            restTemplate.postForEntity(builder.toString(), entity, String.class);
        } catch (Exception e) {
            String message = ((HttpServerErrorException) e).getResponseBodyAsString();
            LOGGER.error("invoke single sms api failed, exception: {}", message);
            throw new FeignException("error.invoke.async.sms.api", message);
        }
    }

    /**
     * 解析json,用map的值替换${}
     *
     * @param params 值map
     * @param node   json node
     * @return
     */
    private void substitutionVariable(Map<String, Object> params, JsonNode node) {
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            JsonNode jsonNode = node.get(field);
            if (jsonNode.isContainerNode()) {
                substitutionVariable(params, jsonNode);
            } else {
                String data = jsonNode.asText();
                Matcher matcher = pattern.matcher(data);
                if (matcher.find()) {
                    String value = matcher.group(2);
                    String variable = (String) params.get(value);
                    if (variable != null && jsonNode.isTextual()) {
                        ((ObjectNode) node).put(field, variable);
                    }
                }
            }
        }
    }
}
