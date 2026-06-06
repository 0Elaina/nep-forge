package com.nep.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import com.nep.result.ApiResponse;
import java.util.stream.Collectors;
import org.springframework.validation.BindException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

/**
 * 全局异常处理器。
 * 使用 {@link RestControllerAdvice} 统一拦截各层抛出的异常，
 * 将其转换为统一的 {@link ApiResponse} 格式返回给客户端。
 * 包含常见参数校验、权限、数据冲突等异常的处理方法。
 *
 * @author Neptune
 * @date 2026-06-06
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理通用异常
     * @param ex 异常
     * @return 响应实体
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommonException(CommonException ex) {
        BaseErrorInfo errorInfo = ex.getErrorInfo();

        return ResponseEntity
        .status(errorInfo.getHttpStatus())
        .body(ApiResponse.error(
            errorInfo.getCode(),
            ex.getMessage(),
            traceId()
        ));
    }


    /**
     * 处理 POST/PUT 请求中 @RequestBody 参数校验失败异常。
     * 当使用 @Valid 或 @Validated 注解校验 @RequestBody 参数时，
     * 若校验失败会抛出 MethodArgumentNotValidException。
     * 该方法将 BindingResult 中所有字段的错误信息拼接成一条提示字符串返回。
     * 与 BindException 的区别在于：BindException 处理 GET 请求中 @RequestParam、@ModelAttribute 的绑定失败。
     *
     * @param ex MethodArgumentNotValidException 异常对象，包含字段校验错误信息
     * @return 包含统一错误码和详细参数错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));

        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, message);
    }

    
    /**
     * 处理 GET 请求中 @RequestParam、@ModelAttribute 的参数绑定校验失败异常
     * 以及表单提交 application/x-www-form-urlencoded 的参数校验失败。
     * 将 BindingResult 中所有字段的错误信息拼接成一条提示字符串返回。
     * 与 MethodArgumentNotValidException 的区别在于：
     * MethodArgumentNotValidException 处理 POST/PUT 请求中 @RequestBody 参数的校验失败。
     *
     * @param ex BindException 异常对象，包含绑定结果和字段校验错误信息
     * @return 包含统一错误码和详细参数错误信息的响应实体
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));

        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, message);
    }


    /**
     * 处理方法参数或方法返回值上的单个参数校验失败异常。
     * 当在类上标注 @Validated，同时在方法参数上使用 @RequestParam、@PathVariable 等注解
     * 配合校验注解（如 @NotBlank、@Min）时，若校验失败会抛出 ConstraintViolationException。
     * 与 MethodArgumentNotValidException 的区别在于：
     * MethodArgumentNotValidException 处理 @RequestBody 的整个对象参数校验失败；
     * 与 BindException 的区别在于：
     * BindException 处理 @ModelAttribute 和表单提交的参数绑定校验失败。
     * 该方法将 ConstraintViolation 集合中所有校验违规信息拼接成一条提示字符串返回。
     *
     * @param ex ConstraintViolationException 异常对象，包含所有校验违规信息
     * @return 包含统一错误码和详细参数错误信息的响应实体
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
        .stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining("; "));

        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 处理 GET 请求中缺少必需请求参数的异常。
     * 当控制器方法参数使用 @RequestParam(required = true) 但请求未携带该参数时抛出。
     *
     * @param ex MissingServletRequestParameterException 异常对象，包含缺少的参数名
     * @return 包含统一错误码和具体缺少参数信息的响应实体
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = "缺少请求参数: " + ex.getParameterName();
        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, message);
    }


    /**
     * 处理请求体不可读的异常。
     * 当 @RequestBody 请求体为空、格式错误（如 JSON 语法错误）或 Content-Type 不匹配时抛出。
     *
     * @param ex HttpMessageNotReadableException 异常对象
     * @return 包含统一错误码和请求体格式错误提示的响应实体
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, "请求体格式错误，请检查 JSON 格式");
    }

    /**
     * 处理 HTTP 请求方法不支持的异常。
     * 当客户端使用不支持的 HTTP 方法（如对只支持 POST 的接口发送 GET 请求）时抛出。
     *
     * @param ex HttpRequestMethodNotSupportedException 异常对象，包含请求方法信息
     * @return 包含统一错误码和请求方法不支持提示的响应实体
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex
    ) {
        return fail(CommonErrorCode.REQUEST_PARAM_ERROR, "请求方法不支持: " + ex.getMethod());
    }

    /**
     * 处理访问被拒绝的异常。
     * 当用户尝试访问没有权限的资源时抛出，由 Spring Security 框架触发。
     * 返回 403 Forbidden 状态码及统一错误信息。
     *
     * @param ex AccessDeniedException 异常对象
     * @return 包含 403 状态码和禁止访问错误信息的响应实体
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return fail(CommonErrorCode.FORBIDDEN, CommonErrorCode.FORBIDDEN.getMessage());
    }

    /**
     * 处理数据完整性违规异常。
     * 当数据库操作违反完整性约束（如唯一索引冲突、外键约束失败）时抛出。
     * 返回 409 Conflict 状态码及冲突提示信息。
     *
     * @param ex DataIntegrityViolationException 异常对象
     * @return 包含 409 状态码和数据唯一约束冲突信息的响应实体
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        return fail(CommonErrorCode.CONFLICT, "数据已存在或违反唯一约束");
    }

    /**
     * 全局兜底异常处理器。
     * 当没有匹配到更具体的异常处理方法时，由该方法统一处理所有未被捕获的异常。
     * 打印异常堆栈信息便于排查，返回 500 内部服务器错误。
     *
     * @param ex Exception 异常对象
     * @return 包含 500 状态码和系统内部错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("系统发生未知异常: {}", ex);

        return fail(CommonErrorCode.SYSTEM_ERROR, CommonErrorCode.SYSTEM_ERROR.getMessage());
    }



    /**
     * 处理失败响应
     * @param errorInfo 错误信息
     * @param message 错误信息
     * @return 响应实体
     */
    private ResponseEntity<ApiResponse<Void>> fail(BaseErrorInfo errorInfo, String message) {
        return ResponseEntity
        .status(errorInfo.getHttpStatus())
        .body(ApiResponse.error(
            errorInfo.getCode(),
            message,
            traceId()
        ));
    }

    /**
     * 生成跟踪ID
     * @return 跟踪ID
     */
    private String traceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
