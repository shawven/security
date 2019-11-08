package com.github.shawven.security.verification.configuraion;

/**
 * 图片验证码配置项
 */
public class CaptchaConfiguration extends VerificationConfiguration{
    /**
     * 验证码长度
     */
    private int length = 6;

    /**
     * 过期时间
     */
    private int expireIn = 60;

    /**
     * 图片宽
     */
    private int width = 67;
    /**
     * 图片高
     */
    private int height = 23;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
}
