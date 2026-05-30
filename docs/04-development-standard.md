可以。NepForge 当前阶段正式采用下面这套开发规范。建议你把它保存成：

```text
docs/00-development-standard.md
```

这套规范会和你已有的接口文档保持一致：接口统一使用 `/api/v1` 前缀、统一 JSON 请求、统一响应结构、ID 以字符串返回、DTO/VO/前端 JSON 使用小驼峰命名。

# 阶段 0：NepForge 开发规范

## 1. Git 分支规范

你是个人项目，但目标是模拟企业开发，所以采用**简化版 Git Flow**。

### 1.1 固定分支

```text
main        # 稳定版本分支，只放可以运行、可以展示的版本
develop     # 日常集成分支，所有功能完成后先合并到这里
```

### 1.2 临时开发分支

从 `develop` 拉出功能分支：

```text
feature/功能模块-简短说明
fix/问题模块-简短说明
docs/文档说明
chore/工程配置说明
refactor/重构模块说明
```

### 1.3 分支命名示例

```text
feature/auth-login
feature/hardware-list
feature/build-create
feature/article-editor
feature/admin-hardware

fix/auth-token-expired
fix/build-total-price
fix/hardware-query-error

docs/api-design
docs/database-design

chore/docker-compose
chore/gitignore
refactor/common-response
```

### 1.4 分支使用流程

```bash
git checkout develop
git pull

git checkout -b feature/auth-login

# 开发完成后
git add .
git commit -m "feat(auth): add login api"

git checkout develop
git merge feature/auth-login
```

阶段版本完成后：

```bash
git checkout main
git merge develop
git tag v0.1.0
```

### 1.5 版本标签规范

```text
v0.1.0  项目初始化、环境启动、数据库初始化
v0.2.0  用户注册登录、JWT 鉴权
v0.3.0  配件分类、配件列表、配件详情
v0.4.0  装机单创建、编辑、详情
v0.5.0  点赞、收藏、收藏夹
v0.6.0  文章、评论、社区基础功能
```

这和你需求文档里的 MVP 分阶段验收方式一致，项目应优先完成闭环，不要一开始追求大而全。

---

## 2. Commit 提交规范

采用 **Conventional Commits** 风格。

### 2.1 提交格式

```text
type(scope): subject
```

格式说明：

```text
type    提交类型
scope   影响范围
subject 简短说明
```

### 2.2 type 类型

| type     | 说明                           |
| -------- | ------------------------------ |
| feat     | 新功能                         |
| fix      | 修复 Bug                       |
| docs     | 文档修改                       |
| style    | 代码格式调整，不影响逻辑       |
| refactor | 重构代码，不新增功能、不修 Bug |
| test     | 测试相关                       |
| chore    | 工程配置、依赖、脚手架等       |
| build    | 构建相关                       |
| ci       | CI/CD 相关                     |
| perf     | 性能优化                       |
| revert   | 回滚提交                       |

### 2.3 scope 范围

后端推荐：

```text
auth
user
role
hardware
category
build
interaction
favorite
article
comment
admin
common
security
db
```

前端推荐：

```text
layout
router
store
api
auth
hardware
build
article
profile
admin
components
utils
```

工程类推荐：

```text
docker
git
docs
config
deps
```

### 2.4 提交示例

```bash
git commit -m "chore(git): add project gitignore"

git commit -m "docs(project): add development standard"

git commit -m "feat(auth): add user register api"

git commit -m "feat(hardware): add category tree api"

git commit -m "fix(build): correct total price calculation"

git commit -m "refactor(common): optimize api response structure"

git commit -m "feat(article): add article publish page"

git commit -m "chore(docker): add mysql and redis services"
```

### 2.5 提交原则

每次提交只做一类事情。

不推荐：

```text
feat: 写了登录、注册、配件列表、顺便改了数据库和页面样式
```

推荐拆成：

```text
feat(auth): add register api
feat(auth): add login api
feat(hardware): add hardware list api
style(login): adjust login page layout
docs(db): update schema description
```

---

## 3. 接口命名规范

接口规范以当前 `03-api-design.md` 为准。基础规则如下：

### 3.1 API 前缀

统一使用：

```http
/api/v1
```

示例：

```http
GET /api/v1/hardware/list
POST /api/v1/auth/login
```

### 3.2 请求格式

除文件上传外，统一使用：

```http
Content-Type: application/json;charset=UTF-8
```

### 3.3 响应格式

统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "202605301430000001"
}
```

### 3.4 ID 返回规则

后端数据库主键使用 `bigint unsigned`，前端 JavaScript 对大整数有精度风险，所以接口返回 ID 时统一用字符串：

```json
{
  "id": "1839203849203849216"
}
```

### 3.5 字段命名

数据库字段使用下划线：

```text
create_time
is_deleted
source_url
```

接口字段、DTO、VO、前端类型统一使用小驼峰：

```text
createTime
isDeleted
sourceUrl
```

### 3.6 REST 风格规则

查询用 `GET`：

```http
GET /api/v1/hardware/{id}
GET /api/v1/articles/{id}
GET /api/v1/builds/{id}
```

新增用 `POST`：

```http
POST /api/v1/articles
POST /api/v1/builds
POST /api/v1/interactions/like
```

更新用 `PUT`：

```http
PUT /api/v1/articles/{id}
PUT /api/v1/builds/{id}
PUT /api/v1/admin/hardware/{id}
```

删除用 `DELETE`：

```http
DELETE /api/v1/articles/{id}
DELETE /api/v1/builds/{id}
DELETE /api/v1/interactions/like
```

### 3.7 路径命名规则

资源使用复数名词：

```text
/users
/articles
/comments
/builds
/favorites
```

功能动作可以作为子路径：

```http
POST /api/v1/auth/login
POST /api/v1/auth/logout
GET /api/v1/users/me
POST /api/v1/hardware/compare
GET /api/v1/interactions/status
```

后台接口统一加 `/admin`：

```http
GET /api/v1/admin/users
POST /api/v1/admin/hardware
PUT /api/v1/admin/articles/{id}/status
```

### 3.8 分页参数

列表接口统一使用：

```text
pageNum
pageSize
keyword
sortField
sortOrder
```

分页响应统一使用：

```json
{
  "records": [],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10,
  "pages": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## 4. 后端分层规范

后端采用 **按业务模块分包**。需求文档已经明确要求后端按业务模块分包、接口返回格式统一、错误码统一管理、枚举字段统一定义。 数据库设计文档也已经给出推荐包结构和实体命名。

### 4.1 后端根包

```text
com.nepforge
```

### 4.2 推荐目录结构

```text
backend/src/main/java/com/nepforge/
├── NepForgeApplication.java
├── common
│   ├── result
│   │   ├── ApiResponse.java
│   │   ├── PageResult.java
│   │   └── ErrorCode.java
│   ├── exception
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── config
│   ├── constant
│   ├── enums
│   └── util
├── security
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── SecurityConfig.java
└── module
    ├── auth
    │   ├── controller
    │   ├── service
    │   ├── dto
    │   └── vo
    ├── user
    │   ├── controller
    │   ├── service
    │   ├── mapper
    │   ├── entity
    │   ├── dto
    │   └── vo
    ├── hardware
    ├── build
    ├── interaction
    ├── favorite
    ├── article
    ├── comment
    └── admin
```

### 4.3 每层职责

| 层               | 职责                                           |
| ---------------- | ---------------------------------------------- |
| controller       | 接收请求、参数校验、调用 service、返回统一响应 |
| service          | 写业务逻辑、事务控制、权限校验                 |
| mapper           | MyBatis-Plus 数据库访问                        |
| entity           | 数据库表映射对象                               |
| dto              | 请求参数对象                                   |
| vo               | 返回给前端的视图对象                           |
| enums            | 枚举值，例如文章状态、目标类型、行为类型       |
| common.result    | 统一响应、分页响应、错误码                     |
| common.exception | 全局异常和业务异常                             |
| security         | JWT、Spring Security、权限处理                 |

### 4.4 命名规范

Entity：

```text
User
Role
Hardware
UserBuild
Article
Comment
UserInteraction
```

DTO：

```text
RegisterRequest
LoginRequest
HardwareQueryRequest
BuildCreateRequest
ArticleCreateRequest
CommentCreateRequest
```

VO：

```text
CurrentUserVO
HardwareDetailVO
BuildDetailVO
ArticleListVO
CommentVO
InteractionStatusVO
```

Service：

```text
UserService
HardwareService
BuildService
ArticleService
CommentService
```

Service 实现类：

```text
UserServiceImpl
HardwareServiceImpl
BuildServiceImpl
```

Controller：

```text
AuthController
UserController
HardwareController
BuildController
ArticleController
CommentController
```

后台 Controller：

```text
AdminUserController
AdminHardwareController
AdminArticleController
AdminCommentController
```

### 4.5 后端开发规则

Controller 不直接操作数据库。

错误不要直接 `return null`，统一抛出业务异常：

```java
throw new BusinessException(ErrorCode.NOT_FOUND, "配件不存在");
```

新增、编辑、删除类操作放在 Service 层处理事务。

所有列表接口必须分页。

所有需要登录的接口从当前登录上下文获取 userId，不允许前端直接传 userId 来决定资源归属。

---

## 5. 前端目录规范

前端采用 Vue 3 + TypeScript + Axios，按页面和业务模块拆分。这个方向与需求文档中的可维护性要求一致。

### 5.1 推荐目录结构

```text
frontend/
├── public/
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── router/
│   │   ├── index.ts
│   │   └── routes.ts
│   ├── stores/
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   └── compare.ts
│   ├── api/
│   │   ├── request.ts
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── hardware.ts
│   │   ├── build.ts
│   │   ├── interaction.ts
│   │   ├── favorite.ts
│   │   ├── article.ts
│   │   ├── comment.ts
│   │   └── admin.ts
│   ├── types/
│   │   ├── api.ts
│   │   ├── user.ts
│   │   ├── hardware.ts
│   │   ├── build.ts
│   │   ├── article.ts
│   │   └── comment.ts
│   ├── views/
│   │   ├── home/
│   │   ├── auth/
│   │   │   ├── LoginView.vue
│   │   │   └── RegisterView.vue
│   │   ├── hardware/
│   │   │   ├── HardwareListView.vue
│   │   │   ├── HardwareDetailView.vue
│   │   │   └── CompareView.vue
│   │   ├── build/
│   │   │   ├── BuildListView.vue
│   │   │   ├── BuildDetailView.vue
│   │   │   └── BuildCreateView.vue
│   │   ├── article/
│   │   │   ├── ArticleListView.vue
│   │   │   ├── ArticleDetailView.vue
│   │   │   └── ArticleEditorView.vue
│   │   ├── profile/
│   │   │   ├── ProfileView.vue
│   │   │   ├── MyBuildsView.vue
│   │   │   ├── MyFavoritesView.vue
│   │   │   └── MyLikesView.vue
│   │   └── admin/
│   │       ├── AdminLayout.vue
│   │       ├── AdminDashboardView.vue
│   │       ├── AdminUserView.vue
│   │       ├── AdminHardwareView.vue
│   │       ├── AdminArticleView.vue
│   │       └── AdminCommentView.vue
│   ├── components/
│   │   ├── common/
│   │   ├── layout/
│   │   ├── hardware/
│   │   ├── build/
│   │   ├── article/
│   │   └── comment/
│   ├── composables/
│   │   ├── useAuth.ts
│   │   ├── usePagination.ts
│   │   └── usePermission.ts
│   ├── utils/
│   │   ├── token.ts
│   │   ├── format.ts
│   │   └── validate.ts
│   ├── assets/
│   └── styles/
│       ├── index.scss
│       └── variables.scss
├── package.json
└── vite.config.ts
```

### 5.2 前端文件命名

Vue 页面组件：

```text
LoginView.vue
HardwareListView.vue
HardwareDetailView.vue
ArticleEditorView.vue
```

普通组件：

```text
HardwareCard.vue
ArticleCard.vue
BuildItemList.vue
CommentTree.vue
```

API 文件：

```text
auth.ts
hardware.ts
build.ts
article.ts
comment.ts
```

类型文件：

```text
api.ts
user.ts
hardware.ts
article.ts
```

### 5.3 Axios 规范

`src/api/request.ts` 只负责 Axios 实例、请求拦截、响应拦截。

业务接口不要直接写在页面里，必须放到 `src/api/xxx.ts`。

例如：

```ts
// src/api/hardware.ts
export function getHardwareDetail(id: string) {
  return request.get(`/hardware/${id}`)
}
```

页面中只调用：

```ts
const res = await getHardwareDetail(id)
```

### 5.4 前端类型规范

统一定义通用响应类型：

```ts
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
  hasNext: boolean
  hasPrevious: boolean
}
```

这和当前接口文档中的前端 Axios 与分页类型约定一致。
