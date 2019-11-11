
package com.github.shawven.security.browser.session;

import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 默认的session失效处理策略
 *
 * @author Shoven
 * @since 2019-05-08 21:53
 */
public class BrowserInvalidSessionStrategy extends AbstractSessionStrategy implements InvalidSessionStrategy {

	public BrowserInvalidSessionStrategy(BrowserConfiguration browserConfiguration) {
		super(browserConfiguration);
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		onSessionInvalid(request, response);
	}

}
