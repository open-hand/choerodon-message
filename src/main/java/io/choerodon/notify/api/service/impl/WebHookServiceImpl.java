package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.exception.WebHookException;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.dto.WebHookDTO;
import io.choerodon.notify.infra.dto.WebHookMessageSettingDTO;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.enums.WebHookTypeEnum;
import io.choerodon.notify.infra.mapper.MessegeSettingMapper;
import io.choerodon.notify.infra.mapper.WebHookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class WebHookServiceImpl implements WebHookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
    private WebHookMapper webHookMapper;
    private TemplateService templateService;
    private TemplateRender templateRender;
    private MessegeSettingMapper messegeSettingMapper;


    public WebHookServiceImpl(WebHookMapper webHookMapper, TemplateService templateService, TemplateRender templateRender, MessegeSettingMapper messegeSettingMapper) {
        this.webHookMapper = webHookMapper;
        this.templateService = templateService;
        this.templateRender = templateRender;
        this.messegeSettingMapper = messegeSettingMapper;
    }

    @Override
    public void trySendWebHook(NoticeSendDTO dto, SendSettingDTO sendSetting) {
        //0. 若发送设置非项目层 / 发送信息未指定项目Id 则取消发送
        if (!ResourceLevel.PROJECT.value().equalsIgnoreCase(sendSetting.getLevel())
                || ObjectUtils.isEmpty(dto.getSourceId())
                || dto.getSourceId().equals(0L)) {
            LOGGER.warn(">>>CANCEL_SENDING_WEBHOOK>>> Missing project information.");
            return;
        }

        //1. 获取该发送设置的WebHook模版
        Template template = null;
        try {
            template = templateService.getOne(new Template()
                    .setSendingType(SendingTypeEnum.WH.getValue())
                    .setSendSettingCode(sendSetting.getCode()));
        } catch (Exception e) {
            LOGGER.warn(">>>CANCEL_SENDING_WEBHOOK>>> No valid templates available.");
            return;
        }

        //2. 获取项目下配置该发送设置的WebHook
        Set<WebHookDTO> hooks = webHookMapper.selectBySendSetting(dto.getSourceId(), sendSetting.getId());
        if (CollectionUtils.isEmpty(hooks)) {
            LOGGER.info(">>>CANCEL_SENDING_WEBHOOK>>> The send settings have not been associated with webhook.");
            return;
        }
        //3. 发送WebHook
        try {
            for (WebHookDTO hook : hooks) {
                Map<String, Object> userParams = dto.getParams();
                String content = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.CONTENT);
                if (WebHookTypeEnum.DINGTALK.getValue().equalsIgnoreCase(hook.getType())) {
                    sendDingTalk(hook, content);
                } else if (WebHookTypeEnum.WECHAT.getValue().equalsIgnoreCase(hook.getType())) {
                    sendWeChat(hook, content);
                } else if (WebHookTypeEnum.JSON.getValue().equalsIgnoreCase(hook.getType())) {
                    sendJson(hook, dto);
                } else {
                    throw new CommonException("Unsupported web hook type : {}", hook.getType());
                }
            }
        } catch (Exception e) {
            LOGGER.error(">>>SENDING_WEBHOOK_ERROR>>> An error occurred while sending the web hook", e.getMessage());
        }
    }

    private void sendDingTalk(WebHookDTO hook, String content) {
        RestTemplate template = new RestTemplate();
        Map<String, Object> request = new TreeMap<>();
        request.put("msgtype", "text");
        Map<String, Object> text = new TreeMap<>();
        text.put("content", content);
        request.put("text", text);
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private void sendWeChat(WebHookDTO hook, String content) {
        RestTemplate template = new RestTemplate();
        Map<String, Object> request = new TreeMap<>();
        request.put("msgtype", "text");
        Map<String, Object> text = new TreeMap<>();
        text.put("content", content);
        request.put("text", text);
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private void sendJson(WebHookDTO hook, NoticeSendDTO dto) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), dto, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private List<WebHookDTO> selectWebHookByProjectId(Long projectId) {
        WebHookDTO example = new WebHookDTO();
        example.setProjectId(projectId);
        return webHookMapper.select(example);
    }

    @Override
    public PageInfo<WebHookDTO> pagingWebHook(Pageable pageable, Long projectId, String name, String type, Boolean enableFlag, String params) {
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() -> webHookMapper.selectWebHookAll(projectId, name, type, enableFlag, params));
    }

    @Override
    public void check(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new WebHookException("error the name is not be null");
        }
        WebHookDTO webHookDTO = new WebHookDTO();
        webHookDTO.setName(name);
        if (!CollectionUtils.isEmpty(webHookMapper.select(webHookDTO))) {
            throw new WebHookException("The name is already exited");
        }
    }

    @Override
    @Transactional
    public WebHookDTO createWebHook(Long projectId, WebHookVO webHookVO) {
        if (projectId == null) {
            throw new CommonException("error.the.projectId.is.not.be.null");
        }
        WebHookDTO webHookDTO=new WebHookDTO();
        webHookDTO.setId(webHookVO.getId());
        webHookDTO.setName(webHookVO.getName());
        webHookDTO.setType(webHookVO.getType());
        webHookDTO.setWebhookPath(webHookVO.getWebhookPath());
        webHookDTO.setProjectId(projectId);
        List<WebHookDTO> webHookDTOS = webHookMapper.select(webHookDTO);
        if (!CollectionUtils.isEmpty(webHookDTOS)){
            throw new CommonException("error.the.webhook.is.aready.exited");
        }//不为空
        if (webHookMapper.insertSelective(webHookDTO) !=1){
            throw new CommonException("error.insert.is.failed!");
        }
        List<WebHookMessageSettingDTO> webHookMessageSettingDTOs=new ArrayList<>();
        WebHookMessageSettingDTO webHookMessageSettingDTO=null;
        Long[] ids = webHookVO.getIds();
        for (int i = 0; i < ids.length; i++) {
            webHookMessageSettingDTO=new WebHookMessageSettingDTO();
            webHookMessageSettingDTO.setWebhookId(webHookDTO.getId());
            webHookMessageSettingDTO.setSendSettingId(ids[i]);
            webHookMessageSettingDTOs.add(webHookMessageSettingDTO);
        }
        messegeSettingMapper.insertMessage(webHookMessageSettingDTOs);
        return webHookDTO;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public WebHookDTO updateWebHook(Long projectId, WebHookDTO webHookDTO) {
        WebHookDTO webHookDTO1 = webHookNotExisted(webHookDTO.getId());
        webHookMapper.updateByPrimaryKeySelective(webHookDTO);
        return webHookDTO1;
    }


    @Override
    public WebHookDTO deleteWebHook(Long id) {
        WebHookDTO webHookDTO = webHookNotExisted(id);
        webHookMapper.delete(webHookDTO);
        return webHookDTO;
    }

    private WebHookDTO webHookNotExisted(Long id) {
        WebHookDTO webHookDTO = new WebHookDTO();
        webHookDTO.setId(id);
        WebHookDTO webHookDTO1 = webHookMapper.selectByPrimaryKey(webHookDTO);
        if (ObjectUtils.isEmpty(webHookDTO1)) {
            throw new CommonException("error the webhook entity is null!");
        }
        return webHookDTO1;
    }

    @Override
    public WebHookDTO disableWebHook(Long id) {
        WebHookDTO webHookDTO=new WebHookDTO();
        webHookDTO.setId(id);
        WebHookDTO webHookDTO1 = webHookMapper.selectByPrimaryKey(webHookDTO);
        if (ObjectUtils.isEmpty(webHookDTO1)){
            throw new CommonException("error.the.webhook.not.exited");
        }
        if (webHookDTO1.getEnableFlag()==true){
            throw new CommonException("error.the.update.enableFlage.status");
        }
        return updateStatus(id, false);
    }

    @Override
    public WebHookDTO enableWebHook(Long id) {
        WebHookDTO webHookDTO=new WebHookDTO();
        webHookDTO.setId(id);
        WebHookDTO webHookDTO1 = webHookMapper.selectByPrimaryKey(webHookDTO);
        if (ObjectUtils.isEmpty(webHookDTO1)){
            throw new CommonException("error.the.webhook.not.exited");
        }
        if (webHookDTO1.getEnableFlag()==true){
            throw new CommonException("error.the.update.enableFlage.status");
        }
        return updateStatus(id, true);
    }

    private WebHookDTO updateStatus(Long id, boolean able) {
        WebHookDTO webHookDTO = webHookNotExisted(id);
        webHookDTO.setEnableFlag(able);
        if (webHookMapper.updateByPrimaryKeySelective(webHookDTO) != 1) {
            throw new CommonException("error.the.webhook.is.not.existsted");
        }
        return webHookDTO;
    }

}
