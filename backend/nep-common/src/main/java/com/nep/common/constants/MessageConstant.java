package com.nep.common.constants;

/**
 * 消息提示常量类
 * <p>
 * 集中管理项目中所有业务提示消息、错误提示信息及日志消息，
 * 便于统一维护和修改，避免字符串硬编码散落在各处。
 *
 * @author Neptune
 * @date 2026-06-06
 */
public final class MessageConstant {
    private MessageConstant() {
    }

    /* ===================== 通用 ===================== */

    public static final String SUCCESS = "成功";
    public static final String REQUEST_PARAM_ERROR = "请求参数错误";
    public static final String UNAUTHORIZED = "未登录或登录已过期";
    public static final String FORBIDDEN = "无权限访问";
    public static final String NOT_FOUND = "资源不存在";
    public static final String CONFLICT = "数据冲突";
    public static final String TOO_MANY_REQUESTS = "请求过于频繁";
    public static final String SYSTEM_ERROR = "系统内部错误";
    public static final String NO_REQUEST_RESOURCE = "请求资源不存在";
    public static final String PARAM_JSON_ERROR = "参数 JSON 格式错误";

    /* ===================== 用户模块 ===================== */

    public static final String USERNAME_OR_PASSWORD_ERROR = "用户名或密码错误";
    public static final String USER_DISABLED = "用户已被删除或不可用";
    public static final String USERNAME_EXISTS = "用户名已存在";
    public static final String EMAIL_EXISTS = "邮箱已存在";
    public static final String USERNAME_NOT_BLANK = "用户名不能为空";
    public static final String USERNAME_LENGTH_LIMIT = "用户名长度必须在 3-50 个字符之间";
    public static final String EMAIL_NOT_BLANK = "邮箱不能为空";
    public static final String EMAIL_INVALID = "邮箱格式不正确";
    public static final String EMAIL_LENGTH_LIMIT = "邮箱长度不能超过 100 个字符";
    public static final String PASSWORD_NOT_BLANK = "密码不能为空";
    public static final String PASSWORD_LENGTH_LIMIT = "密码长度必须在 8-32 个字符之间";

    public static final String ACCOUNT_NOT_BLANK = "账号不能为空";
    public static final String ACCOUNT_LENGTH_LIMIT = "账号长度不能超过 100 个字符";
    public static final String AVATAR_URL_INVALID = "头像地址格式不正确";
    public static final String AVATAR_LENGTH_LIMIT = "头像地址长度不能超过 512 个字符";
    public static final String NICKNAME_LENGTH_LIMIT = "昵称长度不能超过 50 个字符";
    public static final String BIO_LENGTH_LIMIT = "简介长度不能超过 255 个字符";
    public static final String USER_PROFILE_UPDATE_EMPTY = "至少需要修改一项用户资料";
    public static final String USER_PROFILE_UPDATE_FAILED = "更新用户资料失败";

    /* ===================== 装机单模块 ===================== */

    public static final String BUILD_NOT_FOUND = "装机单不存在";
    public static final String BUILD_FORBIDDEN = "无权访问该装机单";
    public static final String BUILD_HARDWARE_EXISTS = "装机单中已存在该配件";
    public static final String BUILD_STATUS_INVALID = "装机单状态不合法";
    public static final String BUILD_TITLE_NOT_BLANK = "装机单标题不能为空";
    public static final String BUILD_TITLE_LENGTH_MAX_LIMIT = "装机单标题长度不能超过 100 个字符";
    public static final String BUILD_DESCRIPTION_LENGTH_MAX_LIMIT = "装机单描述长度不能超过 255 个字符";
    public static final String BUILD_COVER_IMAGE_LENGTH_MAX_LIMIT = "装机单封面图片长度不能超过 512 个字符";
    public static final String BUILD_CREATE_FAILED = "创建装机单失败";
    public static final String BUILD_UPDATE_FAILED = "更新装机单失败";
    public static final String BUILD_HARDWARE_QUANTITY_NOT_NULL = "配件数量不能为空";
    public static final String BUILD_HARDWARE_QUANTITY_MIN_LIMIT = "配件数量不能小于 1";
    public static final String BUILD_TOTAL_DATA_UPDATE_FAILED = "更新装机单总价格和总功率失败";
    public static final String BUILD_HARDWARE_ADD_FAILED = "添加装机单配件失败";
    public static final String BUILD_HARDWARE_REMOVE_FAILED = "删除装机单配件失败";
    public static final String BUILD_HARDWARE_NOT_FOUND = "装机单配件不存在";
    public static final String BUILD_VISIBILITY_NOT_NULL = "装机单可见性不能为空";

    /* ===================== 配件模块 ===================== */

    public static final String HARDWARE_NOT_FOUND = "配件不存在";
    public static final String HARDWARE_CATEGORY_NOT_FOUND = "配件分类不存在";
    public static final String HARDWARE_MIN_PRICE_MIN_LIMIT = "配件最低价格不能低于 0";
    public static final String HARDWARE_MAX_PRICE_MIN_LIMIT = "配件最高价格不能低于 0";
    public static final String HARDWARE_COMPARE_IDS_NOT_EMPTY = "请选择需要对比的配件";
    public static final String HARDWARE_COMPARE_ID_NOT_NULL = "配件ID不能为空";
    public static final String HARDWARE_COMPARE_SIZE_LIMIT = "对比配件数量必须在 2-5 个之间";
    public static final String HARDWARE_COMPARE_CATEGORY_NOT_SAME = "当前仅支持同类配件对比";
    public static final String HARDWARE_CATEGORY_NOT_NULL = "配件分类不能为空";
    public static final String HARDWARE_NAME_NOT_BLANK = "配件名称不能为空";
    public static final String HARDWARE_NAME_LENGTH_MAX_LIMIT = "配件名称长度不能超过 100 个字符";
    public static final String HARDWARE_BRAND_NAME_LENGTH_MAX_LIMIT = "品牌名称长度不能超过 50 个字符";
    public static final String HARDWARE_PRICE_NOT_NULL = "配件价格不能为空";
    public static final String HARDWARE_PRICE_MIN_LIMIT = "配件价格不能低于 0";
    public static final String HARDWARE_SOURCE_NAME_LENGTH_MAX_LIMIT = "来源名称长度不能超过 100 个字符";
    public static final String HARDWARE_SOURCE_URL_LENGTH_MAX_LIMIT = "来源URL长度不能超过 512 个字符";
    public static final String HARDWARE_COVER_IMAGE_URL_LENGTH_MAX_LIMIT = "封面图片URL长度不能超过 512 个字符";
    public static final String HARDWARE_ID_NOT_NULL = "配件ID不能为空";


    /* ===================== 文章模块 ===================== */

    public static final String ARTICLE_NOT_FOUND = "文章不存在";
    public static final String ARTICLE_NOT_PUBLISHED = "文章未发布或已下架";
    public static final String TAG_NOT_FOUND = "标签不存在";
    public static final String ARTICLE_STATUS_INVALID = "文章状态不合法";

    /* ===================== 交互模块（点赞/收藏） ===================== */

    public static final String FAVORITE_FOLDER_NOT_FOUND = "收藏夹不存在";
    public static final String FAVORITE_FOLDER_ID_INVALID = "收藏夹ID无效";
    public static final String ALREADY_LIKED = "已点赞，请勿重复操作";
    public static final String ALREADY_FAVORITED = "已收藏，请勿重复操作";
    public static final String TARGET_TYPE_INVALID = "目标类型不合法";

    /* ===================== 评论模块 ===================== */

    public static final String COMMENT_NOT_FOUND = "评论不存在";
    public static final String COMMENT_DISABLED = "评论已被禁用";

    /* ===================== API 响应 ===================== */

    public static final String API_SUCCESS = "success";

    /* ===================== 分页参数 ===================== */
    public static final String PAGE_NUM_MIN_LIMIT = "页码不能小于 1";
    public static final String PAGE_SIZE_MIN_LIMIT = "每页条数不能小于 1";
    public static final String PAGE_SIZE_MAX_LIMIT = "每页条数不能超过 100";

    /* ===================== 参数校验 & 异常处理 ===================== */

    public static final String MISSING_REQUEST_PARAM = "缺少请求参数";
    public static final String REQUEST_BODY_INVALID = "请求体格式错误，请检查 JSON 格式";
    public static final String METHOD_NOT_SUPPORTED = "请求方法不支持";
    public static final String DATA_CONSTRAINT_VIOLATION = "数据已存在或违反唯一约束";
    public static final String UNKNOWN_EXCEPTION_LOG = "系统发生未知异常";

    /* ===================== 文档配置 ===================== */

    public static final String API_DOC_TITLE = "NepForge 接口文档";
    public static final String API_DOC_VERSION = "v0.0.1";
    public static final String API_DOC_DESCRIPTION = "NepForge 项目测试接口文档";
    public static final String API_DOC_CONTACT_NAME = "Neptune";

}
