package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.TemplateCreateVO;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.infra.dto.Template;
import org.springframework.data.domain.Pageable;

/**
 * @author Eugen
 */
public interface TemplateService {
    /**
     * 分页获取模版信息
     *
     * @param pageable 分页对象
     * @return 模板列表
     */
    PageInfo<TemplateVO> pagingTemplateByMessageType(Pageable pageable, String businessType, String messageType, String name, Boolean predefined, String params);


    /**
     * 根据id获取模版信息
     *
     * @param id 模版主键
     * @return 模版信息
     */
    TemplateVO getById(Long id);


    /**
     * 删除模版
     *
     * @param id 模版主键
     * @return 删除结果
     */
    Boolean deleteById(Long id);

    /**
     * 将模版设成
     * 所属发送设置对应消息类型的当前模版
     *
     * @param id 模版主键
     * @return 设置结果
     */
    Boolean setToTheCurrent(Long id);

    /**
     * 创建模版
     *
     * @param setToTheCurrent 是否设为当前模版
     * @param createVO        创建信息
     * @return 创建结果
     */
    TemplateCreateVO createTemplate(Boolean setToTheCurrent, TemplateCreateVO createVO);


    /**
     * 更新模版
     *
     * @param updateVO 更新VO
     * @return 更新结果
     */
    TemplateCreateVO updateTemplate(Boolean setToTheCurrent, TemplateCreateVO updateVO);


    /**
     * 查询模版
     *
     * @param template
     * @return 模版详情
     */
    Template getOne(Template template);
}
