package io.choerodon.notify.api.controller.v1

import io.choerodon.core.exception.ExceptionResponse
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.dto.ReceiveSettingDTO
import io.choerodon.notify.domain.ReceiveSetting
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class ReceiveSettingControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/receive_setting"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private ReceiveSettingMapper mapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def count = 3
    @Shared
    List<ReceiveSetting> settingList = new ArrayList<>()
    @Shared
    List<ReceiveSettingDTO> settingDTOList = new ArrayList<>()

    def setup() {
        if (needInit) {
            given: "构造请求参数"
            needInit = false
            for (int i = 0; i < count; i++) {
                ReceiveSetting setting = new ReceiveSetting()
                setting.setUserId(1L)
                setting.setSourceId(0L)
                setting.setSourceType("site")
                setting.setDisable(true)
                setting.setSendSettingId(i)
                settingList.add(setting)
            }
            for (int i = 0; i < count; i++) {
                ReceiveSettingDTO setting = new ReceiveSettingDTO()
                setting.setUserId(0L)
                setting.setSourceId(0L)
                setting.setSourceType("site")
                setting.setDisable(true)
                setting.setSendSettingId(i + 1)
                settingDTOList.add(setting)
            }

            when: "插入数据"
            int num = 0
            for (ReceiveSetting receiveSetting: settingList) {
                mapper.insert(receiveSetting)
                num++
            }

            then: "校验参数"
            num == count
        }
    }

    def cleanup() {
        if (needClean) {
            given: ""
            needClean = false

            when: "删除数据"
            int result = 0
            for (ReceiveSetting setting : settingList) {
                result += mapper.deleteByPrimaryKey(setting)
            }

            then: "校验结果"
            result == count
        }
    }

    def "QueryByUserId"() {
        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH, List)

        then: "校验参数"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().size() == count
    }

    def "Update"() {
        given: "调用方法"
        HttpEntity<Object> httpEntity = new HttpEntity<>(settingDTOList)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/all", HttpMethod.PUT, httpEntity, Void)

        then: "校验参数"
        entity.statusCode.is2xxSuccessful()
    }

    def "UpdateByUserId"() {
        given: "调用方法"
        needClean = true
        HttpEntity<Object> httpEntity = new HttpEntity<>(settingDTOList)
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("messageType", "site")
        paramsMap.put("disable", false)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "?message_type={messageType}&disable={disable}", HttpMethod.PUT, httpEntity, Void, paramsMap)
        then: "校验参数"
        entity.statusCode.is2xxSuccessful()

        when: "调用方法"
        paramsMap.put("sourceId", 0L)
        paramsMap.put("sourceType", "site")
        entity = restTemplate.exchange(BASE_PATH + "?message_type={messageType}&disable={disable}&source_id={sourceId}&source_type={sourceType}", HttpMethod.PUT, httpEntity, Void, paramsMap)
        then: "校验参数"
        entity.statusCode.is2xxSuccessful()

        when: "调用方法[异常]"
        paramsMap.put("sourceType", "user")
        entity = restTemplate.exchange(BASE_PATH + "?message_type={messageType}&disable={disable}&source_id={sourceId}&source_type={sourceType}", HttpMethod.PUT, httpEntity, ExceptionResponse, paramsMap)
        then: "校验参数"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.notify.messageType")
    }
}
