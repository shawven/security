
package com.github.shawven.security.app.connect.wxmini;

import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import com.github.shawven.security.connect.RedisSignInUtils;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.security.SocialUserDetailsService;

public class WxMiniSecuritySupportConfigurer extends HttpSecuritySupportConfigurer {

    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    private AuthenticationFailureHandler appAuthenticationFailureHandler;

	private SocialUserDetailsService userDetailsService;

    private RedisSignInUtils redisSignInUtils;

    private ConnectConfiguration connectConfiguration;

	@Override
	public void configure(HttpSecurity http) throws Exception {

        WxMiniAuthenticationFilter wxMiniAuthenticationFilter = new WxMiniAuthenticationFilter();
        wxMiniAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        wxMiniAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        wxMiniAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);
        wxMiniAuthenticationFilter.setRedisSignInUtils(redisSignInUtils);
        wxMiniAuthenticationFilter.setConnectConfiguration(connectConfiguration);

        WxMiniAuthenticationProvider wxMiniAuthenticationProvider = new WxMiniAuthenticationProvider();
        wxMiniAuthenticationProvider.setUserDetailsService(userDetailsService);
        wxMiniAuthenticationProvider.setUsersConnectionRepository(redisSignInUtils.getUsersConnectionRepository());

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

    public RedisSignInUtils getRedisSignInUtils() {
        return redisSignInUtils;
    }

    public void setRedisSignInUtils(RedisSignInUtils redisSignInUtils) {
        this.redisSignInUtils = redisSignInUtils;
    }

    public ConnectConfiguration getConnectConfiguration() {
        return connectConfiguration;
    }

    public void setConnectConfiguration(ConnectConfiguration connectConfiguration) {
        this.connectConfiguration = connectConfiguration;
    }
}
