package com.nep.interaction.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nep.interaction.entity.Favorite;
import com.nep.interaction.vo.FavoriteFolderVO;

/**
 * 收藏夹映射接口
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    /**
     * 查询用户收藏夹列表
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FavoriteFolderVO> selectMyFavoriteFolders(@Param("userId") Long userId);
}
