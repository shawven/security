
package com.github.shawven.security.connect.provider.weixin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.connect.provider.weixin.WeixinConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Weixin API调用模板， scope为Request的Spring bean, 根据当前用户的accessToken创建。
 */
public class WeixinImpl extends AbstractOAuth2ApiBinding implements Weixin {

	/**
	 * @param accessToken
	 */
	public WeixinImpl(String accessToken) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
	}

	/**
	 * 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
	 */
	@Override
    protected List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		messageConverters.remove(0);
		messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return messageConverters;
	}

	/**
	 * 获取微信用户信息。
	 */
	@Override
	public WeixinUserInfo getUserInfo(String openId) {
		String url = WeixinConfig.USER_INFO_URL+ "?openid=%s" + openId;
		String response = getRestTemplate().getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        if(response != null && response.contains("errcode")) {
            Map<String, Object> result;
            try {
                result = (Map<String, Object>)objectMapper.readValue(response, Map.class);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            String errcode = String.valueOf(result.get("errcode"));
            String errmsg = String.valueOf(result.get("errmsg"));
            throw new RuntimeException("获取weixin 用户信息失败, errCode:"+errcode+", errmsg:"+errmsg);
		}

		try {
            return objectMapper.readValue(response, WeixinUserInfo.class);
		} catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
		}
	}
}
