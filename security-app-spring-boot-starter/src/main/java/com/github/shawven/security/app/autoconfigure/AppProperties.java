package com.github.shawven.security.app.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Shoven
 * @date 2019-11-11
 */
@ConfigurationProperties("security.app")
public class AppProperties {
    /**
     * session管理配置项
     */
    @NestedConfigurationProperty
    private SessionProperties session = new SessionProperties();

    /**
     * 登陆处理url
     */
    private String signInProcessingUrl = "/login";
    /**
     * 退出处理url
     */
    private String signOutProcessingUrl = "/logout";

    /**
     * '记住我'功能的有效时间
     */
    private int rememberMeSeconds;

    public SessionProperties getSession() {
        return session;
    }

    public void setSession(SessionProperties session) {
        this.session = session;
    }

    public String getSignInProcessingUrl() {
        return signInProcessingUrl;
    }

    public void setSignInProcessingUrl(String signInProcessingUrl) {
        this.signInProcessingUrl = signInProcessingUrl;
    }

    public String getSignOutProcessingUrl() {
        return signOutProcessingUrl;
    }

    public void setSignOutProcessingUrl(String signOutProcessingUrl) {
        this.signOutProcessingUrl = signOutProcessingUrl;
    }

    public int getRememberMeSeconds() {
        return rememberMeSeconds;
    }

    public void setRememberMeSeconds(int rememberMeSeconds) {
        this.rememberMeSeconds = rememberMeSeconds;
    }
}
