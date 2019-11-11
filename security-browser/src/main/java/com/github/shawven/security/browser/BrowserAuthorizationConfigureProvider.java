package com.github.shawven.security.browser;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;

/**
 * @author Shoven
 * @date 2019-08-20
 */
public class BrowserAuthorizationConfigureProvider implements AuthorizationConfigureProvider {

    private BrowserConfiguration browserConfiguration;

    public BrowserAuthorizationConfigureProvider(BrowserConfiguration browserConfiguration) {
        this.browserConfiguration = browserConfiguration;
    }

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        String[] urls = {
                browserConfiguration.getSignInUrl(),
                browserConfiguration.getSignUpUrl(),
                browserConfiguration.getSession().getSessionInvalidUrl(),
                browserConfiguration.getSignOutSuccessUrl()
        };
        config.antMatchers(Arrays.stream(urls).filter(StringUtils::isNotBlank).toArray(String[]::new)).permitAll();

        return false;
    }
}
