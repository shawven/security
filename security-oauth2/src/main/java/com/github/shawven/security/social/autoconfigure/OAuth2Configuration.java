
package com.github.shawven.security.social.autoconfigure;

import com.github.shawven.security.social.DefaultPhoneUserDetailsService;
import com.github.shawven.security.social.PhoneUserDetailsService;
import com.github.shawven.security.social.config.SmsAuthenticationSecurityConfigurer;
import com.github.shawven.security.social.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 *
 * 认证相关的扩展点配置。配置在这里的bean，业务系统都可以通过声明同类型或同名的bean来覆盖安全
 * 模块默认的配置。
 */
@Configuration
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2Configuration {

	/**
	 * 客户端密码处理器
	 * @return
	 */
	@Bean
    @ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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

	@Bean
    public SmsAuthenticationSecurityConfigurer smsAuthenticationSecurityConfigurer(
            PhoneUserDetailsService userDetailsService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler
            ) {
	    return new SmsAuthenticationSecurityConfigurer(userDetailsService,
                authenticationSuccessHandler, authenticationFailureHandler);
    }
}