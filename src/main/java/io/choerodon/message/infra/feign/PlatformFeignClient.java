package io.choerodon.message.infra.feign;

import io.choerodon.message.infra.feign.fallback.PlatFormFeignClientFallback;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * @author scp
 * @date 2020/5/10
 * @description
 */

@FeignClient(value = HZeroService.Platform.NAME, fallback = PlatFormFeignClientFallback.class)
public interface PlatformFeignClient {

    @GetMapping("/choerodon/v1/lov/meanings")
    ResponseEntity<Map<String, String>> getMeanings(@RequestParam("lovCode") String lovCode);
}
