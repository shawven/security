package com.github.shawven.security.connect;

import com.github.shawven.security.connect.config.ConnectConfiguration;
import com.github.shawven.security.connect.config.QQConfiguration;
import com.github.shawven.security.connect.config.WeixinConfiguration;
import com.github.shawven.security.connect.provider.qq.connet.QQConnectionFactory;
import com.github.shawven.security.connect.provider.weixin.connect.WeixinConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

/**
 * @author Shoven
 * @date 2019-12-15
 */
@Configuration
public class ConnectProviderConfiguration {

    private ConnectProperties properties;

    public ConnectProviderConfiguration(ConnectProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security.connect.weixin", name = "app-id")
    public ConnectionFactory<?> createWeixinConnectionFactory() {
        WeixinConfiguration weixinConfig = getConnectConfiguration().getWeixin();
        return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
                weixinConfig.getAppSecret());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security.connect.qq", name = "app-id")
    public ConnectionFactory<?> createQqConnectionFactory() {
        QQConfiguration qqConfig = getConnectConfiguration().getQq();
        return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(),
                qqConfig.getAppSecret());
    }

    @Bean
    public ConnectConfiguration getConnectConfiguration() {
        ConnectConfiguration cfg = new ConnectConfiguration();
        cfg.setFilterProcessesUrl(properties.getFilterProcessesUrl());
        cfg.setQq(properties.getQq());
        cfg.setWeixin(properties.getWeixin());
        return cfg;
    }
}
