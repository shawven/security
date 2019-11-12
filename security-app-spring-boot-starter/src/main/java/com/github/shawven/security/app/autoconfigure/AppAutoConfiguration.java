package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppAuthorizationConfigureProvider;
import com.github.shawven.security.app.authentication.*;
import com.github.shawven.security.app.config.AppConfiguration;
import com.github.shawven.security.app.config.SessionConfiguration;
import com.github.shawven.security.app.session.AppExpiredSessionStrategy;
import com.github.shawven.security.app.session.AppInvalidSessionStrategy;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @author Shoven
 * @date 2019-08-20
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
@AutoConfigureAfter({ConnectSupportConfiguration.class, OAuth2SupportConfiguration.class})
public class AppAutoConfiguration {

    @Autowired
    private AppProperties properties;

    /**
     * session失效时的处理策略配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public InvalidSessionStrategy invalidSessionStrategy(){
        return new AppInvalidSessionStrategy();
    }

    /**
     * 并发登录导致前一个session失效时的处理策略配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy(){
        return new AppExpiredSessionStrategy();
    }

    /**
     * 基本验证成功处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler (
            @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler) {
        return new AppAuthenticationSuccessHandler(loginSuccessHandler);
    }

    /**
     * 验证失败处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler authenticationFailureHandler(
            @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
        return new AppAuthenticationFailureHandler(loginFailureHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new AppLogoutSuccessHandler();
    }

    /**
     * 基本访问拒绝处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AppAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AppAuthenticationExceptionEntryPoint();
    }


    @Bean
    @Order
    @ConditionalOnMissingBean
    public AuthorizationConfigureProvider appAuthorizationConfigureProvider() {
        return new AppAuthorizationConfigureProvider(appConfiguration());
    }


    @Bean
    public AppConfiguration appConfiguration() {
        AppConfiguration cfg = new AppConfiguration();
        cfg.setSignInProcessingUrl(properties.getSignInProcessingUrl());
        cfg.setSignOutProcessingUrl(properties.getSignOutProcessingUrl());
        cfg.setRememberMeSeconds(properties.getRememberMeSeconds());

        SessionProperties sessionProperties = properties.getSession();
        SessionConfiguration scfg = new SessionConfiguration();
        scfg.setMaximumSessions(sessionProperties.getMaximumSessions());
        scfg.setMaxSessionsPreventsLogin(sessionProperties.isMaxSessionsPreventsLogin());
        cfg.setSession(scfg);
        return cfg;
    }
}
