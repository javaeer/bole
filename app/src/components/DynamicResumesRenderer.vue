<template>
  <view
    class="resume-container"
    :style="containerStyle"
    :class="[`theme-${currentTheme}`, `layout-${globalLayout.type}`]"
  >
    <!-- 按照指定的顺序渲染组件 -->
    <block v-for="(component, index) in orderedComponents" :key="component.id || index">
      <component
        v-if="componentMap[component.key]"
        :is="componentMap[component.key]"
        :component="getComponentConfig(component)"
        :global-style="globalStyle"
        :theme="currentTheme"
        class="resume-section"
        :style="getSectionStyle(component)"
      />
      <view v-else class="unknown-component">
        <text class="warning-text">组件 "{{ component.key }}" 未找到</text>
      </view>
    </block>
  </view>
</template>

<script setup>
import { computed, defineAsyncComponent, shallowRef, onMounted } from 'vue'

// 异步导入组件
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
  resumeData: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

// 组件映射表
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
const globalStyle = computed(() => props.resumeData.globalStyle || {})
const globalLayout = computed(() => props.resumeData.globalLayout || {})

// 主题
const currentTheme = computed(() => globalStyle.value.theme || 'light')

// 所有组件
const rawComponents = computed(() => props.resumeData.components || [])

// 根据 componentOrder 排序的组件
const orderedComponents = computed(() => {
  const order = globalLayout.value.componentOrder || []
  const components = rawComponents.value

  if (order.length === 0) {
    // 如果没有指定顺序，按照传入的顺序显示
    return components
  }

  // 按照指定的顺序排序
  const ordered = []
  const unordered = []

  // 先按顺序添加
  order.forEach(key => {
    const found = components.find(comp => comp.key === key)
    if (found) {
      ordered.push(found)
    }
  })

  // 添加未在 order 中指定的组件
  components.forEach(comp => {
    if (!order.includes(comp.key)) {
      unordered.push(comp)
    }
  })

  return [...ordered, ...unordered]
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
    '--font-size-body': style.fontSizes?.body ? `${style.fontSizes.body}px` : '14px',
    '--font-size-h1': style.fontSizes?.h1 ? `${style.fontSizes.h1}px` : '32px',
    '--section-margin': spacing.sectionMargin || '20px',
    '--padding': spacing.padding || '15px',
    '--line-height': spacing.lineHeight || '1.6',
    'background-color': style.backgroundColor || '#ffffff',
    'font-family': style.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    'padding': spacing.padding || '20px',
    'color': style.textColor || '#333333'
  }
})

// 处理字段值，避免访问错误的属性
const safeValue = (value, defaultValue = null) => {
  if (value === null || value === undefined) {
    return defaultValue
  }
  if (typeof value === 'string' && value.includes('~数据超出限制')) {
    return defaultValue
  }
  return value
}

// 获取组件配置
const getComponentConfig = (component) => {
  if (!component) {
    return {
      id: '',
      name: '未选择组件',
      key: '',
      props: {},
      styles: {}
    }
  }

  // 处理默认配置
  const defaultConfig = component.defaultConfig || {}
  const propsData = component.props || {}
  const styles = component.styles || {}

  // 特殊处理：根据组件类型转换数据结构
  const processedProps = processComponentData(component.key, propsData, defaultConfig)

  return {
    id: component.id,
    name: component.name,
    key: component.key,
    props: { ...defaultConfig.props, ...processedProps },
    styles: { ...defaultConfig.styles, ...styles },
    defaultConfig: defaultConfig
  }
}

// 处理组件数据，确保数据结构统一
const processComponentData = (key, propsData, defaultConfig) => {
  const processed = { ...propsData }

  switch (key) {
    case 'UserBasicInfo':
      // 确保基础信息字段存在
      processed.name = safeValue(processed.name, '')
      processed.email = safeValue(processed.email, '')
      processed.phone = safeValue(processed.phone, '')
      processed.location = safeValue(processed.location, '')
      processed.avatar = safeValue(processed.avatar, '')
      processed.title = safeValue(processed.title, '')
      processed.workYears = safeValue(processed.workYears, 0)
      break

    case 'WorkExperience':
    case 'CompanyExperience':
    case 'ProjectExperience':
      // 统一 experiences 字段
      let experiences = safeValue(processed.experiences, [])
      if (!Array.isArray(experiences)) {
        experiences = []
      }
      processed.experiences = experiences.map(exp => ({
        ...exp,
        // 确保必要字段存在
        id: safeValue(exp.id, Date.now()),
        company: safeValue(exp.company, '未指定公司'),
        position: safeValue(exp.position, '职位未填写'),
        startDate: safeValue(exp.startDate, ''),
        endDate: safeValue(exp.endDate, exp.isCurrent ? '' : ''),
        description: safeValue(exp.description, ''),
        // 转换技能和成就数组
        skills: Array.isArray(exp.skills) ? exp.skills : [],
        achievements: Array.isArray(exp.achievements) ? exp.achievements : [],
        duration: safeValue(exp.duration, ''),
        department: safeValue(exp.department, '')
      }))
      break

    case 'EducationExperience':
      // 教育经历特殊处理
      let eduExperiences = safeValue(processed.experiences, [])
      if (!Array.isArray(eduExperiences)) {
        eduExperiences = []
      }
      processed.experiences = eduExperiences.map(exp => ({
        ...exp,
        id: safeValue(exp.id, Date.now()),
        school: safeValue(exp.school, '未指定学校'),
        degree: safeValue(exp.degree, '学历未填写'),
        major: safeValue(exp.major, ''),
        startDate: safeValue(exp.startDate, ''),
        endDate: safeValue(exp.endDate, ''),
        description: safeValue(exp.description, ''),
        courses: Array.isArray(exp.courses) ? exp.courses : [],
        achievements: Array.isArray(exp.achievements) ? exp.achievements : [],
        gpa: safeValue(exp.gpa, ''),
        ranking: safeValue(exp.ranking, '')
      }))
      break

    case 'JobIntention':
      // 确保 intentions 字段
      let intentions = safeValue(processed.intentions, [])
      if (!Array.isArray(intentions)) {
        intentions = []
      }
      processed.intentions = intentions.map(intention => ({
        ...intention,
        id: safeValue(intention.id, Date.now()),
        position: safeValue(intention.position, '职位未填写'),
        salary: safeValue(intention.salary, '面议'),
        jobType: safeValue(intention.jobType, '全职'),
        city: safeValue(intention.city, '')
      }))
      break

    case 'SelfEvaluation':
      // 确保 evaluations 字段
      let evaluations = safeValue(processed.evaluations, [])
      if (!Array.isArray(evaluations)) {
        evaluations = []
      }
      processed.evaluations = evaluations.map(evalItem => ({
        ...evalItem,
        id: safeValue(evalItem.id, Date.now()),
        content: safeValue(evalItem.content, ''),
        createdAt: safeValue(evalItem.createdAt, '')
      }))
      break

    case 'Skills':
      // 确保 skills 字段
      let skills = safeValue(processed.skills, [])
      if (!Array.isArray(skills)) {
        skills = []
      }
      processed.skills = skills.map(skill => ({
        ...skill,
        id: safeValue(skill.id, Date.now()),
        name: safeValue(skill.name, '技能名称'),
        category: safeValue(skill.category, '其他'),
        proficiencyPercent: safeValue(skill.proficiencyPercent, 0),
        level: safeValue(skill.level, '初级'),
        experienceYears: safeValue(skill.experienceYears, 0),
        description: safeValue(skill.description, ''),
        tags: safeValue(skill.tags, ''),
        isCertified: safeValue(skill.isCertified, false),
        certificateName: safeValue(skill.certificateName, ''),
        certificateDate: safeValue(skill.certificateDate, '')
      }))
      break
  }

  return processed
}

// 获取区块样式
const getSectionStyle = (component) => {
  const styles = component?.styles || {}

  return {
    marginBottom: globalStyle.value.spacing?.sectionMargin || '20px',
    ...styles
  }
}

onMounted(() => {
  console.log('动态渲染器加载完成', {
    组件总数: rawComponents.value.length,
    排序后组件: orderedComponents.value.map(c => c.key),
    主题: currentTheme.value,
    布局: globalLayout.value.type
  })
})
</script>

<style lang="scss" scoped>
.resume-container {
  min-height: 100vh;
  transition: all 0.3s ease;

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

  &.layout-three-column {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
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

@media (max-width: 768px) {
  .resume-container {
    padding: 15px !important;

    &.layout-two-column,
    &.layout-three-column {
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