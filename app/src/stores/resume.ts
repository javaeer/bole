import { reactive, computed } from 'vue'

export function useResumeEditStore() {
  // 主要数据源
  const state = reactive({
    // 原始数据（从API获取）
    rawData: null,
    // 编辑中的数据
    editingData: null,
    // 预览数据（计算属性，自动同步）
    previewData: computed(() => {
      if (!state.editingData) return null

      const { globalStyle, globalLayout, components, ...rest } = state.editingData

      return {
        ...rest,
        globalStyle: { ...globalStyle },
        globalLayout: { ...globalLayout },
        components: (components || []).map(component => ({
          id: component.id,
          name: component.name,
          key: component.key,
          defaultConfig: component.defaultConfig || {},
          props: component.props || {},
          styles: component.styles || {},
          createdAt: component.createdAt,
          updatedAt: component.updatedAt,
          deleted: component.deleted || 0,
          templateId: component.templateId,
          componentId: component.componentId
        }))
      }
    })
  })

  // 初始化编辑数据
  const initializeData = (apiData) => {
    state.rawData = JSON.parse(JSON.stringify(apiData))
    state.editingData = JSON.parse(JSON.stringify(apiData))
  }

  // 更新组件属性
  const updateComponentProp = (componentId, propPath, value) => {
    if (!state.editingData?.components) return

    const component = state.editingData.components.find(c => c.id === componentId)
    if (!component) return

    // 支持深层属性路径，如 'experiences.0.company'
    if (typeof propPath === 'string' && propPath.includes('.')) {
      const path = propPath.split('.')
      let obj = component.props
      for (let i = 0; i < path.length - 1; i++) {
        if (!obj[path[i]]) obj[path[i]] = {}
        obj = obj[path[i]]
      }
      obj[path[path.length - 1]] = value
    } else {
      if (!component.props) component.props = {}
      component.props[propPath] = value
    }
  }

  // 添加数组项
  const addArrayItem = (componentId, arrayPath, defaultValue = {}) => {
    const component = state.editingData.components.find(c => c.id === componentId)
    if (!component) return

    const path = arrayPath.split('.')
    let obj = component.props
    for (let i = 0; i < path.length - 1; i++) {
      if (!obj[path[i]]) obj[path[i]] = {}
      obj = obj[path[i]]
    }

    const array = obj[path[path.length - 1]]
    if (!Array.isArray(array)) {
      obj[path[path.length - 1]] = [defaultValue]
    } else {
      array.push({ ...defaultValue, id: Date.now() })
    }
  }

  // 删除数组项
  const removeArrayItem = (componentId, arrayPath, index) => {
    const component = state.editingData.components.find(c => c.id === componentId)
    if (!component) return

    const path = arrayPath.split('.')
    let obj = component.props
    for (let i = 0; i < path.length - 1; i++) {
      obj = obj[path[i]]
    }

    if (Array.isArray(obj[path[path.length - 1]])) {
      obj[path[path.length - 1]].splice(index, 1)
    }
  }

  // 重置修改
  const resetChanges = () => {
    if (state.rawData) {
      state.editingData = JSON.parse(JSON.stringify(state.rawData))
    }
  }

  // 获取当前编辑数据
  const getFormData = () => {
    return state.editingData
  }

  return {
    state,
    initializeData,
    updateComponentProp,
    addArrayItem,
    removeArrayItem,
    resetChanges,
    getFormData
  }
}