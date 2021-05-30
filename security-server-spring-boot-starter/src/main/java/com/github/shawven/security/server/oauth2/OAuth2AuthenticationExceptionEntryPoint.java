package com.github.shawven.security.server.oauth2;

import com.github.shawven.security.authorization.MessageConstants;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

/**
 * @author Shoven
 * @date 2018/11/2 10:48
 */
public class OAuth2AuthenticationExceptionEntryPoint extends OAuth2AuthenticationEntryPoint {

    private final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationExceptionEntryPoint.class);

    @Override
    protected ResponseEntity<ResponseData> enhanceResponse(ResponseEntity<?> response, Exception e) {
        logger.debug(e.getMessage(), e);

        ResponseData data;
        int i;
        if ((i = ExceptionUtils.indexOfThrowable(e, InvalidTokenException.class)) != -1) {
            InvalidTokenException invalidException = (InvalidTokenException)ExceptionUtils.getThrowableList(e).get(i);
            data = invalidException.getMessage().contains("expired")
                    ? Responses.expiredAccessToken()
                    : Responses.invalidAccessToken();
        } else if (ExceptionUtils.indexOfThrowable(e, BadCredentialsException.class) != -1) {
            data = Responses.badClientCredentials();
        } else {
            if (MessageConstants.UNAUTHORIZED.equals(e.getMessage())) {
                data = Responses.requireLogin();
            } else {
                data = new ResponseData().setMessage(e.getMessage()).setCode(response.getStatusCodeValue());
            }
        }
        return ResponseEntity
                .status(response.getStatusCodeValue())
                .headers(response.getHeaders())
                .body(data);
    }

}
