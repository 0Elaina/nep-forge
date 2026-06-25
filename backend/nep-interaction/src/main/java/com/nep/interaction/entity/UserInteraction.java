package com.nep.interaction.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseTimeEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_interaction")
public class UserInteraction extends BaseTimeEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 主键ID, 雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 用户ID
    private Long userId;

    // 目标ID
    private Long targetId;

    // 目标类型: 1: 文章, 2: 配件, 3: 装机单, 4: 评论
    private Integer targetType;

    // 行为类型 1: 点赞, 2: 收藏
    private Integer actionType;

    // 收藏夹Id, 点赞时默认为0
    private Long folderId;
}
