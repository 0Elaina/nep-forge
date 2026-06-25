package com.nep.content.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * 个人中心文章VO
 */
@Data
@Builder
public class ProfileArticleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 文章ID
    private String id;

    // 分类ID
    private Integer categoryId;

    // 标题
    private String title;

    // 文章状态 0: 草稿 1: 已发布 2: 已下架
    private Integer status;

    // 浏览量
    private Integer viewCount;

    // 点赞量
    private Integer likeCount;

    // 收藏量
    private Integer favoriteCount;

    // 评论量
    private Integer commentCount;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

}
