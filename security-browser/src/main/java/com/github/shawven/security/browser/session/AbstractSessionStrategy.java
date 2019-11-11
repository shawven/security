
package com.github.shawven.security.browser.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.MessageConstants;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 抽象的session失效处理器
 *
 * @author Shoven
 * @since 2019-05-08 21:53
 */
public class AbstractSessionStrategy {
	/**
	 * 跳转的url
	 */
	private String destinationUrl;
	/**
	 * 系统配置信息
	 */
	private BrowserConfiguration browserConfiguration;
	/**
	 * 重定向策略
	 */
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	/**
	 * 跳转前是否创建新的session
	 */
	private boolean createNewSession = true;

	private ObjectMapper objectMapper = new ObjectMapper();


	public AbstractSessionStrategy(BrowserConfiguration browserConfiguration) {
		String invalidSessionUrl = browserConfiguration.getSession().getSessionInvalidUrl();
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
		this.browserConfiguration = browserConfiguration;
	}

	protected void onSessionInvalid(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (createNewSession) {
			request.getSession();
		}

		if (ResponseType.JSON.equals(browserConfiguration.getResponseType())) {
            Object result = buildResponseContent(request);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } else {
            String sourceUrl = request.getRequestURI();
            String targetUrl;
            // 登陆也或者退出页保持原来页面即可
            if (StringUtils.equals(sourceUrl, browserConfiguration.getSignInUrl())
                    || StringUtils.equals(sourceUrl, browserConfiguration.getSignOutSuccessUrl())){
                targetUrl = sourceUrl;
            } else{
                targetUrl = destinationUrl;
            }
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
	}

	/**
	 * @param request
	 * @return
	 */
	protected Object buildResponseContent(HttpServletRequest request) {
		String message = isConcurrency() ? "当前用户已在其地方登陆" : MessageConstants.REQUIRE_LOGIN;
		return new ResponseData()
                .setCode(HttpStatus.UNAUTHORIZED.value())
                .setMessage(message);
	}

	/**
	 * session失效是否是并发导致的
	 *
	 * @return
	 */
	protected boolean isConcurrency() {
		return false;
	}


	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}

}
