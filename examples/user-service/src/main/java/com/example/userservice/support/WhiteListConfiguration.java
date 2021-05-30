package com.example.userservice.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Order(1)
@Configuration(proxyBeanMethods = false)
public class WhiteListConfiguration extends WebSecurityConfigurerAdapter {

    private String whiteList = "/user/register";

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers(getWhiteList())
                .and().authorizeRequests().anyRequest().permitAll()
                .and().csrf().disable();
    }

    public String[] getWhiteList() {
        String whitelistStr = whiteList;
        if (whitelistStr != null) {
            String[] whitelist = whitelistStr.split(",");
            return Arrays.stream(whitelist).filter(s -> !s.isEmpty()).toArray(String[]::new);

        }
        return new String[0];
    }
}
