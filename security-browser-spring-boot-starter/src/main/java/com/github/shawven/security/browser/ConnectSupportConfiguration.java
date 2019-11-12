package com.github.shawven.security.browser;

import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Configuration
@ConditionalOnClass(ConnectAutoConfiguration.class)
public class ConnectSupportConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {
        return new BrowserConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                authenticationFailureHandler);
    }

}
