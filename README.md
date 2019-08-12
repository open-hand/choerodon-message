# Notify Service

## 主要功能

`notify-service` 是一种通知服务，用于发送电子邮件、短信或者是站内信。

## 服务配置

1. application.yml

   ```YAML
   spring:
     redis:
       port: 6379
       host: localhost
       database: 2
     http:
       encoding:
         charset: UTF-8
         force: true
         enabled: true
     servlet:
       multipart:
         max-file-size: 30MB
         max-request-size: 30MB
     datasource:
       url: jdbc:mysql://localhost:3306/notify_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
       username: choerodon
       password: 123456
     mail:
       username: choerodon
       password: choerodon
       protocol: smtp
       host: smtp.qiye.aliyun.com
       port: 465
       properties:
         mail.smtp.ssl.enable: true
         mail.send.name: choerodon
       test-connection: false
     freemarker:
       check-template-location: false
   eureka:
     instance:
       preferIpAddress: true
       leaseRenewalIntervalInSeconds: 10
       leaseExpirationDurationInSeconds: 30
     client:
       serviceUrl:
         defaultZone: http://localhost:8000/eureka/
       registryFetchIntervalSeconds: 10
   hystrix:
     command:
       default:
         execution:
           isolation:
             thread:
               timeoutInMilliseconds: 15000
   ribbon:
     ReadTimeout: 5000
     ConnectTimeout: 5000
   iam-service:
     ribbon:
       ReadTimeout: 30000
       ConnectTimeout: 30000
   file-service:
     ribbon:
       ReadTimeout: 15000
       ConnectTimeout: 15000
   mybatis:
     mapperLocations: classpath*:/mapper/*.xml
     configuration: # 数据库下划线转驼峰配置
       mapUnderscoreToCamelCase: true
   db:
     type: mysql
   choerodon:
     schedule:
       consumer:
         enabled: true # 启用任务调度消费端
         poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
         core-thread-num: 1
         max-thread-num: 2
     notify:
       init-spring-email-config: true # 是否根据spring.mail的配置初始化email配置
     ws:
       paths: /choerodon:msg/**
       oauth: false
       heart-beat-interval-ms: 10000
     eureka:
       event:
         max-cache-size: 300 # 存储的最大失败数量
         retry-time: 5 # 自动重试次数
         retry-interval: 3 # 自动重试间隔(秒)
         skip-services: config**, **register-server, **gateway**, zipkin**, hystrix**, oauth**
   logging:
     level:
       javax.mail: info
       io.choerodon.notify.service.impl.PmSendTask: info
   ```

2. bootstrap.yml

   ```yaml
   server:
     port: 18085
   spring:
     application:
       name: notify-service
     cloud:
       config:
         failFast: true
         retry:
           maxAttempts: 6
           multiplier: 1.5
           maxInterval: 2000
         uri: localhost:8010
         enabled: false
     mvc:
       static-path-pattern: /**
     resources:
       static-locations: classpath:/static,classpath:/public,classpath:/resources,classpath:/META-INF/resources,file:/dist
   management:
     endpoint:
       health:
         show-details: ALWAYS
     server:
       port: 18086
     health:
       mail:
         enabled: false
     endpoints:
       web:
         exposure:
           include: '*'
   ```

## 环境需求

- mysql 5.6+
- redis 3.0+
- 该项目是一个 Eureka Client 项目启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤

- 运行 `eureka-server`，[代码在这里](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。

- 拉取当前项目到本地

  ```xml
  git clone https://code.choerodon.com.cn/choerodon-framework/notify-service.git
  ```

- 初始化数据库。如果本地已经创建 `choerodon` 用户则不需要执行创建用户命令，直接执行创建 `notify-service` 数据库，代码如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE notify-service DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON notify-service.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `notify-service` 数据库表，运行项目根目录下的 `init-mysql-database.sh`，该脚本默认初始化的数据库地址为 `localhost`，若有变更则需要修改脚本文件

  ```sh
  sh init-mysql-database.sh
  ```

- 启动项目，项目根目录下运行 `mvn spring-boot:run` 命令，或者在本地集成环境中运行 `Spring Boot` 启动类 `src/main/java/io/choerodon/notify/NotifyApplication.java`

## 更新日志

- [更新日志](./CHANGELOG.zh-CN.md)

## 如何参与

欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。

