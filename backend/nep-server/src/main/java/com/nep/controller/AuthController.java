package com.nep.controller;

import com.nep.common.result.ApiResponse;
import com.nep.system.dto.LoginRequest;
import com.nep.system.dto.RegisterRequest;
import com.nep.system.service.AuthService;
import com.nep.system.vo.LoginResponse;
import com.nep.system.vo.RegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@Tag(name = "用户认证接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * 用户注册
     * 接收用户提交的注册信息，校验通过后创建新用户并返回注册结果。
     *
     * @param request 用户注册请求参数，包含用户名、邮箱和密码
     * @return ApiResponse<RegisterVO> 注册成功后的用户信息视图对象
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<RegisterVO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request);
        return ApiResponse.success(authService.register(request));
    }

    /**
     * 用户登录
     * 接收用户提交的登录信息，校验通过后生成访问令牌并返回登录结果。
     *
     * @param request 用户登录请求参数，包含用户名或邮箱和密码
     * @return ApiResponse<LoginResponse> 登录成功后的登录响应视图对象，包含访问令牌、令牌类型、过期时间、当前用户信息等
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getAccount());
        return ApiResponse.success(authService.login(request));
    }


    /**
     * 用户退出登录
     * 清除当前用户的访问令牌，使用户无法继续使用系统服务。
     *
     * @return ApiResponse<Boolean> 退出登录成功后的布尔值视图对象，包含 true
     */
    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public ApiResponse<Boolean> logout() {
        log.info("用户退出登录");
        return ApiResponse.success(authService.logout());
    }
}
