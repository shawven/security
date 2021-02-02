
package com.github.shawven.security.oauth2;

/**
 * security常量
 *
 * @author Shoven
 * @date 2019-05-08
 */
public class OAuth2Constants {

    /**
     * 默认的授权端点（获取和刷新令牌端点），为了统一登陆前缀
     * 密码登陆（获取令牌、刷新令牌）：/oauth/token
     * 手机登陆（获取令牌）：/oauth/phone
     * 社交登陆（获取令牌）：/oauth/connect/qq、/login/connect/weixin 等
     */
    public static final String DEFAULT_OAUTH_TOKEN_ENDPOINTS = "/oauth/token";
}
