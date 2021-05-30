package com.github.shawven.security.verification.sms;

import com.github.shawven.security.verification.Verification;
import com.github.shawven.security.verification.VerificationRepository;
import com.github.shawven.security.verification.VerificationType;
import com.github.shawven.security.verification.repository.VerificationRepositoryFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.github.shawven.security.verification.VerificationConstants.*;

public class SmsVerificationRepositoryFactoryWrapper implements VerificationRepositoryFactory {

    private VerificationRepositoryFactory repositoryFactory;

    public SmsVerificationRepositoryFactoryWrapper(VerificationRepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public VerificationRepository create(HttpServletRequest request, VerificationType type) {
        VerificationRepository delegate = repositoryFactory.create(request, type);
        return new VerificationRepository() {
            @Override
            public void save(String key, Verification verification) {
                delegate.save(key, verification);
            }

            @Override
            public Verification get(String key) {
                return delegate.get(key);
            }

            @Override
            public void remove(String key) {
                delegate.remove(key);
            }

            @Override
            public String getKey() {
                // 先尝试获取手机号
                Object attribute = request.getAttribute(PHONE_ATTRIBUTE_NAME);
                String uniqueId = attribute != null ? attribute.toString() : null;
                if (StringUtils.isBlank(uniqueId)) {
                    uniqueId = request.getParameter(PHONE_PARAMETER_NAME);
                }
                if (StringUtils.isNotBlank(uniqueId)) {
                    return REDIS_CODE_KEY + ":" + type.getLabel() + ":" + uniqueId;
                }
                return delegate.getKey();
            }
        };
    }


}
