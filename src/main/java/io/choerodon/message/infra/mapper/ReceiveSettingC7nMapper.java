package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.ReceiveSettingDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author dengyouquan
 **/
public interface ReceiveSettingC7nMapper extends BaseMapper<ReceiveSettingDTO> {
    void deleteByUserIdAndSourceTypeAndSourceId(@Param("userId") Long userId,
                                                @Param("sourceType") String sourceType,
                                                @Param("sourceId") Long sourceId);

    /**
     * 查询用户接收设置
     *
     * @param projectId
     * @param tempServerId 消息发送设置主表id
     * @param userIds
     * @param type      消息发送类型 email/web
     * @return
     */
    List<Long> selectByTemplateServerId(@Param("projectId") Long projectId,
                                        @Param("tempServerId") Long tempServerId,
                                        @Param("userIds") List<Long> userIds,
                                        @Param("type") String type);
}
