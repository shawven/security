
package com.github.shawven.security.verification.sms;

import com.github.shawven.security.verification.configuraion.SmsConfiguration;
import com.github.shawven.security.verification.configuraion.VerificationConfiguration;
import com.github.shawven.security.verification.VerificationGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 短信验证码生成器
 */
public class SmsGenerator implements VerificationGenerator<Sms> {

	private SmsConfiguration configuration;

    public SmsGenerator(SmsConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
	public Sms generate(ServletWebRequest request) {
		String code = RandomStringUtils.randomNumeric(configuration.getLength());
		return new Sms(code, configuration.getExpireIn());
	}

}
