package io.choerodon.notify.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.MessageDetailDTO;
import io.choerodon.notify.infra.feign.AgileFeignClient;

/**
 * @author scp
 **/
@Component
public class AgileFeignClientFallback implements AgileFeignClient {
    @Override
    public ResponseEntity<List<MessageDetailDTO>> migrateMessageDetail() {
        throw new CommonException("error.get.agile.message.detail");
    }
}
