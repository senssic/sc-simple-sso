# sc-simple-sso
## 简述
   简单的SSO 单点登录应用,可以通过代码了解SSO实现理解SSO流程

## 组织结构

```lua
sc-simple-sso
├── sc-simple-sso-client -- 客户端demo
├── sc-simple-sso-server -- SSO认证服务端，可以集成用户登录应用验证等
├── sc-simple-sso-starter -- springboot的starter,拦截验证跳转，方便集成
```
## 使用说明
1.客户端使用
引用封装好的springboot starter,未上传到中央仓库,可以把工程下载下来自己编译
```xml
    <dependency>
       <artifactId>sc-simple-sso-starter</artifactId>
       <groupId>sc-senssic</groupId>
       <version>1.0-SNAPSHOT</version>
    </dependency>
```
客户端springboot 的application文件配置
```yaml
server:
  port: 8082
spring:
  redis:
    host: 10.20.38.64
    password: ''
    port: 6379

sc:
  sso:
    # 应用唯一编号
    app-id: client1
    # 应用认证码
    app-secret: 123456
    # SSO服务地址
    serverUrl: http://10.20.38.64:8081
    # 是否启用SSO，默认不启用
    enabled: true
```
2.服务端使用
服务端springboot 的application文件配置
```yaml
server:
  port: 8081
spring:
  redis:
    host: 10.20.38.64
    password: ''
    port: 6379

sc:
  sso:
    app-id: service1
    app-secret: 123456
    serverUrl: http://10.20.38.64:8081
    # 过期时间(TicketGrantingTicket)TGT-7200s (refreshToken)RT-7200s (accessToken)AT-7200/2s
    timeout: 7200
    enabled: true
    # 白名单
    white-url-list:
      - /login
      - /logout
      - /sso/*
      - /favicon.ico
    # 预置的可以被认证过的应用信息,可以跟进自己业务扩展为管理应用
    appInfoList:
      - name: service1
        appId: service1
        appSecret: 123456
      - name: client1
        appId: client1
        appSecret: 123456
```


## 单点登录流程


## 单点退出流程


## 呜谢

封装改自smart-sso.