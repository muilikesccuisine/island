package com.my.task.config;

import com.my.common.config.CommonIslandConfig;
import com.my.task.client.SurvivorClient;
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
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {
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
