
package com.github.shawven.security.server.verification;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 短信登录配置
 */
public class SmsSecurityConfigurer extends HttpSecurityConfigurer {

    private String loginProcessingUrl;

    private PhoneUserDetailsService userDetailsService;

    private ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider;

    private AuthenticationFailureHandler authenticationFailureHandler;

    public SmsSecurityConfigurer(String loginProcessingUrl, PhoneUserDetailsService userDetailsService,
                                 ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
                                 AuthenticationFailureHandler authenticationFailureHandler) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandlerProvider = authenticationSuccessHandlerProvider;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

	@Override
	public void configure(HttpSecurity http) throws Exception {

		SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter(loginProcessingUrl);
		smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		smsAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandlerProvider.getIfAvailable());
		smsAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

		SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
		smsAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsAuthenticationProvider)
			.addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

    public String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    public void setLoginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
    }

    public PhoneUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(PhoneUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public ObjectProvider<AuthenticationSuccessHandler> getAuthenticationSuccessHandlerProvider() {
        return authenticationSuccessHandlerProvider;
    }

    public void setAuthenticationSuccessHandlerProvider(ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider) {
        this.authenticationSuccessHandlerProvider = authenticationSuccessHandlerProvider;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }
}
