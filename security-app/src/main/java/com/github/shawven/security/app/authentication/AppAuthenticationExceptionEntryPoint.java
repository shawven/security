package com.github.shawven.security.app.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.MessageConstants;
import com.github.shawven.security.authorization.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shoven
 * @date 2018/11/2 10:48
 */

public class AppAuthenticationExceptionEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        String errorMessage = MessageConstants.UNAUTHORIZED.equals(e.getMessage())
                ? MessageConstants.REQUIRE_LOGIN
                : HttpStatus.UNAUTHORIZED.getReasonPhrase();

        int status = HttpStatus.UNAUTHORIZED.value();
        ResponseData rsp = new ResponseData()
                .setMessage(errorMessage)
                .setCode(status);

        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(rsp));
    }
}
