package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.WebHookDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface WebHookMapper extends Mapper<WebHookDTO> {
    /**
     * 获取项目下与配置了某发送设置的WebHook
     *
     * @param projectId
     * @param sendSettingId
     * @return
     */
    Set<WebHookDTO> selectBySendSetting(@Param("project_id") Long projectId,
                                        @Param("send_setting_id") Long sendSettingId);
}
