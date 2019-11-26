package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.MessageServiceVO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SendSettingMapper extends Mapper<SendSettingDTO> {
    /**
     * The full text retrieval
     *
     * @param messageType
     * @param introduce
     * @param level
     * @param enabled
     * @param allowConfig
     * @param params      全局过滤信息（name，description）
     * @return
     */
    List<MessageServiceVO> doFTR(@Param("messageType") String messageType,
                                 @Param("introduce") String introduce,
                                 @Param("level") String level,
                                 @Param("categoryCode") String categoryCode,
                                 @Param("enabled") Boolean enabled,
                                 @Param("allowConfig") Boolean allowConfig,
                                 @Param("params") String params);


    List<SendSettingDetailDTO> queryByLevelAndAllowConfig(@Param("level") String level,
                                                          @Param("allowConfig") boolean allowConfig);
}
