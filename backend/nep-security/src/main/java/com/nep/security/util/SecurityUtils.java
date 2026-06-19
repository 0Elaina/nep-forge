package com.nep.security.util;

import com.nep.security.jwt.JwtUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供获取当前用户ID、用户名、用户信息等方法
 */
public final class SecurityUtils {
    private SecurityUtils(){}


    /**
     * 获取当前用户ID
     * @return 当前用户ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().userId();
    }

    /**
     * 获取当前用户名
     * @return 当前用户名
     */
    public static String getCurrentUsername() {
        return getCurrentUser().username();
    }


    /**
     * 获取当前用户信息
     * 
     * @return 当前用户信息
     * @throws IllegalArgumentException 如果当前用户未认证
     */
    public static JwtUserInfo getCurrentUser() {
        // 从Security上下文获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 检查认证信息是否为空或不是JwtUserInfo类型
        // getPrincipal() 方法返回认证信息的主体
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new IllegalArgumentException("当前用户未认证");
        }

        return userInfo;
    }
}