## 使用说明
### 概述

管理服务，基础服务之一，把路由、限流、熔断等功能易用化，集中在管理服务来管控，提供自动化的路由刷新、权限刷新、swagger信息刷新服务，提供界面化的服务、配置、路由、限流、熔断管理功能以及Spring Boot Admin控制台。

## 服务配置 

- `参数配置`
```
# 页面右上角铃铛预览未读消息的数量，默认值5
hzero.message.maxUnreadMessageCount

# 伪装动作：如果为真则不会发送短信到目标用户，默认值false
hzero.message.sms.fakeAction
# 伪装账号
# 如果有值且fakeAction为真，则所有短信都会被拦截发送至该伪装账号
# 如果无值且fakeAction为真，则不会发生发送短信的动作，但是会返回发送成功
hzero.message.sms.fakeAccount
# 伪装账号的国际冠码，默认值 +86
hzero.message.sms.fakeIdd

# 伪装动作：如果为真则不会发送邮件到目标用户，默认值false
hzero.message.sms.fakeAction
# 伪装账号
# 如果有值且fakeAction为真，则所有邮件都会被拦截发送至该伪装账号
# 如果无值且fakeAction为真，则不会发生发送邮件的动作，但是会返回发送成功
hzero.message.sms.fakeAccount

```



