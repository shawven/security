
package com.github.shawven.security.verification;

import com.github.shawven.security.verification.repository.VerificationRepositoryFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Objects;


/**
 * 抽象的图片验证码处理器
 *
 * @author Shoven
 */
public abstract class AbstractVerificationProcessor<T extends Verification> implements VerificationProcessor<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractVerificationProcessor.class);

	private VerificationRepositoryFactory verificationRepositoryFactory;

	private VerificationGenerator<T> verificationGenerator;

    public AbstractVerificationProcessor(VerificationRepositoryFactory verificationRepositoryFactory,
                                         VerificationGenerator<T> verificationGenerator) {

        this.verificationRepositoryFactory = verificationRepositoryFactory;
        this.verificationGenerator = verificationGenerator;
    }

    @Override
	public void processed(VerificationRequest<T> verificationRequest) {
        Objects.requireNonNull(verificationRequest);
        try {
            // 生成校验码
            T verification = verificationGenerator.generate(verificationRequest);
            HttpServletRequest request = verificationRequest.getRequest();
            request.setAttribute(VerificationConstants.REQUEST_ID, verificationRequest.getRequestId());

            // 保存最基本的信息
            Verification base = new Verification(verification.getCode(), verification.getExpireTime());
            getVerificationRepository(request).save(base);

            // 发送校验码
            send(verificationRequest, verification);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void validate(HttpServletRequest request) {
        VerificationType codeType = getVerificationType(request);
        VerificationRepository repository = getVerificationRepository(request);
        String name = codeType.getName();
        Verification codeInStore = repository.get();

        String codeInRequest = request.getParameter(codeType.getLabel());
        if (StringUtils.isBlank(codeInRequest)) {
            throw new VerificationException("请输入" + name + "验证码");
        }

        if (codeInStore == null || codeInStore.isExpired()) {
            repository.remove();
            throw new VerificationException(name + "验证码已过期");
        }

        if (!StringUtils.equalsIgnoreCase(codeInStore.getCode(), codeInRequest)) {
            throw new VerificationException(name + "验证码错误");
        }

        repository.remove();
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
     * 获取存储
     *
     * @param request
     * @return
     */
    private VerificationRepository getVerificationRepository(HttpServletRequest request) {
        return verificationRepositoryFactory.create(request, getVerificationType(request));
    }

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

    public static class ResponseData implements Serializable {


        /**
         * 状态码
         */
        private int code = 200;

        /**
         * 消息
         */
        private String message;

        /**
         * 数据
         */
        private Object data;

        public ResponseData() { }

        public ResponseData(String message) {
            this.message = message;
        }

        public ResponseData(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ResponseData(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public ResponseData setCode(int code) {
            this.code = code;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public ResponseData setMessage(String message) {
            this.message = message;
            return this;
        }

        public Object getData() {
            return data;
        }

        public ResponseData setData(Object data) {
            this.data = data;
            return this;
        }
    }
}
