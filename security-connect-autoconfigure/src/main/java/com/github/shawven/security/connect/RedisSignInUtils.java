package com.github.shawven.security.connect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis环境下的providerSignInUtils，避免由于没有session导致读不到社交用户信息的问题
 */
public class RedisSignInUtils {

	private RedisTemplate<String, Object> redisTemplate;

	private UsersConnectionRepository usersConnectionRepository;

	private ConnectionFactoryLocator connectionFactoryLocator;

    public RedisSignInUtils(RedisTemplate<String, Object> redisTemplate,
                            UsersConnectionRepository usersConnectionRepository,
                            ConnectionFactoryLocator connectionFactoryLocator) {
        this.redisTemplate = redisTemplate;
        this.usersConnectionRepository = usersConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    /**
	 * 缓存社交网站用户信息到redis
	 * @param request
	 * @param connectionData
	 */
	public void saveConnectionData(WebRequest request, ConnectionData connectionData) {
		redisTemplate.opsForValue().set(getKey(request), connectionData, 30, TimeUnit.MINUTES);
	}

	/**
	 * 将缓存的社交网站用户信息与系统注册用户信息绑定
     * @param request
     * @param userId
     * @return
     */
	public ConnectionData doPostSignUp(WebRequest request, String userId) {
		String key = getKey(request);
        ConnectionData connectionData  = (ConnectionData) redisTemplate.opsForValue().get(key);
        throwErrorIfConnectionDataIsNull(connectionData);

		Connection<?> connection = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId())
				.createConnection(connectionData);
		usersConnectionRepository.createConnectionRepository(userId).addConnection(connection);

		redisTemplate.delete(key);

		return connectionData;
	}

	public void saveWxMiniConnection(WebRequest request, Map<String, Object> map) {
        redisTemplate.opsForValue().set(getKey(request), map, 30, TimeUnit.MINUTES);
    }

    public ConnectionData doPostSignUpForWxMini(WebRequest request, String userId) {
        String key = getKey(request);
        ConnectionData connectionData  = (ConnectionData) redisTemplate.opsForValue().get(key);
        throwErrorIfConnectionDataIsNull(connectionData);
        // todo

        return null;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public UsersConnectionRepository getUsersConnectionRepository() {
        return usersConnectionRepository;
    }

    public ConnectionFactoryLocator getConnectionFactoryLocator() {
        return connectionFactoryLocator;
    }

    /**
     * 获取redis key
     * @param request
     * @return
     */
    private String getKey(WebRequest request) {
        String requestId = request.getHeader("Request-Id");
        if (StringUtils.isBlank(requestId)) {
            throw new IllegalArgumentException("请求头缺少Request-Id，值为临时会话唯一ID");
        }
        return "security:connect:" + requestId;
    }

    private void throwErrorIfConnectionDataIsNull(ConnectionData connectionData) {
	    if (connectionData == null) {
            throw new IllegalArgumentException("长时间未注册该链接已失效，请重新登录在绑定");
        }
    }
}


