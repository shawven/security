package com.github.shawven.security.app.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * web安全配置
 *
 * @author Shoven
 * @since 2019-05-09 15:33
 */
@Configuration
public class AppWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .anyRequest().authenticated().and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();

    }
}
