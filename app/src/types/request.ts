import { HTTP_METHODS, UPLOAD_FILE_TYPES } from "@/constants/request";

export interface RequestConfig {
  /**请求服务地址*/
  baseURL?: string;
  /**请求接口地址*/
  url?: string;
  /**请求方法*/
  method?: HTTP_METHODS.GET | HTTP_METHODS.POST | HTTP_METHODS.PUT | HTTP_METHODS.DELETE | HTTP_METHODS.OPTIONS | HTTP_METHODS.HEAD;
  /**请求头内容*/
  header?: Record<string, string>;
  /**请求参数 query */
  params?: Record<string, any>;
  /**请求体*/
  data?: any;
  /**请求超时时间*/
  timeout?: number;
  /**请求的接口是否强制签名*/
  skipAuth?: boolean;
  /**是否文件上传*/
  isFormData?: boolean;
  /**是否显示加载按钮*/
  loading?: boolean;
  /** 加载提示文字 */
  loadingText?: string;
  /**是否显示错误信息*/
  showError?: boolean;
  /** 重试次数 */
  retryCount?: number;
  /** 当前重试次数 */
  _retryCount?: number;
  /** 取消 */
  signal?: AbortSignal;
}

// 请求选项（外部使用）
export interface RequestOptions extends Omit<RequestConfig, "baseURL"> {
  url: string;
}

// 上传配置
export interface UploadConfig extends RequestConfig {
  filePath: string;
  fileType?: UPLOAD_FILE_TYPES.IMAGE | UPLOAD_FILE_TYPES.VIDEO | UPLOAD_FILE_TYPES.AUDIO | UPLOAD_FILE_TYPES.FILE;
  name?: string;
  formData?: Record<string, any>;
  showProgress?: boolean;
  onProgressUpdate?: (event: UploadProgressEvent) => void;
  taskId?: string;
  stopOnError?: boolean;
}

// 上传文件请求选项（外部使用）
export interface UploadOptions {
  // 必需参数
  filePath: string;

  // 可选参数
  name?: string;
  formData?: Record<string, any>;
  headers?: Record<string, string>;
  params?: Record<string, any>;
  showProgress?: boolean;
  onProgress?: (progress: number) => void;
  signal?: AbortSignal;

  // 业务特定参数
  compress?: boolean;
  maxWidth?: number;
  maxHeight?: number;
  quality?: number;
}

export interface UploadProgressEvent {
  progress: number;
  totalBytesSent: number;
  totalBytesExpectedToSend: number;
  taskId: string;
  config: UploadConfig;
}
