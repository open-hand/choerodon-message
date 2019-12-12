package io.choerodon.notify.infra.feign;

import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.infra.config.FeignConfig;
import io.choerodon.notify.infra.feign.fallback.FileFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/11
 */
@FeignClient(value = "devops-service",
        configuration = FeignConfig.class,
        fallback = FileFeignClientFallback.class)
public interface DevopsFeignClient {


    @GetMapping("v1/projects/{project_id}/notification/transfer/data")
    ResponseEntity<List<DevopsNotificationVO>> transferData(@PathVariable("project_id") Long projectId);
}
