package com.nep.hardware.service.impl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.nep.hardware.mapper.HardwareCategoryMapper;
import com.nep.hardware.vo.HardwareCategoryTreeVO;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.HardwareCategoryConstant;
import com.nep.hardware.entity.HardwareCategory;
import com.nep.hardware.service.HardwareCategoryService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;

/**
 * 硬件分类服务实现类
 * 提供硬件分类相关的业务逻辑实现，如查询、插入、更新和删除。
 */
@Service
@RequiredArgsConstructor
public class HardwareCategoryServiceImpl implements HardwareCategoryService {
    private final HardwareCategoryMapper hardwareCategoryMapper;

    @Override
    public List<HardwareCategoryTreeVO> listCategoryTree() {
        // 查询所有未删除的顶级硬件分类，按父ID、排序号、ID升序排列
        List<HardwareCategory> categories = hardwareCategoryMapper.selectList(
            new LambdaQueryWrapper<HardwareCategory>()
                // 只查询未被逻辑删除的记录
                .eq(HardwareCategory::getIsDeleted, FieldConstant.NOT_DELETED)
                // 筛选启用状态的分类
                .eq(HardwareCategory::getStatus, FieldConstant.ENABLED)
                // 按父ID、排序号、ID 升序排列，确保返回顺序稳定且可控
                .orderByAsc(
                    HardwareCategory::getParentId,
                    HardwareCategory::getSortOrder,
                    HardwareCategory::getId
                )
        );

        // 如果查询结果为空，返回空列表
        if (categories == null || categories.isEmpty()) return List.of();

        // 将查询结果转换为硬件分类树VO列表
        // 并根据ID创建一个映射，方便后续根据ID快速查找
        List<HardwareCategoryTreeVO> nodes = categories.stream()
                .map(this::toTreeVO)
                .collect(Collectors.toList());

        
        /**
         * 构建 "分类ID → 树节点" 的映射表。
         *
         * 为什么要构建这个 Map？
         *   在将平铺的列表构造成树形结构时，需要频繁地根据 parentId 查找对应的父节点。
         *   如果每次都遍历列表，时间复杂度为 O(n²)；而构建 Map 后每次查找为 O(1)，
         *   可以显著提升构建树的性能。这里提前构建好映射，为后续的树构建做准备。
         *
         * Collectors.toMap 四个参数的含义：
         *
         *   1. keyMapper  (HardwareCategoryTreeVO::getId)
         *      ─ 从每个节点中提取 ID 作为 Map 的键
         *      ─ 因为 ID 是数据库主键，全局唯一，天然适合作为键
         *
         *   2. valueMapper (Function.identity())
         *      ─ 返回节点本身作为 Map 的值
         *      ─ identity() 等价于 node -> node，语义更清晰
         *
         *   3. mergeFunction ((oldValue, newValue) -> oldValue)
         *      ─ 当两个节点 ID 相同时（理论上不会发生，但为了健壮性）保留先放入的旧值
         *      ─ 因为查询结果按 parentId, sortOrder, id 排序，旧值的排序更靠前，更合理
         *
         *   4. mapFactory (LinkedHashMap::new)
         *      ─ 使用 LinkedHashMap 而非默认的 HashMap
         *      ─ LinkedHashMap 会维护元素的插入顺序，即数据库查询返回的排序顺序
         *      ─ 后续遍历该 Map 时，节点依然按 parentId, sortOrder, id 排列，保证展示顺序可控
         */
        Map<Integer, HardwareCategoryTreeVO> nodeMap = nodes.stream()
                .collect(Collectors.toMap(
                    HardwareCategoryTreeVO::getId,
                    Function.identity(),
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                ));

        List<HardwareCategoryTreeVO> roots = new ArrayList<>();

        // 遍历所有节点，构建树结构
        for (HardwareCategoryTreeVO node: nodes) {
            // 如果是根分类，直接添加到根列表
            if (node.getParentId().equals(HardwareCategoryConstant.ROOT_PARENT_ID)) {
                roots.add(node);
                continue;
            }
            
            // 如果不是根分类，根据父ID查找父节点
            HardwareCategoryTreeVO parent = nodeMap.get(node.getParentId());
            if (parent != null) {
                parent.getChildren().add(node);
            }
        }



        return roots;
    }

    /**
     * 将硬件分类实体转换为硬件分类树VO
     * @param category 硬件分类实体
     * @return 硬件分类树VO
     */
    private HardwareCategoryTreeVO toTreeVO(HardwareCategory category) {
        return HardwareCategoryTreeVO.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentId())
                .sortOrder(category.getSortOrder())
                .build();
    }
}