package com.github.shawven.security.app;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shoven
 * @date 2019-05-10
 */
public interface AppLoginSuccessHandler {

    void onAuthenticationSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication);
}
