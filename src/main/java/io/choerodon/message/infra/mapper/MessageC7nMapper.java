package io.choerodon.message.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.Message;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface MessageC7nMapper {
    List<Message> selectEmailMessage(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);
}
