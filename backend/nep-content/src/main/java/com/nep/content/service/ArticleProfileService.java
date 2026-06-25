package com.nep.content.service;

import com.nep.common.result.PageResult;
import com.nep.content.dto.ProfileArticleQueryRequest;
import com.nep.content.vo.ProfileArticleVO;

/**
 * 个人中心文章服务
 */
public interface ArticleProfileService {
    
    /**
     * 查询个人中心文章列表
     * @param currentUserId 当前用户ID
     * @param rquest 查询参数
     * @return 文章列表分页结果
     */
    PageResult<ProfileArticleVO> listMyArticles(Long currentUserId, ProfileArticleQueryRequest rquest);
}
