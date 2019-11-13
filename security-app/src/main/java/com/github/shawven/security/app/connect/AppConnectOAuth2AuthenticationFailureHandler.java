
package com.github.shawven.security.app.connect;

import com.github.shawven.security.app.AppLoginFailureHandler;
import com.github.shawven.security.app.oauth2.AppOAuth2AuthenticationFailureHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.security.SocialAuthenticationRedirectException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 社交登陆APP环境下认证失败处理器
 */
public class AppConnectOAuth2AuthenticationFailureHandler extends AppOAuth2AuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(AppConnectOAuth2AuthenticationFailureHandler.class);

    private AppLoginFailureHandler loginFailureHandler;

    public AppConnectOAuth2AuthenticationFailureHandler(AppLoginFailureHandler loginFailureHandler) {
        super(loginFailureHandler);
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
        logger.debug(e.getMessage(), e);
        if (e instanceof SocialAuthenticationRedirectException) {
            if (loginFailureHandler != null) {
                loginFailureHandler.onAuthenticationFailure(request, response, e);
            }
            response.sendRedirect(((SocialAuthenticationRedirectException) e).getRedirectUrl());
            return;
        }

        super.onAuthenticationFailure(request, response, e);
	}
}
