package io.choerodon.message.app.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.MessageServiceVO;
import io.choerodon.message.api.vo.MsgServiceTreeVO;
import io.choerodon.message.api.vo.SendSettingDetailTreeVO;
import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.dto.MessageTemplateRelDTO;
import io.choerodon.message.infra.dto.TemplateServerC7NDTO;
import io.choerodon.message.infra.enums.LevelType;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.feign.PlatformFeignClient;
import io.choerodon.message.infra.mapper.HzeroTemplateServerMapper;
import io.choerodon.message.infra.mapper.MessageTemplateRelMapper;
import io.choerodon.message.infra.mapper.TemplateServerC7nMapper;
import io.choerodon.message.infra.utils.ConversionUtil;
import io.choerodon.message.infra.validator.CommonValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
@Service
public class SendSettingC7nServiceImpl implements SendSettingC7nService {

    public static final String LOV_MESSAGE_CODE = "HMSG.TEMP_SERVER.SUBCATEGORY";
    public static final String LOV_ERROR_INFO = "error.get.lov.meaning";
    public static final String RESOURCE_DELETE_CONFIRMATION = "resourceDeleteConfirmation";

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private MessageTemplateRelMapper messageTemplateRelMapper;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private MessageClientProperties messageClientProperties;
    @Autowired
    private TemplateServerC7nMapper templateServerC7nMapper;
    @Autowired
    private PlatformFeignClient platformFeignClient;
    @Autowired
    private TemplateServerRepository templateServerRepository;
    @Autowired
    private TemplateServerLineRepository templateServerLineRepository;
    @Autowired
    private HzeroTemplateServerMapper hzeroTemplateServerMapper;


    @Override
    public Page<MessageServiceVO> pagingAll(String messageCode, String messageName, Boolean enabled, Boolean receiveConfigFlag, String params, PageRequest pageRequest, String firstCode, String secondCode) {
        Page<MessageServiceVO> serviceVOPage = PageHelper.doPageAndSort(pageRequest, () -> templateServerC7nMapper.selectTemplateServer(messageCode, messageName, secondCode, firstCode, enabled, receiveConfigFlag, params));
        Map<String, String> meaningsMap = getMeanings();
        serviceVOPage.getContent().forEach(t -> t.setMessageTypeValue(meaningsMap.get(t.getMessageType())));
        return serviceVOPage;
    }

    @Override
    public List<MsgServiceTreeVO> getMsgServiceTree() {
        List<TemplateServer> templateServers = templateServerRepository.selectAll();
        List<MsgServiceTreeVO> msgServiceTreeVOS = new ArrayList<>();
        MsgServiceTreeVO msgServiceTreeVO1 = new MsgServiceTreeVO();
        msgServiceTreeVO1.setParentId(0L);
        msgServiceTreeVO1.setId(1L);
        msgServiceTreeVO1.setName(LevelType.SITE.value());
        msgServiceTreeVO1.setCode(ResourceLevel.SITE.value());
        msgServiceTreeVOS.add(msgServiceTreeVO1);

        MsgServiceTreeVO msgServiceTreeVO2 = new MsgServiceTreeVO();
        msgServiceTreeVO2.setParentId(0L);
        msgServiceTreeVO2.setId(2L);
        msgServiceTreeVO2.setName(LevelType.ORGANIZATION.value());
        msgServiceTreeVO2.setCode(ResourceLevel.ORGANIZATION.value());
        msgServiceTreeVOS.add(msgServiceTreeVO2);

        MsgServiceTreeVO msgServiceTreeVO3 = new MsgServiceTreeVO();
        msgServiceTreeVO3.setParentId(0L);
        msgServiceTreeVO3.setId(3L);
        msgServiceTreeVO3.setName(LevelType.PROJECT.value());
        msgServiceTreeVO3.setCode(ResourceLevel.PROJECT.value());
        msgServiceTreeVOS.add(msgServiceTreeVO3);

        Map<String, Set<String>> categoryMap = new HashMap<>();
        categoryMap.put(ResourceLevel.SITE.value(), new HashSet<>());
        categoryMap.put(ResourceLevel.ORGANIZATION.value(), new HashSet<>());
        categoryMap.put(ResourceLevel.PROJECT.value(), new HashSet<>());
        for (TemplateServer templateServer : templateServers) {
            Set<String> categoryCodes = categoryMap.get(templateServer.getCategoryCode());
            if (categoryCodes != null) {
                categoryCodes.add(templateServer.getCategoryCode());
            }
        }
        getSecondMsgServiceTreeVOS(categoryMap, msgServiceTreeVOS, templateServers);
        return msgServiceTreeVOS;
    }

    @Override
    public void updateReceiveConfigFlag(Long tempServerId, Boolean receiveConfigFlag) {
        TemplateServer templateServer = templateServerRepository.selectByPrimaryKey(tempServerId);
        if (ObjectUtils.isEmpty(templateServer)) {
            throw new CommonException("error.query.tempServer");
        }
        templateServer.setReceiveConfigFlag(ConversionUtil.booleanConverToInteger(receiveConfigFlag));
        templateServerService.updateTemplateServer(templateServer);
    }

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

    @Override
    public SendSettingVO queryByCode(String messageCode) {
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setMessageCode(messageCode));
        if (ObjectUtils.isEmpty(templateServer)) {
            throw new CommonException("error.query.tempServer");
        }
        return queryByTempServerId(templateServer.getTempServerId());
    }

    @Override
    public void enableOrDisabled(String messageCode, Boolean status) {
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setMessageCode(messageCode));
        if (ObjectUtils.isEmpty(templateServer)) {
            throw new CommonException("error.query.tempServer");
        }
        templateServer.setEnabledFlag(ConversionUtil.booleanConverToInteger(status));
        templateServerService.updateTemplateServer(templateServer);
    }


    @Override
    public SendSettingVO updateSendSetting(Long id, SendSettingVO sendSettingVO) {
        TemplateServer templateServer = templateServerRepository.selectByPrimaryKey(id);
        templateServer.setReceiveConfigFlag(sendSettingVO.getReceiveConfigFlag());
        templateServerService.updateTemplateServer(templateServer);

        List<TemplateServerLine> lineList = templateServerService.listTemplateServerLine(id, BaseConstants.DEFAULT_TENANT_ID);
        if (!CollectionUtils.isEmpty(lineList)) {
            lineList.forEach(t -> setEnabledFlag(t, sendSettingVO));
            templateServerLineRepository.batchUpdateByPrimaryKey(lineList);
        }
        List<MessageTemplateRelDTO> templateRelDTOS = messageTemplateRelMapper.select(new MessageTemplateRelDTO(templateServer.getMessageCode()));
        if (!CollectionUtils.isEmpty(templateRelDTOS)) {
            templateRelDTOS.forEach(t -> {
                setEnabledFlag(t, sendSettingVO);
                messageTemplateRelMapper.updateByPrimaryKey(t);
            });
        }
        return sendSettingVO;
    }

    @Override
    public Boolean checkResourceDeleteEnabled() {
        TemplateServer sendSettingDTO = templateServerRepository.selectOne(new TemplateServer().setMessageCode(RESOURCE_DELETE_CONFIRMATION));
        return ConversionUtil.IntegerConverToBoolean(sendSettingDTO.getEnabledFlag());
    }

    private void getSecondMsgServiceTreeVOS(Map<String, Set<String>> categoryMap, List<MsgServiceTreeVO> msgServiceTreeVOS, List<TemplateServer> templateServers) {
        int i = 4;
        Map<String, String> meaningsMap = getMeanings();
        for (String level : categoryMap.keySet()) {
            for (String categoryCode : categoryMap.get(level)) {
                MsgServiceTreeVO msgServiceTreeVO = new MsgServiceTreeVO();
                if (level.equals(ResourceLevel.SITE.value())) {
                    msgServiceTreeVO.setParentId(1L);
                } else if (level.equals(ResourceLevel.ORGANIZATION.value())) {
                    msgServiceTreeVO.setParentId(2L);
                } else {
                    msgServiceTreeVO.setParentId(3L);
                }
                msgServiceTreeVO.setName(meaningsMap.get(categoryCode));
                msgServiceTreeVO.setId((long) i);
                msgServiceTreeVO.setCode(categoryCode);
                msgServiceTreeVOS.add(msgServiceTreeVO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdMsgServiceTreeVOS(templateServers, level, categoryCode, secondParentId, msgServiceTreeVOS, i);

            }
        }
    }

    private int getThirdMsgServiceTreeVOS(List<TemplateServer> sendSettingDTOS, String level, String categoryCode, Integer secondParentId, List<MsgServiceTreeVO> msgServiceTreeVOS, Integer i) {
        for (TemplateServer templateServer : sendSettingDTOS) {
            if (templateServer.getCategoryCode().equals(level) && templateServer.getSubcategoryCode().equals(categoryCode)) {
                MsgServiceTreeVO treeVO = new MsgServiceTreeVO();
                treeVO.setParentId((long) secondParentId);
                treeVO.setId((long) i);
                treeVO.setName(templateServer.getMessageName());
                treeVO.setEnabled(ConversionUtil.IntegerConverToBoolean(templateServer.getEnabledFlag()));
                treeVO.setCode(templateServer.getMessageCode());
                msgServiceTreeVOS.add(treeVO);
                i = i + 1;
            }
        }
        return i;
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
            case WEB:
                sendSettingVO.setPmEnabledFlag(templateServerLine.getEnabledFlag());
                break;
            default:
        }
    }

    private void setEnabledFlag(TemplateServerLine templateServerLine, SendSettingVO sendSettingVO) {
        switch (SendingTypeEnum.valueOf(templateServerLine.getTypeCode())) {
            case EMAIL:
                templateServerLine.setEnabledFlag(sendSettingVO.getEmailEnabledFlag());
                break;
            case SMS:
                templateServerLine.setEnabledFlag(sendSettingVO.getSmsEnabledFlag());
                break;
            case WEB:
                templateServerLine.setEnabledFlag(sendSettingVO.getPmEnabledFlag());
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

    private void setEnabledFlag(MessageTemplateRelDTO messageTemplateRelDTO, SendSettingVO sendSettingVO) {
        switch (SendingTypeEnum.valueOf(messageTemplateRelDTO.getSendType())) {
            case WH:
                messageTemplateRelDTO.setEnabledFlag(sendSettingVO.getWebhookEnabledFlag());
                break;
            case WHJSON:
                messageTemplateRelDTO.setEnabledFlag(sendSettingVO.getWebhookJsonEnabledFlag());
                break;
            default:
        }
    }

    /**
     * 注意TemplateServerC7NDTO中CategoryCode与SubCategoryCode字段
     * CategoryCode         表示level关系
     * SubCategoryCode      表示分类关系
     *
     * @param level
     * @param allowConfig
     * @return
     */
    @Override
    public List<SendSettingDetailTreeVO> queryByLevelAndAllowConfig(String level, int allowConfig) {
        if (level == null) {
            throw new CommonException("error.level.null");
        }
        // 验证资源层级类型，project、organization、site
        CommonValidator.validatorLevel(level);

        // 查询 处于启用状态 允许配置 的对应层级的消息发送设置
        List<TemplateServerC7NDTO> templateServerList = hzeroTemplateServerMapper.queryByCategoryCodeAndReceiveConfigFlag(level, allowConfig);

        // 返回给客户端的消息发送设置列表
        List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS = new ArrayList<>();

        // key: 资源层级     value: categoryCode集合
        Map<String, Set<String>> levelAndCategoryCodeMap = new HashMap<>();
        levelAndCategoryCodeMap.put(ResourceLevel.valueOf(level.toUpperCase()).value(), new HashSet<>());

        // for循环里过滤掉不是level层级的categoryCode
        for (TemplateServerC7NDTO templateServerC7NDTO : templateServerList) {
            Set<String> categoryCodes = levelAndCategoryCodeMap.get(templateServerC7NDTO.getCategoryCode());
            if (categoryCodes != null) {
                categoryCodes.add(templateServerC7NDTO.getSubcategoryCode());
            }
        }
        getSecondSendSettingDetailTreeVOS(levelAndCategoryCodeMap, sendSettingDetailTreeDTOS, templateServerList);

        return sendSettingDetailTreeDTOS;
    }

    private void getSecondSendSettingDetailTreeVOS(Map<String, Set<String>> levelAndCategoryCodeMap,
                                                   List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS,
                                                   List<TemplateServerC7NDTO> templateServerC7NDTOList) {
        int i = 1;
        // 将不同层级的categoryCode取出
        for (String level : levelAndCategoryCodeMap.keySet()) {
            Map<String, String> categoryMeanings = getMeanings();
            for (String subCategoryCode : levelAndCategoryCodeMap.get(level)) {

                // 表示第一层的SendSettingDetailTreeVO，parentId就是0
                SendSettingDetailTreeVO sendSettingDetailTreeDTO = new SendSettingDetailTreeVO();
                sendSettingDetailTreeDTO.setParentId(0L);
                sendSettingDetailTreeDTO.setName(categoryMeanings.get(subCategoryCode));
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTO.setCode(subCategoryCode);

                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdSendSettingDetailTreeVOS(templateServerC7NDTOList, level, subCategoryCode, secondParentId, sendSettingDetailTreeDTOS, i);
            }
        }
    }

    private int getThirdSendSettingDetailTreeVOS(List<TemplateServerC7NDTO> templateServerC7NDTOList,
                                                 String level,
                                                 String categoryCode,
                                                 Integer secondParentId,
                                                 List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS, Integer i) {
        for (TemplateServerC7NDTO templateServerC7NDTO : templateServerC7NDTOList) {
            // 取出指定层级、指定类别的消息发送设置，比如project层级的pro-management类别的所有消息发送设置
            // 与hzero融合后，层级字段是 CategoryCode，分类字段是 SubCategoryCode
            if (templateServerC7NDTO.getCategoryCode().equals(level) && templateServerC7NDTO.getSubcategoryCode().equals(categoryCode)) {
                SendSettingDetailTreeVO sendSettingDetailTreeDTO = new SendSettingDetailTreeVO();
                TemplateServerC7NDTOConvertToSendSettingDetailTreeVO(templateServerC7NDTO, sendSettingDetailTreeDTO, level);
                sendSettingDetailTreeDTO.setParentId((long) secondParentId);
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                i = i + 1;
            }
        }
        return i;
    }

    public Map<String, String> getMeanings() {
        ResponseEntity<Map<String, String>> meanings = platformFeignClient.getMeanings(LOV_MESSAGE_CODE);
        if (!meanings.getStatusCode().is2xxSuccessful()) {
            throw new CommonException(LOV_ERROR_INFO);
        }
        if (meanings.getBody() == null) {
            throw new CommonException(LOV_ERROR_INFO);
        }
        return meanings.getBody();
    }

    private void TemplateServerC7NDTOConvertToSendSettingDetailTreeVO(TemplateServerC7NDTO templateServerC7NDTO, SendSettingDetailTreeVO sendSettingDetailTreeVO, String level) {
        sendSettingDetailTreeVO.setId(templateServerC7NDTO.getTempServerId());
        sendSettingDetailTreeVO.setLevel(level);
        sendSettingDetailTreeVO.setName(templateServerC7NDTO.getMessageName());
        sendSettingDetailTreeVO.setCategoryCode(templateServerC7NDTO.getSubcategoryCode());
        sendSettingDetailTreeVO.setCode(templateServerC7NDTO.getMessageCode());
        sendSettingDetailTreeVO.setEmailEnabledFlag(templateServerC7NDTO.getEmailEnabledFlag());
        sendSettingDetailTreeVO.setPmEnabledFlag(templateServerC7NDTO.getPmEnabledFlag());
    }

}