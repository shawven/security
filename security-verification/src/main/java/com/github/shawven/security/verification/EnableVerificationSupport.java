package com.github.shawven.security.verification;

import com.github.shawven.security.verification.config.VerificationSupportConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(VerificationSupportConfiguration.class)
public @interface EnableVerificationSupport {
}
