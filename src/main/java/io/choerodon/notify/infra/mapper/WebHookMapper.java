package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.WebHookDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    /**
     * WebHook全表过滤搜索
     *
     * @param projectId 项目主键
     * @param filterDTO 过滤信息
     * @param params    全局过滤参数（名称与地址）
     * @return
     */
    List<WebHookDTO> doFTR(@Param("projectId") Long projectId,
                           @Param("filterDTO") WebHookDTO filterDTO,
                           @Param("params") String params);
}
