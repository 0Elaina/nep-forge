package com.nep.common.util;

import com.nep.common.constants.QueryConstant;

public class PageQueryUtils {

    private PageQueryUtils() {
    }

    /**
     * 校验并归一化分页页码
     * 
     * @param pageNum 分页页码
     * @return 归一化后的分页页码
     */
    public static int normalizePageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            return QueryConstant.DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    /**
     * 校验并归一化分页每页数量
     * 
     * @param pageSize 分页每页数量
     * @return 归一化后的分页每页数量
     */
    public static int normalizePageSize(Integer pageSize, int maxPageSize) {
        if (pageSize == null || pageSize < 1) {
            return QueryConstant.DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, maxPageSize);
    }
}
