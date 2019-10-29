
package com.github.shawven.security.social.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社交账号绑定状态视图
 *
 */
@Component("connect/status")
public class AppConnectionStatusView extends AbstractView {

	@Autowired
	private ObjectMapper objectMapper;


	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, List<Connection<?>>> connections = (Map<String, List<Connection<?>>>) model.get("connectionMap");

		Map<String, Boolean> result = new HashMap<>();
		for (String key : connections.keySet()) {
			result.put(key, !connections.get(key).isEmpty());
		}
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));
	}

}
