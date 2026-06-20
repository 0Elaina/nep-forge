package com.nep.security.handler;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.result.ApiResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 自定义访问被拒绝处理程序
 * 用于处理访问被拒绝的异常, 并返回 JSON 格式的错误响应
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;


    /**
     * 处理访问被拒绝的异常
     * @param request HTTP请求
     * @param response HTTP响应
     * @param accessDeniedException 访问被拒绝的异常
     */
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        // 如果响应已提交, 则直接返回
        if (response.isCommitted()) {
            return;
        }

        // 设置响应状态码为403, 表示拒绝访问
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 设置响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置响应字符编码为UTF-8
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 构建错误响应
        ApiResponse<Void> result = ApiResponse.error(CommonErrorCode.FORBIDDEN.getCode(), CommonErrorCode.FORBIDDEN.getMessage(), null);

        // writeValue 将 ApiResponse 写入响应流, 并转换为 JSON 格式
        objectMapper.writeValue(response.getOutputStream(), result);
    }
}