
package com.github.shawven.security.oauth2.phone;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.verification.PhoneUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 短信登录配置
 */
public class PhoneFilterProvider extends AuthenticationFilterProvider {

    private PhoneConfiguration configuration;

    private PhoneUserDetailsService userDetailsService;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    public PhoneFilterProvider(PhoneConfiguration configuration, PhoneUserDetailsService userDetailsService,
                               AuthenticationSuccessHandler authenticationSuccessHandler,
                               AuthenticationFailureHandler authenticationFailureHandler) {
        this.configuration = configuration;
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

	@Override
	public void configure(HttpSecurity http) throws Exception {

		PhoneAuthenticationFilter phoneAuthenticationFilter = new PhoneAuthenticationFilter(configuration);
		phoneAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		phoneAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		phoneAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

		SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
		smsAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsAuthenticationProvider)
			.addFilterAfter(phoneAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

}
