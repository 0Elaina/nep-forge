package com.nep.hardware.service.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.QueryConstant;
import com.nep.common.constants.ValidationConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.exception.HardwareErrorCode;
import com.nep.common.result.PageResult;
import com.nep.hardware.vo.HardwareCompareFieldVO;
import com.nep.hardware.vo.HardwareCompareItemVO;
import com.nep.hardware.vo.HardwareCompareVO;
import com.nep.hardware.vo.HardwareDetailVO;
import com.nep.hardware.vo.HardwareListVO;
import com.nep.hardware.dto.HardwareCompareRequest;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.entity.Hardware;
import com.nep.hardware.entity.HardwareCategory;
import com.nep.hardware.mapper.HardwareCategoryMapper;
import com.nep.hardware.mapper.HardwareMapper;
import com.nep.hardware.service.HardwareService;

import lombok.RequiredArgsConstructor;

/**
 * 配件服务实现类
 * 提供配件相关的业务逻辑，如查询、插入、更新和删除。
 */
@Service
@RequiredArgsConstructor
public class HardwareServiceImpl implements HardwareService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final String SORT_FIELD_PRICE = "price";

    private static final String SORT_FIELD_CREATE_TIME = "createTime";

    private static final String SORT_FIELD_RELEASE_DATE = "releaseDate";

    private final HardwareMapper hardwareMapper;
    private final ObjectMapper objectMapper;
    private final HardwareCategoryMapper hardwareCategoryMapper;

    /**
     * 查询配件列表
     * 
     * @param request 查询参数
     * @return 配件列表
     */
    @Override
    public PageResult<HardwareListVO> listHardware(HardwareQueryRequest request) {
        // 检查请求参数是否为空，若为空则创建默认实例
        HardwareQueryRequest query = request == null ? new HardwareQueryRequest() : request;

        int pageNum = normalizePageNum(query.getPageNum());
        int pageSize = normalizePageSize(query.getPageSize());

        // 创建分页对象
        Page<Hardware> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Hardware> wrapper = new LambdaQueryWrapper<>();

        wrapper.select(
                Hardware::getId,
                Hardware::getCategoryId,
                Hardware::getName,
                Hardware::getBrand,
                Hardware::getPrice,
                Hardware::getCoverImage,
                Hardware::getReleaseDate,
                Hardware::getSourceName,
                Hardware::getCreateTime);

        // 过滤已删除配件
        wrapper.eq(Hardware::getIsDeleted, FieldConstant.NOT_DELETED);

        // 如果指定了分类ID，则添加分类条件
        if (query.getCategoryId() != null) {
            wrapper.eq(Hardware::getCategoryId, query.getCategoryId());
        }

        // 如果指定了品牌，则添加品牌条件
        if (StringUtils.hasText(query.getBrand())) {
            wrapper.eq(Hardware::getBrand, query.getBrand().trim());
        }

        // 如果指定了关键词，则添加关键词条件
        // 搜索名称和品牌
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(item -> item
                    .like(Hardware::getName, keyword)
                    .or()
                    .like(Hardware::getBrand, keyword));
        }

        // 如果指定了最小价格，则添加最小价格条件
        if (query.getMinPrice() != null) {
            wrapper.ge(Hardware::getPrice, query.getMinPrice());
        }

        // 如果指定了最大价格，则添加最大价格条件
        if (query.getMaxPrice() != null) {
            wrapper.le(Hardware::getPrice, query.getMaxPrice());
        }

        // 应用排序条件
        applyOrder(wrapper, query.getSortField(), query.getSortOrder());

        // 执行分页查询，返回包含 records（当前页数据）和 total（总条数）的分页结果
        Page<Hardware> resultPage = hardwareMapper.selectPage(page, wrapper);

        // 将查询结果转换为 HardwareListVO 列表
        List<HardwareListVO> records = resultPage.getRecords()
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
     * 查询配件详情
     * 
     * @param id 配件ID
     * @return 配件详情
     */
    @Override
    public HardwareDetailVO getHardwareDetail(Long id) {
        // 构建查询条件包装器，查询指定ID的配件
        Hardware hardware = hardwareMapper.selectOne(
                new LambdaQueryWrapper<Hardware>()
                        .eq(Hardware::getId, id)
                        .eq(Hardware::getIsDeleted, FieldConstant.NOT_DELETED)
                        .last("limit 1"));

        if (hardware == null) {
            throw new CommonException(HardwareErrorCode.HARDWARE_NOT_FOUND);
        }

        // 查询配件分类
        HardwareCategory category = hardwareCategoryMapper.selectOne(
                new LambdaQueryWrapper<HardwareCategory>()
                        .eq(HardwareCategory::getId, hardware.getCategoryId())
                        .eq(HardwareCategory::getIsDeleted, FieldConstant.NOT_DELETED)
                        .last("limit 1"));

        String categoryName = category == null ? null : category.getName();

        return toDetailVO(hardware, categoryName);
    }


    /**
     * 对比配件
     * @param request 对比参数
     * @return 对比结果
     */
    @Override
    public HardwareCompareVO compareHardware(HardwareCompareRequest request) {
        // 校验对比参数是否为空
        if (request == null || request.getHardwareIds() == null || request.getHardwareIds().isEmpty()) {
            throw new CommonException(
                    CommonErrorCode.REQUEST_PARAM_ERROR,
                    MessageConstant.HARDWARE_COMPARE_ID_NOT_NULL);
        }

        // 获取对比配件ID列表, 过滤空值并去重
        List<Long> hardwareIds = request.getHardwareIds()
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 校验对比配件ID数量是否在有效范围内
        if (hardwareIds.size() < ValidationConstant.HARDWARE_COMPARE_SIZE_MIN
                || hardwareIds.size() > ValidationConstant.HARDWARE_COMPARE_SIZE_MAX) {
            throw new CommonException(
                    CommonErrorCode.REQUEST_PARAM_ERROR,
                    MessageConstant.HARDWARE_COMPARE_SIZE_LIMIT);
        }

        // 查询对比配件列表, 包含分类、品牌、价格、封面图片、规格差异化参数
        List<Hardware> hardwareList = hardwareMapper.selectList(
                new LambdaQueryWrapper<Hardware>()
                        .select(
                                Hardware::getId,
                                Hardware::getCategoryId,
                                Hardware::getName,
                                Hardware::getBrand,
                                Hardware::getPrice,
                                Hardware::getCoverImage,
                                Hardware::getSpecsJson)
                        .in(Hardware::getId, hardwareIds)
                        .eq(Hardware::getId, FieldConstant.NOT_DELETED));

        // 校验查询结果是否与对比配件ID数量一致
        // 如果查询结果为空或数量与对比配件ID数量不一致, 则抛出异常
        if(hardwareList == null || hardwareList.size() != hardwareIds.size()) {
            throw new CommonException(HardwareErrorCode.HARDWARE_NOT_FOUND);
        }

        // 将查询结果转换为 Map<Long, Hardware>
        // 键为配件ID, 值为配件对象
        Map<Long, Hardware> hardwareMap = hardwareList.stream()
                .collect(Collectors.toMap(
                    Hardware::getId,
                    Function.identity()
                ));

        // 按对比配件ID顺序排序
        List<Hardware> orderedHardwareList = hardwareIds.stream()
                    .map(hardwareMap::get)
                    .toList();

        // 校验排序后的配件列表是否包含空值
        // match为什么不直接填null: 因为查询结果中可能包含null值, 而null值不能直接比较
        if(orderedHardwareList.stream().anyMatch(Objects::isNull)){
            throw new CommonException(HardwareErrorCode.HARDWARE_NOT_FOUND);
        }

        Integer categoryId = orderedHardwareList.get(0).getCategoryId();
        // 校验所有配件是否属于同一分类
        boolean sameCategory = orderedHardwareList.stream()
                .allMatch(hardware -> hardware.getCategoryId().equals(categoryId));

        if (!sameCategory) {
            throw new CommonException(HardwareErrorCode.HARDWARE_COMPARE_CATEGORY_NOT_SAME);
        }

        // 查询配件分类
        HardwareCategory category = hardwareCategoryMapper.selectOne(
            new LambdaQueryWrapper<HardwareCategory>()
                    .eq(HardwareCategory::getId, categoryId)
                    .eq(HardwareCategory::getIsDeleted, FieldConstant.NOT_DELETED)
                    .last("limit 1")
        );

        // 获取分类名称
        String categoryName = category == null ? null : category.getName();

        // 转换为对比项VO列表
        List<HardwareCompareItemVO> items = orderedHardwareList.stream()
                .map(this::toCompareItemVO)
                .toList();

        return HardwareCompareVO.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .items(items)
                .fields(buildCompareFields(items))
                .build();
    }

    /**
     * 校验并归一化分页页码
     * 
     * @param pageNum 分页页码
     * @return 归一化后的分页页码
     */
    private int normalizePageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            return QueryConstant.DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    /**
     * 校验并归一化分页每页数量
     * 
     * @param pageSize 分页每页数量
     * @return 归一化后的分页每页数量
     */
    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return QueryConstant.DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    /**
     * 应用排序条件
     * 
     * @param wrapper   查询条件包装器
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     */
    private void applyOrder(
            LambdaQueryWrapper<Hardware> wrapper,
            String sortField,
            String sortOrder) {
        // equalsIgnoreCase: 忽略大小写比较
        boolean isAsc = QueryConstant.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder);

        // 按价格排序
        if (SORT_FIELD_PRICE.equals(sortField)) {
            // orderBy(condition, isAsc, column)
            // condition: true 表示始终生效，false 可动态跳过当前排序
            // isAsc: true 升序(ASC)，false 降序(DESC)，由 sortOrder 参数控制
            // Hardware::getPrice: 按价格字段排序
            wrapper.orderBy(true, isAsc, Hardware::getPrice);
            return;
        }

        // 按发布时间排序
        if (SORT_FIELD_RELEASE_DATE.equals(sortField)) {
            wrapper.orderBy(true, isAsc, Hardware::getReleaseDate);
            return;
        }

        // 按创建时间排序
        if (SORT_FIELD_CREATE_TIME.equals(sortField)) {
            wrapper.orderBy(true, isAsc, Hardware::getCreateTime);
            return;
        }

        // 默认按创建时间倒序
        wrapper.orderByDesc(Hardware::getCreateTime);

    }

    /**
     * 解析配件差异化参数JSON字符串为 Map
     * 
     * @param specsJson 配件差异化参数JSON字符串
     * @return 解析后的 Map 对象
     */
    private Map<String, Object> parseSpecs(String specsJson) {
        // 检查 JSON 字符串是否为空
        if (!StringUtils.hasText(specsJson)) {
            return Collections.emptyMap();
        }

        // 解析 JSON 字符串为 Map
        try {
            return objectMapper.readValue(
                    specsJson,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 构建对比字段列表
     * 
     * @param items 对比项列表
     * @return 对比字段列表
     */
    private List<HardwareCompareFieldVO> buildCompareFields(List<HardwareCompareItemVO> items) {
        // 构建对比字段列表, 从所有项中提取所有参数 key
        Set<String> fieldKeys = new LinkedHashSet<>();

        // 遍历所有项，提取所有参数 key
        for (HardwareCompareItemVO item : items) {
            if (item.getSpecs() != null && !item.getSpecs().isEmpty()) {
                // 提取当前项的所有参数 key 并添加到对比字段列表中
                fieldKeys.addAll(item.getSpecs().keySet());
            }
        }

        return fieldKeys.stream()
                .map(key -> HardwareCompareFieldVO.builder()
                        .key(key)
                        .label(key)
                        .unit(null)
                        .build())
                .toList();
    }

    /**
     * 将硬件实体转换为列表VO
     * 
     * @param hardware 硬件实体
     * @return 列表VO
     */
    private HardwareListVO toListVO(Hardware hardware) {
        return HardwareListVO.builder()
                .id(String.valueOf(hardware.getId()))
                .categoryId(hardware.getCategoryId())
                .name(hardware.getName())
                .brand(hardware.getBrand())
                .price(hardware.getPrice())
                .coverImage(hardware.getCoverImage())
                .releaseDate(hardware.getReleaseDate())
                .sourceName(hardware.getSourceName())
                .createTime(hardware.getCreateTime())
                .build();
    }

    /**
     * 将硬件实体转换为详情VO
     * 
     * @param hardware     硬件实体
     * @param categoryName 分类名称
     * @return 详情VO
     */
    private HardwareDetailVO toDetailVO(Hardware hardware, String categoryName) {
        return HardwareDetailVO.builder()
                .id(String.valueOf(hardware.getId()))
                .categoryId(hardware.getCategoryId())
                .categoryName(categoryName)
                .name(hardware.getName())
                .brand(hardware.getBrand())
                .price(hardware.getPrice())
                .coverImage(hardware.getCoverImage())
                .sourceName(hardware.getSourceName())
                .sourceUrl(hardware.getSourceUrl())
                .releaseDate(hardware.getReleaseDate())
                .lastSyncTime(hardware.getLastSyncTime())
                .specs(parseSpecs(hardware.getSpecsJson()))
                .liked(false)
                .favorited(false)
                .createTime(hardware.getCreateTime())
                .build();
    }

    /**
     * 将硬件实体转换为对比项VO
     * 
     * @param hardware 硬件实体
     * @return 对比项VO
     */
    private HardwareCompareItemVO toCompareItemVO(Hardware hardware) {
        return HardwareCompareItemVO.builder()
                .id(String.valueOf(hardware.getId()))
                .name(hardware.getName())
                .brand(hardware.getBrand())
                .price(hardware.getPrice())
                .coverImage(hardware.getCoverImage())
                .specs(parseSpecs(hardware.getSpecsJson()))
                .build();
    }

}
