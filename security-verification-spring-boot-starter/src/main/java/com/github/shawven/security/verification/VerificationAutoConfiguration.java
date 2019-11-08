package com.github.shawven.security.verification;

import com.github.shawven.security.verification.captcha.CaptchaGenerator;
import com.github.shawven.security.verification.captcha.CaptchaProcessor;
import com.github.shawven.security.verification.config.VerificationConfiguration;
import com.github.shawven.security.verification.sms.DefaultSmsSender;
import com.github.shawven.security.verification.sms.SmsGenerator;
import com.github.shawven.security.verification.sms.SmsProcessor;
import com.github.shawven.security.verification.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnBean(VerificationRepository.class)
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
    @ConditionalOnBean(VerificationRepository.class)
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
    @ConditionalOnBean(VerificationProcessor.class)
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
    @ConditionalOnMissingBean
    public VerificationSecurityConfigurer verificationSecurityConfig(VerificationFilter filter) {
        return new VerificationSecurityConfigurer(filter);
    }

}

