package com.github.shawven.security.oauth2;

/**
 * token签名配置
 *
 * @author Shoven
 * @date 2019-12-15
 */
public class Oauth2JwtProperties {

    /**
     * 对称加密签名密钥
     */
    private String signingKey;

    /**
     * 非对称加密，jks文件路径
     */
    private String keyStore;

    /**
     * 非对称加密，别名
     */
    private String keyAlias;

    /**
     * 非对称加密，密钥库的密码
     */
    private String keyStorePassword;

    /**
     * 非对称加密，私钥的密码
     */
    private String keyPassword;

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
}
