package io.choerodon.message.app.service;

import java.util.List;

import io.choerodon.message.infra.dto.TargetUserDTO;

/**
 * 〈功能简述〉
 * 〈消息通知用户Service〉
 *
 * @author wanghao
 * @Date 2019/12/12 17:50
 */
public interface MessageSettingTargetUserC7nService {

    void save(TargetUserDTO targetUserDTO);

    void deleteBySettingId(Long id);

    List<TargetUserDTO> getBySettingId(Long id);
}
