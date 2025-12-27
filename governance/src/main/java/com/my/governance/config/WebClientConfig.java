package com.my.auth.config;

import com.my.auth.client.SurvivorClient;
import com.my.common.config.CommonIslandConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final CommonIslandConfig commonIslandConfig;

    @Bean
    @LoadBalanced // 如果将来使用 Nacos/Eureka，需要这个注解实现负载均衡
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {
        // 使用 builder 直接构建，不指定 baseUrl，由 Interface 上的注解指定
        WebClient webClient = builder
                .defaultHeader(commonIslandConfig.getKey(), commonIslandConfig.getValue())
                .build();
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
    }

    @Bean
    public SurvivorClient survivorClient(HttpServiceProxyFactory factory) {
        return factory.createClient(SurvivorClient.class);
    }
}