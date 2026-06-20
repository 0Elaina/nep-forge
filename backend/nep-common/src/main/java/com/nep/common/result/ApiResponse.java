package com.nep.common.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;

/**
 * API 响应实体
 * @param <T> 响应数据类型
 * @author Neptune
 * @date 2026-06-06
 */
@Data
public class ApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code; // 状态码 0: 成功 其他: 失败
    private String message; // 状态描述
    private T data; // 数据
    private String traceId; // 跟踪ID

    /**
     * 成功响应
     * @param data 数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(0);
        response.setMessage(MessageConstant.SUCCESS);
        response.setData(data);
        response.setTraceId(null);
        return response;
    }

    /**
     * 成功响应
     * @param data 数据
     * @param traceId 跟踪ID
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data, String traceId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(0);
        response.setMessage(MessageConstant.SUCCESS);
        response.setData(data);
        response.setTraceId(traceId);
        return response;
    }

    /**
     * 失败响应
     * @param code 状态码
     * @param message 状态描述
     * @param traceId 跟踪ID
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message, String traceId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        response.setTraceId(traceId);
        return response;
    }
}
