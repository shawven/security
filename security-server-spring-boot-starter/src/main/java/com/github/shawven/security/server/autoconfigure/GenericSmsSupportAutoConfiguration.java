package com.github.shawven.security.server.autoconfigure;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.server.verification.DefaultPhoneUserDetailsService;
import com.github.shawven.security.server.verification.PhoneUserDetailsService;
import com.github.shawven.security.server.verification.SmsProperties;
import com.github.shawven.security.server.verification.SmsSecurityConfigurer;
import com.github.shawven.security.verification.VerificationFilterPostProcessor;
import com.github.shawven.security.verification.VerificationType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 发送手机短信登录验证配置
 *
 * @author Shoven
 * @date 2019-11-17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsProperties.class)
class GenericSmsSupportAutoConfiguration {

    private SmsProperties smsProperties;

    public GenericSmsSupportAutoConfiguration(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

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
    @ConditionalOnMissingBean(name = "smsSecurityConfigurer")
    public HttpSecurityConfigurer smsSecurityConfigurer(
            PhoneUserDetailsService phoneUserDetailsService,
            ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
            AuthenticationFailureHandler authenticationFailureHandler) {
        return new SmsSecurityConfigurer(
                smsProperties.getLoginProcessingUrl(),
                phoneUserDetailsService,
                authenticationSuccessHandlerProvider,
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
            filter.getUrlMap().put(smsProperties.getLoginProcessingUrl(), VerificationType.SMS);
        };
    }

}
