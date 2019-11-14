
package com.github.shawven.security.verification.captcha;

import com.github.shawven.security.verification.AbstractVerificationProcessor;
import com.github.shawven.security.verification.VerificationGenerator;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * 图片验证码处理器
        */
public class CaptchaProcessor extends AbstractVerificationProcessor<Captcha> {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaProcessor.class);

    public CaptchaProcessor(VerificationRepository verificationRepository,
                            VerificationGenerator<Captcha> verificationGenerator) {
        super(verificationRepository, verificationGenerator);
    }

    /**
     * 发送图形验证码，将其写到响应中
     *  @param captchaRequest
     * @param captcha
     */
    @Override
    protected void send(VerificationRequest<Captcha> captchaRequest, Captcha captcha) {
        try {
            ServletOutputStream outputStream = captchaRequest.getResponse().getOutputStream();
            ImageIO.write(captcha.getImage(), "JPEG", outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
