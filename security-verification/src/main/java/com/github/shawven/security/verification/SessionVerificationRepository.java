
package com.github.shawven.security.verification;

import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 基于session的验证码存取器
 *
 * @author Shoven
 * @since 2019-05-08 21:51
 */
public class SessionVerificationRepository implements VerificationRepository {

	/**
	 * 验证码放入session时的前缀
	 */
	public static String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";

	@Override
	public void save(HttpServletRequest request, Verification verification, VerificationType verificationCodeType) {
        getSession(request).setAttribute(getKey(request, verificationCodeType), verification);
	}

	@Override
	public Verification get(HttpServletRequest request, VerificationType verificationCodeType) {
		return (Verification) getSession(request).getAttribute(getKey(request, verificationCodeType));
	}

	@Override
	public void remove(HttpServletRequest request, VerificationType codeType) {
        getSession(request).removeAttribute(getKey(request, codeType));
	}

    @Override
    public String getKey(HttpServletRequest request, VerificationType verificationCodeType) {
        return SESSION_KEY_PREFIX + verificationCodeType.toString().toUpperCase();
    }
    private HttpSession getSession(HttpServletRequest request) {
	    return request.getSession();
    }
}
