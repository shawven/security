package com.github.shawven.security.connect;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

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
        config.antMatchers(configuration.getSignUpUrl()).permitAll();
        return false;
    }
}
