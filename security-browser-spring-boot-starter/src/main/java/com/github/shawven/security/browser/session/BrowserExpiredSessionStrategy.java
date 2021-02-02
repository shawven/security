
package com.github.shawven.security.browser.session;

import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 并发登录导致session失效时，默认的处理策略
 *
 * @author Shoven
 * @date 2019-05-08
 */
public class BrowserExpiredSessionStrategy extends AbstractSessionStrategy implements SessionInformationExpiredStrategy {

	public BrowserExpiredSessionStrategy(BrowserConfiguration securityPropertie) {
		super(securityPropertie);
	}

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		onSessionInvalid(event.getRequest(), event.getResponse());
	}

	@Override
	protected boolean isConcurrency() {
		return true;
	}

}
