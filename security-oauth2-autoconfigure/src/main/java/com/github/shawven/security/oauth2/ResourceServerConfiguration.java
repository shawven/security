
package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Collections;
import java.util.List;

/**
 * 资源服务器配置
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private AuthorizationConfigurerManager authorizationConfigurerManager;

    @Autowired
    private AccessDeniedHandler appAccessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired(required = false)
    private List<AuthenticationFilterProvider> providerConfigurers = Collections.emptyList();

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(appAccessDeniedHandler);
        super.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        for (AuthenticationFilterProvider configurer : providerConfigurers) {
            http.apply(configurer);
        }

        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(appAccessDeniedHandler);

        authorizationConfigurerManager.config(http.authorizeRequests());
    }
}
