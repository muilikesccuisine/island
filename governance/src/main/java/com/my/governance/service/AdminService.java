package com.my.governance.service;

import com.my.governance.dto.req.AdminReq;
import reactor.core.publisher.Mono;

public interface AdminService {

    /**
     * 任命管理员
     */
    Mono<Void> appointAdmin(AdminReq req);

    /**
     * 撤职/辞职管理员
     */
    Mono<Void> dismissAdmin(AdminReq req);
}
