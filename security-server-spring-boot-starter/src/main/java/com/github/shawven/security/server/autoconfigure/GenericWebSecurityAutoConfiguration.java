package com.github.shawven.security.server.autoconfigure;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.List;

@Configuration(proxyBeanMethods = false)
class GenericWebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {


    private AccessDeniedHandler accessDeniedHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private List<HttpSecurityConfigurer> configurers;

    public GenericWebSecurityAutoConfiguration(AccessDeniedHandler accessDeniedHandler,
                                               AuthenticationEntryPoint authenticationEntryPoint,
                                               List<HttpSecurityConfigurer> configurers) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.configurers = configurers;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(new AnonymousAuthenticationProvider("default"))
                // N.B. exceptionHandling is duplicated in resources.configure() so that
                // it works
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable();

        for (HttpSecurityConfigurer configurer : configurers) {
            configurer.initHttp(http);
            http.apply(configurer);
        }

        if (configurers.isEmpty()) {
            // Add anyRequest() last as a fall back. Spring Security would
            // replace an existing anyRequest() matcher with this one, so to
            // avoid that we only add it if the user hasn't configured anything.
            http.authorizeRequests().anyRequest().authenticated();
        }

    }
}
