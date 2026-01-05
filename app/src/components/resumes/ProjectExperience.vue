<template>
  <view
    :class="['project-section', `theme-${theme}`]"
    :style="[containerStyle, customStyles]"
  >
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title" :style="titleStyle">{{ componentName }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasProjectData" class="empty-state" :style="emptyStateStyle">
      <text class="empty-icon">📁</text>
      <text class="empty-text">暂无项目经历信息</text>
    </view>

    <!-- 项目经历列表 -->
    <view v-else class="project-list">
      <block v-for="(project, index) in projects" :key="index">
        <view
          class="project-item"
          :style="[itemStyle(project), { marginBottom: itemSpacing }]"
        >
          <!-- 项目信息 -->
          <view class="project-header">
            <view class="project-main">
              <text
                class="project-name"
                :style="{ color: companyColor || titleColor || '#333' }"
              >
                {{ project.name || '未知项目' }}
              </text>
              <view class="role-info">
                <text class="role" :style="{ color: primaryColor || '#d4af37' }">
                  {{ project.role || '角色未填写' }}
                </text>
                <text v-if="project.company" class="company" :style="{ color: textColor || '#666' }">
                  · {{ project.company }}
                </text>
              </view>
            </view>

            <!-- 时间信息 -->
            <view class="project-time">
              <text class="duration" :style="{ color: periodColor || '#666' }">
                {{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) || '至今' }}
              </text>
              <text v-if="project.duration" class="duration-label" :style="{ color: textColor || '#999' }">
                ({{ project.duration }})
              </text>
            </view>
          </view>

          <!-- 项目描述 -->
          <view v-if="project.description" class="project-description">
            <text class="description-text" :style="{ color: textColor || '#666', fontSize: fontSize }">
              {{ project.description }}
            </text>
          </view>

          <!-- 技术栈 -->
          <view v-if="showTechnologies && project.technologies && project.technologies.length > 0"
                class="technologies-section">
            <text class="technologies-title" :style="{ color: textColor || '#666' }">技术栈：</text>
            <view class="technology-tags">
              <text
                v-for="(tech, techIndex) in project.technologies"
                :key="techIndex"
                class="technology-tag"
                :style="technologyTagStyle"
              >
                {{ tech }}
              </text>
            </view>
          </view>

          <!-- 项目职责 -->
          <view v-if="showResponsibilities && project.responsibilities && project.responsibilities.length > 0"
                class="responsibilities">
            <text class="responsibilities-title" :style="{ color: textColor || '#666' }">我的职责：</text>
            <view class="responsibilities-list">
              <view
                v-for="(responsibility, rIndex) in project.responsibilities"
                :key="rIndex"
                class="responsibility-item"
              >
                <text class="responsibility-icon" :style="{ color: primaryColor || '#52c41a' }">✅</text>
                <text class="responsibility-text" :style="{ color: textColor || '#555', fontSize: fontSize }">
                  {{ responsibility }}
                </text>
              </view>
            </view>
          </view>

          <!-- 项目成果 -->
          <view v-if="showAchievements && project.achievements && project.achievements.length > 0"
                class="achievements">
            <text class="achievements-title" :style="{ color: textColor || '#666' }">项目成果：</text>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in project.achievements"
                :key="aIndex"
                class="achievement-item"
              >
                <text class="achievement-icon" :style="{ color: accentColor || '#faad14' }">🎯</text>
                <text class="achievement-text" :style="{ color: textColor || '#555', fontSize: fontSize }">
                  {{ achievement }}
                </text>
              </view>
            </view>
          </view>

          <!-- 项目链接 -->
          <view v-if="project.link" class="project-link">
            <text class="link-icon" :style="{ color: highlightColor || '#1890ff' }">🔗</text>
            <text
              class="link-text"
              :style="{ color: highlightColor || '#1890ff' }"
              @click="openLink(project.link)"
            >
              查看项目
            </text>
          </view>
        </view>

        <!-- 分隔线 -->
        <view
          v-if="index < projects.length - 1"
          class="item-divider"
          :style="{ background: timelineColor || 'linear-gradient(90deg, transparent, #f0f0f0, transparent)' }"
        ></view>
      </block>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

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
  },
  customStyles: {
    type: Object,
    default: () => ({})
  }
})

// 🚀 从组件配置中提取样式
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => props.component?.styles || {})

// 🚀 组件名称
const componentName = computed(() => props.component?.name || '项目经历')

// 🚀 项目数据适配
const projects = computed(() => {
  const raw = componentProps.value.experiences || []
  return raw.map(project => ({
    id: project.id || Date.now(),
    name: project.name || project.projectName || '未指定项目',
    role: project.role || project.position || '角色未填写',
    description: project.description || '',
    startDate: project.startDate || '',
    endDate: project.endDate || '',
    company: project.company || '',
    technologies: project.technologies || project.skills || [],
    responsibilities: project.responsibilities || [],
    achievements: project.achievements || [],
    link: project.link || '',
    duration: project.duration || ''
  }))
})

const hasProjectData = computed(() => projects.value.length > 0)

// 🚀 样式计算 - 从组件配置和全局样式中提取
const primaryColor = computed(() =>
  componentStyles.value.primaryColor ||
  props.globalStyle?.primaryColor ||
  '#d4af37'
)

const accentColor = computed(() =>
  componentStyles.value.accentColor ||
  props.globalStyle?.accentColor ||
  '#faad14'
)

const secondaryColor = computed(() =>
  componentStyles.value.secondaryColor ||
  props.globalStyle?.secondaryColor ||
  '#52c41a'
)

const backgroundColor = computed(() =>
  componentStyles.value.backgroundColor ||
  props.globalStyle?.backgroundColor ||
  '#ffffff'
)

const textColor = computed(() =>
  componentStyles.value.textColor ||
  props.globalStyle?.textColor ||
  '#333333'
)

const titleColor = computed(() =>
  componentStyles.value.titleColor ||
  componentStyles.value.primaryColor ||
  primaryColor.value
)

const companyColor = computed(() =>
  componentStyles.value.companyColor ||
  componentStyles.value.highlightColor ||
  props.globalStyle?.primaryColor ||
  '#1890ff'
)

const periodColor = computed(() =>
  componentStyles.value.periodColor ||
  componentStyles.value.textColor ||
  textColor.value
)

const highlightColor = computed(() =>
  componentStyles.value.highlightColor ||
  props.globalStyle?.primaryColor ||
  '#1890ff'
)

const timelineColor = computed(() =>
  componentStyles.value.timelineColor ||
  '#e8e8e8'
)

const fontSize = computed(() => {
  const componentFontSize = componentStyles.value.fontSize
  const globalBodySize = props.globalStyle?.fontSizes?.body

  if (componentFontSize) {
    return typeof componentFontSize === 'number' ? `${componentFontSize}px` : componentFontSize
  }

  if (globalBodySize) {
    return `${globalBodySize}px`
  }

  return '14px'
})

const padding = computed(() =>
  componentStyles.value.padding ||
  props.globalStyle?.spacing?.padding ||
  '20px'
)

const itemSpacing = computed(() =>
  componentStyles.value.itemSpacing ||
  '16px'
)

// 🚀 是否显示技术栈
const showTechnologies = computed(() =>
  componentProps.value.showTechnologies !== false &&
  componentStyles.value.showTechnologies !== false
)

// 🚀 是否显示职责
const showResponsibilities = computed(() =>
  componentProps.value.showResponsibilities !== false &&
  componentStyles.value.showResponsibilities !== false
)

// 🚀 是否显示成就
const showAchievements = computed(() =>
  componentProps.value.showAchievements !== false &&
  componentStyles.value.showAchievements !== false
)

// 🚀 容器样式
const containerStyle = computed(() => {
  const style = {
    // CSS变量 - 用于子元素继承
    '--primary-color': primaryColor.value,
    '--accent-color': accentColor.value,
    '--secondary-color': secondaryColor.value,
    '--background-color': backgroundColor.value,
    '--text-color': textColor.value,
    '--font-family': props.globalStyle?.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    '--font-size-body': fontSize.value,
    '--padding': padding.value
  }

  // 基础样式
  const baseStyle = {
    backgroundColor: backgroundColor.value,
    padding: padding.value,
    borderRadius: componentStyles.value.borderRadius || '8px',
    fontSize: fontSize.value,
    fontFamily: props.globalStyle?.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif",
    color: textColor.value
  }

  return { ...style, ...baseStyle }
})

// 🚀 标题样式
const titleStyle = computed(() => ({
  color: titleColor.value,
  fontSize: props.globalStyle?.fontSizes?.h1 ? `${props.globalStyle.fontSizes.h1}px` : '24px',
  fontWeight: 'bold',
  marginBottom: '8px'
}))

// 🚀 分隔线样式
const dividerStyle = computed(() => ({
  height: componentStyles.value.dividerHeight || '2px',
  background: componentStyles.value.dividerColor || primaryColor.value,
  marginBottom: componentStyles.value.dividerMargin || '20px',
  width: componentStyles.value.dividerWidth || '60px'
}))

// 🚀 项目项样式
const itemStyle = computed(() => (project) => {
  const baseStyle = {
    backgroundColor: componentStyles.value.cardBackground || '#ffffff',
    borderRadius: componentStyles.value.cardBorderRadius || '8px',
    padding: componentStyles.value.cardPadding || '20px',
    boxShadow: componentStyles.value.cardShadow || '0 2px 8px rgba(0,0,0,0.1)',
    border: componentStyles.value.cardBorder || '1px solid #f0f0f0'
  }

  // 可以针对不同的项目添加特殊样式
  const projectSpecificStyle = {}

  return { ...baseStyle, ...projectSpecificStyle }
})

// 🚀 技术栈标签样式
const technologyTagStyle = computed(() => ({
  backgroundColor: componentStyles.value.tagBackground || '#e8f4ff',
  color: componentStyles.value.tagColor || '#409eff',
  border: componentStyles.value.tagBorder || '1px solid #b3d8ff',
  borderRadius: componentStyles.value.tagBorderRadius || '4px',
  padding: componentStyles.value.tagPadding || '4px 8px',
  fontSize: componentStyles.value.tagFontSize || '12px'
}))

// 🚀 空状态样式
const emptyStateStyle = computed(() => ({
  backgroundColor: componentStyles.value.emptyBackground || '#fafafa',
  border: componentStyles.value.emptyBorder || '1px dashed #e0e0e0',
  borderRadius: componentStyles.value.emptyBorderRadius || '8px',
  padding: componentStyles.value.emptyPadding || '40px 20px'
}))

// 🚀 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace(/-/g, '.')
}

// 🚀 打开链接
const openLink = (url) => {
  if (url) {
    uni.navigateTo({
      url: `/pages/webview/webview?url=${encodeURIComponent(url)}`
    })
  }
}

// 🚀 组件加载日志
console.log('项目经历组件加载完成', {
  组件名称: componentName.value,
  项目经历数量: projects.value.length,
  组件样式: componentStyles.value,
  全局样式: props.globalStyle,
  主题: props.theme,
  自定义样式: props.customStyles
})
</script>

<style lang="scss" scoped>
.project-section {
  margin-bottom: 40rpx;
  transition: all 0.3s ease;

  // 使用CSS变量
  background-color: var(--background-color);
  font-family: var(--font-family);
  color: var(--text-color);
  font-size: var(--font-size-body);
  padding: var(--padding);

  // 主题样式
  &.theme-light {
    --primary-color: #d4af37;
    --accent-color: #f7ef8a;
    --background-color: #ffffff;
    --text-color: #333333;
  }

  &.theme-dark {
    --primary-color: #177ddc;
    --accent-color: #49aa19;
    --background-color: #141414;
    --text-color: #ffffff;
  }

  &.theme-classic {
    --primary-color: #2c3e50;
    --accent-color: #2c3e50;
    --background-color: #f8f9fa;
    --text-color: #333333;
  }

  // 空状态样式
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 30rpx;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
      opacity: 0.5;
    }

    .empty-text {
      color: #999;
      font-size: 28rpx;
    }
  }

  // 项目头部信息
  .project-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;

    .project-main {
      flex: 1;

      .project-name {
        font-size: 32rpx;
        font-weight: 600;
        display: block;
        margin-bottom: 8rpx;
      }

      .role-info {
        .role {
          font-size: 26rpx;
          font-weight: 500;
        }

        .company {
          font-size: 26rpx;
        }
      }
    }

    .project-time {
      text-align: right;
      min-width: 200rpx;

      .duration {
        font-size: 24rpx;
        display: block;
        margin-bottom: 4rpx;
      }

      .duration-label {
        font-size: 22rpx;
      }
    }
  }

  // 项目描述
  .project-description {
    margin-bottom: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .description-text {
      font-size: 26rpx;
      line-height: 1.6;
    }
  }

  // 技术栈标签
  .technologies-section {
    margin-bottom: 20rpx;

    .technologies-title {
      font-size: 26rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 12rpx;
    }

    .technology-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;

      .technology-tag {
        font-size: 22rpx;
      }
    }
  }

  // 职责列表
  .responsibilities {
    margin-bottom: 20rpx;

    .responsibilities-title {
      font-size: 26rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 12rpx;
    }

    .responsibilities-list {
      .responsibility-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 12rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .responsibility-icon {
          margin-right: 12rpx;
          font-size: 24rpx;
          flex-shrink: 0;
          margin-top: 4rpx;
        }

        .responsibility-text {
          font-size: 24rpx;
          line-height: 1.4;
          flex: 1;
        }
      }
    }
  }

  // 成就列表
  .achievements {
    margin-bottom: 20rpx;

    .achievements-title {
      font-size: 26rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 12rpx;
    }

    .achievements-list {
      .achievement-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 12rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .achievement-icon {
          margin-right: 12rpx;
          font-size: 24rpx;
          flex-shrink: 0;
          margin-top: 4rpx;
        }

        .achievement-text {
          font-size: 24rpx;
          line-height: 1.4;
          flex: 1;
        }
      }
    }
  }

  // 项目链接
  .project-link {
    display: flex;
    align-items: center;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .link-icon {
      margin-right: 8rpx;
      font-size: 24rpx;
    }

    .link-text {
      font-size: 24rpx;
      text-decoration: underline;

      &:active {
        opacity: 0.7;
      }
    }
  }

  // 项目分隔线
  .item-divider {
    height: 1rpx;
    margin: 30rpx 0;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .project-section {
    .project-header {
      flex-direction: column;

      .project-time {
        text-align: left;
        margin-top: 10rpx;
        min-width: auto;
      }
    }
  }
}
</style>