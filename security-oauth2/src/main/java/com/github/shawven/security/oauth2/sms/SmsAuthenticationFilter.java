
package com.github.shawven.security.oauth2.sms;

import com.github.shawven.security.verification.VerificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录过滤器
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	// ~ Static fields/initializers
	// =====================================================================================

	private String phoneParameter = VerificationConstants.DEFAULT_PHONE_PARAMETER_NAME;

	// ~ Constructors
	// ===================================================================================================

	public SmsAuthenticationFilter(SmsConfiguration config) {
		super(new AntPathRequestMatcher(config.getFilterProcessingUrl(), "POST"));
	}

	// ~ Methods
	// ========================================================================================================

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (!"POST".equals(request.getMethod())) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String mobile = request.getParameter(phoneParameter);
        if (StringUtils.isBlank(mobile)) {
            throw new IllegalArgumentException("参数错误，手机号码不能为空");
        }

		SmsAuthenticationToken smsAuthenticationToken = new SmsAuthenticationToken(mobile.trim());
        smsAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

		return this.getAuthenticationManager().authenticate(smsAuthenticationToken);
	}

	/**
	 * Sets the parameter name which will be used to obtain the username from
	 * the login request.
	 *
	 * @param usernameParameter
	 *            the parameter name. Defaults to "username".
	 */
	public void setPhoneParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.phoneParameter = usernameParameter;
	}

	public final String getPhoneParameter() {
		return phoneParameter;
	}

}
