
package com.github.shawven.security.oauth2;

import com.github.shawven.security.oauth2.config.OAuth2ClientConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("security.oauth2")
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
	 * 客户端配置
	 */
	@NestedConfigurationProperty
	private OAuth2ClientConfiguration[] clients = {};

	public OAuth2ClientConfiguration[] getClients() {
		return clients;
	}

	public void setClients(OAuth2ClientConfiguration[] clients) {
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
}
