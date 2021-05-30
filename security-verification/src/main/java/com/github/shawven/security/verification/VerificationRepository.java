
package com.github.shawven.security.verification;

/**
 * 校验码存取器
 */
public interface VerificationRepository {

	/**
	 * 保存验证码
     *  @param request
     * @param verification
     * @param type
     */
	default void save(Verification verification) {
	    save(getKey(), verification);
    }

    void save(String key, Verification verification);


    default Verification get() {
        return get(getKey());
    }

    /**
     * 获取验证码
     *
     * @return
     */
    Verification get(String key);

	/**
	 * 移除验证码
     */
	default void remove() {
	    remove(getKey());
    }

    void remove(String key);

    String getKey();
}
