
package com.github.shawven.security.connect.config;


/**
 * QQ登录配置项
 */
public class QQConfig extends ProviderConfig {

	/**
	 * 第三方id，用来决定发起第三方登录的url，默认是 qq。
	 */
	private String providerId = "qq";

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
