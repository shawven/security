package com.github.shawven.security.connect.provider.weixin;

/**
 * @author Shoven
 * @date 2019-11-22
 */
public class WeixinConfig {

    /**
     * 获取授权码的url
     */
    public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect";

    /**
     * 获取accessToken的url
     */
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 刷新accessToken的url
     */
    public static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    /**
     * 获取用户信息的url
     */
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
}
