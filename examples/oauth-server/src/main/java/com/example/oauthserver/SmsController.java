package com.example.oauthserver;

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
public class SmsController {

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
    public void sendSms(HttpServletRequest request, HttpServletResponse response, String phone){
        SmsRequest smsRequest = new SmsRequest(request, response, phone);
        smsRequest.setMessageTemplate("您的登录验证短信验证码[{0}]，请在{1,number}分钟输入，请勿将验证码泄漏于他人");
        smsProcessor.processed(smsRequest);
    }

    /**
     * 获取图形验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("verification/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response,
                           Integer width, Integer height, Integer length){
        CaptchaRequest captchaRequest = new CaptchaRequest(request, response);
        captchaRequest.setWidth(width);
        captchaRequest.setHeight(height);
        captchaRequest.setLength(length);
        captchaProcessor.processed(captchaRequest);
    }
}
