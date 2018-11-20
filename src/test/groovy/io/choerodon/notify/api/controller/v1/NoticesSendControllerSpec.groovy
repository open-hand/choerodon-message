package io.choerodon.notify.api.controller.v1

import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.EmailConfigDTO
import io.choerodon.notify.api.dto.NoticeSendDTO
import io.choerodon.notify.api.dto.UserDTO
import io.choerodon.notify.api.service.NoticesSendService
import io.choerodon.notify.api.service.WebSocketSendService
import io.choerodon.notify.api.service.impl.NoticesSendServiceImpl
import io.choerodon.notify.domain.Config
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.feign.UserFeignClient
import io.choerodon.notify.infra.mapper.ConfigMapper
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class NoticesSendControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private SiteMsgRecordMapper recordMapper
    @Autowired
    private SendSettingMapper sendSettingMapper
    @Autowired
    private TemplateMapper templateMapper
    @Autowired
    private ConfigMapper configMapper
    @Autowired
    private NoticesSendServiceImpl noticesSendService
    @Autowired
    private WebSocketSendService webSocketSendService
    @Autowired
    private NoticesSendController noticesSendController

    def setup() {
        given: "mock"
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClient)
        List<UserDTO> userDTOS = new ArrayList<>()
        userDTOS.add(new UserDTO(id: 1L, email: "123@qq.com"))
        ResponseEntity<List<UserDTO>> entity = new ResponseEntity<>(userDTOS, HttpStatus.OK)
        Mockito.when(userFeignClient.listUsersByIds(Mockito.any())).thenReturn(entity)

        noticesSendService.setUserFeignClient(userFeignClient)
        noticesSendController = new NoticesSendController(noticesSendService, webSocketSendService)
    }

    def "PostNotice"() {
        given: "构造请求参数"
        def userId = 1L
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO()
        noticeSendDTO.setCode("addUser")
        noticeSendDTO.setFromUserId(1L)
        List<Long> ids = new ArrayList<>()
        ids.add(userId)
        noticeSendDTO.setTargetUsersIds(ids)
        Map<String, Object> paramsMap = new HashMap<>()
        paramsMap.put("addCount", 1L)
        noticeSendDTO.setParams(paramsMap)

        and: "关联发送设置和站内信模板"
        SendSetting sendSetting = new SendSetting()
        sendSetting.setCode("addUser")
        sendSetting = sendSettingMapper.selectOne(sendSetting)
        Template template = new Template()
        template.setCode("addUser-preset")
        template = templateMapper.selectOne(template)
        sendSetting.setPmTemplateId(template.getId())
        sendSetting.setObjectVersionNumber(1)
        sendSettingMapper.updateByPrimaryKeySelective(sendSetting)

        and: "关联发送设置和邮件模板"
        sendSetting = new SendSetting()
        sendSetting.setCode("forgetPassword")
        sendSetting = sendSettingMapper.selectOne(sendSetting)
        template = new Template()
        template.setCode("forgetPassword-preset")
        template = templateMapper.selectOne(template)
        template.setEmailContent(template.getPmContent())
        templateMapper.updateByPrimaryKey(template)
        sendSetting.setEmailTemplateId(template.getId())
        sendSetting.setObjectVersionNumber(1)
        sendSettingMapper.updateByPrimaryKeySelective(sendSetting)

        and: "创建config"
        Config config = new Config()
        config.setEmailAccount("noreply@choerodon.com.cn")
        config.setEmailPassword("password")
        config.setEmailSendName("choerodon")
        config.setEmailProtocol("SMTP")
        config.setEmailSsl(true)
        config.setEmailHost("smtp.aliyun.com")
        config.setEmailPort(22)
        configMapper.insert(config)

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH, noticeSendDTO, Void)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        recordMapper.selectAll().size() == 1
        recordMapper.selectAll().get(0).getUserId().equals(userId)

        when: "调用方法"
        noticeSendDTO.setCode("forgetPassword")
        entity = restTemplate.postForEntity(BASE_PATH, noticeSendDTO, Void)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        recordMapper.deleteByPrimaryKey(recordMapper.selectAll().get(0))
        configMapper.deleteByPrimaryKey(config)
    }

    def "PostWebSocket"() {
        given: "构造请求参数"
        String message = "message"

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH + "/ws/{code}/{id}", message, Void, "code", "2")

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }
}
