
package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验码生成器
 *
 */
public interface VerificationGenerator<T extends Verification> {

	/**
	 * 生成校验码
	 * @param request
	 * @return
	 */
    T generate(VerificationRequest<T> request);

}
