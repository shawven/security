package com.github.shawven.security.connect.config;

import com.github.shawven.security.connect.ConnectConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Shoven
 * @date 2019-11-08
 */
@ConfigurationProperties("app.security.connect")
public class ConnectProperties {

    /**
     * 社交登录功能拦截的url
     */
    private String filterProcessesUrl;

    /**
     * 社交登录，如果需要用户注册，跳转的页面
     */
    private String signUpUrl = ConnectConstants.CONNECT_USER_INFO_URL;

    @NestedConfigurationProperty
    private QQConfig qq;

    @NestedConfigurationProperty
    private WeixinConfig weixin;

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

    public QQConfig getQq() {
        return qq;
    }

    public void setQq(QQConfig qq) {
        this.qq = qq;
    }

    public WeixinConfig getWeixin() {
        return weixin;
    }

    public void setWeixin(WeixinConfig weixin) {
        this.weixin = weixin;
    }
}
