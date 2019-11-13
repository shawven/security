
package com.github.shawven.security.connect;

import com.github.shawven.security.authorization.AuthenticationFilterProviderConfigurer;
import com.github.shawven.security.connect.config.ConnectConfiguration;
import com.github.shawven.security.connect.config.QQConfiguration;
import com.github.shawven.security.connect.config.WeixinConfiguration;
import com.github.shawven.security.connect.provider.qq.connet.QQConnectionFactory;
import com.github.shawven.security.connect.provider.weixin.connect.WeixinConnectionFactory;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;

import javax.sql.DataSource;
import java.util.List;

/**
 * 社交登录配置主类
 */
@Configuration
@EnableSocial
@EnableConfigurationProperties(ConnectProperties.class)
public class ConnectAutoConfiguration extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ConnectProperties properties;

	@Autowired(required = false)
    private List<ConnectionFactory<?>> connectionFactories;

    @Autowired(required = false)
	private ConnectionSignUp connectionSignUp;

	@Autowired(required = false)
	private ConnectAuthenticationFilterPostProcessor connectAuthenticationFilterPostProcessor;

    @Autowired(required = false)
    private ConnectConfigurerProcessor connectConfigurerProcessor;

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        MyJdbcUsersConnectionRepository repository = new MyJdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());

		// 配置无感知注册处理程序，用户第一次登陆时需要注册的，调用该程序处理
        // org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository.findUserIdsWithConnection
        // 上面那个类里面判断了如果当前社交用户还不存在当前系统且配置了 connectionSignUp
        // 那么就可以调用去注册，并且返回系统的userId
        // 如果没有配置就会在org.springframework.social.security.SocialAuthenticationFilter.doAuthentication
        // 跳转到SocialConfigurer.signupUrl()配置的注册页面
        if(connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        if (connectionFactories != null) {
            connectionFactories.forEach(connectionFactoryConfigurer::addConnectionFactory);
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security.connect.weixin", name = "app-id")
    public ConnectionFactory<?> createWeixinConnectionFactory() {
        WeixinConfiguration weixinConfig = getConnectConfiguration().getWeixin();
        return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
                weixinConfig.getAppSecret());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security.connect.qq", name = "app-id")
    public ConnectionFactory<?> createQqConnectionFactory() {
        QQConfiguration qqConfig = getConnectConfiguration().getQq();
        return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(),
                qqConfig.getAppSecret());
    }

    /**
	 * 社交登录配置类，供浏览器或app模块引入设计登录配置用。
	 * @return
	 */
	@Bean
	public AuthenticationFilterProviderConfigurer connectFilterProviderConfigurer() {
        ConnectConfiguration connectConfiguration = getConnectConfiguration();
        // 设置过滤器拦截社交登录的url
		String filterProcessesUrl = connectConfiguration.getFilterProcessesUrl();
        ConnectConfigurer configurer = new ConnectConfigurer(filterProcessesUrl);

		// 设置社交登录判断是第一次登录时需要跳转的页面，需要引导用户进行注册或绑定
		// 如果没有配置 connectionSignUp 那么 org.springframework.social.security.SocialAuthenticationFilter.doAuthentication
        // 的方法会跳转到这里配置的注册页面
		configurer.signupUrl(connectConfiguration.getSignUpUrl());

		// 设置过滤器链的后处理器，例如app环境下的成功处理器与浏览器环境会不同
		configurer.setConnectAuthenticationFilterPostProcessor(connectAuthenticationFilterPostProcessor);

		// 不同环境下的社交配置不一样
		if (connectConfigurerProcessor != null) {
            connectConfigurerProcessor.proceed(configurer);
        }
		return new ConnectFilterProviderConfigurer(configurer);
	}


    /**
     * 用来处理注册流程的工具类
     *
     * @param connectionFactoryLocator
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
        return new ProviderSignInUtils(connectionFactoryLocator,
                getUsersConnectionRepository(connectionFactoryLocator));
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
                                               ConnectionRepository connectionRepository) {
        return new ConnectController(connectionFactoryLocator, connectionRepository);
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean
    public RedisSingInUtils appSingUpUtils(RedisTemplate<Object, Object> redisTemplate,
                                           UsersConnectionRepository usersConnectionRepository,
                                           ConnectionFactoryLocator connectionFactoryLocator) {
        return new RedisSingInUtils(redisTemplate, usersConnectionRepository, connectionFactoryLocator);
    }

    /**
     * 默认认证器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SocialUserDetailsService socialUserDetailsService() {
        return new DefaultSocialUserDetailsService();
    }

    @Bean(name = "connect/status")
    @ConditionalOnMissingBean
    public AppConnectionStatusView appConnectionStatusView() {
        return new AppConnectionStatusView();
    }


    @Bean
    public AuthorizationConfigureProvider connectAuthorizationConfigurerProvider() {
        return new ConnectAuthorizationConfigureProvider(getConnectConfiguration());
    }

    private ConnectConfiguration getConnectConfiguration() {
        ConnectConfiguration cfg = new ConnectConfiguration();
        cfg.setFilterProcessesUrl(properties.getFilterProcessesUrl());
        cfg.setQq(properties.getQq());
        cfg.setWeixin(properties.getWeixin());
        cfg.setSignUpUrl(properties.getSignUpUrl());
        return cfg;
    }
}
