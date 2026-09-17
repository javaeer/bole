export const useStatus = () => {
  // 通用状态映射
  const statusMaps = {
    application: {
      pending: '待处理',
      approved: '已通过',
      rejected: '已拒绝',
      reviewing: '审核中'
    },
    project: {
      0: '未开始',
      1: '进行中',
      2: '已完成',
      3: '已暂停',
      4: '已取消'
    },
    notification: {
      unread: '未读',
      read: '已读'
    },
    resume: {
      draft: '草稿',
      published: '已发布',
      archived: '已归档'
    },
    work: {
      current: '在职',
      previous: '离职'
    }
  }

  // 状态颜色映射
  const statusColors = {
    application: {
      pending: '#faad14',
      approved: '#52c41a',
      rejected: '#ff4d4f',
      reviewing: '#1890ff'
    },
    project: {
      0: '#d9d9d9',
      1: '#1890ff',
      2: '#52c41a',
      3: '#faad14',
      4: '#ff4d4f'
    },
    notification: {
      unread: '#ff4d4f',
      read: '#52c41a'
    }
  }

  // 获取状态文本
  const getStatusText = (type: keyof typeof statusMaps, status: string | number): string => {
    const map = statusMaps[type]
    return map?.[status] || String(status)
  }

  // 获取状态样式类
  const getStatusClass = (type: keyof typeof statusMaps, status: string | number): string => {
    const baseClass = `status-${type}`
    const statusClass = `status-${String(status).toLowerCase().replace(/\s+/g, '-')}`
    return `${baseClass} ${statusClass}`
  }

  // 获取状态颜色
  const getStatusColor = (type: keyof typeof statusMaps, status: string | number): string => {
    const typeColors = statusColors[type as keyof typeof statusColors]
    return typeColors?.[status] || '#d9d9d9'
  }

  // 状态图标
  const getStatusIcon = (type: string, status: string | number): string => {
    const icons: Record<string, Record<string, string>> = {
      application: {
        pending: '⏳',
        approved: '✅',
        rejected: '❌',
        reviewing: '🔍'
      },
      project: {
        0: '⏸️',
        1: '▶️',
        2: '✅',
        3: '⏸️',
        4: '❌'
      }
    }
    
    return icons[type]?.[status] || '📄'
  }

  // 状态是否可操作
  const isStatusActionable = (type: string, status: string | number): boolean => {
    const actionableStatuses: Record<string, (string | number)[]> = {
      application: ['pending', 'reviewing'],
      project: [0, 1, 3]
    }
    
    return actionableStatuses[type]?.includes(status) || false
  }

  return {
    statusMaps,
    statusColors,
    getStatusText,
    getStatusClass,
    getStatusColor,
    getStatusIcon,
    isStatusActionable
  }
}