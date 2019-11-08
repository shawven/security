package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.verification.VerificationConstants;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class Oauth2AuthorizationConfigureProvider implements AuthorizationConfigureProvider {


    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config.antMatchers(
                OAuth2Constants.DEFAULT_PHONE_TOKEN_PROCESSING_URL,
                VerificationConstants.DEFAULT_VERIFICATION_URL_PREFIX + "/*"
        ).permitAll();
        return false;
    }
}
