package com.github.shawven.security.server.autoconfigure;

import com.github.shawven.security.server.GenericOAuth2Endpoint;
import com.github.shawven.security.server.LoginFailureHandler;
import com.github.shawven.security.server.LoginSuccessHandler;
import com.github.shawven.security.server.oauth2.OAuth2AccessDeniedHandler;
import com.github.shawven.security.server.oauth2.OAuth2AuthenticationExceptionEntryPoint;
import com.github.shawven.security.server.oauth2.OAuth2AuthenticationFailureHandler;
import com.github.shawven.security.server.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 适用于APP端，全部json返回，适配手机验证码、社交登录
 *
 * @author Shoven
 * @date 2019-08-20
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(GenericConnectAutoConfiguration.class)
class GenericAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GenericOAuth2Endpoint oauth2Endpoint(TokenEndpoint tokenEndpoint,
                                                AuthorizationServerTokenServices tokenServices,
                                                ObjectProvider<LoginSuccessHandler> loginSuccessHandlerProvider,
                                                ObjectProvider<LoginFailureHandler> loginFailureHandlerProvider) {
        return new GenericOAuth2Endpoint(tokenEndpoint, tokenServices,
                loginSuccessHandlerProvider.getIfAvailable(),
                loginFailureHandlerProvider.getIfAvailable());
    }

    /**
     * 验证成功处理器后处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler(
            ClientDetailsService clientDetailsService,
            AuthorizationServerTokenServices tokenServices,
            PasswordEncoder passwordEncoder,
            ObjectProvider<LoginSuccessHandler> loginSuccessHandlerProvider,
            ObjectProvider<LoginFailureHandler> loginFailureHandlerProvider) {
        return new OAuth2AuthenticationSuccessHandler(clientDetailsService, tokenServices, passwordEncoder,
                loginSuccessHandlerProvider.getIfAvailable(), loginFailureHandlerProvider.getIfAvailable());
    }

    /**
     * 验证失败处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler authenticationFailureHandler(
            ObjectProvider<LoginFailureHandler> loginFailureHandlerProvider) {
        return new OAuth2AuthenticationFailureHandler(loginFailureHandlerProvider.getIfAvailable());
    }

    /**
     * 验证异常入口点
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new OAuth2AuthenticationExceptionEntryPoint();
    }

    /**
     * 访问拒绝处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler accessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }
}
