package com.github.shawven.security.server.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.server.LoginFailureHandler;
import com.github.shawven.security.server.LoginSuccessHandler;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

/**
 * @author Shoven
 * @date 2019-11-12
 */
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper;

    private LoginSuccessHandler loginSuccessHandler;

    private LoginFailureHandler loginFailureHandler;

    private PasswordEncoder passwordEncoder;

    private ClientDetailsService clientDetailsService;

    private AuthorizationServerTokenServices tokenServices;

    private OAuth2AuthenticationDetailsSource detailsSource ;

    public OAuth2AuthenticationSuccessHandler(ClientDetailsService clientDetailsService,
                                              AuthorizationServerTokenServices tokenServices,
                                              PasswordEncoder passwordEncoder,
                                              LoginSuccessHandler loginSuccessHandler,
                                              LoginFailureHandler loginFailureHandler) {
        this.detailsSource = new OAuth2AuthenticationDetailsSource();
        this.objectMapper = new ObjectMapper();
        this.clientDetailsService = clientDetailsService;
        this.tokenServices = tokenServices;
        this.passwordEncoder = passwordEncoder;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String[] clientInfo = getClientInfo(request);

        String clientId = clientInfo[0];
        String clientSecret = clientInfo[1];
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            outputUnauthorized(response, Responses.noSuchClient());
            return;
        }
        if (!authenticateClient(passwordEncoder, clientDetails.getClientSecret(), clientSecret)) {
            outputUnauthorized(response, Responses.badClientCredentials());
            return;
        }

        TokenRequest tokenRequest = new TokenRequest(Collections.emptyMap(), clientId,
                clientDetails.getScope(), "custom");
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        try {
            OAuth2AccessToken token = tokenServices.createAccessToken(oAuth2Authentication);
            request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, token.getValue());
            request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, token.getTokenType());
            oAuth2Authentication.setDetails(detailsSource.buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);

            onSuccess(request, response, oAuth2Authentication, token);
        } catch (AuthenticationException e) {
            onFailure(request, response, e);
        }
    }

    public void onSuccess(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication, OAuth2AccessToken token) throws IOException, ServletException {
        boolean isRefresh = "refresh_token".equals(request.getParameter("grant_type"));
        ResponseData data = isRefresh ? Responses.refreshTokenSuccess() : Responses.getTokenSuccess();
        data.setData(token);
        output(response, data);
        if (loginSuccessHandler != null) {
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }

    }

    public void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        ResponseData result = new ResponseData()
                .setCode(HttpStatus.UNAUTHORIZED.value())
                .setMessage(e.getMessage());
        outputUnauthorized(response, result);
        if (loginFailureHandler != null) {
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    private String[] getClientInfo(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        if (clientId != null && clientId.length() > 0) {
            return new String[]{clientId, clientSecret};
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            return new String[2];
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (Exception e) {
            return new String[2];
        }
        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        return new String[] {token.substring(0, delim), token.substring(delim + 1) };
    }


    private boolean authenticateClient(PasswordEncoder passwordEncoder, String storedSecret, String inputSecret)
            throws BadCredentialsException {
        storedSecret = storedSecret == null ? "" : storedSecret;
        inputSecret = inputSecret == null ? "" : inputSecret;
        if (StringUtils.isBlank(inputSecret) && StringUtils.isBlank(storedSecret)) {
            return true;
        }
        return passwordEncoder.matches(inputSecret,storedSecret);
    }


    private void output(HttpServletResponse response, ResponseData tokenResult) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResult));
    }

    private void outputUnauthorized(HttpServletResponse response, ResponseData data) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json,charset=utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }
}
