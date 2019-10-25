package io.choerodon.notify.api.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.pojo.MessageType
import io.choerodon.notify.api.service.EmailTemplateService
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.infra.dto.Template
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import io.choerodon.swagger.notify.NotifyTemplateScanData
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class EmailTemplateServiceImplSpec extends Specification {
    private int count = 2
    private TemplateMapper templateMapper = Mock(TemplateMapper)
    private SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private EmailTemplateService emailTemplateService = new EmailTemplateServiceImpl(templateMapper, sendSettingMapper)

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
        SendSetting sendSetting = new SendSetting()

        when: "调用方法"
        emailTemplateService.createByScan(set)

        then: "校验结果"
        2 * templateMapper.selectOne(_) >> template
        2 * templateMapper.updateByPrimaryKeySelective(_)
        2 * sendSettingMapper.updateByPrimaryKey(_)
        2 * sendSettingMapper.selectOne(_) >> sendSetting
    }

    def "Delete"() {
        given: "构造请求参数"
        Template dbTemplate = new Template()
        dbTemplate.setMessageType(MessageType.EMAIL.getValue())

        when: "调用方法[异常-邮件模板不存在]"
        emailTemplateService.delete(1L)
        then: "校验结果"
        1 * templateMapper.selectByPrimaryKey(_)
        def exception = thrown(CommonException)
        exception.getCode().equals("error.emailTemplate.notExist")

        when: "调用方法[异常-不能删除预定义]"
        emailTemplateService.delete(1L)
        then: "校验结果"
        1 * templateMapper.selectByPrimaryKey(_) >> dbTemplate
        exception = thrown(CommonException)
        exception.getCode().equals("error.emailTemplate.cannotDeletePredefined")

        when: "调用方法[异常-删除]"
        dbTemplate.setIsPredefined(false)
        emailTemplateService.delete(1L)
        then: "校验结果"
        1 * templateMapper.selectByPrimaryKey(_) >> dbTemplate
        1 * templateMapper.deleteByPrimaryKey(_) >> 0
        exception = thrown(CommonException)
        exception.getCode().equals("error.emailTemplate.delete")
    }

    def "Check"() {
        given: "构造请求参数"
        def code = "code"

        when: "调用方法"
        emailTemplateService.check(code)
        then: "校验结果"
        def exception = thrown(CommonException)
        exception.getCode().equals("error.emailTemplate.codeSiteExist")
        1 * templateMapper.selectLevelByCode(_, _) >> "site"

        when: "调用方法"
        emailTemplateService.check(code)
        then: "校验结果"
        exception = thrown(CommonException)
        exception.getCode().equals("error.emailTemplate.codeOrgExist")
        1 * templateMapper.selectLevelByCode(_, _) >> "organization"
    }
}
