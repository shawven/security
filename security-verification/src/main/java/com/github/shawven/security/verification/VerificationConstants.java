
package com.github.shawven.security.verification;


/**
 * 验证码常量
 */
public class VerificationConstants {

    /**
     * 默认的校验处理验证码的url前缀
     */
    public static final String DEFAULT_VERIFICATION_URL_PREFIX = "/verification";

    /**
	 * 验证图片验证码时，http请求中默认的携带图片验证码信息的参数的名称
	 */
	public static final String DEFAULT_CAPTCHA_PARAMETER_NAME = "captcha";

	/**
	 * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
	 */
	public static final String DEFAULT_SMS_PARAMETER_NAME = "sms";

	/**
	 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
	 */
	public static final String DEFAULT_PHONE_PARAMETER_NAME = "phone";

    /**
     * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
     */
    public static final String DEFAULT_SMS_MESSAGE_ATTR_NAME = "sms_message";
}
