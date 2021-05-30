
package com.github.shawven.security.verification.sms;

import com.github.shawven.security.verification.VerificationConstants;
import com.github.shawven.security.verification.VerificationGenerator;
import com.github.shawven.security.verification.VerificationRequest;
import com.github.shawven.security.verification.config.SmsConfig;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.MessageFormat;

/**
 * 短信验证码生成器
 */
public class SmsGenerator implements VerificationGenerator<Sms> {

	private SmsConfig configuration;

    public SmsGenerator(SmsConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public Sms generate(VerificationRequest<Sms> request) {
        SmsRequest smsReq = (SmsRequest) request;
        int length = smsReq.getLength() != null ? smsReq.getLength() : configuration.getLength();
        int expireIn = smsReq.getExpireIn() != null ? smsReq.getExpireIn() :  configuration.getExpireIn();

        String code = RandomStringUtils.randomNumeric(length);
        String message = toMessage(smsReq.getMessageTemplate(), code, expireIn);

        // 重要，用作存储的key
        request.getRequest().setAttribute(VerificationConstants.PHONE_ATTRIBUTE_NAME, smsReq.getPhone());
        return new Sms(smsReq.getPhone(), message, code, expireIn);
    }

    private String toMessage(String template, String code, int expireIn) {
        int seconds = expireIn / 60;
        return MessageFormat.format(template, code, seconds);
    }
}
