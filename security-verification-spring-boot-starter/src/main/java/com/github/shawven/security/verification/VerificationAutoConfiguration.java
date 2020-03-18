package com.github.shawven.security.verification;

import com.github.shawven.security.authorization.HttpSecuritySupportConfigurer;
import com.github.shawven.security.verification.captcha.CaptchaGenerator;
import com.github.shawven.security.verification.captcha.CaptchaProcessor;
import com.github.shawven.security.verification.config.VerificationConfiguration;
import com.github.shawven.security.verification.repository.*;
import com.github.shawven.security.verification.repository.SpringSecurityContextKeyExtractor;
import com.github.shawven.security.verification.security.VerificationSecuritySupportConfigurer;
import com.github.shawven.security.verification.sms.DefaultSmsSender;
import com.github.shawven.security.verification.sms.SmsGenerator;
import com.github.shawven.security.verification.sms.SmsProcessor;
import com.github.shawven.security.verification.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Configuration
@EnableConfigurationProperties(VerificationProperties.class)
public class VerificationAutoConfiguration {

    private VerificationProperties properties;

    public VerificationAutoConfiguration(VerificationProperties properties) {
        this.properties = properties;
    }

    /**
     * 短信验证码生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsGenerator smsGenerator() {
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
    public CaptchaGenerator captchaGenerator() {
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
    public SmsProcessor smsProcessor(VerificationRepository verificationRepository,
                                     SmsGenerator smsGenerator, SmsSender smsSender) {
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


    @Configuration
    @ConditionalOnMissingClass("org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration")
    public static class NotSecuritySupportConfiguration {

        private VerificationProperties properties;

        private List<VerificationProcessor> processors;

        public NotSecuritySupportConfiguration(VerificationProperties properties,
                                               List<VerificationProcessor> processors) {
            this.properties = properties;
            this.processors = processors;
        }

        /**
         * 验证码校验过滤器
         *
         * @return
         */
        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        @ConditionalOnMissingBean
        public VerificationFilter verificationFilter() {
            ArrayList<VerificationConfiguration> configurations = new ArrayList<>();
            configurations.add(properties.getCaptcha());
            configurations.add(properties.getSms());
            return new VerificationFilter(processors, configurations);
        }
    }

    @Configuration
    @ConditionalOnClass(WebSecurityConfiguration.class)
    public static class SecuritySupportConfiguration {

        private VerificationProperties properties;

        private List<VerificationProcessor> processors;

        private List<VerificationFilterPostProcessor> filterPostProcessors;

        public SecuritySupportConfiguration(VerificationProperties properties,
                                            List<VerificationProcessor> processors,
                                            List<VerificationFilterPostProcessor> filterPostProcessors) {
            this.properties = properties;
            this.processors = processors;
            this.filterPostProcessors = filterPostProcessors;
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        @ConditionalOnMissingBean(name = "verificationSecuritySupportConfigurer")
        public HttpSecuritySupportConfigurer verificationSecuritySupportConfigurer() {
            ArrayList<VerificationConfiguration> configurations = new ArrayList<>();
            configurations.add(properties.getCaptcha());
            configurations.add(properties.getSms());
            VerificationFilter filter = new VerificationFilter(processors, configurations);
            for (VerificationFilterPostProcessor filterPostProcessor : filterPostProcessors) {
                filterPostProcessor.proceed(filter);
            }
            return new VerificationSecuritySupportConfigurer(filter);
        }
    }


    /**
     * redis支持配置
     */
    @Configuration
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
        public VerificationRepository redisVerificationRepository(RedisTemplate redisTemplate) {
            RedisVerificationRepository repository = new RedisVerificationRepository(redisTemplate);
            repository.setKeyFunction(new SpringSecurityContextKeyExtractor().get());
            return repository;
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
    public VerificationRepository verificationRepository() {
        return new SessionVerificationRepository();
    }
}

