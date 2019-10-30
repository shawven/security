
package com.github.shawven.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.verification.ResponseData;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.properties.BrowserProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 浏览器环境下登录失败的处理器
 *
 * @author Shoven
 * @since 2019-05-08 21:55
 */
public class BrowserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private ObjectMapper objectMapper;

	private BrowserProperties browserProperties;

    private BrowserLoginFailureHandler browserLoginFailureHandler;

    public BrowserAuthenticationFailureHandler(BrowserProperties browserProperties,
                                               BrowserLoginFailureHandler browserLoginFailureHandler) {
        this.objectMapper = new ObjectMapper();
        this.browserProperties = browserProperties;
        this.browserLoginFailureHandler = browserLoginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
        if (browserLoginFailureHandler != null) {
            browserLoginFailureHandler.onAuthenticationFailure(request, response, exception);
        }

        if (ResponseType.JSON.equals(browserProperties.getResponseType())) {
            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseData result = new ResponseData()
                    .setCode(status)
                    .setMessage(exception.getMessage());

            response.setStatus(status);
			response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
		}else{
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
