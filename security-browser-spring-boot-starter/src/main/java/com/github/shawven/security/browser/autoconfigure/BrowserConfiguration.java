
package com.github.shawven.security.browser.autoconfigure;

import com.github.shawven.security.base.authentication.configurer.AuthorizationConfigurerManager;
import com.github.shawven.security.base.authentication.configurer.AuthorizationConfigurerProvider;
import com.github.shawven.security.browser.authentication.*;
import com.github.shawven.security.browser.config.BrowserAuthorizationConfigurerProvider;
import com.github.shawven.security.browser.config.BrowserSecurityConfigurer;
import com.github.shawven.security.browser.config.BrowserSocialAuthenticationFilterPostProcessor;
import com.github.shawven.security.browser.config.FormLoginSecurityConfigurer;
import com.github.shawven.security.browser.properties.BrowserProperties;
import com.github.shawven.security.browser.session.BrowserExpiredSessionStrategy;
import com.github.shawven.security.browser.session.BrowserInvalidSessionStrategy;
import com.github.shawven.security.browser.session.SessionVerificationRepository;
import com.github.shawven.security.social.EnableOAuth2Support;
import com.github.shawven.security.social.config.SmsAuthenticationSecurityConfigurer;
import com.github.shawven.security.social.properties.SocialProperties;
import com.github.shawven.security.social.support.SocialAuthenticationFilterPostProcessor;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.config.VerificationSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * 浏览器环境下扩展点配置，配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 *
 * @author Shoven
 * @since 2019-05-08 21:53
 */
@Configuration
@EnableOAuth2Support
@EnableConfigurationProperties(BrowserProperties.class)
public class BrowserConfiguration {

	@Autowired
	private BrowserProperties browserProperties;

    /**
     * 基于session的验证码存取器
     *
     * @return
     */
	@Bean
    @ConditionalOnMissingBean
    public VerificationRepository sessionVerificationRepository() {
	    return new SessionVerificationRepository();
    }

	/**
	 * session失效时的处理策略配置
     *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public InvalidSessionStrategy invalidSessionStrategy(){
		return new BrowserInvalidSessionStrategy(browserProperties);
	}

	/**
	 * 并发登录导致前一个session失效时的处理策略配置
     *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy(){
		return new BrowserExpiredSessionStrategy(browserProperties);
	}

	/**
	 * 退出时的处理策略配置
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public LogoutSuccessHandler logoutSuccessHandler(){
		return new BrowserLogoutSuccessHandler(browserProperties.getSignOutSuccessUrl());
	}

    /**
     * 验证成功处理器
     *
     * @param loginSuccessHandler
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler browserAuthenticationSuccessHandler(
            @Autowired(required = false) BrowserLoginSuccessHandler loginSuccessHandler) {
        return new BrowserAuthenticationSuccessHandler(browserProperties, loginSuccessHandler);
    }

    /**
     * 验证失败处理器
     *
     * @return
     */
	@Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler browserAuthenticationFailureHandler(
            @Autowired(required = false) BrowserLoginFailureHandler browserLoginFailureHandler) {
	    return new BrowserAuthenticationFailureHandler(browserProperties, browserLoginFailureHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler browserAccessDeniedHandler() {
	    return new BrowserAccessDeniedHandler(browserProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint() {
        return new BrowserAuthenticationExceptionEntryPoint(browserProperties);
    }

    /**
     * 表单登陆安全配置
     *
     * @param authenticationSuccessHandler
     * @param authenticationFailureHandler
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public FormLoginSecurityConfigurer formLoginSecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler,
                                                               AuthenticationFailureHandler authenticationFailureHandler) {
	    return new FormLoginSecurityConfigurer(browserProperties, authenticationSuccessHandler, authenticationFailureHandler);
    }


    @Bean
    @ConditionalOnMissingBean
    public SocialAuthenticationFilterPostProcessor
    browserSocialAuthenticationFilterPostProcessor(AuthenticationSuccessHandler authenticationSuccessHandler,
                                                   AuthenticationFailureHandler authenticationFailureHandler) {
        return new BrowserSocialAuthenticationFilterPostProcessor(authenticationSuccessHandler, authenticationFailureHandler);
    }

    /**
     * 授权配置提供器
     *
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    @ConditionalOnMissingBean
    public AuthorizationConfigurerProvider authorizationConfigurerProvider(SocialProperties socialProperties) {
	    return new BrowserAuthorizationConfigurerProvider(browserProperties, socialProperties);
    }

    @Bean
    public BrowserSecurityConfigurer browserSecurityConfigurer(
            DataSource dataSource,
            BrowserProperties browserProperties,
            UserDetailsService userDetailsService,
            @Autowired(required = false)
            SessionInformationExpiredStrategy sessionInformationExpiredStrategy,
            @Autowired(required = false)
            InvalidSessionStrategy invalidSessionStrategy,
            @Autowired(required = false)
            LogoutSuccessHandler logoutSuccessHandler,
            AuthorizationConfigurerManager authorizationConfigurerManager,
            @Autowired(required = false)
            AccessDeniedHandler browserAccessDeniedHandler,
            @Autowired(required = false)
            AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint,
            FormLoginSecurityConfigurer formLoginSecurityConfigurer,
            @Autowired(required = false)
            SpringSocialConfigurer springSocialConfigurer,
            @Autowired(required = false)
            VerificationSecurityConfigurer verificationSecurityConfigurer,
            @Autowired(required = false)
            SmsAuthenticationSecurityConfigurer smsAuthenticationSecurityConfigurer) {
        BrowserSecurityConfigurer configurer = new BrowserSecurityConfigurer();
        configurer.setDataSource(dataSource);
        configurer.setBrowserProperties(browserProperties);
        configurer.setUserDetailsService(userDetailsService);
        configurer.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
        configurer.setInvalidSessionStrategy(invalidSessionStrategy);
        configurer.setLogoutSuccessHandler(logoutSuccessHandler);
        configurer.setBrowserAccessDeniedHandler(browserAccessDeniedHandler);
        configurer.setBrowserAuthenticationExceptionEntryPoint(browserAuthenticationExceptionEntryPoint);

        configurer.setAuthorizationConfigurerManager(authorizationConfigurerManager);
        configurer.setFormLoginSecurityConfigurer(formLoginSecurityConfigurer);
        configurer.setSpringSocialConfigurer(springSocialConfigurer);
        configurer.setVerificationSecurityConfigurer(verificationSecurityConfigurer);
        configurer.setSmsAuthenticationSecurityConfigurer(smsAuthenticationSecurityConfigurer);
        return configurer;
    }
}
