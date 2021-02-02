
package com.github.shawven.security.verification.security;

import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 */
public class VerificationSecuritySupportConfigurer extends HttpSecuritySupportConfigurer {

	private Filter verificationFilter;

    public VerificationSecuritySupportConfigurer(Filter verificationFilter) {
        this.verificationFilter = verificationFilter;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(verificationFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}

}
