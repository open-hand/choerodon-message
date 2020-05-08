package io.choerodon.message.app.service.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.enums.SendingTypeEnum;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
@Service
public class SendSettingC7nServiceImpl implements SendSettingC7nService {

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private MessageClientProperties messageClientProperties;

    @Override
    public SendSettingVO queryByTempServerId(Long tempServerId) {
        SendSettingVO sendSettingVO = (SendSettingVO) templateServerService.getTemplateServer(0L, tempServerId);
        if (!CollectionUtils.isEmpty(sendSettingVO.getServerList())) {
            List<MessageTemplate> messageTemplates = new ArrayList<>();
            sendSettingVO.getServerList().forEach(t -> {
                        messageTemplates.add(messageTemplateService.getMessageTemplate(BaseConstants.DEFAULT_TENANT_ID, t.getTemplateCode(), messageClientProperties.getDefaultLang()));
                        setSendTypeEnable(t, sendSettingVO);
                    }
            );
            sendSettingVO.setMessageTemplates(messageTemplates);
        }
        return sendSettingVO;
    }

    /**
     * 设置消息类型（邮件、站内信、短信等）是否启用
     *
     * @param templateServerLine
     * @param sendSettingVO
     */
    private void setSendTypeEnable(TemplateServerLine templateServerLine, SendSettingVO sendSettingVO) {
        switch (SendingTypeEnum.valueOf(templateServerLine.getTypeCode())) {
            case EMAIL:
                sendSettingVO.setEmailEnabledFlag(templateServerLine.get);
                break;
            case SMS:
                sendSettingVO.setSmsEnabledFlag(true);
                break;
            case WH:
                sendSettingVO.setWebhookEnabledFlag(true);
                break;
            case WHJSON:
                sendSettingVO.setWebhookJsonEnabledFlag(true);
                break;
            default:
        }
    }
}
