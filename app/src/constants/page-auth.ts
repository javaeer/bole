// 页面认证配置表 - 集中管理所有页面的权限要求
import { PageAuthConfig } from "@/types/page-auth";

export const PAGE_AUTH_CONFIG: PageAuthConfig[] = [
  // 公开页面
  { path: "/pages/index/index", auth: false },
  { path: "/pages/auth/auth", auth: false },
  { path: "/pages/register/index", auth: false },
  { path: "/pages/product/list", auth: false },

  // 需登录页面
  { path: "/pages/follow/follow", auth: true },
  { path: "/pages/message/message", auth: true },
  { path: "/pages/profile/profile", auth: true },

  // 重要页面（严格模式）
  { path: "/pages/payment/confirm", auth: true, strict: true },
  { path: "/pages/user/security", auth: true, strict: true },
  { path: "/pages/wallet/withdraw", auth: true, strict: true },

  // 支持通配符路径
  { path: "/pages/admin/*", auth: true, roles: ["admin"] },
] as const;

export type PAGE_AUTH_CONFIG = typeof PAGE_AUTH_CONFIG[keyof typeof PAGE_AUTH_CONFIG];