package com.github.shawven.security.connect.config;

/**
 * @author Shoven
 * @date 2019-04-18
 */
public class ProviderConfig {

    protected String appId;

    protected String appSecret;

    protected String providerId;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
