package com.my.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "island.internal.header")
@Getter
@Setter
public class CommonIslandConfig {

    private String key;

    private String value;

}
