
package com.github.shawven.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 默认的退出成功处理器，如果设置了security.browser.signOutUrl，则跳到配置的地址上，
 * 如果没配置，则返回json格式的响应。
 *
 * @author Shoven
 * @date 2019-05-08
 */
public class BrowserLogoutSuccessHandler implements LogoutSuccessHandler {

    private BrowserConfiguration configuration;

    private ObjectMapper objectMapper;

	public BrowserLogoutSuccessHandler(BrowserConfiguration configuration) {
        this.configuration = configuration;
        this.objectMapper = new ObjectMapper();
    }

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
        if (ResponseType.JSON.equals(configuration.getResponseType())) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Responses.loginOutSuccess()));
        } else {
            String redirectUrl = configuration.getSignOutSuccessUrl();
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = configuration.getSignInUrl();
            }
            response.sendRedirect(redirectUrl);
        }
	}

}
