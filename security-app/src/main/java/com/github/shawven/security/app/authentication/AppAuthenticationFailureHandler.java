
package com.github.shawven.security.app.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
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
public class AppAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private ObjectMapper objectMapper;

	private AppLoginFailureHandler loginFailureHandler;

    public AppAuthenticationFailureHandler(AppLoginFailureHandler loginFailureHandler) {
        this.objectMapper = new ObjectMapper();
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
        if (loginFailureHandler != null) {
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
        int status = HttpStatus.BAD_REQUEST.value();
        ResponseData newResponse = new ResponseData()
                .setCode(status)
                .setMessage(e.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(newResponse));
	}
}
