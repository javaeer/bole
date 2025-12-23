/**
 * 将对象转换为 URL 查询字符串（UNIAPP 安全版本）
 */
export function toQueryParams(params: Record<string, any>): string {
  if (!params || typeof params !== 'object') {
    return ''
  }

  // 直接使用 UNIAPP 兼容方案，不依赖 URLSearchParams
  return toQueryParamsUniapp(params)
}

/**
 * UNIAPP 专用查询参数序列化
 */
function toQueryParamsUniapp(params: Record<string, any>): string {
  const parts: string[] = []

  Object.entries(params).forEach(([key, value]) => {
    // 跳过无效值
    if (value === null || value === undefined || value === '') {
      return
    }

    // 处理数组
    if (Array.isArray(value)) {
      value.forEach(item => {
        if (item !== null && item !== undefined && item !== '') {
          parts.push(`${safeEncode(key)}=${safeEncode(String(item))}`)
        }
      })
    } else {
      // 处理基本类型
      parts.push(`${safeEncode(key)}=${safeEncode(String(value))}`)
    }
  })

  const result = parts.join('&')
  return result ? `?${result}` : ''
}

/**
 * 安全编码函数
 */
function safeEncode(str: string): string {
  try {
    return encodeURIComponent(str)
  } catch (error) {
    console.warn('编码失败:', str, error)
    return str
  }
}

/**
 * 构建完整的 URL
 */
export function buildUrl(baseUrl: string, params?: Record<string, any>): string {
  if (!params) return baseUrl

  const queryString = toQueryParams(params)
  if (!queryString) return baseUrl

  // 简单拼接，UNIAPP 环境通常不需要复杂处理
  const separator = baseUrl.includes('?') ? '&' : '?'
  return `${baseUrl}${separator}${queryString.substring(1)}`
}