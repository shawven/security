
package com.github.shawven.security.verification;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 */
public class VerificationFilterProvider extends AuthenticationFilterProvider {

	private Filter verificationFilter;

    public VerificationFilterProvider(Filter verificationFilter) {
        this.verificationFilter = verificationFilter;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(verificationFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}


}
