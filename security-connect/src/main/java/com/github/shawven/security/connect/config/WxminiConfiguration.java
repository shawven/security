package com.github.shawven.security.connect.config;

import com.github.shawven.security.connect.config.ProviderConfiguration;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class WxminiConfiguration extends ProviderConfiguration {
    /**
     * 微信小程序的流程和标准的connect不一样，单独配置
     */
    private String wxMiniProviderId = "wxmini";

    public String getWxMiniProviderId() {
        return wxMiniProviderId;
    }

    public void setWxMiniProviderId(String wxMiniProviderId) {
        this.wxMiniProviderId = wxMiniProviderId;
    }
}
