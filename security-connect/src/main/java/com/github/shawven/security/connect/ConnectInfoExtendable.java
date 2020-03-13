
package com.github.shawven.security.connect;

import org.springframework.social.connect.Connection;

/**
 * 社交信息扩展方法
 *
 * @author Shoven
 * @date 2019-11-08
 */
public class ConnectInfoExtendable {

	/**
	 * 根据Connection信息构建SocialUserInfo
     *
	 * @param connection
	 * @return
	 */
	protected ConnectUserInfo buildSocialUserInfo(Connection<?> connection) {
		ConnectUserInfo userInfo = new ConnectUserInfo();
		userInfo.setProviderId(connection.getKey().getProviderId());
		userInfo.setProviderUserId(connection.getKey().getProviderUserId());
		userInfo.setNickname(connection.getDisplayName());
		userInfo.setAvatar(connection.getImageUrl());
		return userInfo;
	}

}
