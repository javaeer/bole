export const usePagination = () => {
  const page = ref(1)
  const loading = ref(false)
  const noMore = ref(false)
  
  const loadMore = async (loadFn: (page: number) => Promise<any[]>) => {
    if (loading.value || noMore.value) return []
    
    loading.value = true
    try {
      const data = await loadFn(page.value)
      if (data.length === 0) noMore.value = true
      else page.value++
      return data
    } finally {
      loading.value = false
    }
  }
  
  return { page, loading, noMore, loadMore }
}