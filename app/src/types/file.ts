export interface FileResult {
  id: number;
  fileName: string;
  fileKey: string;
  fileSize: string;
  fileSizeBytes: number;
  originalFilename: string;
  storagePath: string;
  storageType: string;
  contentType: string;
  accessUrl: string;
  lastAccessTime: string;
}