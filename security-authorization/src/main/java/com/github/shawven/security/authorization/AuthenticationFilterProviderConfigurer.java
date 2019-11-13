package com.github.shawven.security.authorization;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * 验证过滤器提供者，实际就是apply应用的目标
 *  void configure(HttpSecurity http) {
 *      http.apply( )
 *  }
 *
 *
 * @author Shoven
 * @date 2019-11-11
 */
public class AuthenticationFilterProviderConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

}
