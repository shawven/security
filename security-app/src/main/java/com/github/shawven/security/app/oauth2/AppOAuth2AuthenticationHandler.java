package com.github.shawven.security.app.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.app.AppLoginFailureHandler;
import com.github.shawven.security.app.AppLoginSuccessHandler;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.oauth2.AdaptedAuthenticationHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shoven
 * @date 2019-11-12
 */
public class AppOAuth2AuthenticationHandler implements AdaptedAuthenticationHandler {

    private ObjectMapper objectMapper;

    private AppLoginSuccessHandler loginSuccessHandler;
    private AppLoginFailureHandler loginFailureHandler;

    public AppOAuth2AuthenticationHandler(AppLoginSuccessHandler loginSuccessHandler,
                                          AppLoginFailureHandler loginFailureHandler) {
        this.objectMapper =  new ObjectMapper();
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
    public void onSuccess(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication, OAuth2AccessToken token) throws IOException, ServletException {
        boolean isRefresh = "refresh_token".equals(request.getParameter("grant_type"));
        ResponseData data = isRefresh ? Responses.refreshTokenSuccess() : Responses.getTokenSuccess();
        data.setData(token);
        if (loginSuccessHandler != null) {
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
        output(response, data);
    };

    @Override
    public void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        ResponseData result = new ResponseData()
                .setCode(HttpStatus.UNAUTHORIZED.value())
                .setMessage(e.getMessage());
        if (loginFailureHandler != null) {
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
        output(response, result);
    }

    private void output(HttpServletResponse response, ResponseData tokenResult) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResult));
    }
}
