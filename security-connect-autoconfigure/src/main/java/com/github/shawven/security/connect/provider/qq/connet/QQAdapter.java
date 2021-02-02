
package com.github.shawven.security.connect.provider.qq.connet;

import com.github.shawven.security.connect.provider.qq.api.QQ;
import com.github.shawven.security.connect.provider.qq.api.QQUserInfo;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;


public class QQAdapter implements ApiAdapter<QQ> {

	@Override
	public boolean test(QQ api) {
		return true;
	}

	@Override
	public void setConnectionValues(QQ api, ConnectionValues values) {
		QQUserInfo userInfo = api.getUserInfo();
        values.setProviderUserId(userInfo.getOpenId());
        values.setDisplayName(userInfo.getNickname());
        values.setImageUrl(userInfo.getFigureurl_qq_1());
	}

	@Override
	public UserProfile fetchUserProfile(QQ api) {
        return UserProfile.EMPTY;
	}

	@Override
	public void updateStatus(QQ api, String message) {
		//do noting
	}

}
