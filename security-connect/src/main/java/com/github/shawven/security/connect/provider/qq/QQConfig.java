package com.github.shawven.security.connect.provider.qq;

/**
 * @author Shoven
 * @date 2019-11-22
 */
public class QQConfig {

    /**
     * 获取授权码的url
     */
    public static final String AUTHORIZE_URL = "https://graph.qq.com/connect.0/authorize";

    /**
     * 获取accessToken的url
     */
    public static final String ACCESS_TOKEN_URL = "https://graph.qq.com/connect.0/token";

    /**
     * 获取openId的url
     */
    public static final String OPENID_URL = "https://graph.qq.com/connect.0/me";

    /**
     * 获取用户信息的url
     */
    public static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info";
}
