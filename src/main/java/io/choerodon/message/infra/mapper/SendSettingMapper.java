package io.choerodon.message.infra.mapper;

import io.choerodon.message.infra.dto.MessageServiceVO;
import io.choerodon.message.infra.dto.SendSettingDTO;
import io.choerodon.message.infra.dto.SendSettingDetailDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SendSettingMapper extends BaseMapper<SendSettingDTO> {
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

    List<SendSettingDTO> pageSendSettingByCondition(@Param("sendSettingDTO") SendSettingDTO sendSettingDTO,
                                                    @Param("type") String type);
}
