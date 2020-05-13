package io.choerodon.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.MsgServiceTreeVO;
import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.api.vo.MessageServiceVO;
import io.choerodon.message.api.vo.SendSettingDetailTreeVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
public interface SendSettingC7nService {


    /**
     * 消息服务页
     * 分页过滤接口
     *
     * @param messageCode
     * @param messageName
     * @param enabled
     * @param receiveConfigFlag
     * @param params
     * @param pageRequest       分页信息
     * @return 分页结果
     */
    Page<MessageServiceVO> pagingAll(String messageCode, String messageName, Boolean enabled, Boolean receiveConfigFlag, String params, PageRequest pageRequest, String firstCode, String secondCode);


    /**
     * 更新启用/停用状态
     *
     * @param messageCode
     * @param status
     */
    void enableOrDisabled(String messageCode, Boolean status);


    SendSettingVO updateSendSetting(Long id, SendSettingVO sendSettingVO);


    /**
     * 平台层消息 左侧树形结构查询
     *
     * @return
     */
    List<MsgServiceTreeVO> getMsgServiceTree();

    void updateReceiveConfigFlag(Long tempServerId, Boolean receiveConfigFlag);

    SendSettingVO queryByTempServerId(Long tempServerId);

    SendSettingVO queryByCode(String messageCode);

    /**
     * 校验资源删除验证通知是否启用
     * @return
     */
    Boolean checkResourceDeleteEnabled();


    List<SendSettingDetailTreeVO> queryByLevelAndAllowConfig(String level, boolean allowConfig);
}
