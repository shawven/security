package com.github.shawven.security.browser;

import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-11-09
 */
@Configuration
public class BrowserSecuritySupportConfiguration extends HttpSecuritySupportConfigurer {

    private BrowserConfiguration configuration;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    private InvalidSessionStrategy invalidSessionStrategy;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private AccessDeniedHandler accessDeniedHandler;

    private AuthorizationConfigurerManager authorizationConfigurerManager;

    private UserDetailsService userDetailsService;

    private LogoutSuccessHandler logoutSuccessHandler;

    private DataSource dataSource;

    private List<HttpSecuritySupportConfigurer> providerConfigurers;

    public BrowserSecuritySupportConfiguration(BrowserConfiguration configuration,
                                               AuthenticationSuccessHandler authenticationSuccessHandler,
                                               AuthenticationFailureHandler authenticationFailureHandler,
                                               SessionInformationExpiredStrategy sessionInformationExpiredStrategy,
                                               InvalidSessionStrategy invalidSessionStrategy,
                                               AuthenticationEntryPoint authenticationEntryPoint,
                                               AccessDeniedHandler accessDeniedHandler,
                                               AuthorizationConfigurerManager authorizationConfigurerManager,
                                               UserDetailsService userDetailsService,
                                               LogoutSuccessHandler logoutSuccessHandler,
                                               @Autowired(required = false) DataSource dataSource,
                                               @Autowired(required = false) List<HttpSecuritySupportConfigurer> providerConfigurers) {
        this.configuration = configuration;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
        this.invalidSessionStrategy = invalidSessionStrategy;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authorizationConfigurerManager = authorizationConfigurerManager;
        this.userDetailsService = userDetailsService;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.dataSource = dataSource;
        this.providerConfigurers = providerConfigurers;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        try {
            Class.forName("com.github.shawven.security.oauth2.OAuth2AutoConfiguration");
        } catch (ClassNotFoundException ignored) {
            // OAuth2 已经配置到资源服务去了，所以只有没有OAuth2模块时才配置
            for (HttpSecuritySupportConfigurer configurer : providerConfigurers) {
                http.apply(configurer);
            }
            authorizationConfigurerManager.config(http.authorizeRequests());
            // 只有不是OAuth2时就加载这些
            configureExceptionHandler(http);
            configureSession(http);
            configureRememberMe(http);
            configureFormLogin(http);
            configureLogout(http);
        }


    }

    public void configureExceptionHandler(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler);
    }

    public void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionStrategy(invalidSessionStrategy)
                .maximumSessions(configuration.getSession().getMaximumSessions())
                .maxSessionsPreventsLogin(configuration.getSession().isMaxSessionsPreventsLogin())
                .expiredSessionStrategy(sessionInformationExpiredStrategy);
    }

    public void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage(configuration.getSignInUrl())
                .loginProcessingUrl(configuration.getSignInProcessingUrl())
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);
    }

    public void configureLogout(HttpSecurity http) throws Exception {
        LogoutConfigurer<HttpSecurity> logoutConfigurer = http.logout();
        String signOutProcessingUrl = configuration.getSignOutProcessingUrl();
        if (signOutProcessingUrl != null) {
            logoutConfigurer.logoutUrl(signOutProcessingUrl);
        }
        logoutConfigurer.deleteCookies("JSESSIONID").logoutSuccessHandler(logoutSuccessHandler);
    }

    public void configureRememberMe(HttpSecurity http) throws Exception {
        //记住我配置，如果想在'记住我'登录时记录日志，可以注册一个InteractiveAuthenticationSuccessEvent事件的监听器
        int rememberMeSeconds = configuration.getRememberMeSeconds();
        if (rememberMeSeconds > 0) {
            if (dataSource == null) {
                throw new IllegalStateException("Remember me that my feature must have a DataSource!");
            }
            JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
            tokenRepository.setDataSource(dataSource);
            http
                    .rememberMe()
                    .tokenRepository(tokenRepository)
                    .tokenValiditySeconds(rememberMeSeconds)
                    .userDetailsService(userDetailsService);
        }
    }

    @Configuration
    public static class AuthenticationManagerConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().mvcMatchers(
                    "/**/favicon.ico",
                    "/**/*.js",
                    "/**/*.css"
            );
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        /**
         * 跨域支持
         *
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public CorsFilter corsFilter() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("*");
            configuration.addAllowedMethod("*");
            configuration.addAllowedHeader("*");
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return new CorsFilter(source);
        }
    }
}
