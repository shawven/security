
package com.github.shawven.security.browser.properties;

import com.github.shawven.security.browser.ResponseType;

/**
 * 浏览器环境配置项
 */
public class BrowserConfiguration {

    /**
     * 登录url
     */
    private String signInUrl;

    /**
     * 登陆处理url
     */
    private String signInProcessingUrl;

    /**
     * 登录成功后跳转的地址，如果设置了此属性，则登录成功后总是会跳到这个地址上。
     * 只在signInResponseType为REDIRECT时生效
     */
    private String singInSuccessUrl;

    /**
     * 用户注册页面
     */
    private String signUpUrl;

    /**
     * 退出处理url
     */
    private String signOutProcessingUrl;

    /**
     * 退出url
     */
    private String signOutSuccessUrl;

    /**
     * '记住我'功能的有效时间
     */
    private int rememberMeSeconds;

    /**
     * 登录响应的方式
     */
    private ResponseType responseType;

    /**
     * session配置
     */
    private SessionConfiguration session;

    public String getSignInUrl() {
        return signInUrl;
    }

    public void setSignInUrl(String signInUrl) {
        this.signInUrl = signInUrl;
    }

    public String getSignInProcessingUrl() {
        return signInProcessingUrl;
    }

    public void setSignInProcessingUrl(String signInProcessingUrl) {
        this.signInProcessingUrl = signInProcessingUrl;
    }

    public String getSingInSuccessUrl() {
        return singInSuccessUrl;
    }

    public void setSingInSuccessUrl(String singInSuccessUrl) {
        this.singInSuccessUrl = singInSuccessUrl;
    }

    public String getSignUpUrl() {
        return signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    public String getSignOutSuccessUrl() {
        return signOutSuccessUrl;
    }

    public void setSignOutSuccessUrl(String signOutSuccessUrl) {
        this.signOutSuccessUrl = signOutSuccessUrl;
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

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public SessionConfiguration getSession() {
        return session;
    }

    public void setSession(SessionConfiguration session) {
        this.session = session;
    }
}
