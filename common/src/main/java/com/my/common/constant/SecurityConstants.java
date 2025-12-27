package com.my.common.constant;

public interface SecurityConstants {
    /**
     * 透传的用户ID请求头
     */
    String HEADER_USER_ID = "HEADER_USER_ID";

    /**
     * 透传的用户角色请求头
     */
    String HEADER_USER_ROLE = "X-User-Role";
    
    /**
     * 认证头
     */
    String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Token 前缀
     */
    String BEARER_PREFIX = "Bearer";

    String ROLE_USER = "ROLE_USER";

    String ROLE_INTERNAL = "ROLE_INTERNAL";

    String INTERNAL_SYSTEM = "INTERNAL_SYSTEM";




}