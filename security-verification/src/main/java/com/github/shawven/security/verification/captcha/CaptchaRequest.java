package com.github.shawven.security.verification.captcha;

import com.github.shawven.security.verification.VerificationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图形码请求
 * 此配置会覆盖配置文件的配置
 *
 * @author Shoven
 * @date 2019-11-15
 */
public class CaptchaRequest extends VerificationRequest<Captcha> {

    private Integer length;

    private Integer width;

    private Integer height;

    public CaptchaRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
