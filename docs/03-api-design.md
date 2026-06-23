# NepForge 接口设计文档

> 文档位置：`docs/03-api-design.md`  
> 当前阶段：接口契约设计  
> 对应数据库：`schema.sql` / `nep_forge`  
> 技术栈建议：Spring Boot 3.x + Spring Security + JWT + MyBatis-Plus + Vue 3 + TypeScript + Axios

---

## 1. 文档目标

本文档用于将当前数据库能力转换为前后端接口约定，作为后续 Spring Boot 后端开发、Vue 3 前端联调、接口测试、Swagger / Knife4j 文档生成的基础。

当前接口设计覆盖以下模块：

1. 通用规范：统一响应结构、错误码、分页格式、认证头、命名规范。
2. 认证与用户模块：注册、登录、获取当前用户、用户主页、角色信息。
3. 配件库模块：分类、配件列表、详情、参数筛选与对比。
4. 装机单模块：创建装机单、添加配件、更新数量、公开展示。
5. 点赞收藏模块：统一点赞、取消点赞、收藏、取消收藏、收藏夹管理。
6. 文章专栏模块：文章发布、草稿、列表、详情、标签、分类。
7. 评论模块：文章评论、楼中楼回复、评论点赞。
8. 后台管理模块：用户管理、配件管理、文章管理、评论审核、分类管理。

说明：本文档只设计接口契约，不再修改数据库表结构。

---

## 2. 基础约定

### 2.1 API 前缀

统一使用版本化前缀：

```http
/api/v1
```

示例：

```http
GET /api/v1/hardware/list
POST /api/v1/auth/login
```

### 2.2 请求格式

除文件上传接口外，默认使用 JSON：

```http
Content-Type: application/json;charset=UTF-8
```

### 2.3 响应格式

所有业务接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "202605301430000001"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| code | integer | 是 | 业务状态码，`0` 表示成功 |
| message | string | 是 | 提示信息 |
| data | any | 否 | 业务数据 |
| traceId | string | 否 | 请求追踪 ID，方便排查日志 |

成功响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "10001",
    "username": "nepgear"
  },
  "traceId": "202605301430000001"
}
```

失败响应示例：

```json
{
  "code": 40001,
  "message": "用户名或密码错误",
  "data": null,
  "traceId": "202605301430000002"
}
```

### 2.4 ID 类型约定

数据库中用户、配件、文章、评论、装机单等核心主键使用 `bigint unsigned`，后端采用雪花算法生成。

由于 JavaScript `Number` 对超大整数存在精度风险，前端响应中的 ID 建议统一以字符串返回。

示例：

```json
{
  "id": "1839203849203849216"
}
```

### 2.5 时间格式约定

后端统一返回 ISO 风格本地时间字符串：

```text
yyyy-MM-dd HH:mm:ss
```

示例：

```json
{
  "createTime": "2026-05-30 14:30:00"
}
```

### 2.6 字段命名约定

数据库使用下划线命名：

```text
create_time
is_deleted
source_url
```

后端 DTO / VO / 前端 JSON 使用小驼峰命名：

```text
createTime
isDeleted
sourceUrl
```

---

## 3. 认证与权限约定

### 3.1 认证方式

登录成功后，后端返回 JWT Token。前端后续请求在 Header 中携带：

```http
Authorization: Bearer <accessToken>
```

### 3.2 Token 返回结构

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 7200,
  "user": {
    "id": "10001",
    "username": "nepgear",
    "email": "nepgear@example.com",
    "avatar": null,
    "roles": ["ROLE_USER"]
  }
}
```

### 3.3 角色约定

当前系统预置三类角色：

| 角色编码 | 说明 |
|---|---|
| ROLE_USER | 普通用户，可浏览、发文、评论、点赞、收藏、创建装机单 |
| ROLE_MODERATOR | 版主，可管理评论、处理违规内容 |
| ROLE_ADMIN | 超级管理员，拥有后台管理权限 |

### 3.4 权限粒度建议

| 功能 | 游客 | 登录用户 | 版主 | 管理员 |
|---|---:|---:|---:|---:|
| 浏览配件 | 是 | 是 | 是 | 是 |
| 浏览公开装机单 | 是 | 是 | 是 | 是 |
| 浏览已发布文章 | 是 | 是 | 是 | 是 |
| 点赞/收藏 | 否 | 是 | 是 | 是 |
| 创建装机单 | 否 | 是 | 是 | 是 |
| 发布文章 | 否 | 是 | 是 | 是 |
| 删除自己的内容 | 否 | 是 | 是 | 是 |
| 管理评论 | 否 | 否 | 是 | 是 |
| 管理用户/配件/分类 | 否 | 否 | 否 | 是 |

---

## 4. 错误码设计

### 4.1 通用错误码

| code | HTTP 状态 | 说明 |
|---:|---:|---|
| 0 | 200 | 成功 |
| 40000 | 400 | 请求参数错误 |
| 40001 | 401 | 未登录或登录已过期 |
| 40003 | 403 | 无权限访问 |
| 40004 | 404 | 资源不存在 |
| 40009 | 409 | 数据冲突，例如重复操作 |
| 42900 | 429 | 请求过于频繁 |
| 50000 | 500 | 系统内部错误 |

### 4.2 业务错误码

| code | 说明 |
|---:|---|
| 40100 | 用户名或密码错误 |
| 40101 | 用户已被删除或不可用 |
| 40102 | 用户名已存在 |
| 40103 | 邮箱已存在 |
| 40200 | 配件不存在 |
| 40201 | 配件分类不存在 |
| 40300 | 装机单不存在 |
| 40301 | 无权访问该装机单 |
| 40302 | 装机单中已存在该配件 |
| 40400 | 收藏夹不存在 |
| 40401 | 已点赞，请勿重复操作 |
| 40402 | 已收藏，请勿重复操作 |
| 40500 | 文章不存在 |
| 40501 | 文章未发布或已下架 |
| 40502 | 标签不存在 |
| 40600 | 评论不存在 |
| 40601 | 评论已被禁用 |

---

## 5. 分页格式

### 5.1 分页请求参数

列表接口统一支持：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|---:|---:|---|
| pageNum | integer | 否 | 1 | 当前页码，从 1 开始 |
| pageSize | integer | 否 | 10 | 每页数量 |
| keyword | string | 否 | - | 搜索关键词 |
| sortField | string | 否 | createTime | 排序字段 |
| sortOrder | string | 否 | desc | 排序方向：`asc` / `desc` |

### 5.2 分页响应结构

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

字段说明：

| 字段 | 类型 | 说明 |
|---|---:|---|
| records | array | 当前页数据 |
| total | long | 总记录数 |
| pageNum | integer | 当前页码 |
| pageSize | integer | 每页数量 |
| pages | integer | 总页数 |
| hasNext | boolean | 是否有下一页 |
| hasPrevious | boolean | 是否有上一页 |

---

## 6. 枚举约定

### 6.1 用户状态

| 字段 | 值 | 说明 |
|---:|---:|---|
| `status` | 0 | 禁用（无法登录，数据保留） |
| `status` | 1 | 正常 |
| `is_deleted` | 0 | 未删除 |
| `is_deleted` | 1 | 已删除

### 6.2 文章状态

| 值 | 说明 |
|---:|---|
| 0 | 草稿 |
| 1 | 已发布 |
| 2 | 已下架 |

### 6.3 装机单状态

| 值 | 说明 |
|---:|---|
| 0 | 草稿 |
| 1 | 正常 |
| 2 | 下架 |

### 6.4 评论状态

| 值 | 说明 |
|---:|---|
| 0 | 禁用 |
| 1 | 正常 |

### 6.5 交互目标类型 `targetType`

| 值 | 说明 |
|---:|---|
| 1 | 文章 |
| 2 | 配件 |
| 3 | 装机单 |
| 4 | 评论 |

### 6.6 交互行为类型 `actionType`

| 值 | 说明 |
|---:|---|
| 1 | 点赞 |
| 2 | 收藏 |

---

## 7. 认证接口

### 7.1 用户注册

```http
POST /api/v1/auth/register
```

请求体：

```json
{
  "username": "nepgear",
  "email": "nepgear@example.com",
  "password": "12345678"
}
```

校验规则：

| 字段 | 规则 |
|---|---|
| username | 必填，长度 3-50，唯一 |
| email | 必填，邮箱格式，唯一 |
| password | 必填，长度 8-32 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216",
    "username": "nepgear",
    "email": "nepgear@example.com"
  }
}
```

### 7.2 用户登录

```http
POST /api/v1/auth/login
```

请求体：

```json
{
  "account": "nepgear",
  "password": "12345678"
}
```

说明：`account` 可以是用户名或邮箱。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "id": "1839203849203849216",
      "username": "nepgear",
      "email": "nepgear@example.com",
      "avatar": null,
      "roles": ["ROLE_USER"]
    }
  }
}
```

### 7.3 退出登录

```http
POST /api/v1/auth/logout
```

权限：登录用户。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

说明：如果第一阶段不做 Token 黑名单，前端删除本地 Token 即可；后端接口可预留。

### 7.4 获取当前登录用户

```http
GET /api/v1/users/me
```

权限：登录用户。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216",
    "username": "nepgear",
    "email": "nepgear@example.com",
    "nickname": null,
    "bio": null,
    "avatar": null,
    "status": 1,
    "lastLoginTime": null,
    "roles": ["ROLE_USER"],
    "createTime": "2026-05-30 14:30:00"
  }
}
```

### 7.5 更新当前用户资料

```http
PUT /api/v1/users/me/profile
```

权限：登录用户。

请求体：

```json
{
  "avatar": "https://example.com/avatar.png",
  "nickname": "nepgear_new",
  "bio": "喜欢折腾硬件的程序员"
}
```

校验规则：

| 字段 | 规则 |
|---|---|
| avatar | 可选，有效的 URL 格式 |
| nickname | 可选，长度 1-50 |
| bio | 可选，长度不超过 255 |

---

## 8. 配件库接口

### 8.1 获取配件分类树

```http
GET /api/v1/hardware/categories/tree
```

权限：游客可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "CPU 处理器",
      "parentId": 0,
      "sortOrder": 1,
      "children": []
    }
  ]
}
```

### 8.2 获取配件分类列表

```http
GET /api/v1/hardware/categories
```

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| parentId | integer | 否 | 父级分类 ID，默认查全部 |
| status | integer | 否 | 分类状态：0 禁用，1 启用 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "CPU 处理器",
      "parentId": 0,
      "status": 1,
      "sortOrder": 1
    }
  ]
}
```

### 8.3 配件分页列表

```http
GET /api/v1/hardware/list
```

权限：游客可访问。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| keyword | string | 否 | 按名称/品牌搜索 |
| categoryId | integer | 否 | 配件分类 ID |
| brand | string | 否 | 品牌 |
| minPrice | decimal | 否 | 最低价格 |
| maxPrice | decimal | 否 | 最高价格 |
| sortField | string | 否 | `price` / `createTime` / `releaseDate` |
| sortOrder | string | 否 | `asc` / `desc` |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "1839203849203849216",
        "categoryId": 1,
        "name": "Intel Core i5-14600KF",
        "brand": "Intel",
        "price": 1899.00,
        "coverImage": "https://example.com/cpu.png",
        "releaseDate": "2023-10-17",
        "sourceName": "手动维护",
        "createTime": "2026-05-30 14:30:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 8.4 配件详情

```http
GET /api/v1/hardware/{id}
```

权限：游客可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216",
    "categoryId": 1,
    "categoryName": "CPU 处理器",
    "name": "Intel Core i5-14600KF",
    "brand": "Intel",
    "price": 1899.00,
    "coverImage": "https://example.com/cpu.png",
    "sourceName": "手动维护",
    "sourceUrl": "https://example.com/product/1",
    "releaseDate": "2023-10-17",
    "lastSyncTime": "2026-05-30 14:30:00",
    "specs": {
      "cores": 14,
      "threads": 20,
      "baseClock": "3.5GHz",
      "tdp": "125W"
    },
    "liked": false,
    "favorited": false,
    "createTime": "2026-05-30 14:30:00"
  }
}
```

说明：

- 数据库字段为 `specs_json`，接口返回字段命名为 `specs`。
- 如果用户未登录，`liked` 和 `favorited` 可统一返回 `false`。

### 8.5 配件参数对比

```http
POST /api/v1/hardware/compare
```

权限：游客可访问。

请求体：

```json
{
  "hardwareIds": [
    "1839203849203849216",
    "1839203849203849217"
  ]
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "categoryId": 1,
    "categoryName": "CPU 处理器",
    "items": [
      {
        "id": "1839203849203849216",
        "name": "Intel Core i5-14600KF",
        "brand": "Intel",
        "price": 1899.00,
        "specs": {
          "cores": 14,
          "threads": 20,
          "tdp": "125W"
        }
      }
    ],
    "fields": [
      {
        "key": "cores",
        "label": "核心数",
        "unit": "核"
      },
      {
        "key": "threads",
        "label": "线程数",
        "unit": "线程"
      }
    ]
  }
}
```

说明：第一阶段可直接返回 `specs_json` 原始参数；后续再维护分类参数模板，用于统一字段中文名、单位和排序。

---

## 9. 装机单接口

### 9.1 创建装机单

```http
POST /api/v1/builds
```

权限：登录用户。

请求体：

```json
{
  "title": "我的第一套游戏主机",
  "description": "2K 游戏配置",
  "isPublic": false,
  "coverImage": "https://example.com/build.png"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216"
  }
}
```

### 9.2 我的装机单列表

```http
GET /api/v1/builds/my
```

权限：登录用户。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| status | integer | 否 | 状态：0 草稿，1 正常，2 下架 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "1839203849203849216",
        "title": "我的第一套游戏主机",
        "totalPrice": 6999.00,
        "totalPower": 550.00,
        "isPublic": false,
        "status": 0,
        "coverImage": "https://example.com/build.png",
        "createTime": "2026-05-30 14:30:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 9.3 公开装机单列表

```http
GET /api/v1/builds/public
```

权限：游客可访问。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| keyword | string | 否 | 标题搜索 |
| sortField | string | 否 | `createTime` / `totalPrice` / `totalPower` |
| sortOrder | string | 否 | `asc` / `desc` |

响应结构同分页格式。

### 9.4 装机单详情

```http
GET /api/v1/builds/{id}
```

权限：

- 公开装机单：游客可访问。
- 私密装机单：仅创建者可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216",
    "userId": "1839203849203849000",
    "title": "我的第一套游戏主机",
    "description": "2K 游戏配置",
    "totalPrice": 6999.00,
    "totalPower": 550.00,
    "isPublic": false,
    "status": 0,
    "coverImage": "https://example.com/build.png",
    "liked": false,
    "favorited": false,
    "items": [
      {
        "detailId": "1839203849203849300",
        "hardwareId": "1839203849203849216",
        "name": "Intel Core i5-14600KF",
        "brand": "Intel",
        "categoryId": 1,
        "categoryName": "CPU 处理器",
        "price": 1899.00,
        "quantity": 1,
        "subtotal": 1899.00,
        "coverImage": "https://example.com/cpu.png",
        "specs": {
          "tdp": "125W"
        }
      }
    ],
    "createTime": "2026-05-30 14:30:00",
    "updateTime": "2026-05-30 14:30:00"
  }
}
```

### 9.5 更新装机单基础信息

```http
PUT /api/v1/builds/{id}
```

权限：装机单创建者。

请求体：

```json
{
  "title": "我的第一套游戏主机 V2",
  "description": "升级显卡后的配置",
  "isPublic": true,
  "status": 1,
  "coverImage": "https://example.com/build-v2.png"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 9.6 删除装机单

```http
DELETE /api/v1/builds/{id}
```

权限：装机单创建者。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

说明：删除采用软删除，更新 `is_deleted = 1`。

### 9.7 添加配件到装机单

```http
POST /api/v1/builds/{id}/items
```

权限：装机单创建者。

请求体：

```json
{
  "hardwareId": "1839203849203849216",
  "quantity": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "detailId": "1839203849203849300"
  }
}
```

说明：添加或删除明细后，后端需要重新计算并更新 `user_builds.total_price` 和 `user_builds.total_power`。

### 9.8 更新装机单配件数量

```http
PUT /api/v1/builds/{id}/items/{detailId}
```

权限：装机单创建者。

请求体：

```json
{
  "quantity": 2
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 9.9 移除装机单配件

```http
DELETE /api/v1/builds/{id}/items/{detailId}
```

权限：装机单创建者。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```



### 9.10 发布装机单

PUT /api/v1/builds/{id}/publish

权限：装机单创建者。

说明：

- 将装机单 `status` 从 `0` 草稿更新为 `1` 正常。
- 不修改 `isPublic`。
- 已下架装机单不允许普通用户重新发布。

响应：

```
{
  "code": 0,
  "message": "success",
  "data": true
}
```


### 9.11 设置装机单公开/私密

```http
PUT /api/v1/builds/{id}/visibility
```

权限：装机单创建者。

请求体：

```
{
  "isPublic": true
}
```

说明：

- `true` 表示公开。
- `false` 表示私密。
- 该接口只修改 `is_public`，不修改 `status`。

响应：

```
{
  "code": 0,
  "message": "success",
  "data": true
}
```





## 10. 点赞与收藏接口

### 10.1 点赞

```http
POST /api/v1/interactions/like
```

权限：登录用户。

请求体：

```json
{
  "targetId": "1839203849203849216",
  "targetType": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

说明：

- `targetType = 1` 表示文章。
- `targetType = 2` 表示配件。
- `targetType = 3` 表示装机单。
- `targetType = 4` 表示评论。

### 10.2 取消点赞

```http
DELETE /api/v1/interactions/like
```

权限：登录用户。

请求体：

```json
{
  "targetId": "1839203849203849216",
  "targetType": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 10.3 收藏

```http
POST /api/v1/interactions/favorite
```

权限：登录用户。

请求体：

```json
{
  "targetId": "1839203849203849216",
  "targetType": 1,
  "folderId": "1839203849203849001"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 10.4 取消收藏

```http
DELETE /api/v1/interactions/favorite
```

权限：登录用户。

请求体：

```json
{
  "targetId": "1839203849203849216",
  "targetType": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 10.5 获取当前用户对目标的交互状态

```http
GET /api/v1/interactions/status
```

权限：登录用户。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| targetId | string | 是 | 目标 ID |
| targetType | integer | 是 | 目标类型 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "liked": true,
    "favorited": false,
    "folderId": null
  }
}
```

### 10.6 我的点赞列表

```http
GET /api/v1/interactions/my/likes
```

权限：登录用户。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| targetType | integer | 否 | 目标类型，不传则查全部 |
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |

响应结构：分页格式。

### 10.7 我的收藏列表

```http
GET /api/v1/interactions/my/favorites
```

权限：登录用户。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| folderId | string | 否 | 收藏夹 ID |
| targetType | integer | 否 | 目标类型 |
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |

响应结构：分页格式。

---

## 11. 收藏夹接口

### 11.1 创建收藏夹

```http
POST /api/v1/favorites/folders
```

权限：登录用户。

请求体：

```json
{
  "name": "装机资料",
  "description": "收藏配件和文章",
  "isPublic": false
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849001"
  }
}
```

### 11.2 我的收藏夹列表

```http
GET /api/v1/favorites/folders/my
```

权限：登录用户。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": "1839203849203849001",
      "name": "装机资料",
      "description": "收藏配件和文章",
      "isPublic": false,
      "createTime": "2026-05-30 14:30:00"
    }
  ]
}
```

### 11.3 更新收藏夹

```http
PUT /api/v1/favorites/folders/{id}
```

权限：收藏夹创建者。

请求体：

```json
{
  "name": "CPU 资料",
  "description": "CPU 测评和参数",
  "isPublic": true
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 11.4 删除收藏夹

```http
DELETE /api/v1/favorites/folders/{id}
```

权限：收藏夹创建者。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

---

## 12. 文章接口

### 12.1 文章分页列表

```http
GET /api/v1/articles
```

权限：游客可访问。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| keyword | string | 否 | 标题关键词 |
| categoryId | integer | 否 | 文章分类 ID |
| tagId | integer | 否 | 标签 ID |
| sortField | string | 否 | `createTime` / `viewCount` / `likeCount` |
| sortOrder | string | 否 | `asc` / `desc` |

说明：游客侧只返回 `status = 1` 且 `isDeleted = 0` 的文章。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "1839203849203849216",
        "userId": "1839203849203849000",
        "authorName": "nepgear",
        "title": "第一次装机应该怎么选 CPU？",
        "categoryId": 1,
        "categoryName": "装机指南",
        "tags": [
          { "id": 1, "name": "CPU" }
        ],
        "viewCount": 100,
        "likeCount": 12,
        "favoriteCount": 8,
        "commentCount": 3,
        "createTime": "2026-05-30 14:30:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 12.2 文章详情

```http
GET /api/v1/articles/{id}
```

权限：游客可访问已发布文章；草稿仅作者本人可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216",
    "userId": "1839203849203849000",
    "authorName": "nepgear",
    "title": "第一次装机应该怎么选 CPU？",
    "content": "# CPU 选择指南\n\n正文内容...",
    "status": 1,
    "categoryId": 1,
    "categoryName": "装机指南",
    "tags": [
      { "id": 1, "name": "CPU" }
    ],
    "viewCount": 101,
    "likeCount": 12,
    "favoriteCount": 8,
    "commentCount": 3,
    "liked": false,
    "favorited": false,
    "createTime": "2026-05-30 14:30:00",
    "updateTime": "2026-05-30 14:40:00"
  }
}
```

### 12.3 创建文章

```http
POST /api/v1/articles
```

权限：登录用户。

请求体：

```json
{
  "title": "第一次装机应该怎么选 CPU？",
  "content": "# CPU 选择指南\n\n正文内容...",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": 0
}
```

说明：

- `status = 0` 保存草稿。
- `status = 1` 直接发布。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216"
  }
}
```

### 12.4 更新文章

```http
PUT /api/v1/articles/{id}
```

权限：文章作者。

请求体：

```json
{
  "title": "第一次装机应该怎么选 CPU？",
  "content": "# CPU 选择指南\n\n更新后的正文内容...",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 12.5 删除文章

```http
DELETE /api/v1/articles/{id}
```

权限：文章作者或管理员。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 12.6 我的文章列表

```http
GET /api/v1/articles/my
```

权限：登录用户。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| status | integer | 否 | 0 草稿，1 已发布，2 已下架 |
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |

响应结构：分页格式。

---

## 13. 文章分类与标签接口

### 13.1 文章分类列表

```http
GET /api/v1/article-categories
```

权限：游客可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "装机指南",
      "sortOrder": 1
    }
  ]
}
```

### 13.2 文章标签列表

```http
GET /api/v1/article-tags
```

权限：游客可访问。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| keyword | string | 否 | 标签名称搜索 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "CPU"
    }
  ]
}
```

---

## 14. 评论接口

### 14.1 获取文章评论列表

```http
GET /api/v1/articles/{articleId}/comments
```

权限：游客可访问。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 主楼评论页码 |
| pageSize | integer | 否 | 主楼评论每页数量 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "1839203849203849216",
        "articleId": "1839203849203849000",
        "userId": "1839203849203849001",
        "username": "nepgear",
        "content": "这篇文章很有帮助",
        "parentId": "0",
        "replyToUserId": null,
        "likeCount": 3,
        "liked": false,
        "createTime": "2026-05-30 14:30:00",
        "replies": [
          {
            "id": "1839203849203849217",
            "articleId": "1839203849203849000",
            "userId": "1839203849203849002",
            "username": "user2",
            "content": "同感",
            "parentId": "1839203849203849216",
            "replyToUserId": "1839203849203849001",
            "replyToUsername": "nepgear",
            "likeCount": 1,
            "liked": false,
            "createTime": "2026-05-30 14:35:00"
          }
        ]
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 14.2 发布评论

```http
POST /api/v1/articles/{articleId}/comments
```

权限：登录用户。

请求体：

```json
{
  "content": "这篇文章很有帮助",
  "parentId": "0",
  "replyToUserId": null
}
```

说明：

- `parentId = 0` 表示直接评论文章。
- `parentId != 0` 表示回复某条评论。
- `replyToUserId` 用于展示“回复 @某用户”。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216"
  }
}
```

### 14.3 删除评论

```http
DELETE /api/v1/comments/{id}
```

权限：评论作者、版主或管理员。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 14.4 点赞评论

评论点赞复用统一点赞接口：

```http
POST /api/v1/interactions/like
```

请求体：

```json
{
  "targetId": "1839203849203849216",
  "targetType": 4
}
```

---

## 15. 用户主页接口

### 15.1 用户公开主页

```http
GET /api/v1/users/{userId}/profile
```

权限：游客可访问。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849000",
    "username": "nepgear",
    "nickname": null,
    "bio": null,
    "avatar": "https://example.com/avatar.png",
    "articleCount": 10,
    "publicBuildCount": 3,
    "createTime": "2026-05-30 14:30:00"
  }
}
```

### 15.2 用户公开文章

```http
GET /api/v1/users/{userId}/articles
```

权限：游客可访问。

请求参数：分页参数。

响应结构：分页格式。

### 15.3 用户公开装机单

```http
GET /api/v1/users/{userId}/builds
```

权限：游客可访问。

请求参数：分页参数。

响应结构：分页格式。

### 15.4 用户公开收藏夹

```http
GET /api/v1/users/{userId}/favorite-folders
```

权限：游客可访问。

说明：只返回 `isPublic = 1` 且未删除的收藏夹。

响应结构：列表。

---

## 16. 后台管理接口

后台接口统一使用：

```http
/api/v1/admin
```

除特别说明外，均需要 `ROLE_ADMIN`。

### 16.1 后台用户分页列表

```http
GET /api/v1/admin/users
```

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| keyword | string | 否 | 用户名/邮箱搜索 |

响应结构：分页格式。

### 16.2 删除用户

```http
DELETE /api/v1/admin/users/{id}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

说明：当前 `users` 表未设计独立 `status` 字段，因此后台用户处理以软删除为主，即更新 `is_deleted = 1`。

### 16.3 后台配件分页列表

```http
GET /api/v1/admin/hardware
```

请求参数同前台配件列表，但后台可查询已删除或全部状态。

### 16.4 新增配件

```http
POST /api/v1/admin/hardware
```

请求体：

```json
{
  "categoryId": 1,
  "name": "Intel Core i5-14600KF",
  "brand": "Intel",
  "price": 1899.00,
  "sourceName": "手动维护",
  "sourceUrl": "https://example.com/product/1",
  "releaseDate": "2023-10-17",
  "coverImage": "https://example.com/cpu.png",
  "specs": {
    "cores": 14,
    "threads": 20,
    "tdp": "125W"
  }
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "1839203849203849216"
  }
}
```

### 16.5 更新配件

```http
PUT /api/v1/admin/hardware/{id}
```

请求体同新增配件。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 16.6 删除配件

```http
DELETE /api/v1/admin/hardware/{id}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 16.7 配件分类管理

#### 新增配件分类

```http
POST /api/v1/admin/hardware/categories
```

请求体：

```json
{
  "name": "声卡",
  "parentId": 0,
  "status": 1,
  "sortOrder": 20
}
```

#### 更新配件分类

```http
PUT /api/v1/admin/hardware/categories/{id}
```

#### 删除配件分类

```http
DELETE /api/v1/admin/hardware/categories/{id}
```

### 16.8 后台文章分页列表

```http
GET /api/v1/admin/articles
```

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| keyword | string | 否 | 标题关键词 |
| status | integer | 否 | 文章状态 |
| userId | string | 否 | 作者 ID |

响应结构：分页格式。

### 16.9 下架或恢复文章

```http
PUT /api/v1/admin/articles/{id}/status
```

请求体：

```json
{
  "status": 2
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 16.10 评论管理列表

```http
GET /api/v1/admin/comments
```

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| pageNum | integer | 否 | 页码 |
| pageSize | integer | 否 | 每页数量 |
| articleId | string | 否 | 文章 ID |
| userId | string | 否 | 用户 ID |
| status | integer | 否 | 评论状态 |

响应结构：分页格式。

### 16.11 禁用或恢复评论

```http
PUT /api/v1/admin/comments/{id}/status
```

请求体：

```json
{
  "status": 0
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 16.12 文章分类管理

#### 新增文章分类

```http
POST /api/v1/admin/article-categories
```

请求体：

```json
{
  "name": "装机指南",
  "sortOrder": 1
}
```

#### 更新文章分类

```http
PUT /api/v1/admin/article-categories/{id}
```

#### 删除文章分类

```http
DELETE /api/v1/admin/article-categories/{id}
```

### 16.13 文章标签管理

#### 新增文章标签

```http
POST /api/v1/admin/article-tags
```

请求体：

```json
{
  "name": "CPU"
}
```

#### 更新文章标签

```http
PUT /api/v1/admin/article-tags/{id}
```

#### 删除文章标签

```http
DELETE /api/v1/admin/article-tags/{id}
```

---

## 17. 文件上传接口

当前数据库中图片字段均以 URL 形式保存，例如：

- `users.avatar`
- `hardware.cover_image`
- `user_builds.cover_image`

因此可以预留统一上传接口。

### 17.1 上传图片

```http
POST /api/v1/files/images
```

权限：登录用户。

请求格式：

```http
Content-Type: multipart/form-data
```

表单字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| file | file | 是 | 图片文件 |
| bizType | string | 否 | 业务类型：`avatar` / `hardware` / `article` / `build` |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "https://example.com/uploads/2026/05/30/demo.png"
  }
}
```

第一阶段可先返回本地静态资源路径，后续再切换到对象存储。

---

## 18. 前端 Axios 约定

### 18.1 响应拦截逻辑

前端统一判断：

```ts
if (response.data.code !== 0) {
  // 弹出错误信息
  // code = 40001 时跳转登录页
}
```

### 18.2 Token 注入逻辑

```ts
config.headers.Authorization = `Bearer ${token}`
```

### 18.3 分页类型建议

```ts
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

### 18.4 通用响应类型建议

```ts
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  traceId?: string
}
```

---

## 19. 后端 DTO / VO 命名建议

### 19.1 认证模块

| 类型 | 名称 |
|---|---|
| Request | `RegisterRequest` |
| Request | `LoginRequest` |
| Response | `LoginResponse` |
| VO | `CurrentUserVO` |

### 19.2 配件模块

| 类型 | 名称 |
|---|---|
| Query | `HardwareQueryRequest` |
| Request | `HardwareCompareRequest` |
| VO | `HardwareListVO` |
| VO | `HardwareDetailVO` |
| VO | `HardwareCategoryVO` |

### 19.3 装机单模块

| 类型 | 名称 |
|---|---|
| Request | `BuildCreateRequest` |
| Request | `BuildUpdateRequest` |
| Request | `BuildItemAddRequest` |
| Request | `BuildItemUpdateRequest` |
| VO | `BuildListVO` |
| VO | `BuildDetailVO` |
| VO | `BuildItemVO` |

### 19.4 内容模块

| 类型 | 名称 |
|---|---|
| Request | `ArticleCreateRequest` |
| Request | `ArticleUpdateRequest` |
| Query | `ArticleQueryRequest` |
| VO | `ArticleListVO` |
| VO | `ArticleDetailVO` |
| Request | `CommentCreateRequest` |
| VO | `CommentVO` |

### 19.5 交互模块

| 类型 | 名称 |
|---|---|
| Request | `InteractionRequest` |
| Request | `FavoriteRequest` |
| VO | `InteractionStatusVO` |
| Request | `FavoriteFolderCreateRequest` |
| VO | `FavoriteFolderVO` |

---

## 20. Controller 分层建议

建议后端 Controller 包结构：

```text
com.nepforge.controller
├── AuthController
├── UserController
├── HardwareController
├── BuildController
├── InteractionController
├── FavoriteController
├── ArticleController
├── ArticleCategoryController
├── ArticleTagController
├── CommentController
├── FileController
└── admin
    ├── AdminUserController
    ├── AdminHardwareController
    ├── AdminHardwareCategoryController
    ├── AdminArticleController
    ├── AdminCommentController
    ├── AdminArticleCategoryController
    └── AdminArticleTagController
```

---

## 21. 第一阶段开发优先级

建议按以下顺序实现接口：

### 21.1 P0：项目基础能力

1. 统一响应结构 `ApiResponse<T>`。
2. 全局异常处理 `GlobalExceptionHandler`。
3. 错误码枚举 `ErrorCode`。
4. 分页返回结构 `PageResult<T>`。
5. 登录注册与 JWT 鉴权。
6. 当前用户接口 `/users/me`。

### 21.2 P1：核心业务闭环

1. 配件分类树。
2. 配件列表与详情。
3. 装机单创建、详情、添加配件、删除配件。
4. 公开装机单列表。
5. 点赞与收藏。
6. 收藏夹列表。

### 21.3 P2：内容社区闭环

1. 文章分类、标签列表。
2. 文章创建、更新、详情、列表。
3. 评论发布、评论列表、评论删除。
4. 我的文章列表。
5. 我的点赞、我的收藏。

### 21.4 P3：后台管理

1. 配件管理。
2. 配件分类管理。
3. 文章管理。
4. 评论管理。
5. 用户软删除管理。
6. 标签与文章分类管理。

---

## 22. 接口测试建议

建议在项目中维护：

```text
docs/
  03-api-design.md

api/
  nepforge.postman_collection.json

src/test/java/
  com/nepforge/controller/
```

第一阶段可以先使用 Apifox / Postman 手动调试，后续再补充 Spring Boot 集成测试。

每个接口至少验证：

1. 正常请求。
2. 参数缺失。
3. 未登录访问。
4. 无权限访问。
5. 资源不存在。
6. 重复操作，例如重复点赞、重复收藏、装机单重复添加同一配件。

---

## 23. 与数据库表的映射关系

| 接口模块 | 主要数据表 |
|---|---|
| 认证与用户 | `users`, `roles`, `role_users` |
| 收藏夹 | `favorites` |
| 点赞收藏 | `user_interaction` |
| 配件库 | `hw_category`, `hardware` |
| 装机单 | `user_builds`, `user_build_details`, `hardware` |
| 文章 | `article`, `article_category`, `article_tag`, `article_tag_relation` |
| 评论 | `comment`, `article`, `users` |
| 后台管理 | 以上所有业务表 |

---

## 24. 当前阶段暂不实现的接口

以下功能可以预留，但不建议第一阶段实现：

1. 教学板块交互式动画接口。
2. 第三方硬件数据自动爬取接口。
3. 消息通知接口。
4. 关注用户接口。
5. 私信接口。
6. 硬件价格历史接口。
7. 评论审核队列工作流。
8. OAuth 第三方登录。

---

## 25. 文档结论

当前接口设计已经覆盖 NepForge 第一阶段的完整业务闭环：

1. 用户可以注册、登录、访问个人主页。
2. 用户可以浏览配件库、查看配件详情、对比参数。
3. 用户可以创建自己的装机单，并保存配件配置。
4. 用户可以点赞、收藏文章、配件、装机单和评论。
5. 用户可以创建收藏夹并管理收藏内容。
6. 用户可以发布文章、浏览专栏、参与评论交流。
7. 管理员可以在后台维护配件、分类、文章、评论和用户。

后续开发时，应优先保证接口路径、请求字段、响应字段稳定，再进入具体业务实现。
