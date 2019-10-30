package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.PmTemplateDTO
import io.choerodon.notify.infra.enums.SendingTypeEnum
import io.choerodon.notify.infra.dto.Template
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
class PmTemplateSiteControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/letters/templates"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private TemplateMapper templateMapper

    def "PageSite"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("name", "添加新用户")
        params.put("code", "addUser-preset")
        params.put("type", "")
        params.put("isPredefined", true)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH, Page, 1L)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 0

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "?name={name}&type={type}&code={code}&isPredefined={isPredefined}", Page, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 0
    }

    def "ListNames"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("business_type", "addUser")

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/names", List, 1L)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 7

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/names?business_type={business_type}", List, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 1
    }

    def "Query"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("id", 3L)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{id}", PmTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getId().equals(params.get("id"))
    }

    def "Check"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("code", "addUser")

        when: "调用方法[全查询]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/check?code={code}", void, Void, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
    }

    def "Create"() {
        given: "构造请求参数"
        PmTemplateDTO pmTemplateDTO = new PmTemplateDTO()
        pmTemplateDTO.setCode("testPmSite")
        pmTemplateDTO.setName("测试站内信模板")
        pmTemplateDTO.setType("addUser")
        pmTemplateDTO.setContent("\${content}")
        pmTemplateDTO.setIsPredefined(true)
        pmTemplateDTO.setTitle("测试站内信")
        def params = new HashMap<String, Object>()

        when: "调用方法[全查询]"
        def entity = restTemplate.postForEntity(BASE_PATH, pmTemplateDTO, PmTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals(pmTemplateDTO.getCode())
        entity.getBody().getName().equals(pmTemplateDTO.getName())
        entity.getBody().getType().equals(pmTemplateDTO.getType())
        entity.getBody().getTitle().equals(pmTemplateDTO.getTitle())
        entity.getBody().getContent().equals(pmTemplateDTO.getContent())
        !entity.getBody().getIsPredefined()
        Template template = new Template()
        template.setCode(pmTemplateDTO.getCode())
        templateMapper.delete(template)
    }

    def "Update"() {
        given: "插入测试数据"
        Template template = new Template()
        template.setCode("testEmailSite1")
        template.setName("测试站内信模板")
        template.setBusinessType("addUser")
        template.setEmailContent("{content}")
        template.setIsPredefined(false)
        template.setEmailTitle("测试站内信")
        template.setMessageType(SendingTypeEnum.PM.getValue())
        templateMapper.insert(template)

        and: "构造请求参数"
        PmTemplateDTO pmTemplateDTO = new PmTemplateDTO()
        pmTemplateDTO.setCode("testPm-update")
        pmTemplateDTO.setName("测试站内信模板-update")
        pmTemplateDTO.setType("addUser")
        pmTemplateDTO.setContent("\${content}-update")
        pmTemplateDTO.setIsPredefined(true)
        pmTemplateDTO.setTitle("测试站内信-update")
        pmTemplateDTO.setObjectVersionNumber(1)
        def params = new HashMap<String, Object>()
        params.put("id", template.getId())
        HttpEntity httpEntity = new HttpEntity<Object>(pmTemplateDTO)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.PUT, httpEntity, PmTemplateDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals(pmTemplateDTO.getCode())
        entity.getBody().getName().equals(pmTemplateDTO.getName())
        entity.getBody().getType().equals(pmTemplateDTO.getType())
        entity.getBody().getTitle().equals(pmTemplateDTO.getTitle())
        entity.getBody().getContent().equals(pmTemplateDTO.getContent())
        !entity.getBody().getIsPredefined()
        templateMapper.deleteByPrimaryKey(template.getId())
    }

    def "Delete"() {
        given: "构造请求参数"
        Template template = new Template()
        template.setCode("testPmSite2")
        template.setName("测试站内信模板")
        template.setBusinessType("forgetPassword")
        template.setEmailContent("\${content}")
        template.setIsPredefined(false)
        template.setEmailTitle("测试站内信")
        template.setMessageType(SendingTypeEnum.PM.value)
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
