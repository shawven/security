
package com.github.shawven.security.connect.provider.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.connect.provider.qq.QQConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;


public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

	private String appId;

	private String openId;

	public QQImpl(String accessToken, String appId) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		this.appId = appId;
		String url = QQConfig.OPENID_URL + "?access_token=" + accessToken;
		String result = getRestTemplate().getForObject(url, String.class);
		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
	}


	@Override
	public QQUserInfo getUserInfo() {
		String url = QQConfig.USER_INFO_URL + "?oauth_consumer_key=" + appId+ "&openid=" + openId;
		String result = getRestTemplate().getForObject(url, String.class);
		try {
            QQUserInfo userInfo = new ObjectMapper().readValue(result, QQUserInfo.class);
			userInfo.setOpenId(openId);
			return userInfo;
		} catch (Exception e) {
			throw new RuntimeException("获取用户信息失败", e);
		}
	}

}
