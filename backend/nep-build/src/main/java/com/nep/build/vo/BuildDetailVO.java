package com.nep.build.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 装机单详情VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildDetailVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 装机单详情ID
    private String id;

    // 用户ID
    private String userId;

    // 标题
    private String title;

    // 描述
    private String description;

    // 总价格
    private BigDecimal totalPrice;

    // 总功耗
    private BigDecimal totalPower;

    // 是否公开
    private Boolean isPublic;

    // 状态
    private Integer status;

    // 封面图片
    private String coverImage;

    // 是否点赞
    private Boolean liked;

    // 是否收藏
    private Boolean favorited;

    // 配件列表
    private List<BuildDetailItemVO> items;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
}
