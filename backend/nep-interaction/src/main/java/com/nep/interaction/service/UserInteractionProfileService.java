package com.nep.interaction.service;

import java.util.List;

import com.nep.common.result.PageResult;
import com.nep.interaction.vo.FavoriteFolderVO;
import com.nep.interaction.vo.UserInteractionTargetVO;
import com.nep.interaction.dto.UserInteractionQueryRequest;

/**
 * 用户交互服务接口
 */
public interface UserInteractionProfileService {
    /**
     * 查询用户点赞列表
     * @param currentUserId 当前用户ID
     * @param request 查询参数
     * @return 点赞列表
     */
    PageResult<UserInteractionTargetVO> listMyLikes(Long currentUserId, UserInteractionQueryRequest request);    

    /**
     * 查询用户收藏列表
     * @param currentUserId 当前用户ID
     * @param request 查询参数
     * @return 收藏列表
     */
    PageResult<UserInteractionTargetVO> listMyFavorites(Long currentUserId, UserInteractionQueryRequest request);

    /**
     * 查询用户收藏夹列表
     * @param currentUserId 当前用户ID
     * @return 收藏夹列表
     */
    List<FavoriteFolderVO> listMyFavoriteFolders(Long currentUserId);
}
