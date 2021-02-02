
package com.github.shawven.security.browser.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.authentication.BrowserLoginFailureHandler;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationRedirectException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 社交登陆APP环境下认证失败处理器
 */
public class BrowserConnectAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(BrowserConnectAuthenticationFailureHandler.class);

    private BrowserConfiguration configuration;

    private BrowserLoginFailureHandler loginFailureHandler;

    public BrowserConnectAuthenticationFailureHandler(BrowserConfiguration configuration,
                                                      BrowserLoginFailureHandler loginFailureHandler) {
        this.configuration = configuration;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
        logger.debug(e.getMessage(), e);
        if (loginFailureHandler != null) {
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
        if (e instanceof SocialAuthenticationRedirectException) {
            response.sendRedirect(((SocialAuthenticationRedirectException) e).getRedirectUrl());
            return;
        }
        if (ResponseType.JSON.equals(configuration.getResponseType())) {
            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseData newResponse = new ResponseData()
                    .setCode(status)
                    .setMessage(e.getMessage());

            response.setStatus(status);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(newResponse));
        } else{
            super.onAuthenticationFailure(request, response, e);
        }
	}
}
