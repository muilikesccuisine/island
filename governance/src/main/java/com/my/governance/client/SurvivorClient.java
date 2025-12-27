package com.my.governance.client;

import com.my.governance.dto.req.ChangeGradeReq;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange("http://survivor/inner/survivor")
public interface SurvivorClient {

    /**
     * 更新幸存者等级
     */
    @PostExchange("/grade/change")
    Mono<Void> updateSurvivorGrade(@RequestBody @Valid ChangeGradeReq req);

    /**
     * 获取岛民总数
     */
    @GetExchange("/count")
    Mono<Integer> getSurvivorCount();
}
