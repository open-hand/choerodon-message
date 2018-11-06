package io.choerodon.notify.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.swagger.CustomController;
import io.choerodon.swagger.notify.NotifyScanData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {

    private RestTemplate restTemplate = new RestTemplate();

    private final EmailTemplateService emailTemplateService;

    private final SendSettingService sendSettingService;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EurekaEventObserver(EmailTemplateService emailTemplateService, SendSettingService sendSettingService) {
        this.emailTemplateService = emailTemplateService;
        this.sendSettingService = sendSettingService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        NotifyScanData notifyScanData = fetchNotifyTemplate(payload);
        sendSettingService.createByScan(notifyScanData.getBusinessTypeScanData());
        emailTemplateService.createByScan(notifyScanData.getTemplateScanData());
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
       // do nothing
    }

    private NotifyScanData fetchNotifyTemplate(final EurekaEventPayload payload) {
        ResponseEntity<NotifyScanData> response = restTemplate.getForEntity("http://"
                + payload.getInstanceAddress() + CustomController.CUSTOM_NOTIFY_URL, NotifyScanData.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RemoteAccessException("error.fetchNotifyTemplate.httpRequest");
        }
    }

}
