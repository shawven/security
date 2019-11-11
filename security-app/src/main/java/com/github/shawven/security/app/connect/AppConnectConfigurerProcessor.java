package com.github.shawven.security.app.connect;

import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.ConnectConfigurer;
import com.github.shawven.security.connect.ConnectConfigurerProcessor;

/**
 * APP的社交登陆配置器的处理器
 *
 * @author Shoven
 * @since 2019-04-20 15:37
 */
public class AppConnectConfigurerProcessor implements ConnectConfigurerProcessor {

    /**
     *  假如没有配置无感知处理程序connectionSignUp，需要引导用户进行注册或绑定
     *  在浏览器环境下时第一次社交登录时会跳转配置的注册页面
     *  app环境下跳到该接口存储用户信息备用，让app去跳转到注册页再来拿这个信息
     *
     * @param configurer
     */
    @Override
    public void proceed(ConnectConfigurer configurer) {
        configurer.signupUrl(ConnectConstants.DEFAULT_CURRENT_USER_INFO_URL);
    }
}
