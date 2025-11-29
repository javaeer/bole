export const ResultCode = {
  SUCCESS: 200,
  UNAUTHORIZED: 401, // 未授权/令牌过期
  FORBIDDEN: 403,    // 禁止访问
  NOT_FOUND: 404,    // 资源不存在
  SERVER_ERROR: 500, // 服务器错误
  NETWORK_ERROR: -1, // 网络错误
  // 可以添加其他业务状态码
} as const;

export type ResultCode = typeof ResultCode[keyof typeof ResultCode];