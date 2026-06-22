package com.nep.build.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.nep.common.util.StringCommonUtils;

import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.build.dto.BuildCreateRequest;
import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.dto.BuildUpdateRequest;
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
     * 创建装机单
     * @param currentUserId 当前用户ID
     * @param request       创建请求
     * @return 装机单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBuild(Long currentUserId, BuildCreateRequest request) {
        if(currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        if(request == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        // 创建装机单实体
        UserBuild userBuild = new UserBuild();
        userBuild.setUserId(currentUserId);
        createRequestFillBuild(request, userBuild);

        // 插入装机单
        int rows = userBuildMapper.insert(userBuild);
        if(rows <= 0 || userBuild.getId() == null) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_CREATE_FAILED);
        }

        return userBuild.getId();
    }


    /**
     * 更新装机单基本信息
     * 包括标题、描述、封面图片
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param request 更新请求
     * @return 是否成功更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBuildBasicInfo(Long currentUserId, Long buildId, BuildUpdateRequest request) {
        if(currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        if(request == null || buildId == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        // 查询装机单是否存在
        UserBuild userBuild = userBuildMapper.selectById(buildId);

        if(userBuild == null || FieldConstant.DELETED == userBuild.getIsDeleted()) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, MessageConstant.BUILD_NOT_FOUND);
        }

        // 验证当前用户是否是装机单的创建者
        if(!Objects.equals(currentUserId, userBuild.getUserId())) {
            throw new CommonException(CommonErrorCode.FORBIDDEN, MessageConstant.BUILD_FORBIDDEN);
        }

        // 构建更新实体的原因:
        // mybatis-plus配置的是严格填充, 不会覆盖已有的字段
        // 直接修改原来的实体会导致填充更新时间失败
        UserBuild updateBuild = new UserBuild();
        updateBuild.setId(buildId);
        updateBuild.setTitle(request.getTitle().trim());
        updateBuild.setDescription(StringCommonUtils.trimToNull(request.getDescription()));
        updateBuild.setCoverImage(StringCommonUtils.trimToNull(request.getCoverImage()));

        int rows = userBuildMapper.updateById(updateBuild);
        if(rows <= 0) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_UPDATE_FAILED);
        }

        return true;
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


    /**
     * 根据创建请求填充装机单
     * @param request 创建请求参数
     * @param build   用户装机单实体
     * @return 用户装机单实体
     */
    private UserBuild createRequestFillBuild(BuildCreateRequest request, UserBuild build) {
        build.setTitle(request.getTitle().trim());
        build.setDescription(StringCommonUtils.trimToNull(request.getDescription()));
        build.setIsPublic(
            Boolean.TRUE.equals(request.getIsPublic())
            ? FieldConstant.PUBLIC
            : FieldConstant.PRIVATE
        );
        build.setStatus(FieldConstant.BUILD_STATUS_DRAFT);
        build.setTotalPrice(BigDecimal.ZERO);
        build.setTotalPower(BigDecimal.ZERO);
        build.setCoverImage(StringCommonUtils.trimToNull(request.getCoverImage()));
        return build;
    }


}
