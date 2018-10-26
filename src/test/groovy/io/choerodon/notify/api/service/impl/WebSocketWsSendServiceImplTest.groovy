package io.choerodon.notify.api.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.notify.api.service.WebSocketSendService
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import io.choerodon.notify.websocket.send.MessageSender
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class WebSocketWsSendServiceImplTest extends Specification {
    private TemplateRender templateRender = Mock(TemplateRender)
    private TemplateMapper templateMapper = Mock(TemplateMapper)
    private SiteMsgRecordMapper siteMsgRecordMapper = Mock(SiteMsgRecordMapper)
    private MessageSender messageSender = Mock(MessageSender)
    private SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private WebSocketSendService webSocketSendService =
            new WebSocketWsSendServiceImpl(templateRender,
                    templateMapper, messageSender,
                    siteMsgRecordMapper, sendSettingMapper)

    def "Send"() {
        given: "构造参数"
        String code = "code"
        Map<String, Object> params = new HashMap<>()
        Set<Long> ids = new HashSet<>()
        SendSetting sendSetting = new SendSetting()
        sendSetting.setPmTemplateId(1L)
        Long sendBy = 1L

        when: "调用方法"
        webSocketSendService.send(code, params, ids, sendBy)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_)

        when: "调用方法"
        webSocketSendService.send(code, params, ids, sendBy)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> { new SendSetting() }

        when: "调用方法"
        webSocketSendService.send(code, params, ids, sendBy)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> sendSetting
        1 * templateMapper.selectByPrimaryKey(_)
        def exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.notExist")

        when: "调用方法"
        webSocketSendService.send(code, params, ids, sendBy)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> sendSetting
        1 * templateMapper.selectByPrimaryKey(_) >> new Template()
        exception = thrown(CommonException)
        exception.getCode().equals("error.pmTemplate.contentNull")
    }
}
