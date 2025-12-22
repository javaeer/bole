import TemplateAPI from "@/api/template";
import type { PageQuery, TemplateQuery, TemplateResult } from "@/types/template";

export function useTemplate() {
  // 模板列表状态
  const templateList = ref<TemplateResult[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = reactive<PaginationState>({
    current: 1,
    pageSize: 10,
    total: 0,
    pages: 1,
  })

  // 当前选中的模板详情
  const currentTemplate = ref<TemplateResult | null>(null)

  // 缓存最近一次查询条件
  const lastQuery = ref<TemplateQuery & Record<string, any>>({})
  const lastSort = ref<{ sortBy?: string; sortOrder?: "asc" | "desc" }>({})

  // 缓存管理
  const cache = new Map<string, TemplateResult[]>()
  const CACHE_DURATION = 5 * 60 * 1000 // 5分钟缓存
  const cacheTimestamps = new Map<string, number>()

  // 生成缓存键
  const generateCacheKey = (params: CacheKeyQuery): string => {
    const { page, size, query, sortBy, sortOrder } = params
    const queryStr = JSON.stringify(query || {})
    const sortStr = `${sortBy || ""}_${sortOrder || ""}`
    return `template_${page}_${size}_${queryStr}_${sortStr}`
  }

  // 清理过期缓存
  const cleanupExpiredCache = () => {
    const now = Date.now()
    for (const [key, timestamp] of cacheTimestamps.entries()) {
      if (now - timestamp > CACHE_DURATION) {
        cache.delete(key)
        cacheTimestamps.delete(key)
      }
    }
  }

  // 防抖相关
  let debounceTimer: NodeJS.Timeout | null = null

  /**
   * 通用加载模板方法
   */
  const loadTemplates = async (params: LoadParams = {}): Promise<TemplateResult[]> => {
    const {
      page = 1,
      size = 10,
      query = {},
      append = false,
      showToast = true,
      sortBy,
      sortOrder,
    } = params

    // 保存查询和排序条件
    lastQuery.value = query
    if (sortBy || sortOrder) {
      lastSort.value = { sortBy, sortOrder }
    }

    // 防抖处理
    if (debounceTimer) {
      clearTimeout(debounceTimer)
    }

    return new Promise((resolve, reject) => {
      debounceTimer = setTimeout(async () => {
        loading.value = true
        error.value = null

        try {
          // 清理过期缓存
          cleanupExpiredCache()

          // 构建缓存键
          const cacheKey = generateCacheKey({
            page,
            size,
            query,
            sortBy,
            sortOrder,
          })

          // 检查缓存
          if (!append && cache.has(cacheKey)) {
            const cachedData = cache.get(cacheKey)!
            templateList.value = cachedData
            resolve(cachedData)
            loading.value = false
            return
          }

          // 构建 API 请求参数
          const pageQuery: PageQuery = {
            page,
            size,
            ...(sortBy && { sortBy }),
            ...(sortOrder && { sortOrder }),
          }

          // 调用 API - 注意：根据接口，第二个参数是 TemplateQuery
          const response = await TemplateAPI.page(pageQuery, query)

          if (!response?.records) {
            throw new RequestError("数据格式不正确")
          }

          // 直接使用 API 返回的数据，确保类型匹配
          const mappedData: TemplateResult[] = response.records.map((item: any) => ({
            id: item.id,
            name: item.name,
            code: item.code,
            description: item.description,
            previewImage: item.previewImage,
            isActive: item.isActive,
            version: item.version,
            globalStyle: item.globalStyle,
            globalLayout: item.globalLayout, // 使用 globalLayout 而非 layout
            components: item.components || [],
            price: item.price || 0,
            users: item.users || 0,
            tags: item.tags || [],
            category: item.category || '',
            rating: item.rating || 0,
            createdAt: item.createdAt,
            updatedAt: item.updatedAt,
          }))

          // 处理数据
          if (append && page > 1) {
            templateList.value = [...templateList.value, ...mappedData]
          } else {
            templateList.value = mappedData
            // 缓存第一页数据
            if (page === 1) {
              cache.set(cacheKey, mappedData)
              cacheTimestamps.set(cacheKey, Date.now())
            }
          }

          // 更新分页信息 - 使用 response 中的字段
          Object.assign(pagination, {
            current: response.current || response.page || 1,
            pageSize: response.size || response.pageSize || size,
            total: response.total,
            pages: response.pages || Math.ceil(response.total / (response.size || size)),
          })

          resolve(mappedData)
        } catch (err) {
          console.error("加载模板失败:", err)
          error.value = err instanceof Error ? err.message : "加载失败"

          // 错误处理
          if (showToast && typeof uni !== "undefined") {
            uni.showToast({
              title: "加载失败",
              icon: "error",
            })
          }
          reject(err)
        } finally {
          loading.value = false
        }
      }, 300) // 300ms 防抖
    })
  }

  /**
   * 加载首页模板（固定5条）
   */
  const loadHomeTemplates = async (): Promise<TemplateResult[]> => {
    return await loadTemplates({
      page: 1,
      size: 8,
      query: { isActive: true },
      showToast: false, // 首页加载失败不显示 toast
    })
  }

  /**
   * 加载列表页模板（分页）
   */
  const loadListTemplates = async (
    page: number = 1,
    query: TemplateQuery & Record<string, any> = {},
  ): Promise<TemplateResult[]> => {
    return await loadTemplates({
      page,
      size: pagination.pageSize,
      query,
      append: page > 1,
    })
  }

  /**
   * 刷新模板数据
   */
  const refreshTemplates = async (params: Omit<LoadParams, "page" | "append"> = {}): Promise<TemplateResult[]> => {
    // 清除相关缓存
    const cacheKeys = Array.from(cache.keys())
    cacheKeys.forEach(key => {
      if (key.includes("template_")) {
        cache.delete(key)
        cacheTimestamps.delete(key)
      }
    })

    return await loadTemplates({
      page: 1,
      size: params.size || pagination.pageSize,
      query: params.query || lastQuery.value,
      append: false,
      sortBy: params.sortBy || lastSort.value.sortBy,
      sortOrder: params.sortOrder || lastSort.value.sortOrder,
    })
  }

  /**
   * 根据ID获取模板详情
   */
  const getTemplateById = (id: number): TemplateResult | undefined => {
    return templateList.value.find(template => template.id === id)
  }

  /**
   * 获取模板详情（从API重新获取）
   */
  const fetchTemplateDetail = async (id: number): Promise<TemplateResult> => {
    try {
      const detail = await TemplateAPI.getById(id)

      // 更新列表中的对应项
      const index = templateList.value.findIndex(item => item.id === id)
      if (index !== -1 && detail) {
        const updatedItem: TemplateResult = {
          ...templateList.value[index],
          ...detail,
          components: detail.components || [],
          // 保留列表中的扩展字段
          price: templateList.value[index].price,
          users: templateList.value[index].users,
          tags: templateList.value[index].tags,
          category: templateList.value[index].category,
          rating: templateList.value[index].rating,
        }
        templateList.value[index] = updatedItem

        // 更新当前选中的模板
        if (currentTemplate.value?.id === id) {
          currentTemplate.value = updatedItem
        }
      }

      return detail
    } catch (err) {
      console.error("获取模板详情失败:", err)
      throw err
    }
  }

  /**
   * 搜索模板
   */
  const searchTemplates = async (keyword: string): Promise<TemplateResult[]> => {
    return await loadTemplates({
      page: 1,
      query: { ...lastQuery.value, name: keyword },
    })
  }

  /**
   * 过滤模板
   */
  const filterTemplates = async (filters: TemplateQuery & Record<string, any>): Promise<TemplateResult[]> => {
    return await loadTemplates({
      page: 1,
      query: { ...lastQuery.value, ...filters },
    })
  }

  /**
   * 排序模板
   */
  const sortTemplates = async (sortBy: string, sortOrder: "asc" | "desc" = "asc"): Promise<TemplateResult[]> => {
    return await loadTemplates({
      page: 1,
      query: lastQuery.value,
      sortBy,
      sortOrder,
    })
  }

  /**
   * 加载更多
   */
  const loadMore = async (): Promise<TemplateResult[]> => {
    if (pagination.current >= pagination.pages) {
      return Promise.reject(new Error("没有更多数据"))
    }

    return await loadTemplates({
      page: pagination.current + 1,
      append: true,
      query: lastQuery.value,
      sortBy: lastSort.value.sortBy,
      sortOrder: lastSort.value.sortOrder,
      showToast: false,
    })
  }

  /**
   * 获取活跃模板数量
   */
  const activeTemplatesCount = computed(() => {
    return templateList.value.filter(t => t.isActive).length
  })

  /**
   * 是否有更多数据
   */
  const hasMore = computed(() => pagination.current < pagination.pages)

  /**
   * 是否为空
   */
  const isEmpty = computed(() => !loading.value && templateList.value.length === 0)

  /**
   * 模板总数
   */
  const totalTemplates = computed(() => pagination.total)

  /**
   * 当前页数据
   */
  const currentPageData = computed(() => {
    const start = (pagination.current - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    return templateList.value.slice(start, end)
  })

  /**
   * 活跃模板列表
   */
  const activeTemplates = computed(() => {
    return templateList.value.filter(t => t.isActive)
  })

  /**
   * 按分类分组
   */
  const templatesByCategory = computed(() => {
    const groups: Record<string, TemplateResult[]> = {}
    templateList.value.forEach(template => {
      const category = template.category || "未分类"
      if (!groups[category]) {
        groups[category] = []
      }
      groups[category].push(template)
    })
    return groups
  })

  /**
   * 预加载下一页
   */
  const prefetchNextPage = async (): Promise<void> => {
    if (pagination.current >= pagination.pages) return

    try {
      const nextPage = pagination.current + 1
      const cacheKey = generateCacheKey({
        page: nextPage,
        size: pagination.pageSize,
        query: lastQuery.value,
        sortBy: lastSort.value.sortBy,
        sortOrder: lastSort.value.sortOrder,
      })

      if (!cache.has(cacheKey)) {
        const pageQuery: PageQuery = {
          page: nextPage,
          size: pagination.pageSize,
          ...(lastSort.value.sortBy && { sortBy: lastSort.value.sortBy }),
          ...(lastSort.value.sortOrder && { sortOrder: lastSort.value.sortOrder }),
        }

        const response = await TemplateAPI.page(pageQuery, lastQuery.value)
        if (response?.records) {
          const mappedData: TemplateResult[] = response.records.map((item: any) => ({
            id: item.id,
            name: item.name,
            code: item.code,
            description: item.description,
            previewImage: item.previewImage,
            isActive: item.isActive,
            version: item.version,
            globalStyle: item.globalStyle,
            globalLayout: item.globalLayout,
            components: item.components || [],
            price: item.price || 0,
            users: item.users || 0,
            tags: item.tags || [],
            category: item.category || '',
            rating: item.rating || 0,
            createdAt: item.createdAt,
            updatedAt: item.updatedAt,
          }))
          cache.set(cacheKey, mappedData)
          cacheTimestamps.set(cacheKey, Date.now())
        }
      }
    } catch (err) {
      // 静默失败，不影响主流程
      console.log("预加载失败:", err)
    }
  }

  /**
   * 设置当前选中的模板
   */
  const setCurrentTemplate = (template: TemplateResult | number): void => {
    if (typeof template === "number") {
      currentTemplate.value = getTemplateById(template) || null
    } else {
      currentTemplate.value = template
    }
  }

  /**
   * 清除缓存
   */
  const clearCache = (): void => {
    cache.clear()
    cacheTimestamps.clear()
  }

  /**
   * 重置状态
   */
  const reset = (): void => {
    templateList.value = []
    loading.value = false
    error.value = null
    currentTemplate.value = null
    Object.assign(pagination, {
      current: 1,
      pageSize: 10,
      total: 0,
      pages: 1,
    })
    lastQuery.value = {}
    lastSort.value = {}
    clearCache()
  }

  return {
    // 状态
    templateList,
    loading,
    error,
    pagination,
    currentTemplate,

    // 计算属性
    activeTemplatesCount,
    hasMore,
    isEmpty,
    totalTemplates,
    currentPageData,
    activeTemplates,
    templatesByCategory,

    // 方法
    loadTemplates,
    loadHomeTemplates,
    loadListTemplates,
    refreshTemplates,
    getTemplateById,
    fetchTemplateDetail,
    searchTemplates,
    filterTemplates,
    sortTemplates,
    loadMore,
    prefetchNextPage,
    setCurrentTemplate,
    clearCache,
    reset,
  }
}

// 导出类型供外部使用
export type { TemplateResult, PaginationState, LoadParams }