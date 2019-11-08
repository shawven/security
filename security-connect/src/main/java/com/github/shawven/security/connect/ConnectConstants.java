package com.github.shawven.security.connect;

/**
 * @author Shoven
 * @date 2019-08-19
 */
public class ConnectConstants {
    /**
     * 默认的OPENID登录请求处理url
     */
    public static final String DEFAULT_OPENID_TOKEN_PROCESSING_URL = "/login/connect";

    /**
     * 社交登陆需要注册时获取用户信息的处理url
     */
    public static final String DEFAULT_CURRENT_USER_INFO_URL = "/social/user";

    /**
     * openid参数名
     */
    public static final String DEFAULT_PARAMETER_NAME_OPENID = "openId";

    /**
     * providerId参数名
     */
    public static final String DEFAULT_PARAMETER_NAME_PROVIDERID = "providerId";
}
