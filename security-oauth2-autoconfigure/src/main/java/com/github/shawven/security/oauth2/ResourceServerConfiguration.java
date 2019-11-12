
package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.oauth2.phone.PhoneFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    private AuthorizationConfigurerManager authorizationConfigurerManager;

    private AccessDeniedHandler appAccessDeniedHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private List<AuthenticationFilterProvider> providerConfigurers;

    public ResourceServerConfiguration(AuthorizationConfigurerManager authorizationConfigurerManager,
                                       AccessDeniedHandler appAccessDeniedHandler,
                                       AuthenticationEntryPoint authenticationEntryPoint,
                                       @Autowired(required = false)
                                       List<AuthenticationFilterProvider> providerConfigurers) {
        this.authorizationConfigurerManager = authorizationConfigurerManager;
        this.appAccessDeniedHandler = appAccessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.providerConfigurers = providerConfigurers != null ? providerConfigurers : Collections.emptyList();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(appAccessDeniedHandler);
        super.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 手机号码验证单独提出来了
        providerConfigurers.removeIf(provider -> provider instanceof PhoneFilterProvider);
        for (AuthenticationFilterProvider configurer : providerConfigurers) {
             http.apply(configurer);
        }

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(appAccessDeniedHandler);

        authorizationConfigurerManager.config(http.authorizeRequests());
    }

}
