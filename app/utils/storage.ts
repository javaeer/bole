export class StorageUtil {
  private static accessTokenKey = 'access_token'
  private static refreshTokenKey = 'refresh_token'
  private static userInfoKey = 'user_info'
  private static tokenTypeKey = 'token_type'
  private static expiresInKey = 'expires_in'

  /**
   * 保存登录信息
   */
  static async saveLoginData(loginResult: any): Promise<void> {
    try {
      uni.setStorageSync(this.accessTokenKey, loginResult.accessToken)
      uni.setStorageSync(this.refreshTokenKey, loginResult.refreshToken)
      uni.setStorageSync(this.tokenTypeKey, loginResult.tokenType)
      uni.setStorageSync(this.expiresInKey, loginResult.expiresIn)
      uni.setStorageSync(this.userInfoKey, loginResult.userInfo)
    } catch (error) {
      console.error('保存登录信息失败:', error)
    }
  }

  /**
   * 获取 access token
   */
  static async getAccessToken(): Promise<string | null> {
    try {
      return uni.getStorageSync(this.accessTokenKey)
    } catch (error) {
      console.error('获取 access token 失败:', error)
      return null
    }
  }

  /**
   * 获取 refresh token
   */
  static async getRefreshToken(): Promise<string | null> {
    try {
      return uni.getStorageSync(this.refreshTokenKey)
    } catch (error) {
      console.error('获取 refresh token 失败:', error)
      return null
    }
  }

  /**
   * 获取 token 类型
   */
  static async getTokenType(): Promise<string> {
    try {
      return uni.getStorageSync(this.tokenTypeKey) || 'Bearer'
    } catch (error) {
      console.error('获取 token 类型失败:', error)
      return 'Bearer'
    }
  }

  /**
   * 获取过期时间
   */
  static async getExpiresIn(): Promise<number> {
    try {
      return uni.getStorageSync(this.expiresInKey) || 0
    } catch (error) {
      console.error('获取过期时间失败:', error)
      return 0
    }
  }

  /**
   * 获取用户信息
   */
  static async getUserInfo(): Promise<UserInfo | null> {
    try {
      return uni.getStorageSync(this.userInfoKey)
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  /**
   * 更新用户信息
   */
  static async updateUserInfo(userInfo: Partial<UserInfo>): Promise<void> {
    try {
      const currentUserInfo = await this.getUserInfo()
      if (currentUserInfo) {
        const updatedUserInfo = { ...currentUserInfo, ...userInfo }
        uni.setStorageSync(this.userInfoKey, updatedUserInfo)
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
    }
  }

  /**
   * 清除所有认证信息
   */
  static async clearAuthData(): Promise<void> {
    try {
      uni.removeStorageSync(this.accessTokenKey)
      uni.removeStorageSync(this.refreshTokenKey)
      uni.removeStorageSync(this.tokenTypeKey)
      uni.removeStorageSync(this.expiresInKey)
      uni.removeStorageSync(this.userInfoKey)
    } catch (error) {
      console.error('清除认证信息失败:', error)
    }
  }

  /**
   * 检查是否已登录
   */
  static async isLoggedIn(): Promise<boolean> {
    const token = await this.getAccessToken()
    if (!token) return false

    // 检查 token 是否过期（简单检查，实际应该验证 token 有效性）
    const expiresIn = await this.getExpiresIn()
    if (expiresIn > 0) {
      // 这里可以添加更复杂的过期检查逻辑
      return true
    }

    return !!token
  }
}