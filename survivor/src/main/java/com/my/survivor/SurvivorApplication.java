package com.my.survivor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication(scanBasePackages = {"com.my.survivor", "com.my.common"})
@EnableR2dbcAuditing
public class SurvivorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurvivorApplication.class, args);
    }

}
