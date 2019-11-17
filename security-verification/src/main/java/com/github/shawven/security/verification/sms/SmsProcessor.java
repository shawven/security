
package com.github.shawven.security.verification.sms;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.verification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 短信验证码处理器
 */
public class SmsProcessor extends AbstractVerificationProcessor<Sms> {

    private static final Logger logger = LoggerFactory.getLogger(SmsProcessor.class);

	/**
	 * 短信验证码发送器
	 */
	private SmsSender smsSender;

    private ObjectMapper objectMapper ;

    public SmsProcessor(VerificationRepository verificationRepository,
                        VerificationGenerator<Sms> verificationGenerator,
                        SmsSender smsSender) {
        super(verificationRepository, verificationGenerator);
        this.objectMapper = new ObjectMapper();
        this.smsSender = smsSender;
    }

    @Override
	protected void send(VerificationRequest<Sms> verificationRequest, Sms sms) {
        SmsRequest smsRequest = (SmsRequest)verificationRequest;
        HttpServletResponse response = smsRequest.getResponse();
        try {
            smsSender.send(sms);
            responseMessage(response, sms.getExpireIn()/60 + "分钟内有效");
        } catch (VerificationException e) {
            responseErrorMessage(response, e.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            responseErrorMessage(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
	}

    private void responseMessage(HttpServletResponse response, String message) {
        try {
            ResponseData result = new ResponseData(message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void responseErrorMessage(HttpServletResponse response, String message, int status) {
        try {
            ResponseData result = new ResponseData(status, message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status);
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

	private String getPhone(HttpServletRequest request) {
        String mobile = request.getParameter(VerificationConstants.PHONE_PARAMETER_NAME);
        return Objects.isNull(mobile) ? String.valueOf(request.getAttribute(VerificationConstants.PHONE_PARAMETER_NAME)) : mobile;
    }

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }
}
