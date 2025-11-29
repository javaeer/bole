# vue-uniapp-template

## 介绍
基于 uni-app + Vue 3 + TypeScript 的项目，集成了 ESLint、Prettier、Stylelint、Husky 和 Commitlint 等工具，确保代码规范与质量。

![输入图片说明](https://i-blog.csdnimg.cn/direct/2c0eaeaa15134517a77b7a1689388537.gif)

## 项目启动

安装依赖
```
pnpm install
```

h5启动
```
pnpm run dev:h5
```

访问 [http://localhost:3000](http://localhost:4096)


TypeScript 文件的命名规范主要有以下几种常见方式：

## 1. **kebab-case（烤肉串式）** - **最推荐**
```typescript
// 文件命名
system-config-handler.ts
user-profile-service.ts
api-client.ts
data-access-layer.ts
```

**优点：**
- 与 URL 风格一致
- 在大多数文件系统中兼容性最好
- 清晰易读

## 2. **camelCase（驼峰式）**
```typescript
// 文件命名
systemConfigHandler.ts
userProfileService.ts
apiClient.ts
dataAccessLayer.ts
```

## 3. **PascalCase（帕斯卡式）**
```typescript
// 文件命名
SystemConfigHandler.ts
UserProfileService.ts
ApiClient.ts
DataAccessLayer.ts
```

## 4. **snake_case（蛇形式）**
```typescript
// 文件命名
system_config_handler.ts
user_profile_service.ts
api_client.ts
data_access_layer.ts
```

## **推荐规范**

### **通用规范：**
```typescript
// ✅ 推荐 - kebab-case
system-config.ts
user-service.ts
api-endpoints.ts
data-models.ts

// ✅ 组件文件 - PascalCase
UserProfile.vue
ProductCard.tsx
AppHeader.ts

// ✅ 测试文件
system-config.test.ts
user-service.spec.ts
api-client.test.ts
```

### **特殊文件类型：**
```typescript
// 配置文件
tsconfig.json
package.json
.eslintrc.js

// 类型定义文件
global.d.ts
index.d.ts
types.ts

// 工具函数
utils.ts
helpers.ts
constants.ts

// Hooks
use-system-config.ts
use-user-data.ts
```

### **项目结构示例：**
```
src/
├── components/
│   ├── user-profile/
│   │   ├── user-profile.vue
│   │   ├── user-profile.styles.ts
│   │   └── user-profile.test.ts
│   └── product-card.tsx
├── services/
│   ├── api-client.ts
│   ├── user-service.ts
│   └── system-config-service.ts
├── stores/
│   ├── user-store.ts
│   └── system-config-store.ts
├── types/
│   ├── user.types.ts
│   └── api.types.ts
├── utils/
│   ├── format-utils.ts
│   └── validation-utils.ts
└── hooks/
    ├── use-system-config.ts
    └── use-user-data.ts
```

## **最佳实践建议：**

1. **保持一致性** - 在整个项目中使用同一种命名约定
2. **描述性命名** - 文件名应清晰反映其内容
3. **避免缩写** - 除非是广泛认可的缩写（如 `api`, `util`）
4. **组件用 PascalCase** - 特别是 Vue/React 组件
5. **其他用 kebab-case** - 服务、工具、存储等