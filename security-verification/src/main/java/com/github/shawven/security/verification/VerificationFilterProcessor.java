package com.github.shawven.security.verification;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@FunctionalInterface
public interface VerificationFilterProcessor {

    void proceed(VerificationFilter filter);
}
