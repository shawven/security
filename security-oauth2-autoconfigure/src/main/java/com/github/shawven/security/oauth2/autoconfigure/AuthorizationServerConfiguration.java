
package com.github.shawven.security.oauth2.autoconfigure;

import com.github.shawven.security.oauth2.ClientCredentialsTokenEndpointFilterPostProcessor;
import com.github.shawven.security.oauth2.OAuth2ClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务器配置
 */
@Configuration(proxyBeanMethods = false)
@EnableAuthorizationServer
@AutoConfigureAfter(TokenStoreConfiguration.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private UserDetailsService userDetailsService;

    private ObjectProvider<AuthenticationManager> authenticationManagerProvider;

    private TokenStore tokenStore;

    private OAuth2Properties properties;

    private PasswordEncoder passwordEncoder;

    private AccessDeniedHandler accessDeniedHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private JwtAccessTokenConverter jwtAccessTokenConverter;

    private TokenEnhancer jwtTokenEnhancer;

    private DataSource dataSource;

    public AuthorizationServerConfiguration(UserDetailsService userDetailsService,
                                            ObjectProvider<AuthenticationManager> authenticationManagerProvider,
                                            TokenStore tokenStore,
                                            OAuth2Properties properties,
                                            PasswordEncoder passwordEncoder,
                                            AccessDeniedHandler accessDeniedHandler,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            ObjectProvider<JwtAccessTokenConverter> jwtAccessTokenConverterProvider,
                                            ObjectProvider<TokenEnhancer> jwtTokenEnhancerProvider,
                                            DataSource dataSource) {
        this.userDetailsService = userDetailsService;
        this.authenticationManagerProvider = authenticationManagerProvider;
        this.tokenStore = tokenStore;
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAccessTokenConverter = jwtAccessTokenConverterProvider.getIfAvailable();
        this.jwtTokenEnhancer = jwtTokenEnhancerProvider.getIfAvailable();
        this.dataSource = dataSource;
    }

    /**
	 * 认证及token配置
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManagerProvider.getIfAvailable())
                .userDetailsService(userDetailsService);

        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancers = new ArrayList<>();
            enhancers.add(jwtTokenEnhancer);
            enhancers.add(jwtAccessTokenConverter);
            enhancerChain.setTokenEnhancers(enhancers);

            endpoints
                    .tokenEnhancer(enhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
        }


	}

	/**
	 * tokenKey的访问权限表达式配置
	 */
	@Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .passwordEncoder(passwordEncoder)
                .addObjectPostProcessor(new ClientCredentialsTokenEndpointFilterPostProcessor(authenticationEntryPoint));

		security.tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);
	}

	/**
	 * 客户端配置
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        List<OAuth2ClientProperties> userClients = properties.getClients();
        if (userClients == null || userClients.isEmpty()) {
            clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
			return;
		}

        InMemoryClientDetailsServiceBuilder memoryClientDetailsServiceBuilder = clients.inMemory();
        for (OAuth2ClientProperties client : userClients) {
            if (client == null) {
                continue;
            }
            memoryClientDetailsServiceBuilder.withClient(client.getClientId())
                    .secret(passwordEncoder.encode(client.getClientSecret()))
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                    .accessTokenValiditySeconds(client.getAccessTokenValidateSeconds())
                    .refreshTokenValiditySeconds(client.getRefreshTokenValidateSeconds())
                    .autoApprove(true)
                    .scopes("all");
        }
	}
}
