package io.choerodon.notify.infra.feign;

import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.feign.fallback.BaseFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈base-service Fegin接口〉
 *
 * @author wanghao
 * @Date 2019/12/11 19:00
 */
@FeignClient(value = "base-service", fallback = BaseFeignClientFallback.class)
public interface BaseFeignClient {

    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDTO>> listUsersByIds(@RequestBody Long[] ids, @RequestParam(value = "only_enabled") Boolean onlyEnabled);

}
