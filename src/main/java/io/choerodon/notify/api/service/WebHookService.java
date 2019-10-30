package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookDTO;
import org.springframework.data.domain.Pageable;

import java.util.Set;


public interface WebHookService {
    /**
     * 发送WebHook
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     */
    void trySendWebHook(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO);


    /**
     * 分页查询webhook信息
     * @param pageable 分页信息
     * @param projectId 项目ID
     * @param name webhook名称
     * @param type 类别
     * @param enableFlag 是否启用
     * @param params 模糊匹配字段
     * @return
     */
    PageInfo<WebHookDTO> pagingWebHook(Pageable pageable, Long projectId, String name, String type, Boolean enableFlag, String params);

    /**
     * 根据webhook名称检测该webhook是由已经存在
     * @param name webhook名称
     */
    void check(String name);

    /**
     * 添加webhook
     * @param projectId 项目ID
     * @param webHookVO
     * @return
     */
    WebHookDTO createWebHook(Long projectId, WebHookVO webHookVO);

    /**
     * 跟新webhook
     * @param projectId 项目ID
     * @param webHookDTO
     * @return
     */
    WebHookDTO updateWebHook(Long projectId, WebHookDTO webHookDTO);

    /**
     * 删除webhook
     * @param id webhook唯一标识ID
     * @return
     */
    WebHookDTO deleteWebHook(Long id);

    /**
     * 停用webhook
     * @param id webhook唯一标识ID
     * @return
     */
    WebHookDTO disableWebHook(Long id);

    /**
     * 启用webhook
     * @param id webhook唯一标识ID
     * @return
     */
    WebHookDTO enableWebHook(Long id);
}
