// stores/resumeTemplate.js
import { defineStore } from 'pinia';
import { ref, reactive, computed } from 'vue';

export const useTemplateStore = defineStore('template', () => {
  // 1. 当前编辑的模板 (基于你提供的JSON)
  const currentTemplate = reactive({
    id: 1,
    name: '经典简洁',
    description: '...',
    globalStyle: {
      theme: 'classic',
      fontSizes: { h1: '24px', body: '14px' },
      fontFamily: 'Microsoft YaHei, SimSun, serif',
      primaryColor: '#2c3e50',
      backgroundColor: '#ffffff',
      spacing: { sectionMargin: '20px', padding: '15px', lineHeight: '1.6' }
    },
    layout: {
      columns: 1,
      componentOrder: ['UserBasicInfo', 'EducationExperience', 'WorkExperience', 'Skills', 'ProjectExperience']
    },
    // 将你的 `sections` 转换为可编辑的组件列表
    components: []
  });

  // 2. 初始化：将 sections 数据转换为组件列表
  const initFromJSON = (jsonData) => {
    const { sections, ...templateInfo } = jsonData;
    Object.assign(currentTemplate, templateInfo);

    // 根据 layout.componentOrder 排序并转换 sections
    currentTemplate.components = currentTemplate.layout.componentOrder
      .map(compName => {
        const section = sections.find(s => s.component === compName);
        if (!section) return null;
        return {
          id: `${currentTemplate.id}_${section.id}`, // 唯一ID
          type: section.component, // 对应组件类型，如 `UserBasicInfo`
          name: section.name, // 显示名称，如“基本信息”
          props: section.props || {}, // 组件属性数据
          styles: section.styles || {}, // 组件独立样式
          // 可编辑状态
          editable: true,
          visible: true
        };
      })
      .filter(Boolean); // 过滤掉未找到的组件
  };

  // 3. 当前选中的组件（用于右侧属性编辑）
  const selectedComponentId = ref(null);
  const selectedComponent = computed(() =>
    currentTemplate.components.find(c => c.id === selectedComponentId.value)
  );

  // 4. 更新全局样式
  const updateGlobalStyle = (key, value) => {
    if (key.includes('.')) {
      // 支持嵌套路径，如 `fontSizes.h1`
      const [parent, child] = key.split('.');
      if (currentTemplate.globalStyle[parent]) {
        currentTemplate.globalStyle[parent][child] = value;
      }
    } else {
      currentTemplate.globalStyle[key] = value;
    }
  };

  // 5. 更新组件属性/样式
  const updateComponent = (componentId, updates) => {
    const component = currentTemplate.components.find(c => c.id === componentId);
    if (component) {
      Object.assign(component, updates);
    }
  };

  // 6. 拖拽重新排序组件
  const reorderComponents = (newOrder) => {
    // newOrder 是一个包含组件ID的数组
    const idToComponentMap = {};
    currentTemplate.components.forEach(comp => {
      idToComponentMap[comp.id] = comp;
    });
    currentTemplate.components = newOrder.map(id => idToComponentMap[id]).filter(Boolean);
    // 同步更新 layout.componentOrder
    currentTemplate.layout.componentOrder = currentTemplate.components.map(c => c.type);
  };

  return {
    currentTemplate,
    selectedComponentId,
    selectedComponent,
    initFromJSON,
    updateGlobalStyle,
    updateComponent,
    reorderComponents
  };
});