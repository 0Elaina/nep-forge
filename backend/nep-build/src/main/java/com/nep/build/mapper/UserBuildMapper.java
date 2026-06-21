package com.nep.build.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nep.build.entity.UserBuild;

/**
 * 装机单Mapper
 */
@Mapper
public interface UserBuildMapper extends BaseMapper<UserBuild> {
    
}
