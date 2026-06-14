package com.nep.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;
import jakarta.validation.constraints.Size;

/**
 * 登录请求DTO
 *     用于接收登录请求的账号和密码参数
 *     包含账号和密码两个字段，账号不能为空，密码不能为空，密码长度必须在 8-32 个字符之间
 * @author Neptune
 * @date 2026-06-14
 */
@Data
public class LoginRequest {
    @NotBlank(message = MessageConstant.ACCOUNT_NOT_BLANK)
    @Size(max = ValidationConstant.ACCOUNT_MAX_LENGTH, message = MessageConstant.ACCOUNT_LENGTH_LIMIT)
    private String account;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @Size(min = ValidationConstant.PASSWORD_MIN_LENGTH, max = ValidationConstant.PASSWORD_MAX_LENGTH, message = MessageConstant.PASSWORD_LENGTH_LIMIT)
    private String password;
}
