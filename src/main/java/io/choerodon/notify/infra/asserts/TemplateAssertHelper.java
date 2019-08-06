package io.choerodon.notify.infra.asserts;

import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.stereotype.Component;

/**
 * @author superlee
 * @since 2019-05-17
 */
@Component
public class TemplateAssertHelper {

    private TemplateMapper templateMapper;

    public TemplateAssertHelper(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    public Template templateNotExisted(Long id) {
        return templateNotExisted(id, "error.template.not.exist");
    }

    public Template templateNotExisted(Long id, String message) {
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new FeignException(message);
        }
        return template;
    }
}
