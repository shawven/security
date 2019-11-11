package com.github.shawven.security.app;

import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.ConnectInfoExtendable;
import com.github.shawven.security.connect.ConnectUserInfo;
import com.github.shawven.security.connect.RedisSingInUtils;
import com.github.shawven.security.oauth2.OAuth2AutoConfiguration;
import com.github.shawven.security.oauth2.OAuth2Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@ConditionalOnClass(OAuth2AutoConfiguration.class)
public class AppOAuth2Endpoint {

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
        String message;
        try {
            result = tokenEndpoint.postAccessToken(principal, parameters);
        } catch (Exception e) {
            String error = null;
            if (e instanceof InvalidGrantException) {
                if ("坏的凭证".equals(e.getMessage())) {
                    error = "用户名或密码信息错误";
                }
            }
            message = error != null ? error: e.getMessage();

            ResponseData response = new ResponseData()
                    .setCode(HttpStatus.UNAUTHORIZED.value())
                    .setMessage(message);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
        message = "refresh_token".equals(parameters.get("grant_type"))
                ? "刷新token成功"
                : "获取token成功";
        return ResponseEntity
                .status(result.getStatusCodeValue())
                .headers(result.getHeaders())
                .body(new ResponseData().setMessage(message).setData(result.getBody()));
    }



}
