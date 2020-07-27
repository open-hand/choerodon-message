## 使用说明
### 概述

支持短信、邮箱、站内消息发送，并能够灵活管理消息模板和对接云平台支持的微服务


## 服务配置 

## 服务配置

- `application.yml`

  ```yaml
    spring:
      application:
        name: hzero-message
      datasource:
        url: ${SPRING_DATASOURCE_URL:jdbc:mysql://db.hzero.org:3306/hzero_message?useUnicode=true&characterEncoding=utf-8&useSSL=false}
        username: ${SPRING_DATASOURCE_USERNAME:root}
        password: ${SPRING_DATASOURCE_PASSWORD:root}
        hikari:
          # 连接池最小空闲连接数
          minimum-idle: ${SPRING_DATASOURCE_MINIMUM_IDLE:20}
          # 连接池允许的最大连接数
          maximum-pool-size: ${SPRING_DATASOURCE_MAXIMUM_POOL_SIZE:200}
          # 等待连接池分配连接的最大时长（毫秒）
          connection-timeout: ${SPRING_DATASOURCE_CONNECTION_TIMEOUT:30000}
      redis:
        host: ${SPRING_REDIS_HOST:redis.hzero.org}
        port: ${SPRING_REDIS_PORT:6379}
        database: ${SPRING_REDIS_DATABASE:1}
        jedis:
          pool:
            # 资源池中最大连接数
            # 默认8，-1表示无限制；可根据服务并发redis情况及服务端的支持上限调整
            max-active: ${SPRING_REDIS_POOL_MAX_ACTIVE:50}
            # 资源池运行最大空闲的连接数
            # 默认8，-1表示无限制；可根据服务并发redis情况及服务端的支持上限调整，一般建议和max-active保持一致，避免资源伸缩带来的开销
            max-idle: ${SPRING_REDIS_POOL_MAX_IDLE:50}
            # 当资源池连接用尽后，调用者的最大等待时间(单位为毫秒)
            # 默认 -1 表示永不超时，设置5秒
            max-wait: ${SPRING_REDIS_POOL_MAX_WAIT:5000}
    
    feign:
      hystrix:
        enabled: true
    
    hystrix:
      threadpool:
        default:
          # 执行命令线程池的核心线程数，也是命令执行的最大并发量
          # 默认10
          coreSize: 1000
          # 最大执行线程数
          maximumSize: 1000
      command:
        default:
          execution:
            isolation:
              thread:
                # HystrixCommand 执行的超时时间，超时后进入降级处理逻辑。一个接口，理论的最佳响应速度应该在200ms以内，或者慢点的接口就几百毫秒。
                # 默认 1000 毫秒，最高设置 2000足矣。如果超时，首先看能不能优化接口相关业务、SQL查询等，不要盲目加大超时时间，否则会导致线程堆积过多，hystrix 线程池卡死，最终服务不可用。
                timeoutInMilliseconds: ${HYSTRIX_COMMAND_TIMEOUT_IN_MILLISECONDS:40000}
      devops-deploy-service:
        execution:
          isolation:
            thread:
              timeoutInMilliseconds: 5000
    
    ribbon:
      # 客户端读取超时时间，超时时间要小于Hystrix的超时时间，否则重试机制就无意义了
      ReadTimeout: ${RIBBON_READ_TIMEOUT:30000}
      # 客户端连接超时时间
      ConnectTimeout: ${RIBBON_CONNECT_TIMEOUT:3000}
      # 访问实例失败(超时)，允许自动重试，设置重试次数，失败后会更换实例访问，请一定确保接口的幂等性，否则重试可能导致数据异常。
      OkToRetryOnAllOperations: true
      MaxAutoRetries: 1
      MaxAutoRetriesNextServer: 1
    
    mybatis:
      mapperLocations: classpath*:/mapper/*.xml
      configuration:
        mapUnderscoreToCamelCase: true
    mapper:
      not-empty: true
    
    hzero:
      websocket:
        # 用于连接websocket的路径
        websocket: /websocket
        # 与当前服务的redis数据库一致
        redisDb: ${SPRING_REDIS_DATABASE:1}
        secretKey: hzero
        # 获取用户信息的接口
        oauthUrl: http://hzero-oauth/oauth/api/user
      lov:
        sql:
          enabled: true
        value:
          enabled: true
      message:
        message-redis-database: ${SPRING_REDIS_DATABASE:1}
        sms:
          fake-action: ${HZERO_SMS_FAKE:false}
      cache-value:
        enable: true
      resource:
        # 匹配的资源才会解析JwtToken便于得到UserDetails
        pattern: ${HZERO_RESOURCE_PATTERN:/v1/*,/hzero/*,/hmsg/v1/*,/hmsg/hzero/*,/choerodon/v1/*}
    
    logging:
      level:
        org.apache.ibatis: ${LOG_LEVEL:info}
        io.choerodon: ${LOG_LEVEL:info}
        org.hzero.boot.message.feign: ${LOG_LEVEL:info}
        org.hzero.message.infra.mapper: ${LOG_LEVEL:info}

  ```
- `bootstrap.yml`

  ```yaml
  server:
    port: 8120
  management:
    server:
      port: 8121
    endpoints:
      web:
        exposure:
          include: '*'
  
  spring:
    profiles:
      active: ${SPRING_PROFILES_ACTIVE:default}
    cloud:
      config:
        fail-fast: false
        # 是否启用配置中心
        enabled: ${SPRING_CLOUD_CONFIG_ENABLED:false}
        # 配置中心地址
        uri: ${SPRING_CLOUD_CONFIG_URI:http://dev.hzero.org:8010}
        retry:
          # 最大重试次数
          maxAttempts: 6
          multiplier: 1.1
          # 重试间隔时间
          maxInterval: 2000
        # 标签
        label: ${SPRING_CLOUD_CONFIG_LABEL:}
  
  eureka:
    instance:
      # 以IP注册到注册中心
      preferIpAddress: ${EUREKA_INSTANCE_PREFER_IP_ADDRESS:true}
      leaseRenewalIntervalInSeconds: 10
      leaseExpirationDurationInSeconds: 30
      # 服务的一些元数据信息
      metadata-map:
        VERSION: 1.3.0.RELEASE
    client:
      serviceUrl:
        # 注册中心地址
        defaultZone: ${EUREKA_DEFAULT_ZONE:http://dev.hzero.org:8000/eureka}
      registryFetchIntervalSeconds: 10
      disable-delta: true
  


  ```

## 环境需求

- mysql 5.6+
- redis 3.0+
- 该项目是一个 Eureka Client 项目，启动后需要注册到 `hzero-register`

## 安装和启动步骤

- 运行 `hzero-register`

- 本地启动 redis-server

- 启动项目，项目根目录下执行如下命令：

  ```sh
   mvn spring-boot:run
  ```

## 更新日志

- [更新日志](./CHANGELOG.zh-CN.md)

## 如何参与

- 欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。






