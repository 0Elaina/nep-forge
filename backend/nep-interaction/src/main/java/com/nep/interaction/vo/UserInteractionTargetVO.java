package com.nep.interaction.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInteractionTargetVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 交互ID
    private String interactionId;

    // 目标ID
    private String targetId;

    // 目标类型 1: 文章 2: 配件 3: 装机单 4: 评论
    private Integer targetType;

    // 操作类型 1: 点赞 2: 收藏
    private Integer actionType;

    // 收藏夹ID, 点赞时默认为0
    private String folderId;

    // 收藏夹名称, 点赞时默认为空
    private String folderName;

    // 目标标题
    private String title;

    // 目标品牌
    private String brand;

    // 目标封面图片
    private String coverImage;

    // 目标状态
    private Integer targetStatus;

    /**
     * 前端可直接根据该路径跳转。
     */
    private String targetPath;

    // 创建时间
    private LocalDateTime createTime;
}
