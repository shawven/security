package com.github.shawven.security.browser.authentication;

import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shoven
 * @date 2019-05-10
 */
public interface BrowserLoginSuccessHandler {

    void onAuthenticationSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException, ServletException;
}
