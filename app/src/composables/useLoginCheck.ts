import { useUserStore } from "@/stores/user";
import { LoginCheckOptions, LoginError } from "@/types/user";
import { computed, ref } from "vue";

/**
 * 登录检查组合式函数
 * 用于按钮级别的登录状态检查
 */
export function useLoginCheck() {
  const userStore = useUserStore();

  // 使用 computed 确保响应式 - 统一使用这种方式
  const isLoggedIn = computed(() => userStore.isLoggedIn);
  const userInfo = computed(() => userStore.userInfo);
  const userStoreRef = ref(userStore); // 将 store 转为 ref 以保持响应性

  /**
   * 构建当前页面完整路径
   */
  const buildCurrentPath = (): string => {
    const pages = getCurrentPages();
    if (pages.length === 0) return '/pages/index/index';

    const page = pages[pages.length - 1];

    // 类型安全检查
    if (!page || typeof page !== 'object') {
      console.warn('获取当前页面失败');
      return '/pages/index/index';
    }

    const path = page.route ? `/${page.route}` : '/pages/index/index';
    const options = page.options || {};

    if (Object.keys(options).length === 0) return path;

    const query = Object.entries(options)
      .map(([k, v]) => {
        // 处理不同类型的值
        if (v === null || v === undefined) return '';
        return `${k}=${encodeURIComponent(String(v))}`;
      })
      .filter(Boolean)
      .join('&');

    return query ? `${path}?${query}` : path;
  };

  /**
   * 默认的未登录处理
   */
  const defaultLoginHandler = (
    options: LoginCheckOptions = {}
  ): Promise<boolean> => { // 返回 Promise<boolean> 表示用户是否确认登录
    const {
      message = '需要登录后才能继续操作',
      confirmText = '去登录',
      cancelText = '取消',
      redirect = true,
      loginPath = '/pages/auth/auth',
      redirectParamKey = 'redirect',
    } = options;

    return new Promise((resolve, reject) => {
      uni.showModal({
        title: '提示',
        content: message,
        confirmText,
        cancelText,
        success: (modalRes) => {
          if (modalRes.confirm && redirect) {
            const currentPath = buildCurrentPath();
            const redirectUrl = encodeURIComponent(currentPath);

            uni.redirectTo({
              url: `${loginPath}?${redirectParamKey}=${redirectUrl}&requireLogin=1`,
              success: () => {
                // 跳转成功，但原操作被中断，所以 reject
                reject(new LoginError('用户未登录，已跳转登录页', 'redirect_failed'));
              },
              fail: (err) => {
                console.error('跳转登录页失败:', err);
                reject(new LoginError('跳转登录页失败', 'redirect_failed'));
              },
            });
          } else if (modalRes.confirm) {
            // 用户确认但不需要重定向
            resolve(true);
          } else {
            // 用户取消
            reject(new LoginError('用户取消登录', 'user_cancelled'));
          }
        },
        fail: (err) => {
          console.error('显示确认框失败:', err);
          reject(new LoginError('显示确认框失败', 'modal_failed'));
        },
      });
    });
  };

  /**
   * 要求登录执行函数
   * 返回一个包装函数，自动检查登录状态
   */
  const requireLogin = <T extends (...args: any[]) => any>(
    action: T,
    options: LoginCheckOptions = {}
  ): ((...args: Parameters<T>) => Promise<Awaited<ReturnType<T>>>) => {
    return async (...args: Parameters<T>): Promise<Awaited<ReturnType<T>>> => {
      // 检查登录状态
      if (isLoggedIn.value) {
        try {
          // 已登录，执行原函数
          const result = action(...args);
          // 如果返回的是 Promise，直接返回，否则包装成 Promise
          return result instanceof Promise ? result : Promise.resolve(result);
        } catch (error) {
          // 捕获同步错误并转换为 Promise rejection
          return Promise.reject(error);
        }
      }

      // 未登录
      if (options.onFail) {
        try {
          options.onFail();
        } catch (error) {
          // 忽略 onFail 中的错误，继续执行默认逻辑
          console.warn('onFail 回调执行失败:', error);
        }
        throw new LoginError('用户未登录，已执行自定义失败处理');
      }

      try {
        // 显示登录提示
        await defaultLoginHandler(options);
        // 如果用户确认登录但不需要重定向，重新检查登录状态
        if (isLoggedIn.value) {
          const result = action(...args);
          return result instanceof Promise ? result : Promise.resolve(result);
        } else {
          throw new LoginError('登录后状态仍未变更');
        }
      } catch (error) {
        // 如果是 LoginError，直接抛出
        if (error instanceof LoginError) {
          throw error;
        }
        // 其他错误包装为 LoginError
        throw new LoginError(error instanceof Error ? error.message : '登录检查失败');
      }
    };
  };

  /**
   * 快速检查并执行（适用于按钮点击）
   */
  const checkAndExecute = async <T>(
    action: () => T | Promise<T>,
    options: LoginCheckOptions = {}
  ): Promise<T> => {
    const wrappedAction = requireLogin(action, options);
    return wrappedAction();
  };

  /**
   * 条件渲染包装器
   * 返回一个对象，包含登录状态和包装后的函数
   */
  const createLoginWrapper = <T extends (...args: any[]) => any>(
    action: T,
    options: LoginCheckOptions = {}
  ) => {
    const wrappedAction = requireLogin(action, options);

    return {
      /** 是否已登录 - 响应式 */
      isLoggedIn,
      /** 包装后的函数（已处理登录检查） */
      execute: wrappedAction,
      /** 原始函数 */
      raw: action,
      /** 立即执行（快捷方式） */
      exec: (...args: Parameters<T>) => wrappedAction(...args),
    };
  };

  /**
   * 获取用户ID（安全方式）- 响应式
   */
  const getUserId = computed(() => {
    if (!isLoggedIn.value || !userInfo.value) {
      return null;
    }
    return userInfo.value.id;
  });

  /**
   * 获取用户信息（安全方式）- 响应式
   */
  const getUserInfoSafe = computed(() => {
    if (!isLoggedIn.value) {
      return null;
    }
    // 返回深拷贝，防止外部直接修改 store 状态
    return JSON.parse(JSON.stringify(userInfo.value));
  });

  /**
   * 检查特定权限
   */
  const hasPermission = (permissionCode: string): boolean => {
    if (!isLoggedIn.value || !userInfo.value?.permissions) {
      return false;
    }
    return userInfo.value.permissions.includes(permissionCode);
  };

  /**
   * 检查角色
   */
  const hasRole = (roleCode: string): boolean => {
    if (!isLoggedIn.value || !userInfo.value?.roles) {
      return false;
    }
    return userInfo.value.roles.includes(roleCode);
  };

  return {
    /** 用户是否已登录 - 响应式 */
    isLoggedIn,
    /** 当前用户信息 - 响应式 */
    userInfo,
    /** 要求登录执行函数 */
    requireLogin,
    /** 快速检查并执行 */
    checkAndExecute,
    /** 创建登录包装器 */
    createLoginWrapper,
    /** 获取用户ID - 响应式 */
    getUserId,
    /** 获取用户信息（安全拷贝）- 响应式 */
    getUserInfoSafe,
    /** 检查权限 */
    hasPermission,
    /** 检查角色 */
    hasRole,
    /** 用户Store引用（响应式） */
    userStore: userStoreRef,
    /** 构建当前路径（非响应式工具函数） */
    buildCurrentPath,
  };
}

// 提供类型导出
export type UseLoginCheckReturn = ReturnType<typeof useLoginCheck>;