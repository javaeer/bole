import type { 
  BaseResponse, 
  PageResponse, 
  BasePageResponse 
} from '@/types/api'

export class ResponseUtil {
  /**
   * 创建成功响应
   */
  static createSuccess<T = any>(data: T): BaseResponse<T> {
    return {
      code: 200,
      message: '操作成功',
      data,
      timestamp: Date.now()
    }
  }

  /**
   * 创建分页成功响应
   */
  static createPageSuccess<T = any>(
    records: T[],
    total: number,
    current: number,
    size: number
  ): BasePageResponse<T> {
    const pages = Math.ceil(total / size)
    
    return {
      code: 200,
      message: '操作成功',
      data: {
        records,
        total,
        size,
        current,
        pages
      },
      timestamp: Date.now()
    }
  }

  /**
   * 创建错误响应
   */
  static createError(
    message: string = '操作失败',
    code: number = 500
  ): BaseResponse<null> {
    return {
      code,
      message,
      data: null,
      timestamp: Date.now()
    }
  }

  /**
   * 检查响应是否成功
   */
  static isSuccess<T>(response: BaseResponse<T>): boolean {
    return response.code === 200
  }

  /**
   * 提取响应数据
   */
  static extractData<T>(response: BaseResponse<T>): T {
    if (!this.isSuccess(response)) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }

  /**
   * 提取分页数据
   */
  static extractPageData<T>(response: BasePageResponse<T>): PageResponse<T> {
    if (!this.isSuccess(response)) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }

  /**
   * 处理列表数据（兼容非分页和分页）
   */
  static processListData<T>(
    data: T[] | PageResponse<T>
  ): { list: T[]; total?: number; hasMore?: boolean } {
    // 如果是分页数据
    if (typeof data === 'object' && 'records' in data) {
      const pageData = data as PageResponse<T>
      return {
        list: pageData.records,
        total: pageData.total,
        hasMore: pageData.current < pageData.pages
      }
    }
    
    // 如果是普通数组
    return {
      list: data as T[],
      total: (data as T[]).length,
      hasMore: false
    }
  }
}