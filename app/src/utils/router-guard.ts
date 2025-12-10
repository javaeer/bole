import { getToken } from "@/utils/store";
import { isAuthRequired } from "@/utils/page-auth";

// 平台检测
const isApp = typeof uni !== "undefined" && uni.getSystemInfoSync().platform === "app";
const isH5 = typeof window !== "undefined" && !isApp;

/**
 * 设置全局路由守卫
 * 注意：仅支持 App 和 H5 平台
 */
export function setupGlobalRouterGuard() {
  // #ifdef APP-PLUS || H5
  if (!isApp && !isH5) return;

  const createInterceptor = (method: string) => {
    return (args: any) => {
      const url = args.url.split("?")[0];

      if (isAuthRequired(url) && !getToken()) {
        console.log(`[路由守卫] 拦截 ${method} 到 ${url}`);

        const redirectUrl = encodeURIComponent(args.url);
        uni.redirectTo({
          url: `/pages/auth/auth?redirect=${redirectUrl}`,
        });
        return false;
      }
      return true;
    };
  };

  // 拦截普通跳转
  ["navigateTo", "redirectTo", "reLaunch"].forEach(method => {
    uni.addInterceptor(method as any, {
      invoke: createInterceptor(method),
    });
  });

  // 特殊处理 tabBar 跳转
  uni.addInterceptor("switchTab", {
    invoke: (args: any) => {
      const url = args.url.split("?")[0];

      if (isAuthRequired(url) && !getToken()) {
        const redirectUrl = encodeURIComponent(url);
        uni.reLaunch({
          url: `/pages/auth/auth?redirect=${redirectUrl}`,
        });
        return false;
      }
      return true;
    },
  });

  console.log("全局路由守卫已启用 (App/H5)");
  // #endif
}

/**
 * 检查启动时的页面
 */
export function checkLaunchPage() {
  // #ifdef APP-PLUS || H5
  setTimeout(() => {
    const pages = getCurrentPages();
    if (pages.length === 0) return;

    const currentPage = pages[pages.length - 1];
    const currentPath = `/${currentPage.route}`;

    if (isAuthRequired(currentPath) && !getToken()) {
      uni.redirectTo({
        url: `/pages/auth/auth?redirect=${encodeURIComponent(currentPath)}`,
      });
    }
  }, 100);
  console.log("启动页面检查完成")
  // #endif
}