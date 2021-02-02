
package com.github.shawven.security.verification.security;

import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 短信登录配置
 */
public class SmsSecuritySupportConfigurer extends HttpSecuritySupportConfigurer {

    private String loginProcessingUrl;

    private PhoneUserDetailsService userDetailsService;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    public SmsSecuritySupportConfigurer(String loginProcessingUrl, PhoneUserDetailsService userDetailsService,
                                        AuthenticationSuccessHandler authenticationSuccessHandler,
                                        AuthenticationFailureHandler authenticationFailureHandler) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

	@Override
	public void configure(HttpSecurity http) throws Exception {

		SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter(loginProcessingUrl);
		smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		smsAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		smsAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

		SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
		smsAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsAuthenticationProvider)
			.addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

}
