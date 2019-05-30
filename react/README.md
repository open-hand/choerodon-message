# @choerodon/notify

通知服务用于提供 Choerodon 平台的消息中心及个人通知的前端交互支持。

基础模块包含1个层级，具体如下：


## 全局层：

* 消息中心
    * 发送设置
    * 邮件模板
    * 站内信模板
    * 邮箱配置
    * 发送记录
    * 系统公告
* 个人中心
    * 消息通知
    * 接收设置
    
   
## 目录结构

`assets` 存放`css` 文件和`images`
`common` 存放通用配置
`components` 存放公共组件
`containers` 存放前端页面
`dashboard` 存放仪表盘
`guide` 存放新手指引
`locale` 存放多语言文件
`stores` 存放前端页面需要的store

## 依赖

* Node environment (6.9.0+)
* Git environment
* [@choerodon/boot](https://github.com/choerodon/choerodon-front-boot)
* [@choerodon/master](https://github.com/choerodon/choerodon-front-master)

## 运行

``` bash
npm install
npm start
```

启动后，打开 http://localhost:9090

## 相关技术文档

* [React](https://reactjs.org)
* [Mobx](https://github.com/mobxjs/mobx)
* [webpack](https://webpack.docschina.org)
* [gulp](https://gulpjs.com)
