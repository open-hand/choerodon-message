package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.pojo.DefaultAutowiredField;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.SiteMsgRecord;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.send.SendMessagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("pmWsSendService")
public class WebSocketWsSendServiceImpl implements WebSocketSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSendService.class);
    public static final String MSG_TYPE_PM = "site-msg";
    private final TemplateRender templateRender;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private final WebSocketHelper webSocketHelper;

    private final TemplateService templateService;

    public WebSocketWsSendServiceImpl(TemplateRender templateRender,
                                      TemplateService templateService,
                                      WebSocketHelper webSocketHelper,
                                      SiteMsgRecordMapper siteMsgRecordMapper) {
        this.templateRender = templateRender;
        this.templateService = templateService;
        this.webSocketHelper = webSocketHelper;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
    }

    @Override
    public void sendSiteMessage(String code, Map<String, Object> params, Set<UserDTO> targetUsers, Long sendBy, String senderType, SendSettingDTO sendSettingDTO) {
        LOGGER.info(">>>START_SENDING_SITE_MESSAGE>>> Send a site message to the user.[INFO:send_setting_code:'{}' - users:{} ]", sendSettingDTO.getCode(), targetUsers);
        //1. 获取该发送设置的站内信模版
        Template tmp = null;
        try {
            tmp = templateService.getOne(new Template()
                    .setSendingType(SendingTypeEnum.PM.getValue())
                    .setSendSettingCode(sendSettingDTO.getCode()));
        } catch (Exception e) {
            LOGGER.error(">>>CANCEL_SENDING_SITE_MSG>>> No valid templates available.");
            return;
        }
        Template template = tmp;
        //2. 发送站内信
        List<SiteMsgRecord> siteMsgRecords = new LinkedList<>();
        targetUsers.forEach(user -> {
            try {
                //2.1.若循环至记录达到100条，则进行批量插入（oracle可以批量插入超过1000条,但是超过太多则耗时长）
                if (siteMsgRecords.size() >= 99) {
                    int num = siteMsgRecordMapper.batchInsert(siteMsgRecords);
                    LOGGER.info(">>>SENDING_SITE_MSG>>>Insert {} pieces of site msg records in batches", num);
                    siteMsgRecords.clear();
                }
                //2.2.user主键不为空，则发送站内信
                if (ObjectUtils.isEmpty(user.getId())) {
                    //2.2.1.完善参数信息
                    Map<String, Object> userParams = DefaultAutowiredField.autowiredDefaultParams(params, user);
                    //2.2.2.融参
                    String content = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.CONTENT);
                    String title = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.TITLE);
                    //2.2.3.插入记录
                    SiteMsgRecord siteMsgRecord = new SiteMsgRecord(user.getId(), title, content);
                    siteMsgRecord.setSendBy(sendBy);
                    siteMsgRecord.setSenderType(senderType);
                    siteMsgRecord.setBacklogFlag(sendSettingDTO.getBacklogFlag());
                    siteMsgRecords.add(siteMsgRecord);
                }
            } catch (IOException | TemplateException e) {
                throw new CommonException("error.templateRender.renderError", e);
            } catch (BadSqlGrammarException e) {
                throw new CommonException("error.siteMsgContent.renderError", e);
            }
        });
        //2.3.插入剩余记录
        int num = siteMsgRecordMapper.batchInsert(siteMsgRecords);
        LOGGER.info(">>>SENDING_SITE_MSG>>>Insert {} pieces of site msg records in batches", num);
        //2.4.发送站内信
        if (ObjectUtils.isEmpty(sendSettingDTO.getIsSendInstantly()) && sendSettingDTO.getIsSendInstantly()) {
            targetUsers.forEach(user -> {
                String key = "choerodon:msg:site-msg:" + user.getId();
                webSocketHelper.sendMessageByKey(key, new SendMessagePayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(user.getId())));
            });
        }
    }

    @Override
    public void sendWebSocket(String code, String id, String message) {
        String key = "choerodon:msg:" + code + ":" + id;
        webSocketHelper.sendMessageByKey(key, new SendMessagePayload<>(code, key, message));
    }
}
