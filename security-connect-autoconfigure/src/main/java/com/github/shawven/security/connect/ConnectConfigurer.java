
package com.github.shawven.security.connect;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 继承默认的社交登录配置，加入自定义的后处理逻辑
 */
public class ConnectConfigurer extends SpringSocialConfigurer {

	private String filterProcessesUrl;

	private ObjectProvider<ConnectAuthenticationFilterPostProcessor> connectAuthenticationFilterPostProcessorProvider;
	private ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor;


	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		if (filterProcessesUrl != null) {
            filter.setFilterProcessesUrl(filterProcessesUrl);
        }
        ConnectAuthenticationFilterPostProcessor processor = getConnectAuthenticationFilterPostProcessor();
        if (processor != null) {
            processor.postProcess(filter);
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
	    if (connectAuthenticationFilterPostProcessor == null) {
            connectAuthenticationFilterPostProcessor = connectAuthenticationFilterPostProcessorProvider.getIfAvailable();
        }
        return connectAuthenticationFilterPostProcessor;
	}

    public void setConnectAuthenticationFilterPostProcessor(ConnectAuthenticationFilterPostProcessor
                                                                    connectAuthenticationFilterPostProcessor) {
        this.connectAuthenticationFilterPostProcessor = connectAuthenticationFilterPostProcessor;
    }

    public void setConnectAuthenticationFilterPostProcessor(ObjectProvider<ConnectAuthenticationFilterPostProcessor>
                                                                    connectAuthenticationFilterPostProcessorProvider) {
		this.connectAuthenticationFilterPostProcessorProvider = connectAuthenticationFilterPostProcessorProvider;
	}

    public String getSignupUrl() {
        Field field = ReflectionUtils.findField(this.getClass(), "signupUrl");
        if (field == null) {
            throw new RuntimeException(this.getClass().getName() + " not exist field 'signupUrl'");
        }
        ReflectionUtils.makeAccessible(field);
        return (String)ReflectionUtils.getField(field, this);
    }
}
