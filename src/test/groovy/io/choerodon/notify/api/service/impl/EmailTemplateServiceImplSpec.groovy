package io.choerodon.notify.api.service.impl


import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.service.EmailTemplateService
import io.choerodon.notify.infra.dto.SendSettingDTO
import io.choerodon.notify.infra.dto.Template
import io.choerodon.notify.infra.mapper.TemplateMapper
import io.choerodon.swagger.notify.NotifyTemplateScanData
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan*  */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class EmailTemplateServiceImplSpec extends Specification {
    private int count = 2
    private TemplateMapper templateMapper = Mock(TemplateMapper)
    private EmailTemplateService emailTemplateService = new EmailTemplateServiceImpl(templateMapper)

    def "CreateByScan"() {
        given: "构造请求参数"
        Set<NotifyTemplateScanData> set = new HashSet<>()
        for (int i = 0; i < count; i++) {
            NotifyTemplateScanData scanData = new NotifyTemplateScanData()
            scanData.setCode("test")
            scanData.setContent("content")
            scanData.setBusinessType("addUser")
            scanData.setTitle("title")
            scanData.setName("name")
            scanData.setType(i % 2 == 0 ? "pm" : "email")
            scanData.setBusinessType(i % 2 == 0 ? "pm" : "email")
            set.add(scanData)
        }
        Template template = new Template()
        SendSettingDTO sendSetting = new SendSettingDTO()

        when: "调用方法"
        emailTemplateService.createByScan(set)

        then: "校验结果"
        2 * templateMapper.selectOne(_) >> template
        2 * templateMapper.updateByPrimaryKeySelective(_)
    }
}
