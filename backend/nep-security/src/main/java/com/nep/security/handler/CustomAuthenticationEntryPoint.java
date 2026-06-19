package com.nep.security.handler;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

import com.nep.common.exception.CommonErrorCode;
import com.nep.common.result.ApiResponse;

/**
 * 自定义认证入口点
 * 
 * 当用户未登录或登录过期时，触发此方法
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    /**
     * 处理未授权异常
     * 
     * 当用户未登录或登录过期时，触发此方法
     * 
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param authException 认证异常
     * @throws IOException 如果写入响应时发生错误
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        // 设置响应状态码为401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容类型为 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置响应字符编码为 UTF-8
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        /**
         * 构建错误响应
         * 包含错误码、错误消息
         * 
         * 错误码：CommonErrorCode.UNAUTHORIZED.getCode()
         * 错误消息：CommonErrorCode.UNAUTHORIZED.getMessage()
         */
        response.getWriter().write(
            objectMapper.writeValueAsString(
                ApiResponse.error(CommonErrorCode.UNAUTHORIZED.getCode(), CommonErrorCode.UNAUTHORIZED.getMessage(), null)
            )
        );
    }
}
