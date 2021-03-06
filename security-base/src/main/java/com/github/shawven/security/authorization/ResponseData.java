package com.github.shawven.security.authorization;

import java.io.Serializable;

/**
 * 自定义响应消息体

 *
 * @author Shoven
 * @date 2019-07-10
 */
public class ResponseData implements Serializable {

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
