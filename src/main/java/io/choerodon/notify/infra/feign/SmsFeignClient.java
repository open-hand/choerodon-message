package io.choerodon.notify.infra.feign;

import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.config.FeignConfig;
import io.choerodon.notify.infra.feign.fallback.SmsFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-17
 */
@FeignClient(name = "sms-service",
        configuration = FeignConfig.class,
        fallback = SmsFeignClientFallback.class,
        path = "/v1/sms")
public interface SmsFeignClient {

    @PostMapping
    ResponseEntity<List<UserDTO>> send(@RequestBody NoticeSendDTO noticeSendDTO);

}
