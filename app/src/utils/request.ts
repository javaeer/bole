import { buildUrl } from "@/utils/url";
import { RequestConfig } from "@/types/request";
import { interceptors } from "@/utils/interceptors";
import { mergeConfig } from "@/utils/request-config";
import { errorHandles } from "@/utils/error-handles";

class Request {

  async request<T = any>(config: RequestConfig): Promise<T> {
    return new Promise(async (resolve, reject) => {
      try {
        // 合并配置
        const mergedConfig: RequestConfig = mergeConfig(config);

        // 请求拦截，自动刷新token
        const finalConfig = await interceptors.requestInterceptor(mergedConfig);

        // 构建完整 URL（包含查询参数）
        const url = buildUrl(`${finalConfig.baseURL}${finalConfig.url}`, finalConfig.params);

        const requestTask = uni.request({
          ...finalConfig,
          url: url,
          success: (response) => {
            console.log("请求成功:", response);
            try {
              const data = interceptors.responseInterceptor<T>(response, finalConfig);
              resolve(data);
            } catch (error) {
              reject(error);
            }
          },
          fail: (error) => {
            console.log("请求失败:", error);
            try {
              // 如果定义了网络错误处理，则调用
              const handledError = errorHandles?.handleNetworkError
                ? errorHandles.handleNetworkError(error, finalConfig)
                : error;
              reject(handledError);
            } catch (handlerError) {
              reject(handlerError);
            }
          },
          complete: () => {
            console.log("请求完成");
          },
        });

        // 支持请求取消
        if (finalConfig.signal) {
          const signal = finalConfig.signal as any;
          const abortHandler = () => {
            requestTask.abort();
            reject(new Error("请求已取消"));
          };

          if (signal.addEventListener) {
            signal.addEventListener('abort', abortHandler);
          } else if (signal.onabort !== undefined) {
            // 保存原始 onabort 处理函数
            const originalOnAbort = signal.onabort;
            signal.onabort = () => {
              if (originalOnAbort) originalOnAbort();
              abortHandler();
            };
          }
        }

      } catch (error) {
        reject(error);
      }
    });
  }


  // 便捷方法 - 支持查询参数
  get<T = any>(url: string, params?: any, config?: Partial<RequestConfig>): Promise<ResponseResult<T>> {
    return this.request<T>({
      url,
      params,
      method: "GET",
      ...config,
    });
  };

  post<T = any>(url: string, data?: any, config?: Partial<RequestConfig>): Promise<ResponseResult<T>> {
    return this.request<T>({
      url,
      data,
      method: "POST",
      ...config,
    });
  };

  put<T = any>(url: string, data?: any, config?: Partial<RequestConfig>): Promise<ResponseResult<T>> {
    return this.request<T>({
      url,
      data,
      method: "PUT",
      ...config,
    });
  };

  delete<T = any>(url: string, params?: any, config?: Partial<RequestConfig>): Promise<ResponseResult<T>> {
    return this.request<T>({
      url,
      params,
      method: "DELETE",
      ...config,
    });
  };

// 分页查询专用方法
  page<T = any>(url: string, params: PageQuery, data?: any, config?: Partial<RequestConfig>): Promise<ResponseResult<T>> {
    return this.request<T>({
      url,
      data: data, // 查询条件放在 body
      params: params, // 分页参数放在 query
      method: "POST",
      ...config,
    });

  };
}

export const request = new Request();



