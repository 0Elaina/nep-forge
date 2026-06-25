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

    public static final int BUILD_STATUS_DRAFT = 0; // 草稿
    public static final int BUILD_STATUS_NORMAL = 1; // 正常
    public static final int BUILD_STATUS_OFFLINE = 2; // 下架

    public static final int PUBLIC = 1; // 公开
    public static final int PRIVATE = 0; // 私有

    public static final int ARTICLE_STATUS_DRAFT = 0; // 草稿
    public static final int ARTICLE_STATUS_PUBLISHED = 1; // 已发布
    public static final int ARTICLE_STATUS_OFFLINE = 2; // 下架

    public static final int TARGET_TYPE_ARTICLE = 1; // 文章
    public static final int TARGET_TYPE_HARDWARE = 2; // 硬件
    public static final int TARGET_TYPE_BUILD = 3; // 装机单
    public static final int TARGET_TYPE_COMMENT = 4; // 评论

    public static final int ACTION_TYPE_LIKE = 1; // 点赞
    public static final int ACTION_TYPE_FAVORITE = 2; // 收藏

    public static final long DEFAULT_FAVORITE_FOLDER_ID = 0L; // 默认收藏夹ID

}
