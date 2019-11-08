
package com.github.shawven.security.browser.config;

import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.browser.properties.BrowserConfiguration;
import com.github.shawven.security.oauth2.SmsAuthenticationSecurityConfigurer;
import com.github.shawven.security.verification.VerificationSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * 浏览器环境下安全配置主类
 *
 * @author Shoven
 * @since 2019-05-08 21:53
 */
public class BrowserSecurityConfigurer {

    private BrowserConfiguration browserConfiguration;

	private DataSource dataSource;

	private UserDetailsService userDetailsService;

	private SmsAuthenticationSecurityConfigurer smsAuthenticationSecurityConfigurer;

    private VerificationSecurityConfigurer verificationSecurityConfigurer;

	private SpringSocialConfigurer springSocialConfigurer;

	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

	private InvalidSessionStrategy invalidSessionStrategy;

	private LogoutSuccessHandler logoutSuccessHandler;

	private AuthorizationConfigurerManager authorizationConfigurerManager;

    private AccessDeniedHandler browserAccessDeniedHandler;

    private AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint;

    private AuthenticationSuccessHandler browserAuthenticationSuccessHandler;

    private AuthenticationFailureHandler browserAuthenticationFailureHandler;

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/error",
                "/**/favicon.ico",
                "/**/*.js",
                "/**/*.css"
        );
    }

	public void configure(HttpSecurity http) throws Exception {
        if (verificationSecurityConfigurer != null) {
            http.apply(verificationSecurityConfigurer);
        }
		if (verificationSecurityConfigurer != null) {
            http.apply(verificationSecurityConfigurer);
        }
        if (smsAuthenticationSecurityConfigurer != null) {
            http.apply(smsAuthenticationSecurityConfigurer);
        }
        if (springSocialConfigurer != null) {
            http.apply(springSocialConfigurer);
        }
        authorizationConfigurerManager.config(http.authorizeRequests());

        configureExceptionHandler(http);
        configureSession(http);
        configureRememberMe(http);
        configureFormLogin(http);
	}

    public void configureExceptionHandler(HttpSecurity http) throws Exception {
        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http.exceptionHandling();
        if (browserAuthenticationExceptionEntryPoint != null) {
            exceptionHandling.authenticationEntryPoint(browserAuthenticationExceptionEntryPoint);
        }
        if (browserAccessDeniedHandler != null) {
            exceptionHandling.accessDeniedHandler(browserAccessDeniedHandler);
        }
    }

    public void configureSession(HttpSecurity http) throws Exception {
        SessionManagementConfigurer<HttpSecurity> sessionManagement = http.sessionManagement();
        if (invalidSessionStrategy != null) {
            sessionManagement.invalidSessionStrategy(invalidSessionStrategy);
        }
        SessionManagementConfigurer<HttpSecurity>.ConcurrencyControlConfigurer controlConfigurer = sessionManagement
                .maximumSessions(browserConfiguration.getSession().getMaximumSessions())
                .maxSessionsPreventsLogin(browserConfiguration.getSession().isMaxSessionsPreventsLogin());
        if (sessionInformationExpiredStrategy != null) {
            controlConfigurer.expiredSessionStrategy(sessionInformationExpiredStrategy);
        }

    }

    public void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage(browserConfiguration.getSignInUrl())
                .loginProcessingUrl(browserConfiguration.getSignInProcessingUrl())
                .successHandler(browserAuthenticationSuccessHandler)
                .failureHandler(browserAuthenticationFailureHandler);
    }

    public void configureLogout(HttpSecurity http) throws Exception {
        LogoutConfigurer<HttpSecurity> logoutConfigurer = http.logout();
        String signOutProcessingUrl = browserConfiguration.getSignOutProcessingUrl();
        if (signOutProcessingUrl != null) {
            logoutConfigurer .logoutUrl(signOutProcessingUrl);
        }
        logoutConfigurer .deleteCookies("JSESSIONID");
        if (logoutSuccessHandler != null) {
            logoutConfigurer.logoutSuccessHandler(logoutSuccessHandler);
        }
    }

    public void configureRememberMe(HttpSecurity http) throws Exception {
        //记住我配置，如果想在'记住我'登录时记录日志，可以注册一个InteractiveAuthenticationSuccessEvent事件的监听器
        int rememberMeSeconds = browserConfiguration.getRememberMeSeconds();
        if (rememberMeSeconds > 0) {
            http
                    .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(browserConfiguration.getRememberMeSeconds())
                    .userDetailsService(userDetailsService);
        }
    }

	/**
	 * 记住我功能的token存取器配置
	 * @return
	 */
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}

    public BrowserConfiguration getBrowserConfiguration() {
        return browserConfiguration;
    }

    public void setBrowserConfiguration(BrowserConfiguration browserConfiguration) {
        this.browserConfiguration = browserConfiguration;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public SmsAuthenticationSecurityConfigurer getSmsAuthenticationSecurityConfigurer() {
        return smsAuthenticationSecurityConfigurer;
    }

    public void setSmsAuthenticationSecurityConfigurer(SmsAuthenticationSecurityConfigurer smsAuthenticationSecurityConfigurer) {
        this.smsAuthenticationSecurityConfigurer = smsAuthenticationSecurityConfigurer;
    }

    public VerificationSecurityConfigurer getVerificationSecurityConfigurer() {
        return verificationSecurityConfigurer;
    }

    public void setVerificationSecurityConfigurer(VerificationSecurityConfigurer verificationSecurityConfigurer) {
        this.verificationSecurityConfigurer = verificationSecurityConfigurer;
    }

    public SpringSocialConfigurer getSpringSocialConfigurer() {
        return springSocialConfigurer;
    }

    public void setConnectConfigurer(SpringSocialConfigurer springSocialConfigurer) {
        this.springSocialConfigurer = springSocialConfigurer;
    }

    public SessionInformationExpiredStrategy getSessionInformationExpiredStrategy() {
        return sessionInformationExpiredStrategy;
    }

    public void setSessionInformationExpiredStrategy(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
        this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
    }

    public InvalidSessionStrategy getInvalidSessionStrategy() {
        return invalidSessionStrategy;
    }

    public void setInvalidSessionStrategy(InvalidSessionStrategy invalidSessionStrategy) {
        this.invalidSessionStrategy = invalidSessionStrategy;
    }

    public LogoutSuccessHandler getLogoutSuccessHandler() {
        return logoutSuccessHandler;
    }

    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    public AuthorizationConfigurerManager getAuthorizationConfigurerManager() {
        return authorizationConfigurerManager;
    }

    public void setAuthorizationConfigurerManager(AuthorizationConfigurerManager authorizationConfigurerManager) {
        this.authorizationConfigurerManager = authorizationConfigurerManager;
    }

    public AccessDeniedHandler getBrowserAccessDeniedHandler() {
        return browserAccessDeniedHandler;
    }

    public void setBrowserAccessDeniedHandler(AccessDeniedHandler browserAccessDeniedHandler) {
        this.browserAccessDeniedHandler = browserAccessDeniedHandler;
    }

    public AuthenticationEntryPoint getBrowserAuthenticationExceptionEntryPoint() {
        return browserAuthenticationExceptionEntryPoint;
    }

    public void setBrowserAuthenticationExceptionEntryPoint(AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint) {
        this.browserAuthenticationExceptionEntryPoint = browserAuthenticationExceptionEntryPoint;
    }

    public AuthenticationSuccessHandler getBrowserAuthenticationSuccessHandler() {
        return browserAuthenticationSuccessHandler;
    }

    public void setBrowserAuthenticationSuccessHandler(AuthenticationSuccessHandler browserAuthenticationSuccessHandler) {
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;
    }

    public AuthenticationFailureHandler getBrowserAuthenticationFailureHandler() {
        return browserAuthenticationFailureHandler;
    }

    public void setBrowserAuthenticationFailureHandler(AuthenticationFailureHandler browserAuthenticationFailureHandler) {
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
    }
}
