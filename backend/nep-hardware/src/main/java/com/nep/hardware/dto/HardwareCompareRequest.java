package com.nep.hardware.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 硬件对比请求参数
 * 
 * 用于对比多个硬件的性能，如价格、规格等。
 */
@Data
public class HardwareCompareRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 待对比的硬件 ID 列表。
     *
     * 将 @NotNull 写在泛型 List<@NotNull Long> 内部，表示对列表中
     * 的每个元素进行校验（禁止 null 元素），而非校验 list 引用本身。
     * 这样 hardwareIds 本身允许为 null（用于某些场景跳过该参数），
     * 但只要传入非空列表，其中的每个 ID 都不能为 null。
     */
    @NotEmpty(message = MessageConstant.HARDWARE_COMPARE_IDS_NOT_EMPTY)
    @Size(
        min = ValidationConstant.HARDWARE_COMPARE_SIZE_MIN,
        max = ValidationConstant.HARDWARE_COMPARE_SIZE_MAX,
        message = MessageConstant.HARDWARE_COMPARE_SIZE_LIMIT
    )
    private List<@NotNull(message = MessageConstant.HARDWARE_COMPARE_ID_NOT_NULL) Long> hardwareIds;
    
}
