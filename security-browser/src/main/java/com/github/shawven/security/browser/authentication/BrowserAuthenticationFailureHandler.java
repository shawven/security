
package com.github.shawven.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @date 2019-05-08
 */
public class BrowserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(BrowserAuthenticationFailureHandler.class);

	private BrowserConfiguration browserConfiguration;

    private BrowserLoginFailureHandler browserLoginFailureHandler;

    public BrowserAuthenticationFailureHandler(BrowserConfiguration browserConfiguration,
                                               BrowserLoginFailureHandler browserLoginFailureHandler) {
        this.browserConfiguration = browserConfiguration;
        this.browserLoginFailureHandler = browserLoginFailureHandler;
    }

    @Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
        logger.debug(exception.getMessage(), exception);
        if (browserLoginFailureHandler != null) {
            browserLoginFailureHandler.onAuthenticationFailure(request, response, exception);
        }

        if (ResponseType.JSON.equals(browserConfiguration.getResponseType())) {
            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseData result = new ResponseData()
                    .setCode(status)
                    .setMessage(exception.getMessage());

            response.setStatus(status);
			response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(result));
		} else{
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
