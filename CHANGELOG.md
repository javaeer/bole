# 更新日志 / Changelog

本项目遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/) 约定，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-01-14

首个公开版本。

### 新增 / Added
- 结构化履历：简历主档与教育 / 工作 / 项目经历、技能、求职意向、自我评价模块
- 简历模板引擎：模板、组件、布局列与样式（字体、间距）解耦
- 文档导出：基于 Apache POI 生成 Word / PDF 简历（`DocumentTask`）
- 对象存储：集成 MinIO，统一管理文件资源
- 账号体系：JWT 鉴权与 `User` / `Role` / `UserRole` RBAC
- 基础数据：城市、地区、公司、院校、数据字典
- 互动功能：公司 / 院校关注、公司评论、用户反馈
- 消息中心：邮件、短信与消息模板
- 多端前端：uni-app 一套代码覆盖 H5 / 微信小程序 / App / 鸿蒙等
- API 文档：集成 Knife4j（OpenAPI 3）
- 数据库版本化：Flyway 管理 SQL 迁移
- 一键部署：Docker Compose 编排 PostgreSQL / Redis / MinIO / server / app

### 文档 / Docs
- 项目 README、架构文档、贡献指南、行为准则、安全策略

---

## 待发布 / Unreleased

- 管理后台 `admin` 模块落地
- 作品集公开展示与分享页
- 多语言（i18n）前端完善
- 简历 AI 润色与智能排版

[1.0.0]: https://github.com/javaeer/bole/releases/tag/v1.0.0
