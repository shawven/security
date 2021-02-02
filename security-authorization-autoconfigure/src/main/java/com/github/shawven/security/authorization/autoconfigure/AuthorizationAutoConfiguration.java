package com.github.shawven.security.authorization.autoconfigure;

import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.authorization.AuthorizationConfigurerManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-10-28
 */
@Configuration
@EnableConfigurationProperties(AuthorizationProperties.class)
public class AuthorizationAutoConfiguration {

    private List<AuthorizationConfigureProvider> providers;

    public AuthorizationAutoConfiguration(List<AuthorizationConfigureProvider> providers) {
        this.providers = providers;
    }

    @Bean
    public AuthorizationConfigurerManager authorizationConfigurerManager() {
        return new AuthorizationConfigurerManager(providers);
    }

    @Configuration
    @Order(2)
    public static class WhiteListConfiguration extends WebSecurityConfigurerAdapter {

        private AuthorizationProperties properties;

        public WhiteListConfiguration(AuthorizationProperties properties) {
            this.properties = properties;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().mvcMatchers("/error");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers().antMatchers(getWhiteList())
                    .and().authorizeRequests().anyRequest().permitAll()
                    .and().csrf().disable();
        }

        public String[] getWhiteList() {
            String whitelistStr = properties.getWhitelist();
            if (whitelistStr != null) {
                String[] whitelist = whitelistStr.split(",");
                return Arrays.stream(whitelist).filter(s -> !s.isEmpty()).toArray(String[]::new);

            }
            return new String[0];
        }
    }
}
