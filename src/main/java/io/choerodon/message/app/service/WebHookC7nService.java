package io.choerodon.message.app.service;

import org.hzero.message.domain.entity.WebhookServer;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
public interface WebHookC7nService {
    /**
     * webhook 分页查询
     *
     * @param pageable
     * @param sourceId
     * @param sourceLevel
     * @param messageName
     * @param type
     * @param enableFlag
     * @param params
     * @return
     */
    Page<WebHookVO> pagingWebHook(PageRequest pageable,
                                  Long sourceId,
                                  String sourceLevel,
                                  String messageName,
                                  String type,
                                  Boolean enableFlag,
                                  String params);

    /**
     * WebHook 地址校验重复
     *
     * @param id
     * @param path
     */
    Boolean checkPath(Long id, String path);


    /**
     * 添加WebHook
     *
     * @param sourceId 项目ID/组织id
     * @param createVO webHook创建信息
     * @return
     */
    WebHookVO create(Long sourceId, WebHookVO createVO, String sourceLevel);


    WebHookVO update(Long sourceId, WebHookVO createVO, String sourceLevel);

    void delete(Long sourceId, Long webHookId, String sourceLevel);

    void updateEnabledFlag(Long webHookId, Boolean enableFlag);

    void resendMessage(Long tenantId, Long recordId);

    WebHookVO queryById(Long webHookId);

}
