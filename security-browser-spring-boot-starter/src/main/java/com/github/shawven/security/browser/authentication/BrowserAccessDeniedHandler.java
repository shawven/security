package com.github.shawven.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.config.BrowserConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 非匿名用户且没有记住我，验证是失败时会走这里
 *
 * @author Shoven
 * @date 2018/11/1 18:15
 * 请求拒绝，没有权限
 */
public class BrowserAccessDeniedHandler extends AccessDeniedHandlerImpl {

    private final Logger logger = LoggerFactory.getLogger(BrowserAccessDeniedHandler.class);

    private BrowserConfiguration browserConfiguration;

    public BrowserAccessDeniedHandler(BrowserConfiguration browserConfiguration) {
        this.browserConfiguration = browserConfiguration;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        logger.debug(e.getMessage(), e);
        if (ResponseType.JSON.equals(browserConfiguration.getResponseType())) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Responses.accessDenied()));
        } else {
            super.handle(request, response, e);
        }
    }
}
