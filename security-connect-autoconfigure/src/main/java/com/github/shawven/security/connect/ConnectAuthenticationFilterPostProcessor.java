
package com.github.shawven.security.connect;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * 社交过滤器的后处理器，用于在不同环境下个性化社交登录的配置
 */
public interface ConnectAuthenticationFilterPostProcessor {

	/**
	 * @param socialAuthenticationFilter
	 */
	void postProcess(SocialAuthenticationFilter socialAuthenticationFilter);

}
