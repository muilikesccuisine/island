package com.my.survivor.config;

import com.my.common.config.CommonIslandConfig;
import com.my.common.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SurvivorSecurityConfig {

    private final CommonIslandConfig commonIslandConfig;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // 在认证阶段之前，执行我们的“头部信息读取”过滤器
                .addFilterAt(headerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        // 允许 Actuator
                        .pathMatchers("/actuator/**").permitAll()
                        // 所有业务接口都需要认证（即必须有 Header）
                        .anyExchange().authenticated()
                )
                .build();
    }

    /**
     * 自定义 Filter：从 Header 读取 X-User-Id 并校验内部 Token
     */
    private WebFilter headerAuthenticationFilter() {
        return (exchange, chain) -> {
            // 1. 先获取内部通信 Token
            String internalToken = exchange.getRequest().getHeaders().getFirst(commonIslandConfig.getKey());

            // 2. 只要内部密钥对上了，我们就认为“认证通过”（放行）
            if (StringUtils.equals(commonIslandConfig.getValue(), internalToken)) {
                String userId = exchange.getRequest().getHeaders().getFirst(SecurityConstants.HEADER_USER_ID);
                Authentication auth = getAuthentication(userId);

                // 填充上下文并继续
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            }

            // 3. 密钥不对 -> 不填充上下文 -> 后续 Security 拦截并报 401
            return chain.filter(exchange);
        };
    }

    private static Authentication getAuthentication(String userId) {
        Authentication auth;

        if (userId != null) {
            // 情况 A：这是网关转发过来的，带着用户身份 -> 赋予 ROLE_USER
            auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(SecurityConstants.ROLE_USER))
            );
        } else {
            // 情况 B：这是内部服务互调 (Feign)，没有用户ID -> 赋予 ROLE_INTERNAL
            // 这样既能通过 .authenticated() 检查，又能区分出这不是一个真实用户
            auth = new UsernamePasswordAuthenticationToken(
                    SecurityConstants.INTERNAL_SYSTEM, // Principal
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(SecurityConstants.ROLE_INTERNAL))
            );
        }
        return auth;
    }
}