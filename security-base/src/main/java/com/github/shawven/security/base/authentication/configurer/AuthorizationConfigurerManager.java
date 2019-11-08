
package com.github.shawven.security.base.authentication.configurer;

import com.github.shawven.security.base.properties.SecurityProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 授权信息管理器
 *
 * 用于收集系统中所有 ConfigProvider 并加载其配置
 */
public class AuthorizationConfigurerManager {

	private List<AuthorizationConfigurerProvider> authorizationConfigurerProviders;

    private SecurityProperties securityProperties;

    public AuthorizationConfigurerManager(List<AuthorizationConfigurerProvider> authorizationConfigurerProviders,
                                          SecurityProperties securityProperties) {
        this.authorizationConfigurerProviders = authorizationConfigurerProviders;
        this.securityProperties = securityProperties;
    }

    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        configWhitelist(config);

		boolean existAnyRequestConfig = false;
		String existAnyRequestConfigName = null;

		for (AuthorizationConfigurerProvider authorizeConfigProvider : authorizationConfigurerProviders) {
			boolean currentIsAnyRequestConfig = authorizeConfigProvider.config(config);

			if (existAnyRequestConfig && currentIsAnyRequestConfig) {
				throw new RuntimeException("重复的anyRequest配置:" + existAnyRequestConfigName + ","
						+ authorizeConfigProvider.getClass().getSimpleName());
			} else if (currentIsAnyRequestConfig) {
				existAnyRequestConfig = true;
				existAnyRequestConfigName = authorizeConfigProvider.getClass().getSimpleName();
			}
		}
		if(!existAnyRequestConfig){
			config.anyRequest().authenticated();
		}
	}

	public void configWhitelist(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        String whitelistStr = securityProperties.getWhitelist();
        if (whitelistStr != null) {
            String[] whitelist = whitelistStr.split(",");
            whitelist = Arrays.stream(whitelist).filter(s -> !s.isEmpty()).toArray(String[]::new);
            config.antMatchers(whitelist).permitAll();
        }
    }
}
