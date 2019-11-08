
package com.github.shawven.security.app.config;

import com.github.shawven.security.app.wxmini.WxMiniAuthenticationFilter;
import com.github.shawven.security.app.wxmini.WxMiniAuthenticationProvider;
import com.github.shawven.security.app.config.social.AppSingUpUtils;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.security.SocialUserDetailsService;

public class WxMiniAuthenticationSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    private AuthenticationFailureHandler appAuthenticationFailureHandler;

	private SocialUserDetailsService userDetailsService;

    private AppSingUpUtils appSingUpUtils;

    private ConnectConfiguration connectConfiguration;

	@Override
	public void configure(HttpSecurity http) throws Exception {

        WxMiniAuthenticationFilter wxMiniAuthenticationFilter = new WxMiniAuthenticationFilter();
        wxMiniAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        wxMiniAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        wxMiniAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);
        wxMiniAuthenticationFilter.setAppSingUpUtils(appSingUpUtils);
        wxMiniAuthenticationFilter.setConnectConfiguration(connectConfiguration);

        WxMiniAuthenticationProvider wxMiniAuthenticationProvider = new WxMiniAuthenticationProvider();
        wxMiniAuthenticationProvider.setUserDetailsService(userDetailsService);
        wxMiniAuthenticationProvider.setUsersConnectionRepository(appSingUpUtils.getUsersConnectionRepository());

		http.authenticationProvider(wxMiniAuthenticationProvider)
			.addFilterAfter(wxMiniAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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

    public AppSingUpUtils getAppSingUpUtils() {
        return appSingUpUtils;
    }

    public void setAppSingUpUtils(AppSingUpUtils appSingUpUtils) {
        this.appSingUpUtils = appSingUpUtils;
    }

    public ConnectConfiguration getConnectConfiguration() {
        return connectConfiguration;
    }

    public void setConnectConfiguration(ConnectConfiguration connectConfiguration) {
        this.connectConfiguration = connectConfiguration;
    }
}
