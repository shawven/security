package com.github.shawven.security.app.config.social;

import com.github.shawven.security.social.support.SocialAuthenticationFilterPostProcessor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * 社交过滤器的后处理器，用于在不同环境下个性化社交登录的配置
 *
 * @author Shoven
 * @since 2019-04-19 10:15
 */
public class AppSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {

    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    public AppSocialAuthenticationFilterPostProcessor(AuthenticationSuccessHandler authenticationSuccessHandler,
                                                      AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    /**
     * 修改成功获取openid后的成功处理器，浏览器会发起页面跳转到
     * 这里是app环境，要直接返回token，避免接口请求返回304
     *
     * @param socialAuthenticationFilter
     */
    @Override
    public void proceed(SocialAuthenticationFilter socialAuthenticationFilter) {
        socialAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        socialAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
    }
}
