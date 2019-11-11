package com.github.shawven.security.verification.config;

/**
 * @author Shoven
 * @date 2019-08-16
 */
public class SmsConfiguration extends VerificationConfiguration{
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

}

