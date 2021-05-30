
package com.github.shawven.security.verification.autoconfigure;

import com.github.shawven.security.verification.config.CaptchaConfig;
import com.github.shawven.security.verification.config.SmsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.security.verification")
public class VerificationProperties {

    /**
     * 图片验证码配置
     */
    @NestedConfigurationProperty
    private CaptchaConfig captcha = new CaptchaConfig();

    /**
     * 短信验证码配置
     */
    @NestedConfigurationProperty
    private SmsConfig sms = new SmsConfig();

    public CaptchaConfig getCaptcha() {
        return captcha;
    }

    public void setCaptcha(CaptchaConfig captcha) {
        this.captcha = captcha;
    }

    public SmsConfig getSms() {
        return sms;
    }

    public void setSms(SmsConfig sms) {
        this.sms = sms;
    }
}

