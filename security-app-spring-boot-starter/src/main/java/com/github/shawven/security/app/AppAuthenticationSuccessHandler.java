package com.github.shawven.security.app;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shoven
 * @date 2020-03-14
 */
public class AppAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private AppLoginSuccessHandler loginSuccessHandler;


    public AppAuthenticationSuccessHandler(AppLoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (loginSuccessHandler != null) {
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
