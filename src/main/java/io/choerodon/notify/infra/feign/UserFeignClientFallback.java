package io.choerodon.notify.infra.feign;

import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author dengyouquan
 **/
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<Long[]> getUserIds() {
        throw new CommonException("error.iam.getUserId");
    }
}
