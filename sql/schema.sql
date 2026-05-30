create database if not exists nep_forge
default character set utf8mb4
collate utf8mb4_unicode_ci;

use nep_forge;

-- ---------------------
-- 用户模块
-- ---------------------

-- 用户表
-- 说明：核心用户信息，密码加密存储
create table if not exists `users`(
    `id` bigint unsigned primary key comment '用户id(雪花算法生成)',
    `username` varchar(50) not null comment '用户名',
    `password_hash` varchar(255) not null comment '加密后的密码',
    `email` varchar(100) unique not null comment '邮箱',
    `avatar` varchar(512) default null comment '头像URL',

    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除',

    -- 索引设计
    unique key `uk_username` (`username`),
    unique key `uk_email` (`email`),
    key `idx_is_deleted_create_time` (`is_deleted`, `create_time`)
) engine=InnoDB default charset=utf8mb4 comment='用户表';


-- 角色表
-- 说明：预置三种角色，通过 user_role 关联到用户
--       ROLE_USER / ROLE_MODERATOR / ROLE_ADMIN
create table if not exists `roles`(
    `id` int unsigned not null auto_increment comment '角色id',
    `role_name` varchar(50) not null comment '角色名称',
    `role_code` varchar(50) not null comment '角色编码(如: ADMIN)',
    `description` varchar(255) default null comment '角色描述',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    unique key `uk_role_code`(`role_code`)
) engine=InnoDB default charset=utf8mb4 comment='角色表';

-- 预置角色数据
insert into `roles`(`role_name`, `role_code`, `description`, `create_time`, `update_time`) values
('普通用户', 'ROLE_USER', '注册后默认角色，可发帖、评论、点赞', now(), now()),
('版主', 'ROLE_MODERATOR', '可管理评论, 删除违规内容', now(), now()),
('超级管理员', 'ROLE_ADMIN', '拥有所有权限', now(), now());


-- 用户角色关联表
-- 说明：一个用户可以拥有多个角色（如同时是版主和普通用户）
create table if not exists `role_users`(
    `id` bigint unsigned not null auto_increment comment '主键id',
    `user_id` bigint unsigned not null comment '用户id',
    `role_id` int unsigned not null comment '角色id',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    unique key `uk_user_role`(`user_id`, `role_id`), -- 联合索引, 确保同一用户不能重复分配同一角色
    key `idx_user_id`(`user_id`) -- 索引, 加速查询用户的角色
) engine=InnoDB default charset=utf8mb4 comment='用户角色关联表';


-- 收藏夹表
-- 说明：必须在 user_interaction 之前建，因为 user_interaction.folder_id 引用此表
--       用户可以创建多个收藏夹，并设置公开/私密
create table if not exists `favorites`(
    `id` bigint unsigned not null comment '收藏夹id(雪花算法生成)',
    `user_id` bigint unsigned not null comment '创建该收藏夹的用户id',
    `name` varchar(50) not null comment '收藏夹名称',
    `description` varchar(255) default null comment '收藏夹描述',
    `is_public` tinyint(1) not null default 0 comment '是否公开(0:私密, 1:公开)',
    `create_time` datetime not null comment '收藏夹创建时间',
    `update_time` datetime not null comment '收藏夹更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_user_id`(`user_id`), -- 索引, 加速查询用户的收藏夹
    unique key `uk_user_folder_name`(`user_id`, `name`, `is_deleted`)
) engine=InnoDB default charset=utf8mb4 comment='收藏夹表';


-- 用户交互表（点赞 & 收藏）
-- 说明：一张表统一管理点赞和收藏两种行为
--       复合唯一索引从数据库层面防止重复操作，无惧脚本刷接口
create table if not exists `user_interaction` (
    `id` bigint unsigned not null comment '交互id(雪花算法生成)',
    `user_id` bigint unsigned not null comment '用户id',
    `target_id` bigint unsigned not null comment '目标id(文章/配件/装机单)',
    `target_type` tinyint unsigned not null comment '目标类型(1:文章, 2:配件, 3:装机单, 4:评论)',
    `action_type` tinyint unsigned not null comment '行为类型(1:点赞, 2:收藏)',
    `folder_id` bigint unsigned not null default 0 comment '收藏夹id(用户行为为收藏时填写)',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    unique key `uk_interaction`(`user_id`, `target_id`, `target_type`, `action_type`), -- 复合唯一索引, 确保用户对同一目标只能进行一种行为
    key `idx_target` (`target_id`, `target_type`, `action_type`), -- 查询"某篇文章的点赞数"时走此索引
    key `idx_user_id` (`user_id`)
) engine=InnoDB default charset=utf8mb4 comment='用户行为表';


-- --------------------------
-- 配件库模块
-- --------------------------

-- 分类表
-- 说明：通过 parent_id 实现无限级分类树
--       根分类的 parent_id = 0
--       例：电脑配件(0) → CPU(1) → Intel(2)
create table if not exists `hw_category`(
    `id` int unsigned not null auto_increment comment '分类id',
    `name` varchar(50) not null comment '分类名称',
    `parent_id` int unsigned not null default 0 comment '父级分类id(自关联)',
    `status` tinyint(1) not null default 1 comment '分类状态(0:禁用, 1:启用)',
    `sort_order` int unsigned not null default 0 comment '排序值(数值越小越靠前)',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_parent_id`(`parent_id`) -- 索引, 加速查询子分类
) engine=InnoDB default charset=utf8mb4 comment='配件分类表';

-- 预置顶级分类数据
insert into `hw_category` (`name`, `parent_id`, `status`, `sort_order`, `create_time`, `update_time`)
values ('CPU 处理器',   0, 1, 1, now(), now()),
       ('主板',         0, 1, 2, now(), now()),
       ('内存',         0, 1, 3, now(), now()),
       ('硬盘',         0, 1, 4, now(), now()),
       ('显卡',         0, 1, 5, now(), now()),
       ('电源',         0, 1, 6, now(), now()),
       ('机箱',         0, 1, 7, now(), now()),
       ('散热器',       0, 1, 8, now(), now()),
       ('显示器',       0, 1, 9, now(), now()),
       ('键盘',         0, 1, 10, now(), now()),
       ('鼠标',         0, 1, 11, now(), now()),
       ('其他配件',     0, 1, 12, now(), now());


-- 配件表
-- 说明：specs_json 用 JSON 类型存储各类配件的差异化参数
--       MySQL 8.0 原生支持 JSON 类型，可按 JSON 路径查询
--       例：WHERE specs_json->>'$.cores' > 8  （查8核以上CPU）
create table if not exists `hardware` (
    `id` bigint unsigned not null comment '配件id(雪花算法生成)',
    `category_id` int unsigned not null comment '所属分类id',
    `name` varchar(100) not null comment '产品名称/型号',
    `brand` varchar(50) default null comment '品牌',
    `price` decimal(10,2) not null default 0.00 comment '价格',
    `source_name` varchar(100) default null comment '数据来源名称',
    `source_url` varchar(512) default null comment '数据来源链接',
    `release_date` date default null comment '发布时间',
    `last_sync_time` datetime default null comment '最近同步时间',
    `cover_image` varchar(512) default null comment '封面图片URL',
    `specs_json` json default null comment '配件差异化参数(JSON格式)',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_category_id`(`category_id`), -- 索引, 加速查询分类下的配件
    key `idx_brand`(`brand`), -- 索引, 加速查询品牌下的配件
    key `idx_category_deleted_price`(`category_id`, `is_deleted`, `price`), -- 联合索引, 加速分类下的配件列表查询(过滤已删除和排序)
    key `idx_deleted_create_time`(`is_deleted`, `create_time`) -- 联合索引, 加速查询未删除的装机单列表(过滤已删除和排序)
)engine=InnoDB default charset=utf8mb4 comment='配件表';


-- --------------------------
-- 装机单模块
-- --------------------------

-- 装机单主表
-- 说明：一个用户可以创建多份装机单
--       总价和总功耗由后端计算后写入，方便前端直接展示
create table if not exists `user_builds`(
    `id` bigint unsigned not null comment '装机单id(雪花算法生成)',
    `user_id` bigint unsigned not null comment '创建该装机单的用户id',
    `title` varchar(100) not null comment '装机单标题',
    `total_price` decimal(10, 2) not null default 0.00 comment '装机单总价',
    `total_power` decimal(10, 2) not null default 0.00 comment '装机单总功耗',
    `description` varchar(255) default null comment '装机单描述',
    `is_public` tinyint(1) not null default 0 comment '是否公开(0:私密, 1:公开)',
    `status` tinyint not null default 0 comment '状态(0:草稿, 1:正常, 2:下架)',
    `cover_image` varchar(512) default null comment '装机单封面图',
    `create_time` datetime not null comment '装机单创建时间',
    `update_time` datetime not null comment '装机单更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_user_id_deleted`(`user_id`, `is_deleted`), -- 索引, 加速查询用户的装机单
    key `idx_public_status_create_time`(`is_public`, `status`, `is_deleted`, `create_time`)
) engine=InnoDB default charset=utf8mb4 comment='装机单主表';

-- 装机单详情表
-- 说明：记录装机单内每个配件及其数量
--       同一装机单内同一配件只能出现一次（靠 unique key 保证）
--       若需要多块同款显卡，调整 quantity 字段即可
create table if not exists `user_build_details`(
    `id` bigint unsigned not null comment '装机单详情id(雪花算法生成)',
    `build_id` bigint unsigned not null comment '装机单id',
    `hardware_id` bigint unsigned not null comment '配件id',
    `quantity` int unsigned not null default 1 comment '配件数量',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    unique key `uk_build_hardware_deleted`(`build_id`, `hardware_id`, `is_deleted`),
    key `idx_build_id`(`build_id`) -- 索引, 加速查询装机单下的配件
)engine=InnoDB default charset=utf8mb4 comment='装机单详情表';


-- --------------------------
-- 用户论坛模块
-- --------------------------


-- 文章表
-- 说明：content 使用 longtext 存储 Markdown 原文
--       计数字段（like_count 等）作为冗余字段存储，
--       避免每次都 count(*) 关联表，提升列表页查询性能
--       后续引入 Redis 后，计数更新改为异步写回
create table if not exists `article` (
    `id` bigint unsigned not null comment '文章id(雪花算法生成)',
    `category_id` int unsigned default null comment '文章分类id',
    `user_id` bigint unsigned not null comment '作者id',
    `title` varchar(255) not null comment '标题',
    `content` longtext not null comment '文章内容(Markdown格式)',
    `status` tinyint(1) not null default 0 comment '文章状态(0:草稿, 1:已发布, 2:已下架)',

    `view_count` int unsigned not null default 0 comment '浏览量',
    `like_count` int unsigned not null default 0 comment '点赞量',
    `favorite_count` int unsigned not null default 0 comment '收藏量',
    `comment_count` int unsigned not null default 0 comment '评论量',

    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_user_id`(`user_id`), -- 索引, 加速查询作者的文章
    key `idx_deleted_create_time`(`is_deleted`, `create_time`), -- 联合索引, 加速查询未删除的文章列表(过滤已删除和排序)
    key `idx_status_deleted_create_time`(`status`, `is_deleted`, `create_time`)
) engine=InnoDB default charset=utf8mb4 comment='文章表';

-- 文章分类表
-- 说明：文章分类表，用于文章的分类
--       分类名称不允许重复
create table if not exists `article_category`(
    `id` int unsigned not null auto_increment comment '文章分类id',
    `name` varchar(50) not null comment '分类名称',
    `sort_order` int unsigned not null default 0 comment '排序值',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除',
    primary key(`id`),
    unique key `uk_name`(`name`)
) engine=InnoDB default charset=utf8mb4 comment='文章分类表';

-- 文章标签表
-- 说明：文章标签表，用于文章的标签
--       标签名称不允许重复
create table if not exists `article_tag`(
    `id` int unsigned not null auto_increment comment '标签id',
    `name` varchar(50) not null comment '标签名称',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除',
    primary key(`id`),
    unique key `uk_name`(`name`)
) engine=InnoDB default charset=utf8mb4 comment='文章标签表';

-- 文章标签关联表
-- 说明：文章标签关联表，用于文章和标签的关联
--       一个文章可以有多个标签，一个标签也可以被多个文章所使用
create table if not exists `article_tag_relation`(
    `id` bigint unsigned not null comment '主键id',
    `article_id` bigint unsigned not null comment '文章id',
    `tag_id` int unsigned not null comment '标签id',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除',
    primary key(`id`),
    unique key `uk_article_tag`(`article_id`, `tag_id`),
    key `idx_article_id`(`article_id`),
    key `idx_tag_id`(`tag_id`)
) engine=InnoDB default charset=utf8mb4 comment='文章标签关联表';


-- 评论表
-- 说明：parent_id = 0 表示直接评论文章
--        parent_id = N 表示回复 ID 为 N 的那条评论
--       一张表搞定楼中楼，后端查询时先取 parent_id=0 的主楼，
--       再按 parent_id 聚合子评论即可
create table if not exists `comment` (
    `id` bigint unsigned not null comment '评论id(雪花算法生成)',
    `article_id` bigint unsigned not null comment '文章id',
    `user_id` bigint unsigned not null comment '用户id',
    `content` text not null comment '评论内容',
    `parent_id` bigint unsigned not null default 0 comment '父级评论id(0表示直接评论文章)',
    `like_count` int unsigned not null default 0 comment '点赞量',
    `reply_to_user_id` bigint unsigned default null comment '回复的评论用户id',
    `status` tinyint not null default 1 comment '评论状态(0:禁用, 1:正常)',
    `create_time` datetime not null comment '创建时间',
    `update_time` datetime not null comment '更新时间',
    `is_deleted` tinyint(1) not null default 0 comment '是否删除(0:未删除, 1:已删除)',

    primary key(`id`),
    key `idx_article_parent_deleted`(`article_id`, `parent_id`, `is_deleted`), -- 联合索引, 加速查询文章下的评论列表(过滤已删除和排序)
    key `idx_user_id`(`user_id`) -- 索引, 加速查询用户下的评论
)engine=InnoDB default charset=utf8mb4 comment='评论表';