
package com.github.shawven.security.verification.configuraion;

public class VerificationConfiguration {

    /**
     * 要拦截的url，多个url用逗号隔开，ant pattern
     */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

