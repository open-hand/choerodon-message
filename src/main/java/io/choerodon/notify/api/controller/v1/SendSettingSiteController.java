package io.choerodon.notify.api.controller.v1;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.vo.MessageServiceSearchVO;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.swagger.annotation.CustomPageRequest;

@RestController
@RequestMapping("v1/notices/send_settings")
@Api("全局层发送设置接口")
public class SendSettingSiteController {

    private SendSettingService sendSettingService;

    public SendSettingSiteController(SendSettingService sendSettingService) {
        this.sendSettingService = sendSettingService;
    }

    @GetMapping("/names")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层获取业务类型名称列表")
    public ResponseEntity<Set<BusinessTypeDTO>> listNames() {
        return new ResponseEntity<>(sendSettingService.listNames(), HttpStatus.OK);
    }

    @PostMapping("/list")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层分页查询消息服务列表")
    @CustomPageRequest
    public ResponseEntity<PageInfo<MessageServiceVO>> pageSite(@ApiIgnore
                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                               @RequestBody(required = false) MessageServiceSearchVO messageQueryVO) {
        SendSetting filterDTO = new SendSetting();
        if (messageQueryVO.getMessageType() != null) {
            filterDTO.setName(messageQueryVO.getMessageType());
        }
        if (messageQueryVO.getIntroduce() != null) {
            filterDTO.setDescription(messageQueryVO.getIntroduce());
        }
        if (messageQueryVO.getLevel() != null) {
            filterDTO.setLevel(messageQueryVO.getLevel());
        }
        if (messageQueryVO.getEnabled() != null) {
            filterDTO.setEnabled(messageQueryVO.getEnabled());
        }
        if (messageQueryVO.getAllowConfig() != null) {
            filterDTO.setAllowConfig(messageQueryVO.getAllowConfig());
        }
        return new ResponseEntity<>(sendSettingService.pagingAll(filterDTO, messageQueryVO.getParams(), pageRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查看发送设置详情")
    public ResponseEntity<SendSettingDetailDTO> query(@PathVariable Long id) {
        return new ResponseEntity<>(sendSettingService.query(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层更新发送设置")
    public ResponseEntity<SendSetting> update(@PathVariable Long id,
                                              @RequestBody @Valid SendSettingUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return new ResponseEntity<>(sendSettingService.update(updateDTO), HttpStatus.OK);
    }

    @GetMapping("/list/allow_config")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据层级查询未禁用通知配置的启用状态的发送设置列表")
    public ResponseEntity<List<SendSettingDetailDTO>> listLevelAndAllowConfig(@RequestParam(name = "source_type", required = false) String level) {
        return new ResponseEntity<>(sendSettingService.queryByLevelAndAllowConfig(level, true), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "根据Id删除发送设置")
    public ResponseEntity delSendSetting(@PathVariable Long id) {
        sendSettingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }


    @PutMapping("/{id}/enabled")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据id启用消息服务")
    public ResponseEntity<MessageServiceVO> enabled(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.enabled(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/disabled")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据id停用消息服务")
    public ResponseEntity<MessageServiceVO> disabled(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.disabled(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/allow_configuration")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "允许配置接受设置")
    public ResponseEntity<MessageServiceVO> allowConfig(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.allowConfiguration(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/forbidden_configuration")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "禁止配置接受设置")
    public ResponseEntity<MessageServiceVO> forbiddenConfig(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.forbiddenConfiguration(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/email_send_setting")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "获取邮件内容的发送设置信息")
    public ResponseEntity<EmailSendSettingVO> getEmailSendSetting(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.getEmailSendSetting(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/email_send_setting")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改邮件内容的发送设置信息")
    public ResponseEntity<EmailSendSettingVO> updateEmailSendSetting(@PathVariable("id") Long id,
                                                                     @RequestBody EmailSendSettingVO updateVO) {
        updateVO.setId(id);
        return new ResponseEntity<>(sendSettingService.updateEmailSendSetting(updateVO), HttpStatus.OK);
    }


    @GetMapping("/{id}/pm_send_setting")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "获取站内信内容的发送设置信息")
    public ResponseEntity<PmSendSettingVO> getPmSendSetting(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.getPmSendSetting(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/pm_send_setting")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改站内信内容的发送设置信息")
    public ResponseEntity<PmSendSettingVO> updatePmSendSetting(@PathVariable("id") Long id,
                                                               @RequestBody PmSendSettingVO updateVO) {
        updateVO.setId(id);
        return new ResponseEntity<>(sendSettingService.updatePmSendSetting(updateVO), HttpStatus.OK);
    }
}
