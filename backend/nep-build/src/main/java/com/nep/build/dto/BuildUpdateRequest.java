package com.nep.build.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 装机单更新请求参数
 */
@Data
public class BuildUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 装机单标题
    @NotBlank(message = MessageConstant.BUILD_TITLE_NOT_BLANK)
    @Size(
        max = ValidationConstant.BUILD_TITLE_MAX_LENGTH,
        message = MessageConstant.BUILD_TITLE_LENGTH_MAX_LIMIT
    )
    private String title;

    // 装机单描述
    @Size(
        max = ValidationConstant.BUILD_DESCRIPTION_MAX_LENGTH,
        message = MessageConstant.BUILD_DESCRIPTION_LENGTH_MAX_LIMIT
    )
    private String description;

    // 装机单封面图片
    @Size(
        max = ValidationConstant.BUILD_COVER_IMAGE_MAX_LENGTH,
        message = MessageConstant.BUILD_COVER_IMAGE_LENGTH_MAX_LIMIT
    )
    private String coverImage;
}
