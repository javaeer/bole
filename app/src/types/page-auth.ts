/**
 * 页面权限配置
 */
export interface PageAuthConfig {
  path: string;      // 页面路径
  auth: boolean;     // 是否需要登录
  strict?: boolean;  // 是否严格模式（重要页面）
  roles?: string[];  // 需要的角色（可选）
}