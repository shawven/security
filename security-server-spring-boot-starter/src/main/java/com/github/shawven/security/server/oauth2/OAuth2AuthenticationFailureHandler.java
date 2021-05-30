
package com.github.shawven.security.server.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.server.LoginFailureHandler;
import com.github.shawven.security.authorization.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APP环境下认证失败处理器
 */
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

	private LoginFailureHandler loginFailureHandler;

    public OAuth2AuthenticationFailureHandler(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
        logger.debug(e.getMessage(), e);
        if (loginFailureHandler != null) {
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
        int status = HttpStatus.UNAUTHORIZED.value();
        ResponseData newResponse = new ResponseData()
                .setCode(status)
                .setMessage(e.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(newResponse));
	}
}
