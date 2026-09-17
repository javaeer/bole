import { ref } from 'vue'

export const useFile = () => {
  const uploading = ref(false)
  const uploadProgress = ref(0)

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // 检查文件类型
  const getFileType = (filename: string): string => {
    const ext = filename.split('.').pop()?.toLowerCase() || ''
    const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
    const docTypes = ['pdf', 'doc', 'docx', 'txt']
    const excelTypes = ['xls', 'xlsx', 'csv']
    const videoTypes = ['mp4', 'avi', 'mov', 'wmv']
    
    if (imageTypes.includes(ext)) return 'image'
    if (docTypes.includes(ext)) return 'document'
    if (excelTypes.includes(ext)) return 'excel'
    if (videoTypes.includes(ext)) return 'video'
    return 'other'
  }

  // 文件预览处理
  const previewFile = async (file: any) => {
    const fileType = getFileType(file.name || file.filename)
    
    switch (fileType) {
      case 'image':
        uni.previewImage({
          urls: [file.url || file.path],
          current: file.url || file.path
        })
        break
      case 'document':
        uni.showToast({
          title: `打开文档: ${file.name || file.filename}`,
          icon: 'none'
        })
        break
      case 'video':
        uni.showToast({
          title: `播放视频: ${file.name || file.filename}`,
          icon: 'none'
        })
        break
      default:
        uni.showToast({
          title: `不支持的文件类型: ${file.name || file.filename}`,
          icon: 'none'
        })
    }
  }

  // 文件上传
  const uploadFile = async (filePath: string, options: any = {}) => {
    uploading.value = true
    uploadProgress.value = 0
    
    try {
      const result = await new Promise((resolve, reject) => {
        uni.uploadFile({
          url: options.url || '/api/upload',
          filePath,
          name: options.name || 'file',
          formData: options.formData || {},
          success: resolve,
          fail: reject,
          complete: () => {
            uploading.value = false
            uploadProgress.value = 0
          }
        })
      })
      
      return result
    } catch (error) {
      uploading.value = false
      uploadProgress.value = 0
      throw error
    }
  }

  // 下载文件
  const downloadFile = async (url: string, options: any = {}) => {
    try {
      const result = await new Promise((resolve, reject) => {
        uni.downloadFile({
          url,
          success: resolve,
          fail: reject
        })
      })
      
      return result
    } catch (error) {
      throw error
    }
  }

  return {
    uploading,
    uploadProgress,
    formatFileSize,
    getFileType,
    previewFile,
    uploadFile,
    downloadFile
  }
}