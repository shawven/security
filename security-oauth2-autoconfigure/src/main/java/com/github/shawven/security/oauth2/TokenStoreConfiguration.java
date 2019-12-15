
package com.github.shawven.security.oauth2;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;


@Configuration
public class TokenStoreConfiguration {

	/**
	 * 使用redis存储token的配置，只有在security.oauth2.tokenStore配置为redis时生效
	 *
	 */
	@Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnProperty(prefix = "app.security.oauth2", name = "token-store", havingValue = "redis")
	public static class RedisConfig {

		private RedisConnectionFactory redisConnectionFactory;

        public RedisConfig(RedisConnectionFactory redisConnectionFactory) {
            this.redisConnectionFactory = redisConnectionFactory;
        }

        /**
		 * @return
		 */
		@Bean
		public TokenStore redisTokenStore() {
			return new RedisTokenStore(redisConnectionFactory);
		}

	}

	/**
	 * 使用jwt时的配置，默认生效
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "app.security.oauth2", name = "token-store", havingValue = "jwt", matchIfMissing = true)
	public static class JwtConfig implements ApplicationContextAware {

	    private ApplicationContext context;

		private Oauth2JwtProperties jwt;

        public JwtConfig(OAuth2Properties properties) {
            this.jwt = properties.getJwt();
        }

        public void setApplicationContext(ApplicationContext context) throws BeansException {
            this.context = context;
        }

        /**
		 * @return
		 */
        @Bean
		public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
			return new JwtTokenStore(jwtAccessTokenConverter);
		}

        @Bean
        @ConditionalOnProperty(prefix = "app.security.oauth2.jwt",name = "key-store")
        @ConditionalOnMissingBean
        public JwtAccessTokenConverter jwtKeyStoreAccessTokenConverter() {
            Objects.requireNonNull(jwt.getKeyStore(), "keyStore cannot be null");
            Objects.requireNonNull(jwt.getKeyStorePassword(), "keyStorePassword cannot be null");
            Objects.requireNonNull(jwt.getKeyAlias(), "keyAlias cannot be null");

            Resource keyStore = this.context.getResource(jwt.getKeyStore());
            char[] keyStorePassword = jwt.getKeyStorePassword().toCharArray();
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyStore, keyStorePassword);
            char[] keyPassword = Optional.ofNullable(jwt.getKeyPassword())
                    .map(String::toCharArray)
                    .orElse(keyStorePassword);
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setKeyPair(keyStoreKeyFactory.getKeyPair(jwt.getKeyAlias(), keyPassword));
            return converter;
        }

        /**
         * @return
         */
        @Bean
        @ConditionalOnProperty(prefix = "app.security.oauth2.jwt",name = "key-store", matchIfMissing = true)
        @ConditionalOnMissingBean
        public JwtAccessTokenConverter jwtSigningKeyAccessTokenConverter(){
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            Objects.requireNonNull(jwt.getSigningKey(), "SigningKey cannot be null");
            converter.setSigningKey(jwt.getSigningKey());
            return converter;
        }

		/**
		 * @return
		 */
		@Bean
		@ConditionalOnBean(TokenEnhancer.class)
		public TokenEnhancer jwtTokenEnhancer(){
			return new TokenJwtEnhancer();
		}
	}
}
