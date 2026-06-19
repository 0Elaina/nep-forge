package com.nep.system.vo;

import java.io.Serializable;
import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户注册响应 VO。
 *     用户注册成功后返回给前端的视图对象（View Object），
 *     用于在响应中展示新创建的用户信息。
 * 
 * @author Neptune
 * @date 2026-06-06
 */
@Data
@AllArgsConstructor
public class RegisterVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，注册成功后由系统自动生成的唯一标识。
     */
    private String id;
    
    /**
     * 用户名，用户注册时设置的用户名。
     */
    private String username;
    
    /**
     * 邮箱地址，用户注册时使用的邮箱。
     */
    private String email;
}
