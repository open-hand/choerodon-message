package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.SendSettingDetailDTO
import io.choerodon.notify.api.dto.SendSettingUpdateDTO
import io.choerodon.notify.infra.mapper.SendSettingMapper
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
class SendSettingOrgControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/send_settings"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private SendSettingMapper sendSettingMapper

    def "PageOrganization"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("organization_id", 1L)
        params.put("name", "忘记密码")
        params.put("code", "forgetPassword")
        params.put("type", "")
        params.put("isPredefined", true)

        when: "调用方法[全查询]"
        //组织层没有发送设置模板
        def entity = restTemplate.getForEntity(BASE_PATH + "/organizations/{organization_id}", Page, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 0

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/organizations/{organization_id}?name={name}&type={type}&code={code}&isPredefined={isPredefined}", Page, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 0
    }

    def "ListNames"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("organization_id", 1L)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/names/organizations/{organization_id}", Set, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == 0
    }

    def "Query"() {
        given: "构造请求参数"
        def params = new HashMap<String, Object>()
        params.put("organization_id", 1L)
        params.put("id", 8L)

        when: "调用方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{id}/organizations/{organization_id}", SendSettingDetailDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getId().equals(params.get("id"))
    }

    def "Update"() {
        given: "构造请求参数"
        SendSettingDetailDTO sendSetting = sendSettingMapper.selectById(5L)
        SendSettingUpdateDTO dto = new SendSettingUpdateDTO()
        dto.setEmailTemplateId(1L)
        dto.setObjectVersionNumber(1)
        def params = new HashMap<String, Object>()
        params.put("organization_id", 1L)
        params.put("id", sendSetting.getId())
        HttpEntity httpEntity = new HttpEntity<Object>(dto)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}/organizations/{organization_id}", HttpMethod.PUT, httpEntity, SendSettingDetailDTO, params)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getId().equals(sendSetting.getId())
        entity.getBody().getEmailTemplateId().equals(dto.getEmailTemplateId())
    }
}
