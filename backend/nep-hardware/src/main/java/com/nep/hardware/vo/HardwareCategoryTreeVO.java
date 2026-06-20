package com.nep.hardware.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 硬件分类树VO图对象
 * 用于表示硬件分类的树状结构，包含分类的ID、名称、父分类ID、排序顺序和子分类列表。
 * 该VO图对象用于在API响应中返回硬件分类的树状结构，方便前端展示和操作。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardwareCategoryTreeVO {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer sortOrder;

    // Builder.Default 注解用于设置默认值
    @Builder.Default
    private List<HardwareCategoryTreeVO> children = new ArrayList<>();
}
