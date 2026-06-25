package com.nep.common.constants;

public final class QueryConstant {
    
    // 默认分页页码
    public static final int DEFAULT_PAGE_NUM = 1;
    // 分页页码最小值
    public static final int PAGE_NUM_MIN = 1;

    // 默认分页每页数量
    public static final int DEFAULT_PAGE_SIZE = 10;
    // 分页每页数量最小值
    public static final int PAGE_SIZE_MIN = 1;
    // 分页每页数量最大值
    public static final int PAGE_SIZE_MAX = 100;

    // 排序顺序: 升序
    public static final String SORT_ORDER_ASC = "asc";
    // 排序顺序: 降序
    public static final String SORT_ORDER_DESC = "desc";

    private QueryConstant() {
    }
}
