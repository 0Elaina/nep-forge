package com.nep.hardware.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 硬件对比项视图对象
 * 
 * 用于展示硬件对比的项，如价格、规格等。
 */
@Data
@Builder
public class HardwareCompareItemVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String coverImage;
    private Map<String, Object> specs;
}
