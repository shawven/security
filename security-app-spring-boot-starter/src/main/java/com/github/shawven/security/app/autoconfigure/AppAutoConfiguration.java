package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppAuthorizationConfigureProvider;
import com.github.shawven.security.app.authentication.*;
import com.github.shawven.security.app.authentication.AppOAuth2AccessDeniedHandler;
import com.github.shawven.security.app.authentication.AppOAuth2AuthenticationSuccessHandler;
import com.github.shawven.security.app.config.AppConfiguration;
import com.github.shawven.security.app.config.SessionConfiguration;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.app.connect.AppConnectConfigurerProcessor;
import com.github.shawven.security.app.openid.OpenIdFilterProvider;
import com.github.shawven.security.app.session.AppExpiredSessionStrategy;
import com.github.shawven.security.app.session.AppInvalidSessionStrategy;
import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.connect.ConnectConfigurerProcessor;
import com.github.shawven.security.oauth2.ClientAuthenticationFilter;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
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
@AutoConfigureAfter({AppAutoConfiguration.ConnectSupportConfiguration.class, AppAutoConfiguration.OAuth2SupportConfiguration.class})
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
    public AuthenticationSuccessHandler authenticationSuccessHandler(
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

    @Configuration
    @ConditionalOnClass({ConnectAutoConfiguration.class, RedisTemplate.class})
    public static class ConnectSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ConnectConfigurerProcessor appConnectConfigurerProcessor() {
            return new AppConnectConfigurerProcessor();
        }

        @Bean
        @ConditionalOnMissingBean
        public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
                AuthenticationSuccessHandler authenticationSuccessHandler,
                @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
            return new AppConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                    new AppSocailAuthenticationFailureHandler(loginFailureHandler));
        }

        @Bean
        @ConditionalOnMissingBean
        public AuthenticationFilterProvider openIdFilterProvider() {
            return new OpenIdFilterProvider();
        }

    }

    @Configuration
    @ConditionalOnClass(OAuth2AutoConfiguration.class)
    public static class OAuth2SupportConfiguration {

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

        /**
         * 客户端验证过滤器
         *
         * @param clientDetailsService
         * @param passwordEncoder
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public ClientAuthenticationFilter clientAuthenticationFilter(ClientDetailsService clientDetailsService,
                                                 PasswordEncoder passwordEncoder) {
            return new ClientAuthenticationFilter(clientDetailsService, passwordEncoder);
        }


        /**
         * 验证成功处理器
         *
         * @return
         */
        @Bean
        @Lazy
        @ConditionalOnMissingBean
        public AuthenticationSuccessHandler authenticationSuccessHandler(ClientDetailsService clientDetailsService,
                                                                            PasswordEncoder passwordEncoder,
                                                                            @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler,
                                                                            AuthorizationServerTokenServices services) {
            return new AppOAuth2AuthenticationSuccessHandler(clientDetailsService, passwordEncoder, loginSuccessHandler, services);
        }
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
