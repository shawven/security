package com.github.shawven.security.app;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

public interface AppWebSecurityConfigurer {

    default void configure(WebSecurity web) throws Exception { }

    default void configure(HttpSecurity http) throws Exception { }
}
