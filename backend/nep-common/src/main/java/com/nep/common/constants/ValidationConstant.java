package com.nep.common.constants;

/**
 * 校验规则常量类
 * 
 * 集中管理所有字段的校验边界值，如 min / max / 正则等。
 * 与 MessageConstant 分离，职责单一。
 * 
 * @author Neptune
 * @date 2026-06-07
 */
public final class ValidationConstant {
    private ValidationConstant(){}

    /*===================== 用户模块 ====================== */
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 32;
    public static final int ACCOUNT_MAX_LENGTH = 100;
    public static final int AVATAR_MAX_LENGTH = 512;
    public static final int NICKNAME_MAX_LENGTH = 50;
    public static final int BIO_MAX_LENGTH = 255;

    /*===================== 配件模块 ====================== */
    public static final int HARDWARE_PAGE_NUM_MIN = 1;
    public static final int HARDWARE_PAGE_SIZE_MIN = 1;
    public static final int HARDWARE_PAGE_SIZE_MAX = 100;
    public static final String HARDWARE_MIN_PRICE_MIN = "0.00";
    public static final String HARDWARE_MAX_PRICE_MIN = "0.00";
    public static final int HARDWARE_COMPARE_SIZE_MIN = 2;
    public static final int HARDWARE_COMPARE_SIZE_MAX = 5;
    public static final int HARDWARE_NAME_MAX_LENGTH = 100;
    public static final int HARDWARE_BRAND_NAME_MAX_LENGTH = 50;
    public static final String HARDWARE_PRICE_MIN = "0.00";
    public static final int HARDWARE_SOURCE_NAME_MAX_LENGTH = 100;
    public static final int HARDWARE_SOURCE_URL_MAX_LENGTH = 512;
    public static final int HARDWARE_COVER_IMAGE_URL_MAX_LENGTH = 512;
}