
package com.github.shawven.security.oauth2;

/**
 * security常量
 *
 * @author Shoven
 * @since 2019-05-08 21:52
 */
public class OAuth2Constants {

    /**
     * 默认的授权端点（获取和刷新令牌端点），为了统一登陆前缀
     * 密码登陆（获取令牌、刷新令牌）：/oauth/token
     * 手机登陆（获取令牌）：/oauth/phone
     * 社交登陆（获取令牌）：/oauth/connect/qq、/login/connect/weixin 等
     */
    public static final String DEFAULT_OAUTH_TOKEN_ENDPOINTS = "/oauth/token";

	/**
	 * 默认的手机验证码登录请求处理url
	 */
	public static final String DEFAULT_PHONE_TOKEN_PROCESSING_URL = "/oauth/phone";
}
