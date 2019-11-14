package com.github.shawven.security.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shoven
 * @date 2019-10-28
 */
public interface AppLoginFailureHandler {

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, Exception exception);
}
