package com.github.shawven.security.connect.config;

/**
 * @author Shoven
 * @date 2019-11-08
 */
public class WxminiConfig extends ProviderConfig {
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
