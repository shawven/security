package com.github.shawven.security.verification;

import com.github.shawven.security.authorization.AuthenticationFilterProvider;
import com.github.shawven.security.authorization.AuthorizationConfigureProvider;
import com.github.shawven.security.verification.captcha.CaptchaGenerator;
import com.github.shawven.security.verification.captcha.CaptchaProcessor;
import com.github.shawven.security.verification.config.VerificationConfiguration;
import com.github.shawven.security.verification.sms.DefaultSmsSender;
import com.github.shawven.security.verification.sms.SmsGenerator;
import com.github.shawven.security.verification.sms.SmsProcessor;
import com.github.shawven.security.verification.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Configuration
@EnableConfigurationProperties(VerificationProperties.class)
public class VerificationAutoConfiguration {

    @Autowired
    private VerificationProperties properties;

    /**
     * 短信验证码生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsGenerator smsCodeGenerator() {
        return new SmsGenerator(properties.getSms());
    }

    /**
     * 短信验证码发送器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsSender smsCodeSender() {
        return new DefaultSmsSender();
    }

    /**
     * 图片验证码图片生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CaptchaGenerator imageCodeGenerator() {
        return new CaptchaGenerator(properties.getCaptcha());
    }


    @Configuration
    @AutoConfigureOrder(1)
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisSupportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public VerificationRepository redisVerificationRepository(RedisTemplate<Object, Object> redisTemplate) {
            return new RedisVerificationRepository(redisTemplate);
        }
    }

    /**
     * 基于session的验证码存取器
     *
     * @return
     */
    @Bean
    @AutoConfigureOrder(2)
    @ConditionalOnMissingBean
    public VerificationRepository sessionVerificationRepository() {
        return new SessionVerificationRepository();
    }


    /**
     * 短信验证码处理器
     *
     * @param verificationRepository
     * @param smsGenerator
     * @param smsSender
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsProcessor smsProcessor(VerificationRepository verificationRepository,
                                         SmsGenerator smsGenerator,
                                         SmsSender smsSender) {
        return new SmsProcessor(verificationRepository, smsGenerator, smsSender);
    }

    /**
     * 图形验证码处理器
     *
     * @param verificationRepository
     * @param captchaGenerator
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CaptchaProcessor imageProcessor(VerificationRepository verificationRepository,
                                               CaptchaGenerator captchaGenerator) {
        return new CaptchaProcessor(verificationRepository, captchaGenerator);
    }

    /**
     * 验证码处理器持有者
     *
     * @param processors
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public VerificationProcessorHolder verificationProcessorHolder(List<VerificationProcessor> processors) {
        return new VerificationProcessorHolder(processors);
    }

    /**
     * 验证码校验过滤器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public VerificationFilter verificationFilter(VerificationProcessorHolder verificationProcessorHolder) {
        ArrayList<VerificationConfiguration> configurations = new ArrayList<>();
        configurations.add(properties.getCaptcha());
        configurations.add(properties.getSms());
        return new VerificationFilter(verificationProcessorHolder, configurations);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    @ConditionalOnBean
    public AuthorizationConfigureProvider verificationAuthorizationConfigureProvider() {
        return new VerificationAuthorizationConfigureProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFilterProvider verificationFilterProvider(VerificationFilter filter) {
        return new VerificationFilterProvider(filter);
    }

}

