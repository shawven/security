
package com.github.shawven.security.app.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APP环境下登录成功的处理器
 *
 * @author Shoven
 * @since 2019-05-08 21:55
 */
public class AppAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private ObjectMapper objectMapper;

	private AppLoginSuccessHandler loginSuccessHandler;

    public AppAuthenticationSuccessHandler(AppLoginSuccessHandler loginSuccessHandler) {
        this.objectMapper = new ObjectMapper();
        this.loginSuccessHandler = loginSuccessHandler;
    }


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
        if (loginSuccessHandler != null) {
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }

        ResponseData result = new ResponseData("登录成功");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(objectMapper.writeValueAsString(result));

	}

}
