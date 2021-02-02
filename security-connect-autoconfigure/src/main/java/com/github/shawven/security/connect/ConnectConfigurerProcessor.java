package com.github.shawven.security.connect;

/**
 * 社交配置器的处理器 不同环境下的社交配置不一样
 *
 * @author Shoven
 * @date 2019-04-20
 */
public interface ConnectConfigurerProcessor {

    /**
     * 处理配置器
     *
     * @param configurer
     */
    void proceed(ConnectConfigurer configurer);
}
