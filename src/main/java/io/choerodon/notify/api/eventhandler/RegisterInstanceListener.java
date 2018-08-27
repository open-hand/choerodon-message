package io.choerodon.notify.api.eventhandler;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.RegisterInstancePayloadDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.infra.config.NotifyProperties;
import io.choerodon.swagger.notify.EmailTemplateScanData;
import io.choerodon.swagger.swagger.CustomSwagger2Controller;
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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

    public RegisterInstanceListener(EmailTemplateService emailTemplateService, NotifyProperties notifyProperties) {
        this.emailTemplateService = emailTemplateService;
        this.notifyProperties = notifyProperties;
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
                            emailTemplateService.createByScan(fetchEmailTemplate(payload));
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

    private Set<EmailTemplateScanData> fetchEmailTemplate(final RegisterInstancePayloadDTO payload) {
        String address = payload.getInstanceAddress();
        if (notifyProperties.getLocal()) {
            address = "127.0.0.1:" + address.split(":")[1];
        }
        ResponseEntity<String> response = restTemplate.getForEntity("http://"
                + address + CustomSwagger2Controller.CUSTOM_EMAIL_URL, String.class);

        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                JavaType javaType = mapper.getTypeFactory().constructCollectionType(HashSet.class, EmailTemplateScanData.class);
                return mapper.readValue(response.getBody(), javaType);
            } else {
                throw new RemoteAccessException("error.fetchEmailTemplate.httpRequest");
            }
        } catch (IOException e) {
            throw new CommonException("error.fetchEmailTemplate.jsonDeserialize", e);
        }

    }
}
