package com.nep.system.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import com.nep.common.constants.ValidationConstant;
import com.nep.common.constants.MessageConstant;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户个人信息更新请求DTO
 */
@Data
public class UserProfileUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Size(
        max = ValidationConstant.AVATAR_MAX_LENGTH,
        message = MessageConstant.AVATAR_LENGTH_LIMIT
    )
    @Pattern(
        regexp = "^\\s*$|^\\s*https?://\\S+\\s*$",
        message = MessageConstant.AVATAR_URL_INVALID
    )
    private String avatar;

    @Size(
        max = ValidationConstant.NICKNAME_MAX_LENGTH,
        message = MessageConstant.NICKNAME_LENGTH_LIMIT
    )
    private String nickname;

    @Size(
        max = ValidationConstant.BIO_MAX_LENGTH,
        message = MessageConstant.BIO_LENGTH_LIMIT
    )
    private String bio;
}
