package io.choerodon.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.utils.PageUtils;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.message.app.service.WebHookC7nService;
import io.choerodon.message.infra.constant.MisConstants;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.enums.WebHookTypeEnum;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.TemplateServerLineC7nMapper;
import io.choerodon.message.infra.mapper.WebHookC7nMapper;
import io.choerodon.message.infra.mapper.WebhookProjectRelMapper;
import io.choerodon.message.infra.mapper.WebhookServerC7nMapper;
import io.choerodon.message.infra.utils.CommonExAssertUtil;
import io.choerodon.message.infra.utils.ConversionUtil;
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
import org.hzero.message.infra.mapper.WebhookServerMapper;
import org.hzero.mybatis.helper.DataSecurityHelper;
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
    @Autowired
    private WebhookServerC7nMapper webhookServerC7nMapper;


    @Override
    public Page<WebHookVO> pagingWebHook(PageRequest pageRequest, Long sourceId, String sourceLevel, String messageName, String type, Boolean enableFlag, String params, String messageCode) {
        List<WebHookVO> list;
        if (ResourceLevel.PROJECT.value().toUpperCase().equals(sourceLevel.toUpperCase())) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            list = webHookC7nMapper.pagingWebHook(projectDTO.getOrganizationId(), sourceId, messageName, type, enableFlag, params, messageCode);
        } else {
            list = webHookC7nMapper.pagingWebHook(sourceId, null, messageName, type, enableFlag, params, messageCode);
        }
        return PageUtils.createPageFromList(list, pageRequest);
    }

    @Override
    public Boolean checkPath(Long sourceId, String address, String source) {
        if (StringUtils.isEmpty(address)) {
            throw new CommonException("error.web.hook.check.path.can.not.be.empty");
        }
        //webhook项目下唯一
        if (StringUtils.equals(ResourceLevel.PROJECT.value(), source)) {
            if (webhookServerC7nMapper.existWebHookUnderProject(address, sourceId) != null) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
        //webhook 组织下唯一
        else if (StringUtils.equals(ResourceLevel.ORGANIZATION.value(), source)) {
            if (webhookServerC7nMapper.existWebHookUnderOrganization(address, sourceId) != null) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            throw new CommonException("error.web.hook.check.path.level");
        }
    }

    @Override
    @Transactional
    public WebHookVO create(Long sourceId, WebHookVO webHookVO, String sourceLevel) {
        //校验type
        if (!WebHookTypeEnum.isInclude(webHookVO.getServerType())) {
            throw new CommonException("error.web.hook.type.invalid");
        }
        //0.校验web hook path
        if (!checkPath(sourceId, webHookVO.getWebhookAddress(), sourceLevel)) {
            throw new CommonException("error.web.hook.path.duplicate");
        }

        WebhookServer webhookServer = new WebhookServer();
        BeanUtils.copyProperties(webHookVO, webhookServer);
        String codeStr;
        String nameStr;
        Long tenantId = sourceId;
        if (sourceLevel.toUpperCase().equals(ResourceLevel.PROJECT.value().toUpperCase())) {
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
        webhookServer.setEnabledFlag(1);
        webhookServer = webhookServerService.createWebHook(tenantId, webhookServer);

        Set<Long> sendSettingIdList = webHookVO.getSendSettingIdList();
        for (Long aLong : sendSettingIdList) {
            String type = webHookVO.getServerType().toUpperCase();
            TemplateServerLine serverLine = templateServerLineC7nMapper.queryByTempServerIdAndType(aLong, type);
            if (!ObjectUtils.isEmpty(serverLine)) {
                TemplateServerWh templateServerWh = new TemplateServerWh();
                templateServerWh.setTenantId(tenantId);
                templateServerWh.setServerCode(webhookServer.getServerCode());
                templateServerWh.setServerName(webhookServer.getServerName());
                templateServerWh.setServerType(webhookServer.getServerType());
                templateServerWh.setTempServerId(serverLine.getTempServerId());
                List<TemplateServerWh> templateServerWhList = new ArrayList<>();
                templateServerWhList.add(templateServerWh);
                templateServerWhService.batchCreateTemplateServerWh(serverLine.getTempServerLineId(), templateServerWhList);
            }
        }

        if (Objects.equals(ResourceLevel.PROJECT.value().toUpperCase(), sourceLevel.toUpperCase())) {
            WebhookProjectRelDTO webhookProjectRelDTO = new WebhookProjectRelDTO(webhookServer.getServerId(), sourceId);
            webhookProjectRelDTO.setTenantId(tenantId);
            webhookProjectRelDTO.setServerCode(webhookServer.getServerCode());
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
        if (!checkPath(webHookVO.getServerId(), webHookVO.getWebhookAddress(), sourceLevel)) {
            throw new CommonException("error.web.hook.path.duplicate");
        }

        WebhookServer webhookServer = new WebhookServer();
        BeanUtils.copyProperties(webHookVO, webhookServer);
        Long tenantId = sourceId;
        if (sourceLevel.equals(ResourceLevel.PROJECT.value().toUpperCase())) {
            ProjectDTO projectDTO = iamClientOperator.queryProjectById(sourceId);
            tenantId = projectDTO.getOrganizationId();
        }
        webhookServer = webhookServerService.updateWebHook(tenantId, webhookServer);

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
                String type = webHookVO.getServerType().toUpperCase();
                TemplateServerLine templateServerLine = templateServerLineC7nMapper.queryByTempServerIdAndType(aLong, type);
                templateServerWhService.batchCreateTemplateServerWh(templateServerLine.getTempServerLineId(), templateServerWhList);
            }
        }
        return webHookVO;
    }

    @Override
    @Transactional
    public void delete(Long sourceId, Long webHookId, String sourceLevel) {
        if (sourceLevel.equals(ResourceLevel.PROJECT.value())) {
            WebhookProjectRelDTO webhookProjectRelDTO = new WebhookProjectRelDTO();
            webhookProjectRelDTO.setProjectId(sourceId);
            webhookProjectRelDTO.setWebhookId(webHookId);
            webhookProjectRelMapper.delete(webhookProjectRelDTO);
        }
        TemplateServerWh templateServerWh = new TemplateServerWh();
        templateServerWh.setServerCode(webhookServerRepository.selectByPrimaryKey(webHookId).getServerCode());
        templateServerWhRepository.delete(templateServerWh);
        webhookServerRepository.deleteByPrimaryKey(webHookId);
    }

    @Override
    public void updateEnabledFlag(Long organizationId, Long webHookId, Boolean enableFlag) {
        WebhookServer webhookServer = webhookServerRepository.selectByPrimaryKey(webHookId);
        //签解密
        webhookServer.setSecret(DataSecurityHelper.decrypt(webhookServer.getSecret()));
        CommonExAssertUtil.assertTrue(organizationId.equals(webhookServer.getTenantId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        webhookServer.setEnabledFlag(ConversionUtil.booleanConverToInteger(enableFlag));
        webhookServerService.updateWebHook(webhookServer.getTenantId(), webhookServer);
    }

    @Override
    public void updateEnabledFlagInProject(Long projectId, Long webhookId, Boolean enabledFlag) {
        ProjectDTO projectDTO = iamClientOperator.queryProjectById(projectId);
        updateEnabledFlag(projectDTO.getOrganizationId(), webhookId, enabledFlag);
    }

    @Override
    public void resendMessage(Long projectId, Long recordId) {
        ProjectDTO projectDTO = iamClientOperator.queryProjectById(projectId);
        messageService.resendMessage(projectDTO.getOrganizationId(), recordId);
    }

    @Override
    public WebHookVO queryById(Long webHookId) {
        //查询可选发送设置主键集合
        WebHookVO webHookVO = Optional.ofNullable(webHookC7nMapper.queryById(webHookId)).orElse(new WebHookVO());
        //查询已选发送设置主键集合
        Set<Long> templateServerIds = webHookC7nMapper.queryWebHook(webHookId);
        WebhookServer webhookServer = webhookServerRepository.selectByPrimaryKey(webHookId);
        webhookServer.setSecret(DataSecurityHelper.decrypt(webhookServer.getSecret()));
        WebHookVO result = new WebHookVO();
        BeanUtils.copyProperties(webhookServer, result);
        result.setTemplateServers(webHookVO.getTemplateServers());
        result.setSendSettingIdList(templateServerIds);
        return result;
    }
}
