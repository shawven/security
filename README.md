# Security
#### 只需要简单的配置就能接入OAuth2授权认证流程、短信登录、第三方登录等功能
![spring-securiy](./doc/images/logo.png)
- **基于[Spring Security](https://spring.io/projects/spring-security)开发，更多信息请参考官方文档**
- **提供浏览器和APP环境spring-boot-starter支持，分别以session 和 token认保存会话**
- **支持表单登陆、短信登录、OAuth2授权登录、第三方登录（微信、QQ）**
---

## 自动配置
**spring-boot-starter支持，配置属性即可成功使用**
### security-server-spring-boot-starter
 auth server依赖包，无状态，会话主要存储依赖redis，仅支持json响应
### security-verification-spring-boot-starter 
 短信、验证码模块，单独包/自动配置包
### security-oauth2-autoconfigure
OAuth2自动配置包，
### security-connect-autoconfigure
第三方登录自动配置，[Spring Social](https://projects.spring.io/spring-social/)社交登陆支持，在APP环境下APP拿到token，请求登录接口登录

## 按需加载
**当引入了 app 或者 browser 主包时**
- 加载security-verification-spring-boot-starter 具备短信登录、发送短信、图形验证码功能
- 加载security-oauth2-autoconfigure 具备token登录功能
- 加载security-connect-autoconfigure 具备第三方登录功能
- 只要加载security-oauth2-autoconfigure 则，第三方登录和短信登录无缝兼容OAuth2 Token方式

## License
Security is Open Source software released under the
[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html ).
