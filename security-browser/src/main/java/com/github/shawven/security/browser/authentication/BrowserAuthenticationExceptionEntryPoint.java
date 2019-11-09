package com.github.shawven.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.browser.BrowserConstants;
import com.github.shawven.security.verification.ResponseData;
import com.github.shawven.security.browser.ResponseType;
import com.github.shawven.security.browser.properties.BrowserConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shoven
 * @date 2018/11/2 10:48
 */

public class BrowserAuthenticationExceptionEntryPoint extends LoginUrlAuthenticationEntryPoint {



    private BrowserConfiguration browserConfiguration;

    private ObjectMapper objectMapper;

    public BrowserAuthenticationExceptionEntryPoint(BrowserConfiguration browserConfiguration) {
        super(browserConfiguration.getSignInUrl());
        this.objectMapper = new ObjectMapper();
        this.browserConfiguration = browserConfiguration;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        if (ResponseType.JSON.equals(browserConfiguration.getResponseType())) {
            String errorMessage = BrowserConstants.UNAUTHORIZED.equals(e.getMessage())
                    ? BrowserConstants.REQUIRE_LOGIN
                    : HttpStatus.UNAUTHORIZED.getReasonPhrase();

            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseData rsp = new ResponseData()
                    .setMessage(errorMessage)
                    .setCode(status);

            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(rsp));
        } else {
            super.commence(request, response, e);
        }
    }
}
