package io.choerodon.notify.api.service.impl

import io.choerodon.core.iam.ResourceLevel
import io.choerodon.notify.api.dto.OrganizationProjectDTO
import io.choerodon.notify.api.service.ReceiveSettingService
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.infra.feign.UserFeignClient
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper
import io.choerodon.notify.infra.mapper.SendSettingMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class ReceiveSettingServiceImplSpec extends Specification {
    private ReceiveSettingMapper receiveSettingMapper = Mock(ReceiveSettingMapper)
    private SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private UserFeignClient userFeignClient = Mock(UserFeignClient)
    private ReceiveSettingService settingService =
            new ReceiveSettingServiceImpl(receiveSettingMapper, sendSettingMapper, userFeignClient)

    def "UpdateByUserIdAndSourceTypeAndSourceId"() {
        given: "构造请求参数"
        Long userId = 1L
        String sourceType = "site"
        Long sourceId = 1L
        String messageType = "site"
        boolean disable = true
        List<SendSetting> settingList = new ArrayList<>()
        SendSetting setting = new SendSetting()
        setting.setId(1L)
        settingList << setting

        when: "调用方法"
        settingService.updateByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId, messageType, disable)

        then: "校验参数"
        1 * sendSettingMapper.select(_) >> settingList
    }

    def "UpdateByUserId"() {
        given: "构造请求参数"
        Long userId = 1L
        String messageType = "site"
        boolean disable = true
        List<SendSetting> settingList = new ArrayList<>()
        SendSetting setting = new SendSetting()
        setting.setId(1L)
        setting.setLevel(ResourceLevel.SITE.value())
        settingList << setting
        SendSetting setting1 = new SendSetting()
        setting1.setId(1L)
        setting1.setLevel(ResourceLevel.ORGANIZATION.value())
        settingList << setting1
        SendSetting setting2 = new SendSetting()
        setting2.setId(1L)
        setting2.setLevel(ResourceLevel.PROJECT.value())
        settingList << setting2
        OrganizationProjectDTO dto = new OrganizationProjectDTO()
        dto.getOrganizationList().add(OrganizationProjectDTO.newInstanceOrganization(1L, "organization", "code"))
        dto.getProjectList().add(OrganizationProjectDTO.newInstanceProject(1L, "project", "code"))
        ResponseEntity<OrganizationProjectDTO> entity = new ResponseEntity<>(dto, HttpStatus.OK)

        when: "调用方法"
        settingService.updateByUserId(userId, messageType, disable)

        then: "校验参数"
        1 * sendSettingMapper.select(_) >> settingList
        1 * userFeignClient.queryByUserIdOrganizationProject(_) >> entity
    }
}
