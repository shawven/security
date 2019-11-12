package com.github.shawven.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@Configuration
public class OAuth2SupportableConfiguration {

    private ClientDetailsService clientDetailsService;

    private PasswordEncoder passwordEncoder;

    private AuthorizationServerTokenServices services;

    public OAuth2SupportableConfiguration(ClientDetailsService clientDetailsService,
                                          PasswordEncoder passwordEncoder,
                                          AuthorizationServerTokenServices services) {
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.services = services;
    }

    @Bean
    public OAuth2AuthenticationSuccessHandlerAdaptor authenticationSuccessHandlerAdaptor() {
        return new OAuth2AuthenticationSuccessHandlerAdaptor(clientDetailsService, passwordEncoder, services);
    }
}
