import { PageAuthConfig } from "@/types/page-auth";
import { PAGE_AUTH_CONFIG } from "@/constants/page-auth";

/**
 * 判断页面是否需要认证
 * @param path
 */
export function isAuthRequired(path: string): boolean {
  // 精确匹配
  const exactMatch = PAGE_AUTH_CONFIG.find(
    config => config.path === path,
  );
  if (exactMatch) return exactMatch.auth;

  // 通配符匹配
  for (const config of PAGE_AUTH_CONFIG) {
    if (config.path.includes("*")) {
      const pattern = config.path.replace("*", ".*");
      if (new RegExp(`^${pattern}$`).test(path)) {
        return config.auth;
      }
    }
  }

  // 默认：未配置的页面不需要认证（可根据需求调整）
  return false;
}

/**
 * 获取页面配置
 * @param path
 */
export function getPageConfig(path: string): PageAuthConfig | null {
  // 精确匹配
  // return PAGE_AUTH_CONFIG.find(config => config.path === path) || null;

  // 1. 精确匹配
  const exactMatch = PAGE_AUTH_CONFIG.find(config => config.path === path)
  if (exactMatch) return exactMatch

  // 2. 通配符匹配
  for (const config of PAGE_AUTH_CONFIG) {
    if (config.path.includes('*')) {
      const pattern = '^' + config.path.replace(/\*/g, '.*') + '$'
      const regex = new RegExp(pattern)
      if (regex.test(path)) {
        return config
      }
    }
  }
  return null
}

/**
 * 检查用户是否有权限访问指定页面
 * @param path 页面路径
 * @param userPermissions 用户权限列表
 * @param userRoles 用户角色列表
 * @returns 是否有权限
 */
export function checkPageAccess(
  path: string,
  userPermissions: string[] = [],
  userRoles: string[] = []
): { allowed: boolean; reason?: string } {
  const config = getPageConfig(path)

  if (!config) {
    // 未配置的页面默认允许访问
    return { allowed: true }
  }

  if (!config.auth) {
    // 不需要认证的页面
    return { allowed: true }
  }

  // 检查角色权限
  if (config.roles && config.roles.length > 0) {
    const hasRequiredRole = config.roles.some(role => userRoles.includes(role))
    if (!hasRequiredRole) {
      return {
        allowed: false,
        reason: `需要以下角色之一: ${config.roles.join(', ')}`
      }
    }
  }

  // 检查具体权限
  if (config.permissions && config.permissions.length > 0) {
    const hasRequiredPermission = config.permissions.some(
      permission => userPermissions.includes(permission)
    )
    if (!hasRequiredPermission) {
      return {
        allowed: false,
        reason: `需要以下权限之一: ${config.permissions.join(', ')}`
      }
    }
  }

  return { allowed: true }
}

/**
 * 获取所有需要登录的页面路径
 * @returns 需要登录的页面路径数组
 */
export function getAuthRequiredPages(): string[] {
  return PAGE_AUTH_CONFIG
    .filter(config => config.auth)
    .map(config => config.path)
    .filter(path => !path.includes('*')) // 过滤掉通配符路径
}