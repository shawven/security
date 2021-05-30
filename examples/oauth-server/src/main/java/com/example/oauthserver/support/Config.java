package com.example.oauthserver.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class Config {

    @Bean
    public ConnectionSignUp connectionSignUp() {
        return connection -> {
            return "1";
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
