package com.nep.common.constants;

/**
 * 角色常量类
 * 定义了系统中使用的角色常量，用于权限验证和授权。
 */
public final class RoleConstants {
    /**
     * 普通用户角色
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * 模版管理员角色
     */
    public static final String ROLE_MODERATOR = "ROLE_MODERATOR";

    /**
     * 系统管理员角色
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private RoleConstants(){}
}
