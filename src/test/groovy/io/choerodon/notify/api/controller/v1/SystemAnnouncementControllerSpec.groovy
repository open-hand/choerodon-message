package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.core.iam.ResourceLevel
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.SystemAnnouncementDTO
import io.choerodon.notify.api.service.SystemAnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SystemAnnouncementControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/system_notice"
    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private SystemAnnouncementController systemAnnouncementController

    private SystemAnnouncementService systemAnnouncementService = Mock(SystemAnnouncementService)


    @Shared
    private SystemAnnouncementDTO systemAnnouncementDTO

    void setup() {

        systemAnnouncementDTO = new SystemAnnouncementDTO()
        systemAnnouncementDTO.setContent("test-1,发送站内信，时间为现在")
        systemAnnouncementDTO.setTitle("test-1")
        systemAnnouncementDTO.setSendNotices(true)
        systemAnnouncementDTO.setSendDate(new Date())
        systemAnnouncementDTO.setSticky(false)
    }

    def "Create"() {
        given: "参数准备"
        systemAnnouncementController.setSystemAnnouncementService(systemAnnouncementService)
        when: "创建系统公告"
        def announcements = restTemplate.postForEntity(BASE_PATH + "/create", systemAnnouncementDTO, SystemAnnouncementDTO)
        then: "无异常抛出，状态码良好"
        announcements.statusCode.is2xxSuccessful()
        noExceptionThrown()
    }

    def "PagingQuery"() {
        given: "参数准备"
        systemAnnouncementController.setSystemAnnouncementService(systemAnnouncementService)
        when: "分页查询系统公告"
        def page = restTemplate.getForEntity(BASE_PATH + "/all", Page)
        then: "无异常抛出，状态码正确"
        noExceptionThrown()
        page.statusCode.is2xxSuccessful()
        1 * systemAnnouncementService.pagingQuery(_, _, _, _, _, _,_)
    }

    def "GetDetail"() {
        given: "参数准备"
        systemAnnouncementController.setSystemAnnouncementService(systemAnnouncementService)
        def id = 1
        when: "获取系统公告的详情"
        def announcement = restTemplate.getForEntity(BASE_PATH + "/{id}", SystemAnnouncementDTO, id)
        then: "无异常抛出，状态码正确"
        noExceptionThrown()
        announcement.statusCode.is2xxSuccessful()
        1 * systemAnnouncementService.getDetailById(id)
    }

    def "Delete"() {
        given: "参数准备"
        systemAnnouncementController.setSystemAnnouncementService(systemAnnouncementService)
        def id = 1
        HttpEntity httpEntity = new HttpEntity<Object>()
        when: "根据Id删除系统公告"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.DELETE, httpEntity, Void, id)
        then: "无异常抛出，状态码正确"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
        1 * systemAnnouncementService.delete(id)

    }

    def "Update"() {
        given: "参数准备"
        systemAnnouncementController.setSystemAnnouncementService(systemAnnouncementService)
        HttpEntity httpEntity = new HttpEntity<Object>(systemAnnouncementDTO)
        when: "更新系统公告"
        def entity = restTemplate.exchange(BASE_PATH + "/update", HttpMethod.PUT, httpEntity, SystemAnnouncementDTO)
        then: "无异常抛出，状态码正确"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
        1 * systemAnnouncementService.update(_, ResourceLevel.SITE, 0L)

    }
}
