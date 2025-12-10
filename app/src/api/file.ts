import { upload } from "@/utils/upload";

const FileAPI = {
  /**
   * 文件上传地址
   */
  uploadUrl: "/file",

  /**
   * 上传文件
   *
   * @param filePath
   */
  upload(filePath: string) {
    const config = {
      url: this.uploadUrl,
      filePath: filePath,
    };
    return upload.upload(config);
  },
};

export default FileAPI;
