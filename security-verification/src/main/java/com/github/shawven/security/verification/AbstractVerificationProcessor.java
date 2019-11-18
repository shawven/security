
package com.github.shawven.security.verification;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * 抽象的图片验证码处理器
 *
 * @author Shoven
 */
public abstract class AbstractVerificationProcessor<T extends Verification> implements VerificationProcessor<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractVerificationProcessor.class);

	private VerificationRepository verificationRepository;

	private VerificationGenerator<T> verificationGenerator;

    public AbstractVerificationProcessor(VerificationRepository verificationRepository,
                                         VerificationGenerator<T> verificationGenerator) {

        this.verificationRepository = verificationRepository;
        this.verificationGenerator = verificationGenerator;
    }

    @Override
	public void processed(VerificationRequest<T> verificationRequest) {
        Objects.requireNonNull(verificationRequest);
        try {
            // 生成校验码
            T verification = verificationGenerator.generate(verificationRequest);
            HttpServletRequest request = verificationRequest.getRequest();
            // 保存最基本的信息
            Verification base = new Verification(verification.getCode(), verification.getExpireTime());
            verificationRepository.save(request, base, getVerificationType(request));

            // 发送校验码
            send(verificationRequest, verification);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void validate(HttpServletRequest request) {
        VerificationType codeType = getVerificationType(request);
        Verification verificationInSession = verificationRepository.get(request, codeType);

        String codeInRequest = request.getParameter(codeType.getLabel());
        if (StringUtils.isBlank(codeInRequest)) {
            throw new VerificationException("请输入" + codeType.getName() + "验证码");
        }

        if (verificationInSession == null || verificationInSession.isExpired()) {
            verificationRepository.remove(request, codeType);
            throw new VerificationException(codeType.getName() + "验证码已过期");
        }

        if (!StringUtils.equalsIgnoreCase(verificationInSession.getCode(), codeInRequest)) {
            throw new VerificationException(codeType.getName() + "验证码错误");
        }

        verificationRepository.remove(request, codeType);
    }


	/**
	 * 发送校验码，由子类实现
	 *
	 * @param verificationRequest
	 * @param verification
	 * @throws Exception
	 */
	protected abstract void send(VerificationRequest<T> verificationRequest, T verification) throws Exception;

	/**
	 * 根据请求的url获取校验码的类型
	 *
	 * @param request
	 * @return
	 */
	public VerificationType getVerificationType(HttpServletRequest request) {
		String type = StringUtils.substringBefore(getClass().getSimpleName(), "Processor");
		return VerificationType.valueOf(type.toUpperCase());
	}
}
