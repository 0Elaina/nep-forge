package com.nep.hardware.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nep.hardware.entity.Hardware;

/**
 * 配件Mapper
 */
@Mapper
public interface HardwareMapper extends BaseMapper<Hardware> {
    
}
