package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dengyouquan
 **/
@Component
public class PmSendTask {
    private final Logger logger = LoggerFactory.getLogger(PmSendTask.class);
    private static final String MSG_TYPE_PM = "sit-msg";
    private final TemplateMapper templateMapper;
    private final NoticesSendService noticesSendService;
    private final ObjectMapper objectMapper;
    private final UserFeignClient userFeignClient;

    public PmSendTask(TemplateMapper templateMapper, NoticesSendService noticesSendService, UserFeignClient userFeignClient) {
        this.templateMapper = templateMapper;
        this.noticesSendService = noticesSendService;
        this.userFeignClient = userFeignClient;
        objectMapper = new ObjectMapper();
    }

    @JobTask(maxRetryCount = 2, code = "sendPm", params = {
            @JobParam(name = "code", defaultValue = MSG_TYPE_PM),
            @JobParam(name = "templateCode", defaultValue = "msg"),
            @JobParam(name = "variables", defaultValue = "")
    })
    public void sendPm(Map<String, Object> map) {
        String code = (String) map.get("code");
        String templateCode = (String) map.get("templateCode");
        String mapJson = (String) map.get("variables");
        Map<String, Object> params = new HashMap<>(0);
        if (!StringUtils.isEmpty(mapJson)) {
            try {
                params = objectMapper.readValue(mapJson, Map.class);
            } catch (IOException e) {
                throw new CommonException("error.pmSendTask.paramsJsonNotValid", e);
            }
        }
        validatorCode(code, templateCode);
        Map<String, Object> finalParams = params;
        long startTime = System.currentTimeMillis();
        logger.info("send pm started");
        Arrays.stream(userFeignClient.getUserIds().getBody()).forEach(id -> {
            WsSendDTO wsSendDTO = new WsSendDTO();
            wsSendDTO.setParams(finalParams);
            wsSendDTO.setTemplateCode(templateCode);
            wsSendDTO.setCode(code);
            wsSendDTO.setId(id);
            noticesSendService.sendWs(wsSendDTO);
        });
        long endTime = System.currentTimeMillis();
        logger.info("send pm completed. speed time:{} millisecond", (endTime - startTime));
    }

    private void validatorCode(String code, String templateCode) {
        if (!MSG_TYPE_PM.equals(code)) {
            throw new CommonException("error.pmSendTask.codeNotValid");
        }
        if (StringUtils.isEmpty(templateCode)) {
            throw new CommonException("error.pmSendTask.templateCodeEmpty");
        }
        Template query = new Template();
        query.setCode(templateCode);
        query.setMessageType(MessageType.PM.getValue());
        Template template = templateMapper.selectOne(query);
        if (template == null) {
            throw new CommonException("error.pmSendTask.templateCodeNotExist");
        }
    }
}