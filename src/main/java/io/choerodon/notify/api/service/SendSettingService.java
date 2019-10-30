package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;

import java.util.*;

import io.choerodon.notify.infra.dto.SendSettingDTO;
import org.springframework.data.domain.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.swagger.notify.*;

public interface SendSettingService {

    Set<BusinessTypeDTO> listNames(String level);

    Set<BusinessTypeDTO> listNames();

    PageInfo<SendSettingListDTO> page(String level, String name, String code,
                                      String description, String params, int page, int size);

    PageInfo<SendSettingListDTO> page(String name, String code,
                                      String description, String params, int page, int size);

    SendSettingDTO update(SendSettingUpdateDTO updateDTO);

    SendSettingDetailDTO query(Long id);

    void createByScan(Set<NotifyBusinessTypeScanData> businessTypes);

    List<SendSettingDetailDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig);

    void delete(Long id);

    /**
     * 消息服务页
     * 分页过滤接口
     *
     * @param messageType
     * @param introduce
     * @param level
     * @param enabled
     * @param allowConfig
     * @param params
     * @param pageable    分页信息
     * @return 分页结果
     */
    PageInfo<MessageServiceVO> pagingAll(String messageType, String introduce, String level, Boolean enabled, Boolean allowConfig, String params, Pageable pageable);

    /**
     * 根据id启用消息服务（对应表；notify_send_setting）
     *
     * @param id 记录主键
     * @return 启用的消息服务信息
     */
    MessageServiceVO enabled(Long id);

    /**
     * 根据id停用消息服务（对应表；notify_send_setting）
     *
     * @param id 记录主键
     * @return 停用的消息服务信息
     */
    MessageServiceVO disabled(Long id);


    /**
     * 根据id
     * 允许配置接受设置
     * 对应表；notify_send_setting
     *
     * @param id 记录主键
     * @return 消息服务信息
     */
    MessageServiceVO allowConfiguration(Long id);

    /**
     * 根据id
     * 禁止配置接受设置
     * 对应表；notify_send_setting
     *
     * @param id 记录主键
     * @return 消息服务信息
     */
    MessageServiceVO forbiddenConfiguration(Long id);


    /**
     * 获取邮件内容的发送设置信息
     *
     * @param id 发送设置主键
     * @return 邮件内容的发送设置信息
     */
    EmailSendSettingVO getEmailSendSetting(Long id);


    /**
     * 修改邮件内容的发送设置信息
     *
     * @param updateVO 更新信息
     * @return 更新结果
     */
    EmailSendSettingVO updateEmailSendSetting(EmailSendSettingVO updateVO);


    /**
     * 获取站内信内容的发送设置信息
     *
     * @param id 发送设置主键
     * @return 站内信内容的发送设置信息
     */
    PmSendSettingVO getPmSendSetting(Long id);


    /**
     * 修改站内信内容的发送设置信息
     *
     * @param updateVO 更新信息
     * @return 更新结果
     */
    PmSendSettingVO updatePmSendSetting(PmSendSettingVO updateVO);

    /**
     * 查询项目层下的所有的SendSetting
     */
    List<SendSettingDTO> selectSendSetting();
}
