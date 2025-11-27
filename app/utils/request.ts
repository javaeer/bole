import { EnvConfig } from './env'
import { StorageUtil } from './storage'
import { AuthApi } from '@/apis/auth'
import { 
  BaseResponse, 
  PageResponse, 
  RequestOptions, 
  RequestError, // 改为直接导入，而不是 import type
  BasePageResponse
} from '@/types/api'

export class RequestUtil {
  private static baseURL: string = EnvConfig.getBaseURL()
  private static apiPrefix: string = EnvConfig.getApiPrefix()
  
  /**
   * 通用请求方法
   */
  static async request<T = any>(
    url: string,
    options: RequestOptions = {}
  ): Promise<T> {
    const {
      method = 'GET',
      header = {},
      data,
      timeout = 10000,
      showLoading = true,
      showError = true,
      retryOnTokenExpired = true
    } = options

    if (showLoading) {
      uni.showLoading({ title: '加载中...', mask: true })
    }

    try {
      const requestHeader = await this.buildRequestHeader(header)
      const fullURL = this.buildFullURL(url)

      // 修复：明确指定返回类型
      const originalRequest = async (): Promise<any> => {
        const response = await uni.request({
          url: fullURL,
          method,
          data,
          header: requestHeader as any, // 使用类型断言解决 UTSJSONObject 问题
          timeout
        })
        return response
      }

      const response = await originalRequest()
      
      return await this.handleEnhancedResponse<T>(
        response, 
        showError, 
        retryOnTokenExpired ? originalRequest : undefined
      )
    } catch (error) {
      return this.handleError(error, showError)
    } finally {
      if (showLoading) {
        uni.hideLoading()
      }
    }
  }

  /**
   * GET 请求
   */
  static async get<T = any>(
    url: string,
    params?: Record<string, any>,
    options: RequestOptions = {}
  ): Promise<T> {
    let finalUrl = url
    if (params && Object.keys(params).length > 0) {
      const queryString = Object.keys(params)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
      finalUrl += `?${queryString}`
    }

    return this.request<T>(finalUrl, { ...options, method: 'GET' })
  }

  /**
   * POST 请求
   */
  static async post<T = any>(
    url: string,
    data?: any,
    options: RequestOptions = {}
  ): Promise<T> {
    return this.request<T>(url, { ...options, method: 'POST', data })
  }

  /**
   * PUT 请求
   */
  static async put<T = any>(
    url: string,
    data?: any,
    options: RequestOptions = {}
  ): Promise<T> {
    return this.request<T>(url, { ...options, method: 'PUT', data })
  }

  /**
   * DELETE 请求
   */
  static async delete<T = any>(
    url: string,
    options: RequestOptions = {}
  ): Promise<T> {
    return this.request<T>(url, { ...options, method: 'DELETE' })
  }

  /**
   * 构建请求头 - 修复类型问题
   */
  private static async buildRequestHeader(customHeader: Record<string, string> = {}): Promise<Record<string, string>> {
    const token = await StorageUtil.getAccessToken()
    const tokenType = await StorageUtil.getTokenType()
    
    // 修复：确保 Authorization 不会是 undefined
    const authHeader: Record<string, string> = token ? { 
      'Authorization': `${tokenType} ${token}` 
    } : {}

    return {
      'Content-Type': 'application/json',
      ...authHeader,
      ...customHeader
    }
  }

  /**
   * 处理 token 过期
   */
  private static async handleTokenExpired(): Promise<boolean> {
    const refreshToken = await StorageUtil.getRefreshToken()
    if (!refreshToken) {
      this.redirectToLogin()
      return false
    }

    try {
      const newLoginResult = await AuthApi.refreshToken(refreshToken)
      await StorageUtil.saveLoginData(newLoginResult)
      return true
    } catch (error) {
      console.error('刷新 token 失败:', error)
      this.redirectToLogin()
      return false
    }
  }

   /**
    * 跳转到登录页 - 使用 UniApp 原生路由
    */
   private static redirectToLogin(): void {
     StorageUtil.clearAuthData()
     
     // 使用 UniApp 原生路由跳转，而不是动态导入
     const currentPages = getCurrentPages()
     if (currentPages.length === 0) {
       // 没有页面栈，直接跳转到登录页
       uni.reLaunch({
         url: '/pages/login/login'
       })
     } else {
       // 检查当前页面是否是登录页，避免重复跳转
       const currentRoute = currentPages[currentPages.length - 1].route
       if (!currentRoute.includes('login')) {
         uni.redirectTo({
           url: '/pages/login/login'
         })
       }
     }
   }

  /**
   * 增强的响应处理方法
   */
  private static async handleEnhancedResponse<T>(
    response: any,
    showError: boolean,
    originalRequest?: () => Promise<any>
  ): Promise<T> {
    const { statusCode, data: responseData } = response

    if (statusCode === 200) {
      const baseResponse = responseData as BaseResponse<T>
      
      if (baseResponse.code === 200) {
        return baseResponse.data
      } 
      else if (baseResponse.code === 401) {
        const refreshSuccess = await this.handleTokenExpired()
        if (refreshSuccess && originalRequest) {
          const retryResponse = await originalRequest()
          return await this.handleEnhancedResponse<T>(retryResponse, showError)
        }
        // 修复：直接使用 Error 而不是 RequestError
        throw new Error('登录已过期，请重新登录')
      }
      else {
        // 修复：使用 Error 而不是 RequestError
        const error = new Error(baseResponse.message)
        
        if (showError) {
          this.showErrorToast(baseResponse.message)
        }
        
        throw error
      }
    } 
    else {
      // 修复：使用 Error 而不是 RequestError
      const error = new Error(`HTTP错误: ${statusCode}`)
      
      if (showError) {
        this.showErrorToast(`网络错误: ${statusCode}`)
      }
      
      throw error
    }
  }

  /**
   * 处理错误
   */
  private static handleError(error: any, showError: boolean): Promise<never> {
    console.error('请求错误:', error)
    
    let errorMessage = '网络请求失败'
    
    // 修复：不再使用 instanceof RequestError
    if (error.message) {
      errorMessage = error.message
    } else if (error.errMsg) {
      errorMessage = error.errMsg
    }

    if (showError) {
      this.showErrorToast(errorMessage)
    }

    return Promise.reject(error)
  }

  /**
   * 显示错误提示
   */
  private static showErrorToast(message: string): void {
    uni.showToast({
      title: message,
      icon: 'none',
      duration: 3000
    })
  }

  /**
   * 构建完整 URL
   */
  private static buildFullURL(url: string): string {
    if (url.startsWith('http')) {
      return url
    }

    const cleanUrl = url.replace(/^\//, '')
    const cleanPrefix = this.apiPrefix.replace(/^\//, '').replace(/\/$/, '')
    
    return `${this.baseURL}/${cleanPrefix}/${cleanUrl}`
  }

  /**
   * 分页请求方法
   */
  static async pageRequest<T = any>(
    url: string,
    params: Record<string, any> = {},
    options: RequestOptions = {}
  ): Promise<PageResponse<T>> {
    return this.get<PageResponse<T>>(url, params, options)
  }
}