# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

# [1.1.0] - 2021-11-1
## 新增
- 新增短信认证模板
- 增加邮件服务不支持高并发，对发送失败邮件进行重试功能

## 修复
- 修复了高并发websocket发送异常

## 优化
- 优化了邮件记录慢查询
- 优化了邮箱发送人校验
- 优化了测试连接超时时间配置

# [0.24.0] - 2020-12-31
## 新增
- 新增应用市场的相关消息

## 修复
- 修改初始化数据脚本
- 修复公告查询
- 调整saga执行顺序
- 修复部分主键加密的问题

## 优化


# [0.23.0] - 2020-10-10
## 新增
- 清理半年前webhook日志
- 新增消息类别，消息模板以及调整部分消息模板
- 升级hzero依赖到1.5

## 修复
- 修复webhook查询
- 修复修改webhook时加签的问题
- 修复企业微信没有发送的问题
- 修复项目层设置拦截
## 优化


# [0.22.0] - 2020-08-01

## 后端
### 新增
- 系统公告与通知
- 模板管理
- 邮箱、短信、企业微信、微信公众号、钉钉账户配置
- 消息发送配置
- 消息接收配置
- 消息监控
### 优化


### 删除

