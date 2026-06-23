package com.nep.build.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 装机单可见性更新请求
 */
@Data
public class BuildVisibilityUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 是否公开 true 公开 false 私密
    @NotNull(message = MessageConstant.BUILD_VISIBILITY_NOT_NULL)
    private Boolean isPublic;
}
