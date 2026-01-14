// composables/useDeleteAndBack.ts
import { ref } from 'vue'

interface DeleteOptions {
  deleteFn: () => Promise<void>
  confirmMessage?: string
  successMessage?: string
  errorCallback?: (error: any) => void
}

export function useDeleteAndBack() {
  const deleting = ref(false)
  const loadingShown = ref(false)

  const showCustomLoading = (title: string = '删除中...') => {
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

  const deleteAndBack = async (options: DeleteOptions) => {
    const {
      deleteFn,
      confirmMessage = '确定要删除吗？删除后不可恢复！',
      successMessage = '删除成功',
      errorCallback
    } = options

    return new Promise<void>((resolve, reject) => {
      uni.showModal({
        title: '确认删除',
        content: confirmMessage,
        confirmColor: '#e64340',
        success: async (modalRes) => {
          if (modalRes.confirm) {
            deleting.value = true
            loadingShown.value = false

            try {
              showCustomLoading()

              await deleteFn()

              // 先隐藏 loading，再显示 toast
              hideCustomLoading()

              uni.showToast({
                title: successMessage,
                icon: 'success',
                duration: 1500
              })

              // 触发父页面刷新
              const pages = getCurrentPages()
              if (pages.length > 1) {
                const prevPage = pages[pages.length - 2]
                const prevRoute = prevPage?.route || 'unknown'
                const eventName = `refresh:${prevRoute.replace(/\//g, ':')}`
                uni.$emit(eventName, true)
              }

              // 延迟返回
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

              resolve()
            } catch (error: any) {
              // 确保异常情况下也隐藏 loading
              hideCustomLoading()

              let errorMessage = '删除失败'
              if (error?.message) {
                errorMessage = error.message.length > 50
                  ? error.message.substring(0, 50) + '...'
                  : error.message
              }

              // 延迟显示错误 toast
              setTimeout(() => {
                uni.showToast({
                  title: errorMessage,
                  icon: 'none',
                  duration: 2000
                })
              }, 100)

              errorCallback?.(error)
              reject(error)
            } finally {
              deleting.value = false
              // 最后确保 loading 被隐藏
              setTimeout(() => {
                hideCustomLoading()
              }, 500)
            }
          } else {
            resolve()
          }
        },
        fail: () => {
          reject(new Error('删除确认框显示失败'))
        }
      })
    })
  }

  return {
    deleting,
    deleteAndBack
  }
}