package io.choerodon.message.infra.mapper;

import java.util.List;

import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;

/**
 * @author scp
 * @since 2022/11/17
 */
public interface C7nUserMessageMapper {

    /**
     * 查询消息列表
     *
     * @param userMsgParamDTO 查询参数
     * @return 消息列表
     */
    List<UserMessageDTO> selectMessageList(UserMsgParamDTO userMsgParamDTO);
}
