package com.nep.interaction.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.result.PageResult;
import com.nep.common.util.PageQueryUtils;
import com.nep.interaction.dto.UserInteractionQueryRequest;
import com.nep.interaction.mapper.FavoriteMapper;
import com.nep.interaction.mapper.UserInteractionMapper;
import com.nep.interaction.service.UserInteractionProfileService;
import com.nep.interaction.vo.FavoriteFolderVO;
import com.nep.interaction.vo.UserInteractionTargetVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInteractionProfileServiceImpl implements UserInteractionProfileService {

    private final FavoriteMapper favoriteMapper;
    private final UserInteractionMapper userInteractionMapper;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 查询用户点赞列表
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 点赞分页结果列表
     */
    @Override
    public PageResult<UserInteractionTargetVO> listMyLikes(Long currentUserId, UserInteractionQueryRequest request) {
        return listMyInteractions(currentUserId, request, FieldConstant.ACTION_TYPE_LIKE);
    }

    /**
     * 查询用户收藏列表
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @return 收藏夹分页结果列表
     */
    @Override
    public PageResult<UserInteractionTargetVO> listMyFavorites(Long currentUserId,
            UserInteractionQueryRequest request) {
        return listMyInteractions(currentUserId, request, FieldConstant.ACTION_TYPE_FAVORITE);
    }

    /**
     * 查询用户收藏夹列表
     * 
     * @param currentUserId 当前用户ID
     * @return 收藏夹列表
     */
    @Override
    public List<FavoriteFolderVO> listMyFavoriteFolders(Long currentUserId) {
        if (currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        // 查询用户收藏夹列表
        return favoriteMapper.selectMyFavoriteFolders(currentUserId);
    }

    /**
     * 查询用户交互列表
     * 
     * @param currentUserId 当前用户ID
     * @param request       查询参数
     * @param actionType    操作类型
     * @return 交互分页结果列表
     */
    private PageResult<UserInteractionTargetVO> listMyInteractions(
            Long currentUserId,
            UserInteractionQueryRequest request,
            Integer actionType) {
        if (currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        // 如果查询参数为空，创建默认查询参数
        if (request == null) {
            request = new UserInteractionQueryRequest();
        }

        // 校验目标类型是否有效
        validateTargetType(request.getTargetType());
        // 校验收藏夹ID是否有效
        validateFolderId(request.getFolderId());

        int pageNum = PageQueryUtils.normalizePageNum(request.getPageNum());
        int pageSize = PageQueryUtils.normalizePageSize(request.getPageSize(), MAX_PAGE_SIZE);

        // 创建分页参数
        Page<UserInteractionTargetVO> page = new Page<>(pageNum, pageSize);

        // 获取目标类型参数
        Integer targetType = request.getTargetType();

        // 校验收藏夹ID是否有效
        Long folderId = normalizeFolderId(actionType, request.getFolderId());

        // 声明分页结果变量
        IPage<UserInteractionTargetVO> resultPage;

        // 如果目标类型为空，查询所有交互
        if(targetType == null) {
            resultPage = userInteractionMapper.selectMixedInteractionPage(page, currentUserId, actionType, folderId);
        } else {
            resultPage = selectByTargetType(
                page,
                currentUserId,
                actionType,
                targetType,
                folderId);
        }

        // 获取分页结果列表
        List<UserInteractionTargetVO> records = resultPage.getRecords();
        // 填充目标路径
        records.forEach(this::fillTargetPath);

        // 返回分页结果
        return PageResult.of(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    /**
     * 根据目标类型查询用户交互列表
     * @param page 分页参数
     * @param currentUserId 当前用户ID
     * @param actionType 操作类型
     * @param targetType 目标类型
     * @param folderId 收藏夹ID
     * @return 分页结果列表
     */
    private IPage<UserInteractionTargetVO> selectByTargetType(
        Page<UserInteractionTargetVO> page,
        Long currentUserId,
        Integer actionType,
        Integer targetType,
        Long folderId
    ) {
        switch(targetType) {
            case FieldConstant.TARGET_TYPE_ARTICLE:
                return userInteractionMapper.selectArticleInteractionPage(page, currentUserId, actionType, folderId);
            case FieldConstant.TARGET_TYPE_BUILD:
                return userInteractionMapper.selectBuildInteractionPage(page, currentUserId, actionType, folderId);
            case FieldConstant.TARGET_TYPE_HARDWARE:
                return userInteractionMapper.selectHardwareInteractionPage(page, currentUserId, actionType, folderId);
            case FieldConstant.TARGET_TYPE_COMMENT:
                return userInteractionMapper.selectCommentInteractionPage(page, currentUserId, actionType, folderId);
            default:
                return userInteractionMapper.selectMixedInteractionPage(page, currentUserId, actionType, folderId);
        }
    }

    /**
     * 校验收藏夹ID是否有效
     * 
     * @param actionType 操作类型
     * @param folderId   收藏夹ID
     * @return 校验后的收藏夹ID
     */
    private Long normalizeFolderId(Integer actionType, Long folderId) {
        // 如果操作类型不是收藏夹操作，直接返回null
        if (!Integer.valueOf(FieldConstant.ACTION_TYPE_FAVORITE).equals(actionType)) {
            return null;
        }

        // 如果收藏夹ID为空或默认收藏夹ID，直接返回null
        if (folderId == null || FieldConstant.DEFAULT_FAVORITE_FOLDER_ID == folderId) {
            return null;
        }

        // 如果收藏夹ID有效，直接返回
        return folderId;
    }

    /**
     * 校验目标类型是否有效
     * 
     * @param targetType 目标类型
     */
    private void validateTargetType(Integer targetType) {
        if (targetType == null)
            return;

        // 校验目标类型是否有效
        if (!List.of(
                FieldConstant.TARGET_TYPE_ARTICLE,
                FieldConstant.TARGET_TYPE_BUILD,
                FieldConstant.TARGET_TYPE_HARDWARE,
                FieldConstant.TARGET_TYPE_COMMENT).contains(targetType)) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, MessageConstant.TARGET_TYPE_INVALID);
        }
    }

    /**
     * 校验收藏夹ID是否有效
     * 
     * @param folderId 收藏夹ID
     */
    private void validateFolderId(Long folderId) {
        // 校验收藏夹ID是否有效
        if (folderId != null && folderId < FieldConstant.DEFAULT_FAVORITE_FOLDER_ID) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, MessageConstant.FAVORITE_FOLDER_ID_INVALID);
        }
    }

    /**
     * 填充用户交互目标路径
     * 
     * @param vo 用户交互目标VO
     */
    private void fillTargetPath(UserInteractionTargetVO vo) {
        // 校验目标类型和ID是否为空
        if (vo == null || vo.getTargetType() == null || vo.getTargetId() == null) {
            return;
        }

        // 获取目标ID
        String targetId = vo.getTargetId();

        // 根据目标类型设置路径
        switch (vo.getTargetType()) {
            case FieldConstant.TARGET_TYPE_ARTICLE -> vo.setTargetPath("/articles/" + targetId);
            case FieldConstant.TARGET_TYPE_HARDWARE -> vo.setTargetPath("/hardware/" + targetId);
            case FieldConstant.TARGET_TYPE_BUILD -> vo.setTargetPath("/builds/" + targetId);
            case FieldConstant.TARGET_TYPE_COMMENT -> vo.setTargetPath("/comments/" + targetId);
            default -> vo.setTargetPath(null);
        }
    }
}
