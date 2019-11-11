
package com.github.shawven.security.app.config;


/**
 * 浏览器环境配置项
 */
public class AppConfiguration {

    /**
     * 登陆处理url
     */
    private String signInProcessingUrl;

    /**
     * 退出处理url
     */
    private String signOutProcessingUrl;

    /**
     * '记住我'功能的有效时间
     */
    private int rememberMeSeconds;

    /**
     * session配置
     */
    private SessionConfiguration session;

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

    public SessionConfiguration getSession() {
        return session;
    }

    public void setSession(SessionConfiguration session) {
        this.session = session;
    }
}
