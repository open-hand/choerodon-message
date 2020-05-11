package io.choerodon.message.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.DevopsNotificationTransferDataVO;
import io.choerodon.message.api.vo.NotifyEventGroupVO;
import io.choerodon.message.infra.feign.DevopsFeignClient;
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
public class DevopsFeignClientFallback implements DevopsFeignClient {

    @Override
    public ResponseEntity<List<NotifyEventGroupVO>> listByActive(Long projectId, Boolean active) {
        throw new CommonException("error.query.env");
    }

    @Override
    public ResponseEntity<List<DevopsNotificationTransferDataVO>> transferData(Long projectId) {
        throw new CommonException("error.transfer.data");
    }
}
