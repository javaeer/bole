import { PageAuthConfig } from "@/types/page-auth";
import { PAGE_AUTH_CONFIG } from "@/constants/page-auth";

// 判断页面是否需要认证
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

// 获取页面配置
export function getPageConfig(path: string): PageAuthConfig | null {
  // 精确匹配
  return PAGE_AUTH_CONFIG.find(config => config.path === path) || null;
}