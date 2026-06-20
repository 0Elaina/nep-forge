package com.nep.hardware.service;

import java.util.List;

import com.nep.hardware.vo.HardwareCategoryTreeVO;

/**
 * 硬件分类服务接口
 * 提供硬件分类相关的业务逻辑，如查询、插入、更新和删除。
 */
public interface HardwareCategoryService {
    
    /**
     * 查询硬件分类树
     * @return 硬件分类树列表
     */
    List<HardwareCategoryTreeVO> listCategoryTree();
}
