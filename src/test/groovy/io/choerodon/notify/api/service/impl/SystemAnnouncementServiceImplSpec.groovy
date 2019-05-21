package io.choerodon.notify.api.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.core.iam.ResourceLevel
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.notify.api.dto.SystemAnnouncementDTO
import io.choerodon.notify.api.service.utils.SpockUtils
import io.choerodon.notify.domain.QuartzTask
import io.choerodon.notify.domain.SystemAnnouncement
import io.choerodon.notify.infra.feign.AsgardFeignClient
import io.choerodon.notify.infra.feign.UserFeignClient
import io.choerodon.notify.infra.mapper.SystemAnnouncementMapper
import io.choerodon.notify.infra.utils.AsyncSendAnnouncementUtils
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Eugen
 * */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([DetailsHelper])
class SystemAnnouncementServiceImplSpec extends Specification {

    private final UserFeignClient userFeignClient = Mock(UserFeignClient)
    private AsyncSendAnnouncementUtils asyncSendAnnouncementUtils = Mock(AsyncSendAnnouncementUtils)
    private SystemAnnouncementMapper announcementMapper = Mock(SystemAnnouncementMapper)
    private AsgardFeignClient asgardFeignClient = Mock(AsgardFeignClient)

    SystemAnnouncementServiceImpl systemAnnouncementService = new SystemAnnouncementServiceImpl(asyncSendAnnouncementUtils, announcementMapper, userFeignClient, asgardFeignClient)

    @Shared
    private SystemAnnouncementDTO systemAnnouncementDTO
    @Shared
    private SystemAnnouncement systemAnnouncement
    private Long userId

    void setup() {
        systemAnnouncementDTO = new SystemAnnouncementDTO()
        systemAnnouncementDTO.setId(1L)
        systemAnnouncementDTO.setContent("test-1,发送站内信，时间为现在")
        systemAnnouncementDTO.setTitle("test-1")
        systemAnnouncementDTO.setSendDate(new Date())
        systemAnnouncementDTO.setSendNotices(true)

        systemAnnouncement = new SystemAnnouncement()
        systemAnnouncement.setId(1L)
        systemAnnouncement.setContent("test-1,发送站内信，时间为现在")
        systemAnnouncement.setTitle("test-1")
        systemAnnouncement.setSendDate(new Date())
        systemAnnouncement.setSendNotices(true)
        systemAnnouncement.setScheduleTaskId(1L)

        and: "mock静态方法-CustomUserDetails"
        PowerMockito.mockStatic(DetailsHelper)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(SpockUtils.getCustomUserDetails())
        userId = DetailsHelper.getUserDetails().getUserId()
    }

    def "Create"() {
        given: "mock"
        announcementMapper.insert(_) >> {
            return 1
        }
        announcementMapper.updateByPrimaryKeySelective(_) >> {
            return 1
        }
        asgardFeignClient.getMethodIdByCode("systemNotification") >> {
            def entity = Mock(ResponseEntity)
            entity.getBody() >> { return 1L }
            return entity
        }
        asgardFeignClient.createSiteScheduleTask(_) >> {
            def entity = Mock(ResponseEntity)
            entity.getBody() >> {
                def task = new QuartzTask()
                task.setId(1)
                return task
            }
            return entity
        }
        announcementMapper.selectByPrimaryKey(_) >> {
            return new SystemAnnouncement()
        }
        when: "方法调用"
        def create = systemAnnouncementService.create(systemAnnouncementDTO)
        then: "结果判断"
        noExceptionThrown()
    }

    def "Update"() {
        given: "mock"
        systemAnnouncement.setStatus(SystemAnnouncementDTO.AnnouncementStatus.WAITING.value())
        announcementMapper.selectByPrimaryKey(_) >> { return systemAnnouncement }
        asgardFeignClient.getMethodIdByCode("systemNotification") >> {
            def entity = Mock(ResponseEntity)
            entity.getBody() >> { return 1L }
            return entity
        }
        asgardFeignClient.createSiteScheduleTask(_) >> {
            def entity = Mock(ResponseEntity)
            entity.getBody() >> {
                def task = new QuartzTask()
                task.setId(1)
                return task
            }
            return entity
        }
        announcementMapper.updateByPrimaryKeySelective(_) >> {
            return 1
        }
        when: "方法调用"
        def update = systemAnnouncementService.update(systemAnnouncementDTO, ResourceLevel.SITE, 0L)
        then: "结果判断"
        noExceptionThrown()
    }

    def "PagingQuery"() {
        given: "参数准备"
        def title = "标题"
        def content = "content"
        def params = "params"
        def status = "status"
        def sendNotices = false
        and: "构造pageRequest"
        when: "方法调用"
        systemAnnouncementService.pagingQuery(1,2, title, content, params, status, sendNotices)
        then: "结果比对"
        noExceptionThrown()
    }

    def "GetDetailById"() {
        given: "参数准备"
        def id = 1L
        announcementMapper.selectByPrimaryKey(id) >> { return systemAnnouncement }
        when: "方法调用"
        systemAnnouncementService.getDetailById(id)
        then: "结果比对"
        noExceptionThrown()
    }

    def "Delete"() {
        given: "参数准备"
        def id = 1L
        announcementMapper.deleteByPrimaryKey(id) >> { return 1 }
        announcementMapper.selectByPrimaryKey(id) >> { return systemAnnouncement }
        when: "方法调用"
        systemAnnouncementService.delete(id)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "SendSystemNotification"() {
        given: "参数准备"
        def id = 1L

        and: "mock"
        announcementMapper.selectByPrimaryKey(id) >> { return systemAnnouncement }
        userFeignClient.getUserIds() >> {
            def entity = Mock(ResponseEntity)
            entity.getBody() >> {
                def longs = new Long[2]
                longs[0] = 1L
                longs[1] = 2L
                return longs
            }
            return entity
        }

        announcementMapper.updateByPrimaryKey(_) >> { return 1 }
        when: "方法调用"
        systemAnnouncementService.sendSystemNotification(ResourceLevel.SITE, 0L, id)
        then: "结果比对"
        noExceptionThrown()
    }

    def "Exception"() {
        when: "create插入失败"
        systemAnnouncementService.create(systemAnnouncementDTO)
        then: "异常捕获"
        def e1 = thrown(CommonException)
        e1.message == "error.systemAnnouncement.create"

        when: "update原公告不存在"
        systemAnnouncementService.update(systemAnnouncementDTO,ResourceLevel.SITE,0L)
        then: "异常捕获"
        def e2 = thrown(CommonException)
        e2.message == "error.update.system.announcement.not.exist,id:" + systemAnnouncementDTO.getId()

        when: "getUserDetails公告不存在"
        systemAnnouncementService.getDetailById(1L)
        then: "异常捕获"
        def e3 = thrown(CommonException)
        e3.message == "error.system.announcement.not.exist,id:" + 1L

        when: "delete公告不存在"
        systemAnnouncementService.delete(1L)
        then: "异常捕获"
        def e4 = thrown(CommonException)
        e4.message == "error.delete.system.announcement.not.exist,id:" + 1L


        when: "执行可执行程序时公告不存在"
        systemAnnouncementService.sendSystemNotification(ResourceLevel.SITE, 0L, 1L)
        then: "异常捕获"
        def e5 = thrown(CommonException)
        e5.message == "error.systemAnnouncement.empty,id:" + 1L
    }


}
