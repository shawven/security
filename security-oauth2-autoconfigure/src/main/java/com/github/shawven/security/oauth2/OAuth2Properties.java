
package com.github.shawven.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("app.security.oauth2")
public class OAuth2Properties {

	/**
	 * 使用jwt时为token签名的秘钥
	 */
	private String jwtSigningKey = "app_jwt_signing_key";

    /**
     * jwt、redis
     */
    private String tokenStore = "jwt";

    /**
     * 手机短信处理URL
     */
    private String phoneProcessingUrl = OAuth2Constants.DEFAULT_PHONE_TOKEN_PROCESSING_URL;

	/**
	 * 客户端配置
	 */
	@NestedConfigurationProperty
	private OAuth2ClientProperties[] clients = {};

	public OAuth2ClientProperties[] getClients() {
		return clients;
	}

	public void setClients(OAuth2ClientProperties[] clients) {
		this.clients = clients;
	}

	public String getJwtSigningKey() {
		return jwtSigningKey;
	}

	public void setJwtSigningKey(String jwtSigningKey) {
		this.jwtSigningKey = jwtSigningKey;
	}

    public String getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(String tokenStore) {
        this.tokenStore = tokenStore;
    }

    public String getPhoneProcessingUrl() {
        return phoneProcessingUrl;
    }

    public void setPhoneProcessingUrl(String phoneProcessingUrl) {
        this.phoneProcessingUrl = phoneProcessingUrl;
    }
}
