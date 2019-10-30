
package com.github.shawven.security.app.config;

import com.github.shawven.security.app.authentication.wxmini.WxMiniAuthenticationFilter;
import com.github.shawven.security.app.authentication.wxmini.WxMiniAuthenticationProvider;
import com.github.shawven.security.app.config.social.AppSingUpUtils;
import com.github.shawven.security.social.properties.SocialProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

public class WxMiniAuthenticationSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    private AuthenticationFailureHandler appAuthenticationFailureHandler;

	private SocialUserDetailsService userDetailsService;

    private AppSingUpUtils appSingUpUtils;

    private SocialProperties socialProperties;

	@Override
	public void configure(HttpSecurity http) throws Exception {

        WxMiniAuthenticationFilter wxMiniAuthenticationFilter = new WxMiniAuthenticationFilter();
        wxMiniAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        wxMiniAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        wxMiniAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);
        wxMiniAuthenticationFilter.setAppSingUpUtils(appSingUpUtils);
        wxMiniAuthenticationFilter.setSecurityProperties(socialProperties);

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

    public SocialProperties getSocialProperties() {
        return socialProperties;
    }

    public void setSocialProperties(SocialProperties socialProperties) {
        this.socialProperties = socialProperties;
    }
}
