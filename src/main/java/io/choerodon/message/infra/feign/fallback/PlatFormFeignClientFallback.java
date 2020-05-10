package io.choerodon.message.infra.feign.fallback;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.infra.feign.PlatformFeignClient;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
@Component
public class PlatFormFeignClientFallback implements PlatformFeignClient {
    @Override
    public ResponseEntity<Map<String, String>> getMeanings(String lovCode) {
        throw new CommonException("error.get.lov.values");
    }
}
