package com.github.shawven.security.connect;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class ConnectAuthorizationConfigureProvider implements AuthorizationConfigureProvider {

    private ConnectConfiguration configuration;

    public ConnectAuthorizationConfigureProvider(ConnectConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        String[] urls = {
                ConnectConstants.DEFAULT_CURRENT_USER_INFO_URL,
                configuration.getFilterProcessesUrl() + "/**",
        };
        config.antMatchers(Arrays.stream(urls).filter(StringUtils::isNotBlank).toArray(String[]::new)).permitAll();
        return false;
    }
}
