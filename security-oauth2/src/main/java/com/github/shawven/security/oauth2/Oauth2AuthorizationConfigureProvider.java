package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.verification.VerificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class Oauth2AuthorizationConfigureProvider implements AuthorizationConfigureProvider {


    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        String[] urls = {
//                OAuth2Constants.DEFAULT_PHONE_TOKEN_PROCESSING_URL,
//                OAuth2Constants.DEFAULT_OAUTH_TOKEN_ENDPOINTS,
        };
        config.antMatchers(Arrays.stream(urls).filter(StringUtils::isNotBlank).toArray(String[]::new)).permitAll();
        return false;
    }
}
