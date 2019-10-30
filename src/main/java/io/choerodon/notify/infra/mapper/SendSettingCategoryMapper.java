package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.SendSettingCategoryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface SendSettingCategoryMapper extends Mapper<SendSettingCategoryDTO> {

    /**
     * 根据类别Code查询发送设置类别
     *
     * @param codes
     * @return
     */
    Set<SendSettingCategoryDTO> selectByCodeSet(@Param("codes") Set<String> codes);
}
