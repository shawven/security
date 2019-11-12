
package com.github.shawven.security.oauth2;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer
@AutoConfigureAfter(TokenStoreConfiguration.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private UserDetailsService userDetailsService;

    private AuthenticationManager authenticationManager;

    private TokenStore tokenStore;

    private OAuth2Properties oAuth2Properties;

    private PasswordEncoder passwordEncoder;

    private AccessDeniedHandler accessDeniedHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private ClientDetailsService clientDetailsService;

    private JwtAccessTokenConverter jwtAccessTokenConverter;

    private TokenEnhancer jwtTokenEnhancer;

    public AuthorizationServerConfiguration(UserDetailsService userDetailsService,
                                            AuthenticationManager authenticationManager,
                                            TokenStore tokenStore,
                                            OAuth2Properties oAuth2Properties,
                                            PasswordEncoder passwordEncoder,
                                            AccessDeniedHandler accessDeniedHandler,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            ClientDetailsService clientDetailsService,
                                            @Autowired(required = false)
                                            JwtAccessTokenConverter jwtAccessTokenConverter,
                                            @Autowired(required = false)
                                            TokenEnhancer jwtTokenEnhancer) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenStore = tokenStore;
        this.oAuth2Properties = oAuth2Properties;
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.clientDetailsService = clientDetailsService;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.jwtTokenEnhancer = jwtTokenEnhancer;
    }

    /**
	 * 认证及token配置
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
                .tokenServices(tokenServices())
				.authenticationManager(authenticationManager)
                .pathMapping("/oauth/token", OAuth2Constants.DEFAULT_OAUTH_TOKEN_ENDPOINTS);


	}

	/**
	 * tokenKey的访问权限表达式配置
	 */
	@Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .addTokenEndpointAuthenticationFilter(clientAuthenticationFilter());
	}

	/**
	 * 客户端配置
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
        OAuth2ClientProperties[] clientArray = oAuth2Properties.getClients();
        if (ArrayUtils.isEmpty(clientArray)) {
			return;
		}
        for (OAuth2ClientProperties client : oAuth2Properties.getClients()) {
            if (client == null) {
                continue;
            }
            builder.withClient(client.getClientId())
                    .secret(passwordEncoder.encode(client.getClientSecret()))
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                    .accessTokenValiditySeconds(client.getAccessTokenValidateSeconds())
                    .refreshTokenValiditySeconds(client.getRefreshTokenValidateSeconds())
                    .autoApprove(true)
                    .scopes("all");
        }
	}


    /**
     * 客户端验证过滤器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientAuthenticationFilter clientAuthenticationFilter() {
        return new ClientAuthenticationFilter(clientDetailsService, passwordEncoder);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setClientDetailsService(clientDetailsService);

        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            jwtAccessTokenConverter.setSigningKey(oAuth2Properties.getJwtSigningKey());
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancers = new ArrayList<>();
            enhancers.add(jwtTokenEnhancer);
            enhancers.add(jwtAccessTokenConverter);
            enhancerChain.setTokenEnhancers(enhancers);
            tokenServices.setTokenEnhancer(enhancerChain);
        }
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
        return tokenServices;
    }

}
