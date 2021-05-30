package com.github.shawven.security.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shoven
 * @date 2019-10-28
 */
public interface LoginFailureHandler {

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, Exception exception);
}
