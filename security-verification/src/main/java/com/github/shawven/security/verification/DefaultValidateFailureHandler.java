package com.github.shawven.security.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultValidateFailureHandler implements ValidateFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultValidateFailureHandler.class);

    private ObjectMapper objectMapper;

    public DefaultValidateFailureHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, VerificationException e) {
        String message = e.getMessage();
        try {
            AbstractVerificationProcessor.ResponseData result = new AbstractVerificationProcessor.ResponseData(HttpStatus.BAD_REQUEST.value(), message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
