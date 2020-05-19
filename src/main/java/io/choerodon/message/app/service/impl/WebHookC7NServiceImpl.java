package io.choerodon.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.message.app.service.WebHookC7nService;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.WebHookTypeEnum;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.TemplateServerLineC7nMapper;
import io.choerodon.message.infra.mapper.WebHookC7nMapper;
import io.choerodon.message.infra.mapper.WebhookProjectRelMapper;
import io.choerodon.message.infra.utils.ConversionUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.StringUtils;
import org.hzero.message.app.service.MessageService;
import org.hzero.message.app.service.TemplateServerWhService;
import org.hzero.message.app.service.WebhookServerService;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.entity.TemplateServerWh;
import org.hzero.message.domain.entity.WebhookServer;
import org.hzero.message.domain.repository.TemplateServerWhRepository;
import org.hzero.message.domain.repository.WebhookServerRepository;
import org.hzero.message.infra.mapper.MessageTemplateMapper;
import org.hzero.message.infra.mapper.TemplateServerLineMapper;
import org.hzero.message.infra.mapper.TemplateServerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private TemplateServerLineMapper templateServerLineMapper;
    @Autowired
    private TemplateServerMapper templateServerMapper;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private TemplateServerLineC7nMapper templateServerLineC7nMapper;
    @Autowired
    private TemplateServerWhService templateServerWhService;
    @Autowired
    private TemplateServerWhRepository templateServerWhRepository;


    @Override
    public Page<WebHookVO> pagingWebHook(PageRequest pageRequest, Long sourceId, String sourceLevel, String messageName, String type, Boolean enableFlag, String params) {
        if (ResourceLevel.PROJECT.value().equals(sourceLevel)) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
//            return PageHelper.doPageAndSort(pageRequest, () -> webHookC7nMapper.pagingWebHook(projectDTO.getOrganizationId(), sourceId, messageName, type, enableFlag, params));
            return PageHelper.doPage(pageRequest, () -> webHookC7nMapper.pagingWebHook(projectDTO.getOrganizationId(), sourceId, messageName, type, enableFlag, params));
        } else {
//            return PageHelper.doPageAndSort(pageRequest, () -> webHookC7nMapper.pagingWebHook(sourceId, null, messageName, type, enableFlag, params));
            return PageHelper.doPage(pageRequest, () -> webHookC7nMapper.pagingWebHook(sourceId, null, messageName, type, enableFlag, params));
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
    @Transactional
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
        String serverCode = codeStr.length() > CODE_MAX_LENGTH ? codeStr.substring(0, CODE_MAX_LENGTH) : codeStr;
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, CODE_MAX_LENGTH);
        webhookServer.setServerCode(String.format("%s-%s", serverCode, uuid));
        String serverName = nameStr.length() > NAME_MAX_LENGTH ? nameStr.substring(NAME_MAX_LENGTH) : nameStr;
        webhookServer.setServerName(String.format("%s-%s", serverName, uuid));
        webhookServer = webhookServerService.createWebHook(tenantId, webhookServer);

        Set<Long> sendSettingIdList = webHookVO.getSendSettingIdList();
        for (Long aLong : sendSettingIdList) {
            TemplateServerLine serverLine = templateServerLineC7nMapper.queryByTempServerIdAndType(aLong, webHookVO.getServerType().toUpperCase());
            if (!ObjectUtils.isEmpty(serverLine)) {
                TemplateServerWh templateServerWh = new TemplateServerWh();
                templateServerWh.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
                templateServerWh.setServerCode(webhookServer.getServerCode());
                templateServerWh.setServerName(webhookServer.getServerName());
                templateServerWh.setServerType(webhookServer.getServerType());
                templateServerWh.setTempServerId(serverLine.getTempServerId());
                List<TemplateServerWh> templateServerWhList = new ArrayList<>();
                templateServerWhList.add(templateServerWh);
                templateServerWhService.batchCreateTemplateServerWh(serverLine.getTempServerLineId(), templateServerWhList);
            }
        }

        if (Objects.equals(ResourceLevel.PROJECT.value(), sourceLevel)) {
            WebhookProjectRelDTO webhookProjectRelDTO = new WebhookProjectRelDTO(webhookServer.getServerId(), sourceId);
            webhookProjectRelMapper.insert(webhookProjectRelDTO);
        }
        return webHookVO;
    }

    @Override
    public WebHookVO update(Long sourceId, WebHookVO webHookVO, String sourceLevel) {
        WebhookServer oldWebHook = webhookServerRepository.selectOne(new WebhookServer().setServerCode(webHookVO.getServerCode()));
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
//        webhookServer = webhookServerService.updateWebHook(tenantId, webhookServer);

        TemplateServerWh queryDTO = new TemplateServerWh();
        queryDTO.setServerCode(webhookServer.getServerCode());
        List<Long> oldSendIds = templateServerWhRepository.select(queryDTO).stream().map(TemplateServerWh::getTempServerId).collect(Collectors.toList());
        Set<Long> newSendIds = webHookVO.getSendSettingIdList();

        if (oldWebHook.getServerType().equals(webHookVO.getServerType())) {
            List<Long> updateSendIds = new ArrayList<>();
            for (Long sendId : oldSendIds) {
                if (newSendIds.contains(sendId)) {
                    newSendIds.remove(sendId);
                    updateSendIds.add(sendId);
                }
            }
            oldSendIds.removeAll(updateSendIds);
        }

        if (!CollectionUtils.isEmpty(oldSendIds)) {
            for (Long aLong : oldSendIds) {
                TemplateServerWh serverWh = new TemplateServerWh();
                serverWh.setServerCode(webhookServer.getServerCode());
                serverWh.setTempServerId(aLong);
                serverWh.setServerType(webhookServer.getServerType());
                templateServerWhRepository.delete(serverWh);
            }
        }

        if (!CollectionUtils.isEmpty(newSendIds)) {
            for (Long aLong : newSendIds) {
                TemplateServerWh templateServerWh = new TemplateServerWh();
                templateServerWh.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
                templateServerWh.setServerCode(webhookServer.getServerCode());
                templateServerWh.setServerName(webhookServer.getServerName());
                templateServerWh.setServerType(webhookServer.getServerType());
                templateServerWh.setTempServerId(aLong);
                List<TemplateServerWh> templateServerWhList = new ArrayList<>();
                templateServerWhList.add(templateServerWh);
                TemplateServerLine templateServerLine = templateServerLineMapper.selectOne(new TemplateServerLine().setTempServerId(aLong));
                templateServerWhService.batchCreateTemplateServerWh(templateServerLine.getTempServerLineId(), templateServerWhList);
            }
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
