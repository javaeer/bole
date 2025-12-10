// 创建默认配置
import { CONTENT_TYPES, HTTP_METHODS, REQUEST_CONSTANTS } from "@/constants/request";
import { RequestConfig, UploadConfig } from "@/types/request";

const getBaseApi = (): string => {
  // #ifdef H5
  return import.meta.env.VITE_APP_BASE_API as string;
  // #endif
  return import.meta.env.VITE_APP_API_URL as string;
};

export const createDefaultConfig = (): RequestConfig => ({
  baseURL: getBaseApi(),
  method: HTTP_METHODS.GET,
  timeout: REQUEST_CONSTANTS.TIMEOUT,
  retryCount: REQUEST_CONSTANTS.RETRY_COUNT,
  loading: true,
  loadingText: "加载中...",
  showError: true,
  skipAuth: false,
  responseType: "json",
  headers: {
    "Content-Type": CONTENT_TYPES.JSON,
  },
});

export const createDefaultUploadConfig = (): UploadConfig => ({
  baseURL: getBaseApi(),
  method: HTTP_METHODS.POST,
  name: "file",
  loading: true,
  loadingText: "加载中...",
  showError: true,
  skipAuth: false,
  responseType: "json",
  isFormData: true,
  headers: {
    "Content-Type": CONTENT_TYPES.MULTIPART,
  },
});

// 通用的合并配置函数
const mergeConfigHelper = <T extends Record<string, any>>(
  defaultConfig: T,
  userConfig: Partial<T>
): T => {
  const merged = { ...defaultConfig };

  // 先处理 headers（深度合并）
  if (userConfig.headers) {
    merged.headers = { ...defaultConfig.headers, ...userConfig.headers };
  }

  // 合并其他配置
  Object.keys(userConfig).forEach(key => {
    const typedKey = key as keyof T;

    // 跳过已经处理的 headers
    if (typedKey === "headers") return;

    if (userConfig[typedKey] !== undefined) {
      // 对于简单值直接覆盖，对于对象可以继续深度合并
      if (typeof userConfig[typedKey] === "object" && userConfig[typedKey] !== null) {
        merged[typedKey] = {
          ...(merged[typedKey] as object),
          ...(userConfig[typedKey] as object)
        };
      } else {
        merged[typedKey] = userConfig[typedKey] as T[keyof T];
      }
    }
  });

  return merged;
};

// 合并配置（推荐：使用泛型辅助函数）
export const mergeConfig = (config: Partial<RequestConfig>): RequestConfig => {
  const defaultConfig = createDefaultConfig();
  return mergeConfigHelper(defaultConfig, config);
};

// 合并上传配置（推荐：使用泛型辅助函数）
export const mergeUploadConfig = (config: Partial<UploadConfig>): UploadConfig => {
  const defaultConfig = createDefaultUploadConfig();
  return mergeConfigHelper(defaultConfig, config);
};

// 或者更简洁的方式，直接使用浅合并（如果不需要深度合并对象属性）
export const mergeConfigSimple = (config: Partial<RequestConfig>): RequestConfig => {
  const defaultConfig = createDefaultConfig();
  return {
    ...defaultConfig,
    ...config,
    headers: {
      ...defaultConfig.headers,
      ...config.headers,
    },
  };
};

export const mergeUploadConfigSimple = (config: Partial<UploadConfig>): UploadConfig => {
  const defaultConfig = createDefaultUploadConfig();
  return {
    ...defaultConfig,
    ...config,
    headers: {
      ...defaultConfig.headers,
      ...config.headers,
    },
  };
};