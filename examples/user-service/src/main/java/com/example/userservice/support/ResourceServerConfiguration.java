
package com.example.userservice.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * 资源服务器配置
 */
@Configuration(proxyBeanMethods = false)
@EnableResourceServer
public class ResourceServerConfiguration {


}
