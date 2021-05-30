package com.example.gateway.support;

import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.security.oauth2.proxy.OAuth2TokenRelayFilter;
import org.springframework.cloud.security.oauth2.proxy.ProxyAuthenticationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomerOAuth2TokenRelayFilter extends OAuth2TokenRelayFilter {

    public CustomerOAuth2TokenRelayFilter(ProxyAuthenticationProperties properties) {
        super(properties);
    }

    @Override
    public int filterOrder() {
        return 11;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();
        requestHeaders.put("Authorization", requestHeaders.remove("authorization"));
        return null;
    }
}
