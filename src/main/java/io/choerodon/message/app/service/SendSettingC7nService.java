package io.choerodon.message.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.*;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
    Page<MessageServiceVO> pagingAll(String messageCode, String messageName, Boolean enabled, Boolean receiveConfigFlag, String params, PageRequest pageRequest, String firstCode, String secondCode, String introduce);


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

    SendSettingVO queryByTempServerCode(String tempServerCode);

    SendSettingVO queryByCode(String messageCode);

    /**
     * 校验资源删除验证通知是否启用
     *
     * @return
     */
    Boolean checkResourceDeleteEnabled();


    List<SendSettingDetailTreeVO> queryByLevelAndAllowConfig(String level, int allowConfig);

    WebHookVO.SendSetting getTempServerForWebhook(Long sourceId, String sourceLevel, String name, String description, String type);

    MessageTemplateVO createMessageTemplate(MessageTemplateVO messageTemplateVO);

    /**
     * 获取lov信息
     * @return
     */
    Map<String, Map<String, String>> getMeanings();
}
