package com.github.shawven.security.verification.sms;

import com.github.shawven.security.verification.VerificationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 发送短信请求
 * 此配置会覆盖配置文件的配置
 *
 * @author Shoven
 * @date 2019-11-15
 */
public class SmsRequest extends VerificationRequest<Sms> {

    private static final String DEFAULT_TEMPLATE = "短信验证码[{0}]，{1,number}分钟按页面提示提交验证码，请勿将验证码泄漏于他人";
    private static final Pattern MESSAGE_TEMPLATE_PATTERN = Pattern.compile("\\{\\d(,?\\w+)?}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("[1]([3-9])[0-9]{9}");

    private Integer length;

    private String phone;

    private String messageTemplate = DEFAULT_TEMPLATE;

    public SmsRequest(HttpServletRequest request, HttpServletResponse response, String phone) {
        super(request, response);
        this.phone = phone;
        validatePhone(phone);
    }

    public SmsRequest(HttpServletRequest request, HttpServletResponse response, String phone, String messageTemplate) {
        this(request, response, phone);
        this.messageTemplate = messageTemplate;
        validateTemplate(messageTemplate);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        validateTemplate(messageTemplate);
        this.messageTemplate = messageTemplate;
    }

    private void validatePhone(String phone) {
        Objects.requireNonNull(phone, "手机号不能为空");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }

    private void validateTemplate(String messageTemplate) {
        Objects.requireNonNull(messageTemplate, "短信模板不能为空");
        if (!MESSAGE_TEMPLATE_PATTERN.matcher(messageTemplate).find()) {
            throw new IllegalArgumentException("短信模板错误，至少有一个填充位，参考MessageFormat");
        }
    }
}
