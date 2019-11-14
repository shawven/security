
package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthenticationFilterProviderConfigurer;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.oauth2.sms.SmsConfiguration;
import com.github.shawven.security.oauth2.sms.SmsFilterProviderConfigurer;
import com.github.shawven.security.verification.DefaultPhoneUserDetailsService;
import com.github.shawven.security.verification.PhoneUserDetailsService;
import com.github.shawven.security.verification.VerificationFilterProcessor;
import com.github.shawven.security.verification.VerificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 *
 * 认证相关的扩展点配置。配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 *
 * @author Shoven
 * @date 2019-11-08
 */
@Configuration
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2AutoConfiguration {

    /**
     * 基于OAuth2的验证成功处理器适配器 （短信登录和社交登陆转Token）
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler(
            ClientDetailsService clientDetailsService,
            PasswordEncoder passwordEncoder,
            AuthorizationServerTokenServices services,
            @Autowired(required = false)
                    AuthenticationSuccessHandlerPostProcessor authenticationSuccessHandlerPostProcessor) {
        OAuth2AuthenticationSuccessHandler handler = new OAuth2AuthenticationSuccessHandler(clientDetailsService,
                passwordEncoder, services);
        if (authenticationSuccessHandlerPostProcessor != null) {
            authenticationSuccessHandlerPostProcessor.postProcess(handler);
        }
        return handler;
    }

    @Bean
    public AuthorizationConfigureProvider oauth2AuthorizationConfigureProvider() {
	    return new Oauth2AuthorizationConfigureProvider();
    }

    @Configuration
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public static class BaseConfiguration {

        /**
         * 客户端密码处理器
         * @return
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Configuration
    @ConditionalOnClass(PhoneUserDetailsService.class)
    public static class OAuth2PhoneSupportConfiguration {

        @Autowired
        private OAuth2Properties properties;

        @Bean
        public AuthenticationFilterProviderConfigurer phoneFilterProviderConfigurer(
                @Lazy PhoneUserDetailsService phoneUserDetailsService,
                @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
                @Lazy AuthenticationFailureHandler authenticationFailureHandler) {
            return new SmsFilterProviderConfigurer(phoneConfiguration(), phoneUserDetailsService,
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
            SmsConfiguration cfg = phoneConfiguration();
            return filter -> filter.getUrlMap().put(cfg.getFilterProcessingUrl(), VerificationType.SMS) ;
        }

        private SmsConfiguration phoneConfiguration() {
            SmsConfiguration cfg = new SmsConfiguration();
            cfg.setFilterProcessingUrl(properties.getPhoneProcessingUrl());
            return cfg;
        }
    }
}