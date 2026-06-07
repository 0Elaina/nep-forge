package com.nep.common.constants;

/**
 * 数据库字段常量类。
 * 集中管理逻辑删除、启用状态等跨模块的通用字段值。
 * 
 * @author Neptune
 * @date 2026-06-07
 */
public final class FieldConstant {
    private FieldConstant(){}

    public static final int NOT_DELETED = 0; // 未删除
    public static final int DELETED = 1; // 已删除

    public static final int ENABLED = 1; // 已启用
    public static final int DISABLED = 0; // 已禁用
}
