
package com.github.shawven.security.oauth2.autoconfigure;

import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.List;

/**
 * 资源服务器配置
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private ApplicationContext applicationContext;

    private AuthorizationConfigurerManager authorizationConfigurerManager;

    private AccessDeniedHandler appAccessDeniedHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private List<HttpSecuritySupportConfigurer> configurers;

    public ResourceServerConfiguration(ApplicationContext applicationContext,
                                       AuthorizationConfigurerManager authorizationConfigurerManager,
                                       AccessDeniedHandler appAccessDeniedHandler,
                                       AuthenticationEntryPoint authenticationEntryPoint,
                                       List<HttpSecuritySupportConfigurer> configurers) {
        this.applicationContext = applicationContext;
        this.authorizationConfigurerManager = authorizationConfigurerManager;
        this.appAccessDeniedHandler = appAccessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.configurers = configurers;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .expressionHandler(oAuth2WebSecurityExpressionHandler(applicationContext))
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(appAccessDeniedHandler);
    }



    @Override
    public void configure(HttpSecurity http) throws Exception {
        for (HttpSecuritySupportConfigurer configurer : configurers) {
             http.apply(configurer);
        }
        authorizationConfigurerManager.config(http.authorizeRequests());
    }


    public SecurityExpressionHandler<FilterInvocation> oAuth2WebSecurityExpressionHandler(
            ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler handler = new OAuth2WebSecurityExpressionHandler();
        handler.setApplicationContext(applicationContext);
        return handler;
    }
}
