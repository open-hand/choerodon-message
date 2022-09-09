package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.api.vo.CustomMessageSettingVO;
import io.choerodon.message.api.vo.MessageSettingVO;
import io.choerodon.message.api.vo.NotifyEventGroupVO;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.mybatis.common.BaseMapper;

public interface MessageSettingC7nMapper extends BaseMapper<MessageSettingDTO> {

    MessageSettingDTO queryByCodeWithoutProjectId(@Param("code") String code);

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
     * @param code
     * @return
     */
    List<CustomMessageSettingVO> listDefaultAndEnabledSettingByNotifyType(@Param("notifyType") String notifyType,
                                                                          @Param("sourceLevel") String sourceLevel,
                                                                          @Param("code") String code);

    /**
     * 根据通知类型查询项目下的通知配置
     *
     * @param projectId
     * @param notifyType
     * @param code
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectId(@Param("sourceId") Long sourceId,
                                                               @Param("sourceLevel") String sourceLevel,
                                                               @Param("notifyType") String notifyType,
                                                               @Param("code") String code);

    /**
     * 查询资源删除验证通知设置
     *
     * @param projectId
     * @param envId
     * @param notifyType
     * @return
     */
    List<CustomMessageSettingVO> listMessageSettingByProjectIdAndEnvId(@Param("sourceId") Long sourceId,
                                                                       @Param("sourceLevel") String sourceLevel,
                                                                       @Param("envId") Long envId, @Param("notifyType") String notifyType);

    /**
     * 根据类型和code，查询项目下的通知配置
     *
     * @param notifyType
     * @param projectId
     * @param code
     * @return
     */
    MessageSettingVO getSettingByTypeAndCode(@Param("notifyType") String notifyType,
                                             @Param("sourceId") Long sourceId,
                                             @Param("sourceLevel") String sourceLevel,
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
                                                      @Param("sourceId") Long sourceId,
                                                      @Param("sourceLevel") String sourceLevel,
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

    MessageSettingDTO selectByParams(@Param("source_id") Long sourceId,
                                     @Param("source_level") String sourceLevel,
                                     @Param("messageCode") String messageCode,
                                     @Param("envId") Long envId,
                                     @Param("eventName") String eventName,
                                     @Param("messageType") String messageType
    );

    List<String> listCategoryCode(@Param("notifyType") String notifyType);

    List<String> selectProjectMessage(@Param("source_level") String sourceLevel);


    List<CustomMessageSettingVO> listDefaultSettingByNotifyType(@Param("notifyType") String notifyType, @Param("source_level") String sourceLevel);

    MessageSettingVO getDefaultProjectHealthSetting(@Param("notifyType") String notifyType, @Param("code") String code);

}
