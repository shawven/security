package com.github.shawven.security.verification.sms;

import com.github.shawven.security.verification.Verification;

import java.time.LocalDateTime;

/**
 * @author Shoven
 * @date 2019-08-16
 */
public class Sms extends Verification {

    private static final long serialVersionUID = -4629437541504397425L;

    private String phone;

    private String message;

    public Sms(String phone, String message, String code, int expireIn) {
        super(code, expireIn);
        this.phone = phone;
        this.message = message;
    }

    public Sms(String phone, String message, String code, LocalDateTime expireTime) {
        super(code, expireTime);
        this.phone = phone;
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
