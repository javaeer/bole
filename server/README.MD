# ✅ knife4j 新注解

---

## 当前主流：Swagger 3.0 + OpenAPI 3.0 注解（推荐用于 Spring Boot 2.6+ / 3.x）

> 对应依赖：`knife4j-openapi3-jakarta-spring-boot-starter`

### 🔹 类级别注解
| 旧注解 (Swagger 2) | 新注解 (OpenAPI 3.0) | 说明 |
|------------------|--------------------|------|
| `@Api`           | `@Tag`             | 描述 Controller 模块 |

**示例：**
```java
@Tag(name = "01. 用户管理", description = "提供用户增删改查接口")
@RestController
@RequestMapping("/users")
public class UserController { ... }
```

> 💡 建议在 `name` 中加序号（如 `"01. 用户管理"`），Knife4j 会自动按此排序。

---

### 🔹 方法级别注解
| 旧注解 | 新注解 | 说明 |
|-------|--------|------|
| `@ApiOperation` | `@Operation` | 描述接口功能 |
| `@ApiImplicitParam` / `@ApiImplicitParams` | `@Parameter` | 描述单个或多个参数 |
| `@ApiResponses` + `@ApiResponse` | `@ApiResponse`（配合 `@Operation`） | 描述响应状态码 |

**示例：**

```java
@Operation(summary = "获取用户详情", description = "根据ID查询用户信息")
@ApiResponse(responseCode = "200", description = "查询成功")
@ApiResponse(responseCode = "404", description = "用户不存在")
@GetMapping("/{id}")
public User getUserById(
    @Parameter(description = "用户ID", required = true, example = "1001")
    @PathVariable Long id) {
    return userService.findById(id);
}
```

---

### 🔹 实体类/字段注解
| 旧注解 | 新注解 | 说明 |
|-------|--------|------|
| `@ApiModelProperty` | `@Schema` | 描述 POJO 字段或类 |

**示例：**
```java
@Schema(description = "用户信息实体")
public class User {
    @Schema(description = "用户ID", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED, example = "张三")
    private String name;
}
```

> ⚠️ 注意：`required = true` 已改为 `requiredMode = RequiredMode.REQUIRED`

---

## 🆕 Knife4j 特有增强注解（仍兼容 Swagger 2 和 OpenAPI 3）

这些注解是 Knife4j 自己扩展的，用于增强文档展示效果：

| 注解 | 作用 | 适用版本 |
|------|------|--------|
| `@ApiOperationSupport(order = 10)` | 控制接口在文档中的排序顺序 | Swagger 2 & OpenAPI 3 |
| `@DynamicParameters` | 动态参数（较少用） | Swagger 2 |
| `@Ignore` | 忽略某个接口（替代 `@Hidden`） | Swagger 2 |

> 🔔 注意：`@ApiOperationSupport` 在 OpenAPI 3 中依然可用，但需确保引入的是 Knife4j 相关 starter。

---

## 📌 总结：如何选择注解？

| 项目技术栈 | 推荐注解体系 | 依赖示例 |
|-----------|-------------|--------|
| Spring Boot ≤ 2.5 + Swagger 2 | 使用 `@Api`, `@ApiOperation`, `@ApiModelProperty` | `knife4j-spring-boot-starter` |
| Spring Boot ≥ 2.6 或 Spring Boot 3 + OpenAPI 3.0 | 使用 `@Tag`, `@Operation`, `@Schema`, `@Parameter` | `knife4j-openapi3-jakarta-spring-boot-starter` |

---