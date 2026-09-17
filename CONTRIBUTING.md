# 贡献指南 / Contributing to Bole

感谢你考虑为 **伯乐 Bole** 做出贡献！无论是报告 Bug、提出新功能，还是提交代码，我们都非常欢迎。

## 🧭 行为准则

参与本项目的所有成员都需遵守 [行为准则](CODE_OF_CONDUCT.md)。

## 🐞 报告问题

- 提交 Issue 前请先[搜索](https://github.com/javaeer/bole/issues)是否已有相同或类似问题。
- 使用我们提供的 [Bug 报告模板](https://github.com/javaeer/bole/issues/new?template=bug_report.yml) 与 [功能建议模板](https://github.com/javaeer/bole/issues/new?template=feature_request.yml)，并尽量提供：
  - 复现步骤
  - 期望与实际表现
  - 环境信息（操作系统、JDK / Node 版本、浏览器等）
  - 相关日志或截图

## 🌿 分支模型

我们采用简化的 Git Flow：

| 分支 | 用途 |
| --- | --- |
| `main` | 稳定可发布版本，受保护 |
| `dev` | 日常开发集成分支 |
| `feature/*` | 新功能（如 `feature/resume-template`） |
| `fix/*` | 缺陷修复（如 `fix/login-token`） |
| `release/*` | 发布准备 |

> 请基于 `dev` 创建你的特性分支，完成后向 `dev` 发起 Pull Request。

## ✅ 提交规范（Conventional Commits）

提交信息请遵循 [Conventional Commits](https://www.conventionalcommits.org/)：

```text
<type>(<scope>): <subject>

<body>（可选）
<footer>（可选，如 Closes #123）
```

常用 `type`：

| 类型 | 说明 |
| --- | --- |
| `feat` | 新功能 |
| `fix` | 缺陷修复 |
| `docs` | 文档变更 |
| `style` | 代码格式（不影响逻辑） |
| `refactor` | 重构 |
| `test` | 测试相关 |
| `chore` | 构建 / 工具链变更 |

示例：

```bash
git commit -m "feat(resume): 支持简历模板自定义字体间距"
```

## 🛠 本地开发

1. Fork 并克隆仓库：`git clone https://github.com/<your-name>/bole.git`
2. 启动基础设施：`docker compose up -d postgresql redis minio`
3. 后端：`cd server && ./mvnw clean package -DskipTests`
4. 前端：`cd app && pnpm install && pnpm run dev:h5`

## 📝 Pull Request 流程

1. 确保分支基于最新的 `dev`。
2. 保证后端 `./mvnw spotless:check` 与 `compile` 通过，前端 `pnpm run type-check` 通过。
3. 在 PR 描述中说明**变更内容**与**测试方式**，并关联相关 Issue。
4. 至少经过一位维护者 Review 通过后合并。

## 📮 联系方式

如有疑问，欢迎在 [Discussions](https://github.com/javaeer/bole/discussions) 或 Issue 中交流。
