package com.nep.common.result;

import java.util.List;
import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页结果实体
 * @param <T> 分页数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 分页数据
    private List<T> records;

    // 数据总条数
    private Long total;

    // 总页数
    private Long pages;

    // 当前页码
    private Long pageNum;

    // 每页条数
    private Long pageSize;

    // 是否还有下一页
    private Boolean hasNext;

    // 是否还有上一页
    private Boolean hasPrev;

    /**
     * 创建分页结果
     * @param records 分页数据
     * @param total 数据总条数
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long pageNum, Long pageSize) {
        Long safeTotal = total == null ? 0L : total;
        Long safePageNum = pageNum == null || pageNum < 1 ? 1L : pageNum;
        Long safePageSize = pageSize == null || pageSize < 1 ? 10L : pageSize;
        /**
         * 计算总页数。
         *
         * 算法：(total + pageSize - 1) / pageSize
         *
         * 这是经典的"向上取整"整数除法技巧，避免使用浮点数：
         *   当 total % pageSize == 0 时，(total + pageSize - 1) / pageSize = total / pageSize
         *   当 total % pageSize != 0 时，则多算一页容纳剩余不足一页的数据
         */
        Long pages = safeTotal == 0 ? 0L : (safeTotal + safePageSize - 1) / safePageSize;

        return new PageResult<>(
            records,
            safeTotal,
            pages,
            safePageNum,
            safePageSize,
            pages > 0 && safePageNum < pages,
            pages > 0 && safePageNum > 1
        );
    }
}
