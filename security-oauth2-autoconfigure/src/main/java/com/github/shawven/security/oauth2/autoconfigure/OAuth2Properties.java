
package com.github.shawven.security.oauth2.autoconfigure;

import com.github.shawven.security.oauth2.OAuth2ClientProperties;
import com.github.shawven.security.oauth2.Oauth2JwtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@ConfigurationProperties("app.security.oauth2")
public class OAuth2Properties {
    /**
     * jwt、redis
     */
    private String tokenStore = "jwt";


    @NestedConfigurationProperty
    private Oauth2JwtProperties jwt;

	/**
	 * 客户端配置
	 */
	@NestedConfigurationProperty
	private List<OAuth2ClientProperties> clients;

    public List<OAuth2ClientProperties> getClients() {
        return clients;
    }

    public void setClients(List<OAuth2ClientProperties> clients) {
        this.clients = clients;
    }

    public String getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(String tokenStore) {
        this.tokenStore = tokenStore;
    }

    public Oauth2JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(Oauth2JwtProperties jwt) {
        this.jwt = jwt;
    }
}
