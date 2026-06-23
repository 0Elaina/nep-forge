package com.nep.build.service;

import com.nep.common.result.PageResult;
import com.nep.build.dto.BuildCreateRequest;
import com.nep.build.dto.BuildItemAddRequest;
import com.nep.build.dto.BuildItemUpdateRequest;
import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.dto.BuildUpdateRequest;
import com.nep.build.dto.BuildVisibilityUpdateRequest;
import com.nep.build.vo.BuildDetailVO;
import com.nep.build.vo.BuildListVO;

/**
 * 装机单服务接口
 * 用于查询装机单列表
 */
public interface BuildService {
    /**
     * 查询公开装机单列表
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<BuildListVO> listPublicBuilds(BuildQueryRequest request);

    /**
     * 查询我的装机单列表
     * @param currentUserId 当前用户ID
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<BuildListVO> listMyBuilds(Long currentUserId, BuildQueryRequest request);

    /**
     * 创建装机单
     * @param currentUserId 当前用户ID
     * @param request 创建请求
     * @return 装机单ID
     */
    Long createBuild(Long currentUserId, BuildCreateRequest request);

    /**
     * 更新装机单基本信息
     * 包括标题、描述、封面图片
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param request 更新请求
     * @return 是否成功更新
     */
    Boolean updateBuildBasicInfo(Long currentUserId, Long buildId, BuildUpdateRequest request);


    /**
     * 添加装机单配件
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param request 添加请求
     * @return 装机单详情ID
     */
    Long addBuildItem(Long currentUserId, Long buildId, BuildItemAddRequest request);

    /**
     * 删除装机单配件
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param detailId 装机单详情ID
     * @return 是否删除成功
     */
    Boolean removeBuildItem(Long currentUserId, Long buildId, Long detailId);

    /**
     * 更新装机单配件数量
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param detailId 装机单详情ID
     * @param request 更新请求
     * @return 是否成功更新
     */
    Boolean updateBuildItemQuantity(
        Long currentUserId,
        Long buildId,
        Long detailId,
        BuildItemUpdateRequest request
    );

    /**
     * 查询装机单详情
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @return 装机单详情VO
     */
    BuildDetailVO getBuildDetail(Long currentUserId, Long buildId);


    /**
     * 更新装机单可见性
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param request 更新请求
     * @return 是否成功更新
     */
    Boolean updateBuildVisibility(
        Long currentUserId,
        Long buildId,
        BuildVisibilityUpdateRequest request
    );
}
