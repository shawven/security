
package com.github.shawven.security.connect.provider.qq.connet;

import com.github.shawven.security.connect.provider.qq.QQConfig;
import com.github.shawven.security.connect.provider.qq.api.QQ;
import com.github.shawven.security.connect.provider.qq.api.QQImpl;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;


public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

	private String appId;

	public QQServiceProvider(String appId, String appSecret) {
		super(new QQOAuth2Template(appId, appSecret, QQConfig.AUTHORIZE_URL, QQConfig.ACCESS_TOKEN_URL));
		this.appId = appId;
	}

	@Override
	public QQ getApi(String accessToken) {
		return new QQImpl(accessToken, appId);
	}

}
