package io.choerodon.message.infra.feign;

import io.choerodon.message.api.vo.DevopsNotificationTransferDataVO;
import io.choerodon.message.api.vo.NotifyEventGroupVO;
import io.choerodon.message.infra.feign.fallback.DevopsFeignClientFallback;
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
@FeignClient(value = "devops-service", fallback = DevopsFeignClientFallback.class)
public interface DevopsFeignClient {
    @GetMapping(value = "/choerodon/v1/projects/{project_id}/envs/list_by_active")
    ResponseEntity<List<NotifyEventGroupVO>> listByActive(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(value = "active") Boolean active);

    @GetMapping("v1/projects/{project_id}/notification/transfer/data")
    ResponseEntity<List<DevopsNotificationTransferDataVO>> transferData(@PathVariable("project_id") Long projectId);
}
