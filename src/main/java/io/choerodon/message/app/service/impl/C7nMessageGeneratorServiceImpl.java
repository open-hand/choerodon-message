package io.choerodon.message.app.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.DingTalkMsgType;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.core.convert.CommonConverter;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.impl.MessageGeneratorServiceImpl;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

/**
 * @author scp
 * @since 2022/5/26
 * c7n覆盖{@link #generateMessage}
 */
@Primary
@Service
public class C7nMessageGeneratorServiceImpl extends MessageGeneratorServiceImpl {
    @Autowired
    private ObjectMapper objectMapper;

    public C7nMessageGeneratorServiceImpl(MessageClientProperties messageClientProperties, MessageTemplateService messageTemplateService, ObjectMapper objectMapper) {
        super(messageClientProperties, messageTemplateService, objectMapper);
    }


    @Override
    public Message generateMessage(DingTalkSender dingTalkSender, Message message) {
        //钉钉发送消息参数
        Map<String, String> params = new HashMap<>(16);
        try {
            params.put(DingTalkSender.FIELD_AGENT_ID, String.valueOf(dingTalkSender.getAgentId()));
            if (!CollectionUtils.isEmpty(dingTalkSender.getUserIdList())) {
                params.put(DingTalkSender.FIELD_USER_ID_LIST, objectMapper.writeValueAsString(dingTalkSender.getUserIdList()));
            }
            if (!CollectionUtils.isEmpty(dingTalkSender.getDeptIdList())) {
                params.put(DingTalkSender.FIELD_DEPT_ID_LIST, objectMapper.writeValueAsString(dingTalkSender.getDeptIdList()));
            }
            if (dingTalkSender.getToAllUser() != null) {
                params.put(DingTalkSender.FIELD_TO_ALL_USER, objectMapper.writeValueAsString(dingTalkSender.getToAllUser()));
            }
            if (dingTalkSender.getMsgType() != null) {
                params.put(DingTalkSender.FIELD_MSG_TYPE, dingTalkSender.getMsgType().getValue());
            }
            if (dingTalkSender.getMedia() != null) {
                generateDingTalkMediaParam(params, dingTalkSender.getMedia(), dingTalkSender.getMsgType());
            }
        } catch (Exception e) {
            throw new CommonException(e);
        }
        // todo 覆盖逻辑
        // 将业务消息参数 保存记录 用于消息重试
        params.putAll(dingTalkSender.getArgs());
        Message messageContent;
        if (dingTalkSender.getMessage() != null) {
            messageContent = CommonConverter.beanConvert(Message.class, dingTalkSender.getMessage());
        } else if (StringUtils.isBlank(dingTalkSender.getMessageCode())) {
            try {
                return message.setSendArgs(objectMapper.writeValueAsString(params));
            } catch (JsonProcessingException e) {
                throw new CommonException(e);
            }
        } else {
            messageContent = generateMessage(dingTalkSender.getTenantId(), dingTalkSender.getMessageCode(), dingTalkSender.getLang(), dingTalkSender.getArgs(), params);
        }
        return message.setSubject(messageContent.getSubject())
                .setContent(messageContent.getContent())
                .setPlainContent(messageContent.getPlainContent())
                .setSendArgs(messageContent.getSendArgs())
                .setExternalCode(messageContent.getExternalCode())
                .setTemplateEditType(messageContent.getTemplateEditType());
    }

    private void generateDingTalkMediaParam(Map<String, String> params, DingTalkSender.Media media, DingTalkMsgType msgType) {
        switch (msgType) {
            case TEXT:
                params.put(DingTalkSender.FIELD_CONTENT, media.getText().getContent());
                break;
            case IMAGE:
                params.put(DingTalkSender.FIELD_MEDIA_ID, media.getImage().getMediaId());
                break;
            case VOICE:
                params.put(DingTalkSender.FIELD_MEDIA_ID, media.getVoice().getMediaId());
                params.put(DingTalkSender.FIELD_DURATION, media.getVoice().getDuration());
                break;
            case FILE:
                params.put(DingTalkSender.FIELD_MEDIA_ID, media.getFile().getMediaId());
                break;
            case LINK:
                params.put(DingTalkSender.FIELD_MESSAGE_URL, media.getLink().getMessageUrl());
                params.put(DingTalkSender.FIELD_PICURL, media.getLink().getPicUrl());
                params.put(DingTalkSender.FIELD_TITLE, media.getLink().getTitle());
                params.put(DingTalkSender.FIELD_TEXT, media.getLink().getText());
                break;
            case OA:
                params.put(DingTalkSender.FIELD_MESSAGE_URL, media.getOa().getMessageUrl());
                try {
                    params.put(DingTalkSender.FIELD_HEAD, objectMapper.writeValueAsString(media.getOa().getHead()));
                    params.put(DingTalkSender.FIELD_BODY, objectMapper.writeValueAsString(media.getOa().getBody()));
                } catch (JsonProcessingException e) {
                    throw new CommonException(e);
                }
                break;
            case ACTION_CARD:
                params.put(DingTalkSender.FIELD_TITLE, media.getActionCard().getTitle());
                params.put(DingTalkSender.FIELD_MARKDOWN, media.getActionCard().getMarkdown());
                if (StringUtils.isBlank(media.getActionCard().getBtnOrientation())) {
                    params.put(DingTalkSender.FIELD_SINGLE_TITLE, media.getActionCard().getSingleTitle());
                    params.put(DingTalkSender.FIELD_SINGLE_URL, media.getActionCard().getSingleUrl());
                } else {
                    params.put(DingTalkSender.FIELD_BTN_ORIENTATION, media.getActionCard().getBtnOrientation());
                    try {
                        params.put(DingTalkSender.FIELD_BTN_JSON_LIST, objectMapper.writeValueAsString(media.getActionCard().getBtnJsonList()));
                    } catch (JsonProcessingException e) {
                        throw new CommonException(e);
                    }
                }
                break;
            default:
                break;
        }
    }

}
