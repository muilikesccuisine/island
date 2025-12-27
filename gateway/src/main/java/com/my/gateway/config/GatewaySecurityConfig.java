package com.my.gateway.config;

import com.my.common.constant.SecurityConstants;
import com.my.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class GatewaySecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 1. 定义认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());

        return http
                // 1. 关键修改：在 Security 层面开启 CORS，并指定配置源
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // 在认证位置加入自定义的 JWT 过滤器
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        // 2. 关键修改：放行所有 OPTIONS 请求（预检请求不带 Token）
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        // 放行登录、注册等接口
                        .pathMatchers("/island/auth/**").permitAll()
                        // 其他接口需要认证
                        .anyExchange().authenticated()
                )
                // 自定义认证失败处理（返回 401 JSON）
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint((exchange, e) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                )
                .build();
    }

    /**
     * 配置跨域规则
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");   // 允许所有方法 (GET, POST, PUT, DELETE, OPTIONS)
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*");   // 允许所有 Header
        config.setAllowCredentials(true); // 允许携带 Cookie/认证头

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 认证管理器：负责校验 Token
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();

            try {
                String userId = jwtUtil.getUserIdFromToken(token);

                if (userId != null) {
                    // 认证成功，返回一个带有 Authority 的 Authentication 对象
                    return Mono.just(new UsernamePasswordAuthenticationToken(
                            userId,
                            null, // 凭证已验证，可以设为 null
                            Collections.singletonList(new SimpleGrantedAuthority(SecurityConstants.ROLE_USER))
                    ));
                }
            } catch (Exception e) {
                log.error("校验Token错误:", e);
            }
            // 不是我们处理的类型，忽略
            return Mono.empty();
        };
    }

    /**
     * 转换器：从请求头提取 Token
     */
    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String token = exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
            if (token != null && token.startsWith(SecurityConstants.BEARER_PREFIX)) {
                token = token.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
            }
            return Mono.empty();
        };
    }
}