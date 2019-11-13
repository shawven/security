
package com.github.shawven.security.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.verification.config.VerificationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 校验验证码的过滤器
 */

public class VerificationFilter extends OncePerRequestFilter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(VerificationFilterProviderConfigurer.class);

    /**
     * 系统中的校验码处理器
     */
    private VerificationProcessorHolder verificationProcessorHolder;

    private List<VerificationConfiguration> configurations;

    /**
     * 存放所有需要校验验证码的url
     */
    private Map<String, VerificationType> urlMap;
    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher;

    private ObjectMapper objectMapper;

    private List<String> whiteList;

    public VerificationFilter(VerificationProcessorHolder verificationProcessorHolder,
                              List<VerificationConfiguration> configurations) {
        urlMap = new HashMap<>();
        pathMatcher = new AntPathMatcher();
        objectMapper = new ObjectMapper();
        this.verificationProcessorHolder = verificationProcessorHolder;
        this.configurations = configurations;
    }

    /**
     * 初始化要拦截的url配置信息
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        whiteList = new ArrayList<>(configurations.size());
        for (VerificationConfiguration configuration : configurations) {
            if (configuration == null) {
                continue;
            }

            String className = configuration.getClass().getSimpleName();
            String prefix = StringUtils.substringBefore(className, "Configuration").toUpperCase();
            // 把自己放入白名单
            whiteList.add("/verification/" + prefix.toLowerCase());
            // 将系统中配置的需要校验验证码的URL根据校验的类型放入map
            // 这里如果多个处理器拦截同一个url会被覆盖，只有一个生效，参考具体配置时的顺序
            String urlString = configuration.getUrl();
            if (StringUtils.isNotBlank(urlString)) {
                String[] urls = StringUtils.split(urlString, ",");
                for (String url : urls) {
                    urlMap.put(url, VerificationType.valueOf(prefix));
                }
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isProcessorUrl(request)) {
            return;
        }
        VerificationType type = getVerificationType(request);
        if (type != null) {
            String name = type.getName();
            logger.info("正在校验" + name + "验证码");
            try {
                verificationProcessorHolder.get(type)
                        .validate(new ServletWebRequest(request, response));
                logger.info(name + "验证码校验通过");
            } catch (VerificationException e) {
                String message = e.getMessage();
                logger.info(name + "校验失败：{}", message);
                responseErrorMessage(response, message);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void responseErrorMessage(HttpServletResponse response, String message) {
        try {
            ResponseData result = new ResponseData(HttpStatus.BAD_REQUEST.value(), message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取校验码的类型，如果当前请求不需要校验，则返回null
     *
     * @param request
     * @return
     */
    private VerificationType getVerificationType(HttpServletRequest request) {
        VerificationType result = null;
        Set<String> urls = urlMap.keySet();
        for (String url : urls) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                result = urlMap.get(url);
            }
        }
        return result;
    }

    /**
     * 是否处理器的url
     * @param request
     * @return
     */
    private boolean isProcessorUrl(HttpServletRequest request) {
        for (String url : whiteList) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, VerificationType> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, VerificationType> urlMap) {
        this.urlMap = urlMap;
    }
}
