package com.my.survivor.dto.req;

import lombok.Data;

@Data
public class SurvivorUpdateReq {

    /**
     * 头像地址
     * (目前只允许改这个，其他关键信息需申请管理员修改)
     */
    private String avatarUrl;
    
    // 如果未来有 个性签名 (signature) 也可以放这里
}