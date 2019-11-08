package com.github.shawven.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Shoven
 * @date 2019-10-28
 */
@Configuration
@EnableConfigurationProperties(AuthorizationProperties.class)
public class AuthorizationAutoConfiguration {

    @Autowired
    private AuthorizationProperties properties;

    @Autowired
    private List<AuthorizationConfigureProvider> authorizationConfigureProviders;

    @Bean
    public AuthorizationConfigurerManager authorizationConfigurerManager() {
        return new AuthorizationConfigurerManager(authorizationConfigureProviders, authorizationConfiguration());
    }

    @Bean
    public AuthorizationConfiguration authorizationConfiguration() {
        AuthorizationConfiguration cfg = new AuthorizationConfiguration();
        cfg.setWhitelist(properties.getWhitelist());
        return cfg;
    }
}
