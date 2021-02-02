
package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.Verification;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 基于session的验证码存取器
 *
 * @author Shoven
 * @date 2019-05-08
 */
public class SessionVerificationRepository implements VerificationRepository {

	/**
	 * 验证码放入session时的前缀
	 */
	public static String VERIFICATION_CODE_KEY = "VERIFICATION_CODE_KEY";

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
        return VERIFICATION_CODE_KEY + verificationCodeType.toString().toUpperCase();
    }
    private HttpSession getSession(HttpServletRequest request) {
	    return request.getSession();
    }
}
