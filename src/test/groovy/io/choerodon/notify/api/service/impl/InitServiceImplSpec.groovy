package io.choerodon.notify.api.service.impl

import io.choerodon.notify.infra.dto.Config
import io.choerodon.notify.infra.config.NotifyProperties
import io.choerodon.notify.infra.mapper.ConfigMapper
import org.springframework.boot.autoconfigure.mail.MailProperties
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class InitServiceImplSpec extends Specification {
    private ConfigMapper configMapper = Mock(ConfigMapper)
    private NotifyProperties notifyProperties = new NotifyProperties()
    private MailProperties mailProperties = new MailProperties()
    private InitServiceImpl initService = new InitServiceImpl(configMapper,
            notifyProperties, mailProperties)

    def "InitDb"() {
        given: "构造参数"
        Config config = new Config()
        //config.setEmailAccount("noreply@choerodon.com.cn")
        config.setEmailPassword("password")
        config.setEmailSendName("choerodon")
        config.setEmailProtocol(null)
        config.setEmailHost("smtp.aliyun.com")
        config.setEmailPort(25)
        mailProperties.setUsername("username")
        mailProperties.setProtocol("smtp")
        mailProperties.setHost("localhost")
        mailProperties.setPort(25)
        mailProperties.setPassword("password")

        when: "调用方法"
        initService.initDb()

        then: "校验结果"
        1 * configMapper.selectOne(_) >> config
        1 * configMapper.updateByPrimaryKeySelective(_)
    }
}
