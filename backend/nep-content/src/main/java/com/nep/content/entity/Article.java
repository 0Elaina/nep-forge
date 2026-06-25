package com.nep.content.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseLogicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseLogicEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 文章ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 分类ID
    private Integer categoryId;
    
    // 用户ID
    private Long userId;

    // 文章标题
    private String title;

    // 文章内容
    private String content;

    // 文章状态 0 草稿 1 已发布 2 下架
    private Integer status;

    // 浏览量
    private Integer viewCount;

    // 点赞数
    private Integer likeCount;

    // 收藏数
    private Integer favoriteCount;

    // 评论数
    private Integer commentCount;
}
