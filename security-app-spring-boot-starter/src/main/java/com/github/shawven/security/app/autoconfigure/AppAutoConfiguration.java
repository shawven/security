package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.*;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFailureHandler;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.app.oauth2.AppOAuth2AccessDeniedHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationExceptionEntryPoint;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationFailureHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationSuccessHandler;
import com.github.shawven.security.app.openid.OpenIdSecuritySupportConfigurer;
import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.connect.RedisSignInUtils;
import com.github.shawven.security.verification.security.EnableSmsAuthentication;
import com.github.shawven.security.verification.security.SmsAuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * 适用于APP端，全部json返回，适配手机验证码、社交登录
 *
 * @author Shoven
 * @date 2019-08-20
 */
@Configuration
public class AppAutoConfiguration {

    /**
     * 验证成功处理器后处理器
     *
     * @return
     */
    @Bean
    @AutoConfigureOrder(2)
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler(
            @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler) {
        return new AppAuthenticationSuccessHandler(loginSuccessHandler);
    }


    @Configuration
    @AutoConfigureOrder(1)
    @ConditionalOnClass({OAuth2Authentication.class})
    public static class OAuth2SupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AppOAuth2Endpoint appOAuth2Endpoint(TokenEndpoint tokenEndpoint,
                                                   AuthorizationServerTokenServices tokenServices,
                                                   @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler,
                                                   @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
            return new AppOAuth2Endpoint(tokenEndpoint, tokenServices, loginSuccessHandler, loginFailureHandler);
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
                @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler,
                @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
            return new AppOAuth2AuthenticationSuccessHandler(clientDetailsService, tokenServices, passwordEncoder,
                    loginSuccessHandler, loginFailureHandler);
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
            return new AppOAuth2AuthenticationFailureHandler(loginFailureHandler);
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

    @Configuration
    @ConditionalOnClass({ConnectAutoConfiguration.class, RedisTemplate.class})
    @AutoConfigureOrder(3)
    public static class ConnectSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AppConnectEndpoint appConnectEndpoint(RedisSignInUtils redisSignInUtils,
                                                     ProviderSignInUtils providerSignInUtils,
                                                     UsersConnectionRepository usersConnectionRepository,
                                                     ConnectionRepository connectionRepository,
                                                     ConnectionFactoryLocator connectionFactoryLocator) {
            return new AppConnectEndpoint(redisSignInUtils, providerSignInUtils,
                    usersConnectionRepository, connectionRepository, connectionFactoryLocator);
        }

        @Bean
        @ConditionalOnMissingBean
        public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
                AuthenticationSuccessHandler authenticationSuccessHandler,
                @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
            return new AppConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                    new AppConnectAuthenticationFailureHandler(loginFailureHandler));
        }
    }

    /**
     *
     * OpenId直接验证支持
     *
     * @return
     */
    @Bean
    @AutoConfigureOrder(3)
    @ConditionalOnMissingBean
    public HttpSecuritySupportConfigurer openIdSecuritySupportConfigurer(
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            SocialUserDetailsService userDetailsService,
            UsersConnectionRepository usersConnectionRepository) {
        return new OpenIdSecuritySupportConfigurer(authenticationSuccessHandler,
                authenticationFailureHandler, userDetailsService, usersConnectionRepository);
    }


    @Configuration
    @ConditionalOnClass(SmsAuthenticationConfiguration.class)
    @EnableSmsAuthentication
    public static class OAuth2PhoneSupportConfiguration { }

    @Configuration
    public static class AppWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                        .anyRequest().authenticated().and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                        .csrf().disable();
        }
    }

}
