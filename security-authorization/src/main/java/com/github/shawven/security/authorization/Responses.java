package com.github.shawven.security.authorization;

/**
 * @author Shoven
 * @date 2019-11-13
 */
public class Responses {

    public static ResponseData loginSuccess() {
        return new ResponseData().setMessage("登录成功");
    }

    public static ResponseData loginOutSuccess() {
        return new ResponseData().setMessage("退出成功");
    }

    public static ResponseData getTokenSuccess() {
        return new ResponseData().setMessage("获取token成功");
    }

    public static ResponseData refreshTokenSuccess() {
        return new ResponseData().setMessage("刷新token成功");
    }

    public static ResponseData requireLogin() {
        return new ResponseData().setMessage("当前用户未登录").setCode(401);
    }

    public static ResponseData badCredentials() {
        return new ResponseData().setMessage("用户名或密码错误").setCode(40101);
    }

    public static ResponseData noSuchClient() {
        return new ResponseData().setMessage("客户端不存在").setCode(40102);
    }

    public static ResponseData badClientCredentials() {
        return new ResponseData().setMessage("客户端密码错误").setCode(40103);
    }

    public static ResponseData expiredAccessToken() {
        return new ResponseData().setMessage("access_token已过期").setCode(40111);
    }

    public static ResponseData expiredRefreshToken() {
        return new ResponseData().setMessage("refresh_token已过期").setCode(40112);
    }

    public static ResponseData invalidAccessToken() {
        return new ResponseData().setMessage("无效的access_token").setCode(40113);
    }

    public static ResponseData invalidRefreshToken() {
        return new ResponseData().setMessage("无效的refresh_token").setCode(40114);
    }

    public static ResponseData concurrentLogin() {
        return new ResponseData().setMessage("当前账号已在其地方登录").setCode(40121);
    }

    public static ResponseData firstLoginNeedBinding() {
        return new ResponseData().setMessage("首次登录需要绑定账号").setCode(40131);
    }

    public static ResponseData duplicateBinding() {
        return new ResponseData().setMessage("重复绑定").setCode(40132);
    }

    public static ResponseData bindSuccess() {
        return new ResponseData().setMessage("绑定成功");
    }

    public static ResponseData unbindSuccess() {
        return new ResponseData().setMessage("解绑成功");
    }

    public static ResponseData accessDenied() {
        return new ResponseData().setMessage("无权限访问").setCode(403);
    }
}
