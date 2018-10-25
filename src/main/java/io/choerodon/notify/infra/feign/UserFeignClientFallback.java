package io.choerodon.notify.infra.feign;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dengyouquan
 **/
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<Long[]> getUserIds() {
        throw new CommonException("error.iam.getUserId");
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByIds(Long[] ids) {
        throw new CommonException("error.iam.listUsersByIds");
    }
}
