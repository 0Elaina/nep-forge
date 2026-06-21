package com.nep.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.service.BuildService;
import com.nep.build.vo.BuildListVO;
import com.nep.common.result.ApiResponse;
import com.nep.common.result.PageResult;
import com.nep.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 装机单Controller
 */
@Tag(name = "装机单接口")
@RestController
@RequestMapping("/api/v1/builds")
@RequiredArgsConstructor
@Slf4j
public class BuildController {
    private final BuildService buildService;

    /**
     * 查看公开装机单列表
     *
     * @param request 查询参数
     * @return 返回结果
     */
    @Operation(summary = "查看公开装机单列表")
    @GetMapping("/public")
    public ApiResponse<PageResult<BuildListVO>> listPublicBuilds(
        @Valid @ParameterObject BuildQueryRequest request
    ) {
        log.info("查看公开装机单列表: {}", request);
        return ApiResponse.success(buildService.listPublicBuilds(request));
    }

    /**
     * 查看当前用户的装机单列表
     *
     * @param request 筛选参数
     * @return 筛选结果
     */
    @Operation(summary = "查看当前用户的装机单列表")
    @GetMapping("/my")
    public ApiResponse<PageResult<BuildListVO>> listMyBuilds(
        @Valid @ParameterObject BuildQueryRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("查看当前用户的装机单列表: id={}", currentUserId);
        return ApiResponse.success(buildService.listMyBuilds(currentUserId, request));
    }
}
