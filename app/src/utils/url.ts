/**
 * 将对象转换为 URL 查询字符串
 */
export function toQueryParams(params: Record<string, any>): string {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== "") {
      if (Array.isArray(value)) {
        value.forEach(item => searchParams.append(key, String(item)));
      } else {
        searchParams.append(key, String(value));
      }
    }
  });

  const result = searchParams.toString();
  return result ? `?${result}` : "";
}

/**
 * 构建完整的 URL（包含查询参数）
 */
export function buildUrl(baseUrl: string, params?: Record<string, any>): string {
  if (!params) return baseUrl;
  return `${baseUrl}${toQueryParams(params)}`;
}