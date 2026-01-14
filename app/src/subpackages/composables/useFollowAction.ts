// composables/useFollowAction.ts
import { ref } from 'vue'

interface FollowOptions {
  followFn: () => Promise<void>
  unfollowFn: () => Promise<void>
  onSuccess?: () => void
  onError?: (error: any) => void
}

export function useFollowAction() {
  const following = ref(false)
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

  const toggleFollow = async (isCurrentlyFollowed: boolean, options: FollowOptions) => {
    const {
      followFn,
      unfollowFn,
      onSuccess,
      onError
    } = options

    if (following.value) return

    following.value = true
    loadingShown.value = false

    try {
      showCustomLoading('处理中...')

      if (isCurrentlyFollowed) {
        await unfollowFn()
      } else {
        await followFn()
      }

      hideCustomLoading()

      const successMessage = isCurrentlyFollowed ? '已取消关注' : '关注成功'
      uni.showToast({
        title: successMessage,
        icon: 'success',
        duration: 1500
      })

      onSuccess?.()
    } catch (error: any) {
      hideCustomLoading()

      let errorMessage = '操作失败'
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
      following.value = false
      setTimeout(() => {
        hideCustomLoading()
      }, 500)
    }
  }

  return {
    following,
    toggleFollow
  }
}