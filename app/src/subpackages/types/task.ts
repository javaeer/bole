// 任务状态枚举
export enum TaskStatus {
  SUCCESS = "SUCCESS",
  FAILED = "FAILED",
  PROCESSING = "PROCESSING",
}

// 文档类型枚举
export enum DocumentType {
  PDF = 'PDF',
  WORD = 'WORD',
  HTML = 'HTML',
  TEXT = 'TEXT',
}

export interface TaskForm {
  /*简历ID*/
  resumesId: number;
  /*可用值:PDF,WORD,HTML,TXT */
  documentType: string;
}

export interface TaskResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  resumesId: number;
  documentType: DocumentType;
  status: TaskStatus;
  fileUrl: string | null;
  fileName: string | null;
  fileSize: number | null;
  errorMessage: string | null;
  retryCount: number;
  startAt: string;
  endAt: string;
}

// 文件信息接口
export interface DocumentResult {
  fileUrl: string;
  fileName: string;
  fileSize: number | null;
  documentType: string;
  resumesId: number;
}

// 文件下载结果
export interface DownloadDocumentResult {
  success: boolean;
  filePath?: string;
  fileName?: string;
  error?: string;
}

// 文件打开结果
export interface OpenDocumentResult {
  success: boolean;
  error?: string;
}

// 文件保存结果
export interface SaveDocumentResult {
  success: boolean;
  savedFilePath?: string;
  error?: string;
}

export interface TaskQuery extends BodyQuery {
  resumesId: number;
}