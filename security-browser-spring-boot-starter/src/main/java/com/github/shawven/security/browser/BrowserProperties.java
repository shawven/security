
package com.github.shawven.security.browser;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 浏览器环境配置项
 */
@ConfigurationProperties("app.security.browser")
public class BrowserProperties {

    /**
     * session管理配置项
     */
    @NestedConfigurationProperty
    private SessionProperties session;
    /**
     * 登录url
     */
    private String signInUrl = BrowserConstants.DEFAULT_SIGN_IN_URL;

    /**
     * 登陆处理url
     */
    private String signInProcessingUrl = BrowserConstants.DEFAULT_SIGN_IN_PROCESSING_URL_FORM;

    /**
     * 登录成功后跳转的地址，如果设置了此属性，则登录成功后总是会跳到这个地址上。
     * 只在signInResponseType为REDIRECT时生效
     */
    private String singInSuccessUrl = BrowserConstants.DEFAULT_SIGN_IN_SUCCESS_URL;

    /**
     * 用户注册页面
     */
    private String signUpUrl = BrowserConstants.DEFAULT_SIGN_UP_URL;

    /**
     * 退出处理url
     */
    private String signOutProcessingUrl = BrowserConstants.DEFAULT_SIGN_OUT_PROCESSING_URL;

    /**
     * 退出url
     */
    private String signOutSuccessUrl = BrowserConstants.DEFAULT_SIGN_OUT_SUCCESS_URL;

    /**
     * '记住我'功能的有效时间
     */
    private int rememberMeSeconds;

    /**
     * 登录响应的方式，默认是json
     */
    private ResponseType responseType = ResponseType.JSON;

    public SessionProperties getSession() {
        return session;
    }

    public void setSession(SessionProperties session) {
        this.session = session;
    }

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
}
