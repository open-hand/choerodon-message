package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.MessageSettingCategoryDTO;

import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.NotifyEventGroupVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageSettingMapper extends Mapper<MessageSettingDTO> {

    List<MessageSettingCategoryDTO> listMessageSettingByCondition(@Param("projectId") Long projectId, @Param("messageSettingDTO") MessageSettingDTO messageSettingDTO);

    MessageSettingDTO queryByCodeWithoutProjectId(@Param("code") String code);

    List<MessageSettingDTO> queryByCodeOrProjectId(@Param("messageSettingDTO") MessageSettingDTO messageSettingDTO);

    /**
     * 根据通知设置类型，查询通知设置分组信息
     * @param notifyType
     * @return
     */
    List<NotifyEventGroupVO> listCategoriesBySettingType(@Param("notifyType") String notifyType);

    /**
     * 查询启用的默认通知配置
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listDefaultAndEnabledSettingByNotifyType(@Param("notifyType") String notifyType);

    /**
     * 根据通知类型查询项目下的通知配置
     * @param projectId
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectId(@Param("projectId") Long projectId, @Param("notifyType") String notifyType);

    /**
     * 查询资源删除验证通知设置
     * @param projectId
     * @param envId
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectIdAndEnvId(@Param("projectId") Long projectId, @Param("envId") Long envId, @Param("notifyType") String notifyType);

    MessageSettingVO getSettingByTypeAndCode(String notifyType, Long projectId, String code);
}
