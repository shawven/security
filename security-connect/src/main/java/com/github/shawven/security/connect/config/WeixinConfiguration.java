
package com.github.shawven.security.connect.config;

/**
 * 微信登录配置项
 */
public class WeixinConfiguration extends ProviderConfiguration {

	/**
	 * 第三方id，用来决定发起第三方登录的url，默认是 weixin。
	 */
	private String providerId = "weixin";

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
