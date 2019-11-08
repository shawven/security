package com.github.shawven.security.authorization;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Shoven
 * @date 2019-10-28
 */
@ConfigurationProperties("app.security")
public class AuthorizationProperties {

    private String whitelist;

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }
}
