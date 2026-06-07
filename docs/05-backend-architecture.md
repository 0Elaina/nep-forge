# NepForge 后端架构设计

> 文档状态：基于当前代码与模块结构固化  
> 适用版本：V0.2（用户认证阶段）  
> 技术栈：Spring Boot 3.5 + MyBatis-Plus 3.5 + MySQL 8.0 + Redis 7.x  
> 生成日期：2026-06-07

---

## 1. 架构总览

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           前端 Vue 3 SPA                                │
│                  (Axios → JWT Token → JSON API)                        │
└──────────────────────────┬──────────────────────────────────────────────┘
                           │ HTTP / HTTPS
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                          nep-server (Boot 入口)                          │
│  ┌───────────┬───────────┬───────────┬───────────┬───────────────────┐  │
│  │ Knife4j   │ 全局异常   │ MyBatis-  │ Spring    │ 统一响应           │  │
│  │ OpenAPI   │ 处理器     │ Plus 配置  │ Security  │ ApiResponse<T>    │  │
│  └───────────┴───────────┴───────────┴───────────┴───────────────────┘  │
└──────────────────────────────────┬───────────────────────────────────────┘
                                   │ 依赖聚合
                                   ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        业务模块层 (Business Modules)                      │
│                                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ nep-     │  │ nep-     │  │ nep-     │  │ nep-     │  │ nep-     │  │
│  │ system   │  │ hardware │  │ build    │  │ content  │  │interaction│  │
│  │ (用户认证)│  │ (配件库)  │  │ (装机单)  │  │ (文章评论)│  │ (点赞收藏)│  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
│       │             │             │             │             │         │
│  ┌────┴─────┐  ┌────┴─────┐  ┌────┴─────┐  ┌────┴─────┐  ┌────┴─────┐  │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│  │ Controller│  │
│  │ Service   │  │ Service   │  │ Service   │  │ Service   │  │ Service   │  │
│  │ Mapper    │  │ Mapper    │  │ Mapper    │  │ Mapper    │  │ Mapper    │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└──────────────────────────────────┬───────────────────────────────────────┘
                                   │ 统一依赖
                                   ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         基础设施层 (Infrastructure)                       │
│                                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                   │
│  │  nep-common  │  │ nep-security │  │ nep-framework │                   │
│  │  (公共组件)   │  │ (认证授权)    │  │ (框架配置)     │                   │
│  └──────────────┘  └──────────────┘  └──────────────┘                   │
└──────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                            数据存储层                                     │
│                                                                          │
│        MySQL 8.0 (InnoDB)                  Redis 7.x (缓存)              │
│   ┌─────────────────────────┐    ┌─────────────────────────┐            │
│   │ 用户/角色/权限           │    │ JWT Token 黑名单         │            │
│   │ 配件分类/配件            │    │ 热点配件数据缓存          │            │
│   │ 装机单/详情              │    │ 文章浏览量计数            │            │
│   │ 文章/标签/评论           │    │ 点赞状态缓存              │            │
│   │ 收藏夹/用户交互          │    │ 验证码存储                │            │
│   └─────────────────────────┘    └─────────────────────────┘            │
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.2 模块依赖关系

```
                    ┌─────────────────┐
                    │   nep-server    │  (聚合模块 / 启动入口)
                    └────────┬────────┘
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
  │ 业务模块     │  │ 基础设施模块  │  │ 管理模块      │
  │             │  │              │  │              │
  │ nep-system  │  │ nep-common   │  │ nep-admin    │
  │ nep-hardware│  │ nep-security │  │              │
  │ nep-build   │  │ nep-framework│  │              │
  │ nep-content │  │              │  │              │
  │ nep-interact │  │              │  │              │
  └──────┬───────┘  └──────────────┘  └──────────────┘
         │
         │ 所有业务模块依赖 nep-common
         ▼
  ┌──────────────┐
  │  nep-common  │  (响应结构 / 异常体系 / 常量定义)
  └──────────────┘
```

---

## 2. 模块划分详解

### 2.1 模块一览

| 模块 | 层级 | 说明 | 当前状态 |
|------|------|------|---------|
| `nep-server` | 应用层 | Spring Boot 启动入口，聚合所有模块依赖 | 已完成 |
| `nep-common` | 基础设施 | 统一响应、异常体系、错误码、常量定义 | 已完成 |
| `nep-framework` | 基础设施 | 框架级配置预留（如 Jackson、CORS、Async） | 空白模块 |
| `nep-security` | 基础设施 | Spring Security 配置 + JWT 认证授权 | 空白模块 |
| `nep-system` | 业务 | 用户注册/登录、角色权限、用户信息 | 部分实现（注册完成） |
| `nep-hardware` | 业务 | 配件分类、配件 CRUD、参数搜索筛选 | 空白模块 |
| `nep-build` | 业务 | 装机单创建编辑、配件关联、价格计算 | 空白模块 |
| `nep-content` | 业务 | 文章发布、分类标签、评论楼中楼 | 空白模块 |
| `nep-interaction` | 业务 | 点赞、收藏、收藏夹、统一交互模型 | 空白模块 |
| `nep-admin` | 管理 | 后台 Dashboard、用户管理、内容审核 | 空白模块 |

### 2.2 模块职责

#### nep-server（启动聚合模块）

- Spring Boot 启动入口
- 聚合所有模块的 Maven 依赖
- 存放全局配置（application.yml、Knife4j、MyBatis-Plus 分页）
- `@MapperScan("com.nep.**.mapper")` 全局扫描所有模块的 Mapper

#### nep-common（公共组件模块）

提供所有业务模块共享的基础能力，不依赖任何项目内其他模块：

| 组件 | 说明 |
|------|------|
| `ApiResponse<T>` | 统一响应体：`code` / `message` / `data` / `traceId` |
| `CommonException` | 通用业务异常，携带 `BaseErrorInfo` 错误信息 |
| `GlobalExceptionHandler` | `@RestControllerAdvice` 全局异常处理 |
| `BaseErrorInfo` | 错误信息接口：`getCode()` / `getHttpStatus()` / `getMessage()` |
| `CommonErrorCode` | 通用错误码枚举（参数错误/未授权/无权限/系统错误等） |
| `UserErrorCode` | 用户模块错误码 |
| `HardwareErrorCode` | 配件模块错误码 |
| `BuildErrorCode` | 装机单模块错误码 |
| `ArticleErrorCode` | 文章模块错误码 |
| `CommentErrorCode` | 评论模块错误码 |
| `InteractionErrorCode` | 交互模块错误码 |
| `MessageConstant` | 全局消息提示常量 |

#### 业务模块（nep-system / nep-hardware / nep-build / nep-content / nep-interaction）

各业务模块采用统一的三层架构：

```
┌─────────────────────────────────────┐
│  Controller 层 (REST 接口)          │
│  └─ @RestController + @RequestMapping │
│     └─ 接收请求 / 参数校验 / 调用 Service │
├─────────────────────────────────────┤
│  Service 层 (业务逻辑)               │
│  └─ @Service                        │
│     └─ 业务编排 / 事务管理 / 缓存操作   │
├─────────────────────────────────────┤
│  Mapper 层 (数据访问)                │
│  └─ extends BaseMapper<T>           │
│     └─ MyBatis-Plus 自动注入 CRUD     │
└─────────────────────────────────────┘
```

模块间禁止循环依赖，业务模块只可依赖 `nep-common`，如需跨模块调用通过 `nep-server` 层编排或引入事件机制。

#### nep-security（安全模块）

预留模块，计划包含：

| 组件 | 说明 |
|------|------|
| `JwtTokenProvider` | JWT Token 的生成、解析、校验 |
| `JwtAuthenticationFilter` | OncePerRequestFilter，从请求头提取 Token |
| `UserDetailsServiceImpl` | 加载用户认证信息 |
| `SecurityConfig` | Spring Security 核心配置（放行路径/角色拦截/密码编码器） |
| `CustomAccessDeniedHandler` | 403 统一响应处理 |
| `CustomAuthenticationEntryPoint` | 401 未认证处理 |

#### nep-framework（框架配置模块）

预留模块，计划包含全局框架配置：Jackson 日期序列化、CORS 跨域、Async 异步执行器、RestTemplate 等。

#### nep-admin（后台管理模块）

预留模块，后台管理相关接口，可与 `nep-security` 配合做细粒度权限控制。

---

## 3. 请求处理流程

### 3.1 完整请求链路

```
请求到达
    │
    ▼
┌──────────────────────────────────────────────────────────┐
│                   Filter Chain                           │
│  ┌────────────────────────────────────────────────────┐  │
│  │  JwtAuthenticationFilter（提取 Token 解析用户身份）   │  │
│  └────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────┐  │
│  │  UsernamePasswordAuthenticationFilter（表单登录）    │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                  DispatcherServlet                        │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                    Controller                             │
│  ┌────────────────────────────────────────────────────┐  │
│  │  参数校验 (@Valid / @Validated)                     │  │
│  │  调用 Service 层                                   │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                    Service 层                             │
│  ┌────────────────────────────────────────────────────┐  │
│  │  业务逻辑编排                                       │  │
│  │  事务管理 (@Transactional)                          │  │
│  │  缓存操作 (Redis)                                    │  │
│  │  调用 Mapper 层                                     │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                    Mapper 层                              │
│  ┌────────────────────────────────────────────────────┐  │
│  │  MyBatis-Plus BaseMapper CRUD                      │  │
│  │  自定义 XML Mapper（复杂查询）                       │  │
│  │  分页查询 (PaginationInnerInterceptor)               │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
                        MySQL / Redis
```

### 3.2 异常处理流程

```
Controller / Service / Mapper 抛出异常
            │
            ▼
┌─────────────────────────────────────┐
│    GlobalExceptionHandler           │
│    (@RestControllerAdvice)          │
│                                     │
│  CommonException        → 业务异常   │
│  MethodArgumentValid    → 参数校验   │
│  BindException          → 参数绑定   │
│  ConstraintViolation    → 约束校验   │
│  MissingServletRequest  → 缺少参数   │
│  HttpMessageNotReadable → 请求体错误 │
│  HttpRequestMethodNot  → 方法不支持  │
│  AccessDeniedException → 权限不足    │
│  DataIntegrityViolation → 数据冲突   │
│  Exception (兜底)       → 系统异常   │
└───────────┬─────────────────────────┘
            │
            ▼
┌─────────────────────────────────────┐
│  ApiResponse.error(code, msg, id)  │
│  → JSON 响应返回客户端               │
└─────────────────────────────────────┘
```

### 3.3 统一响应格式

所有接口统一返回 `ApiResponse<T>` 结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "a1b2c3d4e5f67890"
}
```

- `code = 0` 表示成功，非 0 表示业务异常
- `traceId` 用于请求链路追踪，便于日志排查
- 分页数据封装在 `data` 内（隐含 Page 结构）

### 3.4 错误码体系

错误码根据不同模块划分区间：

| 区间 | 模块 | 示例 |
|------|------|------|
| `0` | 成功 | `SUCCESS(0, 200, "成功")` |
| `400xx` | 通用 | `REQUEST_PARAM_ERROR(40000, 400)` |
| `401xx` | 用户 | `USERNAME_OR_PASSWORD_ERROR(40100, 401)` |
| `402xx` | 配件 | `HARDWARE_NOT_FOUND(40200, 404)` |
| `403xx` | 装机单 | `BUILD_NOT_FOUND(40300, 404)` |
| `404xx` | 交互 | `ALREADY_LIKED(40401, 409)` |
| `405xx` | 文章 | `ARTICLE_NOT_FOUND(40500, 404)` |
| `406xx` | 评论 | `COMMENT_NOT_FOUND(40600, 404)` |
| `500xx` | 系统 | `SYSTEM_ERROR(50000, 500)` |

---

## 4. 数据层架构

### 4.1 数据源配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${nf.db.host}:${nf.db.port}/nep_forge?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: root
    password: ${nf.db.password}
```

数据源配置通过 `${}` 占位符外部化，`application-dev.yml` 提供开发环境值，生产环境通过环境变量注入。

### 4.2 MyBatis-Plus 配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Mapper 扫描 | `com.nep.**.mapper` | 全局扫描所有模块 |
| Mapper XML 位置 | `classpath*:/mapper/**/*.xml` | 多模块 XML 加载 |
| 驼峰命名 | `map-underscore-to-camel-case: true` | 数据库下划线 ↔ Java 驼峰 |
| 分页插件 | `PaginationInnerInterceptor(DbType.MYSQL)` | 物理分页 |

### 4.3 数据库表模块映射

| 业务模块 | 数据库表 | 说明 |
|---------|----------|------|
| 用户系统 | `users`、`roles`、`role_users` | 用户 + RBAC 角色模型 |
| 配件库 | `hw_category`、`hardware` | 无限级分类 + JSON 参数 |
| 装机单 | `user_builds`、`user_build_details` | 主表 + 详情明细 |
| 内容 | `article`、`article_category`、`article_tag`、`article_tag_relation` | 文章 + 分类 + 标签 |
| 评论 | `comment` | 楼中楼 via parent_id |
| 交互 | `favorites`、`user_interaction` | 收藏夹 + 统一交互模型 |

### 4.4 数据库设计要点

- **ID 生成**：核心业务表使用雪花算法（Snowflake），MyBatis-Plus 内置支持
- **软删除**：所有业务表通过 `is_deleted` 字段实现逻辑删除
- **统一时间戳**：`create_time` / `update_time` 由应用层维护
- **冗余计数**：`article` 表冗余 `like_count` / `favorite_count` / `comment_count` / `view_count`，避免频繁 COUNT 查询
- **JSON 字段**：`hardware.specs_json` 利用 MySQL 8.0 原生 JSON 类型存储差异化参数

---

## 5. 基础设施设计

### 5.1 环境配置管理

配置按 profile 分离：

| 文件 | 用途 | 追踪 |
|------|------|------|
| `application.yml` | 通用配置（数据源、日志、MyBatis-Plus、Knife4j） | 版本控制 |
| `application-dev.yml` | 开发环境（DB 地址、密码等） | 版本控制 |
| `application-prod.yml` | 生产环境（由环境变量覆盖） | 不提交 |
| `application-local.yml` | 本地个性化覆盖 | .gitignore |

### 5.2 Docker Compose 基础设施

```yaml
services:
  mysql:      # MySQL 8.0，自动初始化 schema.sql
  redis:      # Redis 7.x，预留缓存支持
  minio:      # MinIO 对象存储（头像/文章图片）
```

### 5.3 日志配置

- 日志级别：`com.nep: debug`
- 时间格式：`HH:mm:ss:SSS`
- 输出路径：`logs/${spring.application.name}/`
- 日志框架：SLF4J + Logback（Spring Boot 默认）

---

## 6. 关键技术决策

### 6.1 多模块拆分策略

**采用理由：**

1. **职责分离**：每个模块独立编译、独立演进，避免单体应用的耦合问题
2. **团队协作**：每个开发者/团队负责一个模块，减少代码冲突
3. **编译优化**：修改单个模块只需重新编译该模块，缩短 CI 时间
4. **复用性**：`nep-common` 可被其他项目直接复用
5. **清晰依赖**：Maven 依赖管理强制模块间单向依赖，防止循环引用

### 6.2 响应体与异常统一处理

```
ApiResponse<T> + GlobalExceptionHandler → 所有异常 → 统一 JSON
```

- Controller 层只需返回业务数据，由 `GlobalExceptionHandler` 兜底异常转换
- 业务异常通过 `CommonException(BaseErrorInfo)` 抛出，携带明确的错误码和 HTTP 状态码
- 避免每个 Controller 编写 try-catch

### 6.3 雪花 ID 策略

- 核心业务表（user, hardware, build, article, comment, interaction 等）使用雪花算法生成 `bigint unsigned` 主键
- 字典表（category, role, tag）使用数据库自增 `int unsigned`
- JSON 序列化时以字符串返回，防止 JavaScript 精度丢失

### 6.4 统一交互模型

使用 `user_interaction` 单表覆盖点赞和收藏两种行为：

```
复合唯一索引: (user_id, target_id, target_type, action_type)
```

- `target_type`: 1=文章, 2=配件, 3=装机单, 4=评论
- `action_type`: 1=点赞, 2=收藏
- 通过复合唯一索引从数据库层面防止重复操作

### 6.5 RBAC 权限模型

基于 Spring Security 实现三角色权限：

| 角色 | 编码 | 权限范围 |
|------|------|---------|
| 普通用户 | `ROLE_USER` | 文章阅读、评论、点赞、创建装机单 |
| 版主 | `ROLE_MODERATOR` | 额外可删除评论、管理违规内容 |
| 管理员 | `ROLE_ADMIN` | 所有权限（用户管理、配件 CRUD、文章审核） |

### 6.6 缓存策略（规划）

Redis 计划用于以下场景：

| 数据 | 策略 | 说明 |
|------|------|------|
| JWT 黑名单 | 过期时间自动清理 | 用户注销后 Token 失效 |
| 配件列表/详情 | 读多写少，缓存穿透保护 | 缓存分类和热门配件 |
| 文章浏览量 | 定时异步落库 | 避免频繁写数据库 |
| 点赞状态 | Redis Set 存储 | 快速判断用户是否已点赞 |

---

## 7. 项目结构索引

```
backend/
├── pom.xml                     # 父 POM，管理所有模块依赖版本
├── nep-common/                 # 公共组件模块
│   └── src/main/java/com/nep/
│       ├── constants/          # MessageConstant 消息提示常量
│       ├── exception/          # 异常体系（BaseErrorInfo + 各模块 ErrorCode + CommonException + GlobalExceptionHandler）
│       └── result/             # ApiResponse<T> 统一响应体
├── nep-server/                 # 启动聚合模块
│   └── src/main/java/com/nep/
│       ├── NepForgeApplication.java  # @SpringBootApplication 入口
│       └── config/
│           ├── Knife4jConfig.java    # Knife4j / OpenAPI 配置
│           └── MybatisPlusConfig.java # MyBatis-Plus 分页 + Mapper 扫描
├── nep-system/                 # 用户模块（空白，待实现）
├── nep-hardware/               # 配件模块（空白）
├── nep-build/                  # 装机单模块（空白）
├── nep-content/                # 文章模块（空白）
├── nep-interaction/            # 交互模块（空白）
├── nep-framework/              # 框架配置（空白）
├── nep-security/               # 安全模块（空白）
└── nep-admin/                  # 后台管理（空白）
```

---

## 8. 版本规划对照

| 版本 | 里程碑 | 涉及模块 | 架构交付 |
|------|--------|---------|---------|
| **V0.1** | 项目骨架 | 全部 | 多模块结构、统一响应、全局异常、Knife4j 配置 ✅ |
| **V0.2** | 用户认证 | `nep-security` + `nep-system` | Spring Security 集成、JWT 登录、RBAC |
| **V0.3** | 配件库 | `nep-hardware` | 分类树、配件 CRUD、搜索筛选 |
| **V0.4** | 装机单 | `nep-build` | 装机单创建、配件增删、价格计算 |
| **V0.5** | 互动功能 | `nep-interaction` | 点赞、收藏、收藏夹 |
| **V0.6** | 社区内容 | `nep-content` | 文章管理、标签、评论楼中楼 |
| **V0.7** | 后台管理 | `nep-admin` | Dashboard、用户管理、内容审核 |
| **V0.8** | 部署展示 | 全部 | Docker Compose 一键部署、CI/CD |
