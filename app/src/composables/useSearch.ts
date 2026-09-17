import { debounce } from '@/utils'
import { ref, computed, onBeforeMount } from 'vue'

export const useSearch = () => {
  const searchText = ref('')
  const searchHistory = ref<string[]>([])
  const isSearching = ref(false)
  const searchResults = ref<any[]>([])

  // 在组件挂载时加载搜索历史
  onBeforeMount(() => {
    loadSearchHistory()
  })

  // 防抖搜索 - 改进版本
  const debouncedSearch = debounce((callback: () => void) => {
    try {
      callback()
    } catch (error) {
      console.error('搜索执行失败:', error)
      isSearching.value = false
    }
  }, 500)

  // 通用搜索处理 - 添加取消功能
  const handleSearch = (callback: (keyword: string) => Promise<void> | void) => {
    if (!searchText.value.trim()) return
    
    isSearching.value = true
    addSearchHistory(searchText.value)
    
    debouncedSearch(() => {
      try {
        const result = callback(searchText.value)
        if (result instanceof Promise) {
          result.finally(() => {
            isSearching.value = false
          })
        } else {
          isSearching.value = false
        }
      } catch (error) {
        console.error('搜索处理失败:', error)
        isSearching.value = false
      }
    })
  }

  // 加载搜索历史
  const loadSearchHistory = () => {
    try {
      const history = uni.getStorageSync('searchHistory')
      if (history && Array.isArray(history)) {
        searchHistory.value = history
      }
    } catch (error) {
      console.error('加载搜索历史失败:', error)
    }
  }

  // 添加搜索历史
  const addSearchHistory = (keyword: string) => {
    if (!keyword.trim()) return
    
    // 移除重复项
    const index = searchHistory.value.indexOf(keyword)
    if (index > -1) {
      searchHistory.value.splice(index, 1)
    }
    
    // 添加到开头
    searchHistory.value.unshift(keyword)
    
    // 限制历史记录数量
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
    
    // 保存到本地存储
    try {
      uni.setStorageSync('searchHistory', searchHistory.value)
    } catch (error) {
      console.error('保存搜索历史失败:', error)
    }
  }

  // 清除搜索历史
  const clearSearchHistory = () => {
    searchHistory.value = []
    try {
      uni.removeStorageSync('searchHistory')
    } catch (error) {
      console.error('清除搜索历史失败:', error)
    }
  }

  // 清除搜索
  const clearSearch = () => {
    searchText.value = ''
    searchResults.value = []
  }

  // 搜索建议
  const searchSuggestions = computed(() => {
    if (!searchText.value.trim()) return []
    
    return searchHistory.value
      .filter(history => history.includes(searchText.value))
      .slice(0, 5)
  })

  // 热门搜索
  const hotSearches = ref([
    '前端开发',
    'Java工程师',
    '产品经理',
    'UI设计师',
    '测试工程师'
  ])

  return {
    searchText,
    searchHistory,
    isSearching,
    searchResults,
    searchSuggestions,
    hotSearches,
    loadSearchHistory,
    addSearchHistory,
    clearSearchHistory,
    handleSearch,
    clearSearch
  }
}