package io.choerodon.notify.infra.feign;

import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.api.vo.NotifyEventGroupVO;
import io.choerodon.notify.infra.feign.fallback.AsgardFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/11 15:10
 */
@FeignClient(value = "devops-service", fallback = AsgardFeignClientFallback.class)
public interface DevopsFeginClient {
    @GetMapping(value = "/v1/projects/{project_id}/envs/list_by_active")
    ResponseEntity<List<NotifyEventGroupVO>> listByActive(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(value = "active") Boolean active);
    @GetMapping("v1/projects/{project_id}/notification/transfer/data")
    ResponseEntity<List<DevopsNotificationVO>> transferData(@PathVariable("project_id") Long projectId);
}
