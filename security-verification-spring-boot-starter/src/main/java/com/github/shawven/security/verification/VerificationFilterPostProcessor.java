package com.github.shawven.security.verification;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@FunctionalInterface
public interface VerificationFilterPostProcessor {

    void proceed(VerificationFilter filter);
}
