package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppAdaptorOAuth2AuthticationHandler;
import com.github.shawven.security.app.authentication.*;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.oauth2.OAuth2AuthenticationSuccessHandlerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 适用于APP端，全部json返回，适配手机验证码、社交登录
 *
 * @author Shoven
 * @date 2019-08-20
 */
@Configuration
@AutoConfigureAfter({ConnectSupportConfiguration.class, OAuth2SupportConfiguration.class})
public class AppAutoConfiguration {

    private OAuth2AuthenticationSuccessHandlerAdaptor authenticationSuccessHandlerAdaptor;

    private AppLoginSuccessHandler loginSuccessHandler;

    private AppLoginFailureHandler loginFailureHandler;

    public AppAutoConfiguration(OAuth2AuthenticationSuccessHandlerAdaptor authenticationSuccessHandlerAdaptor,
                                @Autowired(required = false)
                                AppLoginSuccessHandler loginSuccessHandler,
                                @Autowired(required = false)
                                AppLoginFailureHandler loginFailureHandler) {
        this.authenticationSuccessHandlerAdaptor = authenticationSuccessHandlerAdaptor;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    /**
     * 验证成功处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return authenticationSuccessHandlerAdaptor.adapt(
                new AppAdaptorOAuth2AuthticationHandler(loginSuccessHandler, loginFailureHandler));
    }

    /**
     * 验证失败处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AppAuthenticationFailureHandler(loginFailureHandler);
    }

    /**
     * 成功退出处理器
     *
     * @return
     */
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

    /**
     * 验证入口点
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AppAuthenticationExceptionEntryPoint();
    }

}
