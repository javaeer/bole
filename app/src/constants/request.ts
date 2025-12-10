// HTTP方法
export const HTTP_METHODS = {
  GET: "GET",
  POST: "POST",
  PUT: "PUT",
  DELETE: "DELETE",
  PATCH: "PATCH",
  OPTIONS: "OPTIONS",
  HEAD: "HEAD",
} as const;

// 请求配置常量
export const REQUEST_CONSTANTS = {
  TIMEOUT: 10000,// 默认超时时间（毫秒）
  BASE_URL: "",
  RETRY_COUNT: 2,// 默认重试次数
  MAX_RETRY_COUNT: 3,// 最大重试次数
  RETRY_DELAY: 1000,
} as const;

// 内容类型
export const CONTENT_TYPES = {
  JSON: "application/json",
  FORM: "application/x-www-form-urlencoded",
  MULTIPART: "multipart/form-data",
  TEXT: "text/plain",
} as const;

// 上传文件类型
export const UPLOAD_FILE_TYPES = {
  IMAGE: "image",
  VIDEO: "video",
  AUDIO: "audio",
  FILE: "file",
} as const;