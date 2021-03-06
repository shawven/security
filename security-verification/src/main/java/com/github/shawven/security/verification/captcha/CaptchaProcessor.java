
package com.github.shawven.security.verification.captcha;

import com.github.shawven.security.verification.*;
import com.github.shawven.security.verification.repository.VerificationRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图片验证码处理器
        */
public class CaptchaProcessor extends AbstractVerificationProcessor<Captcha> {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaProcessor.class);

    public CaptchaProcessor(VerificationRepositoryFactory repositoryFactory,
                            VerificationGenerator<Captcha> verificationGenerator) {
        super(repositoryFactory, verificationGenerator);
    }

    /**
     * 发送图形验证码，将其写到响应中
     *  @param captchaRequest
     * @param captcha
     */
    @Override
    protected void send(VerificationRequest<Captcha> captchaRequest, Captcha captcha) {
        try {
            HttpServletResponse response = captchaRequest.getResponse();
            response.setHeader(VerificationConstants.REQUEST_ID, captchaRequest.getRequestId());
            ImageIO.write(captcha.getImage(), "JPEG", response.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
