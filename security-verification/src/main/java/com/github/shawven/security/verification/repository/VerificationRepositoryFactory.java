package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;

import javax.servlet.http.HttpServletRequest;

public interface VerificationRepositoryFactory {

    VerificationRepository create(HttpServletRequest request, VerificationType type);
}
