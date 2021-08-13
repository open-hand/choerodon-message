package io.choerodon.message.app.service.impl;


import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.*;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.dto.NotifyMessageSettingConfigDTO;
import io.choerodon.message.infra.dto.iam.ProjectCategoryDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.*;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.HzeroTemplateServerMapper;
import io.choerodon.message.infra.mapper.NotifyMessageSettingConfigMapper;
import io.choerodon.message.infra.mapper.TemplateServerC7nMapper;
import io.choerodon.message.infra.utils.ConversionUtil;
import io.choerodon.message.infra.validator.CommonValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
@Service
public class SendSettingC7nServiceImpl implements SendSettingC7nService {

    public static final String LOV_MESSAGE_CODE = "HMSG.TEMP_SERVER.SUBCATEGORY";
    public static final String RESOURCE_DELETE_CONFIRMATION = "RESOURCEDELETECONFIRMATION";
    private static final String ADD_OR_IMPORT_USER = "ADD-OR-IMPORT-USER";
    private static final String ISSUE_STATUS_CHANGE_NOTICE = "ISSUE-STATUS-CHANGE-NOTICE";
    private static final String PRO_MANAGEMENT = "PRO-MANAGEMENT";
    private static final String WEBHOOK_OTHER = "webHookOther";
    private static final String WEBHOOK_JSON = "webHookJson";
    private static final String JSON = "JSON";
    private static final String MESSAGE_CHOERODON = "CHOERODON.";
    private static final String DINGTALK_WECHAT = "DINGTALKANDWECHAT";
    private static final String RT = "RT";

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private MessageClientProperties messageClientProperties;
    @Autowired
    private TemplateServerC7nMapper templateServerC7nMapper;
    @Autowired
    private TemplateServerRepository templateServerRepository;
    @Autowired
    private TemplateServerLineRepository templateServerLineRepository;
    @Autowired
    private HzeroTemplateServerMapper hzeroTemplateServerMapper;
    @Autowired
    private LovFeignClient lovFeignClient;
    @Autowired
    private IamClientOperator iamClientOperator;

    @Autowired
    private NotifyMessageSettingConfigMapper notifyMessageSettingConfigMapper;


    @Override
    public Page<MessageServiceVO> pagingAll(String messageCode, String messageName, Boolean enabled, Boolean receiveConfigFlag, String params, PageRequest pageRequest, String firstCode, String secondCode, String introduce) {
        secondCode = secondCode == null ? null : secondCode.toUpperCase();
        firstCode = firstCode == null ? null : firstCode.toUpperCase();
        String finalSecondCode = secondCode;
        String finalFirstCode = firstCode;
        Page<MessageServiceVO> serviceVOPage = PageHelper.doPageAndSort(pageRequest, () -> templateServerC7nMapper.selectTemplateServer(messageCode, messageName, finalSecondCode, finalFirstCode, enabled, receiveConfigFlag, params, introduce));
        Map<String, String> meaningsMap = getMeanings();
        serviceVOPage.getContent().forEach(t -> t.setMessageTypeValue(meaningsMap.get(t.getMessageType())));
        return serviceVOPage;
    }

    @Override
    public List<MsgServiceTreeVO> getMsgServiceTree() {
        List<TemplateServer> templateServers = templateServerC7nMapper.selectAllTemplateServer();
        List<MsgServiceTreeVO> msgServiceTreeVOS = new ArrayList<>();
        MsgServiceTreeVO msgServiceTreeVO1 = new MsgServiceTreeVO();
        msgServiceTreeVO1.setParentId(TenantDTO.DEFAULT_TENANT_ID);
        msgServiceTreeVO1.setId(1L);
        msgServiceTreeVO1.setName(LevelType.SITE.value());
        msgServiceTreeVO1.setCode(ResourceLevel.SITE.value().toUpperCase());
        msgServiceTreeVOS.add(msgServiceTreeVO1);

        MsgServiceTreeVO msgServiceTreeVO2 = new MsgServiceTreeVO();
        msgServiceTreeVO2.setParentId(TenantDTO.DEFAULT_TENANT_ID);
        msgServiceTreeVO2.setId(2L);
        msgServiceTreeVO2.setName(LevelType.ORGANIZATION.value());
        msgServiceTreeVO2.setCode(ResourceLevel.ORGANIZATION.value().toUpperCase());
        msgServiceTreeVOS.add(msgServiceTreeVO2);

        MsgServiceTreeVO msgServiceTreeVO3 = new MsgServiceTreeVO();
        msgServiceTreeVO3.setParentId(TenantDTO.DEFAULT_TENANT_ID);
        msgServiceTreeVO3.setId(3L);
        msgServiceTreeVO3.setName(LevelType.PROJECT.value());
        msgServiceTreeVO3.setCode(ResourceLevel.PROJECT.value().toUpperCase());
        msgServiceTreeVOS.add(msgServiceTreeVO3);

        Map<String, Set<String>> categoryMap = new HashMap<>();
        categoryMap.put(ResourceLevel.SITE.value().toUpperCase(), new HashSet<>());
        categoryMap.put(ResourceLevel.ORGANIZATION.value().toUpperCase(), new HashSet<>());
        categoryMap.put(ResourceLevel.PROJECT.value().toUpperCase(), new HashSet<>());
        for (TemplateServer templateServer : templateServers) {
            Set<String> categoryCodes = categoryMap.get(templateServer.getCategoryCode());
            if (categoryCodes != null) {
                categoryCodes.add(templateServer.getSubcategoryCode());
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
        Long tenantId = templateServer.getTenantId() == null ? TenantDTO.DEFAULT_TENANT_ID : templateServer.getTenantId();
        templateServerService.updateTemplateServer(tenantId, tempServerId, templateServer);
    }

    @Override
    public SendSettingVO queryByTempServerCode(String tempServerCode) {
        TemplateServer templateServer = templateServerService.getTemplateServer(TenantDTO.DEFAULT_TENANT_ID, tempServerCode);
        NotifyMessageSettingConfigDTO configDTO = new NotifyMessageSettingConfigDTO();
        configDTO.setMessageCode(tempServerCode);
        configDTO.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        SendSettingVO sendSettingVO = new SendSettingVO();
        BeanUtils.copyProperties(templateServer, sendSettingVO);
        NotifyMessageSettingConfigDTO messageSettingConfigDTO = notifyMessageSettingConfigMapper.selectOne(configDTO);
        if (messageSettingConfigDTO != null) {
            sendSettingVO.setEdit(messageSettingConfigDTO.getEdit());
        }
        List<TemplateServerLine> lineList = templateServerService.listTemplateServerLine(templateServer.getTempServerId(), BaseConstants.DEFAULT_TENANT_ID);
        if (!CollectionUtils.isEmpty(lineList)) {
            List<MessageTemplateVO> messageTemplates = new ArrayList<>();
            lineList.forEach(t -> {
                        MessageTemplateVO template = new MessageTemplateVO();
                        BeanUtils.copyProperties(messageTemplateService.getMessageTemplate(BaseConstants.DEFAULT_TENANT_ID, t.getTemplateCode(), messageClientProperties.getDefaultLang()), template);
                        messageTemplates.add(template);
                        template.setSendingType(t.getTypeCode());
                        setSendTypeEnable(t, sendSettingVO);
                    }
            );
            sendSettingVO.setMessageTemplateVOS(messageTemplates);
        }
        return sendSettingVO;
    }

    @Override
    public SendSettingVO queryByCode(String messageCode) {
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setMessageCode(messageCode));
        if (ObjectUtils.isEmpty(templateServer)) {
            throw new CommonException("error.query.tempServer");
        }
        return queryByTempServerCode(messageCode);
    }

    @Override
    public void enableOrDisabled(String messageCode, Boolean status) {
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setMessageCode(messageCode));
        if (ObjectUtils.isEmpty(templateServer)) {
            throw new CommonException("error.query.tempServer");
        }
        templateServer.setEnabledFlag(ConversionUtil.booleanConverToInteger(status));
        templateServerRepository.updateOptional(templateServer, TemplateServer.FIELD_ENABLED_FLAG);
    }


    @Override
    public SendSettingVO updateSendSetting(Long id, SendSettingVO sendSettingVO) {
        TemplateServer templateServer = templateServerRepository.selectByPrimaryKey(id);
        templateServer.setReceiveConfigFlag(sendSettingVO.getReceiveConfigFlag());
        templateServerRepository.updateByPrimaryKey(templateServer);

        List<TemplateServerLine> lineList = templateServerService.listTemplateServerLine(id, BaseConstants.DEFAULT_TENANT_ID);
        if (!CollectionUtils.isEmpty(lineList)) {
            lineList.forEach(t -> {
                t.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
                setEnabledFlag(t, sendSettingVO);
                t.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
                if (t.getTypeCode().equals(SendingTypeEnum.EMAIL.getValue())) {
                    t.setTryTimes(sendSettingVO.getRetryCount());
                }
            });
            templateServerLineRepository.batchUpdateByPrimaryKey(lineList);
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
                if (level.equals(ResourceLevel.SITE.value().toUpperCase())) {
                    msgServiceTreeVO.setParentId(1L);
                } else if (level.equals(ResourceLevel.ORGANIZATION.value().toUpperCase())) {
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
            if (level.equals(templateServer.getCategoryCode()) && categoryCode.equals(templateServer.getSubcategoryCode())) {
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
                sendSettingVO.setRetryCount(templateServerLine.getTryTimes());
                break;
            case SMS:
                sendSettingVO.setSmsEnabledFlag(templateServerLine.getEnabledFlag());
                break;
            case WEB:
                sendSettingVO.setPmEnabledFlag(templateServerLine.getEnabledFlag());
            case WEB_HOOK:
                if (templateServerLine.getTemplateCode().contains(WebHookTypeEnum.JSON.getValue().toUpperCase())) {
                    sendSettingVO.setWebhookJsonEnabledFlag(templateServerLine.getEnabledFlag());
                }
                if (templateServerLine.getTemplateCode().contains(WebHookTypeEnum.WECHAT.getValue())) {
                    sendSettingVO.setWebhookEnabledFlag(templateServerLine.getEnabledFlag());
                }
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
            case WEB_HOOK:
                if (templateServerLine.getTemplateCode().contains(WebHookTypeEnum.JSON.getValue().toUpperCase())) {
                    templateServerLine.setEnabledFlag(sendSettingVO.getWebhookJsonEnabledFlag());
                }
                if (templateServerLine.getTemplateCode().contains(WebHookTypeEnum.WECHAT.getValue())) {
                    templateServerLine.setEnabledFlag(sendSettingVO.getWebhookEnabledFlag());
                }
                break;
            default:
        }
    }

    /**
     * 注意SendSettingVO中CategoryCode与SubCategoryCode字段
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
        List<SendSettingVO> sendSettingVOList = hzeroTemplateServerMapper.queryByCategoryCodeAndReceiveConfigFlag(level, allowConfig);

        // 返回给客户端的消息发送设置列表
        List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS = new ArrayList<>();

        // key: 资源层级     value: categoryCode集合
        Map<String, Set<String>> levelAndCategoryCodeMap = new HashMap<>();
        levelAndCategoryCodeMap.put(level.toUpperCase(), new HashSet<>());

        // for循环里过滤掉不是level层级的categoryCode
        for (SendSettingVO sendSettingVO : sendSettingVOList) {
            Set<String> categoryCodes = levelAndCategoryCodeMap.get(sendSettingVO.getCategoryCode());
            if (categoryCodes != null) {
                categoryCodes.add(sendSettingVO.getSubcategoryCode());
            }
        }
        getSecondSendSettingDetailTreeVOS(levelAndCategoryCodeMap, sendSettingDetailTreeDTOS, sendSettingVOList);

        return sendSettingDetailTreeDTOS;
    }

    @Override
    public WebHookVO.SendSetting getTempServerForWebhook(Long sourceId, String sourceLevel, String name, String description, String type) {
        WebHookVO.SendSetting sendSetting = new WebHookVO.SendSetting();
        Map<String, String> lovMap = getMeanings();
        Map<String, String> result = new HashMap<>();
        List<String> subCategories = new ArrayList<>();
        Boolean contains = true;
        if (ResourceLevel.PROJECT.value().toUpperCase().equals(sourceLevel.toUpperCase())) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            //项目类型
            List<ProjectCategoryDTO> categories = projectDTO.getCategories();
            List<String> categoryCodes = categories.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toList());
            subCategories.add(SubcategoryEnum.DEFAULT.getValue());
            subCategories.add(SubcategoryEnum.ACCOUNT_SECURITY_NOTICE.getValue());
            subCategories.add(SubcategoryEnum.REGISTER_ORG_NOTICE.getValue());
            subCategories.add(SubcategoryEnum.ORG_MANAGEMENT.getValue());
            subCategories.add(SubcategoryEnum.PRO_MANAGEMENT.getValue());
            subCategories.add(SubcategoryEnum.ADD_OR_IMPORT_USER.getValue());

            if (categoryCodes.contains(ProjectCategoryEnum.N_AGILE.value())) {
                subCategories.add(ADD_OR_IMPORT_USER);
                subCategories.add(ISSUE_STATUS_CHANGE_NOTICE);
                subCategories.add(SubcategoryEnum.BACKLOG_NOTICE.getValue());
            }
            if (categoryCodes.contains(ProjectCategoryEnum.N_DEVOPS.value())) {
                subCategories.add(SubcategoryEnum.ENV_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.CLUSTER_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.APP_DEPLOYMENT_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.CODE_MANAGEMENT_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.APP_SERVICE_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.DEPLOYMENT_RESOURCES_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.STREAM_CHANGE_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.RESOURCE_SECURITY_NOTICE.getValue());
                subCategories.add(SubcategoryEnum.MARKET_APP.getValue());
            }
            if (categoryCodes.contains(ProjectCategoryEnum.N_TEST.value())){
                subCategories.add(SubcategoryEnum.API_TEST_EXECUTE_NOTICE.getValue());
            }

        }
        if (WEBHOOK_OTHER.equals(type)) {
            type = DINGTALK_WECHAT;
        }
        if (WEBHOOK_JSON.equals(type)) {
            type = JSON;
        }
        List<TemplateServer> sendSettingDTOS = templateServerC7nMapper.selectForWebHook(sourceLevel.toUpperCase(), type.toUpperCase(), subCategories, contains, name, description);
        sendSettingDTOS.forEach(t -> {
            if (lovMap.containsKey(t.getSubcategoryCode())) {
                result.put(t.getSubcategoryCode(), lovMap.get(t.getSubcategoryCode()));
            }
        });
        sendSetting.setCategories(result);
        sendSetting.setSendSettings(sendSettingDTOS);
        return sendSetting;
    }

    @Override
    @Transactional
    public MessageTemplateVO createMessageTemplate(MessageTemplateVO messageTemplateVO) {
        MessageTemplate messageTemplate = new MessageTemplate();
        // 1.准备消息模板数据
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setMessageCode(messageTemplateVO.getMessageCode()));
        BeanUtils.copyProperties(messageTemplateVO, messageTemplate);
        messageTemplate.setTemplateName(templateServer.getMessageName());
        messageTemplate.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        if (messageTemplateVO.getSendingType().equals(HmsgConstant.MessageType.WEB_HOOK) && !StringUtils.isEmpty(messageTemplateVO.getWebhookType())) {
            messageTemplate.setTemplateCode(MESSAGE_CHOERODON + messageTemplateVO.getMessageCode().toUpperCase() + "_" + messageTemplateVO.getWebhookType().toUpperCase());
        } else {
            messageTemplate.setTemplateCode(MESSAGE_CHOERODON + messageTemplateVO.getMessageCode().toUpperCase() + "_" + messageTemplateVO.getSendingType());
        }
        messageTemplate.setLang(messageClientProperties.getDefaultLang());
        messageTemplate.setEnabledFlag(1);
        messageTemplate.setEditorType(RT);
        if (StringUtils.isEmpty(messageTemplate.getTemplateTitle())) {
            messageTemplate.setTemplateTitle(messageTemplateVO.getMessageCode().toUpperCase());
        }
        messageTemplate = messageTemplateService.createMessageTemplate(TenantDTO.DEFAULT_TENANT_ID, messageTemplate);

        // 2. template_server_line表
        TemplateServerLine templateServerLine = new TemplateServerLine();
        templateServerLine.setTempServerId(templateServer.getTempServerId());
        switch (SendingTypeEnum.forValue(messageTemplateVO.getSendingType().toUpperCase())) {
            case EMAIL:
                templateServerLine.setServerCode(ConfigNameEnum.EMAIL_NAME.value());
                break;
            case SMS:
                templateServerLine.setServerCode(ConfigNameEnum.SMS_NAME.value());
                break;
        }
        templateServerLine.setTypeCode(messageTemplateVO.getSendingType().toUpperCase());
        templateServerLine.setTemplateCode(messageTemplate.getTemplateCode());
        templateServerLine.setEnabledFlag(1);
        templateServerLine.setTenantId(0L);
        templateServerLineRepository.insert(templateServerLine);
        BeanUtils.copyProperties(messageTemplate, messageTemplateVO);
        return messageTemplateVO;
    }

    private void getSecondSendSettingDetailTreeVOS(Map<String, Set<String>> levelAndCategoryCodeMap,
                                                   List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS,
                                                   List<SendSettingVO> sendSettingVOList) {
        int i = 1;
        // 将不同层级的categoryCode取出
        for (String level : levelAndCategoryCodeMap.keySet()) {
            Map<String, String> categoryMeanings = getMeanings();
            for (String subCategoryCode : levelAndCategoryCodeMap.get(level)) {

                // 表示第一层的SendSettingDetailTreeVO，parentId就是0
                SendSettingDetailTreeVO sendSettingDetailTreeDTO = new SendSettingDetailTreeVO();
                sendSettingDetailTreeDTO.setName(categoryMeanings.get(subCategoryCode));
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTO.setCode(subCategoryCode);

                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdSendSettingDetailTreeVOS(sendSettingVOList, level, subCategoryCode, secondParentId, sendSettingDetailTreeDTOS, i);
            }
        }
    }

    private int getThirdSendSettingDetailTreeVOS(List<SendSettingVO> sendSettingVOList,
                                                 String level,
                                                 String categoryCode,
                                                 Integer secondParentId,
                                                 List<SendSettingDetailTreeVO> sendSettingDetailTreeDTOS, Integer i) {
        for (SendSettingVO sendSettingVO : sendSettingVOList) {
            // 取出指定层级、指定类别的消息发送设置，比如project层级的pro-management类别的所有消息发送设置
            // 与hzero融合后，层级字段是 CategoryCode，分类字段是 SubCategoryCode
            if (sendSettingVO.getCategoryCode().equals(level) && sendSettingVO.getSubcategoryCode().equals(categoryCode)) {
                SendSettingDetailTreeVO sendSettingDetailTreeDTO = new SendSettingDetailTreeVO();
                SendSettingVOConvertToSendSettingDetailTreeVO(sendSettingVO, sendSettingDetailTreeDTO, level);
                sendSettingDetailTreeDTO.setParentId((long) secondParentId);
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                i = i + 1;
            }
        }
        return i;
    }

    private Map<String, String> getMeanings() {
        List<LovValueDTO> valueDTOList = lovFeignClient.queryLovValue(LOV_MESSAGE_CODE, TenantDTO.DEFAULT_TENANT_ID);
        return valueDTOList.stream().collect(Collectors.toMap(LovValueDTO::getValue, LovValueDTO::getMeaning));
    }

    private void SendSettingVOConvertToSendSettingDetailTreeVO(SendSettingVO sendSettingVO, SendSettingDetailTreeVO sendSettingDetailTreeVO, String level) {
        sendSettingDetailTreeVO.setId(sendSettingVO.getTempServerId())
                .setLevel(level)
                .setName(sendSettingVO.getMessageName())
                .setCategoryCode(sendSettingVO.getSubcategoryCode())
                .setCode(sendSettingVO.getMessageCode())
                .setEmailTemplateId(sendSettingVO.getEmailTemplateId())
                .setSmsTemplateId(sendSettingVO.getSmsTemplateId())
                .setPmTemplateId(sendSettingVO.getPmTemplateId())
                .setAllowConfig(Optional.ofNullable(sendSettingVO.getReceiveConfigFlag()).map(t -> t.equals(1)).orElse(false))
                .setEmailEnabledFlag(Optional.ofNullable(sendSettingVO.getEmailEnabledFlag()).map(t -> t.equals(1)).orElse(false))
                .setPmEnabledFlag(Optional.ofNullable(sendSettingVO.getPmEnabledFlag()).map(t -> t.equals(1)).orElse(false))
                .setSmsEnabledFlag(Optional.ofNullable(sendSettingVO.getSmsEnabledFlag()).map(t -> t.equals(1)).orElse(false));
    }

}
