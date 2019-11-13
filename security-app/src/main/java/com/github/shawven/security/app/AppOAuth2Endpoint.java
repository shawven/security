package com.github.shawven.security.app;

import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import com.github.shawven.security.oauth2.OAuth2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@ConditionalOnClass(OAuth2AutoConfiguration.class)
public class AppOAuth2Endpoint {

    private final Logger logger = LoggerFactory.getLogger(AppOAuth2Endpoint.class);

    @Autowired
    private TokenEndpoint tokenEndpoint;


    /**
     * 覆盖默认的方法，格式化响应
     *
     * @param principal
     * @param parameters
     * @return
     */
    @ResponseBody
    @RequestMapping(value = OAuth2Constants.DEFAULT_OAUTH_TOKEN_ENDPOINTS, method = {POST, GET})
    public ResponseEntity postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) {
        ResponseEntity<OAuth2AccessToken> result;
        ResponseData data;
        boolean isRefresh = "refresh_token".equals(parameters.get("grant_type"));
        try {
            result = tokenEndpoint.postAccessToken(principal, parameters);
            data = isRefresh ? Responses.refreshTokenSuccess() : Responses.getTokenSuccess();
            data.setData(result.getBody());
            return ResponseEntity.status(HttpStatus.OK).headers(result.getHeaders()).body(data);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            if (isRefresh && e instanceof InvalidTokenException) {
                data = e.getMessage().contains("expired")
                        ? Responses.expiredRefreshToken()
                        : Responses.invalidRefreshToken();
            } else if (e instanceof InvalidGrantException && "坏的凭证".equals(e.getMessage())) {
                data = Responses.badCredentials();
            } else {
                data = new ResponseData()
                        .setCode(HttpStatus.UNAUTHORIZED.value())
                        .setMessage(e.getMessage());
            }
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(data);
        }
    }

}
