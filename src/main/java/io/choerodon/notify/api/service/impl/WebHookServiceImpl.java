package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.service.WebHookMessageSettingService;
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
import io.choerodon.web.util.PageableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebHookServiceImpl implements WebHookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
    private WebHookMapper webHookMapper;
    private WebHookMessageSettingService webHookMessageSettingService;
    private TemplateService templateService;
    private SendSettingService sendSettingService;
    private TemplateRender templateRender;
    private MessegeSettingMapper messegeSettingMapper;

    public WebHookServiceImpl(WebHookMapper webHookMapper, WebHookMessageSettingService webHookMessageSettingService, TemplateService templateService, SendSettingService sendSettingService, TemplateRender templateRender, MessegeSettingMapper messegeSettingMapper) {
        this.webHookMapper = webHookMapper;
        this.webHookMessageSettingService = webHookMessageSettingService;
        this.templateService = templateService;
        this.sendSettingService = sendSettingService;
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
    public PageInfo<WebHookDTO> pagingWebHook(Pageable pageable, Long projectId, WebHookDTO filterDTO, String params) {
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> webHookMapper.doFTR(projectId, filterDTO, params));
    }

    @Override
    public Boolean checkPath(Long id, String path) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.web.hook.check.path.can.not.be.empty");
        }
        WebHookDTO existDTO = webHookMapper.selectOne(new WebHookDTO().setWebhookPath(path));
        return ObjectUtils.isEmpty(existDTO)
                || (!ObjectUtils.isEmpty(existDTO) && existDTO.getId().equals(id));
    }

    @Override
    public WebHookVO getById(Long projectId, Long id) {
        //1.查询WebHookVO
        WebHookDTO webHookDTO = checkExistedById(id);
        WebHookVO webHookVO = new WebHookVO();
        BeanUtils.copyProperties(webHookDTO, webHookVO);
        //2.查询可选的发送设置
        webHookVO.setTriggerEventSelection(sendSettingService.getUnderProject());
        //3.查询已选的发送设置主键
        List<WebHookMessageSettingDTO> byWebHookId = webHookMessageSettingService.getByWebHookId(id);
        webHookVO.setSendSettingIdList(CollectionUtils.isEmpty(byWebHookId) ? null : byWebHookId.stream().map(WebHookMessageSettingDTO::getSendSettingId).collect(Collectors.toSet()));
        return webHookVO;
    }

    @Override
    @Transactional
    public WebHookVO create(Long projectId, WebHookVO webHookVO) {
        //0.校验web hook path
        if (!checkPath(null, webHookVO.getWebhookPath())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }
        //1.新增WebHook
        if (webHookMapper.insertSelective(webHookVO) != 1) {
            throw new InsertException("error.web.hook.insert");
        }
        //2.新增WebHook的发送设置配置
        webHookMessageSettingService.update(webHookVO.getId(), webHookVO.getSendSettingIdList());
        //3.返回数据
        return getById(projectId, webHookVO.getId());
    }

    @Override
    @Transactional
    public WebHookVO update(Long projectId, WebHookVO webHookVO) {
        //0.校验web hook path
        if (!checkPath(webHookVO.getId(), webHookVO.getWebhookPath())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }
        //1.更新WebHook
        WebHookDTO webHookDTO = checkExistedById(webHookVO.getId());
        webHookDTO.setObjectVersionNumber(webHookVO.getObjectVersionNumber());
        if (webHookMapper.updateByPrimaryKeySelective(webHookDTO
                .setName(webHookVO.getName())
                .setType(webHookVO.getType())
                .setWebhookPath(webHookVO.getWebhookPath())) != 1) {
            throw new UpdateException("error.web.hook.update");
        }
        //2.更新WebHook的发送设置配置
        webHookMessageSettingService.update(webHookDTO.getId(), webHookVO.getSendSettingIdList());
        //3.返回更新数据
        return getById(projectId, webHookDTO.getId());
    }


    @Override
    @Transactional
    public void delete(Long id) {
        //1.如 WebHook 不存在，则取消删除
        try {
            checkExistedById(id);
        } catch (NotExistedException e) {
            return;
        }
        //2.删除 WebHook
        if (webHookMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.web.hook.delete");
        }
        //3.删除 WebHook Message
        webHookMessageSettingService.deleteByWebHookId(id);
    }

    @Override
    public WebHookDTO disabled(Long id) {
        WebHookDTO webHookDTO = checkExistedById(id);
        if (webHookDTO.getEnableFlag() && webHookMapper.updateByPrimaryKeySelective(webHookDTO.setEnableFlag(false)) != 1) {
            throw new UpdateException("error.web.hook.disabled");
        }
        return webHookDTO;
    }

    @Override
    public WebHookDTO enabled(Long id) {
        WebHookDTO webHookDTO = checkExistedById(id);
        if (!webHookDTO.getEnableFlag() && webHookMapper.updateByPrimaryKeySelective(webHookDTO.setEnableFlag(true)) != 1) {
            throw new UpdateException("error.web.hook.enabled");
        }
        return webHookDTO;
    }

    /**
     * 根据主键校验WebHook是否存在
     *
     * @param id WebHook主键
     * @return WebHook
     */
    private WebHookDTO checkExistedById(Long id) {
        return Optional.ofNullable(webHookMapper.selectByPrimaryKey(id))
                .orElseThrow(() -> new NotExistedException("error.web.hook.does.not.existed"));
    }

}
