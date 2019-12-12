package io.choerodon.notify.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.choerodon.notify.api.dto.MessageDetailDTO;
import io.choerodon.notify.infra.feign.fallback.AgileFeignClientFallback;

/**
 * @author scp
 **/
@FeignClient(value = "agile-service",
        fallback = AgileFeignClientFallback.class)
public interface AgileFeignClient {
    @GetMapping(value = "/v1/fix_data/migrate_message")
    ResponseEntity<List<MessageDetailDTO>> migrateMessageDetail();
}
