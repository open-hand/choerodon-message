package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.TemplateServer;

import io.choerodon.message.api.vo.MessageServiceVO;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
public interface TemplateServerC7nMapper {

    /**
     * The full text retrieval
     *
     * @param messageType
     * @param level
     * @param enabled
     * @param receiveConfigFlag
     * @param params            全局过滤信息（name，description）
     * @return
     */
    List<MessageServiceVO> selectTemplateServer(@Param("messageCode") String messageCode,
                                                @Param("messageName") String messageName,
                                                @Param("messageType") String messageType,
                                                @Param("level") String level,
                                                @Param("enabled") Boolean enabled,
                                                @Param("receiveConfigFlag") Boolean receiveConfigFlag,
                                                @Param("params") String params);

    List<TemplateServer> selectAllTemplateServer();

    List<TemplateServer> selectForWebHook(@Param("level") String level,
                                          @Param("messageType") String messageType,
                                          @Param("agileCategories") List<String> agileCategories,
                                          @Param("name") String name,
                                          @Param("description") String description);

}
