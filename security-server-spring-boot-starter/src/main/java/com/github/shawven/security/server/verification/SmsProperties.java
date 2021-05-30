package com.github.shawven.security.server.verification;

import com.github.shawven.security.verification.config.SmsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "app.security.verification.sms")
public class SmsProperties extends SmsConfig {

}
