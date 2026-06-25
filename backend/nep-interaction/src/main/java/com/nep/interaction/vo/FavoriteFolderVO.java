package com.nep.interaction.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * 收藏夹VO
 */
@Data
@Builder
public class FavoriteFolderVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 收藏夹ID
    private String id;

    // 收藏夹名称
    private String name;

    // 收藏夹描述
    private String description;

    // 是否公开 false: 私有, true: 公开
    private Boolean isPublic;

    // 收藏夹下收藏的物品数量
    private Long itemCount;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
