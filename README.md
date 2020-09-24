# choerodon-message
消息服务

## Introduction
消息管理，平台统一的消息推送入口。此服务是对[hzero-message](https://github.com/open-hand/hzero-message.git)的二开，对消息创建，webhook，接收配置，项目层消息发送等功能都进行了定制化处理。

## Documentation
- 更多详情请参考`hzero-message`[中文文档](http://open.hand-china.com/document-center/doc/application/10027/10158?doc_id=4691)

## Features
- 公告管理：管理系统的公告与通知
- 模板管理：发送消息的内容模板
- 账户配置：邮箱、短信、企业微信、微信公众号、钉钉、webhook配置
- 消息发送配置：模板与账户关联
- 消息接收配置：允许用户指定消息接收方式
- 消息监控：消息发送的记录
- 项目层消息发送配置：自定义项目层消息发送

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/9a5fb3d45bea4e209cd22fab5bc7fb9b/20200713171014.png)


## Dependencies


* 服务依赖

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-message-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## Data initialization

- 创建数据库，本地创建 `hzero_message` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE hzero_message DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON hzero_message.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `hzero_message` 数据库，运行项目根目录下的 `init-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-database.sh
  ```
  
## Changelog

- [更新日志](./CHANGELOG.zh-CN.md)


## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, CHOERODON






