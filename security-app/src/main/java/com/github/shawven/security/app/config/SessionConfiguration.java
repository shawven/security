
package com.github.shawven.security.app.config;


/**
 * session管理相关配置项
 */
public class SessionConfiguration {

	/**
	 * 同一个用户在系统中的最大session数
	 */
	private int maximumSessions;
	/**
	 * 达到最大session时是否阻止新的登录请求，不阻止新的登录会将老的登录失效掉
	 */
	private boolean maxSessionsPreventsLogin;

	public int getMaximumSessions() {
		return maximumSessions;
	}

	public void setMaximumSessions(int maximumSessions) {
		this.maximumSessions = maximumSessions;
	}

	public boolean isMaxSessionsPreventsLogin() {
		return maxSessionsPreventsLogin;
	}

	public void setMaxSessionsPreventsLogin(boolean maxSessionsPreventsLogin) {
		this.maxSessionsPreventsLogin = maxSessionsPreventsLogin;
	}
}
