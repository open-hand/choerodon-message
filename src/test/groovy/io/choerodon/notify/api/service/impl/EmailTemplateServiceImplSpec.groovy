package io.choerodon.notify.api.service.impl

import io.choerodon.notify.IntegrationTestConfiguration
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
    def "CreateByScan"() {
        given: "构造请求参数"
        Set<NotifyTemplateScanData> set = new HashSet<>()
        NotifyTemplateScanData scanData = new NotifyTemplateScanData()
        scanData.setCode("test")
        scanData.setContent("content")
        scanData.setBusinessType("addUser")
        scanData.setTitle("title")
        scanData.setName("name")
        scanData.setType("pm")

        when: "调用方法"

        then: "校验结果"
    }

    def "Check"() {
    }
}
