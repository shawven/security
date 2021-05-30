
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

    private HttpServletRequest request;
    private VerificationType type;

    public SessionVerificationRepository(HttpServletRequest request, VerificationType type) {
        this.request = request;
        this.type = type;
    }

    @Override
	public void save(String key, Verification verification) {
        getSession(request).setAttribute(key, verification);
	}

	@Override
	public Verification get(String key) {
		return (Verification) getSession(request).getAttribute(key);
	}

	@Override
	public void remove(String key) {
        getSession(request).removeAttribute(key);
	}

    @Override
    public String getKey() {
        return VERIFICATION_CODE_KEY + type.toString().toUpperCase();
    }

    private HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }
}
