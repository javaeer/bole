// composables/useSaveAndBack.ts
import { ref } from 'vue'

interface SaveOptions<T = any> {
  saveFn: () => Promise<T>
  successMessage?: string
  successCallback?: (result: T) => void
  errorCallback?: (error: any) => void
  showLoading?: boolean
  loadingText?: string
  successToastDuration?: number
}

export function useSaveAndBack() {
  const saving = ref(false)
  // 使用 ref 来跟踪 loading 状态，避免闭包问题
  const loadingShown = ref(false)

  const showCustomLoading = (title: string) => {
    if (!loadingShown.value) {
      uni.showLoading({
        title,
        mask: true
      })
      loadingShown.value = true
    }
  }

  const hideCustomLoading = () => {
    if (loadingShown.value) {
      uni.hideLoading()
      loadingShown.value = false
    }
  }

  const saveAndBack = async <T>(options: SaveOptions<T>) => {
    const {
      saveFn,
      successMessage = '保存成功',
      successCallback,
      errorCallback,
      showLoading = true,
      loadingText = '保存中...',
      successToastDuration = 1500
    } = options

    if (saving.value) return

    saving.value = true
    loadingShown.value = false

    try {
      if (showLoading) {
        showCustomLoading(loadingText)
      }

      const result = await saveFn()

      // 先隐藏 loading，再显示 toast
      hideCustomLoading()

      uni.showToast({
        title: successMessage,
        icon: 'success',
        duration: successToastDuration
      })

      // 触发父页面刷新
      const pages = getCurrentPages()
      if (pages.length > 1) {
        const prevPage = pages[pages.length - 2]
        const prevRoute = prevPage?.route || 'unknown'
        const eventName = `refresh:${prevRoute.replace(/\//g, ':')}`
        uni.$emit(eventName, true)
      }

      // 延迟返回，确保事件已发出
      setTimeout(() => {
        uni.navigateBack({
          delta: 1,
          success: () => {
            // 再次触发，确保接收
            if (pages.length > 1) {
              const prevPage = pages[pages.length - 2]
              const prevRoute = prevPage?.route || 'unknown'
              const eventName = `refresh:${prevRoute.replace(/\//g, ':')}`
              setTimeout(() => {
                uni.$emit(eventName, true)
              }, 50)
            }
          }
        })
      }, 500)

      successCallback?.(result)
      return result
    } catch (error: any) {
      // 确保异常情况下也隐藏 loading
      hideCustomLoading()

      // 使用更安全的错误处理
      let errorMessage = '保存失败'
      if (error?.message) {
        errorMessage = error.message.length > 50
          ? error.message.substring(0, 50) + '...'
          : error.message
      }

      // 延迟显示错误 toast，避免与 loading 冲突
      setTimeout(() => {
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        })
      }, 100)

      errorCallback?.(error)
      throw error
    } finally {
      saving.value = false
      // 最后再确保一次 loading 被隐藏
      setTimeout(() => {
        hideCustomLoading()
      }, 500)
    }
  }

  return {
    saving,
    saveAndBack
  }
}