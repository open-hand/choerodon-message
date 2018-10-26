package io.choerodon.notify.api.service.impl

import io.choerodon.notify.api.pojo.RecordStatus
import io.choerodon.notify.api.service.EmailSendService
import io.choerodon.notify.api.service.MessageRecordService
import io.choerodon.notify.domain.Record
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.mapper.RecordMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class MessageRecordServiceImplSpec extends Specification {
    private RecordMapper recordMapper = Mock(RecordMapper)
    private EmailSendService emailSendService = Mock(EmailSendService)
    private TemplateMapper templateMapper = Mock(TemplateMapper)
    private MessageRecordService messageRecordService =
            new MessageRecordServiceImpl(recordMapper,
                    emailSendService, templateMapper)

    def "ManualRetrySendEmail"() {
        given: "构造请求参数"
        def recordId = 1L
        Record record = new Record()
        record.setStatus(RecordStatus.FAILED.getValue())
        record.setMessageType("email")
        record.setBusinessType("addUser")
        record.setVariables("{}")
        Template template = new Template()
        template.setMessageType("email")


        when: "调用方法"
        messageRecordService.manualRetrySendEmail(recordId)

        then: "校验结果"
        1 * recordMapper.selectByPrimaryKey(_) >> record
        1 * templateMapper.selectByPrimaryKey(_) >> template
        1 * emailSendService.createEmailSender()
        1 * emailSendService.sendRecord(_, _)
        1 * recordMapper.selectByPrimaryKey(_)
    }
}
