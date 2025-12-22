// stores/template.ts
import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'
import { TemplateForm, TemplateResult } from '@/types/template'
import { TemplateComponentForm } from '@/types/template-component'
import TemplateAPI from '@/api/template'

export const useTemplateStore = defineStore('template', () => {
  // 状态
  const currentTemplate = ref<TemplateResult | null>(null)
  const templateList = ref<TemplateResult[]>([])
  const loading = ref(false)
  const hasMore = ref(true)
  const pageParams = reactive({
    page: 1,
    size: 10,
    total: 0
  })

  // 编辑表单数据
  const editForm = reactive<TemplateForm>({
    name: '',
    code: '',
    description: '',
    isActive: true,
    version: '1.0.0',
    globalLayout: {
      type: 'single-column',
      columns: { left: 40, right: 60 },
      orientation: 'portrait',
      componentOrder: []
    },
    globalStyle: {
      theme: 'light',
      fontSizes: { h1: '24', body: '14' },
      fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
      primaryColor: '#d4af37',
      accentColor: '#f7ef8a',
      secondaryColor: '#f9f3e3',
      backgroundColor: '',
      spacing: {
        sectionMargin: '20px',
        padding: '15px',
        lineHeight: '1.5'
      }
    },
    components: [],
    price: 0,
    users: 0,
    tags: [],
    category: '',
    rating: 0
  })

  // 搜索状态
  const searchState = reactive({
    keyword: '',
    category: '',
    status: ''
  })

  // 预览状态
  const previewState = reactive({
    device: 'desktop',
    refreshKey: 0
  })

  // 组件管理状态
  const componentState = reactive({
    selectedIndex: -1,
    showColorPicker: false,
    currentColorField: 'primaryColor' as 'primaryColor' | 'secondaryColor' | 'accentColor'
  })

  // Actions
  const setCurrentTemplate = (template: TemplateResult) => {
    currentTemplate.value = template
  }

  const updateEditForm = (data: Partial<TemplateForm>) => {
    Object.assign(editForm, data)
  }

  const resetEditForm = () => {
    Object.assign(editForm, {
      name: '',
      code: '',
      description: '',
      isActive: true,
      version: '1.0.0',
      globalLayout: {
        type: 'single-column',
        columns: { left: 40, right: 60 },
        orientation: 'portrait',
        componentOrder: []
      },
      globalStyle: {
        theme: 'light',
        fontSizes: { h1: '24', body: '14' },
        fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
        primaryColor: '#d4af37',
        accentColor: '#f7ef8a',
        secondaryColor: '#f9f3e3',
        backgroundColor: '',
        spacing: {
          sectionMargin: '20px',
          padding: '15px',
          lineHeight: '1.5'
        }
      },
      components: [],
      price: 0,
      users: 0,
      tags: [],
      category: '',
      rating: 0
    })
  }

  const loadTemplateList = async (query?: any) => {
    loading.value = true
    try {
      const params: PageQuery = {
        page: pageParams.page,
        size: pageParams.size
      }
      const response = await TemplateAPI.page(params, query)
      if (pageParams.page === 1) {
        templateList.value = response.records
      } else {
        templateList.value = [...templateList.value, ...response.records]
      }
      pageParams.total = response.total
      hasMore.value = pageParams.page * pageParams.size < response.total
    } catch (error) {
      console.error('加载模板列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  const loadMore = () => {
    if (hasMore.value && !loading.value) {
      pageParams.page++
      loadTemplateList()
    }
  }

  const searchTemplates = async (keyword: string) => {
    searchState.keyword = keyword
    pageParams.page = 1
    await loadTemplateList({ name: keyword })
  }

  const refreshPreview = () => {
    previewState.refreshKey++
  }

  // 组件管理方法
  const addComponent = (component: TemplateComponentForm) => {
    editForm.components.push(component)
    componentState.selectedIndex = editForm.components.length - 1
    refreshPreview()
  }

  const removeComponent = (index: number) => {
    editForm.components.splice(index, 1)
    if (componentState.selectedIndex === index) {
      componentState.selectedIndex = -1
    } else if (componentState.selectedIndex > index) {
      componentState.selectedIndex--
    }
    refreshPreview()
  }

  const moveComponent = (fromIndex: number, toIndex: number) => {
    const component = editForm.components[fromIndex]
    editForm.components.splice(fromIndex, 1)
    editForm.components.splice(toIndex, 0, component)
    componentState.selectedIndex = toIndex
    refreshPreview()
  }

  const selectComponent = (index: number) => {
    componentState.selectedIndex = index
  }

  return {
    // 状态
    currentTemplate,
    templateList,
    loading,
    hasMore,
    pageParams,
    editForm,
    searchState,
    previewState,
    componentState,

    // Actions
    setCurrentTemplate,
    updateEditForm,
    resetEditForm,
    loadTemplateList,
    loadMore,
    searchTemplates,
    refreshPreview,
    addComponent,
    removeComponent,
    moveComponent,
    selectComponent
  }
})