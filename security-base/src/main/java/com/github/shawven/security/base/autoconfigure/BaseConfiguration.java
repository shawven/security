package com.github.shawven.security.base.autoconfigure;

import com.github.shawven.security.base.authentication.configurer.AuthorizationConfigurerManager;
import com.github.shawven.security.base.authentication.configurer.AuthorizationConfigurerProvider;
import com.github.shawven.security.base.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Shoven
 * @date 2019-10-28
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class BaseConfiguration {

    @Bean
    public AuthorizationConfigurerManager authorizationConfigurerManager(SecurityProperties securityProperties,
                                                                         List<AuthorizationConfigurerProvider> authorizationConfigurerProviders) {
        return new AuthorizationConfigurerManager(authorizationConfigurerProviders, securityProperties);
    }
}
