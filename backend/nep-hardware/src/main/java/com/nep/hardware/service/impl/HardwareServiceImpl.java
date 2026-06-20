package com.nep.hardware.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.QueryConstant;
import com.nep.common.result.PageResult;
import com.nep.hardware.vo.HardwareListVO;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.entity.Hardware;
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

    /**
     * 查询配件列表
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
                Hardware::getReleaseTime,
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
            wrapper.orderBy(true, isAsc, Hardware::getReleaseTime);
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
                .releaseDate(hardware.getReleaseTime())
                .sourceName(hardware.getSourceName())
                .createTime(hardware.getCreateTime())
                .build();
    }
}
