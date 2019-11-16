package com.github.shawven.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shawven.security.authorization.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 客户端认证过滤器，自带的无法控制响应格式
 *
 * @author Shoven
 * @date 2018/11/5 11:38
 */
public class ClientCredentialsTokenEndpointFilterPostProcessor
        implements ObjectPostProcessor<ClientCredentialsTokenEndpointFilter> {

    private AuthenticationEntryPoint entryPoint;

    public ClientCredentialsTokenEndpointFilterPostProcessor(AuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public ClientCredentialsTokenEndpointFilter postProcess(ClientCredentialsTokenEndpointFilter filter) {
        filter.setAllowOnlyPost(true);
        filter.setAuthenticationFailureHandler((request, response, e) -> {
            ResponseData data = new ResponseData()
                    .setCode(HttpStatus.UNAUTHORIZED.value())
                    .setMessage(e.getMessage());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(data));
        });
        filter.setAuthenticationEntryPoint(entryPoint);
        return filter;
    }
}
