package io.choerodon.notify.api.eventhandler

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.exception.CommonException
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.RegisterInstancePayloadDTO
import io.choerodon.notify.api.service.EmailTemplateService
import io.choerodon.notify.api.service.SendSettingService
import io.choerodon.notify.infra.config.NotifyProperties
import io.choerodon.swagger.notify.NotifyScanData
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RegisterInstanceListenerSpec extends Specification {
    private EmailTemplateService emailTemplateService = Mock(EmailTemplateService)
    private NotifyProperties notifyProperties = new NotifyProperties()
    private SendSettingService sendSettingService = Mock(SendSettingService)
    private RegisterInstanceListener listener = new RegisterInstanceListener(emailTemplateService,
            notifyProperties, sendSettingService)
    private ObjectMapper mapper = new ObjectMapper()

    def "Handle"() {
        given: "构造请求参数"
        RegisterInstancePayloadDTO payloadDTO = new RegisterInstancePayloadDTO()
        def REGISTER_TOPIC = 'register-server'
        payloadDTO.setStatus("UP")
        payloadDTO.setVersion('v1')
        payloadDTO.setInstanceAddress('127.0.0.1:9092')
        payloadDTO.setAppName('manager-service')
        payloadDTO.setApiData('{}')
        def upRecord = new ConsumerRecord<byte[], byte[]>(REGISTER_TOPIC, 1, 999L, ''.getBytes(), mapper.writeValueAsBytes(payloadDTO))

        when: "调用方法"
        listener.handle(upRecord)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法[mock restTemplate]"
        NotifyScanData scanData = new NotifyScanData()
        ResponseEntity<NotifyScanData> entity = new ResponseEntity<>(scanData, HttpStatus.OK)
        Field field = listener.getClass().getDeclaredField("restTemplate")
        RestTemplate restTemplate = Mock(RestTemplate)
        restTemplate.getForEntity(_, _) >> entity
        field.setAccessible(true)
        field.set(listener, restTemplate)
        listener.handle(upRecord)
        then: "校验结果"
        noExceptionThrown()
    }
}
