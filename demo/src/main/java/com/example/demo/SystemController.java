package com.example.demo;

import com.github.shawven.security.verification.captcha.CaptchaProcessor;
import com.github.shawven.security.verification.captcha.CaptchaRequest;
import com.github.shawven.security.verification.sms.SmsProcessor;
import com.github.shawven.security.verification.sms.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shoven
 * @date 2019-10-28
 */
@RestController
public class SystemController {

    @Autowired
    private SmsProcessor smsProcessor;

    @Autowired
    private CaptchaProcessor captchaProcessor;

    /**
     * 给指定手机号发送短信验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("verification/sms")
    public void createRestrictedSmsCode(HttpServletRequest request, HttpServletResponse response, String phone){
        SmsRequest smsRequest = new SmsRequest(request, response, phone);
        smsProcessor.processed(smsRequest);
    }

    /**
     * 获取图形验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("verification/captcha")
    public void createImageCode(HttpServletRequest request, HttpServletResponse response,
                                Integer width, Integer height, Integer length){
        CaptchaRequest captchaRequest = new CaptchaRequest(request, response);
        captchaRequest.setWidth(width);
        captchaRequest.setHeight(height);
        captchaRequest.setLength(length);
        captchaProcessor.processed(captchaRequest);
    }
}
