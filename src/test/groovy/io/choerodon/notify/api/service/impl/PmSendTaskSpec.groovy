package io.choerodon.notify.api.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.notify.api.service.SiteMsgRecordService
import io.choerodon.notify.domain.SendSetting
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.feign.UserFeignClient
import io.choerodon.notify.infra.mapper.SendSettingMapper
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
import io.choerodon.notify.infra.mapper.TemplateMapper
import io.choerodon.websocket.send.MessageSender
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class PmSendTaskSpec extends Specification {
    private TemplateMapper templateMapper = Mock(TemplateMapper)
    private TemplateRender templateRender = Mock(TemplateRender)
    private MessageSender messageSender = Mock(MessageSender)
    private SiteMsgRecordMapper siteMsgRecordMapper = Mock(SiteMsgRecordMapper)
    private SendSettingMapper sendSettingMapper = Mock(SendSettingMapper)
    private SiteMsgRecordService siteMsgRecordService = Mock(SiteMsgRecordService)
    private UserFeignClient userFeignClient = Mock(UserFeignClient)
    private PmSendTask pmSendTask = new PmSendTask(templateMapper, templateRender,
            messageSender, userFeignClient, siteMsgRecordMapper,
            siteMsgRecordService, sendSettingMapper)

    def "SendStationLetter"() {
        given: "构造请求参数"
        Map<String, Object> map = new HashMap<>()
        map.put("code", "addFunction")
        map.put("variables", "{'content':'定时任务'}")
        SendSetting sendSetting = new SendSetting()
        sendSetting.setPmTemplateId(1L)
        Template template = new Template()
        template.setPmContent("content")
        template.setPmTitle("title")
        Long[] ids = new Long[1]
        ids[0] = 1L
        ResponseEntity<Long[]> responseEntity = new ResponseEntity<>(ids, HttpStatus.OK)


        when: "调用方法"
        pmSendTask.sendStationLetter(map)

        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> sendSetting
        1 * templateMapper.selectByPrimaryKey(_) >> template
        1 * templateRender.renderTemplate(_, _, _) >> "content"
        1 * userFeignClient.getUserIds() >> responseEntity
        1 * siteMsgRecordMapper.selectCountOfUnRead(_) >> 1


        when: "调用方法"
        pmSendTask.sendStationLetter(map)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_)

        when: "调用方法"
        pmSendTask.sendStationLetter(map)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> { new SendSetting() }

        when: "调用方法"
        pmSendTask.sendStationLetter(map)
        then: "校验结果"
        1 * sendSettingMapper.selectOne(_) >> sendSetting
        1 * templateMapper.selectByPrimaryKey(_)
    }
}
