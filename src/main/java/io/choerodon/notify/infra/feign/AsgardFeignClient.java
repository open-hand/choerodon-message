package io.choerodon.notify.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.notify.api.dto.ScheduleTaskDTO;
import io.choerodon.notify.domain.QuartzTask;

/**
 * @author dengyouquan
 **/
@FeignClient(value = "asgard-service",
        fallback = AsgardFeignClientFallback.class)
public interface AsgardFeignClient {

    @DeleteMapping("/v1/schedules/tasks/{id}")
    void deleteSiteTaskByTaskId(@PathVariable("id") long id);

    @PostMapping("/v1/schedules/tasks")
    ResponseEntity<QuartzTask> createSiteScheduleTask(@RequestBody ScheduleTaskDTO dto);

    @GetMapping("/v1/schedules/methods/code/{code}")
    ResponseEntity<Long> getMethodIdByCode(@PathVariable("code") String code);
}
