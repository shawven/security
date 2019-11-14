package com.github.shawven.security.verification.message;

/**
 * 消息模板
 *
 * @author Shoven
 * @date 2019-08-16
 */
public final class MessageTemplate {

    public static final String DEFAULT_SMS_CODE = "短信验证码：{0}";

    public static final String WITH_EXPIRE_TIME_SMS_CODE = "短信验证码：{0}，{1,number}分钟内输入有效";
}
