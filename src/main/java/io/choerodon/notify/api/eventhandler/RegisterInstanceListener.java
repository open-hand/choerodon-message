package io.choerodon.notify.api.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.api.dto.RegisterInstancePayloadDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.infra.config.NotifyProperties;
import io.choerodon.swagger.CustomController;
import io.choerodon.swagger.notify.NotifyScanData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

@Component
public class RegisterInstanceListener {

    private static final String REGISTER_TOPIC = "register-server";
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterInstanceListener.class);
    private static final String STATUS_UP = "UP";
    private final ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailTemplateService emailTemplateService;

    private final NotifyProperties notifyProperties;
    private final SendSettingService sendSettingService;

    public RegisterInstanceListener(EmailTemplateService emailTemplateService,
                                    NotifyProperties notifyProperties,
                                    SendSettingService sendSettingService) {
        this.emailTemplateService = emailTemplateService;
        this.notifyProperties = notifyProperties;
        this.sendSettingService = sendSettingService;
    }

    /**
     * 监听eureka-instance消息队列的新消息处理
     *
     * @param record 消息信息
     */
    @KafkaListener(topics = REGISTER_TOPIC)
    public void handle(ConsumerRecord<byte[], byte[]> record) {
        String message = new String(record.value());
        try {
            LOGGER.info("receive message from register-server, {}", message);
            RegisterInstancePayloadDTO payload = mapper.readValue(message, RegisterInstancePayloadDTO.class);
            boolean isSkipService =
                    notifyProperties.getSkipServices().stream().anyMatch(t -> t.equals(payload.getAppName()));
            if (isSkipService) {
                LOGGER.info("skip message that is skipServices, {}", payload);
                return;
            }
            Observable.just(payload)
                    .map(t -> {
                        if (STATUS_UP.equals(payload.getStatus())) {
                            NotifyScanData notifyScanData = fetchNotifyTemplate(payload);
                            emailTemplateService.createByScan(notifyScanData.getTemplateScanData());
                            sendSettingService.createByScan(notifyScanData.getBusinessTypeScanData());
                        }
                        return t;
                    })
                    .retryWhen(x -> x.zipWith(Observable.range(1, notifyProperties.getFetchTime()),
                            (t, retryCount) -> {
                                if (retryCount >= notifyProperties.getFetchTime()) {
                                    if (t instanceof RemoteAccessException || t instanceof RestClientException) {
                                        LOGGER.warn("error.registerConsumer.fetchDataError, payload {}, cause {}", payload, t);
                                    } else {
                                        LOGGER.warn("error.registerConsumer.msgConsumerError, payload {}, cause {}", payload, t);
                                    }
                                }
                                return retryCount;
                            }).flatMap(y -> Observable.timer(2, TimeUnit.SECONDS)))
                    .subscribeOn(Schedulers.io())
                    .subscribe((RegisterInstancePayloadDTO registerInstancePayload) -> {
                    });
        } catch (Exception e) {
            LOGGER.warn("error happened when handle message， {} cause {}", message, e);
        }
    }

    private NotifyScanData fetchNotifyTemplate(final RegisterInstancePayloadDTO payload) {
        String address = payload.getInstanceAddress();
        if (notifyProperties.getLocal()) {
            address = "127.0.0.1:" + address.split(":")[1];
        }
        ResponseEntity<NotifyScanData> response = restTemplate.getForEntity("http://"
                + address + CustomController.CUSTOM_NOTIFY_URL, NotifyScanData.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RemoteAccessException("error.fetchNotifyTemplate.httpRequest");
        }
    }
}
