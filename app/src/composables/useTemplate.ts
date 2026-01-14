import TemplateAPI from "@/api/template";
import { debounce } from 'lodash-es';
import type {TemplateQuery, TemplateResult } from "@/types/template";

// 缓存配置
const CACHE_CONFIG = {
  DURATION: 5 * 60 * 1000, // 5分钟
  ENABLED_PAGES: [1, 2, 3], // 缓存前3页
  MAX_SIZE: 100, // 最大缓存项数
} as const;

export function useTemplate() {
  // 状态
  const templateList = ref<TemplateResult[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const pagination = reactive<PaginationState>({
    current: 1,
    pageSize: 10,
    total: 0,
    pages: 1,
  });
  const currentTemplate = ref<TemplateResult | null>(null);

  // 缓存状态
  const lastQuery = ref<TemplateQuery & Record<string, any>>({});
  const lastSort = ref<{ sortBy?: string; sortOrder?: "asc" | "desc" }>({});
  const cache = ref<Map<string, { data: TemplateResult[]; timestamp: number }>>(
    new Map()
  );

  // ============ 工具函数 ============

  /**
   * 生成缓存键
   */
  const generateCacheKey = (params: CacheKeyQuery): string => {
    const { page, size, query, sortBy, sortOrder } = params;
    return `template_${page}_${size}_${JSON.stringify(query || {})}_${sortBy || ""}_${sortOrder || ""}`;
  };

  /**
   * 清理过期缓存
   */
  const cleanupExpiredCache = () => {
    const now = Date.now();
    for (const [key, value] of cache.value.entries()) {
      if (now - value.timestamp > CACHE_CONFIG.DURATION) {
        cache.value.delete(key);
      }
    }
  };

  /**
   * 限制缓存大小
   */
  const limitCacheSize = () => {
    while (cache.value.size > CACHE_CONFIG.MAX_SIZE) {
      const firstKey = cache.value.keys().next().value;
      if (firstKey) cache.value.delete(firstKey);
    }
  };

  /**
   * 数据映射函数
   */
  const mapTemplateData = (item: any): TemplateResult => ({
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
    category: item.category || "",
    rating: item.rating || 0,
    createdAt: item.createdAt,
    updatedAt: item.updatedAt,
  });

  /**
   * 处理分页响应
   */
  const handlePaginationResponse = (response: any, size: number): void => {
    Object.assign(pagination, {
      current: response.current || response.page || 1,
      pageSize: response.size || response.pageSize || size,
      total: response.total || 0,
      pages: response.pages || Math.ceil((response.total || 0) / (response.size || size)),
    });
  };

  /**
   * 显示错误提示
   */
  const showErrorToast = (message: string = "加载失败") => {
    if (typeof uni !== "undefined") {
      uni.showToast({
        title: message,
        icon: "error",
        duration: 2000,
      });
    }
  };

  /**
   * 缓存数据
   */
  const cacheData = (key: string, data: TemplateResult[]) => {
    if (CACHE_CONFIG.ENABLED_PAGES.some(page => key.includes(`_${page}_`))) {
      cache.value.set(key, { data, timestamp: Date.now() });
      limitCacheSize();
    }
  };

  /**
   * 处理API响应
   */
  const processApiResponse = (
    response: any,
    page: number,
    append: boolean,
    cacheKey?: string
  ): TemplateResult[] => {
    if (!response?.records) {
      throw new RequestError("数据格式不正确");
    }

    const mappedData = response.records.map(mapTemplateData);

    if (append && page > 1) {
      templateList.value = [...templateList.value, ...mappedData];
    } else {
      templateList.value = mappedData;
    }

    if (cacheKey) {
      cacheData(cacheKey, mappedData);
    }

    return mappedData;
  };

  // ============ 核心加载方法 ============

  /**
   * 通用加载模板方法
   */
  const loadTemplates = debounce(
    async (params: LoadParams = {}): Promise<TemplateResult[]> => {
      const {
        page = 1,
        size = 10,
        query = {},
        append = false,
        showToast = true,
        sortBy,
        sortOrder,
        useIndexApi = false,
      } = params;

      // 更新查询状态
      lastQuery.value = query;
      if (sortBy || sortOrder) {
        lastSort.value = { sortBy, sortOrder };
      }

      loading.value = true;
      error.value = null;

      try {
        cleanupExpiredCache();

        const cacheKey = generateCacheKey({ page, size, query, sortBy, sortOrder });

        // 尝试从缓存获取
        if (!append && cache.value.has(cacheKey)) {
          const cached = cache.value.get(cacheKey)!;
          templateList.value = cached.data;
          return cached.data;
        }

        // 构建API参数
        const pageQuery: PageParam = {
          page,
          size,
          ...(sortBy && { sortBy }),
          ...(sortOrder && { sortOrder }),
        };

        // 调用API
        const response = useIndexApi
          ? await TemplateAPI.pageIndex(pageQuery, query)
          : await TemplateAPI.page(pageQuery, query);

        const result = processApiResponse(response, page, append, cacheKey);
        handlePaginationResponse(response, size);

        return result;
      } catch (err) {
        error.value = err instanceof Error ? err.message : "加载失败";
        showToast && showErrorToast();
        throw err;
      } finally {
        loading.value = false;
      }
    },
    300,
    { leading: false, trailing: true }
  );

  // ============ 专用加载方法 ============

  /**
   * 加载首页模板
   */
  const loadHomeTemplates = async (): Promise<TemplateResult[]> => {
    return loadTemplates({
      page: 1,
      size: 8,
      query: { isActive: true },
      showToast: false,
      useIndexApi: true,
    });
  };

  /**
   * 加载列表页模板
   */
  const loadListTemplates = async (
    page: number = 1,
    query: TemplateQuery & Record<string, any> = {},
    useIndexApi: boolean = false
  ): Promise<TemplateResult[]> => {
    return loadTemplates({
      page,
      size: pagination.pageSize,
      query,
      append: page > 1,
      useIndexApi,
    });
  };

  /**
   * 刷新模板数据
   */
  const refreshTemplates = async (
    params: Omit<LoadParams, "page" | "append"> = {}
  ): Promise<TemplateResult[]> => {
    // 清理相关缓存
    Array.from(cache.value.keys())
      .filter(key => key.includes("template_"))
      .forEach(key => cache.value.delete(key));

    return loadTemplates({
      page: 1,
      size: params.size || pagination.pageSize,
      query: params.query || lastQuery.value,
      sortBy: params.sortBy || lastSort.value.sortBy,
      sortOrder: params.sortOrder || lastSort.value.sortOrder,
      useIndexApi: params.useIndexApi || false,
    });
  };

  // ============ 详情相关方法 ============

  /**
   * 根据ID获取模板
   */
  const getTemplateById = (id: number): TemplateResult | undefined => {
    return templateList.value.find(template => template.id === id);
  };

  /**
   * 获取模板详情
   */
  const fetchTemplateDetail = async (id: number): Promise<TemplateResult> => {
    try {
      const detail = await TemplateAPI.getById(id);
      updateTemplateInList(id, detail);
      return detail;
    } catch (err) {
      showErrorToast("获取详情失败");
      throw err;
    }
  };

  /**
   * 更新列表中的模板
   */
  const updateTemplateInList = (id: number, detail: any): void => {
    const index = templateList.value.findIndex(item => item.id === id);
    if (index !== -1 && detail) {
      const updatedItem: TemplateResult = {
        ...mapTemplateData(detail),
        price: templateList.value[index].price,
        users: templateList.value[index].users,
        tags: templateList.value[index].tags,
        category: templateList.value[index].category,
        rating: templateList.value[index].rating,
      };

      templateList.value[index] = updatedItem;

      if (currentTemplate.value?.id === id) {
        currentTemplate.value = updatedItem;
      }
    }
  };

  // ============ 查询操作方法 ============

  /**
   * 搜索模板
   */
  const searchTemplates = async (
    keyword: string,
    useIndexApi: boolean = false
  ): Promise<TemplateResult[]> => {
    return loadTemplates({
      page: 1,
      query: { ...lastQuery.value, name: keyword },
      useIndexApi,
    });
  };

  /**
   * 过滤模板
   */
  const filterTemplates = async (
    filters: TemplateQuery & Record<string, any>,
    useIndexApi: boolean = false
  ): Promise<TemplateResult[]> => {
    return loadTemplates({
      page: 1,
      query: { ...lastQuery.value, ...filters },
      useIndexApi,
    });
  };

  /**
   * 排序模板
   */
  const sortTemplates = async (
    sortBy: string,
    sortOrder: SortOrder = "asc",
    useIndexApi: boolean = false
  ): Promise<TemplateResult[]> => {
    return loadTemplates({
      page: 1,
      query: lastQuery.value,
      sortBy,
      sortOrder,
      useIndexApi,
    });
  };

  /**
   * 加载更多
   */
  const loadMore = async (useIndexApi: boolean = false): Promise<TemplateResult[]> => {
    if (pagination.current >= pagination.pages) {
      throw new Error("没有更多数据");
    }

    return loadTemplates({
      page: pagination.current + 1,
      append: true,
      query: lastQuery.value,
      sortBy: lastSort.value.sortBy,
      sortOrder: lastSort.value.sortOrder,
      showToast: false,
      useIndexApi,
    });
  };

  /**
   * 预加载下一页
   */
  const prefetchNextPage = async (useIndexApi: boolean = false): Promise<void> => {
    if (pagination.current >= pagination.pages) return;

    const nextPage = pagination.current + 1;
    const cacheKey = generateCacheKey({
      page: nextPage,
      size: pagination.pageSize,
      query: lastQuery.value,
      sortBy: lastSort.value.sortBy,
      sortOrder: lastSort.value.sortOrder,
    });

    if (!cache.value.has(cacheKey)) {
      try {
        const pageQuery: PageParam = {
          page: nextPage,
          size: pagination.pageSize,
          ...(lastSort.value.sortBy && { sortBy: lastSort.value.sortBy }),
          ...(lastSort.value.sortOrder && { sortOrder: lastSort.value.sortOrder }),
        };

        const response = useIndexApi
          ? await TemplateAPI.pageIndex(pageQuery, lastQuery.value)
          : await TemplateAPI.page(pageQuery, lastQuery.value);

        if (response?.records) {
          const mappedData = response.records.map(mapTemplateData);
          cacheData(cacheKey, mappedData);
        }
      } catch (err) {
        console.debug("预加载失败:", err);
      }
    }
  };

  // ============ 状态操作方法 ============

  /**
   * 设置当前模板
   */
  const setCurrentTemplate = (template: TemplateResult | number): void => {
    currentTemplate.value = typeof template === "number"
      ? getTemplateById(template) || null
      : template;
  };

  /**
   * 清除缓存
   */
  const clearCache = (): void => {
    cache.value.clear();
  };

  /**
   * 重置状态
   */
  const reset = (): void => {
    templateList.value = [];
    loading.value = false;
    error.value = null;
    currentTemplate.value = null;
    Object.assign(pagination, {
      current: 1,
      pageSize: 10,
      total: 0,
      pages: 1,
    });
    lastQuery.value = {};
    lastSort.value = {};
    clearCache();
  };

  // ============ 计算属性 ============

  const activeTemplatesCount = computed(() =>
    templateList.value.filter(t => t.isActive).length
  );

  const hasMore = computed(() => pagination.current < pagination.pages);

  const isEmpty = computed(() => !loading.value && templateList.value.length === 0);

  const totalTemplates = computed(() => pagination.total);

  const currentPageData = computed(() => {
    const start = (pagination.current - 1) * pagination.pageSize;
    return templateList.value.slice(start, start + pagination.pageSize);
  });

  const activeTemplates = computed(() =>
    templateList.value.filter(t => t.isActive)
  );

  const templatesByCategory = computed(() => {
    const groups: Record<string, TemplateResult[]> = {};
    templateList.value.forEach(template => {
      const category = template.category || "未分类";
      groups[category] = groups[category] || [];
      groups[category].push(template);
    });
    return groups;
  });

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
  };
}