package com.github.shawven.security.verification.authentication;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.verification.VerificationFilter;
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
 * @author Shoven
 * @date 2019-11-17
 */
@Configuration
@EnableConfigurationProperties(SmsConfiguration.class)
public class SmsAuthenticationConfiguration {

    @Autowired
    private SmsConfiguration configuration;

    @Bean
    public HttpSecurityConfigurer phoneFilterProviderConfigurer(
            PhoneUserDetailsService phoneUserDetailsService,
            @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {
        return new SmsFilterProviderConfigurer(
                configuration.getLoginProcessingUrl(),
                phoneUserDetailsService,
                authenticationSuccessHandler,
                authenticationFailureHandler);
    }

    /**
     * 默认认证器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public PhoneUserDetailsService phoneUserDetailsService() {
        return new DefaultPhoneUserDetailsService();
    }

    @Bean
    public HttpSecurityConfigurer verificationFilterProviderConfigurer(VerificationFilter filter) {
        filter.getUrlMap().put(configuration.getLoginProcessingUrl(), VerificationType.SMS);
        return new VerificationFilterProviderConfigurer(filter);
    }
}
