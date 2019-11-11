package com.github.shawven.security.browser;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * @author Shoven
 * @date 2019-11-11
 */
public interface BrowserWebSecurityConfigurer {

    default void configure(WebSecurity web) throws Exception { }

    default void configure(HttpSecurity http) throws Exception { }
}
