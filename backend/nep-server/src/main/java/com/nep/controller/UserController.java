package com.nep.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nep.common.result.ApiResponse;
import com.nep.security.util.SecurityUtils;
import com.nep.system.service.UserService;
import com.nep.system.vo.CurrentUserDetailVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户接口Controller
 * 提供用户相关的API接口，如获取当前登录用户信息。
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息。
     * @return 当前登录用户的详情VO图对象
     */
    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public ApiResponse<CurrentUserDetailVO> getCurrentUser(){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("当前登录用户id: {}", currentUserId);
        return ApiResponse.success(userService.getCurrentUser(currentUserId));   
    }
}
