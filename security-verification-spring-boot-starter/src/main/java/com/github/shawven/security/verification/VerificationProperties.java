
package com.github.shawven.security.verification;

import com.github.shawven.security.verification.config.CaptchaConfiguration;
import com.github.shawven.security.verification.config.SmsConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "security.verification")
public class VerificationProperties {

    /**
     * 图片验证码配置
     */
    @NestedConfigurationProperty
    private CaptchaConfiguration captcha = new CaptchaConfiguration();

    /**
     * 短信验证码配置
     */
    @NestedConfigurationProperty
    private SmsConfiguration sms = new SmsConfiguration();

    public CaptchaConfiguration getCaptcha() {
        return captcha;
    }

    public void setCaptcha(CaptchaConfiguration captcha) {
        this.captcha = captcha;
    }

    public SmsConfiguration getSms() {
        return sms;
    }

    public void setSms(SmsConfiguration sms) {
        this.sms = sms;
    }
}

