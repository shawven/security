package com.github.shawven.security.browser;

import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.browser.config.BrowserSecurityConfigurer;
import com.github.shawven.security.browser.properties.BrowserConfiguration;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.connect.support.ConnectConfigurer;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import com.github.shawven.security.oauth2.SmsAuthenticationSecurityConfigurer;
import com.github.shawven.security.verification.VerificationSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.sql.DataSource;

/**
 * @author Shoven
 * @date 2019-11-09
 */
@Configuration
public class BrowserWebSecurityConfiguration {

    @Autowired
    private BrowserConfiguration browserConfiguration;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthorizationConfigurerManager authorizationConfigurerManager;

    @Autowired(required = false)
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Autowired(required = false)
    private InvalidSessionStrategy invalidSessionStrategy;

    @Autowired(required = false)
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired(required = false)
    private AccessDeniedHandler browserAccessDeniedHandler;

    @Autowired(required = false)
    private AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint;

    @Autowired(required = false)
    private VerificationSecurityConfigurer verificationSecurityConfigurer;

    @Bean
    public BrowserSecurityConfigurer browserSecurityConfigurer() {
        BrowserSecurityConfigurer configurer = new BrowserSecurityConfigurer();
        configurer.setAuthorizationConfigurerManager(authorizationConfigurerManager);
        configurer.setVerificationSecurityConfigurer(verificationSecurityConfigurer);

        configurer.setDataSource(dataSource);
        configurer.setBrowserConfiguration(browserConfiguration);
        configurer.setUserDetailsService(userDetailsService);
        configurer.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
        configurer.setInvalidSessionStrategy(invalidSessionStrategy);
        configurer.setLogoutSuccessHandler(logoutSuccessHandler);
        configurer.setBrowserAccessDeniedHandler(browserAccessDeniedHandler);
        configurer.setBrowserAuthenticationExceptionEntryPoint(browserAuthenticationExceptionEntryPoint);
        configurer.setBrowserAuthenticationSuccessHandler(authenticationSuccessHandler);
        configurer.setBrowserAuthenticationFailureHandler(authenticationFailureHandler);

        return configurer;
    }
}
