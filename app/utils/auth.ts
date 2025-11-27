import { useUserStore } from '@/stores/user'

export class AuthUtil {
  /**
   * 检查登录状态，未登录则跳转到登录页
   */
  static async checkLoginAndRedirect(): Promise<boolean> {
    const userStore = useUserStore()
    
    // 如果 store 中未初始化，尝试从存储加载
    if (!userStore.isLoggedIn) {
      await userStore.initUserState()
    }
    
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      uni.redirectTo({
        url: '/pages/login/login'
      })
      return false
    }
    
    return true
  }
  
  /**
   * 需要登录的页面包装器
   */
  static async requireLogin(callback: () => Promise<void>): Promise<void> {
    const isLoggedIn = await this.checkLoginAndRedirect()
    if (isLoggedIn) {
      await callback()
    }
  }
}