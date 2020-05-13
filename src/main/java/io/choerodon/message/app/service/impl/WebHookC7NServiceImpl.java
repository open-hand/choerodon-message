package io.choerodon.message.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.hzero.message.app.service.MessageService;
import org.hzero.message.app.service.WebhookServerService;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.entity.WebhookServer;
import org.hzero.message.domain.repository.WebhookServerRepository;
import org.hzero.message.infra.mapper.MessageTemplateMapper;
import org.hzero.message.infra.mapper.TemplateServerLineMapper;
import org.hzero.message.infra.mapper.TemplateServerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.OrganizationProjectVO;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.message.app.service.WebHookC7nService;
import io.choerodon.message.infra.dto.MessageTemplateRelDTO;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.enums.WebHookTypeEnum;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.MessageTemplateRelMapper;
import io.choerodon.message.infra.mapper.WebHookC7nMapper;
import io.choerodon.message.infra.mapper.WebhookProjectRelMapper;
import io.choerodon.message.infra.utils.ConversionUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
@Service
public class WebHookC7NServiceImpl implements WebHookC7nService {
    public static Integer CODE_MAX_LENGTH = 14;
    public static Integer NAME_MAX_LENGTH = 44;
    @Autowired
    private WebHookC7nMapper webHookC7nMapper;
    @Autowired
    private WebhookServerRepository webhookServerRepository;
    @Autowired
    private IamClientOperator iamClientOperator;
    @Autowired
    private WebhookServerService webhookServerService;
    @Autowired
    private MessageTemplateRelMapper messageTemplateRelMapper;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private TemplateServerLineMapper templateServerLineMapper;
    @Autowired
    private TemplateServerMapper templateServerMapper;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;
    @Autowired
    private MessageService messageService;


    @Override
    public Page<WebHookVO> pagingWebHook(PageRequest pageRequest, Long sourceId, String sourceLevel, String messageName, String type, Boolean enableFlag, String params) {
        if (ResourceLevel.PROJECT.value().equals(sourceLevel)) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            return PageHelper.doPageAndSort(pageRequest, () -> webHookC7nMapper.pagingWebHook(projectDTO.getOrganizationId(), sourceId, messageName, type, enableFlag, params));
        } else {
            return PageHelper.doPageAndSort(pageRequest, () -> webHookC7nMapper.pagingWebHook(sourceId, null, messageName, type, enableFlag, params));
        }
    }

    @Override
    public Boolean checkPath(Long id, String address) {
        if (StringUtils.isEmpty(address)) {
            throw new CommonException("error.web.hook.check.path.can.not.be.empty");
        }
        WebhookServer existDTO = webhookServerRepository.selectOne(new WebhookServer().setWebhookAddress(address));
        return ObjectUtils.isEmpty(existDTO)
                || (!ObjectUtils.isEmpty(existDTO) && existDTO.getServerId().equals(id));
    }

    @Override
    public WebHookVO create(Long sourceId, WebHookVO webHookVO, String sourceLevel) {
        //校验type
        if (!WebHookTypeEnum.isInclude(webHookVO.getServerType())) {
            throw new CommonException("error.web.hook.type.invalid");
        }
        //0.校验web hook path
        if (!checkPath(null, webHookVO.getWebhookAddress())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }

        WebhookServer webhookServer = new WebhookServer();
        BeanUtils.copyProperties(webHookVO, webhookServer);
        String codeStr;
        String nameStr;
        Long tenantId = sourceId;
        if (sourceLevel.equals(ResourceLevel.PROJECT.value())) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            codeStr = projectDTO.getCode();
            nameStr = projectDTO.getName();
            tenantId = projectDTO.getOrganizationId();
        } else {
            TenantDTO tenantDTO = iamClientOperator.queryTenantById(sourceId);
            codeStr = tenantDTO.getTenantNum();
            nameStr = tenantDTO.getTenantName();
        }
        String serverCode = codeStr.length() > CODE_MAX_LENGTH ? codeStr.substring(CODE_MAX_LENGTH) : codeStr;
        String uuid = UUID.randomUUID().toString().substring(CODE_MAX_LENGTH);
        webhookServer.setServerCode(String.format("%s-%s", serverCode, uuid));
        String serverName = nameStr.length() > NAME_MAX_LENGTH ? nameStr.substring(NAME_MAX_LENGTH) : nameStr;
        webhookServer.setServerName(String.format("%s-%s", serverName, uuid));
        webhookServerService.createWebHook(tenantId, webhookServer);

        Set<Long> sendSettingIdList = webHookVO.getSendSettingIdList();
        sendSettingIdList.forEach(aLong -> {
            MessageTemplateRelDTO templateRelDTO = messageTemplateRelMapper.selectByTemplateId(aLong);
            MessageTemplate messageTemplate = messageTemplateMapper.selectByPrimaryKey(templateRelDTO.getTemplateId());
            TemplateServerLine serverLine = new TemplateServerLine();
            serverLine.setServerCode(serverCode);
            serverLine.setTempServerId(aLong);
            serverLine.setTemplateCode(messageTemplate.getTemplateCode());
            serverLine.setTypeCode(SendingTypeEnum.WH.getValue());
            templateServerLineMapper.insert(serverLine);
        });

        if (Objects.equals(sourceLevel, sourceLevel)) {
            WebhookProjectRelDTO webhookProjectRelDTO = new WebhookProjectRelDTO(webhookServer.getServerId(), sourceId);
            webhookProjectRelMapper.insert(webhookProjectRelDTO);
        }
        return webHookVO;
    }

    @Override
    public WebHookVO update(Long sourceId, WebHookVO webHookVO, String sourceLevel) {
        //校验type
        if (!WebHookTypeEnum.isInclude(webHookVO.getServerType())) {
            throw new CommonException("error.web.hook.type.invalid");
        }
        //0.校验web hook path
        if (!checkPath(null, webHookVO.getWebhookAddress())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }

        WebhookServer webhookServer = new WebhookServer();
        BeanUtils.copyProperties(webHookVO, webhookServer);
        Long tenantId = sourceId;
        if (sourceLevel.equals(ResourceLevel.PROJECT.value())) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            tenantId = projectDTO.getOrganizationId();
        }
        webhookServerService.updateWebHook(tenantId, webhookServer);

        List<Long> oldSendIds = templateServerLineMapper.select(new TemplateServerLine().setServerCode(webhookServer.getServerCode())).stream().map(TemplateServerLine::getTempServerId).collect(Collectors.toList());
        List<Long> updateSendIds = new ArrayList<>();
        Set<Long> newSendIds = webHookVO.getSendSettingIdList();
        for (Long sendId : oldSendIds) {
            if (newSendIds.contains(sendId)) {
                newSendIds.remove(sendId);
                updateSendIds.add(sendId);
            }
        }
        oldSendIds.removeAll(updateSendIds);
        if (!CollectionUtils.isEmpty(oldSendIds)) {
            oldSendIds.forEach(aLong -> {
                TemplateServer templateServer = templateServerMapper.selectByPrimaryKey(aLong);
                MessageTemplateRelDTO messageTemplateRelDTO = new MessageTemplateRelDTO().setMessageCode(templateServer.getMessageCode());
                messageTemplateRelDTO.setSendType(webHookVO.getServerType());
                MessageTemplateRelDTO templateRelDTO = messageTemplateRelMapper.selectOne(messageTemplateRelDTO);
                MessageTemplate messageTemplate = messageTemplateMapper.selectByPrimaryKey(templateRelDTO.getTemplateId());
                TemplateServerLine serverLine = new TemplateServerLine();
                serverLine.setServerCode(webhookServer.getServerCode());
                serverLine.setTempServerId(aLong);
                serverLine.setTemplateCode(messageTemplate.getTemplateCode());
                serverLine.setTypeCode(SendingTypeEnum.WH.getValue());
                templateServerLineMapper.delete(serverLine);
            });
        }

        if (!CollectionUtils.isEmpty(newSendIds)) {
            newSendIds.forEach(aLong -> {
                TemplateServer templateServer = templateServerMapper.selectByPrimaryKey(aLong);
                MessageTemplateRelDTO templateRelDTO = messageTemplateRelMapper.selectOne(new MessageTemplateRelDTO().setMessageCode(templateServer.getMessageCode()));
                MessageTemplate messageTemplate = messageTemplateMapper.selectByPrimaryKey(templateRelDTO.getTemplateId());
                TemplateServerLine serverLine = new TemplateServerLine();
                serverLine.setServerCode(webhookServer.getServerCode());
                serverLine.setTempServerId(aLong);
                serverLine.setTemplateCode(messageTemplate.getTemplateCode());
                serverLine.setTypeCode(SendingTypeEnum.WH.getValue());
                templateServerLineMapper.insert(serverLine);
            });
        }

        return webHookVO;
    }

    @Override
    public void delete(Long sourceId, Long webHookId, String sourceLevel) {
        webHookC7nMapper.deleteWebHook(webHookId);
    }

    @Override
    public void updateEnabledFlag(Long webHookId, Boolean enableFlag) {
        WebhookServer webhookServer = webhookServerRepository.selectByPrimaryKey(webHookId);
        webhookServer.setEnabledFlag(ConversionUtil.booleanConverToInteger(enableFlag));
        webhookServerService.updateWebHook(webhookServer.getTenantId(), webhookServer);
    }

    @Override
    public void resendMessage(Long projectId, Long recordId) {
        ProjectDTO projectDTO = iamClientOperator.queryProjectById(projectId);
        messageService.resendMessage(projectDTO.getOrganizationId(), recordId);
    }

    @Override
    public WebHookVO queryById(Long webHookId) {
        return webHookC7nMapper.queryById(webHookId);
    }
}
