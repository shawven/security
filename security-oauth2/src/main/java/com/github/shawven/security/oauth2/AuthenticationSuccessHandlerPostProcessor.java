
package com.github.shawven.security.oauth2;

/**
 * 社交过滤器的后处理器，用于在不同环境下个性化社交登录的配置
 */
@FunctionalInterface
public interface AuthenticationSuccessHandlerPostProcessor {

	/**
	 * @param handler
	 */
	void postProcess(OAuth2AuthenticationSuccessHandler handler);

}
