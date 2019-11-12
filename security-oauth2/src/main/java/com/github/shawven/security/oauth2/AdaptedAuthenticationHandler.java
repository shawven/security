package com.github.shawven.security.oauth2;

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
public interface AdaptedAuthenticationHandler {

    void onSuccess(HttpServletRequest request, HttpServletResponse response,
                   Authentication authentication, OAuth2AccessToken token) throws IOException, ServletException;

    void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException;
}
