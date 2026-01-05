<template>
  <view
    :class="['self-evaluation', `theme-${theme}`, `layout-${layoutType}`]"
    :style="containerStyle"
  >
    <!-- 区块标题 -->
    <view class="section-header" :style="headerStyle">
      <text class="section-title" :style="titleStyle">{{ title }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasEvaluationData" class="empty-state" :style="emptyStateStyle">
      <text class="empty-icon">💭</text>
      <text class="empty-text" :style="emptyTextStyle">暂无自我评价</text>
      <text class="empty-hint" :style="emptyHintStyle">请添加您的个人优势、工作态度或职业目标</text>
    </view>

    <!-- 多评价内容 -->
    <view v-else class="evaluations-list">
      <block v-for="(evaluation, index) in displayEvaluations" :key="evaluation.id || index">
        <view class="evaluation-item" :style="getEvaluationItemStyle(index)">
          <!-- 评价内容 -->
          <view v-if="evaluation.content" class="evaluation-content">
            <text class="content-text" :style="contentStyle">{{ evaluation.content }}</text>

            <!-- 评价时间（可选） -->
            <view v-if="showCreateTime && evaluation.createdAt" class="evaluation-time">
              <text class="time-icon">📅</text>
              <text class="time-text" :style="timeStyle">{{ formatDate(evaluation.createdAt) }}</text>
            </view>
          </view>
        </view>

        <!-- 分隔线 -->
        <view
          v-if="index < displayEvaluations.length - 1"
          class="item-divider"
          :style="dividerStyle"
        ></view>
      </block>

      <!-- 显示更多/收起按钮 -->
      <view
        v-if="evaluations.length > maxDisplayItems"
        class="show-more-btn"
        :style="showMoreButtonStyle"
        @click="toggleShowAll"
      >
        <text class="btn-text">
          {{ showAll ? '收起' : `查看更多（${evaluations.length - maxDisplayItems}项）` }}
        </text>
        <text class="btn-icon">{{ showAll ? '↑' : '↓' }}</text>
      </view>

      <!-- 默认配置中的关键词 -->
      <view
        v-if="showCharacterTraits && characterTraits.length > 0"
        class="default-traits"
        :style="traitsContainerStyle"
      >
        <text class="traits-title" :style="traitsTitleStyle">性格特点</text>
        <view class="traits-container">
          <text
            v-for="(trait, index) in characterTraits"
            :key="index"
            :class="['trait-tag', `trait-${index % 4}`]"
            :style="getTraitTagStyle(index)"
          >
            {{ trait }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  component: {
    type: Object,
    default: () => ({})
  },
  globalStyle: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'light'
  }
})

const showAll = ref(false)
const maxDisplayItems = ref(2)

// 提取配置
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => props.component?.styles || {})
const defaultConfig = computed(() => props.component?.defaultConfig || {})

// 获取布局类型
const layoutType = computed(() => {
  return componentStyles.value.layoutType || 'single-column'
})

// 标题
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '自我评价')

// 评价数据适配
const evaluations = computed(() => {
  const rawEvaluations = componentProps.value.evaluations || []

  if (rawEvaluations.length === 0) {
    // 如果没有数组数据，尝试从单个字段构造
    const content = componentProps.value.content
    if (content) {
      return [{
        id: 1,
        content: content,
        createdAt: componentProps.value.createdAt
      }]
    }
  }

  return rawEvaluations.sort((a, b) => {
    // 按ID降序排列，显示最新的评价
    return (b.id || 0) - (a.id || 0)
  })
})

const hasEvaluationData = computed(() => evaluations.value.length > 0)

// 显示的评价列表
const displayEvaluations = computed(() => {
  if (showAll.value) {
    return evaluations.value
  }
  return evaluations.value.slice(0, maxDisplayItems.value)
})

// 默认配置中的性格特点
const characterTraits = computed(() => {
  return componentProps.value.characterTraits || defaultConfig.value.props?.characterTraits || []
})

// 显示选项
const showCharacterTraits = computed(() => componentProps.value.showCharacterTraits ?? defaultConfig.value.props?.showCharacterTraits ?? true)
const showCreateTime = computed(() => componentProps.value.showCreateTime ?? defaultConfig.value.props?.showCreateTime ?? true)

// 🚀 核心修复：样式计算
const containerStyle = computed(() => {
  const style = componentStyles.value
  const global = props.globalStyle || {}
  const defaultStyles = defaultConfig.value.styles || {}

  // 优先级：组件自定义样式 > 默认样式 > 全局样式
  return {
    // 布局和间距
    marginBottom: style.marginBottom || defaultStyles.marginBottom || '24px',
    marginTop: style.marginTop || defaultStyles.marginTop || '0',
    padding: style.padding || defaultStyles.padding || '20px',

    // 背景和边框
    backgroundColor: style.backgroundColor || defaultStyles.backgroundColor || global.backgroundColor || '#ffffff',
    border: style.border || defaultStyles.border || 'none',
    borderRadius: style.borderRadius || defaultStyles.borderRadius || '8px',
    boxShadow: style.boxShadow || defaultStyles.boxShadow || '0 2px 8px rgba(0,0,0,0.1)',

    // 字体
    fontFamily: style.fontFamily || defaultStyles.fontFamily || global.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    fontSize: style.fontSize || defaultStyles.fontSize || global.fontSizes?.body ? `${global.fontSizes.body}px` : '14px',
    lineHeight: style.lineHeight || defaultStyles.lineHeight || global.spacing?.lineHeight || '1.6',
    color: style.color || defaultStyles.color || global.textColor || '#333333',

    // CSS变量 - 用于子元素继承
    '--primary-color': style.primaryColor || defaultStyles.primaryColor || global.primaryColor || '#1890ff',
    '--secondary-color': style.secondaryColor || defaultStyles.secondaryColor || global.secondaryColor || '#52c41a',
    '--accent-color': style.accentColor || defaultStyles.accentColor || global.accentColor || '#faad14',
    '--text-color': style.color || defaultStyles.color || global.textColor || '#333333',
    '--title-color': style.titleColor || defaultStyles.titleColor || global.primaryColor || '#1890ff',
  }
})

// 区块标题样式
const headerStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    marginBottom: style.headerMargin || defaultStyles.headerMargin || '20px',
    paddingBottom: style.headerPaddingBottom || defaultStyles.headerPaddingBottom || '10px',
    borderBottom: style.headerBorder || defaultStyles.headerBorder || '1px solid #f0f0f0'
  }
})

const titleStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}
  const global = props.globalStyle || {}

  return {
    color: style.titleColor || defaultStyles.titleColor || global.primaryColor || '#1890ff',
    fontSize: style.titleFontSize || defaultStyles.titleFontSize || global.fontSizes?.h1 ? `${global.fontSizes.h1}px` : '24px',
    fontWeight: style.titleFontWeight || defaultStyles.titleFontWeight || 'bold',
    marginBottom: style.titleMarginBottom || defaultStyles.titleMarginBottom || '10px'
  }
})

const dividerStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    backgroundColor: style.dividerColor || defaultStyles.dividerColor || 'var(--primary-color)',
    height: style.dividerHeight || defaultStyles.dividerHeight || '2px',
    width: style.dividerWidth || defaultStyles.dividerWidth || '80px',
    marginTop: style.dividerMarginTop || defaultStyles.dividerMarginTop || '8px'
  }
})

// 空状态样式
const emptyStateStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    backgroundColor: style.emptyBackground || defaultStyles.emptyBackground || '#fafafa',
    border: style.emptyBorder || defaultStyles.emptyBorder || '1px dashed #e0e0e0',
    borderRadius: style.emptyBorderRadius || defaultStyles.emptyBorderRadius || '8px',
    padding: style.emptyPadding || defaultStyles.emptyPadding || '40px 20px'
  }
})

const emptyTextStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    color: style.emptyTextColor || defaultStyles.emptyTextColor || '#666',
    fontSize: style.emptyTextSize || defaultStyles.emptyTextSize || '16px',
    fontWeight: style.emptyTextWeight || defaultStyles.emptyTextWeight || '500'
  }
})

const emptyHintStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    color: style.emptyHintColor || defaultStyles.emptyHintColor || '#999',
    fontSize: style.emptyHintSize || defaultStyles.emptyHintSize || '14px'
  }
})

// 评价项样式
const getEvaluationItemStyle = (index) => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  const baseStyle = {
    backgroundColor: style.cardBackground || defaultStyles.cardBackground || '#ffffff',
    border: style.cardBorder || defaultStyles.cardBorder || '1px solid #f0f0f0',
    borderRadius: style.cardBorderRadius || defaultStyles.cardBorderRadius || '8px',
    padding: style.cardPadding || defaultStyles.cardPadding || '20px',
    marginBottom: style.itemSpacing || defaultStyles.itemSpacing || '16px',
    boxShadow: style.cardShadow || defaultStyles.cardShadow || '0 2px 4px rgba(0,0,0,0.05)'
  }

  // 根据索引添加特殊样式
  if (index === 0) {
    baseStyle.marginTop = style.firstItemMarginTop || defaultStyles.firstItemMarginTop || '0'
  }

  return baseStyle
}

// 内容样式
const contentStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}
  const global = props.globalStyle || {}

  return {
    color: style.contentColor || defaultStyles.contentColor || global.textColor || '#444',
    fontSize: style.contentFontSize || defaultStyles.contentFontSize || global.fontSizes?.body ? `${global.fontSizes.body}px` : '14px',
    lineHeight: style.contentLineHeight || defaultStyles.contentLineHeight || global.spacing?.lineHeight || '1.8',
    textAlign: style.contentAlign || defaultStyles.contentAlign || 'left'
  }
})

// 时间样式
const timeStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    color: style.timeColor || defaultStyles.timeColor || '#999',
    fontSize: style.timeFontSize || defaultStyles.timeFontSize || '12px'
  }
})

// 显示更多按钮样式
const showMoreButtonStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    backgroundColor: style.buttonBackground || defaultStyles.buttonBackground || '#f5f7fa',
    color: style.buttonColor || defaultStyles.buttonColor || 'var(--primary-color)',
    border: style.buttonBorder || defaultStyles.buttonBorder || '1px solid #e4e7ed',
    borderRadius: style.buttonBorderRadius || defaultStyles.buttonBorderRadius || '6px',
    padding: style.buttonPadding || defaultStyles.buttonPadding || '12px',
    fontSize: style.buttonFontSize || defaultStyles.buttonFontSize || '14px'
  }
})

// 性格特点容器样式
const traitsContainerStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    backgroundColor: style.traitsBackground || defaultStyles.traitsBackground || '#fafafa',
    border: style.traitsBorder || defaultStyles.traitsBorder || '1px solid #f0f0f0',
    borderRadius: style.traitsBorderRadius || defaultStyles.traitsBorderRadius || '6px',
    padding: style.traitsPadding || defaultStyles.traitsPadding || '20px',
    marginTop: style.traitsMarginTop || defaultStyles.traitsMarginTop || '24px'
  }
})

const traitsTitleStyle = computed(() => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  return {
    color: style.traitsTitleColor || defaultStyles.traitsTitleColor || '#666',
    fontSize: style.traitsTitleSize || defaultStyles.traitsTitleSize || '16px',
    fontWeight: style.traitsTitleWeight || defaultStyles.traitsTitleWeight || '500'
  }
})

const getTraitTagStyle = (index) => {
  const style = componentStyles.value
  const defaultStyles = defaultConfig.value.styles || {}

  // 预定义的颜色数组
  const colors = [
    { bg: 'rgba(212, 175, 55, 0.1)', color: '#d4af37', border: 'rgba(212, 175, 55, 0.2)' },
    { bg: 'rgba(103, 194, 58, 0.1)', color: '#67c23a', border: 'rgba(103, 194, 58, 0.2)' },
    { bg: 'rgba(64, 158, 255, 0.1)', color: '#409eff', border: 'rgba(64, 158, 255, 0.2)' },
    { bg: 'rgba(230, 162, 60, 0.1)', color: '#e6a23c', border: 'rgba(230, 162, 60, 0.2)' }
  ]

  const colorIndex = index % 4
  const defaultColor = colors[colorIndex]

  return {
    backgroundColor: style.traitTagBackground || defaultStyles.traitTagBackground || defaultColor.bg,
    color: style.traitTagColor || defaultStyles.traitTagColor || defaultColor.color,
    border: style.traitTagBorder || defaultStyles.traitTagBorder || `1px solid ${defaultColor.border}`,
    borderRadius: style.traitTagBorderRadius || defaultStyles.traitTagBorderRadius || '20px',
    fontSize: style.traitTagFontSize || defaultStyles.traitTagFontSize || '14px',
    padding: style.traitTagPadding || defaultStyles.traitTagPadding || '4px 12px'
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''

  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}.${month}.${day}`
  } catch (error) {
    // 如果解析失败，尝试简单的格式化
    const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/)
    if (match) {
      return `${match[1]}.${match[2]}.${match[3]}`
    }
    return dateStr
  }
}

// 切换显示全部
const toggleShowAll = () => {
  showAll.value = !showAll.value
}

// 组件加载日志
onMounted(() => {
  console.log('自我评价组件加载完成', {
    评价数量: evaluations.value.length,
    显示数量: displayEvaluations.value.length,
    性格特点数量: characterTraits.value.length,
    组件样式: componentStyles.value,
    全局样式: props.globalStyle,
    标题: title.value
  })
})
</script>

<style lang="scss" scoped>
.self-evaluation {
  transition: all 0.3s ease;

  // 使用CSS变量
  color: var(--text-color);
  font-family: var(--font-family);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  // 布局适配
  &.layout-two-column {
    margin-right: 10px;

    &:last-child {
      margin-right: 0;
    }
  }

  &.layout-three-column {
    margin-right: 8px;

    &:last-child {
      margin-right: 0;
    }
  }

  // 区块标题
  .section-header {
    .section-title {
      display: block;
      font-weight: bold;
    }

    .section-divider {
      transition: all 0.3s ease;
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    transition: all 0.3s ease;

    .empty-icon {
      font-size: 24px;
      margin-bottom: 12px;
    }

    .empty-text {
      margin-bottom: 8px;
    }
  }

  // 评价项
  .evaluation-item {
    transition: all 0.3s ease;

    .evaluation-content {
      .content-text {
        white-space: pre-wrap;
        word-break: break-word;
        display: block;
        margin-bottom: 16px;
      }

      .evaluation-time {
        display: flex;
        align-items: center;
        padding-top: 16px;
        border-top: 1px dashed #f0f0f0;

        .time-icon {
          margin-right: 8px;
          font-size: 12px;
        }
      }
    }
  }

  // 分隔线
  .item-divider {
    transition: all 0.3s ease;
  }

  // 显示更多按钮
  .show-more-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      opacity: 0.9;
    }

    .btn-text {
      margin-right: 8px;
    }

    .btn-icon {
      font-size: 12px;
    }
  }

  // 默认性格特点
  .default-traits {
    transition: all 0.3s ease;

    .traits-title {
      display: block;
      margin-bottom: 16px;
    }

    .traits-container {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;

      .trait-tag {
        display: inline-flex;
        align-items: center;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
}

// 主题适配
.self-evaluation.theme-classic {
  .section-title {
    color: var(--primary-color);
  }

  .section-divider {
    background: var(--primary-color);
  }

  .show-more-btn {
    background: #f8f9fa;
    border-color: #e9ecef;
  }
}

.self-evaluation.theme-light {
  .section-title {
    color: #1890ff;
  }

  .section-divider {
    background: linear-gradient(90deg, #1890ff, #52c41a);
  }
}

.self-evaluation.theme-dark {
  background-color: #141414;
  color: #ffffff;

  .section-title {
    color: #177ddc;
  }

  .evaluation-item {
    background-color: #1f1f1f;
    border-color: #434343;
  }

  .empty-state {
    background-color: #262626;
    border-color: #434343;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .self-evaluation {
    padding: 15px !important;

    .evaluation-item {
      padding: 15px !important;
    }

    .traits-container {
      gap: 8px !important;
    }
  }
}

@media print {
  .self-evaluation {
    box-shadow: none !important;
    border: 1px solid #e8e8e8 !important;

    .show-more-btn {
      display: none !important;
    }
  }
}
</style>