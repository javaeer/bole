import { useUserStore } from "@/stores/user";
import { LoginCheckOptions } from "@/types/user";



/**
 * 登录检查组合式函数
 * 用于按钮级别的登录状态检查
 */
export function useLoginCheck() {
  const userStore = useUserStore()

  /**
   * 构建当前页面完整路径
   */
  const buildCurrentPath = (): string => {
    const pages = getCurrentPages()
    if (pages.length === 0) return ''

    const page = pages[pages.length - 1]
    const path = `/${page.route}`
    const options = page.options || {}

    if (Object.keys(options).length === 0) return path

    const query = Object.entries(options)
      .map(([k, v]) => `${k}=${encodeURIComponent(String(v))}`)
      .join('&')

    return `${path}?${query}`
  }

  /**
   * 默认的未登录处理
   */
  const defaultLoginHandler = (options: LoginCheckOptions = {}) => {
    const {
      message = '需要登录后才能继续操作',
      confirmText = '去登录',
      cancelText = '取消',
      redirect = true,
      loginPath = '/pages/login/index'
    } = options

    return new Promise<void>((resolve, reject) => {
      uni.showModal({
        title: '提示',
        content: message,
        confirmText,
        cancelText,
        success: (modalRes) => {
          if (modalRes.confirm && redirect) {
            const currentPath = buildCurrentPath()
            const redirectUrl = encodeURIComponent(currentPath || '/pages/index/index')

            uni.redirectTo({
              url: `${loginPath}?redirect=${redirectUrl}&requireLogin=1`,
              success: () => {
                reject(new Error('用户未登录，已跳转登录页'))
              },
              fail: () => {
                reject(new Error('跳转登录页失败'))
              }
            })
          } else {
            reject(new Error('用户取消登录'))
          }
        },
        fail: () => {
          reject(new Error('显示确认框失败'))
        }
      })
    })
  }

  /**
   * 要求登录执行函数
   */
  const requireLogin = <T extends (...args: any[]) => any>(
    action: T,
    options: LoginCheckOptions = {}
  ): ((...args: Parameters<T>) => ReturnType<T> | Promise<void>) => {
    return (...args: Parameters<T>) => {
      if (userStore.isLoggedIn) {
        // 已登录，直接执行原函数
        return action(...args)
      } else {
        // 未登录，执行失败处理
        if (options.onFail) {
          options.onFail()
          return Promise.reject(new Error('用户未登录'))
        }

        // 默认处理：显示登录提示
        return defaultLoginHandler(options) as any
      }
    }
  }

  /**
   * 快速检查并执行（适用于按钮点击）
   */
  const checkAndExecute = async <T>(
    action: () => T | Promise<T>,
    options: LoginCheckOptions = {}
  ): Promise<T | void> => {
    if (userStore.isLoggedIn) {
      return action()
    } else {
      if (options.onFail) {
        options.onFail()
        return
      }

      await defaultLoginHandler(options)
      throw new Error('操作中断：需要登录')
    }
  }

  /**
   * 条件渲染包装器
   * 返回一个对象，包含登录状态和包装后的函数
   */
  const createLoginWrapper = <T extends (...args: any[]) => any>(
    action: T,
    options: LoginCheckOptions = {}
  ) => {
    const wrappedAction = requireLogin(action, options)

    return {
      /** 是否已登录 */
      isLoggedIn: userStore.isLoggedIn,
      /** 包装后的函数（已处理登录检查） */
      execute: wrappedAction,
      /** 原始函数 */
      raw: action
    }
  }

  /**
   * 获取用户ID（安全方式）
   */
  const getUserId = (): string | number | null => {
    if (!userStore.isLoggedIn || !userStore.userInfo) {
      return null
    }
    return userStore.userInfo.id
  }

  /**
   * 获取用户信息（安全方式）
   */
  const getUserInfo = () => {
    if (!userStore.isLoggedIn) {
      return null
    }
    return { ...userStore.userInfo }
  }

  return {
    /** 用户是否已登录 */
    isLoggedIn: userStore.isLoggedIn,
    /** 要求登录执行函数 */
    requireLogin,
    /** 快速检查并执行 */
    checkAndExecute,
    /** 创建登录包装器 */
    createLoginWrapper,
    /** 获取用户ID */
    getUserId,
    /** 获取用户信息 */
    getUserInfo,
    /** 用户Store引用 */
    userStore
  }
}

/**
 * 创建特定场景的登录检查
 */
export function createSceneLoginCheck(scene: string) {
  const messages: Record<string, string> = {
    buy: '登录后才能购买商品',
    comment: '登录后才能发表评论',
    favorite: '登录后才能收藏',
    follow: '登录后才能关注',
    order: '登录后才能查看订单',
    payment: '登录后才能进行支付',
    profile: '登录后才能查看个人资料'
  }

  return useLoginCheck().requireLogin((action: Function) => {
    return action()
  }, {
    message: messages[scene] || '需要登录后才能继续操作'
  })
}

// 类型定义
export type LoginWrapper<T> = {
  isLoggedIn: boolean
  execute: T
  raw: T
}