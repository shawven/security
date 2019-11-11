package com.github.shawven.security.connect;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author Shoven
 * @date 2019-11-11
 */
public class ConnectFilterProvider extends AuthenticationFilterProvider {

    private ConnectConfigurer connectConfigurer;

    public ConnectFilterProvider(ConnectConfigurer connectConfigurer) {
        this.connectConfigurer = connectConfigurer;
    }

    @Override
    public void init(HttpSecurity builder) throws Exception {
        connectConfigurer.init(builder);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        connectConfigurer.init(builder);
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
