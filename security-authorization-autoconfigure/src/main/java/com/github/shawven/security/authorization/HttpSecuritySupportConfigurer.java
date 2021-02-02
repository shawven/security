package com.github.shawven.security.authorization;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 *  HttpSecurity支持配置器，会apply到HttpSecurity
 *
 * @author Shoven
 * @date 2019-11-11
 */
public class HttpSecuritySupportConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

}
