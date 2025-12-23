import { UploadConfig, UploadProgressEvent } from "@/types/request";
import { mergeUploadConfig } from "@/utils/request-config";
import { buildUrl } from "@/utils/url";
import { errorHandles } from "@/utils/error-handles";
import { interceptors } from "@/utils/interceptors";


class Upload {

  async upload<T = any>(config: UploadConfig): Promise<T> {
    try {
      const mergedUploadConfig = mergeUploadConfig(config);

      // 请求拦截
      const finalUploadConfig = await interceptors.requestInterceptor(mergedUploadConfig);
      const url = buildUrl(`${finalUploadConfig.baseURL}${finalUploadConfig.url}`, finalUploadConfig.params);

      // 直接返回 Promise，不使用额外的 Promise 包装
      return new Promise<T>((resolve, reject) => {
        const uploadTask = uni.uploadFile({
          ...finalUploadConfig,
          url: url,
          success: (response) => {
            try {
              const data = interceptors.responseInterceptor<T, UploadConfig>(response, finalUploadConfig);
              resolve(data);
            } catch (error) {
              reject(error);
            }
          },
          fail: (error) => {
            console.log("请求失败:", error);
            try {
              const handledError = errorHandles?.handleNetworkError
                ? errorHandles.handleNetworkError(error, finalUploadConfig)
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

        // 进度监听
        if (finalUploadConfig.showProgress) {
          uploadTask.onProgressUpdate((response) => {
            const progressEvent: UploadProgressEvent = {
              progress: response.progress,
              totalBytesSent: response.totalBytesSent,
              totalBytesExpectedToSend: response.totalBytesExpectedToSend,
              config: finalUploadConfig,
              filePath: finalUploadConfig.filePath,
            };

            // 触发自定义进度事件
            if (finalUploadConfig.onProgress) {
              finalUploadConfig.onProgress(progressEvent);
            }

            // 全局事件
            uni.$emit("upload-progress", progressEvent);
          });
        }

        // 支持取消上传
        if (finalUploadConfig.signal) {
          const signal = finalUploadConfig.signal as any;
          const abortHandler = () => {
            uploadTask.abort();
            reject(new Error("上传已取消"));
          };

          if (signal.addEventListener) {
            signal.addEventListener("abort", abortHandler);
          } else if (signal.onabort !== undefined) {
            const originalOnAbort = signal.onabort;
            signal.onabort = () => {
              if (originalOnAbort) originalOnAbort();
              abortHandler();
            };
          }
        }
      });
    } catch (error) {
      // 捕获请求拦截器或其他初始化阶段的错误
      return Promise.reject(error);
    }
  }

  // 多文件上传
  async uploadMultiple<T = any>(configs: UploadConfig[]): Promise<T[]> {
    return Promise.all(configs.map(config => this.upload(config)));
  }
}

export const upload = new Upload();