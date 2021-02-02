
package com.github.shawven.security.connect.provider.weixin.connect;

import com.github.shawven.security.connect.provider.weixin.WeixinConfig;
import com.github.shawven.security.connect.provider.weixin.api.Weixin;
import com.github.shawven.security.connect.provider.weixin.api.WeixinImpl;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/**
 *
 * 微信的OAuth2流程处理器的提供器，供spring social的connect体系调用
 */
public class WeixinServiceProvider extends AbstractOAuth2ServiceProvider<Weixin> {

	/**
	 * @param appId
	 * @param appSecret
	 */
	public WeixinServiceProvider(String appId, String appSecret) {
		super(new WeixinOAuth2Template(appId, appSecret, WeixinConfig.AUTHORIZE_URL,
                WeixinConfig.ACCESS_TOKEN_URL,  WeixinConfig.REFRESH_TOKEN_URL));
	}



	@Override
	public Weixin getApi(String accessToken) {
		return new WeixinImpl(accessToken);
	}

}
