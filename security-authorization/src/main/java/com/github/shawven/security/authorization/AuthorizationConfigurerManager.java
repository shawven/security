
package com.github.shawven.security.authorization;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Comparator;
import java.util.List;

/**
 * 授权信息管理器
 *
 * 用于收集系统中所有 ConfigProvider 并加载其配置
 */
public class AuthorizationConfigurerManager {

	private List<AuthorizationConfigureProvider> providers;

    public AuthorizationConfigurerManager(List<AuthorizationConfigureProvider> providers) {
        this.providers = providers;
    }

    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		boolean existAnyRequestConfig = false;
		String existAnyRequestConfigName = null;

        providers.sort(Comparator.comparing(AuthorizationConfigureProvider::isAnyRequest));
		for (AuthorizationConfigureProvider provider : providers) {
            provider.config(config);
            boolean anyRequest = provider.isAnyRequest();
            if (existAnyRequestConfig && anyRequest) {
				throw new RuntimeException("重复的anyRequest配置:" + existAnyRequestConfigName + ","
						+ provider.getClass().getSimpleName());
			} else if (anyRequest) {
				existAnyRequestConfig = true;
				existAnyRequestConfigName = provider.getClass().getSimpleName();
			}
		}
		if(!existAnyRequestConfig){
			config.anyRequest().authenticated();
		}
	}
}
