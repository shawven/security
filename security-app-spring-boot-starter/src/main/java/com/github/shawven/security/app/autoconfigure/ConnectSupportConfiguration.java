package com.github.shawven.security.app.autoconfigure;

import com.github.shawven.security.app.AppAdaptorOAuth2AuthticationHandler;
import com.github.shawven.security.app.authentication.AppLoginFailureHandler;
import com.github.shawven.security.app.authentication.AppConnectAuthenticationFailureHandler;
import com.github.shawven.security.app.authentication.AppLoginSuccessHandler;
import com.github.shawven.security.app.connect.AppConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.app.connect.AppConnectConfigurerProcessor;
import com.github.shawven.security.app.openid.OpenIdFilterProvider;
import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.connect.ConnectConfigurerProcessor;
import com.github.shawven.security.oauth2.OAuth2AuthenticationSuccessHandlerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Configuration
@ConditionalOnClass({ConnectAutoConfiguration.class, RedisTemplate.class})
public class ConnectSupportConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConnectConfigurerProcessor appConnectConfigurerProcessor() {
        return new AppConnectConfigurerProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
            AuthenticationSuccessHandler authenticationSuccessHandler,
            @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {

        return new AppConnectAuthenticationFilterPostProcessor(authenticationSuccessHandler,
                new AppConnectAuthenticationFailureHandler(loginFailureHandler));
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFilterProvider openIdFilterProvider() {
        return new OpenIdFilterProvider();
    }


    @Configuration
    @ConditionalOnClass()
    public static class ConnectAdaptOAuth2Configuration {

        @Bean
        @ConditionalOnMissingBean
        public AuthenticationSuccessHandler authenticationSuccessHandler(
                OAuth2AuthenticationSuccessHandlerAdaptor authenticationSuccessHandlerAdaptor,
                @Autowired(required = false) AppLoginSuccessHandler loginSuccessHandler,
                @Autowired(required = false) AppLoginFailureHandler loginFailureHandler) {
            return authenticationSuccessHandlerAdaptor.adapt(
                    new AppAdaptorOAuth2AuthticationHandler(loginSuccessHandler, loginFailureHandler));
        }
    }
}
