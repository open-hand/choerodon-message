package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookDTO;
import org.springframework.data.domain.Pageable;


public interface WebHookService {
    /**
     * 发送WebHook
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     */
    void trySendWebHook(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO);


    /**
     * 分页查询WebHook信息
     *
     * @param pageable  分页信息
     * @param projectId 项目ID
     * @param filterDTO 过滤字段
     * @param params    模糊匹配字段
     * @return
     */
    PageInfo<WebHookDTO> pagingWebHook(Pageable pageable, Long projectId, WebHookDTO filterDTO, String params);

    /**
     * WebHook名称校验重复
     *
     * @param id
     * @param path
     */
    Boolean checkPath(Long id,
                      String path);

    /**
     * 查询WebHook详情
     *
     * @param projectId 项目ID
     * @return
     */
    WebHookVO getById(Long projectId, Long webHookId);

    /**
     * 添加WebHook
     *
     * @param projectId 项目ID
     * @param createVO  webHook创建信息
     * @return
     */
    WebHookVO create(Long projectId, WebHookVO createVO);

    /**
     * 更新WebHook
     *
     * @param projectId 项目ID
     * @param updateVO  webHook更新信息
     * @return
     */
    WebHookVO update(Long projectId, WebHookVO updateVO);

    /**
     * 删除WebHook
     *
     * @param id WebHook主键
     * @return
     */
    void delete(Long id);

    /**
     * 停用WebHook
     *
     * @param id WebHook主键
     * @return
     */
    WebHookDTO disabled(Long id);

    /**
     * 启用WebHook
     *
     * @param id WebHook主键
     * @return
     */
    WebHookDTO enabled(Long id);
}
