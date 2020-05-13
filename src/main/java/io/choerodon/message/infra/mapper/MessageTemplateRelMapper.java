package io.choerodon.message.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.MessageTemplateRelDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/5/9
 * @description
 */
public interface MessageTemplateRelMapper extends BaseMapper<MessageTemplateRelDTO> {

    MessageTemplateRelDTO selectByTemplateId(@Param("templateId") Long templateId);
}