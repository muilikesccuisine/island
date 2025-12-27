package com.my.task.client;

import com.my.common.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@HttpExchange("http://survivor/inner/survivor")
public interface SurvivorClient {

    @GetMapping("/grade/{id}")
    Mono<Result<Integer>> getGrade(@PathVariable("id") Long id);

    @PostMapping("/points/add")
    Mono<Result<Void>> addPoints(@RequestBody Map<String, Object> req);
}
