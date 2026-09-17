export const useDateTime = () => {
  // 格式化日期
  const formatDate = (dateStr: string): string => {
    if (!dateStr) return ''
    try {
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    } catch {
      return dateStr
    }
  }

  // 格式化日期时间
  const formatDateTime = (dateStr: string): string => {
    if (!dateStr) return ''
    try {
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    } catch {
      return dateStr
    }
  }

  // 相对时间格式化
  const formatRelativeTime = (timeStr: string): string => {
    const now = new Date()
    const date = new Date(timeStr)
    const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 3600 * 24))
    
    if (diffDays === 0) return '今天'
    if (diffDays === 1) return '昨天'
    if (diffDays < 7) return `${diffDays}天前`
    return formatDate(timeStr)
  }

  // 时间差计算
  const timeDiff = (start: string, end: string): number => {
    return new Date(end).getTime() - new Date(start).getTime()
  }

  return {
    formatDate,
    formatDateTime,
    formatRelativeTime,
    timeDiff
  }
}