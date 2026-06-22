package com.nep.build.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 装机单配件添加请求
 */
@Data
public class BuildItemAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 配件ID
    @NotNull(message = MessageConstant.HARDWARE_ID_NOT_NULL)
    private Long hardwareId;

    // 配件数量
    @NotNull(message = MessageConstant.BUILD_HARDWARE_QUANTITY_NOT_NULL)
    @Min(
        value = ValidationConstant.BUILD_HARDWARE_QUANTITY_MIN,
        message = MessageConstant.BUILD_HARDWARE_QUANTITY_MIN_LIMIT
    )
    private Integer quantity;
    
}
