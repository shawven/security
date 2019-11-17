package com.example.demo;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.net.UnknownHostException;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public AuthorizationConfigureProvider authorizationConfigurerProvider() {
        return new AuthorizationConfigureProvider() {
            @Override
            public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
                return true;
            }
        };
    }

}
