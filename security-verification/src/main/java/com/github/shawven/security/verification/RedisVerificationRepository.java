
package com.github.shawven.security.verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


/**
 * 基于redis的验证码存取器，避免由于没有session导致无法存取验证码的问题
 *
 * @author Shoven
 * @since 2019-05-08 21:51
 */
public class RedisVerificationRepository implements VerificationRepository {

	private RedisTemplate redisTemplate;

    public RedisVerificationRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
	public void save(HttpServletRequest request, Verification verification, VerificationType type) {
        String key = getKey(request, type);
        redisTemplate.opsForValue().set(key, verification, verification.getExpireIn(), TimeUnit.SECONDS);
	}

	@Override
	public Verification get(HttpServletRequest request, VerificationType type) {
		Object value = redisTemplate.opsForValue().get(getKey(request, type));
		if (value == null) {
			return null;
		}
		return (Verification) value;
	}

	@Override
	public void remove(HttpServletRequest request, VerificationType type) {
		redisTemplate.delete(getKey(request, type));
	}

	/**
	 * @param request
	 * @param type
	 * @return
	 */
	@Override
    public String getKey(HttpServletRequest request, VerificationType type) {
        String uniqueId = getUniqueId(request, type);
        return "code:" + type.getLabel() + ":" + uniqueId;
	}

    private String getUniqueId(HttpServletRequest request, VerificationType type) {
        String uniqueId = null;
	    // 先尝试获取手机号
        if (type == VerificationType.SMS) {
            uniqueId = request.getParameter(VerificationConstants.DEFAULT_PHONE_PARAMETER_NAME);
            if (StringUtils.isBlank(uniqueId)) {
                uniqueId = String.valueOf(request.getAttribute(VerificationConstants.DEFAULT_PHONE_PARAMETER_NAME));
            }
        }
        if (StringUtils.isNotBlank(uniqueId)) {
            return uniqueId;
        } else {
            // 再尝试获取session id
            uniqueId = request.getHeader("sid");
            if (StringUtils.isBlank(uniqueId)) {
                uniqueId = request.getParameter("sid");
                if (StringUtils.isBlank(uniqueId) && type == VerificationType.SMS) {
                    throw new VerificationException("请求头或参数中缺少phone，值可以为手机号");
                }
                throw new VerificationException("请在请求头或参数中缺少sid，值可以为代表当前用户临时身份的任意唯一ID");
            }
            return uniqueId;
        }

    }
}
