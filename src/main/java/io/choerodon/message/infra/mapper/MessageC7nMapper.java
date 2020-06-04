package io.choerodon.message.infra.mapper;

import io.choerodon.message.infra.dto.MessageC7nDTO;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.Message;

import java.util.Date;
import java.util.List;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface MessageC7nMapper {
    List<Message> selectEmailMessage(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    List<MessageC7nDTO> listMessage(@Param("tenantId") Long tenantId,
                                    @Param("serverCode") String serverCode,
                                    @Param("messageTypeCode") String messageTypeCode,
                                    @Param("subject") String subject,
                                    @Param("trxStatusCode") String trxStatusCode,
                                    @Param("startDate") Date startDate,
                                    @Param("endDate") Date endDate,
                                    @Param("receiver") String receiver);
}
