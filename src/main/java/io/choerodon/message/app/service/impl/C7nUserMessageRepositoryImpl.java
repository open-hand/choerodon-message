package io.choerodon.message.app.service.impl;

import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.infra.mapper.UserMessageMapper;
import org.hzero.message.infra.repository.impl.UserMessageRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.message.infra.mapper.C7nUserMessageMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2022/11/17
 */
@Component
@Primary
public class C7nUserMessageRepositoryImpl extends UserMessageRepositoryImpl {

    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private C7nUserMessageMapper c7nUserMessageMapper;

    public C7nUserMessageRepositoryImpl(UserMessageMapper userMessageMapper) {
        super(userMessageMapper);
    }

    @Override
    public Page<UserMessageDTO> selectMessageList(UserMsgParamDTO userMsgParamDTO, PageRequest pageRequest) {
        Integer readFlag = userMsgParamDTO.getReadFlag();
        if (readFlag != null && readFlag == 0) {
            return PageHelper.doPageAndSort(pageRequest, () -> userMessageMapper.selectNotReadMessageList(userMsgParamDTO));
        }
        // 重写count慢查询
        return PageHelper.doPageAndSort(pageRequest, () -> c7nUserMessageMapper.selectMessageList(userMsgParamDTO));
    }

}
