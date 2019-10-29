
package com.github.shawven.security.verification.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 */
public class VerificationSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private Filter verificationFilter;

    public VerificationSecurityConfigurer(Filter verificationFilter) {
        this.verificationFilter = verificationFilter;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(verificationFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}

}
