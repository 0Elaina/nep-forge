package com.nep.build.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.nep.build.service.BuildService;
import com.nep.build.vo.BuildListVO;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.QueryConstant;
import com.nep.common.exception.BuildErrorCode;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.exception.HardwareErrorCode;
import com.nep.common.result.PageResult;
import com.nep.common.util.PageQueryUtils;
import com.nep.common.util.StringCommonUtils;
import com.nep.hardware.entity.Hardware;
import com.nep.hardware.mapper.HardwareMapper;

import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nep.build.dto.BuildCreateRequest;
import com.nep.build.dto.BuildItemAddRequest;
import com.nep.build.dto.BuildItemUpdateRequest;
import com.nep.build.dto.BuildQueryRequest;
import com.nep.build.dto.BuildUpdateRequest;
import com.nep.build.entity.UserBuild;
import com.nep.build.entity.UserBuildDetail;
import com.nep.build.mapper.UserBuildDetailMapper;
import com.nep.build.mapper.UserBuildMapper;

@Service
@RequiredArgsConstructor
public class BuildServiceImpl implements BuildService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final String SORT_FIELD_CREATE_TIME = "createTime";
    private static final String SORT_FIELD_TOTAL_PRICE = "totalPrice";
    private static final String SORT_FIELD_TOTAL_POWER = "totalPower";
    private static final String TDP = "tdp";
    private static final String TDP_PATTERN = "(\\d+(\\.\\d+)?)";

    private final UserBuildMapper userBuildMapper;
    private final UserBuildDetailMapper userBuildDetailMapper;
    private final ObjectMapper objectMapper;
    private final HardwareMapper hardwareMapper;

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
      * 添加装机单配件
      * @param currentUserId 当前用户ID
      * @param buildId 装机单ID
      * @param request 添加请求
      * @return 装机单详情ID
      */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addBuildItem(Long currentUserId, Long buildId, BuildItemAddRequest request) {
        if(currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        if(buildId == null || request == null || request.getHardwareId() == null || request.getQuantity() == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        if(request.getQuantity() < 1) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, MessageConstant.BUILD_HARDWARE_QUANTITY_MIN_LIMIT);
        }

        // 查询装机单是否存在
        UserBuild userBuild = getOwnedBuildOrThrow(currentUserId, buildId);

        // 根据硬件ID查询硬件
        Hardware hardware = hardwareMapper.selectById(request.getHardwareId());
        // 如果硬件不存在或已被删除, 抛出硬件不存在异常
        if(hardware == null || FieldConstant.DELETED == hardware.getIsDeleted()) {
            throw new CommonException(HardwareErrorCode.HARDWARE_NOT_FOUND);
        }

        // 检查是否已存在相同硬件的装机单详情
        Long existsCount = userBuildDetailMapper.selectCount(
            new LambdaQueryWrapper<UserBuildDetail>()
                    .eq(UserBuildDetail::getBuildId, userBuild.getId())
                    .eq(UserBuildDetail::getHardwareId, request.getHardwareId())
        );

        // 如果已存在相同硬件的装机单详情, 抛出装机单配件已存在异常
        if(existsCount > 0) {
            throw new CommonException(BuildErrorCode.BUILD_HARDWARE_EXISTS);
        }

        // 构建装机单详情实体
        UserBuildDetail detail = new UserBuildDetail();
        detail.setBuildId(userBuild.getId());
        detail.setHardwareId(request.getHardwareId());
        detail.setQuantity(request.getQuantity());

        int rows = userBuildDetailMapper.insert(detail);
        // 如果插入失败或返回的ID为null, 抛出添加失败异常
        if (rows <= 0 || detail.getId() == null) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_HARDWARE_ADD_FAILED);
        }

        // 计算装机单总价格和总功率
        recalculateBuildTotal(userBuild.getId());

        return detail.getId();
    }

    /**
     * 删除装机单配件
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param detailId 装机单详情ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeBuildItem(Long currentUserId, Long buildId, Long detailId) {
        if(currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }
        if(buildId == null || detailId == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        // 查询装机单是否存在
        UserBuild userBuild = getOwnedBuildOrThrow(currentUserId, buildId);

        // 根据装机单详情ID和装机单ID查询装机单详情
        UserBuildDetail detail = userBuildDetailMapper.selectOne(
            new LambdaQueryWrapper<UserBuildDetail>()
                    .eq(UserBuildDetail::getId, detailId)
                    .eq(UserBuildDetail::getBuildId, userBuild.getId())
        );

        // 如果装机单详情不存在, 抛出装机单配件不存在异常
        if(detail == null) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, MessageConstant.BUILD_HARDWARE_NOT_FOUND);
        }

        int rows = userBuildDetailMapper.deleteById(detailId);
        // 如果删除失败, 抛出删除失败异常
        if(rows <= 0) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_HARDWARE_REMOVE_FAILED);
        }

        // 计算装机单总价格和总功率
        recalculateBuildTotal(buildId);

        // 返回删除成功
        return true;
    }

    /**
     * 更新装机单配件数量
     * @param currentUserId 当前用户ID
     * @param buildId 装机单ID
     * @param detailId 装机单详情ID
     * @param request 更新装机单配件数量请求
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBuildItemQuantity(
        Long currentUserId,
        Long buildId,
        Long detailId,
        BuildItemUpdateRequest request
    ) {
        if(currentUserId == null){
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }
        if(buildId == null || detailId == null || request == null || request.getQuantity() == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        if(request.getQuantity() < 1) {
            throw new CommonException(
                CommonErrorCode.REQUEST_PARAM_ERROR,
                MessageConstant.BUILD_HARDWARE_QUANTITY_MIN_LIMIT
            );
        }

        // 查询装机单是否存在
        UserBuild userBuild = getOwnedBuildOrThrow(currentUserId, buildId);

        // 根据装机单详情ID和装机单ID查询装机单详情
        UserBuildDetail detail = userBuildDetailMapper.selectOne(
            new LambdaQueryWrapper<UserBuildDetail>()
                    .eq(UserBuildDetail::getId, detailId)
                    .eq(UserBuildDetail::getBuildId, userBuild.getId()) 
        );

        // 如果装机单详情不存在, 抛出装机单配件不存在异常
        if(detail == null) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, MessageConstant.BUILD_HARDWARE_NOT_FOUND);
        }

        // 构建装机单详情更新实体
        UserBuildDetail updateDetail = new UserBuildDetail();
        updateDetail.setId(detailId);
        updateDetail.setQuantity(request.getQuantity());

        // 更新装机单详情
        int rows = userBuildDetailMapper.updateById(updateDetail);
        if(rows <= 0){
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_UPDATE_FAILED);
        }

        // 计算装机单总价格和总功率
        recalculateBuildTotal(userBuild.getId());
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
      * 获取装机单实体, 并验证当前用户是否是装机单的创建者
      * 
      * @param currentUserId 当前用户ID
      * @param buildId       装机单ID
      * @return 用户装机单实体
      */
    private UserBuild getOwnedBuildOrThrow(Long currentUserId, Long buildId) {
        UserBuild userBuild = userBuildMapper.selectById(buildId);

        if(userBuild == null || FieldConstant.DELETED == userBuild.getIsDeleted()) {
            throw new CommonException(BuildErrorCode.BUILD_NOT_FOUND);
        }

        if(!Objects.equals(userBuild.getUserId(), currentUserId)) {
            throw new CommonException(BuildErrorCode.BUILD_FORBIDDEN);
        }

        return userBuild;
    }


    /**
     * 重新计算装机单总价格和总功率
     * @param buildId 装机单ID
     */
    private void recalculateBuildTotal(Long buildId) {
        // 查询所有配件详情
        List<UserBuildDetail> details = userBuildDetailMapper.selectList(
            new LambdaQueryWrapper<UserBuildDetail>()
                    .eq(UserBuildDetail::getBuildId, buildId)  
        );

        // 初始化总价格和总功率为0
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalPower = BigDecimal.ZERO;

        // 如果没有配件详情, 则直接更新装机单总价格和总功率为0
        if(details == null || details.isEmpty()) {
            updateBuildTotal(buildId, totalPrice, totalPower);
            return;
        }

        // 获取所有配件详情中的硬件ID
        List<Long> hardwareIds = details.stream()
                .map(UserBuildDetail::getHardwareId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 查询没有硬件ID, 则直接更新装机单总价格和总功率为0
        if(hardwareIds == null || hardwareIds.isEmpty()) {
            updateBuildTotal(buildId, totalPrice, totalPower);
            return;
        }

        // 查询所有的配件实体
        List<Hardware> hardwareList = hardwareMapper.selectList(
            new LambdaQueryWrapper<Hardware>()
                    .in(Hardware::getId, hardwareIds)
                    .eq(Hardware::getIsDeleted, FieldConstant.NOT_DELETED) 
        );

        // 构建硬件实体映射表
        Map<Long, Hardware> hardwareMap = hardwareList.stream()
                .collect(Collectors.toMap(Hardware::getId, hardware -> hardware));

        for(UserBuildDetail detail : details) {
            // 根据 hardwareMap 映射硬件实体
            Hardware hardware = hardwareMap.get(detail.getHardwareId());

            // 如果硬件实体不存在或已删除, 则跳过当前配件详情
            if(hardware == null || FieldConstant.DELETED == hardware.getIsDeleted()) continue;

            // 获取当前配件的购买数量
            BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
            // 获取当前配件的单价, 如果为null, 则默认0
            BigDecimal price = hardware.getPrice() == null ? BigDecimal.ZERO : hardware.getPrice();


            // 累计当前配件的价格：单价 × 数量，累加到总价
            // multiply: BigDecimal 的乘法方法，返回 price * quantity 的结果（BigDecimal 类型）
            totalPrice = totalPrice.add(price.multiply(quantity));

            // 从硬件规格JSON中提取TDP值
            BigDecimal tdp = extractTdp(hardware.getSpecsJson());
            // 累计当前配件的功率：TDP × 数量，累加到总功率
            totalPower = totalPower.add(tdp.multiply(quantity));
        }

        // 更新装机单总价格和总功率
        updateBuildTotal(buildId, totalPrice, totalPower);
    }

    /**
     * 从硬件规格JSON中提取TDP值
     * 
     * @param specsJson 硬件规格JSON字符串
     * @return TDP值
     */
    private BigDecimal extractTdp(String specsJson) {
        // 如果规格JSON为空, 则直接返回0
        if(!StringUtils.hasText(specsJson)) return BigDecimal.ZERO;

        try {
            JsonNode root = objectMapper.readTree(specsJson);
            JsonNode tdpNode = root.get(TDP);

            // 如果 TDP 字段不存在或为null, 则返回0
            if(tdpNode == null || tdpNode.isNull()) return BigDecimal.ZERO;

            // 如果 TDP 字段是数字, 则直接返回
            if(tdpNode.isNumber()) return tdpNode.decimalValue();

            // 如果 TDP 字段是字符串, 则尝试解析为数字
            String text = tdpNode.asText();
            // 使用正则表达式匹配 TDP 值
            Matcher matcher = Pattern.compile(TDP_PATTERN).matcher(text);

            // 如果匹配成功, 则返回匹配到的数字部分
            if(matcher.find()) {
                // 提取匹配到的数字部分
                return new BigDecimal(matcher.group(1));
            }
            // 如果匹配失败, 则返回0
            return BigDecimal.ZERO;
        } catch (Exception e) {
            // 如果解析过程中发生异常, 则返回0
            return BigDecimal.ZERO;
        }
    }

    /**
     * 更新装机单总价格和总功率
     * 
     * @param buildId 装机单ID
     * @param totalPrice 总价格
     * @param totalPower 总功率
     */
    private void updateBuildTotal(Long buildId, BigDecimal totalPrice, BigDecimal totalPower) {
        UserBuild updateBuild = new UserBuild();
        updateBuild.setId(buildId);
        updateBuild.setTotalPrice(totalPrice);
        updateBuild.setTotalPower(totalPower);

        int rows = userBuildMapper.updateById(updateBuild);

        if(rows <= 0) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR, MessageConstant.BUILD_TOTAL_DATA_UPDATE_FAILED);
        }
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
