package io.choerodon.notify.infra.feign;

import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.config.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author dengyouquan
 **/
@FeignClient(name = "iam-service",
        configuration = FeignConfig.class,
        fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    @GetMapping("/v1/users/ids")
    ResponseEntity<Long[]> getUserIds();

    @PostMapping("/v1/users/ids")
    ResponseEntity<List<UserDTO>> listUsersByIds(@RequestBody Long[] ids);
}
