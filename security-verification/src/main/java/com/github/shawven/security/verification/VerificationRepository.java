
package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验码存取器
 */
public interface VerificationRepository {

	/**
	 * 保存验证码
     *  @param request
     * @param verification
     * @param type
     */
	void save(HttpServletRequest request, Verification verification, VerificationType type);

	/**
	 * 获取验证码
     *
	 * @param request
	 * @param type
	 * @return
	 */
    Verification get(HttpServletRequest request, VerificationType type);

	/**
	 * 移除验证码
     * @param request
     * @param type
     */
	void remove(HttpServletRequest request, VerificationType type);

    /**
     * 获取持久化的key
     *
     * @return
     */
	String getKey(HttpServletRequest request, VerificationType type);
}
