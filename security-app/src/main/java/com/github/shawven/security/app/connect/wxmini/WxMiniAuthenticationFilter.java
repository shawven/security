package com.github.shawven.security.app.connect.wxmini;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.MyConnectionRepository;
import com.github.shawven.security.connect.RedisSignInUtils;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import com.github.shawven.security.connect.config.WeixinConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.FormMapHttpMessageConverter;
import org.springframework.social.support.LoggingErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Shoven
 * @since 2019-04-24 15:27
 */
public class WxMiniAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String providerId;

    private String appId;

    private String appSecret;

    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private ObjectMapper objectMapper = new ObjectMapper();

    private RedisSignInUtils redisSignInUtils;

    public WxMiniAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/connect/wxmini", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        Map<String, Object> result = getResponseOfLoginUser(request);

        String openId = String.valueOf(result.get("openid"));
        String sessionKey = String.valueOf(result.get("session_key"));
        try {
            WxMiniAuthenticationToken authenticationToken = new WxMiniAuthenticationToken(openId, providerId);
            authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication success = this.getAuthenticationManager().authenticate(authenticationToken);

            SocialUserDetails user = (SocialUserDetails) success.getPrincipal();
            ConnectionRepository connectionRepository = redisSignInUtils.getUsersConnectionRepository()
                    .createConnectionRepository(user.getUserId());

            ConnectionData connectionData = new ConnectionData(providerId, openId,
                    null, null, null, null, sessionKey, null, null);

            MyConnectionRepository repository = (MyConnectionRepository) connectionRepository;
            repository.updateConnection(connectionData);
            return success;
        } catch (AuthenticationException e) {
            result.remove("errcode");
            result.remove("errmsg");
            redisSignInUtils.saveWxMiniConnection(new ServletWebRequest(request), result);
            throw new SocialAuthenticationRedirectException(ConnectConstants.DEFAULT_CURRENT_USER_INFO_URL);
        }
    }

    private Map<String, Object> getResponseOfLoginUser(HttpServletRequest request) {
        // 获取小程序code
        String code = request.getParameter("code");

        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("参数错误，code不能为空");
        }

        String code2SessionUrl = BASE_URL +
                "?appid=" + appId +
                "&secret=" + appSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        String rsp = getRestTemplate().getForObject(code2SessionUrl, String.class);

        Map<String, Object> result;
        try {
            result = objectMapper.readValue(rsp, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        //返回错误码时直接返回空
        Object errCode = result.get("errcode");
        if(errCode != null && !"0".equals(errCode.toString())){
            String errMsg = String.valueOf(result.get("errmsg"));
            throw new IllegalArgumentException("获取weixin access token失败, errcode:" + errCode + ", errmsg:" + errMsg);
        }

        return result;
    }

    public void setConnectConfiguration(ConnectConfiguration configuration) {
        WeixinConfiguration weixin = configuration.getWeixin();
        appId = weixin.getAppId();
        appSecret = weixin.getAppSecret();
        providerId = "wxmini";
        setFilterProcessesUrl("/login/connect/wxmini");
    }

    public void setRedisSignInUtils(RedisSignInUtils redisSignInUtils) {
        this.redisSignInUtils = redisSignInUtils;
    }

    private RestTemplate getRestTemplate() {
        if (this.restTemplate == null) {
            this.restTemplate = this.createRestTemplate();
        }

        return this.restTemplate;
    }

    private RestTemplate createRestTemplate() {
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactorySelector.getRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> converters = new ArrayList<>(2);
        converters.add(new FormHttpMessageConverter());
        converters.add(new FormMapHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(converters);
        restTemplate.setErrorHandler(new LoggingErrorHandler());

        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
