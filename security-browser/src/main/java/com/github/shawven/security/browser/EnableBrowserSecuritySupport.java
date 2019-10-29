package com.github.shawven.security.browser;

import com.github.shawven.security.browser.config.BrowserConfiguration;
import com.github.shawven.security.browser.properties.BrowserProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {BrowserConfiguration.class, BrowserProperties.class})
public @interface EnableBrowserSecuritySupport {
}
