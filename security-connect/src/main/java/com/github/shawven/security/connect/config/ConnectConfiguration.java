package com.github.shawven.security.connect.config;

import com.github.shawven.security.connect.ConnectConstants;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class ConnectConfiguration {

    /**
     * 社交登录功能拦截的url
     */
    private String filterProcessesUrl;

    /**
     * 社交登录，如果需要用户注册，跳转的页面
     */
    private String signUpUrl = ConnectConstants.CONNECT_USER_INFO_URL;

    private QQConfiguration qq;

    private WeixinConfiguration weixin;

    public String getFilterProcessesUrl() {
        return filterProcessesUrl;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }

    public String getSignUpUrl() {
        return signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    public QQConfiguration getQq() {
        return qq;
    }

    public void setQq(QQConfiguration qq) {
        this.qq = qq;
    }

    public WeixinConfiguration getWeixin() {
        return weixin;
    }

    public void setWeixin(WeixinConfiguration weixin) {
        this.weixin = weixin;
    }
}
