
package com.github.shawven.security.connect;

import com.github.shawven.security.connect.config.QQConfiguration;
import com.github.shawven.security.connect.config.WeixinConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 社交登录配置项
 */
@ConfigurationProperties("app.security.connect")
public class ConnectProperties {
	/**
	 * 社交登录功能拦截的url
	 */
	private String filterProcessesUrl = ConnectConstants.DEFAULT_OPENID_TOKEN_PROCESSING_URL;

    @NestedConfigurationProperty
	private QQConfiguration qq = new QQConfiguration();

    @NestedConfigurationProperty
	private WeixinConfiguration weixin = new WeixinConfiguration();

    public String getFilterProcessesUrl() {
        return filterProcessesUrl;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
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
