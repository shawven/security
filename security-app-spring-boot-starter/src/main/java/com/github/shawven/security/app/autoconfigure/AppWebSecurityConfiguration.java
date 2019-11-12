package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppWebSecurityConfigurer;
import com.github.shawven.security.app.config.AppConfiguration;
import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
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
 * web安全配置
 *
 * @author Shoven
 * @since 2019-05-09 15:33
 */
@Configuration
public class AppWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AppConfiguration configuration;

    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    private InvalidSessionStrategy invalidSessionStrategy;

    private AccessDeniedHandler accessDeniedHandler;

    private AuthorizationConfigurerManager authorizationConfigurerManager;

    private UserDetailsService userDetailsService;

    private LogoutSuccessHandler logoutSuccessHandler;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private DataSource dataSource;

    private List<AppWebSecurityConfigurer> configurers = Collections.emptyList();

    private List<AuthenticationFilterProvider> providerConfigurers = Collections.emptyList();


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        for (AppWebSecurityConfigurer configurer : configurers) {
            configurer.configure(web);
        }
        web.ignoring().mvcMatchers("/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        for (AppWebSecurityConfigurer configurer : configurers) {
            configurer.configure(http);
        }

        try {
            Class.forName("com.github.shawven.security.oauth2.OAuth2AutoConfiguration");
            http.csrf().disable();
        } catch (ClassNotFoundException e) {
            for (AuthenticationFilterProvider configurer : providerConfigurers) {
                http = http.apply(configurer).and();
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

    @Autowired
    public void setConfiguration(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    public void setSessionInformationExpiredStrategy(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
        this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
    }

    @Autowired
    public void setInvalidSessionStrategy(InvalidSessionStrategy invalidSessionStrategy) {
        this.invalidSessionStrategy = invalidSessionStrategy;
    }

    @Autowired
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Autowired
    public void setAuthorizationConfigurerManager(AuthorizationConfigurerManager authorizationConfigurerManager) {
        this.authorizationConfigurerManager = authorizationConfigurerManager;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Autowired
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Autowired
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Autowired
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired(required = false)
    public void setConfigurers(List<AppWebSecurityConfigurer> configurers) {
        this.configurers = configurers;
    }

    @Autowired(required = false)
    public void setProviderConfigurers(List<AuthenticationFilterProvider> providerConfigurers) {
        this.providerConfigurers = providerConfigurers;
    }
}
