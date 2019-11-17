package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.*;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFailureHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AccessDeniedHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationExceptionEntryPoint;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationFailureHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationHandler;
import com.github.shawven.security.app.openid.OpenIdFilterProviderConfigurer;
import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.oauth2.AuthenticationSuccessHandlerPostProcessor;
import com.github.shawven.security.verification.authentication.EnableSmsAuthentication;
import com.github.shawven.security.verification.authentication.SmsAuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.social.connect.UsersConnectionRepository;
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
     * 验证成功处理器后处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandlerPostProcessor authenticationSuccessHandlerPostProcessor(
            @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler,
            @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
        return handler -> {
            handler.adapt(new AppOAuth2AuthenticationHandler(loginSuccessHandler, loginFailureHandler));
        };
    }

    @Configuration
    @Import(AppOAuth2Endpoint.class)
    public static class OAuth2SupportConfiguration {

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
    @Import(AppConnectEndpoint.class)
    @AutoConfigureAfter(AppAutoConfiguration.class)
    public static class ConnectSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
                @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
                @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {

            return new AppConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                    new AppConnectAuthenticationFailureHandler(loginFailureHandler));
        }

        @Bean
        public HttpSecurityConfigurer openIdFilterProviderConfigurer(
                @Lazy AuthenticationSuccessHandler authenticationSuccessHandler,
                AuthenticationFailureHandler authenticationFailureHandler,
                SocialUserDetailsService userDetailsService,
                UsersConnectionRepository usersConnectionRepository) {
            return new OpenIdFilterProviderConfigurer(authenticationSuccessHandler,
                    authenticationFailureHandler, userDetailsService, usersConnectionRepository);
        }

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
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().mvcMatchers("/error");
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
