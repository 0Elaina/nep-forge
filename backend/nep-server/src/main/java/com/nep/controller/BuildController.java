package com.nep.controller;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nep.build.dto.BuildCreateRequest;
import com.nep.build.dto.BuildItemAddRequest;
import com.nep.build.dto.BuildItemUpdateRequest;
import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.dto.BuildUpdateRequest;
import com.nep.build.dto.BuildVisibilityUpdateRequest;
import com.nep.build.service.BuildService;
import com.nep.build.vo.BuildDetailVO;
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

    /**
     * 创建装机单
     *
     * @param request 创建请求参数
     * @return 创建结果
     */
    @Operation(summary = "创建装机单")
    @PostMapping
    public ApiResponse<Map<String, String>> createBuild(
        @Valid @RequestBody BuildCreateRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long id = buildService.createBuild(currentUserId, request);
        log.info("创建装机单: userId={}, id={}", currentUserId, id);
        return ApiResponse.success(Map.of("id", String.valueOf(id)));
    }

    /**
     * 更新装机单基础信息
     *
     * @param id 装机单ID
     * @param request 更新请求参数
     * @return 更新结果
     */
    @Operation(summary = "更新装机单基础信息")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateBuildBasicInfo(
        @PathVariable Long id,
        @Valid @RequestBody BuildUpdateRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("更新装机单基础信息: userId={}, buildId={}", currentUserId, id);
        return ApiResponse.success(buildService.updateBuildBasicInfo(currentUserId, id, request));
    }


    /**
     * 添加配件接口
     * @param id 装机单ID
     * @param request 添加请求参数
     * @return 添加结果
     */
    @Operation(summary = "添加配件接口")
    @PostMapping("/{id}/items")
    public ApiResponse<Map<String, String>> addBuildItem(
        @PathVariable Long id,
        @Valid @RequestBody BuildItemAddRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("添加配件接口: userId={}, buildId={}, request={}", currentUserId, id, request);
        Long detailId = buildService.addBuildItem(currentUserId, id, request);
        return ApiResponse.success(Map.of("detailId", String.valueOf(detailId)));
    }

    /**
     * 删除配件接口
     * @param id 装机单ID
     * @param detailId 配件详情ID
     * @return 删除结果
     */
    @Operation(summary = "删除配件接口")
    @DeleteMapping("/{id}/items/{detailId}")
    public ApiResponse<Boolean> removeBuildItem(
        @PathVariable Long id,
        @PathVariable Long detailId
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("删除配件接口: userId={}, buildId={}, detailId={}", currentUserId, id, detailId);
        return ApiResponse.success(buildService.removeBuildItem(currentUserId, id, detailId));
    }

    /**
     * 更新装机单配件数量接口
     * @param id 装机单ID
     * @param detailId 配件详情ID
     * @param request 更新请求参数
     * @return 更新结果
     */
    @Operation(summary = "更新装机单配件数量接口")
    @PutMapping("/{id}/items/{detailId}")
    public ApiResponse<Boolean> updateBuildItemQuantity(
        @PathVariable Long id,
        @PathVariable Long detailId,
        @Valid @RequestBody BuildItemUpdateRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("更新装机单配件数量接口: userId={}, buildId={}, detailId={}, request={}", currentUserId, id, detailId, request);
        return ApiResponse.success(buildService.updateBuildItemQuantity(currentUserId, id, detailId, request));
    }

    /**
     * 查看装机单详情接口
     * @param id 装机单ID
     * @return 装机单详情
     */
    @Operation(summary = "查看装机单详情接口")
    @GetMapping("/{id}")
    public ApiResponse<BuildDetailVO> getBuildDetail(@PathVariable Long id) {
        // 未登录时为 null，Service 层根据 null 跳过点赞/收藏等用户专属状态的查询
        // 游客可访问公开装机单详情
        // 登录用户可访问自己的私密装机单详情
        // 非作者访问私密装机单被 Service 层拦截
        Long currentUserId = SecurityUtils.getCurrentUserId() == null ? null : SecurityUtils.getCurrentUserId();
        log.info("查看装机单详情接口: userId={}, buildId={}", currentUserId, id);
        return ApiResponse.success(buildService.getBuildDetail(currentUserId, id));
    }

    /**
     * 设置装机单公开/私密接口
     * @param id 装机单ID
     * @param request 更新请求参数
     * @return 更新结果
     */
    @Operation(summary = "设置装机单公开/私密")
    @PutMapping("/{id}/visibility")
    public ApiResponse<Boolean> updateBuildVisibility(
        @PathVariable Long id,
        @Valid @RequestBody BuildVisibilityUpdateRequest request
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("设置装机单公开/私密: userId={}, buildId={}, isPublic={}", currentUserId, id, request.getIsPublic());
        return ApiResponse.success(buildService.updateBuildVisibility(currentUserId, id, request));
    }

    /**
     * 发布装机单接口
     * @param id 装机单ID
     * @return 发布结果
     */
    @Operation(summary = "发布装机单")
    @PutMapping("/{id}/publish")
    public ApiResponse<Boolean> publishBuild(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("发布装机单: userId={}, buildId={}", currentUserId, id);
        return ApiResponse.success(buildService.publishBuild(currentUserId, id));
    }
}
