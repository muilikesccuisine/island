package com.my.gateway.filter;

import com.my.common.config.CommonIslandConfig;
import com.my.common.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 职责变更：仅负责从 SecurityContext 提取用户信息并传递给下游
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final CommonIslandConfig commonIslandConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .map(auth -> {
                    // --- 使用 Decorator 绕过 Builder 异常 ---
                    ServerHttpRequest originalRequest = exchange.getRequest();

                    // 1. 手动创建一个新的可写 HttpHeaders，并复制原有 Header
                    HttpHeaders newHeaders = new HttpHeaders();
                    newHeaders.putAll(originalRequest.getHeaders());
                    newHeaders.set(SecurityConstants.HEADER_USER_ID, (String) auth.getPrincipal());
                    newHeaders.set(commonIslandConfig.getKey(), commonIslandConfig.getValue());

                    // 2. 使用 Decorator 包装原请求，重写 getHeaders 方法
                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(originalRequest) {
                        @Override
                        public HttpHeaders getHeaders() {
                            return newHeaders;
                        }
                    };
                    // 3. 将包装后的请求放入 Exchange
                    return exchange.mutate().request(decoratedRequest).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}