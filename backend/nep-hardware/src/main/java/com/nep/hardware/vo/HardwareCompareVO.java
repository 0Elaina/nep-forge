package com.nep.hardware.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 硬件对比视图对象
 * 
 * 用于展示硬件对比的结果，包括对比项和对比字段。
 */
@Data
@Builder
public class HardwareCompareVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer categoryId;
    private String categoryName;

    // 对比项列表
    private List<HardwareCompareItemVO> items;
    // 对比字段列表
    private List<HardwareCompareFieldVO> fields;
}

