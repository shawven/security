
package com.github.shawven.security.verification.sms;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.verification.*;
import com.github.shawven.security.verification.message.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 短信验证码处理器
 */
public class SmsProcessor extends AbstractVerificationProcessor<Verification> {

    private static final Logger logger = LoggerFactory.getLogger(SmsProcessor.class);

    private Pattern phoneMatcher = Pattern.compile("[1]([3-9])[0-9]{9}");
	/**
	 * 短信验证码发送器
	 */
	private SmsSender smsSender;

    private ObjectMapper objectMapper ;

    public SmsProcessor(VerificationRepository verificationRepository,
                        VerificationGenerator verificationGenerator,
                        SmsSender smsSender) {
        super(verificationRepository, verificationGenerator);
        this.objectMapper = new ObjectMapper();
        this.smsSender = smsSender;
    }

    @Override
	protected void send(ServletWebRequest webRequest, Verification verification) {
        HttpServletRequest request = webRequest.getRequest();
        Sms sms = (Sms) verification;
        String phone = getPhone(request);
        if (!phoneMatcher.matcher(phone).matches()) {
            responseErrorMessage(webRequest,"手机号格式不正确", HttpStatus.BAD_REQUEST.value());
            return;
        }
        sms.setPhone(phone);
        String messageTemplate = (String) request.getAttribute(VerificationConstants.DEFAULT_SMS_MESSAGE_ATTR_NAME);
        sms.setMessage(new SmsMessage(messageTemplate, sms).toString());

        try {
            smsSender.send(sms);
            responseMessage(webRequest, verification.getExpireIn() + "");
        } catch (VerificationException e) {
            responseErrorMessage(webRequest, e.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            responseErrorMessage(webRequest, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
	}

    private void responseMessage(ServletWebRequest request, String message) {
        try {
            ResponseData result = new ResponseData(message);
            HttpServletResponse response = request.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void responseErrorMessage(ServletWebRequest webRequest, String message, int status) {
        try {
            HttpServletResponse response = webRequest.getResponse();
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
        String mobile = request.getParameter(VerificationConstants.DEFAULT_PHONE_PARAMETER_NAME);
        return Objects.isNull(mobile) ? String.valueOf(request.getAttribute(VerificationConstants.DEFAULT_PHONE_PARAMETER_NAME)) : mobile;
    }

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }
}
