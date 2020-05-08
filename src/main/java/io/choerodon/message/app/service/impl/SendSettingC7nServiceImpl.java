package io.choerodon.message.app.service.impl;


import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.dto.MessageTemplateRelDTO;
import io.choerodon.message.infra.dto.SendSettingCategoryDTO;
import io.choerodon.message.infra.dto.SendSettingDetailDTO;
import io.choerodon.message.infra.dto.SendSettingDetailTreeDTO;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.mapper.MessageTemplateRelMapper;
import io.choerodon.message.infra.mapper.SendSettingCategoryMapper;
import io.choerodon.message.infra.mapper.SendSettingMapper;
import io.choerodon.message.infra.validator.CommonValidator;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private MessageTemplateRelMapper messageTemplateRelMapper;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private MessageClientProperties messageClientProperties;
    @Autowired
    private SendSettingCategoryMapper sendSettingCategoryMapper;
    @Autowired
    private SendSettingMapper sendSettingMapper;

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
        List<MessageTemplateRelDTO> templateRelDTOS = messageTemplateRelMapper.select(new MessageTemplateRelDTO(sendSettingVO.getMessageCode()));
        if (!CollectionUtils.isEmpty(templateRelDTOS)) {
            templateRelDTOS.forEach(t -> setSendTypeEnable(t, sendSettingVO));
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
                sendSettingVO.setEmailEnabledFlag(templateServerLine.getEnabledFlag());
                break;
            case SMS:
                sendSettingVO.setSmsEnabledFlag(templateServerLine.getEnabledFlag());
                break;
            case WH:
                sendSettingVO.setWebhookEnabledFlag(templateServerLine.getEnabledFlag());
                break;
            default:
        }
    }

    private void setSendTypeEnable(MessageTemplateRelDTO messageTemplateRelDTO, SendSettingVO sendSettingVO) {
        switch (SendingTypeEnum.valueOf(messageTemplateRelDTO.getSendType())) {
            case WH:
                sendSettingVO.setWebhookEnabledFlag(messageTemplateRelDTO.getEnabledFlag());
                break;
            case WHJSON:
                sendSettingVO.setWebhookJsonEnabledFlag(messageTemplateRelDTO.getEnabledFlag());
                break;
            default:
        }
    }

    @Override
    public List<SendSettingDetailTreeDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig) {
        if (level == null) {
            throw new CommonException("error.level.null");
        }
        // 验证资源层级类型，project、organization、site
        CommonValidator.validatorLevel(level);

        // 查询 处于启用状态 允许配置 的对应层级的消息发送设置
        List<SendSettingDetailDTO> list = sendSettingMapper.queryByLevelAndAllowConfig(level, allowConfig);

        // 返回给客户端的消息发送设置列表
        List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS = new ArrayList<>();

        // key: 资源层级     value: categoryCode集合
        Map<String, Set<String>> categoryMap = new HashMap<>();
        categoryMap.put(ResourceLevel.valueOf(level.toUpperCase()).value(), new HashSet<>());

        // for循环里过滤掉不是level层级的categoryCode
        for (SendSettingDetailDTO sendSettingDTO : list) {
            Set<String> categoryCodes = categoryMap.get(sendSettingDTO.getLevel());
            if (categoryCodes != null) {
                categoryCodes.add(sendSettingDTO.getCategoryCode());
            }
        }
        getSecondSendSettingDetailTreeDTOS(categoryMap, sendSettingDetailTreeDTOS, list);

        // 过滤emailId、PmId、smsId不存在，以及parentId等于0的记录
        return sendSettingDetailTreeDTOS.stream().filter(s -> (s.getEmailTemplateId() != null || s.getPmTemplateId() != null || s.getSmsTemplateId() != null) || s.getParentId() == 0)
                .collect(Collectors.toList());
    }

    private void getSecondSendSettingDetailTreeDTOS(Map<String, Set<String>> categoryMap, List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS, List<SendSettingDetailDTO> sendSettingDetailDTOS) {
        int i = 1;
        // 将不同层级的categoryCode取出
        for (String level : categoryMap.keySet()) {
            for (String categoryCode : categoryMap.get(level)) {

                // 表示第一层的SendSettingDetailTreeDTO，parentId就是0
                SendSettingDetailTreeDTO sendSettingDetailTreeDTO = new SendSettingDetailTreeDTO();
                sendSettingDetailTreeDTO.setParentId(0L);

                SendSettingCategoryDTO categoryDTO = new SendSettingCategoryDTO();
                categoryDTO.setCode(categoryCode);
                categoryDTO = sendSettingCategoryMapper.selectOne(categoryDTO);
                sendSettingDetailTreeDTO.setName(categoryDTO.getName());
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTO.setCode(categoryDTO.getCode());

                //防止被过滤掉
                sendSettingDetailTreeDTO.setEmailTemplateId(0L);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdSendSettingDetailTreeDTOS(sendSettingDetailDTOS, level, categoryCode, secondParentId, sendSettingDetailTreeDTOS, i);

            }
        }
    }

    private int getThirdSendSettingDetailTreeDTOS(List<SendSettingDetailDTO> sendSettingDetailDTOS, String level, String categoryCode, Integer secondParentId, List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS, Integer i) {
        for (SendSettingDetailDTO sendSettingDetailDTO : sendSettingDetailDTOS) {
            // 取出指定层级、指定类别的消息发送设置，比如project层级的pro-management类别的所有消息发送设置
            if (sendSettingDetailDTO.getLevel().equals(level) && sendSettingDetailDTO.getCategoryCode().equals(categoryCode)) {
                SendSettingDetailTreeDTO sendSettingDetailTreeDTO = new SendSettingDetailTreeDTO();
                BeanUtils.copyProperties(sendSettingDetailDTO, sendSettingDetailTreeDTO);
                sendSettingDetailTreeDTO.setParentId((long) secondParentId);
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                i = i + 1;
            }
        }
        return i;
    }

}
