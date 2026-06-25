package com.nep.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.service.BuildService;
import com.nep.build.vo.BuildListVO;
import com.nep.common.result.ApiResponse;
import com.nep.common.result.PageResult;
import com.nep.content.dto.ProfileArticleQueryRequest;
import com.nep.content.service.ArticleProfileService;
import com.nep.content.vo.ProfileArticleVO;
import com.nep.interaction.dto.UserInteractionQueryRequest;
import com.nep.interaction.service.UserInteractionProfileService;
import com.nep.interaction.vo.FavoriteFolderVO;
import com.nep.interaction.vo.UserInteractionTargetVO;
import com.nep.security.util.SecurityUtils;
import com.nep.system.dto.UserProfileUpdateRequest;
import com.nep.system.service.UserService;
import com.nep.system.vo.CurrentUserDetailVO;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final BuildService buildService;
    private final ArticleProfileService articleProfileService;
    private final UserInteractionProfileService userInteractionProfileService;

    /**
     * 获取当前登录用户信息。
     * 
     * @return 当前登录用户的详情VO图对象
     */
    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public ApiResponse<CurrentUserDetailVO> getCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("当前登录用户id: {}", currentUserId);
        return ApiResponse.success(userService.getCurrentUser(currentUserId));
    }

    /**
     * 更新当前用户个人信息。
     * 
     * @param request 更新个人信息个人信息请求对象
     * @return 更新后的用户详情VO图对象
     */
    @Operation(summary = "更新当前用户个人信息")
    @PutMapping("/me/profile")
    public ApiResponse<CurrentUserDetailVO> updateCurrentUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("更新当前用户个人信息: {}", request);
        return ApiResponse.success(userService.updateCurrentUserProfile(currentUserId, request));
    }

    /**
     * 获取当前用户的文章列表。
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 文章分页结果列表
     */
    @Operation(summary = "获取当前用户的文章列表")
    @GetMapping("/me/articles")
    public ApiResponse<PageResult<ProfileArticleVO>> getMyArticles(
            @Valid @ParameterObject ProfileArticleQueryRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("获取当前用户文章列表: userId={}, request={}", currentUserId, request);
        return ApiResponse.success(articleProfileService.listMyArticles(currentUserId, request));
    }

    /**
     * 获取当前用户的装机单列表。
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 装机单分页结果列表
     */
    @Operation(summary = "获取当前用户的装机单列表")
    @GetMapping("/me/builds")
    public ApiResponse<PageResult<BuildListVO>> listMyBuilds(
            @Valid @ParameterObject BuildQueryRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(buildService.listMyBuilds(currentUserId, request));
    }

    /**
     * 获取当前用户的点赞列表。
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 点赞分页结果列表
     */
    @Operation(summary = "获取当前用户的点赞列表")
    @GetMapping("/me/likes")
    public ApiResponse<PageResult<UserInteractionTargetVO>> listMyLikes(
            @Valid @ParameterObject UserInteractionQueryRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(userInteractionProfileService.listMyLikes(currentUserId, request));
    }

    /**
     * 获取当前用户的收藏列表。
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 收藏分页结果列表
     */
    @Operation(summary = "获取当前用户的收藏列表")
    @GetMapping("/me/favorites")
    public ApiResponse<PageResult<UserInteractionTargetVO>> listMyFavorites(
            @Valid @ParameterObject UserInteractionQueryRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(userInteractionProfileService.listMyFavorites(currentUserId, request));
    }

    /**
     * 获取当前用户的收藏夹列表。
     * 
     * @param currentUserId 当前用户ID
     * @return 收藏夹列表
     */
    @Operation(summary = "获取当前用户的收藏夹列表")
    @GetMapping("/me/favorite-folders")
    public ApiResponse<List<FavoriteFolderVO>> listMyFavoriteFolders() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(userInteractionProfileService.listMyFavoriteFolders(currentUserId));
    }
}
