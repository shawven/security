
package com.github.shawven.security.app.session;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 并发登录导致session失效时，默认的处理策略
 *
 * @author Shoven
 * @since 2019-05-08 21:54
 */
public class AppExpiredSessionStrategy extends AbstractSessionStrategy implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		onSessionInvalid(event.getRequest(), event.getResponse());
	}

	@Override
	protected boolean isConcurrency() {
		return true;
	}

}
