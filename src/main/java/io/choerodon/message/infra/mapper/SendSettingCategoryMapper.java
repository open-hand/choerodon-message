package io.choerodon.message.infra.mapper;

import io.choerodon.message.infra.dto.SendSettingCategoryDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface SendSettingCategoryMapper extends BaseMapper<SendSettingCategoryDTO> {

    /**
     * 根据类别Code查询发送设置类别
     *
     * @param codes
     * @return
     */
    Set<SendSettingCategoryDTO> selectByCodeSet(@Param("codes") Set<String> codes);
}
