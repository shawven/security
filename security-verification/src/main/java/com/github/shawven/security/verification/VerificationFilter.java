
package com.github.shawven.security.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import com.github.shawven.security.verification.config.VerificationConfiguration;
import com.github.shawven.security.verification.security.VerificationSecuritySupportConfigurer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

/**
 * 校验验证码的过滤器
 */

public class VerificationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(VerificationSecuritySupportConfigurer.class);

    /**
     * 处理器
     */
    private Map<VerificationType, VerificationProcessor> processorMap;

    /**
     * 存放所有需要校验验证码的url
     */
    private Map<String, VerificationType> urlMap;
    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher;

    private ObjectMapper objectMapper;

    public VerificationFilter(List<VerificationProcessor> processorMap,
                              List<VerificationConfiguration> configurations) {
        this.processorMap = transformProcessor(processorMap);
        this.urlMap = transformUrlMap(configurations);
        this.pathMatcher = new AntPathMatcher();
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        VerificationType type = getVerificationType(request);
        if (type != null) {
            String name = type.getName();
            logger.info("正在校验" + name + "验证码");
            try {
                getProcessor(type).validate(request);
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
     * 转换拦截路径
     *
     * @param configurations
     * @return
     */
    private Map<String, VerificationType> transformUrlMap(List<VerificationConfiguration> configurations)  {
        Map<String, VerificationType> urlMap = new HashMap<>();
        for (VerificationConfiguration configuration : configurations) {
            if (configuration == null) {
                continue;
            }
            String className = configuration.getClass().getSimpleName();
            String prefix = StringUtils.substringBefore(className, "Configuration").toUpperCase();
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
        return urlMap;
    }

    /**
     * 转换处理器
     *
     * @param processors
     * @return
     */
    private Map<VerificationType, VerificationProcessor> transformProcessor(List<VerificationProcessor> processors) {
        if (processors == null || processors.isEmpty()) {
            return emptyMap();
        }
        Map<VerificationType, VerificationProcessor> processorMap = new HashMap<>();
        for (VerificationProcessor processor : processors) {
            String className = processor.getClass().getSimpleName();
            String typeName = StringUtils.substringBefore(className, "Processor").toUpperCase();
            processorMap.put(VerificationType.valueOf(typeName), processor);
        }
        return processorMap;
    }

    /**
     * @param type
     * @return
     */
    private VerificationProcessor getProcessor(VerificationType type) {
        VerificationProcessor processor = processorMap.get(type);
        if (processor == null) {
            throw new VerificationException(type.getName() + "非法校验请求");
        }
        return processor;
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

    public Map<String, VerificationType> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, VerificationType> urlMap) {
        this.urlMap = urlMap;
    }
}
