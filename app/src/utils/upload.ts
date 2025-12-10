import { UploadConfig } from "@/types/request";
import { mergeUploadConfig } from "@/utils/request-config";
import { buildUrl } from "@/utils/url";
import { errorHandles } from "@/utils/error-handles";
import { interceptors } from "@/utils/interceptors";


class Upload {

  async upload(config: UploadConfig): Promise<any> {
    return new Promise(async (resolve, reject) => {
      try {
        const mergedUploadConfig = mergeUploadConfig(config);

        // 请求拦截
        const finalUploadConfig = await interceptors.requestInterceptor(mergedUploadConfig);

        // 构建完整 URL（包含查询参数）
        const url = buildUrl(`${finalUploadConfig.baseURL}${finalUploadConfig.url}`, finalUploadConfig.params);

        const uploadTask = uni.uploadFile({
          ...finalUploadConfig,
          url: url,
          success: (res) => {
            try {
              const data = JSON.parse(res.data);
              if (data.success) {
                resolve(data);
              } else {
                errorHandles.handleBusinessError(data, finalUploadConfig);
                reject(data);
              }
            } catch (error) {
              errorHandles.handleNetworkError(error, finalUploadConfig);
              reject(error);
            }
          },
          fail: (error) => {
            errorHandles.handleNetworkError(error, finalUploadConfig);
            reject(error);
          },
        });

        // 进度监听
        if (finalUploadConfig.showProgress) {
          uploadTask.onProgressUpdate((res) => {
            // 可以在这里触发自定义进度事件
            uni.$emit("upload-progress", {
              progress: res.progress,
              totalBytesSent: res.totalBytesSent,
              totalBytesExpectedToSend: res.totalBytesExpectedToSend,
              config: finalUploadConfig
            });
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
            signal.addEventListener('abort', abortHandler);
          } else if (signal.onabort !== undefined) {
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

  // 多文件上传
  async uploadMultiple(configs: UploadConfig[]): Promise<any[]> {
    return Promise.all(configs.map(config => this.upload(config)));
  }
}

export const upload = new Upload();