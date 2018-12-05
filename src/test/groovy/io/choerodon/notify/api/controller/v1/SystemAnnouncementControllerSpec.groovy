package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.SystemAnnouncementDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class SystemAnnouncementControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/announcements"
    @Autowired
    private TestRestTemplate restTemplate

    def "Create"() {
        given: "构造请求参数"
        SystemAnnouncementDTO dto = new SystemAnnouncementDTO()
        dto.setContent("test")
        dto.setTitle("title")
        dto.setSendDate(new Date())

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH, dto, SystemAnnouncementDTO)

        then: "校验结果"
        entity.getBody().getContent().equals(dto.getContent())
        entity.getBody().getTitle().equals(dto.getTitle())
    }

    def "PagingQuery"() {
        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH, Page)

        then: "校验结果"
        entity.getBody().size() == 1
    }
}
