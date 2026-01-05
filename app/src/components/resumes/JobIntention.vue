<template>
  <view
    :class="['job-intention', `layout-${layout}`, `theme-${theme}`]"
    :style="containerStyle"
  >
    <view class="section-header" :style="headerStyle">
      <text class="section-title" :style="titleStyle">{{ title }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasIntentionData" class="empty-state" :style="emptyStateStyle">
      <text class="empty-icon">💼</text>
      <text class="empty-text">暂无求职意向</text>
      <text class="empty-hint">请添加您的求职期望</text>
    </view>

    <!-- 多求职意向列表 -->
    <view v-else class="intentions-list">
      <block v-for="(intention, index) in displayIntentions" :key="intention.id || index">
        <view class="intention-item" :style="itemStyle">
          <!-- 主要求职信息 -->
          <view class="primary-info">
            <text class="position" :style="positionStyle">{{ intention.position }}</text>
            <view class="meta-info" :style="metaInfoStyle">
              <text v-if="intention.city && showLocation" class="meta-item" :style="metaItemStyle">
                <text class="icon">📍</text>
                {{ intention.city }}
              </text>
              <text v-if="showSalary && intention.salary" class="meta-item" :style="metaItemStyle">
                <text class="icon">💰</text>
                {{ formatSalary(intention.salary) }}
              </text>
              <text v-if="showJobType" class="meta-item" :style="metaItemStyle">
                <text class="icon">🕐</text>
                {{ formatJobType(intention.jobType) }}
              </text>
            </view>
          </view>

          <!-- 工作类型标签 -->
          <view v-if="intention.jobType && showJobTypeTag" class="job-type-tag" :style="tagStyle">
            <text class="tag-text">{{ formatJobType(intention.jobType) }}</text>
          </view>
        </view>

        <!-- 分隔线 -->
        <view v-if="index < displayIntentions.length - 1" class="item-divider" :style="dividerLineStyle"></view>
      </block>

      <!-- 显示更多/收起按钮 -->
      <view v-if="intentions.length > maxDisplayItems" class="show-more-btn" @click="toggleShowAll" :style="buttonStyle">
        <text class="btn-text">
          {{ showAll ? '收起' : `查看更多（${intentions.length - maxDisplayItems}项）` }}
        </text>
        <text class="btn-icon">{{ showAll ? '↑' : '↓' }}</text>
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
const maxDisplayItems = ref(3)

// 🚀 修复点1：正确提取组件配置
const componentConfig = computed(() => {
  return props.component || {}
})

// 提取配置
const componentProps = computed(() => componentConfig.value.props || {})
const componentStyles = computed(() => componentConfig.value.styles || {})
const defaultConfig = computed(() => componentConfig.value.defaultConfig || {})

// 🚀 修复点2：合并样式配置
const mergedStyles = computed(() => {
  const defaultStyles = defaultConfig.value.styles || {}
  const customStyles = componentStyles.value

  // 优先级：自定义样式 > 默认样式 > 全局样式
  return {
    ...defaultStyles,
    ...customStyles
  }
})

// 🚀 修复点3：从全局样式中获取CSS变量
const cssVariables = computed(() => {
  return {
    '--primary-color': props.globalStyle.primaryColor || '#2c3e50',
    '--secondary-color': props.globalStyle.secondaryColor || '#2c3e50',
    '--accent-color': props.globalStyle.accentColor || '#2c3e50',
    '--background-color': props.globalStyle.backgroundColor || '#f8f9fa',
    '--text-color': props.globalStyle.textColor || '#333333',
    '--font-family': props.globalStyle.fontFamily || "'Times New Roman', serif",
    '--font-size-body': props.globalStyle.fontSizes?.body ? `${props.globalStyle.fontSizes.body}px` : '16px',
    '--font-size-h1': props.globalStyle.fontSizes?.h1 ? `${props.globalStyle.fontSizes.h1}px` : '36px',
  }
})

// 🚀 修复点4：容器样式 - 应用CSS变量和组件样式
const containerStyle = computed(() => {
  return {
    ...cssVariables.value,
    marginBottom: props.globalStyle.spacing?.sectionMargin || '24px',
    padding: mergedStyles.value.padding || '20px',
    borderRadius: mergedStyles.value.borderRadius || '8px',
    backgroundColor: mergedStyles.value.backgroundColor || mergedStyles.value.cardBackground || '#ffffff',
    boxShadow: mergedStyles.value.boxShadow || '0 2px 8px rgba(0,0,0,0.1)',
    // 其他组件特定样式
    ...componentStyles.value
  }
})

// 标题相关计算
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '求职意向')

const titleStyle = computed(() => ({
  color: mergedStyles.value.titleColor || cssVariables.value['--primary-color'],
  fontSize: mergedStyles.value.titleFontSize || cssVariables.value['--font-size-h1'],
  fontWeight: 'bold'
}))

const headerStyle = computed(() => ({
  marginBottom: '20px'
}))

const dividerStyle = computed(() => ({
  background: mergedStyles.value.highlightColor || cssVariables.value['--accent-color'],
  height: '2px',
  width: '60px',
  marginTop: '8px'
}))

// 求职意向数据适配
const intentions = computed(() => {
  // 优先从 intentions 字段获取
  const rawIntentions = componentProps.value.intentions || []

  console.log('求职意向数据:', {
    原始数据: componentProps.value,
    intentions字段: rawIntentions,
    组件props: componentProps.value
  })

  // 如果没有数组数据，尝试从单个字段构造（兼容旧数据格式）
  if (rawIntentions.length === 0) {
    const position = componentProps.value.position
    if (position) {
      return [{
        id: Date.now(),
        position: position,
        salary: componentProps.value.salary,
        jobType: componentProps.value.jobType || '全职',
        city: componentProps.value.city || componentProps.value.location || ''
      }]
    }
  }

  // 处理接口返回的数据结构
  return rawIntentions.map(item => ({
    id: item.id || Date.now(),
    position: item.position || '',
    salary: item.salary || '',
    jobType: item.jobType || '全职',
    city: item.city || ''
  })).sort((a, b) => {
    // 按ID降序排列，显示最新的意向
    return (b.id || 0) - (a.id || 0)
  })
})

const hasIntentionData = computed(() => intentions.value.length > 0)

// 显示的意向列表
const displayIntentions = computed(() => {
  if (showAll.value) {
    return intentions.value
  }
  return intentions.value.slice(0, maxDisplayItems.value)
})

// 项目样式
const itemStyle = computed(() => ({
  background: mergedStyles.value.cardBackground || mergedStyles.value.backgroundColor || '#ffffff',
  padding: mergedStyles.value.itemPadding || mergedStyles.value.padding || '20px',
  borderRadius: mergedStyles.value.itemRadius || '8px',
  marginBottom: mergedStyles.value.itemMargin || '16px',
  border: mergedStyles.value.border || '1px solid #f0f0f0',
  boxShadow: mergedStyles.value.itemShadow || '0 1px 3px rgba(0,0,0,0.1)',
  position: 'relative'
}))

const positionStyle = computed(() => ({
  color: mergedStyles.value.positionColor || cssVariables.value['--text-color'],
  fontSize: mergedStyles.value.positionFontSize || cssVariables.value['--font-size-h1'],
  fontWeight: '600',
  marginBottom: '12px'
}))

const metaInfoStyle = computed(() => ({
  display: 'flex',
  flexWrap: 'wrap',
  gap: '16px'
}))

const metaItemStyle = computed(() => ({
  color: mergedStyles.value.metaColor || '#666',
  fontSize: mergedStyles.value.metaFontSize || cssVariables.value['--font-size-body']
}))

const tagStyle = computed(() => ({
  backgroundColor: mergedStyles.value.tagBackground || `rgba(${hexToRgb(cssVariables.value['--primary-color'])}, 0.1)`,
  color: mergedStyles.value.tagColor || cssVariables.value['--primary-color'],
  fontSize: mergedStyles.value.tagFontSize || '12px',
  padding: '4px 12px',
  borderRadius: '12px',
  border: `1px solid ${mergedStyles.value.tagBorder || `rgba(${hexToRgb(cssVariables.value['--primary-color'])}, 0.2)`}`
}))

const dividerLineStyle = computed(() => ({
  height: '1px',
  background: mergedStyles.value.dividerColor || '#f0f0f0',
  margin: '16px 0'
}))

const emptyStateStyle = computed(() => ({
  padding: '40px 20px',
  background: mergedStyles.value.emptyBackground || '#fafafa',
  borderRadius: '8px',
  border: `1px dashed ${mergedStyles.value.emptyBorder || '#e0e0e0'}`
}))

const buttonStyle = computed(() => ({
  background: mergedStyles.value.buttonBackground || '#f5f7fa',
  color: mergedStyles.value.buttonColor || cssVariables.value['--primary-color'],
  padding: '12px 16px',
  borderRadius: '6px',
  border: `1px solid ${mergedStyles.value.buttonBorder || '#e4e7ed'}`
}))

// 布局类型
const layout = computed(() => {
  return componentStyles.value.layout ||
    componentProps.value.layout ||
    defaultConfig.value.props?.layout ||
    'card'
})

// 显示选项 - 修复：正确处理配置
const showSalary = computed(() => {
  const propValue = componentProps.value.showSalary
  const configValue = defaultConfig.value.props?.showSalary
  return propValue !== undefined ? propValue : (configValue !== undefined ? configValue : true)
})

const showLocation = computed(() => {
  const propValue = componentProps.value.showLocation
  const configValue = defaultConfig.value.props?.showWorkLocation
  return propValue !== undefined ? propValue : (configValue !== undefined ? configValue : true)
})

const showJobType = computed(() => {
  const propValue = componentProps.value.showJobType
  const configValue = defaultConfig.value.props?.showJobType
  return propValue !== undefined ? propValue : (configValue !== undefined ? configValue : true)
})

const showJobTypeTag = computed(() => {
  const propValue = componentProps.value.showJobTypeTag
  return propValue !== undefined ? propValue : true
})

// 辅助函数：16进制颜色转RGB
const hexToRgb = (hex) => {
  if (!hex) return '44, 62, 80' // 默认颜色

  hex = hex.replace('#', '')

  if (hex.length === 3) {
    hex = hex.split('').map(char => char + char).join('')
  }

  const r = parseInt(hex.substring(0, 2), 16)
  const g = parseInt(hex.substring(2, 4), 16)
  const b = parseInt(hex.substring(4, 6), 16)

  return `${r}, ${g}, ${b}`
}

// 格式化薪资
const formatSalary = (salary) => {
  if (!salary || salary === '面议') return '面议'

  // 如果薪资是数字字符串，格式化为K为单位
  if (/^\d+$/.test(salary)) {
    const num = parseInt(salary)
    if (num >= 10000) {
      return `${(num / 10000).toFixed(1)}万/月`
    } else if (num >= 1000) {
      return `${(num / 1000).toFixed(1)}K/月`
    }
    return `${salary}元/月`
  }

  // 尝试处理带单位的数据
  if (typeof salary === 'string') {
    const unit = componentProps.value.salaryUnit || defaultConfig.value.props?.salaryUnit || 'K'
    return `${salary}${unit}/月`
  }

  return String(salary)
}

// 格式化工作类型
const formatJobType = (jobType) => {
  if (!jobType) return '全职'

  const typeMap = {
    '全': '全职',
    '兼': '兼职',
    '实': '实习',
    'remote': '远程',
    'freelance': '自由职业',
    'fulltime': '全职',
    'parttime': '兼职',
    'internship': '实习',
    'contract': '合同制',
    'temporary': '临时'
  }

  return typeMap[jobType] || jobType
}

// 切换显示全部
const toggleShowAll = () => {
  showAll.value = !showAll.value
}

// 组件加载日志
console.log('求职意向组件加载完成', {
  组件配置: componentConfig.value,
  组件属性: componentProps.value,
  组件样式: componentStyles.value,
  默认配置: defaultConfig.value,
  合并后样式: mergedStyles.value,
  求职意向数据: intentions.value,
  求职意向数量: intentions.value.length,
  显示选项: {
    showSalary: showSalary.value,
    showLocation: showLocation.value,
    showJobType: showJobType.value
  }
})
</script>

<style lang="scss" scoped>
.job-intention {
  margin-bottom: 40rpx;
  font-family: var(--font-family);
  color: var(--text-color);

  // 通用样式
  .section-header {
    margin-bottom: 20px;

    .section-title {
      font-size: var(--font-size-h1);
      font-weight: 600;
      color: var(--primary-color);
    }

    .section-divider {
      height: 2px;
      background: var(--accent-color);
      width: 60px;
      margin-top: 8px;
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 30rpx;
    text-align: center;
    background: #fafafa;
    border-radius: 16rpx;
    border: 1rpx dashed #e0e0e0;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
      opacity: 0.5;
    }

    .empty-text {
      color: #666;
      font-size: 30rpx;
      font-weight: 500;
      margin-bottom: 12rpx;
    }

    .empty-hint {
      color: #999;
      font-size: 24rpx;
    }
  }

  // 意向项
  .intention-item {
    .primary-info {
      .position {
        font-size: var(--font-size-h1);
        font-weight: 600;
        margin-bottom: 20rpx;
        display: block;
      }

      .meta-info {
        display: flex;
        flex-wrap: wrap;
        gap: 24rpx;
        margin-bottom: 16rpx;

        .meta-item {
          display: flex;
          align-items: center;
          color: #666;
          font-size: 26rpx;

          .icon {
            margin-right: 8rpx;
            font-size: 24rpx;
          }
        }
      }
    }

    .job-type-tag {
      position: absolute;
      top: 30rpx;
      right: 30rpx;
      padding: 4rpx 12rpx;
      border-radius: 20rpx;
      font-size: 22rpx;
      border: 1rpx solid;

      .tag-text {
        font-weight: 500;
      }
    }
  }

  // 分隔线
  .item-divider {
    height: 1rpx;
    background: #f0f0f0;
    margin: 20rpx 0;
  }

  // 显示更多按钮
  .show-more-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: var(--primary-color);
    font-size: 24rpx;
    padding: 16rpx;
    border-radius: 12rpx;
    border: 1rpx solid #e4e7ed;
    margin-top: 16rpx;
    cursor: pointer;

    .btn-text {
      margin-right: 8rpx;
    }

    .btn-icon {
      font-size: 20rpx;
    }

    &:active {
      background: #e8eaf1;
    }
  }
}

// 响应式调整
@media (max-width: 375px) {
  .job-intention {
    .intention-item {
      .meta-info {
        flex-direction: column;
        gap: 12rpx;
      }

      .job-type-tag {
        position: relative;
        top: 0;
        right: 0;
        margin-top: 16rpx;
        display: inline-block;
      }
    }
  }
}
</style>