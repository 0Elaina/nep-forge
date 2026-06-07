package com.nep.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nep.system.entity.RoleUser;

/**
 * 角色用户映射接口
 * @author Neptune
 * @date 2026-06-07
 */
@Mapper
public interface RoleUserMapper extends BaseMapper<RoleUser> {
    
}
