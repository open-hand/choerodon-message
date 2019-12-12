package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.feign.BaseFeignClient;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/11 19:04
 */
public class BaseFeignClientFallback implements BaseFeignClient {
    @Override
    public ResponseEntity<List<UserDTO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        return null;
    }
}
