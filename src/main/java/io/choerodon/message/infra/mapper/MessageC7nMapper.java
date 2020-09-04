package io.choerodon.message.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.Message;

import io.choerodon.message.infra.dto.MessageC7nDTO;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface MessageC7nMapper {
    List<Message> selectEmailMessage(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    List<MessageC7nDTO> listMessage(@Param("status") String status,
                                    @Param("receiveEmail") String receiveEmail,
                                    @Param("templateType") String templateType,
                                    @Param("failedReason") String failedReason,
                                    @Param("params") String params);

    List<MessageC7nDTO> listWebHooks(@Param("status") String status,
                                     @Param("webhookAddress") String webhookAddress,
                                     @Param("templateType") String templateType,
                                     @Param("failedReason") String failedReason,
                                     @Param("params") String params);

    void deleteRecord(@Param("messageType") String messageType,
                      @Param("cleanNum") Integer cleanNum);

}
