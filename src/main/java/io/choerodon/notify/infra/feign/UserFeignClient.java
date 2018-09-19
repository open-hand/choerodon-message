package io.choerodon.notify.infra.feign;

import io.choerodon.notify.infra.config.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author dengyouquan
 **/
@FeignClient(name = "iam-service",
        configuration = FeignConfig.class,
        fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    @GetMapping("/v1/users/ids")
    ResponseEntity<Long[]> getUserIds();
}
