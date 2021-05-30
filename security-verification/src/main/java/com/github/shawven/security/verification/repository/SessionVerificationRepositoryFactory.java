package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;

import javax.servlet.http.HttpServletRequest;

public class SessionVerificationRepositoryFactory implements VerificationRepositoryFactory{

    @Override
    public VerificationRepository create(HttpServletRequest request, VerificationType type) {
        return new SessionVerificationRepository(request ,type);
    }
}
