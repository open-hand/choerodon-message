package io.choerodon.notify.api.service.impl

import io.choerodon.notify.api.service.SendSettingService
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.swagger.notify.NotifyBusinessTypeScanData
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class SendSettingServiceImplSpec extends Specification {
    private int count = 2
    private SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private SendSettingService settingService = new SendSettingServiceImpl(sendSettingMapper)

    def "CreateByScan"() {
        given: "构造参数"
        Set<NotifyBusinessTypeScanData> set = new HashSet<>()
        for (int i = 0; i < count; i++) {
            NotifyBusinessTypeScanData scanData = new NotifyBusinessTypeScanData()
            scanData.setCode("test")
            scanData.setRetryCount(1)
            scanData.setLevel("site")
            scanData.setDescription("description")
            scanData.setName("name")
            set.add(scanData)
        }

        when: "调用方法"
        settingService.createByScan(set)

        then: "校验结果"
        sendSettingMapper.selectOne(_) >>> [null,new SendSetting()]
        1 * sendSettingMapper.updateByPrimaryKeySelective(_)
    }
}
