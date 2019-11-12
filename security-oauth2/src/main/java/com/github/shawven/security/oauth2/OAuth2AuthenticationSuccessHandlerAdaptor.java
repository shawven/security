package com.github.shawven.security.oauth2;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Shoven
 * @date 2019-11-12
 */
public class OAuth2AuthenticationSuccessHandlerAdaptor implements AuthenticationSuccessHandler {

    private PasswordEncoder passwordEncoder;

    private ClientDetailsService clientDetailsService;

    private AuthorizationServerTokenServices authorizationServerTokenServices;

    private AdaptedAuthenticationHandler handler;

    public OAuth2AuthenticationSuccessHandlerAdaptor(ClientDetailsService clientDetailsService,
                                                 PasswordEncoder passwordEncoder,
                                                 AuthorizationServerTokenServices authorizationServerTokenServices) {
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authorizationServerTokenServices = authorizationServerTokenServices;
    }

    public AuthenticationSuccessHandler adapt(AdaptedAuthenticationHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String[] clientInfo;

        try {
            clientInfo = ClientUtils.getClientInfo(request);
            if (clientInfo == null) {
                ClientUtils.outputAbsentClient(response);
                return;
            }
        } catch (RuntimeException e) {
            ClientUtils.outputErrorInfo(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        }

        String clientId = clientInfo[0];
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        try {
            ClientUtils.authenticateClient(passwordEncoder, clientDetails, clientInfo);
        } catch (Exception e) {
            ClientUtils.outputBadCredentials(response, e);
        }

        TokenRequest tokenRequest = new TokenRequest(Collections.emptyMap(),
                clientId, clientDetails.getScope(), "custom");

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        try {

            OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            if (handler != null) {
                handler.onSuccess(request, response, authentication, token);
            }
        } catch (AuthenticationException e) {
            if (handler != null) {
                handler.onFailure(request, response, e);
            }

        }
    }
}
