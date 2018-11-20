package io.choerodon.notify

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.appinfo.InstanceInfo
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
@Import(LiquibaseConfig)
class IntegrationTestConfiguration {

    final ObjectMapper objectMapper = new ObjectMapper()

    private final detachedMockFactory = new DetachedMockFactory()

    @Value('${choerodon.oauth.jwt.key:choerodon}')
    String key

    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    LiquibaseExecutor liquibaseExecutor

    @Bean
    RedisConnectionFactory connectionFactory() {
        detachedMockFactory.Spy(RedisConnectionFactory)
    }

    @Bean
    StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = Mockito.mock(StringRedisTemplate)
        RedisConnectionFactory connectionFactory = Mockito.mock(RedisConnectionFactory)
        RedisConnection connection = Mockito.mock(RedisConnection)
        SetOperations<String, String> setOperations = Mockito.mock(SetOperations)
        HashOperations<String, Object, Object> hashOperations = Mockito.mock(HashOperations)
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations)
        Set<String> set = new HashSet<>()
        set.add("api-service")
        set.add("iam=service")

        Mockito.when(template.getConnectionFactory()).thenReturn(connectionFactory)
        Mockito.when(connectionFactory.getConnection()).thenReturn(connection)

        Mockito.when(template.opsForSet()).thenReturn(setOperations)
        Mockito.when(template.opsForHash()).thenReturn(hashOperations)
        Mockito.when(template.opsForValue()).thenReturn(valueOperations)
        Mockito.when(setOperations.members(Mockito.any())).thenReturn(set)
        Mockito.when(valueOperations.get(Mockito.any())).thenReturn("1")
        return template
    }

    @Bean
    KafkaTemplate<byte[], byte[]> kafkaTemplate() {
        detachedMockFactory.Mock(KafkaTemplate)
    }

    @PostConstruct
    void init() {
        //通过liquibase初始化h2数据库
        liquibaseExecutor.execute()
        //给TestRestTemplate的请求头部添加JWT
        setTestRestTemplateJWT()
    }

    private void setTestRestTemplateJWT() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('JWT_Token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
    }

    static String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        defaultUserDetails.setUserId(1L)
        defaultUserDetails.setOrganizationId(1L)
        defaultUserDetails.setLanguage('zh_CN')
        defaultUserDetails.setTimeZone('CCT')
        String jwtToken = null
        try {
            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return jwtToken
    }

}