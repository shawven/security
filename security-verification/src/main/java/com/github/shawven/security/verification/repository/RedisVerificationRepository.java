
package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.Verification;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.github.shawven.security.verification.VerificationConstants.REDIS_CODE_KEY;
import static com.github.shawven.security.verification.VerificationConstants.REQUEST_ID;


/**
 * 基于redis的验证码存取器，避免由于没有session导致无法存取验证码的问题
 *
 * @author Shoven
 * @date 2019-05-08
 */
public class RedisVerificationRepository implements VerificationRepository {

    private HttpServletRequest request;
	private RedisTemplate<String, Object> redisTemplate;
    private VerificationType type;

    public RedisVerificationRepository(HttpServletRequest request,
                                       RedisTemplate<String, Object> redisTemplate,
                                       VerificationType type) {
        this.request = request;
        this.redisTemplate = redisTemplate;
        this.type = type;
    }


    @Override
	public void save(String key, Verification verification) {
        redisTemplate.opsForValue().set(key, verification, verification.getExpireIn(), TimeUnit.SECONDS);
	}

	@Override
	public Verification get(String key) {
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return null;
		}
		return (Verification) value;
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}


    @Override
    public String getKey() {
        // 尝试获取requestId
        String uniqueId= request.getHeader(REQUEST_ID);
        if (StringUtils.isBlank(uniqueId)) {
            uniqueId = request.getParameter(REQUEST_ID);
        }
        if (StringUtils.isBlank(uniqueId)) {
            Object attribute = request.getAttribute(REQUEST_ID);
            uniqueId = attribute != null ? attribute.toString() : null;
        }
        if (StringUtils.isBlank(uniqueId)) {
            throw new IllegalArgumentException("当前无法提取存储key值，请自行实现："
                    + RedisVerificationRepository.class.getSimpleName());
        }
        return REDIS_CODE_KEY + ":" + type.getLabel() + ":" + uniqueId;
    }

}
