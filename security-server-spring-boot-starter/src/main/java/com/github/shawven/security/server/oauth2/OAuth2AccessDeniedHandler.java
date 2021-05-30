package com.github.shawven.security.server.oauth2;

import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 访问没有权限（用户已登录没有访问权限）
 *
 * @author Shoven
 * @date 2018/11/1 18:15
 *
 */
public class OAuth2AccessDeniedHandler extends org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler {

    private final Logger logger = LoggerFactory.getLogger(OAuth2AccessDeniedHandler.class);

    @Override
    protected ResponseEntity<ResponseData> enhanceResponse(ResponseEntity<?> result, Exception e) {
        logger.debug(e.getMessage(), e);

        ResponseData data;
        if (e instanceof AccessDeniedException) {
            data = Responses.accessDenied();
        } else {
            OAuth2Exception auth2Exception = (OAuth2Exception) result.getBody();
            String message = auth2Exception != null && auth2Exception.getMessage() != null
                    ? auth2Exception.getMessage()
                    : e.getMessage();
            data = new ResponseData().setCode(result.getStatusCodeValue()).setMessage(message);
        }
        return ResponseEntity
                .status(result.getStatusCodeValue())
                .headers(result.getHeaders())
                .body(data);
    }

}
