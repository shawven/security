package com.github.shawven.security.server.connect;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

import java.io.Serializable;

/**
 * @author Shoven
 * @date 2020-03-14
 */
public class ConnectionDTO implements Serializable {

    private static final long serialVersionUID = -6927960069246364686L;

    private String nickname;

    private String avatar;

    private String providerUserId;

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

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public static ConnectionDTO from(Connection<?> connection) {
        ConnectionData data = connection.createData();
        ConnectionDTO result = new ConnectionDTO();
        result.setNickname(data.getDisplayName());
        result.setAvatar(data.getImageUrl());
        result.setProviderUserId(data.getProviderUserId());
        return result;
    }
}

