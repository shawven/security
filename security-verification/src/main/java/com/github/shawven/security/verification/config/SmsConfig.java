package com.github.shawven.security.verification.config;

import com.github.shawven.security.verification.VerificationConstants;

/**
 * @author Shoven
 * @date 2019-08-16
 */
public class SmsConfig extends VerificationConfig {

    /**
     * 手机登录拦截url
     */
    private String loginProcessingUrl = VerificationConstants.DEFAULT_SMS_TOKEN_PROCESSING_URL;

    /**
     * 验证码长度
     */
    private int length = 6;

    /**
     * 过期时间
     */
    private int expireIn = 60;

    public int getLength() {
        return length;
    }

    public void setLength(int lenght) {
        this.length = lenght;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }


    public String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    public void setLoginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
    }

}

