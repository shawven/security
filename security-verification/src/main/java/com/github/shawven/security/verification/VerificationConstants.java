
package com.github.shawven.security.verification;


/**
 * 验证码常量
 */
public class VerificationConstants {

    /**
     * 默认的短信验证登录处理URL
     */
    public static final String DEFAULT_SMS_TOKEN_PROCESSING_URL = "/oauth/phone";

    /**
	 * 验证图片验证码时，http请求中携带图片验证码信息的参数的名称
	 */
	public static final String CAPTCHA_PARAMETER_NAME = "captcha";

	/**
	 * 验证短信验证码时，http请求中携带短信验证码信息的参数的名称
	 */
	public static final String SMS_PARAMETER_NAME = "sms";

	/**
	 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
	 */
	public static final String PHONE_PARAMETER_NAME = "phone";

    /**
     * request attribute传递
     */
    public static final String PHONE_ATTRIBUTE_NAME = "verification_phone";

    /**
     * request id参数
     */
    public static final String REQUEST_ID = "Request-Id";

    /**
     * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
     */
    public static final String REDIS_CODE_KEY = "verification_code";
}
