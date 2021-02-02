
package com.github.shawven.security.oauth2.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * 认证相关的扩展点配置。配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 *
 * @author Shoven
 * @date 2019-11-08
 */
@Configuration
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2AutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public static class BaseConfiguration {

        /**
         * 客户端密码处理器
         * @return
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
