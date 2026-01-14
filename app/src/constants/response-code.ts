export const ResponseCode = {
  SUCCESS: 200,
  CREATED: 201,
  ACCEPTED: 202,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401, // 未授权/令牌过期
  FORBIDDEN: 403,    // 禁止访问
  NOT_FOUND: 404,    // 资源不存在
  METHOD_NOT_ALLOWED: 405,
  REQUEST_TIMEOUT: 408,
  CONFLICT: 409,
  GONE: 410,
  INTERNAL_SERVER_ERROR: 500, // 服务器错误
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504,
  NETWORK_ERROR: -1, // 网络错误
  // 可以添加其他业务状态码


} as const;

export type ResponseCode = typeof ResponseCode[keyof typeof ResponseCode];