// composables/useCommentAction.ts
import { ref } from 'vue'

interface CommentOptions {
  addFn: () => Promise<any>
  onSuccess?: (result: any) => void
  onError?: (error: any) => void
}

interface DeleteCommentOptions {
  deleteFn: () => Promise<void>
  onSuccess?: () => void
  onError?: (error: any) => void
}

export function useCommentAction() {
  const commenting = ref(false)
  const deleting = ref(false)
  let loadingShown = ref(false)

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

  const submitComment = async (options: CommentOptions) => {
    const {
      addFn,
      onSuccess,
      onError
    } = options

    if (commenting.value) return

    commenting.value = true
    loadingShown.value = false

    try {
      showCustomLoading('发布中...')

      const result = await addFn()

      hideCustomLoading()

      uni.showToast({
        title: '评论成功',
        icon: 'success',
        duration: 1500
      })

      onSuccess?.(result)
      return result
    } catch (error: any) {
      hideCustomLoading()

      let errorMessage = '评论失败'
      if (error?.message) {
        errorMessage = error.message.length > 50
          ? error.message.substring(0, 50) + '...'
          : error.message
      }

      setTimeout(() => {
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        })
      }, 100)

      onError?.(error)
      throw error
    } finally {
      commenting.value = false
      setTimeout(() => {
        hideCustomLoading()
      }, 500)
    }
  }

  const deleteComment = async (options: DeleteCommentOptions) => {
    const {
      deleteFn,
      onSuccess,
      onError
    } = options

    return new Promise<void>((resolve, reject) => {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条评论吗？',
        confirmColor: '#e64340',
        success: async (modalRes) => {
          if (modalRes.confirm) {
            deleting.value = true
            loadingShown.value = false

            try {
              showCustomLoading('删除中...')

              await deleteFn()

              hideCustomLoading()

              uni.showToast({
                title: '删除成功',
                icon: 'success',
                duration: 1500
              })

              onSuccess?.()
              resolve()
            } catch (error: any) {
              hideCustomLoading()

              let errorMessage = '删除失败'
              if (error?.message) {
                errorMessage = error.message.length > 50
                  ? error.message.substring(0, 50) + '...'
                  : error.message
              }

              setTimeout(() => {
                uni.showToast({
                  title: errorMessage,
                  icon: 'none',
                  duration: 2000
                })
              }, 100)

              onError?.(error)
              reject(error)
            } finally {
              deleting.value = false
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
    commenting,
    deleting,
    submitComment,
    deleteComment
  }
}