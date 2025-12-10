import { upload } from "@/utils/upload";
import { FileResult } from "@/types/file";
import { UploadConfig, UploadOptions } from "@/types/request";

const FILE_BASE_URL = "/file";

const FileAPI = {

  /**
   * 上传文件
   *
   * @param options
   */
  upload(options: UploadOptions): Promise<FileResult> {
    // 构建完整的上传配置
    const config: UploadConfig = {
      url: `${FILE_BASE_URL}/upload`,
      filePath: options.filePath,
      name: "file",
      formData: {
        ...options.formData,
        // 如果有图片处理参数，添加到formData
        ...(options.compress && { compress: true }),
        ...(options.maxWidth && { maxWidth: options.maxWidth }),
        ...(options.maxHeight && { maxHeight: options.maxHeight }),
        ...(options.quality && { quality: options.quality }),
      },
      header: options.headers,
      params: options.params,
      showProgress: options.showProgress,
      onProgress: options.onProgress,
      signal: options.signal,
    };
    return upload.upload(config);
  },
};

export default FileAPI;
