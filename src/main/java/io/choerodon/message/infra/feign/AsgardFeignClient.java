package io.choerodon.message.infra.feign;

import io.choerodon.message.api.vo.QuartzTask;
import io.choerodon.message.api.vo.ScheduleTaskVO;
import io.choerodon.message.infra.feign.fallback.AsgardFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author dengyouquan
 **/
@FeignClient(value = "asgard-service",
        fallback = AsgardFeignClientFallback.class)
public interface AsgardFeignClient {

    @DeleteMapping("/v1/schedules/tasks/{id}")
    void deleteSiteTaskByTaskId(@PathVariable("id") long id);

    @PostMapping("/v1/schedules/tasks")
    ResponseEntity<QuartzTask> createSiteScheduleTask(@RequestBody ScheduleTaskVO scheduleTaskVO);

    @GetMapping("/v1/schedules/methods/code/{code}")
    ResponseEntity<Long> getMethodIdByCode(@PathVariable("code") String code);
}
