
package com.github.shawven.security.server.connect.openid;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.connect.ConnectConstants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialUserDetailsService;

public class OpenIdSecurityConfigurer extends HttpSecurityConfigurer {

	private ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider;

	private AuthenticationFailureHandler authenticationFailureHandler;

	private SocialUserDetailsService userDetailsService;

	private UsersConnectionRepository usersConnectionRepository;

	private String processingUrl;

    public OpenIdSecurityConfigurer(String processingUrl,
                                    ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
                                    AuthenticationFailureHandler authenticationFailureHandler,
                                    SocialUserDetailsService userDetailsService,
                                    UsersConnectionRepository usersConnectionRepository) {
        this.processingUrl = processingUrl;
        this.authenticationSuccessHandlerProvider = authenticationSuccessHandlerProvider;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.userDetailsService = userDetailsService;
        this.usersConnectionRepository = usersConnectionRepository;
    }

    @Override
    public void initHttp(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(processingUrl).permitAll();
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		OpenIdAuthenticationFilter openIdAuthenticationFilter = new OpenIdAuthenticationFilter(processingUrl);
		openIdAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		openIdAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandlerProvider.getIfAvailable());
		openIdAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

		OpenIdAuthenticationProvider openIdAuthenticationProvider = new OpenIdAuthenticationProvider();
		openIdAuthenticationProvider.setUserDetailsService(userDetailsService);
		openIdAuthenticationProvider.setUsersConnectionRepository(usersConnectionRepository);

		http.authenticationProvider(openIdAuthenticationProvider)
			.addFilterAfter(openIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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

}
