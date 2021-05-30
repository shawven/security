package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;

public class RedisVerificationRepositoryFactory implements VerificationRepositoryFactory {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisVerificationRepositoryFactory(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public VerificationRepository create(HttpServletRequest request, VerificationType type) {
        return new RedisVerificationRepository(request, redisTemplate, type);
    }
}
