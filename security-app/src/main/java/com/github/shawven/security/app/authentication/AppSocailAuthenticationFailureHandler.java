
package com.github.shawven.security.app.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.social.security.SocialAuthenticationRedirectException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 社交登陆APP环境下认证失败处理器
 */
public class AppSocailAuthenticationFailureHandler extends AppAuthenticationFailureHandler {

    private AppLoginFailureHandler loginFailureHandler;

    public AppSocailAuthenticationFailureHandler(AppLoginFailureHandler loginFailureHandler) {
        super(loginFailureHandler);
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {

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
