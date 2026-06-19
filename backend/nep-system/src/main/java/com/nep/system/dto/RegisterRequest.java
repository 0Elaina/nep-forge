package com.nep.system.dto;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户注册请求 DTO。
 *     用于接收用户注册时提交的表单数据，包含用户名、邮箱和密码三个必填字段。
 *     所有字段均通过 Jakarta Validation 注解进行参数校验。
 * 
 * @author Neptune
 * @date 2026-06-07
 */
@Data
public class RegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名。
     *     不能为空，长度必须在指定范围内。用于登录和身份识别。
     * 约束规则:
     *     不能为空（@NotBlank）
     *     最小长度：{@value com.nep.common.constants.ValidationConstant#USERNAME_MIN_LENGTH}
     *     最大长度：{@value com.nep.common.constants.ValidationConstant#USERNAME_MAX_LENGTH}
     */
    @NotBlank(message = MessageConstant.USERNAME_NOT_BLANK)
    @Size(min = ValidationConstant.USERNAME_MIN_LENGTH,
         max = ValidationConstant.USERNAME_MAX_LENGTH,
          message = MessageConstant.USERNAME_LENGTH_LIMIT)
    private String username;

    /**
     * 邮箱地址。
     *     不能为空，必须符合邮箱格式规范。用于账号安全验证和接收系统通知。
     * 约束规则:
     *     不能为空（@NotBlank）
     *     必须符合邮箱格式（@Email）
     *     最大长度：{@value com.nep.common.constants.ValidationConstant#EMAIL_MAX_LENGTH}
     */
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    @Email(message = MessageConstant.EMAIL_INVALID)
    @Size(max = ValidationConstant.EMAIL_MAX_LENGTH, message = MessageConstant.EMAIL_LENGTH_LIMIT)
    private String email;

    /**
     * 密码。
     *     不能为空，长度必须在指定范围内。建议包含大小写字母、数字和特殊字符以提高安全性。
     * 约束规则:
     *     不能为空（@NotBlank）
     *     最小长度：{@value com.nep.common.constants.ValidationConstant#PASSWORD_MIN_LENGTH}
     *     最大长度：{@value com.nep.common.constants.ValidationConstant#PASSWORD_MAX_LENGTH}
     */
    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @Size(min = ValidationConstant.PASSWORD_MIN_LENGTH,
        max = ValidationConstant.PASSWORD_MAX_LENGTH,
        message = MessageConstant.PASSWORD_LENGTH_LIMIT
    )
    private String password;
}