package com.github.shawven.security.server;

import com.github.shawven.security.server.connect.ConnectionDTO;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.authorization.Responses;
import com.github.shawven.security.connect.ConnectConstants;
import com.github.shawven.security.connect.ConnectInfoExtendable;
import com.github.shawven.security.connect.ConnectUserInfo;
import com.github.shawven.security.connect.RedisSignInUtils;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.*;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author Shoven
 * @date 2019-11-11
 */
@RestController
public class GenericConnectEndpoint implements ConnectInfoExtendable {

    private RedisSignInUtils redisSignInUtils;

    private ProviderSignInUtils providerSignInUtils;

    private UsersConnectionRepository usersConnectionRepository;

    private ConnectionRepository connectionRepository;

    private ConnectionFactoryLocator connectionFactoryLocator;

    public GenericConnectEndpoint(RedisSignInUtils redisSignInUtils,
                                  ProviderSignInUtils providerSignInUtils,
                                  UsersConnectionRepository usersConnectionRepository,
                                  ConnectionRepository connectionRepository,
                                  ConnectionFactoryLocator connectionFactoryLocator) {
        this.redisSignInUtils = redisSignInUtils;
        this.providerSignInUtils = providerSignInUtils;
        this.usersConnectionRepository = usersConnectionRepository;
        this.connectionRepository = connectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    /**
     * 需要引导用户注册或绑定时，通过此服务获取当前社交用户的信息
     * 返回401（表示认证失败，第一次登陆）和用户信息
     * @param request
     * @return
     */
    @GetMapping(value = "connect/" + ConnectConstants.CONNECT_USER_INFO_PATH, produces = "application/json")
    public ResponseEntity getSocialUserInfo(HttpServletRequest request) {
        // 从请求中拿用户信息
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        // 用户信息存储到redis
        redisSignInUtils.saveConnectionData(new ServletWebRequest(request), connection.createData());
        // 构建用户信息
        ConnectUserInfo connectUserInfo = buildSocialUserInfo(connection);

        ResponseData response = Responses.firstLoginNeedBinding().setData(connectUserInfo);
        return ResponseEntity.unprocessableEntity().body(response);
    }

    @GetMapping(value = "connect", produces = "application/json")
    public ResponseEntity listConnections() {
        // 用户绑定的所有连接
        MultiValueMap<String, Connection<?>> map = connectionRepository.findAllConnections();
        Map<String, List<ConnectionDTO>> result = map.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue().stream().map(ConnectionDTO::from).collect(toList())))
                .collect(Pair.toMap());
        return ResponseEntity.ok(new ResponseData().setData(result));
    }

    @PostMapping(value ="connect/{providerId}", produces = "application/json")
    public ResponseEntity connect(HttpServletRequest request, @PathVariable String providerId, String code) {
        // 获取第三方信息并生成连接信息
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator
                .getConnectionFactory(providerId);
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
                request.getRequestURI(), null);
        Connection<?> connection = connectionFactory.createConnection(accessGrant);

        // 通过连接信息查找关联的用户ID
        HashSet<String> providerUserIds = new HashSet<>();
        providerUserIds.add(connection.getKey().getProviderUserId());
        Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(providerId, providerUserIds);
        int size = userIds.size();
        // 不存在
        if (size <= 0) {
            // 保存连接信息
            connectionRepository.addConnection(connection);
            return ResponseEntity.ok(Responses.bindSuccess());
        } else {
            return ResponseEntity.unprocessableEntity().body(Responses.duplicateBinding());
        }
    }

    @DeleteMapping(value ="connect/{providerId}/{providerUserId}", produces = "application/json")
    public ResponseEntity disconnect(@PathVariable String providerId, @PathVariable String providerUserId) {
        ConnectionKey connectionKey = new ConnectionKey(providerId, providerUserId);
        // 删除连接信息
        connectionRepository.removeConnection(connectionKey);
        return ResponseEntity.ok(Responses.unbindSuccess());
    }
}
