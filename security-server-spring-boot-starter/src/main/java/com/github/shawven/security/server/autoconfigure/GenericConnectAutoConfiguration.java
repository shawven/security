package com.github.shawven.security.server.autoconfigure;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.connect.ConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.RedisSignInUtils;
import com.github.shawven.security.server.GenericConnectEndpoint;
import com.github.shawven.security.server.LoginFailureHandler;
import com.github.shawven.security.server.connect.ConnectAuthenticationFailureHandler;
import com.github.shawven.security.server.connect.GenericConnectAuthenticationFilterPostProcessor;
import com.github.shawven.security.server.connect.openid.OpenIdSecurityConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUserDetailsService;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisTemplate.class})
class GenericConnectAutoConfiguration {

    private static String processingUrl = ConnectConstants.DEFAULT_OPENID_TOKEN_PROCESSING_URL;

    @Bean
    @ConditionalOnMissingBean
    public GenericConnectEndpoint genericConnectEndpoint(RedisSignInUtils redisSignInUtils,
                                                     ProviderSignInUtils providerSignInUtils,
                                                     UsersConnectionRepository usersConnectionRepository,
                                                     ConnectionRepository connectionRepository,
                                                     ConnectionFactoryLocator connectionFactoryLocator) {
        return new GenericConnectEndpoint(redisSignInUtils, providerSignInUtils,
                usersConnectionRepository, connectionRepository, connectionFactoryLocator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor(
            ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
            ObjectProvider<LoginFailureHandler> loginFailureHandlerProvider) {
        return new GenericConnectAuthenticationFilterPostProcessor(authenticationSuccessHandlerProvider.getIfAvailable(),
                new ConnectAuthenticationFailureHandler(loginFailureHandlerProvider.getIfAvailable()));
    }

    /**
     *
     * OpenId直接验证支持
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "openIdSecurityConfigurer")
    public HttpSecurityConfigurer openIdSecurityConfigurer(
            ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
            AuthenticationFailureHandler authenticationFailureHandler,
            SocialUserDetailsService userDetailsService,
            UsersConnectionRepository usersConnectionRepository) {
        return new OpenIdSecurityConfigurer(processingUrl, authenticationSuccessHandlerProvider,
                authenticationFailureHandler, userDetailsService, usersConnectionRepository);
    }
}
