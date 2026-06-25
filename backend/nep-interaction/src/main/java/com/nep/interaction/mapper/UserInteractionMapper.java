package com.nep.interaction.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.interaction.entity.UserInteraction;
import com.nep.interaction.vo.UserInteractionTargetVO;

@Mapper
public interface UserInteractionMapper extends BaseMapper<UserInteraction> {
    /**
     * 混合交互分页查询用户交互记录
     * 
     * @param page       分页参数
     * @param userId     用户ID
     * @param actionType 行为类型 1: 点赞, 2: 收藏
     * @param folderId   收藏夹ID, 点赞时默认为0
     * @return 分页结果
     */
    IPage<UserInteractionTargetVO> selectMixedInteractionPage(
            Page<UserInteractionTargetVO> page,
            @Param("userId") Long userId,
            @Param("actionType") Integer actionType,
            @Param("folderId") Long folderId);

    /**
     * 文章交互分页查询用户交互记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param folderId � 收藏夹ID, 点赞时默认为0
     * @return 分页结果
     */
    IPage<UserInteractionTargetVO> selectArticleInteractionPage(
            Page<UserInteractionTargetVO> page,
            @Param("userId") Long userId,
            @Param("actionType") Integer actionType,
            @Param("folderId") Long folderId);

    /**
     * 配件交互分页查询用户交互记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param folderId � 收藏夹ID, 点赞时默认为0
     * @return 分页结果
     */
    IPage<UserInteractionTargetVO> selectHardwareInteractionPage(
            Page<UserInteractionTargetVO> page,
            @Param("userId") Long userId,
            @Param("actionType") Integer actionType,
            @Param("folderId") Long folderId);

    /**
     * 装机单交互分页查询用户交互记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param folderId � 收藏夹ID, 点赞时默认为0
     * @return 分页结果
     */
    IPage<UserInteractionTargetVO> selectBuildInteractionPage(
            Page<UserInteractionTargetVO> page,
            @Param("userId") Long userId,
            @Param("actionType") Integer actionType,
            @Param("folderId") Long folderId);

    /**
     * 评论交互分页查询用户交互记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param folderId � 收藏夹ID, 点赞时默认为0
     * @return 分页结果
     */
    IPage<UserInteractionTargetVO> selectCommentInteractionPage(
            Page<UserInteractionTargetVO> page,
            @Param("userId") Long userId,
            @Param("actionType") Integer actionType,
            @Param("folderId") Long folderId);
}