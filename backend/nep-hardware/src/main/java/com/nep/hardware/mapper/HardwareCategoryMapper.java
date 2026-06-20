package com.nep.hardware.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nep.hardware.entity.HardwareCategory;

/**
 * 硬件分类Mapper接口
 * 提供硬件分类相关的数据库操作，如查询、插入、更新和删除。
 */
@Mapper
public interface HardwareCategoryMapper extends BaseMapper<HardwareCategory> {
    
}
