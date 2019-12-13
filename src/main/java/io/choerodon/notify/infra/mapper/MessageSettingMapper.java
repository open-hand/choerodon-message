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
     *
     * @param notifyType
     * @return
     */
    List<NotifyEventGroupVO> listCategoriesBySettingType(@Param("notifyType") String notifyType);

    /**
     * 查询启用的默认通知配置
     *
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listDefaultAndEnabledSettingByNotifyType(@Param("notifyType") String notifyType);

    /**
     * 根据通知类型查询项目下的通知配置
     *
     * @param projectId
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectId(@Param("projectId") Long projectId, @Param("notifyType") String notifyType);

    /**
     * 查询资源删除验证通知设置
     *
     * @param projectId
     * @param envId
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectIdAndEnvId(@Param("projectId") Long projectId, @Param("envId") Long envId, @Param("notifyType") String notifyType);

    /**
     * <<<<<<< 50102f2b1c5f25b2364051febd423125cdf48249
     * 根据类型和code，查询项目下的通知配置
     *
     * @param notifyType
     * @param projectId
     * @param code
     * @return
     */
    MessageSettingVO getSettingByTypeAndCode(@Param("notifyType") String notifyType,
                                             @Param("projectId") Long projectId,
                                             @Param("code") String code);

    /**
     * 查询资源删除通知设置
     *
     * @param notifyType
     * @param projectId
     * @param code
     * @param envId
     * @param eventName
     * @return
     */
    MessageSettingVO getResourceDeleteSettingByOption(@Param("notifyType") String notifyType,
                                                      @Param("projectId") Long projectId,
                                                      @Param("code") String code,
                                                      @Param("envId") Long envId,
                                                      @Param("eventName") String eventName);

    /**
     * 查询默认的资源删除通知设置
     *
     * @param notifyType
     * @param code
     * @param eventName
     * @return
     */
    MessageSettingVO getDefaultResourceDeleteSetting(@Param("notifyType") String notifyType,
                                                     @Param("code") String code,
                                                     @Param("eventName") String eventName);

    /**
     * 查询默认通知配置
     *
     * @param notifyType
     * @param code
     * @return
     */
    MessageSettingVO getDefaultSettingByCode(@Param("notifyType") String notifyType,
                                             @Param("code") String code);

    /**
     * 根据type和envId删除通知设置
     *
     * @param type
     * @param envId
     */
    void deleteByTypeAndEnvId(@Param("type") String type, @Param("envId") Long envId);
}
