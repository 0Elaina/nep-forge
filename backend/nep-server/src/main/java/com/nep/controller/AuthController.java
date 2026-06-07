package com.nep.controller;

import com.nep.common.result.ApiResponse;
import com.nep.system.dto.RegisterRequest;
import com.nep.system.service.AuthService;
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

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<RegisterVO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request);
        return ApiResponse.success(authService.register(request));
    }
}
