package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.vo.NotifyEventGroupVO;
import io.choerodon.notify.infra.feign.DevopsFeginClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/11 15:11
 */
@Component
public class DevopsFeginClientFallback implements DevopsFeginClient {

    @Override
    public ResponseEntity<List<NotifyEventGroupVO>> listByActive(Long projectId, Boolean active) {
        throw new CommonException("error.query.env");
    }
}
