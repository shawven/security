package com.github.shawven.security.verification;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * @author Shoven
 * @date 2019-11-12
 */
public class VerificationAuthorizationConfigureProvider implements AuthorizationConfigureProvider {

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config.antMatchers(VerificationConstants.DEFAULT_VERIFICATION_URL_PREFIX + "/*").permitAll();
        return false;
    }
}
