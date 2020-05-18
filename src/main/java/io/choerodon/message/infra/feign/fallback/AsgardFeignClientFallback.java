package io.choerodon.message.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.QuartzTask;
import io.choerodon.message.api.vo.ScheduleTaskVO;
import io.choerodon.message.infra.feign.AsgardFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
    public ResponseEntity<QuartzTask> createSiteScheduleTask(ScheduleTaskVO dto) {
        throw new CommonException("error.asgard.quartzTask.create.site.task", dto);
    }

    @Override
    public ResponseEntity<Long> getMethodIdByCode(String code) {
        throw new CommonException("error.asgard.quartz.method.query.by.code", code);
    }
}
