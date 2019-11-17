
package com.github.shawven.security.verification.authentication;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.servlet.Filter;

/**
 * 校验码相关安全配置
 */
public class VerificationFilterProviderConfigurer extends HttpSecurityConfigurer {

	private Filter verificationFilter;

    public VerificationFilterProviderConfigurer(Filter verificationFilter) {
        this.verificationFilter = verificationFilter;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(verificationFilter, LogoutFilter.class);
	}


}
