package io.choerodon.notify.api.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.notify.infra.enums.SendingTypeEnum
import io.choerodon.notify.api.service.PmTemplateService
import io.choerodon.notify.infra.dto.Template
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class PmTemplateServiceImplSpec extends Specification {
    private final TemplateMapper templateMapper = Mock(TemplateMapper)
    private final SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private PmTemplateService pmTemplateService =
            new PmTemplateServiceImpl(templateMapper, sendSettingMapper)

    def "Delete"() {
        given: "构造请求参数"
        Template template = new Template()
        template.setMessageType(SendingTypeEnum.EMAIL.value)
        template.setIsPredefined(true)

        when: "调用方法-[异常-模板不存在]"
        pmTemplateService.delete(1L)
        then: "校验结果"
        templateMapper.selectByPrimaryKey(_) >> { template }
        def exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.notExist")

        when: "调用方法-[异常-预定义]"
        template.setMessageType(SendingTypeEnum.PM.value)
        pmTemplateService.delete(1L)
        then: "校验结果"
        templateMapper.selectByPrimaryKey(_) >> { template }
        exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.cannotDeletePredefined")

        when: "调用方法-[异常-删除失败]"
        template.setIsPredefined(false)
        pmTemplateService.delete(1L)
        then: "校验结果"
        templateMapper.selectByPrimaryKey(_) >> { template }
        1 * templateMapper.deleteByPrimaryKey(_) >> 0
        exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.delete")
    }

    def "Check"() {
        when: "调用方法-[异常-模板存在]"
        pmTemplateService.check("code")
        then: "校验结果"
        templateMapper.selectLevelByCode(_, _) >> { "site" }
        def exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.codeSiteExist")

        when: "调用方法-[异常-模板存在]"
        pmTemplateService.check("code")
        then: "校验结果"
        templateMapper.selectLevelByCode(_, _) >> { "organization" }
        exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.codeOrgExist")
    }
}
