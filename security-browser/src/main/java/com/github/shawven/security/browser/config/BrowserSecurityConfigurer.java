
package com.github.shawven.security.browser.config;

import com.github.shawven.security.base.authentication.configurer.AuthorizationConfigurerManager;
import com.github.shawven.security.browser.properties.BrowserProperties;
import com.github.shawven.security.social.config.SmsAuthenticationSecurityConfigurer;
import com.github.shawven.security.verification.config.VerificationSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
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

    private BrowserProperties browserProperties;

	private DataSource dataSource;

	private UserDetailsService userDetailsService;

	private SmsAuthenticationSecurityConfigurer smsAuthenticationSecurityConfigurer;

    private VerificationSecurityConfigurer verificationSecurityConfigurer;

	private SpringSocialConfigurer springSocialConfigurer;

	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

	private InvalidSessionStrategy invalidSessionStrategy;

	private LogoutSuccessHandler logoutSuccessHandler;

	private AuthorizationConfigurerManager authorizationConfigurerManager;

	private FormLoginSecurityConfigurer formLoginSecurityConfigurer;

    private AccessDeniedHandler browserAccessDeniedHandler;

    private AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint;

    public void configureWeb(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/**/favicon.ico",
                "/**/*.js",
                "/**/*.css",
                "/**/*.jpg",
                "/**/*.png",
                "/**/*.gif"
        );
    }

	public void configureHttp(HttpSecurity http) throws Exception {
        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http.exceptionHandling();
        if (browserAuthenticationExceptionEntryPoint != null) {
            exceptionHandling.authenticationEntryPoint(browserAuthenticationExceptionEntryPoint);
        }
        if (browserAccessDeniedHandler != null) {
            exceptionHandling.accessDeniedHandler(browserAccessDeniedHandler);
        }

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

        SessionManagementConfigurer<HttpSecurity> sessionManagement = http.sessionManagement();
        if (invalidSessionStrategy != null) {
            sessionManagement.invalidSessionStrategy(invalidSessionStrategy);
        }
        SessionManagementConfigurer<HttpSecurity>.ConcurrencyControlConfigurer controlConfigurer = sessionManagement
                .maximumSessions(browserProperties.getSession().getMaximumSessions())
                .maxSessionsPreventsLogin(browserProperties.getSession().isMaxSessionsPreventsLogin());
        if (sessionInformationExpiredStrategy != null) {
            controlConfigurer.expiredSessionStrategy(sessionInformationExpiredStrategy);
        }

        LogoutConfigurer<HttpSecurity> logoutConfigurer = http.logout();
        String signOutProcessingUrl = browserProperties.getSignOutProcessingUrl();
        if (signOutProcessingUrl != null) {
            logoutConfigurer .logoutUrl(signOutProcessingUrl);
        }
        logoutConfigurer .deleteCookies("JSESSIONID");
        if (logoutSuccessHandler != null) {
            logoutConfigurer.logoutSuccessHandler(logoutSuccessHandler);
        }

        http .csrf().disable();

        //记住我配置，如果想在'记住我'登录时记录日志，可以注册一个InteractiveAuthenticationSuccessEvent事件的监听器
        int rememberMeSeconds = browserProperties.getRememberMeSeconds();
        if (rememberMeSeconds > 0) {
            http
                .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(browserProperties.getRememberMeSeconds())
                .userDetailsService(userDetailsService);
        }

        formLoginSecurityConfigurer.configure(http);
        authorizationConfigurerManager.config(http.authorizeRequests());
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

    public BrowserProperties getBrowserProperties() {
        return browserProperties;
    }

    public void setBrowserProperties(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
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

    public void setSpringSocialConfigurer(SpringSocialConfigurer springSocialConfigurer) {
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

    public FormLoginSecurityConfigurer getFormLoginSecurityConfigurer() {
        return formLoginSecurityConfigurer;
    }

    public void setFormLoginSecurityConfigurer(FormLoginSecurityConfigurer formLoginSecurityConfigurer) {
        this.formLoginSecurityConfigurer = formLoginSecurityConfigurer;
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
}
