package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppWebSecurityConfigurer;
import com.github.shawven.security.app.config.AppConfiguration;
import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Autowired
    private AppConfiguration configuration;

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
    private List<AppWebSecurityConfigurer> configurers = Collections.emptyList();

    @Autowired(required = false)
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
            http.csrf().disable().rememberMe().disable();
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
}
