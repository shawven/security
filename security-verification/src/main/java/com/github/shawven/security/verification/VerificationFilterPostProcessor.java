package com.github.shawven.security.verification;

import com.github.shawven.security.verification.VerificationFilter;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@FunctionalInterface
public interface VerificationFilterPostProcessor {

    void proceed(VerificationFilter filter);
}
