
package com.github.shawven.security.browser;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.browser.authentication.*;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import com.github.shawven.security.browser.config.SessionConfiguration;
import com.github.shawven.security.browser.session.BrowserExpiredSessionStrategy;
import com.github.shawven.security.browser.session.BrowserInvalidSessionStrategy;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * 浏览器环境下扩展点配置，配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 *
 * @author Shoven
 * @since 2019-05-08 21:53
 */
@Configuration
@EnableConfigurationProperties(BrowserProperties.class)
public class BrowserAutoConfiguration {

	@Autowired(required = false)
	private BrowserProperties properties;

	/**
	 * session失效时的处理策略配置
     *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public InvalidSessionStrategy invalidSessionStrategy(){
		return new BrowserInvalidSessionStrategy(browserConfiguration());
	}

	/**
	 * 并发登录导致前一个session失效时的处理策略配置
     *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy(){
		return new BrowserExpiredSessionStrategy(browserConfiguration());
	}

	/**
	 * 退出时的处理策略配置
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public LogoutSuccessHandler logoutSuccessHandler(){
		return new BrowserLogoutSuccessHandler(browserConfiguration());
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
        return new BrowserAuthenticationSuccessHandler(browserConfiguration(), loginSuccessHandler);
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
	    return new BrowserAuthenticationFailureHandler(browserConfiguration(), browserLoginFailureHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler browserAccessDeniedHandler() {
	    return new BrowserAccessDeniedHandler(browserConfiguration());
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint browserAuthenticationExceptionEntryPoint() {
        return new BrowserAuthenticationExceptionEntryPoint(browserConfiguration());
    }

    @Configuration
    @ConditionalOnClass(ConnectAutoConfiguration.class)
    public static class ConnectSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
                AuthenticationSuccessHandler authenticationSuccessHandler,
                AuthenticationFailureHandler authenticationFailureHandler) {
            return new BrowserConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                    authenticationFailureHandler);
        }

    }


    /**
     * 授权配置提供器
     *
     * @return
     */
    @Bean
    @Order
    @ConditionalOnMissingBean
    public AuthorizationConfigureProvider browserAuthorizationConfigurerProvider() {
        return new BrowserAuthorizationConfigureProvider(browserConfiguration());
    }

    @Bean
    public BrowserConfiguration browserConfiguration() {
        BrowserConfiguration cfg = new BrowserConfiguration();
        cfg.setResponseType(properties.getResponseType());
        cfg.setSignInUrl(properties.getSignInUrl());
        cfg.setSignInProcessingUrl(properties.getSignInProcessingUrl());
        cfg.setSignUpUrl(properties.getSignUpUrl());
        cfg.setSignOutProcessingUrl(properties.getSignOutProcessingUrl());
        cfg.setSignOutSuccessUrl(properties.getSignOutSuccessUrl());
        cfg.setRememberMeSeconds(properties.getRememberMeSeconds());

        SessionProperties sessionProperties = properties.getSession();
        SessionConfiguration scfg = new SessionConfiguration();
        scfg.setMaximumSessions(sessionProperties.getMaximumSessions());
        scfg.setMaxSessionsPreventsLogin(sessionProperties.isMaxSessionsPreventsLogin());
        scfg.setSessionInvalidUrl(sessionProperties.getSessionInvalidUrl());
        cfg.setSession(scfg);
        return cfg;
    }
}
