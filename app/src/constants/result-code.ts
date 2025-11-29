export const ResultCode = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  NETWORK_ERROR: -1,
  // 可以添加其他业务状态码
} as const;

export type ResultCode = typeof ResultCode[keyof typeof ResultCode];