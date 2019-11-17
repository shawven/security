package com.github.shawven.security.browser;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-11-09
 */
@Configuration
public class BrowserWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BrowserConfiguration configuration;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthorizationConfigurerManager authorizationConfigurerManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private List<HttpSecurityConfigurer> providerConfigurers = Collections.emptyList();

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers(
                "/error",
                "/**/favicon.ico",
                "/**/*.js",
                "/**/*.css"
        );
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        boolean redirectSupported = false;
        try {
            // oauth2环境下已经配置了一遍
            Class.forName("com.github.shawven.security.oauth2.OAuth2AutoConfiguration");
            http.csrf().disable();
            if (configuration.getResponseType()  == ResponseType.REDIRECT) {
                redirectSupported = true;
            }
        } catch (ClassNotFoundException ignored) { }
        if (redirectSupported) {
            for (HttpSecurityConfigurer configurer : providerConfigurers) {
                http.apply(configurer);
            }
            authorizationConfigurerManager.config(http.authorizeRequests());
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

}
