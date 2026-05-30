# NepForge

<p align="center">
  <strong>Spring Boot + Vue 3 · 前后端分离 · 装机与数码产品社区平台</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vuedotjs" alt="Vue 3">
  <img src="https://img.shields.io/badge/TypeScript-5.x-3178C6?logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7.x-DC382D?logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/Docker-Supported-2496ED?logo=docker" alt="Docker">
  <img src="https://img.shields.io/badge/license-MIT-blue" alt="License">
</p>

---

## 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [核心功能](#核心功能)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [开发环境](#开发环境)
- [版本规划](#版本规划)
- [文档索引](#文档索引)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

---

## 项目简介

NepForge 是一个面向电脑硬件与数码产品爱好者的社区平台，采用 **Spring Boot + Vue 3** 前后端分离架构。平台整合了配件信息浏览、装机方案管理、参数对比、专栏文章、社区评论、点赞收藏等核心能力，帮助用户在一个系统中完成从硬件选型到装机方案沉淀的全部流程。

项目同时模拟企业级开发流程，涵盖需求分析、数据库设计、接口契约设计、JWT 认证、RBAC 权限控制、Docker Compose 部署、CI/CD 等真实工程实践。

### 适用场景

| 角色 | 价值 |
|---|---|
| 硬件爱好者 | 浏览配件参数、创建装机方案、阅读社区文章 |
| 内容创作者 | 发布装机指南、硬件评测、经验分享 |
| 开发者 | 学习 Spring Boot + Vue 3 全栈项目实战、企业级工程规范 |

---

## 技术架构

### 整体拓扑

```
Browser ──▶ Vue 3 SPA ──▶ Spring Boot REST API ──▶ MySQL + Redis
                │                    │
           Element Plus        Spring Security
           Pinia               JWT Auth
           Axios               MyBatis-Plus
           ECharts             Knife4j / OpenAPI
```

### 后端技术栈

| 类型 | 技术 | 版本 |
|---|---|---|
| 运行时 | Java | 17+ |
| 框架 | Spring Boot | 3.x |
| ORM | MyBatis-Plus | 3.5+ |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.x |
| 安全 | Spring Security + JWT | — |
| 接口文档 | Knife4j / SpringDoc OpenAPI | — |
| 对象存储 | MinIO (开发) / 云 OSS (生产) | — |
| 构建 | Maven | 3.9+ |
| 容器化 | Docker + Docker Compose | — |

### 前端技术栈

| 类型 | 技术 | 版本 |
|---|---|---|
| 框架 | Vue 3 (Composition API) | 3.x |
| 语言 | TypeScript | 5.x |
| 构建 | Vite | 5.x |
| 状态管理 | Pinia | 2.x |
| 路由 | Vue Router | 4.x |
| UI 组件库 | Element Plus | 2.x |
| HTTP 客户端 | Axios | 1.x |
| Markdown | MdEditorV3 / Markdown-it | — |
| 图表 | ECharts | 5.x |
| 代码规范 | ESLint + Prettier | — |

---

## 核心功能

### 用户与权限

- JWT Token 无状态认证
- RBAC 角色权限控制（游客 / 普通用户 / 版主 / 管理员）
- 用户注册、登录、个人信息管理

### 配件库

- 多级分类树，支持 12 大类硬件
- 按分类 / 品牌 / 价格区间 / 关键词检索
- MySQL JSON 字段支撑差异化参数（CPU、显卡、内存等不同规格）
- 配件参数横向对比

### 装机方案

- 创建、编辑、删除装机单
- 配件自由添加 / 移除 / 调整数量
- 自动计算总价与总功耗
- 公开或私密分享

### 文章与社区

- Markdown 编辑器，支持草稿与发布
- 文章分类与标签体系
- 二级评论（楼中楼回复）
- 浏览量 / 点赞数 / 收藏数 / 评论数冗余计数

### 互动系统

- 统一交互模型：`user_interaction` 一表覆盖点赞与收藏
- 自定义收藏夹，支持公开/私密
- 复合唯一索引防重复操作

### 后台管理

- 数据概览 Dashboard
- 用户管理（角色分配、软删除）
- 配件 / 分类 CRUD
- 文章审核、评论管理

---

## 项目结构

```
nep-forge/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/nepforge/
│   │   ├── common/             # 通用响应、异常、工具类
│   │   ├── config/             # 应用配置
│   │   ├── security/           # Spring Security + JWT
│   │   ├── modules/            # 业务模块
│   │   │   ├── auth/           # 认证模块
│   │   │   ├── user/           # 用户模块
│   │   │   ├── hardware/       # 配件库模块
│   │   │   ├── build/          # 装机单模块
│   │   │   ├── article/        # 文章模块
│   │   │   ├── comment/        # 评论模块
│   │   │   ├── interaction/    # 点赞收藏模块
│   │   │   └── admin/          # 后台管理模块
│   │   └── infrastructure/     # 基础设施
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   └── pom.xml
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 请求层
│   │   ├── components/         # 公共组件
│   │   ├── composables/        # 组合式函数
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── types/              # TypeScript 类型
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面视图
│   │       ├── home/
│   │       ├── auth/
│   │       ├── hardware/
│   │       ├── build/
│   │       ├── article/
│   │       ├── profile/
│   │       └── admin/
│   ├── package.json
│   └── vite.config.ts
├── docker/                     # Docker 编排
│   ├── mysql/init/             # 数据库初始化脚本
│   └── ...
├── sql/                        # SQL 脚本
│   └── schema.sql              # 完整建表语句
├── docs/                       # 项目文档
│   ├── 00-project-overview.md
│   ├── 01-requirements-analysis.md
│   ├── 02-database-design.md
│   └── 03-api-design.md
├── docker-compose.yml
└── README.md
```

---

## 快速开始

### 前置条件

- **JDK** 17+
- **Node.js** 18+ / pnpm
- **Docker** & **Docker Compose**
- **Maven** 3.9+

### 1. 克隆仓库

```bash
git clone https://github.com/your-org/nep-forge.git
cd nep-forge
```

### 2. 启动基础设施

```bash
docker compose up -d
```

该命令将在后台启动 MySQL 8.0 与 Redis 7.x 并自动执行数据库初始化脚本。

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端启动后访问：

- API 基础地址：`http://localhost:8080/api/v1`
- 接口文档（Knife4j）：`http://localhost:8080/doc.html`

### 4. 启动前端

```bash
cd frontend
pnpm install
pnpm dev
```

前端开发服务器默认运行在 `http://localhost:5173`。

### 5. 验证

1. 访问 `http://localhost:5173` 查看首页。
2. 调用 `POST /api/v1/auth/register` 注册测试账号。
3. 调用 `POST /api/v1/auth/login` 获取 JWT Token。
4. 使用 Token 访问受保护接口。

---

## 开发环境

### 数据库

| 配置项 | 默认值 |
|---|---|
| 数据库名 | `nep_forge` |
| 字符集 | `utf8mb4` |
| 排序规则 | `utf8mb4_unicode_ci` |
| 引擎 | InnoDB |

### 默认账户

| 角色 | 数据库表 |
|---|---|
| 普通用户 | `roles.role_code = 'ROLE_USER'` |
| 版主 | `roles.role_code = 'ROLE_MODERATOR'` |
| 超级管理员 | `roles.role_code = 'ROLE_ADMIN'` |

> 注册时默认分配 `ROLE_USER`，管理员需在数据库中手动绑定。

### API 规范

所有接口统一以 `/api/v1` 为前缀，响应格式为：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "202605301430000001"
}
```

- `code = 0` 表示成功，非 0 表示业务异常。
- 认证方式：`Authorization: Bearer <token>`。
- 分页参数：`pageNum`（默认 1）、`pageSize`（默认 10）。
- 雪花 ID 在 JSON 中以字符串返回，防止前端精度丢失。

详细接口契约见 [`docs/03-api-design.md`](docs/03-api-design.md)。

---

## 版本规划

| 版本 | 里程碑 | 核心交付 |
|---|---|---|
| **V0.1** | 项目骨架 | 前后端项目初始化、Docker Compose、数据库 Schema、接口文档 |
| **V0.2** | 用户认证 | 注册 / 登录 / JWT / 路由鉴权 / RBAC |
| **V0.3** | 配件库 | 分类树、列表、详情、搜索筛选、后台 CRUD |
| **V0.4** | 装机单 | 创建编辑、配件增删、总价功耗计算 |
| **V0.5** | 互动功能 | 点赞 / 收藏 / 收藏夹 |
| **V0.6** | 社区内容 | 文章 Markdown 发布、评论与回复 |
| **V0.7** | 后台管理 | Dashboard、用户管理、内容审核 |
| **V0.8** | 部署展示 | 一键部署、Nginx、GitHub Actions CI/CD |

> 当前处于 **V0.1** 阶段。

---

## 文档索引

| 文档 | 说明 |
|---|---|
| [`docs/00-project-overview.md`](docs/00-project-overview.md) | 项目概述、技术架构、目录规划 |
| [`docs/01-requirements-analysis.md`](docs/01-requirements-analysis.md) | 需求分析、用户角色、MVP 边界 |
| [`docs/02-database-design.md`](docs/02-database-design.md) | 数据库设计、14 张表结构、索引策略 |
| [`docs/03-api-design.md`](docs/03-api-design.md) | 接口契约、统一响应、分页格式、认证约定 |

---

## 贡献指南

### 分支策略

- `main` — 稳定发布分支，禁止直接提交。
- `develop` — 开发集成分支。
- `feature/*` — 功能分支，从 `develop` 拉取。
- `hotfix/*` — 紧急修复分支，从 `main` 拉取。

### 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/)：

```
feat: add hardware search filter
fix: resolve price calculation precision issue
docs: update API design document
refactor: extract pagination helper
```

### 开发流程

1. 从 `develop` 创建 `feature/xxx` 分支。
2. 按业务模块开发，确保接口与 API 文档一致。
3. 自测通过后发起 Pull Request 到 `develop`。
4. 代码评审通过后合并。

---

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

<p align="center">
  <sub>Built with love for hardware enthusiasts and developers.</sub>
</p>
