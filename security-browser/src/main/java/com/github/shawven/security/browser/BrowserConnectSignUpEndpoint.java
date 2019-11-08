
package com.github.shawven.security.browser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.browser.properties.BrowserConfiguration;
import com.github.shawven.security.connect.ConnectAutoConfiguration;
import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.support.ConnectUserInfo;
import com.github.shawven.security.connect.ConnectInfoExtendable;
import com.github.shawven.security.verification.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 浏览器环境下与安全相关的服务
 *
 * @author Shoven
 * @since 2019-05-08 21:54
 */
@Controller
@ConditionalOnClass(ConnectAutoConfiguration.class)
public class BrowserConnectSignUpEndpoint extends ConnectInfoExtendable {

    private static final Logger logger = LoggerFactory.getLogger(BrowserConnectSignUpEndpoint.class);

	@Autowired
	private ProviderSignInUtils providerSignInUtils;

	@Autowired
    private BrowserConfiguration browserConfiguration;

	/**
	 * 需要引导用户注册或绑定时，通过此服务获取当前社交用户的信息
	 * 返回401（表示认证失败，第一次登陆）和用户信息
     *
	 * @param request
	 * @return
	 */
	@GetMapping(ConnectConstants.DEFAULT_CURRENT_USER_INFO_URL)
	public void getSocialUserInfo(HttpServletRequest request, HttpServletResponse response) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        ConnectUserInfo connectUserInfo = buildSocialUserInfo(connection);

        if (ResponseType.JSON.equals(browserConfiguration.getResponseType())) {
            ResponseData result = new ResponseData()
                    .setCode(HttpStatus.UNAUTHORIZED.value())
                    .setMessage("第一次登录需要绑定账号")
                    .setData(connectUserInfo);

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            try {
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            request.setAttribute("socialUserInfo", "socialUserInfo");
            try {
                response.sendRedirect(browserConfiguration.getSignUpUrl());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
	}
}
