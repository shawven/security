
package com.github.shawven.security.app.openid;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.app.openid.OpenIdAuthenticationFilter;
import com.github.shawven.security.app.openid.OpenIdAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialUserDetailsService;

public class OpenIdFilterProvider extends AuthenticationFilterProvider {

	private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

	private AuthenticationFailureHandler appAuthenticationFailureHandler;

	private SocialUserDetailsService userDetailsService;

	private UsersConnectionRepository usersConnectionRepository;

    @Override
	public void configure(HttpSecurity http) throws Exception {

		OpenIdAuthenticationFilter openIdAuthenticationFilter = new OpenIdAuthenticationFilter();
		openIdAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		openIdAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
		openIdAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

		OpenIdAuthenticationProvider openIdAuthenticationProvider = new OpenIdAuthenticationProvider();
		openIdAuthenticationProvider.setUserDetailsService(userDetailsService);
		openIdAuthenticationProvider.setUsersConnectionRepository(usersConnectionRepository);

		http.authenticationProvider(openIdAuthenticationProvider)
			.addFilterAfter(openIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

    public AuthenticationSuccessHandler getAppAuthenticationSuccessHandler() {
        return appAuthenticationSuccessHandler;
    }

    public void setAppAuthenticationSuccessHandler(AuthenticationSuccessHandler appAuthenticationSuccessHandler) {
        this.appAuthenticationSuccessHandler = appAuthenticationSuccessHandler;
    }

    public AuthenticationFailureHandler getAppAuthenticationFailureHandler() {
        return appAuthenticationFailureHandler;
    }

    public void setAppAuthenticationFailureHandler(AuthenticationFailureHandler appAuthenticationFailureHandler) {
        this.appAuthenticationFailureHandler = appAuthenticationFailureHandler;
    }

    public SocialUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(SocialUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public UsersConnectionRepository getUsersConnectionRepository() {
        return usersConnectionRepository;
    }

    public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
        this.usersConnectionRepository = usersConnectionRepository;
    }

    @Override
    public HttpSecurity and() {
        return super.and();
    }
}
