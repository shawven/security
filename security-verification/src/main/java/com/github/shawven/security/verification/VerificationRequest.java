package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Shoven
 * @date 2019-11-15
 */
public abstract class VerificationRequest<T extends Verification> {

    /**
     * 过期时间秒
     */
    private Integer expireIn;

    private String requestId;

    private HttpServletRequest request;

    private HttpServletResponse response;

    public VerificationRequest(HttpServletRequest request, HttpServletResponse response) {
        this.requestId = getUUID();
        this.request = request;
        this.response = response;
    }

    public Integer getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(Integer expireIn) {
        this.expireIn = expireIn;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
