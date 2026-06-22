package com.nep.build.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新装机单配件数量请求
 */
@Data
public class BuildItemUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 硬件数量
    @NotNull(message = MessageConstant.BUILD_HARDWARE_QUANTITY_NOT_NULL)
    @Min(
        value = ValidationConstant.BUILD_HARDWARE_QUANTITY_MIN,
        message = MessageConstant.BUILD_HARDWARE_QUANTITY_MIN_LIMIT
    )
    private Integer quantity;
    
}
