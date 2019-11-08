
package com.github.shawven.security.verification.autoconfigure;

import com.github.shawven.security.verification.configuraion.CaptchaConfiguration;
import com.github.shawven.security.verification.configuraion.SmsConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.verification")
public class VerificationProperties {

    /**
     * 图片验证码配置
     */
    private CaptchaConfiguration captcha;

    /**
     * 短信验证码配置
     */
    private SmsConfiguration sms;

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

