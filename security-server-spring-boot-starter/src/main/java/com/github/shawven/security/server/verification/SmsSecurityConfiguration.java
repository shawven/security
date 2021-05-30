package com.github.shawven.security.server.verification;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.verification.VerificationFilterPostProcessor;
import com.github.shawven.security.verification.VerificationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 发送手机短信登录验证配置
 *
 * @author Shoven
 * @date 2019-11-17
 */
@Configuration(proxyBeanMethods = false)
public class SmsSecurityConfiguration {



}
