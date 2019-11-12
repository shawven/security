package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.authentication.AppLoginSuccessHandler;
import com.github.shawven.security.app.authentication.AppOAuth2AccessDeniedHandler;
import com.github.shawven.security.app.authentication.AppOAuth2AuthenticationExceptionEntryPoint;
import com.github.shawven.security.app.authentication.AppOAuth2AuthenticationSuccessHandler;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Configuration
@ConditionalOnClass(OAuth2AutoConfiguration.class)
public class OAuth2SupportConfiguration {

    private ClientDetailsService clientDetailsService;

    private PasswordEncoder passwordEncoder;

    private AuthorizationServerTokenServices services;

    public OAuth2SupportConfiguration(ClientDetailsService clientDetailsService,
                                      PasswordEncoder passwordEncoder,
                                      AuthorizationServerTokenServices services) {
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.services = services;
    }

    /**
     * 验证异常入口点
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AppOAuth2AuthenticationExceptionEntryPoint();
    }

    /**
     * 访问拒绝处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AppOAuth2AccessDeniedHandler();
    }
}
