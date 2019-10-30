package com.github.shawven.security.social;

import com.github.shawven.security.social.autoconfigure.OAuth2Configuration;
import com.github.shawven.security.social.autoconfigure.SocialConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Shoven
 * @date 2019-08-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {SocialConfiguration.class, SocialConfiguration.class, OAuth2Configuration.class})
public @interface EnableOAuth2Support {
}
