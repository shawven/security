package com.github.shawven.security.verification.authentication;

/**
 * @author Shoven
 * @date 2019-11-17
 */
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SmsAuthenticationConfiguration.class})
public @interface EnableSmsAuthentication {
}
