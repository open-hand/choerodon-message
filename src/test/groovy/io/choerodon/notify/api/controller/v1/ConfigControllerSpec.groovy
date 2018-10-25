package io.choerodon.notify.api.controller.v1

import io.choerodon.core.exception.ExceptionResponse
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.EmailConfigDTO
import io.choerodon.notify.domain.Config
import io.choerodon.notify.infra.mapper.ConfigMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/configs"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private ConfigMapper configMapper

    @Transactional
    def "CreateEmail"() {
        given: "构造请求参数"
        EmailConfigDTO configDTO = new EmailConfigDTO()
        configDTO.setAccount("noreply@choerodon.com.cn")
        configDTO.setPassword("password")
        configDTO.setSendName("choerodon")
        configDTO.setProtocol(null)
        configDTO.setHost("smtp.aliyun.com")
        configDTO.setPort(465)

        when: "调用方法"

        def entity = restTemplate.postForEntity(BASE_PATH + "/email", configDTO, EmailConfigDTO)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getAccount().equals(configDTO.getAccount())
        entity.getBody().getSendName().equals(configDTO.getSendName())
        !entity.getBody().getSsl()
        entity.getBody().getPassword().equals(configDTO.getPassword())
        entity.getBody().getHost().equals(configDTO.getHost())
        entity.getBody().getPort().equals(configDTO.getPort())

        when: "调用方法[异常]"
        entity = restTemplate.postForEntity(BASE_PATH + "/email", configDTO, ExceptionResponse)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals("error.emailConfig.exist")
    }

    @Transactional
    def "UpdateEmail"() {
        given: "构造请求参数"
        EmailConfigDTO configDTO = new EmailConfigDTO()
        configDTO.setPassword("password")
        configDTO.setSendName("choerodon")
        configDTO.setProtocol(null)
        configDTO.setHost("smtp.aliyun.com")
        configDTO.setPort(null)
        configDTO.setAccount("111")
        Config config = new Config()
        config.setEmailAccount("noreply@choerodon.com.cn")
        configMapper.insert(config)
        HttpEntity httpEntity = new HttpEntity<Object>(configDTO)

        when: "调用方法[异常-账号不是邮箱]"
        def entity = restTemplate.exchange(BASE_PATH + "/email", HttpMethod.PUT, httpEntity, ExceptionResponse)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals("error.emailConfig.accountIllegal")

        when: "调用方法[异常-版本号为空]"
        configDTO.setAccount("noreply@choerodon.com.cn")
        entity = restTemplate.exchange(BASE_PATH + "/email", HttpMethod.PUT, httpEntity, ExceptionResponse)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals("error.emailConfig.objectVersionNumberNull")

        when: "调用方法"
        configDTO.setObjectVersionNumber(1)
        entity = restTemplate.exchange(BASE_PATH + "/email", HttpMethod.PUT, httpEntity, EmailConfigDTO)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getAccount().equals(configDTO.getAccount())
        entity.getBody().getSendName().equals(configDTO.getSendName())
        !entity.getBody().getSsl()
        entity.getBody().getPassword().equals(configDTO.getPassword())
        entity.getBody().getHost().equals(configDTO.getHost())
    }

    @Transactional
    def "SelectEmail"() {
        given: "构造请求参数"
        Config config = new Config()
        config.setEmailAccount("noreply@choerodon.com.cn")
        config.setEmailPassword("password")
        config.setEmailSendName("choerodon")
        config.setEmailProtocol(null)
        config.setEmailHost("smtp.aliyun.com")
        config.setEmailPort(22)
        configMapper.insert(config)

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/email", EmailConfigDTO)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getAccount().equals(config.getEmailAccount())
        entity.getBody().getSendName().equals(config.getEmailSendName())
        !entity.getBody().getSsl()
        entity.getBody().getPassword().equals(config.getEmailPassword())
        entity.getBody().getHost().equals(config.getEmailHost())
    }

    def "TestEmailConnect"() {
        given: "构造请求参数"
        EmailConfigDTO configDTO = new EmailConfigDTO()
        configDTO.setPassword("password")
        configDTO.setAccount("noreply@choerodon.com.cn")
        configDTO.setSendName("choerodon")
        configDTO.setProtocol(null)
        configDTO.setHost("smtp.aliyun.com")
        configDTO.setPort(null)

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH + "/email/test", configDTO, ExceptionResponse)

        then: "校验结果"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals("error.emailConfig.testConnectFailed")
    }
}
