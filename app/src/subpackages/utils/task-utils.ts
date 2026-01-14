import {
  DocumentType,
  DownloadDocumentResult,
  OpenDocumentResult,
  SaveDocumentResult,
  TaskResult,
  TaskStatus,
} from "@/subpackages/types/task";

// 文件扩展名映射
const FILE_EXTENSION_MAP: Record<DocumentType, string> = {
  [DocumentType.PDF]: "pdf",
  [DocumentType.WORD]: "docx",
  [DocumentType.HTML]: "html",
  [DocumentType.TEXT]: "txt",
};

// MIME 类型映射
const MIME_TYPE_MAP: Record<DocumentType, string> = {
  [DocumentType.PDF]: "application/pdf",
  [DocumentType.WORD]: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  [DocumentType.HTML]: "text/html",
  [DocumentType.TEXT]: "text/plain",
};

// 文件类型映射（用于 uni.openDocument）
const FILE_TYPE_MAP: Record<DocumentType, string> = {
  [DocumentType.PDF]: "pdf",
  [DocumentType.WORD]: "doc",
  [DocumentType.HTML]: "html",
  [DocumentType.TEXT]: "txt",
};

// 有效的文件扩展名
const VALID_EXTENSIONS: Record<DocumentType, string[]> = {
  [DocumentType.PDF]: ["pdf"],
  [DocumentType.WORD]: ["doc", "docx", "dot", "dotx", "rtf"],
  [DocumentType.HTML]: ["html", "htm", "xhtml"],
  [DocumentType.TEXT]: ["txt", "text", "md", "log"],
};

/**
 * 获取平台类型
 */
export const getPlatform = (): PlatformType => {
  const systemInfo = uni.getSystemInfoSync();
  const platform = systemInfo.platform?.toLowerCase() || "h5";

  // 处理小程序平台
  if (platform.includes("mp-")) {
    return platform as PlatformType;
  }

  return platform as PlatformType;
};

/**
 * 获取文件扩展名
 */
export const getFileExtension = (documentType: DocumentType): string => {
  return FILE_EXTENSION_MAP[documentType] || "file";
};

/**
 * 获取 MIME 类型
 */
export const getFileMimeType = (documentType: DocumentType): string => {
  return MIME_TYPE_MAP[documentType] || "application/octet-stream";
};

/**
 * 获取文件类型（用于 uni.openDocument）
 */
export const getFileType = (documentType: DocumentType): string => {
  return FILE_TYPE_MAP[documentType] || "pdf";
};

/**
 * 检查扩展名是否有效
 */
export const isValidExtension = (extension: string, documentType: DocumentType): boolean => {
  const validExtensions = VALID_EXTENSIONS[documentType];
  if (!validExtensions) return false;

  return validExtensions.includes(extension.toLowerCase());
};

/**
 * 确保文件名有正确的扩展名
 */
export const ensureFileExtension = (fileName: string, documentType: DocumentType): string => {
  const expectedExtension = getFileExtension(documentType);
  const fileNameLower = fileName.toLowerCase();

  // 检查是否已经有正确的扩展名
  if (fileNameLower.endsWith(`.${expectedExtension}`)) {
    return fileName;
  }

  // 检查是否有其他扩展名
  const lastDotIndex = fileName.lastIndexOf(".");
  if (lastDotIndex !== -1) {
    // 已经有扩展名，检查是否有效
    const currentExtension = fileName.substring(lastDotIndex + 1);
    if (isValidExtension(currentExtension, documentType)) {
      return fileName;
    }
    // 替换为正确的扩展名
    return fileName.substring(0, lastDotIndex) + `.${expectedExtension}`;
  }

  // 没有扩展名，添加扩展名
  return `${fileName}.${expectedExtension}`;
};

/**
 * 从 URL 中提取文件名
 */
export const extractFileNameFromUrl = (url: string): string | null => {
  if (!url) return null;

  try {
    // 尝试从 URL 路径中提取文件名
    const urlObj = new URL(url);
    const pathname = urlObj.pathname;
    const filename = pathname.substring(pathname.lastIndexOf("/") + 1);

    // 移除查询参数
    const filenameWithoutQuery = filename.split("?")[0];

    return filenameWithoutQuery || null;
  } catch (error) {
    console.error("解析 URL 失败:", error);

    // 如果 URL 解析失败，尝试简单提取
    const lastSlashIndex = url.lastIndexOf("/");
    if (lastSlashIndex !== -1) {
      const filename = url.substring(lastSlashIndex + 1);
      return filename.split("?")[0];
    }

    return null;
  }
};

/**
 * 获取下载文件名
 */
export const getDownloadFileName = (task: TaskResult): string => {
  // 如果接口返回了文件名，使用接口返回的文件名
  if (task.fileName) {
    return ensureFileExtension(task.fileName, task.documentType);
  }

  // 尝试从 URL 中提取文件名
  if (task.fileUrl) {
    const extractedName = extractFileNameFromUrl(task.fileUrl);
    if (extractedName) {
      return ensureFileExtension(extractedName, task.documentType);
    }
  }

  // 否则根据文档类型生成文件名
  const timestamp = new Date().getTime();
  const fileExtension = getFileExtension(task.documentType);
  const docTypeLabel = getDocumentTypeLabel(task.documentType).replace("文档", "").replace("文件", "");

  return `简历_${task.resumesId}_${docTypeLabel}_${timestamp}.${fileExtension}`;
};

/**
 * 获取文档类型标签
 */
export const getDocumentTypeLabel = (documentType: DocumentType): string => {
  const map: Record<DocumentType, string> = {
    [DocumentType.PDF]: "PDF文档",
    [DocumentType.WORD]: "Word文档",
    [DocumentType.HTML]: "HTML文件",
    [DocumentType.TEXT]: "文本文件",
  };
  return map[documentType] || documentType;
};

/**
 * 下载文件
 */
export const downloadFile = (url: string, fileName: string): Promise<DownloadDocumentResult> => {
  return new Promise((resolve) => {
    uni.downloadFile({
      url,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve({
            success: true,
            filePath: res.tempFilePath,
            fileName,
          });
        } else {
          resolve({
            success: false,
            error: `下载失败，状态码: ${res.statusCode}`,
          });
        }
      },
      fail: (err) => {
        resolve({
          success: false,
          error: `下载失败: ${err.errMsg || "未知错误"}`,
        });
      },
    });
  });
};

/**
 * 保存文件到本地
 */
export const saveFileToLocal = (tempFilePath: string, fileName?: string): Promise<SaveDocumentResult> => {
  return new Promise((resolve) => {
    uni.saveFile({
      tempFilePath,
      success: (res) => {
        resolve({
          success: true,
          savedFilePath: res.savedFilePath,
        });
      },
      fail: (err) => {
        resolve({
          success: false,
          error: `保存失败: ${err.errMsg || "未知错误"}`,
        });
      },
    });
  });
};

/**
 * 打开文档
 */
export const openDocument = (filePath: string, documentType: DocumentType): Promise<OpenDocumentResult> => {
  return new Promise((resolve) => {
    uni.openDocument({
      filePath,
      fileType: getFileType(documentType),
      showMenu: true,
      success: () => {
        resolve({ success: true });
      },
      fail: (err) => {
        resolve({
          success: false,
          error: `打开失败: ${err.errMsg || "未知错误"}`,
        });
      },
    });
  });
};

/**
 * H5 环境下载
 */
export const downloadFileH5 = (url: string, fileName: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    try {
      // 创建 a 标签下载
      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      link.style.display = "none";

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      resolve();
    } catch (error) {
      reject(error);
    }
  });
};

/**
 * App 环境下载
 */
export const downloadFileApp = async (task: TaskResult, fileName: string): Promise<DownloadDocumentResult> => {
  if (!task.fileUrl) {
    return {
      success: false,
      error: "下载链接不存在",
    };
  }

  // 下载文件
  const downloadResult = await downloadFile(task.fileUrl, fileName);
  if (!downloadResult.success || !downloadResult.filePath) {
    return downloadResult;
  }

  // 尝试打开文档
  const openResult = await openDocument(downloadResult.filePath, task.documentType);
  if (!openResult.success) {
    // 如果打开失败，尝试保存文件
    const saveResult = await saveFileToLocal(downloadResult.filePath, fileName);
    return {
      success: saveResult.success,
      filePath: saveResult.savedFilePath,
      fileName,
      error: saveResult.error,
    };
  }

  return {
    success: true,
    filePath: downloadResult.filePath,
    fileName,
  };
};

/**
 * 小程序环境下载
 */
export const downloadFileMiniProgram = async (task: TaskResult, fileName: string): Promise<DownloadDocumentResult> => {
  if (!task.fileUrl) {
    return {
      success: false,
      error: "下载链接不存在",
    };
  }

  // 下载文件
  const downloadResult = await downloadFile(task.fileUrl, fileName);
  if (!downloadResult.success || !downloadResult.filePath) {
    return downloadResult;
  }

  // 保存文件到本地
  const saveResult = await saveFileToLocal(downloadResult.filePath, fileName);
  if (saveResult.success && saveResult.savedFilePath) {
    // 尝试打开文档
    await openDocument(saveResult.savedFilePath, task.documentType);
  }

  return {
    success: saveResult.success,
    filePath: saveResult.savedFilePath,
    fileName,
    error: saveResult.error,
  };
};

/**
 * 跨平台下载文件
 */
export const downloadFileCrossPlatform = async (task: TaskResult): Promise<DownloadDocumentResult> => {
  const platform = getPlatform();
  const fileName = getDownloadFileName(task);

  try {
    switch (platform) {
      case "h5":
        await downloadFileH5(task.fileUrl!, fileName);
        return { success: true, fileName };

      case "android":
      case "ios":
        return await downloadFileApp(task, fileName);

      case "mp-weixin":
      case "mp-alipay":
      case "mp-baidu":
      case "mp-toutiao":
      case "mp-qq":
        return await downloadFileMiniProgram(task, fileName);

      default:
        await downloadFileH5(task.fileUrl!, fileName);
        return { success: true, fileName };
    }
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : "未知错误",
    };
  }
};

/**
 * 格式化时间
 */
export const formatTime = (timeString: string | null): string => {
  if (!timeString) return "--";

  try {
    const date = new Date(timeString.replace(/-/g, "/"));
    const now = new Date();
    const diff = now.getTime() - date.getTime();

    // 如果日期无效
    if (isNaN(diff)) {
      return timeString;
    }

    // 如果是今天
    if (diff < 24 * 60 * 60 * 1000 && date.getDate() === now.getDate()) {
      const hours = date.getHours().toString().padStart(2, "0");
      const minutes = date.getMinutes().toString().padStart(2, "0");
      return `今天 ${hours}:${minutes}`;
    }

    // 如果是昨天
    if (diff < 48 * 60 * 60 * 1000 && date.getDate() === now.getDate() - 1) {
      const hours = date.getHours().toString().padStart(2, "0");
      const minutes = date.getMinutes().toString().padStart(2, "0");
      return `昨天 ${hours}:${minutes}`;
    }

    // 其他情况
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");
    return `${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error("格式化时间失败:", error);
    return timeString;
  }
};

/**
 * 格式化耗时
 */
export const formatDuration = (startAt: string | null, endAt: string | null): string => {
  if (!startAt || !endAt) return "--";

  try {
    const start = new Date(startAt.replace(/-/g, "/"));
    const end = new Date(endAt.replace(/-/g, "/"));
    const duration = end.getTime() - start.getTime();

    if (duration < 0) return "--";

    if (duration < 1000) {
      return `${duration}ms`;
    } else if (duration < 60000) {
      return `${(duration / 1000).toFixed(1)}s`;
    } else {
      const minutes = Math.floor(duration / 60000);
      const seconds = Math.floor((duration % 60000) / 1000);
      return `${minutes}m${seconds}s`;
    }
  } catch (error) {
    console.error("计算耗时失败:", error);
    return "--";
  }
};

/**
 * 格式化文件大小
 */
export const formatFileSize = (bytes: number | null): string => {
  if (bytes === null || bytes === undefined) return "--";
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / (1024 * 1024)).toFixed(1) + " MB";
};

/**
 * 获取状态文本
 */
export const getStatusText = (status: TaskStatus): string => {
  const map: Record<TaskStatus, string> = {
    [TaskStatus.SUCCESS]: "已完成",
    [TaskStatus.FAILED]: "失败",
    [TaskStatus.PROCESSING]: "生成中",
  };
  return map[status] || "未知";
};

/**
 * 获取状态徽章类名
 */
export const getStatusBadgeClass = (status: TaskStatus): string => {
  const map: Record<TaskStatus, string> = {
    [TaskStatus.SUCCESS]: "status-completed",
    [TaskStatus.FAILED]: "status-failed",
    [TaskStatus.PROCESSING]: "status-processing",
  };
  return map[status] || "";
};

/**
 * 获取任务卡片类名
 */
export const getTaskStatusClass = (status: TaskStatus): string => {
  const map: Record<TaskStatus, string> = {
    [TaskStatus.SUCCESS]: "task-completed",
    [TaskStatus.FAILED]: "task-failed",
    [TaskStatus.PROCESSING]: "task-processing",
  };
  return map[status] || "";
};

/**
 * 判断文件类型是否可预览
 */
export const isPreviewable = (documentType: DocumentType): boolean => {
  return [DocumentType.HTML, DocumentType.PDF].includes(documentType);
};

/**
 * 获取预览页面路径
 */
export const getPreviewPagePath = (task: TaskResult): string => {
  const title = task.fileName || getDocumentTypeLabel(task.documentType);

  if (task.documentType === DocumentType.PDF) {
    return `/subpackages/pages/webview/pdfview?url=${encodeURIComponent(task.fileUrl!)}&title=${encodeURIComponent(title)}`;
  } else {
    return `/subpackages/pages/webview/webview?url=${encodeURIComponent(task.fileUrl!)}&title=${encodeURIComponent(title)}`;
  }
};