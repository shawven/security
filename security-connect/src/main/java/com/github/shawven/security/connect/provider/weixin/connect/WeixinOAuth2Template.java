
package com.github.shawven.security.connect.provider.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 完成微信的OAuth2认证流程的模板类。国内厂商实现的OAuth2每个都不同, spring默认提供的OAuth2Template适应不了，只能针对每个厂商自己微调。
 */
public class WeixinOAuth2Template extends OAuth2Template {

    private String clientId;

    private String clientSecret;

    private String accessTokenUrl;

    private String refreshTokenUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(getClass());


    public WeixinOAuth2Template(String clientId, String clientSecret, String authorizeUrl,
                                String accessTokenUrl, String refreshTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        setUseParametersForClientAuthentication(true);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessTokenUrl = accessTokenUrl;
        this.refreshTokenUrl = refreshTokenUrl;
    }


    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri,
                                         MultiValueMap<String, String> parameters) {

        StringBuilder sb = new StringBuilder(accessTokenUrl);
        sb.append("?appid=" + clientId);
        sb.append("&secret=" + clientSecret);
        sb.append("&code=" + authorizationCode);
        sb.append("&grant_type=authorization_code");
        sb.append("&redirect_uri=" + redirectUri);
        return getAccessToken(sb);
    }

    @Override
    public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
        StringBuilder sb = new StringBuilder(refreshTokenUrl);
        sb.append("?appid=" + clientId);
        sb.append("&grant_type=refresh_token");
        sb.append("&refresh_token=" + refreshToken);
        return getAccessToken(sb);
    }

    @SuppressWarnings("unchecked")
    private AccessGrant getAccessToken(StringBuilder accessTokenRequestUrl) {
//        String str = getRestTemplate().getForObject(accessTokenRequestUrl.toString(), String.class);
        String str = "{\"access_token\":\"27_0g5ZU-IECv-x4gxWOl7HuJ4n_I-YmEgWGvXNLUgFpWLz8gEZPMV9CIRKcQdHS17wDWBVVC2KaB0c78OOf84S-N1ZXW_LupBFY_Ok1W79fm4\",\"expires_in\":7200,\"refresh_token\":\"27_Z603A_WNrYaDPSO9uwMM0jgqdD9L-TSznsGw0Esu1eOLAl3X7r_maeDa8qmk1-gW8R3nwmhmCbCNaCtM6b3H85rvAFyZfYd06ZXFXcVaDhQ\",\"openid\":\"od4PTw7Iijvj9qAw3RtLXSUZpMOU\",\"scope\":\"snsapi_login\",\"unionid\":\"oEg8VuH4KoidJSzmviOKsY9n7igU\"}";
        if (str == null) {
            throw new IllegalArgumentException("获取weixin access token失败：无响应");
        }
        logger.info("获取weixin access_token响应: " + str);

        Map<String, Object> result;
        try {
            result = (Map<String, Object>)objectMapper.readValue(str, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        //返回错误码时直接返回空
        if (result.get("errcode") != null) {
            throw new IllegalArgumentException("获取weixin access token失败："+ result);
        }

        WeixinAccessGrant accessToken = new WeixinAccessGrant(
                result.get("access_token").toString(),
                result.get("scope").toString(),
                result.get("refresh_token").toString(),
                Long.valueOf(result.get("expires_in").toString()));

        accessToken.setOpenId(result.get("openid").toString());

        return accessToken;
    }

    /**
     * 构建获取授权码的请求。也就是引导用户跳转到微信的地址。
     *
     * @param parameters
     * @return
     */
    @Override
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        String url = super.buildAuthenticateUrl(parameters);
        url = url + "&appid=" + clientId + "&scope=snsapi_login";
        return url;
    }

    @Override
    public String buildAuthorizeUrl(OAuth2Parameters parameters) {
        return buildAuthenticateUrl(parameters);
    }

    /**
     * 微信返回的contentType是html/text，添加相应的HttpMessageConverter来处理。
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

}
