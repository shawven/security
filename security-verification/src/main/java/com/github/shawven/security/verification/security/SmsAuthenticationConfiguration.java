package com.github.shawven.security.verification.security;

import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import com.github.shawven.security.verification.VerificationFilterPostProcessor;
import com.github.shawven.security.verification.VerificationType;
import com.github.shawven.security.verification.config.SmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@Configuration
@EnableConfigurationProperties(SmsConfiguration.class)
public class SmsAuthenticationConfiguration {

    @Autowired
    private SmsConfiguration configuration;

    /**
     * 默认的手机号用户获取服务
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public PhoneUserDetailsService phoneUserDetailsService() {
        return new DefaultPhoneUserDetailsService();
    }

    /**
     * 发送手机短信登录验证过滤器配置器
     *
     * @param phoneUserDetailsService
     * @param authenticationSuccessHandler
     * @param authenticationFailureHandler
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "smsSecuritySupportConfigurer")
    public HttpSecuritySupportConfigurer smsSecuritySupportConfigurer(
            PhoneUserDetailsService phoneUserDetailsService,
            @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {
        return new SmsSecuritySupportConfigurer(
                configuration.getLoginProcessingUrl(),
                phoneUserDetailsService,
                authenticationSuccessHandler,
                authenticationFailureHandler);
    }

    /**
     * 校验码过滤器后处理器，把短信拦截URL添加到过滤器中
     *
     * @return
     */
    @Bean
    public VerificationFilterPostProcessor verificationFilterProcessor() {
        return filter -> {
            // 把短信登录支持的路径假如拦截名单
            filter.getUrlMap().put(configuration.getLoginProcessingUrl(), VerificationType.SMS);
        };
    }
}
