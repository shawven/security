package com.github.shawven.security.connect;

/**
 * @author Shoven
 * @date 2019-08-19
 */
public class ConnectConstants {
    /**
     * 默认的OPENID登录请求处理url
     */
    public static final String DEFAULT_OPENID_TOKEN_PROCESSING_URL = "/oauth/connect";

    /**
     * 社交登陆需要注册时获取用户信息的处理url
     */
    public static final String CONNECT_USER_INFO_URL = "/user";

    /**
     * openid参数名
     */
    public static final String OPENID_PARAMETER_NAME = "openId";

    /**
     * providerId参数名
     */
    public static final String PROVIDER_ID_PARAMETER_NAME = "providerId";
}
