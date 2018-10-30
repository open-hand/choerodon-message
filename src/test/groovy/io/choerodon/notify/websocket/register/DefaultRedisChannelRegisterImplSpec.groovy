package io.choerodon.notify.websocket.register

import io.choerodon.notify.websocket.ChoerodonWebSocketProperties
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification

import java.util.concurrent.ScheduledExecutorService

/**
 * @author dengyouquan
 * */
class DefaultRedisChannelRegisterImplSpec extends Specification {
    private Environment environment = Mock(Environment)
    private StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
    //需要执行线程池中的方法，用Spy
    private ScheduledExecutorService scheduledExecutorService = Spy(ScheduledExecutorService)
    private ChoerodonWebSocketProperties properties
    private DefaultRedisChannelRegisterImpl redisChannelRegister

    def setup(){
         properties = Mock(ChoerodonWebSocketProperties)
        properties.getHeartBeatIntervalMs() >> 1L
        redisChannelRegister =
                new DefaultRedisChannelRegisterImpl(environment,
                        redisTemplate, scheduledExecutorService, properties)
    }

    def "RemoveDeathChannel"() {
        given: "构造请求参数"
        SetOperations<String, Object> setOperations =
                Mock(SetOperations)

        when: "调用方法"
        redisChannelRegister.removeDeathChannel("channel")
        then: "校验结果"
        1 * redisTemplate.opsForSet() >> { setOperations }
        1 * setOperations.remove(_, _)
        2 * redisTemplate.delete(_)
    }
}
