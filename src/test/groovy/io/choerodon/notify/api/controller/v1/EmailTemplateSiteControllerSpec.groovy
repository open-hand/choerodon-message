package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.EmailTemplateDTO
import io.choerodon.notify.api.pojo.MessageType
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.mapper.TemplateMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class EmailTemplateSiteControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/emails/templates"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private TemplateMapper templateMapper

    def "PageSite"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("name", "忘记密码")
        params.put("code", "forgetPassword-preset")
        //疑惑，type和name的关联？
        params.put("type", "")
        params.put("isPredefined", true)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH, Page, 1L)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 2

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "?name={name}&type={type}&code={code}&isPredefined={isPredefined}", Page, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 1
    }

    def "ListNames"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("business_type", "forgetPassword")

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/names", List, 1L)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 2

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/names?business_type={business_type}", List, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 1
    }

    def "Query"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("id", 8L)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{id}", EmailTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getId().equals(params.get("id"))
    }

    def "Check"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("code", "forgetPassword")

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/check?code={code}", Void, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
    }

    def "Create"() {
        given: "构造请求参数"
        EmailTemplateDTO emailTemplateDTO = new EmailTemplateDTO()
        emailTemplateDTO.setCode("testEmailSite")
        emailTemplateDTO.setName("测试邮件模板")
        emailTemplateDTO.setType("forgetPassword")
        emailTemplateDTO.setContent("\${content}")
        emailTemplateDTO.setIsPredefined(true)
        emailTemplateDTO.setTitle("测试邮件")
        def params = new HashMap<String, Object>()

        when: "调用方法[全查询]"
        def entity = restTemplate.postForEntity(BASE_PATH, emailTemplateDTO, EmailTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals(emailTemplateDTO.getCode())
        entity.getBody().getName().equals(emailTemplateDTO.getName())
        entity.getBody().getType().equals(emailTemplateDTO.getType())
        entity.getBody().getTitle().equals(emailTemplateDTO.getTitle())
        entity.getBody().getContent().equals(emailTemplateDTO.getContent())
        !entity.getBody().getIsPredefined()
        Template template = new Template()
        template.setCode(emailTemplateDTO.getCode())
        templateMapper.delete(template)
    }

    def "Update"() {
        given: "插入测试数据"
        Template template = new Template()
        template.setCode("testEmailSite1")
        template.setName("测试邮件模板")
        template.setBusinessType("forgetPassword")
        template.setEmailContent("{content}")
        template.setIsPredefined(false)
        template.setEmailTitle("测试邮件")
        template.setMessageType(MessageType.EMAIL.getValue())
        templateMapper.insert(template)

        and: "构造请求参数"
        EmailTemplateDTO emailTemplateDTO = new EmailTemplateDTO()
        emailTemplateDTO.setCode("testEmail-update")
        emailTemplateDTO.setName("测试邮件模板-update")
        emailTemplateDTO.setType("forgetPassword")
        emailTemplateDTO.setContent("\${content}-update")
        emailTemplateDTO.setIsPredefined(true)
        emailTemplateDTO.setTitle("测试邮件-update")
        emailTemplateDTO.setObjectVersionNumber(1)
        def params = new HashMap<String, Object>()
        params.put("id", template.getId())
        HttpEntity httpEntity = new HttpEntity<Object>(emailTemplateDTO)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.PUT, httpEntity, EmailTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals(emailTemplateDTO.getCode())
        entity.getBody().getName().equals(emailTemplateDTO.getName())
        entity.getBody().getType().equals(emailTemplateDTO.getType())
        entity.getBody().getTitle().equals(emailTemplateDTO.getTitle())
        entity.getBody().getContent().equals(emailTemplateDTO.getContent())
        !entity.getBody().getIsPredefined()
        templateMapper.deleteByPrimaryKey(template.getId())
    }

    def "Delete"() {
        given: "构造请求参数"
        Template template = new Template()
        template.setCode("testEmailSite2")
        template.setName("测试邮件模板")
        template.setBusinessType("forgetPassword")
        template.setEmailContent("\${content}")
        template.setIsPredefined(false)
        template.setEmailTitle("测试邮件")
        template.setMessageType(MessageType.EMAIL.value)
        templateMapper.insert(template)
        def params = new HashMap<String, Object>()
        params.put("id", template.getId())
        HttpEntity httpEntity = new HttpEntity<Object>()

        when: "调用方法[全查询]"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.DELETE, httpEntity, Void, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
    }
}
