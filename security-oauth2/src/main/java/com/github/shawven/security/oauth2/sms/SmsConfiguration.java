package com.github.shawven.security.oauth2.sms;

/**
 * @author Shoven
 * @date 2019-11-12
 */
public class SmsConfiguration {

    /**
     * 手机登录拦截url
     */
    private String filterProcessingUrl;

    public String getFilterProcessingUrl() {
        return filterProcessingUrl;
    }

    public void setFilterProcessingUrl(String filterProcessingUrl) {
        this.filterProcessingUrl = filterProcessingUrl;
    }
}
