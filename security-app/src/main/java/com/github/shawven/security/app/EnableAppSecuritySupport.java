package com.github.shawven.security.app;

import com.github.shawven.security.app.config.AppConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {AppConfiguration.class})
public @interface EnableAppSecuritySupport {
}
