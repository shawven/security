package com.github.shawven.security.app;

import com.github.shawven.security.app.config.AppConfiguration;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;

/**
 * @author Shoven
 * @date 2019-08-20
 */
public class AppAuthorizationConfigureProvider implements AuthorizationConfigureProvider {

    private AppConfiguration configuration;

    public AppAuthorizationConfigureProvider(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        String[] urls = {
                configuration.getSignInProcessingUrl(),
                configuration.getSignOutProcessingUrl(),
        };
        config.antMatchers(Arrays.stream(urls).filter(StringUtils::isNotBlank).toArray(String[]::new)).permitAll();
        return false;
    }
}
