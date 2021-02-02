
package com.github.shawven.security.connect;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 继承默认的社交登录配置，加入自定义的后处理逻辑
 */
public class ConnectConfigurer extends SpringSocialConfigurer {

	private String filterProcessesUrl;

	private ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor;


	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		if (filterProcessesUrl != null) {
            filter.setFilterProcessesUrl(filterProcessesUrl);
        }
		if (connectAuthenticationFilterPostProcessor != null) {
			connectAuthenticationFilterPostProcessor.postProcess(filter);
		}
		return (T) filter;
	}

	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	public ConnectAuthenticationFilterPostProcessor getConnectAuthenticationFilterPostProcessor() {
		return connectAuthenticationFilterPostProcessor;
	}

	public void setConnectAuthenticationFilterPostProcessor(ConnectAuthenticationFilterPostProcessor
                                                                    connectAuthenticationFilterPostProcessor) {
		this.connectAuthenticationFilterPostProcessor = connectAuthenticationFilterPostProcessor;
	}

}
