
package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.Verification;
import com.github.shawven.security.verification.VerificationException;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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

    private BiFunction<HttpServletRequest, VerificationType, String> keyFunction;

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
        String uniqueId = null;
        if (keyFunction != null) {
            uniqueId = keyFunction.apply(request, type);
        }
        if (uniqueId == null) {
            uniqueId = tryExtractUniqueId(request, type);
        }
        if (uniqueId == null) {
            throw new UnsupportedOperationException("当前无法提取存储key值，请自行实现："
                    + RedisVerificationRepository.class.getSimpleName());
        }
        return REDIS_CODE_KEY + ":" + type.getLabel() + ":" + uniqueId;
    }

    private String tryExtractUniqueId(HttpServletRequest request, VerificationType type) {
        // 尝试获取requestId
        String uniqueId = request.getParameter(REQUEST_ID);
        if (StringUtils.isBlank(uniqueId)) {
            uniqueId = request.getHeader(REQUEST_ID);
        }
        if (StringUtils.isBlank(uniqueId)) {
            uniqueId = String.valueOf(request.getAttribute(REQUEST_ID));
        }
        if (StringUtils.isNotBlank(uniqueId)) {
            return uniqueId;
        }
        // 先尝试获取手机号
        if (type == VerificationType.SMS) {
            Object attribute = request.getAttribute(PHONE_ATTRIBUTE_NAME);
            if (attribute != null) {
                uniqueId = String.valueOf(attribute);
            }
            if (StringUtils.isBlank(uniqueId)) {
                uniqueId = request.getParameter(PHONE_PARAMETER_NAME);
            }
            if (!Pattern.compile("[1]([3-9])[0-9]{9}").matcher(uniqueId).matches()) {
                throw new VerificationException("手机号：" + uniqueId + "错误");
            }
            return uniqueId;
        }
        return null;
    }

    public void setKeyFunction(BiFunction<HttpServletRequest, VerificationType, String> keyFunction) {
        this.keyFunction = keyFunction;
    }
}
