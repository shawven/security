
package com.github.shawven.security.connect.autoconfigure;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.connect.*;
import com.github.shawven.security.connect.config.ConnectProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;

import javax.sql.DataSource;
import java.util.List;

/**
 * 社交登录配置主类
 */
@Configuration(proxyBeanMethods = false)
@EnableSocial
@EnableConfigurationProperties(ConnectProperties.class)
@Import(ConnectProviderConfiguration.class)
public class ConnectAutoConfiguration extends SocialConfigurerAdapter {

	private DataSource dataSource;

	private ConnectProperties connectProperties;

    private List<ConnectionFactory<?>> connectionFactories;

	private ConnectionSignUp connectionSignUp;

	private ObjectProvider<ConnectAuthenticationFilterPostProcessor> connectAuthenticationFilterPostProcessorProvider;

    public ConnectAutoConfiguration(DataSource dataSource,
                                    List<ConnectionFactory<?>> connectionFactories,
                                    ConnectProperties connectProperties,
                                    ObjectProvider<ConnectionSignUp> connectionSignUpProvider,
                                    ObjectProvider<ConnectAuthenticationFilterPostProcessor> connectAuthenticationFilterPostProcessorProvider) {
        this.dataSource = dataSource;
        this.connectProperties = connectProperties;
        this.connectionFactories = connectionFactories;
        this.connectionSignUp = connectionSignUpProvider.getIfAvailable();
        this.connectAuthenticationFilterPostProcessorProvider = connectAuthenticationFilterPostProcessorProvider;
    }

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


    /**
	 * 社交登录配置类，供浏览器或app模块引入设计登录配置用。
	 * @return
	 */
	@Bean
    @ConditionalOnMissingBean(name = "connectSecurityConfigurer")
	public HttpSecurityConfigurer connectSecuritySupportConfigurer() {
        ConnectConfigurer configurer = new ConnectConfigurer();
        // 设置过滤器拦截社交登录的url
        configurer.setFilterProcessesUrl(connectProperties.getFilterProcessesUrl());
		// 设置社交登录判断是第一次登录时需要跳转的页面，需要引导用户进行注册或绑定
		// 如果没有配置 connectionSignUp 那么 org.springframework.social.security.SocialAuthenticationFilter.doAuthentication
        // 的方法会跳转到这里配置的注册页面
		configurer.signupUrl(connectProperties.getSignUpUrl());
		// 设置过滤器链的后处理器，例如app环境下的成功处理器与浏览器环境会不同
		configurer.setConnectAuthenticationFilterPostProcessor(connectAuthenticationFilterPostProcessorProvider);
		return new ConnectSecurityConfigurer(configurer);
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




    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public RedisSignInUtils redisSignInUtils(RedisTemplate<String, Object> redisTemplate,
                                                 UsersConnectionRepository usersConnectionRepository,
                                                 ConnectionFactoryLocator connectionFactoryLocator) {
            return new RedisSignInUtils(redisTemplate, usersConnectionRepository, connectionFactoryLocator);
        }
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

}
