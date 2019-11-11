
package com.github.shawven.security.app.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.MessageConstants;
import com.github.shawven.security.authorization.ResponseData;
import org.springframework.http.HttpStatus;

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
	 * 跳转前是否创建新的session
	 */
	private boolean createNewSession = true;

	private ObjectMapper objectMapper = new ObjectMapper();


	protected void onSessionInvalid(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (createNewSession) {
			request.getSession();
		}

        Object result = buildResponseContent(request);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
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
