package com.github.shawven.security.verification.autoconfigure;

import com.github.shawven.security.authorization.HttpSecurityConfigurer;
import com.github.shawven.security.verification.VerificationFilter;
import com.github.shawven.security.verification.VerificationFilterPostProcessor;
import com.github.shawven.security.verification.VerificationProcessor;
import com.github.shawven.security.verification.captcha.CaptchaGenerator;
import com.github.shawven.security.verification.captcha.CaptchaProcessor;
import com.github.shawven.security.verification.config.VerificationConfig;
import com.github.shawven.security.verification.repository.*;
import com.github.shawven.security.verification.sms.DefaultSmsSender;
import com.github.shawven.security.verification.sms.SmsGenerator;
import com.github.shawven.security.verification.sms.SmsProcessor;
import com.github.shawven.security.verification.sms.SmsSender;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(VerificationProperties.class)
public class VerificationAutoConfiguration {

    /**
     * 短信验证码生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsGenerator smsGenerator(VerificationProperties properties) {
        return new SmsGenerator(properties.getSms());
    }

    /**
     * 短信验证码发送器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsSender smsSender() {
        return new DefaultSmsSender();
    }

    /**
     * 图片验证码图片生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CaptchaGenerator captchaGenerator(VerificationProperties properties) {
        return new CaptchaGenerator(properties.getCaptcha());
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
    public SmsProcessor smsProcessor(VerificationRepositoryFactory repositoryFactory,
                                     SmsGenerator smsGenerator, SmsSender smsSender) {
        return new SmsProcessor(repositoryFactory, smsGenerator, smsSender);
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
    public CaptchaProcessor imageProcessor(VerificationRepositoryFactory repositoryFactory,
                                           CaptchaGenerator captchaGenerator) {
        return new CaptchaProcessor(repositoryFactory, captchaGenerator);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(HttpSecurityConfigurer.class)
    public static class HttpSecuritySupportConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        @ConditionalOnMissingBean(name = "verificationSecurityConfigurer")
        public HttpSecurityConfigurer verificationSecurityConfigurer(VerificationProperties properties,
                                                                     List<VerificationProcessor> processors,
                                                                     List<VerificationFilterPostProcessor> filterPostProcessors) {
            ArrayList<VerificationConfig> configurations = new ArrayList<>();
            configurations.add(properties.getCaptcha());
            configurations.add(properties.getSms());
            VerificationFilter filter = new VerificationFilter(processors, configurations);
            for (VerificationFilterPostProcessor filterPostProcessor : filterPostProcessors) {
                filterPostProcessor.proceed(filter);
            }
            return new HttpSecurityConfigurer() {
                @Override
                public void configure(HttpSecurity http) throws Exception {
                    http.addFilterAfter(filter, AbstractPreAuthenticatedProcessingFilter.class);
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass(value = "com.github.shawven.security.authorization.HttpSecurityConfigurer")
    public static class WebFilterSupportConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        @ConditionalOnMissingBean
        public Filter verificationFilter(VerificationProperties properties,
                                         List<VerificationProcessor> processors,
                                         List<VerificationFilterPostProcessor> filterPostProcessors) {
            ArrayList<VerificationConfig> configurations = new ArrayList<>();
            configurations.add(properties.getCaptcha());
            configurations.add(properties.getSms());
            VerificationFilter filter = new VerificationFilter(processors, configurations);
            for (VerificationFilterPostProcessor filterPostProcessor : filterPostProcessors) {
                filterPostProcessor.proceed(filter);
            }
            return filter;
        }
    }

    /**
     * redis支持配置
     */
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureOrder(1)
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisSupportConfiguration {

        /**
         * 基于redis的验证码存取器
         *
         * @param redisTemplate
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public VerificationRepositoryFactory redisVerificationRepository(RedisTemplate<String, Object> redisTemplate) {
            return new RedisVerificationRepositoryFactory(redisTemplate);
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
    public VerificationRepositoryFactory verificationRepository() {
        return new SessionVerificationRepositoryFactory();
    }
}

