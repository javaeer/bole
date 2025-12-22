<template>
  <view
    class="resume-container"
    :style="containerStyle"
    :class="[`theme-${currentTheme}`, `layout-${globalLayout.type}`]"
  >
    <!-- 按照指定的顺序渲染组件 -->
    <block v-for="(componentKey, index) in orderedComponents" :key="index">
      <component
        v-if="componentMap[componentKey]"
        :is="componentMap[componentKey]"
        :config="getComponentConfig(componentKey)"
        :theme="currentTheme"
        class="resume-section"
        :style="getSectionStyle(componentKey)"
      />
      <!-- 未知组件占位符 -->
      <view v-else class="unknown-component">
        <text class="warning-text">组件 "{{ componentKey }}" 未找到</text>
      </view>
    </block>
  </view>
</template>

<script setup>
import { computed, defineAsyncComponent, shallowRef, onMounted } from 'vue'

// 异步导入组件（优化首屏加载）
const UserBasicInfo = defineAsyncComponent(() =>
  import('@/components/resumes/UserBasicInfo.vue')
)
const JobIntention = defineAsyncComponent(() =>
  import('@/components/resumes/JobIntention.vue')
)
const CompanyExperience = defineAsyncComponent(() =>
  import('@/components/resumes/CompanyExperience.vue')
)
const WorkExperience = defineAsyncComponent(() =>
  import('@/components/resumes/WorkExperience.vue')
)
const ProjectExperience = defineAsyncComponent(() =>
  import('@/components/resumes/ProjectExperience.vue')
)
const EducationExperience = defineAsyncComponent(() =>
  import('@/components/resumes/EducationExperience.vue')
)
const SelfEvaluation = defineAsyncComponent(() =>
  import('@/components/resumes/SelfEvaluation.vue')
)
const Skills = defineAsyncComponent(() =>
  import('@/components/resumes/Skills.vue')
)

const props = defineProps({
  config: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

// 组件映射表（根据 key 映射）
const componentMap = shallowRef({
  'UserBasicInfo': UserBasicInfo,
  'JobIntention': JobIntention,
  'CompanyExperience': CompanyExperience,
  'WorkExperience': WorkExperience,
  'ProjectExperience': ProjectExperience,
  'EducationExperience': EducationExperience,
  'SelfEvaluation': SelfEvaluation,
  'Skills': Skills
})

// 全局样式
const globalStyle = computed(() => props.config.globalStyle || {})
const globalLayout = computed(() => props.config.globalLayout || {})

// 主题
const currentTheme = computed(() => globalStyle.value.theme || 'light')

// 所有组件
const rawComponents = computed(() => props.config.components || [])

// 组件配置缓存
const componentConfigCache = shallowRef({})

// 根据 componentOrder 排序的组件
const orderedComponents = computed(() => {
  const order = globalLayout.value.componentOrder || []
  const components = rawComponents.value

  if (order.length === 0) {
    // 如果没有指定顺序，按照传入的顺序显示
    return components.map(comp => comp.key)
  }

  // 按照指定的顺序排序
  return order
    .map(key => {
      const found = components.find(comp => comp.key === key)
      return found ? key : null
    })
    .filter(Boolean)
    .concat(
      // 添加未在 order 中指定的组件
      components
        .filter(comp => !order.includes(comp.key))
        .map(comp => comp.key)
    )
})

// 容器样式
const containerStyle = computed(() => {
  const style = globalStyle.value
  const spacing = style.spacing || {}

  return {
    '--primary-color': style.primaryColor || '#1890ff',
    '--secondary-color': style.secondaryColor || '#52c41a',
    '--accent-color': style.accentColor || '#faad14',
    '--background-color': style.backgroundColor || '#ffffff',
    '--text-color': style.textColor || '#333333',
    '--font-family': style.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    '--font-size-body': style.fontSizes?.body || '14px',
    '--font-size-h1': style.fontSizes?.h1 || '32px',
    '--section-margin': spacing.sectionMargin || '20px',
    '--padding': spacing.padding || '15px',
    '--line-height': spacing.lineHeight || '1.6',
    'background-color': style.backgroundColor || '#ffffff',
    'font-family': style.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    'padding': spacing.padding || '20px',
    'color': style.textColor || '#333333'
  }
})

// 获取组件配置
const getComponentConfig = (componentKey) => {
  if (componentConfigCache.value[componentKey]) {
    return componentConfigCache.value[componentKey]
  }

  const component = rawComponents.value.find(comp => comp.key === componentKey)
  if (!component) {
    console.warn(`组件 ${componentKey} 未找到`)
    return null
  }

  // 合并默认配置和实际配置
  const config = {
    props: {
      ...component.defaultConfig?.props,
      ...component.props
    },
    styles: {
      ...component.defaultConfig?.styles,
      ...component.styles
    },
    defaultConfig: component.defaultConfig
  }

  // 缓存配置
  componentConfigCache.value[componentKey] = config
  return config
}

// 获取区块样式
const getSectionStyle = (componentKey) => {
  const component = rawComponents.value.find(comp => comp.key === componentKey)
  const styles = component?.styles || {}

  return {
    marginBottom: globalStyle.value.spacing?.sectionMargin || '20px',
    ...styles
  }
}

// 日志和调试
onMounted(() => {
  console.log('动态渲染器加载完成', {
    组件总数: rawComponents.value.length,
    排序后组件: orderedComponents.value,
    主题: currentTheme.value,
    布局: globalLayout.value.type
  })
})
</script>

<style lang="scss" scoped>
.resume-container {
  min-height: 100vh;
  transition: all 0.3s ease;

  // 主题样式变量
  &.theme-light {
    --primary-color: #1890ff;
    --secondary-color: #52c41a;
    --background-color: #ffffff;
    --text-color: #333333;
  }

  &.theme-dark {
    --primary-color: #177ddc;
    --secondary-color: #49aa19;
    --background-color: #141414;
    --text-color: #ffffff;
  }

  // 布局样式
  &.layout-single-column {
    max-width: 800px;
    margin: 0 auto;
  }

  &.layout-two-column {
    display: grid;
    grid-template-columns: var(--left-width, 40%) var(--right-width, 60%);
    gap: var(--section-margin, 20px);

    .resume-section {
      margin-bottom: 0;
    }
  }

  .resume-section {
    transition: transform 0.3s ease, opacity 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }
  }

  // 未知组件样式
  .unknown-component {
    padding: 40px;
    background-color: #fff3cd;
    border: 1px solid #ffeaa7;
    border-radius: 8px;
    margin-bottom: var(--section-margin);
    text-align: center;

    .warning-text {
      color: #856404;
      font-size: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .resume-container {
    padding: 15px !important;

    &.layout-two-column {
      grid-template-columns: 1fr;

      .resume-section {
        margin-bottom: var(--section-margin);
      }
    }
  }
}

@media (min-width: 769px) and (max-width: 1200px) {
  .resume-container {
    padding: 25px !important;

    &.layout-two-column {
      grid-template-columns: 45% 55%;
    }
  }
}

@media (min-width: 1201px) {
  .resume-container {
    padding: 40px !important;
    max-width: 1200px;
    margin: 0 auto;

    &.layout-single-column {
      max-width: 900px;
    }
  }
}

// 打印样式
@media print {
  .resume-container {
    padding: 0 !important;
    background-color: #ffffff !important;

    .resume-section {
      page-break-inside: avoid;
      margin-bottom: 16px;
    }
  }
}
</style>