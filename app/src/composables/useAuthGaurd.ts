import { getToken } from "@/utils/store";
import { getPageConfig, isAuthRequired } from "@/utils/page-auth";
import { PageAuthConfig } from "@/types/page-auth";

/**
 * 认证守卫配置选项
 */
interface UseAuthGuardOptions {
  /**
   * 是否立即执行检查（默认：true）
   * 设置为 false 时，需要手动调用 check() 方法
   */
  immediate?: boolean;

  /**
   * 检查时机
   * - 'beforeMount': 组件挂载前（默认，推荐）
   * - 'onShow': 页面显示时
   */
  checkTiming?: "beforeMount" | "onShow";

  /**
   * 自定义失败处理
   * 如果提供，将覆盖默认的跳转行为
   */
  onAuthFail?: (config: PageAuthConfig, currentPath: string) => void;

  /**
   * 严格模式
   * 为 true 时，会进行额外的权限检查
   */
  strict?: boolean;

  /** 自定义重定向路径 */
  loginPath?: string;
}

/**
 * 页面认证守卫组合式函数
 * 适用于 Vue 3 组合式 API
 */
export function useAuthGuard(options: UseAuthGuardOptions = {}) {
  const {
    immediate = true,
    checkTiming = "beforeMount",
    onAuthFail,
    strict = false,
    loginPath = "/pages/auth/auth",
  } = options;

  const isChecking = ref(false);
  const isAuthenticated = computed(() => !!getToken());

  // 获取当前页面路径
  const getCurrentPagePath = (): string => {
    const pages = getCurrentPages();
    if (pages.length === 0) return "";

    const currentPage = pages[pages.length - 1];
    return `/${currentPage.route}`;
  };

  // 构建完整路径（包含参数）
  const buildFullPath = (): string => {
    const pages = getCurrentPages();
    if (pages.length === 0) return "";

    const currentPage = pages[pages.length - 1];
    let path = `/${currentPage.route}`;

    const options = currentPage.options || {};
    if (Object.keys(options).length > 0) {
      const queryString = Object.entries(options)
        .map(([key, value]) => `${key}=${encodeURIComponent(String(value))}`)
        .join("&");
      path += `?${queryString}`;
    }

    return path;
  };

  // 执行认证检查
  const check = (): boolean => {
    isChecking.value = true;

    try {
      const currentPath = getCurrentPagePath();
      if (!currentPath) return true;

      // 检查是否需要认证
      if (!isAuthRequired(currentPath)) {
        return true;
      }

      const pageConfig = getPageConfig(currentPath);
      const hasAuth = isAuthenticated.value;

      // 认证失败的情况
      if (!hasAuth) {
        console.warn(`[useAuthGuard] 未授权访问需要登录的页面: ${currentPath}`);

        if (onAuthFail) {
          onAuthFail(pageConfig!, currentPath);
        } else {
          // 默认行为：跳转到登录页
          const redirectUrl = encodeURIComponent(buildFullPath());
          uni.redirectTo({
            url: `${loginPath}?redirect=${redirectUrl}&from=${encodeURIComponent(currentPath)}`,
          });
        }
        return false;
      }

      // 严格模式下的额外检查
      if (strict || pageConfig?.strict) {
        // 可以在这里添加角色检查、权限验证等
        // 例如：if (!hasRequiredRole(pageConfig.roles)) { ... }

        // 检查Token是否即将过期
        if (tokenService.isTokenExpiringSoon.value) {
          console.log("[useAuthGuard] Token即将过期，尝试静默刷新...");
          // 可以在这里触发静默刷新
        }
      }

      return true;
    } finally {
      isChecking.value = false;
    }
  };

  // 根据配置的时机执行检查
  if (immediate) {
    if (checkTiming === "beforeMount") {
      onBeforeMount(() => {
        check();
      });
    } else if (checkTiming === "onShow") {
      // 需要在页面中手动调用 onShow 生命周期
      // 或者使用 uni.onAppShow 全局监听
    }
  }

  // 提供手动检查方法
  const manualCheck = () => check();

  return {
    isChecking,
    isAuthenticated,
    check: manualCheck,
    getCurrentPagePath,
  };
}

/**
 * 严格模式守卫（用于重要页面）
 */
export function useStrictAuthGuard(options?: UseAuthGuardOptions) {
  return useAuthGuard({
    strict: true,
    onAuthFail: (config, path) => {
      // 记录安全日志
      console.error(`[严格守卫] 非法访问重要页面: ${path}`, {
        config,
        timestamp: new Date().toISOString(),
        userAgent: navigator.userAgent,
      });

      // 跳转到登录页，使用 reLaunch 清空页面栈
      const redirectUrl = encodeURIComponent(path);
      uni.reLaunch({
        url: `/pages/auth/auth?redirect=${redirectUrl}&strict=1`,
      });

      // 可以上报到监控系统
      // reportSecurityIncident({ type: 'unauthorized_access', path });
    },
    ...options,
  });
}