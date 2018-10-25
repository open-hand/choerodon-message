package io.choerodon.notify.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.core.exception.ExceptionResponse
import io.choerodon.notify.IntegrationTestConfiguration
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam
import io.choerodon.notify.domain.SiteMsgRecord
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
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
class SiteMsgRecordControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/notices/sitemsgs"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private SiteMsgRecordMapper recordMapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    List<SiteMsgRecord> records = new ArrayList<>()
    @Shared
    def count = 5
    @Shared
    def userId = 1L

    def setup() {
        if (needInit) {
            given: "构造数据"
            needInit = false
            for (int i = 0; i < count; i++) {
                SiteMsgRecord record = new SiteMsgRecord()
                record.setTitle("title" + i)
                record.setContent("content" + i)
                record.setUserId(userId)
                record.setDeleted(false)
                record.setRead(false)
                records.add(record)
            }

            when: "调用方法"
            def num = recordMapper.insertList(records)

            then: "校验插入是否成功"
            num == count
        }
    }

    def cleanup() {
        if (needClean) {
            given: "构造参数"
            needClean = false

            when: "删除数据"
            def num = 0
            for (SiteMsgRecord record : records) {
                num += recordMapper.deleteByPrimaryKey(record)
            }

            then: "校验结果"
            num == count
        }
    }

    def "PagingQuery"() {
        given: "构造请求参数"
        Boolean isRead = null
        def notExistsUserId = 1000L

        when: "调用方法[异常-用户不是当前用户]"
        def entity = restTemplate.getForEntity(BASE_PATH + "?user_id={user_id}&read={read}", ExceptionResponse, notExistsUserId, isRead)
        then: "校验参数"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().getCode().equals("error.siteMsgRecord.notCurrentUser")

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "?user_id={user_id}&read={read}", Page, userId, isRead)
        then: "校验参数"
        entity.getStatusCode().is2xxSuccessful()
        entity.getBody().size() == count

    }

    def "BatchRead"() {
        given: "构造请求参数"
        List<Long> ids = new ArrayList<>()
        ids.add(records.get(0).getId())
        ids.add(records.get(1).getId())
        HttpEntity httpEntity = new HttpEntity<Object>(ids)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/batch_read?user_id={user_id}", HttpMethod.PUT, httpEntity, Void, userId)
        then: "校验参数"
        entity.getStatusCode().is2xxSuccessful()
        SiteMsgRecordQueryParam param = new SiteMsgRecordQueryParam()
        param.setRead(true)
        recordMapper.fulltextSearch(param).size() == 2
    }

    def "BatchDeleted"() {
        given: "构造请求参数"
        needClean = true
        List<Long> ids = new ArrayList<>()
        ids.add(records.get(0).getId())
        ids.add(records.get(1).getId())
        HttpEntity httpEntity = new HttpEntity<Object>(ids)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/batch_delete?user_id={user_id}", HttpMethod.PUT, httpEntity, Void, userId)
        then: "校验参数"
        entity.getStatusCode().is2xxSuccessful()
        SiteMsgRecordQueryParam param = new SiteMsgRecordQueryParam()
        param.setDeleted(true)
        recordMapper.fulltextSearch(param).size() == 2
    }
}
