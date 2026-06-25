package com.nep.content.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.result.PageResult;
import com.nep.common.util.PageQueryUtils;
import com.nep.content.dto.ProfileArticleQueryRequest;
import com.nep.content.entity.Article;
import com.nep.content.mapper.ArticleMapper;
import com.nep.content.service.ArticleProfileService;
import com.nep.content.vo.ProfileArticleVO;

import lombok.RequiredArgsConstructor;

/**
 * 个人中心文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleProfileServiceImpl implements ArticleProfileService {
    private final ArticleMapper articleMapper;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 查询个人中心文章列表
     * @param currentUserId 当前用户ID
     * @param request 查询参数
     * @return 文章列表分页结果
     */
    @Override
    public PageResult<ProfileArticleVO> listMyArticles(Long currentUserId, ProfileArticleQueryRequest request) {
        if(currentUserId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        // 判断 request 是否为空, 如果为空则创建一个默认的查询参数
        request = request == null ? new ProfileArticleQueryRequest() : request;
        // 校验文章状态是否合法
        validateStatus(request.getStatus());

        // 校验并转换分页参数为默认值
        int pageNum = PageQueryUtils.normalizePageNum(request.getPageNum());
        int pageSize = PageQueryUtils.normalizePageSize(request.getPageSize(), MAX_PAGE_SIZE);

        Page<Article> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .select(
                    Article::getId,
                    Article::getCategoryId,
                    Article::getTitle,
                    Article::getStatus,
                    Article::getViewCount,
                    Article::getLikeCount,
                    Article::getFavoriteCount,
                    Article::getCommentCount,
                    Article::getCreateTime,
                    Article::getUpdateTime
                )
                .eq(Article::getUserId, currentUserId)
                .eq(Article::getIsDeleted, FieldConstant.NOT_DELETED)
                // 参数解析: 如果 status 不为空, 则根据状态查询
                .eq(request.getStatus() != null, Article::getStatus, request.getStatus())
                .orderByDesc(Article::getCreateTime);

        // 执行查询
        Page<Article> resultPage = articleMapper.selectPage(page, wrapper);

        // 转换为个人中心文章VO列表
        List<ProfileArticleVO> records = resultPage.getRecords()
                    .stream()
                    .map(this::toProfileArticleVO)
                    .toList();

        return PageResult.of(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    /**
     * 验证文章状态是否合法
     * 
     * @param status 文章状态
     */
    private void validateStatus(Integer status) {
        // 空值校验
        if (status == null)
            return;

        // 状态校验
        if (!List.of(
                FieldConstant.ARTICLE_STATUS_DRAFT,
                FieldConstant.ARTICLE_STATUS_OFFLINE,
                FieldConstant.ARTICLE_STATUS_PUBLISHED).contains(status)) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, MessageConstant.ARTICLE_STATUS_INVALID);
        }
    }

    /**
     * 将文章实体转换为个人中心文章VO
     * 
     * @param article 文章实体
     * @return 个人中心文章VO
     */
    private ProfileArticleVO toProfileArticleVO(Article article) {
        return ProfileArticleVO.builder()
                .id(String.valueOf(article.getId()))
                .categoryId(article.getCategoryId())
                .title(article.getTitle())
                .status(article.getStatus())
                .viewCount(article.getViewCount())
                .likeCount(article.getLikeCount())
                .favoriteCount(article.getFavoriteCount())
                .commentCount(article.getCommentCount())
                .createTime(article.getCreateTime())
                .updateTime(article.getUpdateTime())
                .build();
    }
}
