package com.nep.hardware.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 配件保存请求DTO
 * 用于接收前端提交的配件保存请求参数。
 */
@Data
public class HardwareSaveRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 配件分类ID
    @NotNull(message = MessageConstant.HARDWARE_CATEGORY_NOT_NULL)
    private Integer categoryId;

    // 配件名称
    @NotBlank(message = MessageConstant.HARDWARE_NAME_NOT_BLANK)
    @Size(
        max = ValidationConstant.HARDWARE_NAME_MAX_LENGTH,
        message = MessageConstant.HARDWARE_NAME_LENGTH_MAX_LIMIT
    )
    private String name;

    // 配件品牌
    @Size(
        max = ValidationConstant.HARDWARE_BRAND_NAME_MAX_LENGTH,
        message = MessageConstant.HARDWARE_BRAND_NAME_LENGTH_MAX_LIMIT
    )
    private String brand;

    // 配件价格
    @NotNull(message = MessageConstant.HARDWARE_PRICE_NOT_NULL)
    @DecimalMin(
        value = ValidationConstant.HARDWARE_PRICE_MIN,
        message = MessageConstant.HARDWARE_PRICE_MIN_LIMIT
       )
    private BigDecimal price;

    // 配件来源名称
    @Size(
        max = ValidationConstant.HARDWARE_SOURCE_NAME_MAX_LENGTH,
        message = MessageConstant.HARDWARE_SOURCE_NAME_LENGTH_MAX_LIMIT
    )
    private String sourceName;

    // 配件来源URL
    @Size(
        max = ValidationConstant.HARDWARE_SOURCE_URL_MAX_LENGTH,
        message = MessageConstant.HARDWARE_SOURCE_URL_LENGTH_MAX_LIMIT
    )
    private String sourceUrl;

    // 配件发布时间日期
    private LocalDate releaseDate;

    // 配件封面图片URL
    @Size(
        max = ValidationConstant.HARDWARE_COVER_IMAGE_URL_MAX_LENGTH,
        message = MessageConstant.HARDWARE_COVER_IMAGE_URL_LENGTH_MAX_LIMIT
    )
    private String coverImage;

    /**
    * 前端传 JSON 对象，后端序列化为 hardware.specs_json。
    */
    private Map<String, Object> specs;
    
}
