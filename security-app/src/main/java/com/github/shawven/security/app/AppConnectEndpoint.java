package com.github.shawven.security.app;

import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.connect.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shoven
 * @date 2019-11-11
 */
@Controller
@ConditionalOnClass(ConnectAutoConfiguration.class)
public class AppConnectEndpoint extends ConnectInfoExtendable {

    @Autowired
    private RedisSingInUtils redisSingInUtils;

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    /**
     * 需要引导用户注册或绑定时，通过此服务获取当前社交用户的信息
     * 返回401（表示认证失败，第一次登陆）和用户信息
     * @param request
     * @return
     */
    @GetMapping(ConnectConstants.DEFAULT_CURRENT_USER_INFO_URL)
    public ResponseEntity getSocialUserInfo(HttpServletRequest request) {
        // 从请求中拿用户信息
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        // 用户信息存储到redis
        redisSingInUtils.saveConnectionData(new ServletWebRequest(request), connection.createData());
        // 构建用户信息
        ConnectUserInfo connectUserInfo = buildSocialUserInfo(connection);

        ResponseData response = Responses.firstLoginNeedBindAccount().setData(connectUserInfo);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
