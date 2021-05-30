
package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验码处理器，封装不同校验码的处理逻辑
 */
public interface VerificationProcessor<T extends Verification> {

	/**
	 * 创建校验码
	 *
	 * @param data
	 * @throws Exception
	 */
	void processed(VerificationRequest<T> data);

	/**
	 * 校验验证码
	 *
	 * @param request
	 * @throws Exception
	 */
	void validate(HttpServletRequest request);

}
