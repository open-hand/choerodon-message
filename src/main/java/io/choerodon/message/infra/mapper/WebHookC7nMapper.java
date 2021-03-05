package io.choerodon.message.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.api.vo.WebHookVO;

/**
 * @author scp
 * @date 2020/5/11
 * @description
 */
public interface WebHookC7nMapper {

    /**
     * 查询webhook
     *
     * @param tenantId
     * @param messageName
     * @param type
     * @param enableFlag
     * @param params
     * @return
     */
    List<WebHookVO> pagingWebHook(@Param("tenantId") Long tenantId,
                                  @Param("projectId") Long projectId,
                                  @Param("messageName") String messageName,
                                  @Param("type") String type,
                                  @Param("enableFlag") Boolean enableFlag,
                                  @Param("params") String param,
                                  @Param("messageCode") String messageCode);

    void deleteWebHook(@Param("webhookId") Long webHookId);

    WebHookVO queryById(@Param("webHookId") Long webHookId);


    Set<Long> queryWebHook(@Param("webHookId") Long webHookId);


    Set<Long> listEnabledWebHookProjectIds(@Param("code") String code);
}
