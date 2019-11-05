package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.WebhookRecordVO;
import io.choerodon.notify.infra.dto.WebhookRecordDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/11/4
 */
public interface WebhookRecordMapper extends Mapper<WebhookRecordDTO> {
    List<WebhookRecordVO> fulltextSearch(@Param("webhookRecordVO") WebhookRecordVO webhookRecordVO, @Param("params") String params, @Param("ids") List<Long> ids);
}
