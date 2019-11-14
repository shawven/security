
package com.github.shawven.security.verification;

import com.github.shawven.security.authorization.AuthenticationFilterProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 */
public class VerificationFilterProviderConfigurer extends AuthenticationFilterProviderConfigurer {

	private Filter verificationFilter;

    public VerificationFilterProviderConfigurer(Filter verificationFilter) {
        this.verificationFilter = verificationFilter;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(verificationFilter, LogoutFilter.class);
	}


}