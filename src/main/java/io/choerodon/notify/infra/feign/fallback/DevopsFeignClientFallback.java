package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.infra.feign.DevopsFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/11
 */
@Component
public class DevopsFeignClientFallback implements DevopsFeignClient {
    @Override
    public ResponseEntity<List<DevopsNotificationVO>> transferData(Long projectId) {
        throw new CommonException("error.transfer.data");
    }
}
