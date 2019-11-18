
package com.github.shawven.security.verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.github.shawven.security.verification.VerificationConstants.*;


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
        return REDIS_CODE_KEY + ":" + type.getLabel() + ":" + uniqueId;
	}

    private String getUniqueId(HttpServletRequest request, VerificationType type) {
        String uniqueId = null;
	    // 先尝试获取手机号
        if (type == VerificationType.SMS) {
            Object attribute = request.getAttribute(PHONE_ATTRIBUTE_NAME);
            if (attribute != null) {
                uniqueId = String.valueOf(attribute);
            }
            if (StringUtils.isBlank(uniqueId)) {
                uniqueId = request.getParameter(PHONE_PARAMETER_NAME);
            }
        }
        if (StringUtils.isNotBlank(uniqueId)) {
            if (!Pattern.compile("[1]([3-9])[0-9]{9}").matcher(uniqueId).matches()) {
                throw new VerificationException("手机号：" + uniqueId + "错误");
            }
            return uniqueId;
        } else {
            // 再尝试获取session id
            if (StringUtils.isBlank(uniqueId = request.getHeader(REDIS_SESSION_ID_PARAMETER_NAME))
                    && StringUtils.isBlank(uniqueId = request.getParameter(REDIS_SESSION_ID_PARAMETER_NAME))) {
                if (type == VerificationType.SMS) {
                    throw new VerificationException("请求头或参数中缺少" + PHONE_PARAMETER_NAME +"，值可以为手机号");
                }
                throw new VerificationException("请在请求头或参数中缺少" + REDIS_SESSION_ID_PARAMETER_NAME
                        + "，值可以为代表当前用户临时身份的任意唯一ID");
            }
            return uniqueId;
        }
    }
}
