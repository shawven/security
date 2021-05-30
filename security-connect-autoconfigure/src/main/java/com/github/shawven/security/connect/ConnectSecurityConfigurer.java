package com.github.shawven.security.connect;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.connect.config.ConnectProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author Shoven
 * @date 2019-11-11
 */
public class ConnectSecurityConfigurer extends HttpSecurityConfigurer {

    private ConnectConfigurer connectConfigurer;
    private String signUpUrl;

    public ConnectSecurityConfigurer(ConnectConfigurer connectConfigurer) {
        this.connectConfigurer = connectConfigurer;
        this.signUpUrl = connectConfigurer.getSignupUrl();
    }

    @Override
    public void initHttp(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(signUpUrl).permitAll();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        connectConfigurer.configure(http);
    }

    @Override
    public HttpSecurity and() {
        return connectConfigurer.and();
    }

    @Override
    protected <T> T postProcess(T object) {
        return connectConfigurer.postProcess(object);
    }

}
