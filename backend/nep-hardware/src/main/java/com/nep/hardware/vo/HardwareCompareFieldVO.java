package com.nep.hardware.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 硬件对比字段视图对象
 * 
 * 用于展示硬件对比的字段，如价格、规格等。
 */
@Data
@Builder
public class HardwareCompareFieldVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 参数 key，例如 cores、threads、tdp。
     */
    private String key;

    /**
     * 第一阶段暂无参数模板表，label 暂时与 key 保持一致。
     */
    private String label;

    /**
     * 第一阶段暂无参数模板表，unit 暂时为空。
     */
    private String unit;
}
