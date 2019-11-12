package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.oauth2.phone.PhoneConfiguration;
import com.github.shawven.security.oauth2.phone.PhoneFilterProvider;
import com.github.shawven.security.verification.DefaultPhoneUserDetailsService;
import com.github.shawven.security.verification.PhoneUserDetailsService;
import com.github.shawven.security.verification.VerificationFilterProcessor;
import com.github.shawven.security.verification.VerificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@Configuration
@ConditionalOnClass(PhoneUserDetailsService.class)
public class OAuth2PhoneSupportConfiguration {

    @Autowired
    private OAuth2Properties properties;

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFilterProvider phoneFilterProvider(
            @Lazy PhoneUserDetailsService phoneUserDetailsService,
            @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
            @Lazy AuthenticationFailureHandler authenticationFailureHandler) {
        return new PhoneFilterProvider(phoneConfiguration(), phoneUserDetailsService,
                authenticationSuccessHandler, authenticationFailureHandler);
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
    public VerificationFilterProcessor verificationFilterProcessor() {
        PhoneConfiguration cfg = phoneConfiguration();
        return filter -> filter.getUrlMap().put(cfg.getFilterProcessingUrl(), VerificationType.SMS) ;
    }

    private PhoneConfiguration phoneConfiguration() {
        PhoneConfiguration cfg = new PhoneConfiguration();
        cfg.setFilterProcessingUrl(properties.getPhoneProcessingUrl());
        return cfg;
    }
}
