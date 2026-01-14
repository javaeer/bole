// composables/usePageRefresh.ts
import { ref, onUnmounted, getCurrentInstance } from 'vue'
import { onShow, onHide } from '@dcloudio/uni-app'

interface RefreshOptions {
  immediate?: boolean
  onRefresh?: () => void | Promise<void>
}

export function usePageRefresh(options: RefreshOptions = {}) {
  const { immediate = false, onRefresh } = options
  const refreshing = ref(false)
  const refreshKey = ref(0)

  // 获取当前页面路由
  const getCurrentRoute = (): string => {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    return currentPage?.route || 'unknown'
  }

  // 使用函数获取路由，而不是实例属性
  const pageRoute = getCurrentRoute()
  // 修复：移除 .route，pageRoute 本身就是字符串
  const eventName = `refresh:${pageRoute.replace(/\//g, ':')}`

  const handleRefresh = async (needRefresh: boolean) => {
    if (needRefresh && onRefresh) {
      refreshing.value = true
      try {
        await onRefresh()
        refreshKey.value++ // 用于强制重新渲染
      } finally {
        refreshing.value = false
      }
    }
  }

  // 注册事件监听
  const registerListener = () => {
    uni.$on(eventName, handleRefresh)
  }

  const unregisterListener = () => {
    uni.$off(eventName, handleRefresh)
  }

  // 页面显示时注册，隐藏时注销
  onShow(() => {
    registerListener()
    if (immediate) {
      handleRefresh(true)
    }
  })

  onHide(() => {
    unregisterListener()
  })

  onUnmounted(() => {
    unregisterListener()
  })

  return {
    refreshing,
    refreshKey,
    triggerRefresh: (needRefresh = true) => {
      uni.$emit(eventName, needRefresh)
    }
  }
}