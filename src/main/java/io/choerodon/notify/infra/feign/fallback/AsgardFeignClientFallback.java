package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.notify.infra.feign.AsgardFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.ScheduleTaskDTO;
import io.choerodon.notify.domain.QuartzTask;

/**
 * @author dengyouquan
 **/
@Component
public class AsgardFeignClientFallback implements AsgardFeignClient {

    @Override
    public void deleteSiteTaskByTaskId(long id) {
        throw new CommonException("error.asgard.quartzTask.delete.site.task", id);
    }


    @Override
    public ResponseEntity<QuartzTask> createSiteScheduleTask(ScheduleTaskDTO dto) {
        throw new CommonException("error.asgard.quartzTask.create.site.task", dto);
    }

    @Override
    public ResponseEntity<Long> getMethodIdByCode(String code) {
        throw new CommonException("error.asgard.quartz.method.query.by.code", code);
    }
}
