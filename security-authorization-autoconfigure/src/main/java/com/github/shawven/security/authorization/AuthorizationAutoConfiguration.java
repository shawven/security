package com.github.shawven.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
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

    @Autowired(required = false)
    private List<AuthorizationConfigureProvider> providers = Collections.emptyList();

    @Bean
    public AuthorizationConfigurerManager authorizationConfigurerManager() {
        return new AuthorizationConfigurerManager(providers, authorizationConfiguration());
    }

    @Bean
    public AuthorizationConfiguration authorizationConfiguration() {
        AuthorizationConfiguration cfg = new AuthorizationConfiguration();
        cfg.setWhitelist(properties.getWhitelist());
        return cfg;
    }
}
