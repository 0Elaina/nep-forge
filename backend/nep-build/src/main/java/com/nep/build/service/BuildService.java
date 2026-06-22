package com.nep.build.service;

import com.nep.common.result.PageResult;
import com.nep.build.dto.BuildCreateRequest;
import com.nep.build.dto.BuildQueryRequest;
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
}
