
package com.github.shawven.security.authorization;

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

	private List<AuthorizationConfigureProvider> providers;

    private AuthorizationConfiguration configuration;

    public AuthorizationConfigurerManager(List<AuthorizationConfigureProvider> providers,
                                          AuthorizationConfiguration configuration) {
        this.providers = providers;
        this.configuration = configuration;
    }

    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        configWhitelist(config);

		boolean existAnyRequestConfig = false;
		String existAnyRequestConfigName = null;

		for (AuthorizationConfigureProvider authorizeConfigProvider : providers) {
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
        String whitelistStr = configuration.getWhitelist();
        if (whitelistStr != null) {
            String[] whitelist = whitelistStr.split(",");
            whitelist = Arrays.stream(whitelist).filter(s -> !s.isEmpty()).toArray(String[]::new);
            config.antMatchers(whitelist).permitAll();
        }
    }
}
