
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
	public static final String DEFAULT_PARAMETER_NAME_CAPTCHA = "captcha";

	/**
	 * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_SMS = "sms";

	/**
	 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_MOBILE = "phone";

    /**
     * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
     */
    public static final String DEFAULT_ATTR_NAME_SMS_MESSAGE = "sms_message";
}
