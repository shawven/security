package com.github.shawven.security.authorization;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 *  HttpSecurity支持配置器，会apply到HttpSecurity
 *
 * @author Shoven
 * @date 2019-11-11
 */
public class HttpSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public void initHttp(HttpSecurity http) throws Exception {

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

    }
}
