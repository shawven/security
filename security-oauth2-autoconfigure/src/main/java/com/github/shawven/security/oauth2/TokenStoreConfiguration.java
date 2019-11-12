
package com.github.shawven.security.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;


@Configuration
public class TokenStoreConfiguration {

	/**
	 * 使用redis存储token的配置，只有在security.oauth2.tokenStore配置为redis时生效
	 *
	 */
	@Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnProperty(prefix = "app.security.oauth2", name = "tokenStore", havingValue = "redis")
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
	@ConditionalOnProperty(prefix = "app.security.oauth2", name = "tokenStore", havingValue = "jwt", matchIfMissing = true)
	public static class JwtConfig {

		private OAuth2Properties oAuth2Properties;

        public JwtConfig(OAuth2Properties oAuth2Properties) {
            this.oAuth2Properties = oAuth2Properties;
        }

        /**
		 * @return
		 */
        @Bean
		public TokenStore jwtTokenStore() {
			return new JwtTokenStore(jwtAccessTokenConverter());
		}

		/**
		 * @return
		 */
		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter(){
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	        converter.setSigningKey(oAuth2Properties.getJwtSigningKey());
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
