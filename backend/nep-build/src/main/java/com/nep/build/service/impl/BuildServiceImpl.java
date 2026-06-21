package com.nep.build.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nep.build.service.BuildService;
import com.nep.build.vo.BuildListVO;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.QueryConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.result.PageResult;
import com.nep.common.util.PageQueryUtils;

import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.entity.UserBuild;
import com.nep.build.mapper.UserBuildMapper;

@Service
@RequiredArgsConstructor
public class BuildServiceImpl implements BuildService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final String SORT_FIELD_CREATE_TIME = "createTime";
    private static final String SORT_FIELD_TOTAL_PRICE = "totalPrice";
    private static final String SORT_FIELD_TOTAL_POWER = "totalPower";

    private final UserBuildMapper userBuildMapper;

    /**
     * 查询公开装机单列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<BuildListVO> listPublicBuilds(BuildQueryRequest request) {
        // 检查请求参数是否为空，若为空则创建默认实例
        BuildQueryRequest query = request == null ? new BuildQueryRequest() : request;

        // 归一化页码和页大小
        int pageNum = PageQueryUtils.normalizePageNum(query.getPageNum());
        int pageSize = PageQueryUtils.normalizePageSize(query.getPageSize(), MAX_PAGE_SIZE);

        Page<UserBuild> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserBuild> wrapper = new LambdaQueryWrapper<>();

        wrapper.select(
                UserBuild::getId,
                UserBuild::getTitle,
                UserBuild::getTotalPrice,
                UserBuild::getTotalPower,
                UserBuild::getIsPublic,
                UserBuild::getStatus,
                UserBuild::getCoverImage,
                UserBuild::getCreateTime,
                UserBuild::getUpdateTime);

        wrapper
                .eq(UserBuild::getIsDeleted, FieldConstant.NOT_DELETED)
                .eq(UserBuild::getIsPublic, FieldConstant.PUBLIC)
                .eq(UserBuild::getStatus, FieldConstant.BUILD_STATUS_NORMAL);

        // 添加关键词查询条件
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(UserBuild::getTitle, query.getKeyword().trim());
        }

        // 应用排序
        applyOrder(wrapper, query.getSortField(), query.getSortOrder());

        // 根据查询条件进行分页查询
        Page<UserBuild> resultPage = userBuildMapper.selectPage(page, wrapper);

        // 将查询结果转换为列表视图对象
        List<BuildListVO> records = resultPage.getRecords()
                .stream()
                .map(this::toListVO)
                .toList();

        return PageResult.of(
                records,
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize());
    }

    /**
     * 查询我的装机单列表
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<BuildListVO> listMyBuilds(Long currentUserId, BuildQueryRequest request) {
        if(currentUserId == null){
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        BuildQueryRequest query = request == null ? new BuildQueryRequest() : request;

        int pageNum = PageQueryUtils.normalizePageNum(query.getPageNum());
        int pageSize = PageQueryUtils.normalizePageSize(query.getPageSize(), MAX_PAGE_SIZE);

        Page<UserBuild> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<UserBuild> wrapper = new LambdaQueryWrapper<>();

        wrapper.select(
                UserBuild::getId,
                UserBuild::getTitle,
                UserBuild::getTotalPrice,
                UserBuild::getTotalPower,
                UserBuild::getIsPublic,
                UserBuild::getStatus,
                UserBuild::getCoverImage,
                UserBuild::getCreateTime,
                UserBuild::getUpdateTime
        );

        wrapper
                .eq(UserBuild::getIsDeleted, FieldConstant.NOT_DELETED)
                .eq(UserBuild::getUserId, currentUserId);

        // 条件查询: 根据状态查询
        if(query.getStatus() != null) {
            validateBuildStatus(query.getStatus());
            wrapper.eq(UserBuild::getStatus, query.getStatus());
        }

        // 应用排序
        applyOrder(wrapper, query.getSortField(), query.getSortOrder());

        // 根据查询条件进行分页查询
        Page<UserBuild> resultPage = userBuildMapper.selectPage(page, wrapper);

        // 将查询结果转换为列表视图对象
        List<BuildListVO> records = resultPage.getRecords()
                .stream()
                .map(this::toListVO)
                .toList();

        return PageResult.of(
                records,
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    /**
     * 验证装机单状态
     * 
     * @param status 装机单状态
     */
    private void validateBuildStatus(Integer status) {
        if (status != FieldConstant.BUILD_STATUS_DRAFT
                && status != FieldConstant.BUILD_STATUS_NORMAL
                && status != FieldConstant.BUILD_STATUS_OFFLINE) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, MessageConstant.BUILD_STATUS_INVALID);
        }
    }

    /**
     * 应用排序
     * 
     * @param wrapper   查询条件包装器
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     */
    private void applyOrder(
            LambdaQueryWrapper<UserBuild> wrapper,
            String sortField,
            String sortOrder) {
        boolean isAsc = QueryConstant.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder);

        // 如果排序字段是价格，则按价格排序
        if (SORT_FIELD_TOTAL_PRICE.equals(sortField)) {
            wrapper.orderBy(true, isAsc, UserBuild::getTotalPrice);
            return;
        }

        // 如果排序字段是功率，则按功率排序
        if (SORT_FIELD_TOTAL_POWER.equals(sortField)) {
            wrapper.orderBy(true, isAsc, UserBuild::getTotalPower);
            return;
        }

        // 默认排序字段是创建时间
        if (SORT_FIELD_CREATE_TIME.equals(sortField)) {
            wrapper.orderBy(true, isAsc, UserBuild::getCreateTime);
            return;
        }

        // 默认按创建时间倒序
        wrapper.orderByDesc(UserBuild::getCreateTime);
    }

    /**
     * 转换为列表视图对象
     * 
     * @param userBuild 用户装机单实体
     * @return 列表视图对象
     */
    private BuildListVO toListVO(UserBuild userBuild) {
        return BuildListVO.builder()
                .id(String.valueOf(userBuild.getId()))
                .title(userBuild.getTitle())
                .totalPrice(userBuild.getTotalPrice())
                .totalPower(userBuild.getTotalPower())
                .isPublic(FieldConstant.PUBLIC == userBuild.getIsPublic())
                .status(userBuild.getStatus())
                .coverImage(userBuild.getCoverImage())
                .createTime(userBuild.getCreateTime())
                .updateTime(userBuild.getUpdateTime())
                .build();
    }
}
