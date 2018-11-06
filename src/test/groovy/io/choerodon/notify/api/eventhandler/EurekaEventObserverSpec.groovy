package io.choerodon.notify.api.eventhandler

import io.choerodon.eureka.event.EurekaEventPayload
import io.choerodon.notify.api.service.EmailTemplateService
import io.choerodon.notify.api.service.SendSettingService
import io.choerodon.swagger.notify.NotifyScanData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class EurekaEventObserverSpec extends Specification {

    def "test receiveUpEvent"() {
        given: 'mock RegisterInstanceService'
        def restTemplate = Mock(RestTemplate) {
            getForEntity(_, NotifyScanData) >> new ResponseEntity<NotifyScanData>(new NotifyScanData(), HttpStatus.OK)
        }
        def emailTemplateService = Mock(EmailTemplateService)
        def sendSettingService = Mock(SendSettingService)
        def observer = new EurekaEventObserver(emailTemplateService, sendSettingService)
        observer.setRestTemplate(restTemplate)
        when:
        observer.receiveUpEvent(new EurekaEventPayload())
        then:
        1 * emailTemplateService.createByScan(_)
        1 * sendSettingService.createByScan(null)
    }

}
