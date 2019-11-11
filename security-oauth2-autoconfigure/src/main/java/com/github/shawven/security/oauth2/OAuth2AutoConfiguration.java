
package com.github.shawven.security.oauth2;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.oauth2.sms.SmsFilterProvider;
import com.github.shawven.security.verification.DefaultPhoneUserDetailsService;
import com.github.shawven.security.verification.PhoneUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

	/**
	 * 客户端密码处理器
	 * @return
	 */
	@Bean
    @ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@ConditionalOnClass(PhoneUserDetailsService.class)
	public static class SmsSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AuthenticationFilterProvider smsFilterProvider(PhoneUserDetailsService userDetailsService,
                                                              AuthenticationSuccessHandler authenticationSuccessHandler,
                                                              AuthenticationFailureHandler authenticationFailureHandler) {
            return new SmsFilterProvider(userDetailsService, authenticationSuccessHandler,
                    authenticationFailureHandler);
        }

        /**
         * 默认认证器
         *
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public PhoneUserDetailsService phoneUserDetailsService() {
            return new DefaultPhoneUserDetailsService();
        }
    }


    @Bean
    @Order(Integer.MIN_VALUE)
    @ConditionalOnBean
    public AuthorizationConfigureProvider oauth2AuthorizationConfigureProvider() {
	    return new Oauth2AuthorizationConfigureProvider();
    }
}
