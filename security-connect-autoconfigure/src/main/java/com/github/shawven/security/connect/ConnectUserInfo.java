package com.github.shawven.security.connect;

public class ConnectUserInfo {

	private String providerId;

	private String providerUserId;

	private String nickname;

	private String avatar;

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

    @Override
    public String toString() {
        return "ConnectUserInfo{" +
                "providerId='" + providerId + '\'' +
                ", providerUserId='" + providerUserId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
