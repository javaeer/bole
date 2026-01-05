<template>
  <view
    :class="['work-experience', `layout-${layout}`, `theme-${theme}`]"
    :style="rootStyle"
  >
    <view class="section-header" :style="headerStyle">
      <text class="section-title" :style="titleStyle">{{ title }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasExperienceData" class="empty-state">
      <text class="empty-icon">💼</text>
      <text class="empty-text">暂无工作经历</text>
    </view>

    <!-- 工作经历列表 -->
    <view v-else class="experiences-list">
      <block v-for="(exp, index) in sortedExperiences" :key="exp.id || index">
        <view class="experience-item" :style="getItemStyle(exp, index)">
          <!-- 时间线布局 -->
          <view v-if="layout === 'timeline'" class="timeline-layout">
            <view class="timeline-dot" :style="dotStyle"></view>
            <view
              v-if="index < sortedExperiences.length - 1"
              class="timeline-line"
              :style="lineStyle"
            ></view>

            <view class="experience-content" :style="contentStyle">
              <view class="company-header">
                <view class="company-info">
                  <text class="company-name" :style="companyNameStyle">{{ exp.company }}</text>
                  <text class="duration" :style="durationStyle">
                    {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                    <text v-if="exp.duration"> ({{ exp.duration }})</text>
                  </text>
                </view>
                <text class="position" :style="positionStyle">{{ exp.position }}</text>
              </view>

              <view v-if="exp.department && showDepartment" class="department" :style="departmentStyle">
                <text class="department-label">部门：</text>
                <text class="department-value">{{ exp.department }}</text>
              </view>

              <view v-if="exp.description && showWorkContent" class="experience-desc">
                <text class="desc-text" :style="descStyle">{{ exp.description }}</text>
              </view>

              <!-- 工作成就 -->
              <view v-if="showAchievements && exp.achievements && exp.achievements.length > 0" class="achievements">
                <text class="achievements-title" :style="achievementsTitleStyle">主要成就：</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, aIndex) in exp.achievements"
                    :key="aIndex"
                    class="achievement-item"
                    :style="achievementItemStyle"
                  >
                    <text class="achievement-bullet" :style="bulletStyle">•</text>
                    <text class="achievement-text" :style="achievementTextStyle">{{ achievement }}</text>
                  </view>
                </view>
              </view>

              <!-- 所用技能 -->
              <view v-if="showSkills && exp.skills && exp.skills.length > 0" class="skills-tags">
                <text class="skills-title" :style="skillsTitleStyle">使用技能：</text>
                <view class="skills-container">
                  <text
                    v-for="(skill, sIndex) in exp.skills"
                    :key="sIndex"
                    class="skill-tag"
                    :style="tagStyle"
                  >
                    {{ skill }}
                  </text>
                </view>
              </view>
            </view>
          </view>

          <!-- 卡片布局 -->
          <view v-else class="card-layout">
            <view class="experience-card" :style="cardStyle">
              <view class="card-header">
                <text class="company-name" :style="companyNameStyle">{{ exp.company }}</text>
                <text class="position" :style="positionStyle">{{ exp.position }}</text>
              </view>

              <view class="card-meta" :style="metaStyle">
                <text class="duration" :style="durationStyle">
                  {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                </text>
                <text v-if="exp.department && showDepartment" class="department" :style="departmentTagStyle">
                  {{ exp.department }}
                </text>
              </view>

              <view v-if="exp.description && showWorkContent" class="card-desc">
                <text class="desc-text" :style="descStyle">{{ exp.description }}</text>
              </view>

              <!-- 卡片布局下的成就 -->
              <view v-if="showAchievements && exp.achievements && exp.achievements.length > 0" class="card-achievements" :style="achievementsStyle">
                <text class="achievements-title" :style="achievementsTitleStyle">主要成就：</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, aIndex) in exp.achievements"
                    :key="aIndex"
                    class="achievement-item"
                    :style="achievementItemStyle"
                  >
                    <text class="achievement-bullet" :style="bulletStyle">•</text>
                    <text class="achievement-text" :style="achievementTextStyle">{{ achievement }}</text>
                  </view>
                </view>
              </view>

              <!-- 卡片布局下的技能 -->
              <view v-if="showSkills && exp.skills && exp.skills.length > 0" class="card-skills" :style="skillsStyle">
                <text class="skills-title" :style="skillsTitleStyle">使用技能：</text>
                <view class="skills-container">
                  <text
                    v-for="(skill, sIndex) in exp.skills"
                    :key="sIndex"
                    class="skill-tag"
                    :style="tagStyle"
                  >
                    {{ skill }}
                  </text>
                </view>
              </view>
            </view>
          </view>
        </view>
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
  style: {
    type: Object,
    default: () => ({})
  }
})

// 🚀 提取配置 - 正确接收样式
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => {
  // 合并传入的样式：组件自定义样式 + 全局传入样式
  const compStyles = props.component?.styles || {}
  const globalStyles = props.globalStyle || {}
  const styleProp = props.style || {}

  return {
    // CSS变量继承
    ...globalStyles,
    // 组件自定义样式（如果有）
    ...compStyles,
    // 内联样式（来自动态渲染器）
    ...styleProp
  }
})
const defaultConfig = computed(() => props.component?.defaultConfig || {})

// 🚀 根元素样式
const rootStyle = computed(() => {
  const style = componentStyles.value

  // 基础容器样式
  const baseStyle = {
    marginBottom: style.marginBottom || style.spacing?.sectionMargin || '40rpx',
    padding: style.padding || '0rpx',
    background: style.backgroundColor || 'transparent'
  }

  // CSS变量
  const cssVariables = {
    '--primary-color': style.primaryColor || '#2c3e50',
    '--secondary-color': style.secondaryColor || '#2c3e50',
    '--accent-color': style.accentColor || '#2c3e50',
    '--text-color': style.textColor || '#333333',
    '--font-family': style.fontFamily || "'Times New Roman', serif",
    '--font-size-body': style.fontSizes?.body ? `${style.fontSizes.body}px` : '16px',
    '--font-size-h1': style.fontSizes?.h1 ? `${style.fontSizes.h1}px` : '36px',
    '--line-height': style.spacing?.lineHeight || '1.8'
  }

  return {
    ...cssVariables,
    ...baseStyle,
    // 允许覆盖
    ...style
  }
})

// 🚀 标题相关样式
const headerStyle = computed(() => ({
  marginBottom: componentStyles.value.itemSpacing || '30rpx'
}))

const titleStyle = computed(() => ({
  color: componentStyles.value.titleColor || 'var(--primary-color, #2c3e50)',
  fontSize: componentStyles.value.titleFontSize || 'var(--font-size-h1, 36rpx)',
  fontWeight: componentStyles.value.titleFontWeight || '600'
}))

const dividerStyle = computed(() => ({
  background: componentStyles.value.timelineColor || 'var(--primary-color, #2c3e50)',
  width: componentStyles.value.dividerWidth || '80rpx'
}))

// 工作经历数据
const experiences = computed(() => {
  const raw = componentProps.value.experiences || []
  return raw.map(exp => ({
    id: exp.id || Date.now(),
    company: exp.company || exp.companyName || '未指定公司',
    position: exp.position || exp.jobTitle || '职位未填写',
    description: exp.description || exp.workContent || '',
    startDate: exp.startDate || exp.startTime || '',
    endDate: exp.endDate || exp.endTime || '',
    isCurrent: exp.isCurrent || false,
    department: exp.department || exp.dept || '',
    skills: exp.skills || exp.technologies || [],
    achievements: exp.achievements || exp.accomplishments || [],
    duration: exp.duration || ''
  }))
})

const hasExperienceData = computed(() => experiences.value.length > 0)

// 标题
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '工作经历')

// 显示选项
const showDepartment = computed(() => componentProps.value.showDepartment ?? defaultConfig.value.props?.showDepartment ?? true)
const showWorkContent = computed(() => componentProps.value.showWorkContent ?? defaultConfig.value.props?.showWorkContent ?? true)
const showAchievements = computed(() => componentProps.value.showAchievements ?? defaultConfig.value.props?.showAchievements ?? true)
const showSkills = computed(() => componentProps.value.showSkills ?? defaultConfig.value.props?.showSkills ?? true)

// 布局
const layout = computed(() => {
  return componentStyles.value.layout ||
    componentProps.value.layout ||
    defaultConfig.value.props?.layout ||
    'card'
})

// 排序选项
const orderBy = computed(() => componentProps.value.orderBy || defaultConfig.value.props?.orderBy || 'startDate')
const orderDirection = computed(() => componentProps.value.orderDirection || defaultConfig.value.props?.orderDirection || 'desc')

// 排序后的工作经历
const sortedExperiences = computed(() => {
  const exps = [...experiences.value]

  if (!orderBy.value || exps.length === 0) return exps

  return exps.sort((a, b) => {
    let aValue = a[orderBy.value]
    let bValue = b[orderBy.value]

    // 处理日期比较
    if (orderBy.value.includes('Date') && aValue && bValue) {
      aValue = new Date(aValue).getTime()
      bValue = new Date(bValue).getTime()
    }

    // 处理空值情况
    if (aValue === undefined || aValue === null) aValue = 0
    if (bValue === undefined || bValue === null) bValue = 0

    if (orderDirection.value === 'asc') {
      return aValue < bValue ? -1 : aValue > bValue ? 1 : 0
    } else {
      return aValue > bValue ? -1 : aValue < bValue ? 1 : 0
    }
  })
})

// 🚀 项目项样式
const getItemStyle = (exp, index) => {
  const style = componentStyles.value

  return {
    marginBottom: style.itemSpacing || '24rpx',
    transition: 'all 0.3s ease'
  }
}

// 🚀 内容区域样式
const contentStyle = computed(() => {
  const style = componentStyles.value

  return {
    background: style.backgroundColor || '#ffffff',
    borderRadius: style.borderRadius || '16rpx',
    padding: style.padding || '30rpx',
    border: style.border || '1rpx solid #f0f0f0'
  }
})

// 🚀 卡片样式
const cardStyle = computed(() => {
  const style = componentStyles.value

  return {
    background: style.cardBackground || style.backgroundColor || '#ffffff',
    borderRadius: style.borderRadius || '16rpx',
    padding: style.padding || '30rpx',
    border: style.border || '1rpx solid #f0f0f0'
  }
})

// 🚀 文字样式
const companyNameStyle = computed(() => ({
  color: componentStyles.value.companyColor || '#333333',
  fontSize: componentStyles.value.companyFontSize || '32rpx',
  fontWeight: '600'
}))

const positionStyle = computed(() => ({
  color: componentStyles.value.positionColor || 'var(--primary-color, #2c3e50)',
  fontSize: componentStyles.value.positionFontSize || '28rpx',
  fontWeight: '500'
}))

const durationStyle = computed(() => ({
  color: componentStyles.value.periodColor || '#999999',
  fontSize: componentStyles.value.periodFontSize || '24rpx'
}))

const descStyle = computed(() => ({
  color: componentStyles.value.descColor || '#666666',
  fontSize: componentStyles.value.descFontSize || '26rpx',
  lineHeight: componentStyles.value.descLineHeight || '1.6'
}))

const departmentStyle = computed(() => ({
  color: componentStyles.value.departmentColor || '#666666',
  fontSize: componentStyles.value.departmentFontSize || '24rpx'
}))

const departmentTagStyle = computed(() => ({
  color: componentStyles.value.departmentColor || '#666666',
  fontSize: componentStyles.value.departmentFontSize || '24rpx',
  background: componentStyles.value.departmentBgColor || '#f5f7fa',
  padding: '4rpx 12rpx',
  borderRadius: '4rpx'
}))

const metaStyle = computed(() => ({
  borderBottom: '1rpx solid #f0f0f0',
  paddingBottom: '16rpx',
  marginBottom: '16rpx'
}))

// 🚀 时间线样式
const dotStyle = computed(() => ({
  backgroundColor: componentStyles.value.timelineDotColor || 'var(--primary-color, #2c3e50)',
  borderColor: componentStyles.value.timelineDotBorderColor || '#ffffff'
}))

const lineStyle = computed(() => ({
  backgroundColor: componentStyles.value.timelineColor || '#e8e8e8'
}))

// 🚀 成就和技能样式
const achievementsStyle = computed(() => ({
  borderTop: '1rpx solid #f0f0f0',
  paddingTop: '20rpx',
  marginTop: '20rpx'
}))

const skillsStyle = computed(() => ({
  borderTop: '1rpx solid #f0f0f0',
  paddingTop: '20rpx',
  marginTop: '20rpx'
}))

const achievementsTitleStyle = computed(() => ({
  color: componentStyles.value.achievementsTitleColor || '#666666',
  fontSize: componentStyles.value.achievementsTitleFontSize || '26rpx',
  fontWeight: '500'
}))

const skillsTitleStyle = computed(() => ({
  color: componentStyles.value.skillsTitleColor || '#666666',
  fontSize: componentStyles.value.skillsTitleFontSize || '26rpx',
  fontWeight: '500'
}))

const achievementItemStyle = computed(() => ({
  marginBottom: componentStyles.value.achievementSpacing || '8rpx'
}))

const achievementTextStyle = computed(() => ({
  color: componentStyles.value.achievementTextColor || '#555555',
  fontSize: componentStyles.value.achievementTextFontSize || '24rpx',
  lineHeight: componentStyles.value.achievementTextLineHeight || '1.5'
}))

const bulletStyle = computed(() => ({
  color: componentStyles.value.bulletColor || 'var(--primary-color, #2c3e50)'
}))

const tagStyle = computed(() => ({
  background: componentStyles.value.tagBackground || '#f5f7fa',
  color: componentStyles.value.tagColor || '#555555',
  fontSize: componentStyles.value.tagFontSize || '22rpx',
  border: componentStyles.value.tagBorder || '1rpx solid #e4e7ed',
  borderRadius: componentStyles.value.tagBorderRadius || '6rpx'
}))

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''

  const match = dateStr.match(/^(\d{4})-(\d{2})/)
  if (match) {
    return `${match[1]}.${match[2]}`
  }
  return dateStr
}

// 🚀 调试日志
console.log('工作经历组件加载完成', {
  工作经历数量: experiences.value.length,
  排序方式: orderBy.value,
  排序方向: orderDirection.value,
  布局: layout.value,
  组件样式: componentStyles.value,
  全局样式: props.globalStyle
})
</script>

<style lang="scss" scoped>
.work-experience {
  margin-bottom: 40rpx;
  font-family: var(--font-family, "'Times New Roman', serif");
  font-size: var(--font-size-body, 16px);
  line-height: var(--line-height, 1.8);
  color: var(--text-color, #333333);

  // 布局样式
  &.layout-card, &.layout-default {
    .experience-card {
      background: var(--card-background, #ffffff);
      border-radius: var(--border-radius, 16rpx);
      padding: var(--card-padding, 30rpx);
      margin-bottom: var(--item-spacing, 24rpx);
      box-shadow: var(--card-shadow, 0 2rpx 12rpx rgba(0, 0, 0, 0.05));
      border: var(--card-border, 1rpx solid #f0f0f0);

      .card-achievements,
      .card-skills {
        margin-top: 20rpx;
        padding-top: 20rpx;
        border-top: 1rpx solid var(--border-color, #f0f0f0);

        .achievements-title,
        .skills-title {
          color: var(--subtitle-color, #666);
          font-size: 26rpx;
          font-weight: 500;
          display: block;
          margin-bottom: 12rpx;
        }
      }
    }
  }

  &.layout-timeline {
    .timeline-layout {
      position: relative;
      padding-left: 40rpx;
      margin-bottom: 40rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .timeline-dot {
        position: absolute;
        left: 0;
        top: 10rpx;
        width: 16rpx;
        height: 16rpx;
        background-color: var(--primary-color, #2c3e50);
        border-radius: 50%;
        border: 3rpx solid white;
        box-shadow: 0 0 0 2rpx var(--primary-color, #2c3e50);
        z-index: 2;
      }

      .timeline-line {
        position: absolute;
        left: 7rpx;
        top: 26rpx;
        bottom: -30rpx;
        width: 2rpx;
        background-color: var(--timeline-color, #e8e8e8);
        z-index: 1;
      }

      .experience-content {
        background: var(--card-background, #ffffff);
        border-radius: var(--border-radius, 12rpx);
        padding: var(--card-padding, 24rpx);
        box-shadow: var(--card-shadow, 0 2rpx 8rpx rgba(0, 0, 0, 0.05));
        border: var(--card-border, 1rpx solid #f0f0f0);
      }
    }
  }

  // 主题样式
  &.theme-modern {
    .section-title {
      color: var(--primary-color, #2c3e50);
      font-size: var(--font-size-h1, 36rpx);
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, var(--primary-color, #2c3e50), var(--accent-color, #2c3e50));
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .company-name {
      color: var(--company-color, #333);
      font-size: 32rpx;
      font-weight: 600;
    }

    .position {
      color: var(--primary-color, #2c3e50);
      font-size: 28rpx;
      font-weight: 500;
    }
  }

  &.theme-classic {
    .section-title {
      color: var(--primary-color, #1890ff);
      font-size: var(--font-size-h1, 36rpx);
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, var(--primary-color, #1890ff), var(--secondary-color, #52c41a));
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .position {
      color: var(--primary-color, #1890ff);
      font-size: 28rpx;
      font-weight: 500;
    }
  }

  &.theme-light {
    .section-title {
      color: var(--primary-color, #1890ff);
    }
  }

  &.theme-dark {
    .section-title {
      color: var(--primary-color, #177ddc);
    }
    .experience-content,
    .experience-card {
      background: #1f1f1f;
      border-color: #333;
      color: #fff;

      .duration {
        color: #aaa;
      }
      .desc-text {
        color: #ccc;
      }
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 30rpx;
    background: #fafafa;
    border-radius: 16rpx;
    border: 1rpx dashed #e0e0e0;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
    }

    .empty-text {
      color: #999;
      font-size: 28rpx;
    }
  }

  // 时间线布局
  .timeline-layout {
    .company-header {
      margin-bottom: 16rpx;

      .company-info {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 8rpx;

        .company-name {
          font-size: 30rpx;
          font-weight: 600;
          color: var(--company-color, #333);
        }

        .duration {
          color: var(--period-color, #999);
          font-size: 24rpx;
          text-align: right;
          min-width: 200rpx;
        }
      }
    }

    .department {
      margin-bottom: 16rpx;
      padding: 8rpx 0;

      .department-label {
        color: var(--label-color, #666);
        font-size: 24rpx;
        font-weight: 500;
      }

      .department-value {
        color: var(--department-color, #666);
        font-size: 24rpx;
      }
    }

    .experience-desc {
      margin-bottom: 16rpx;

      .desc-text {
        color: var(--desc-color, #666);
        font-size: 26rpx;
        line-height: 1.6;
      }
    }

    .achievements {
      margin-bottom: 16rpx;

      .achievements-title {
        color: var(--subtitle-color, #666);
        font-size: 26rpx;
        font-weight: 500;
        display: block;
        margin-bottom: 12rpx;
      }

      .achievements-list {
        .achievement-item {
          display: flex;
          align-items: flex-start;
          margin-bottom: 8rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .achievement-bullet {
            color: var(--primary-color, #2c3e50);
            margin-right: 12rpx;
            flex-shrink: 0;
            font-weight: bold;
            margin-top: 4rpx;
          }

          .achievement-text {
            color: var(--text-color-secondary, #555);
            font-size: 24rpx;
            line-height: 1.5;
            flex: 1;
          }
        }
      }
    }

    .skills-tags {
      .skills-title {
        color: var(--subtitle-color, #666);
        font-size: 26rpx;
        font-weight: 500;
        display: block;
        margin-bottom: 12rpx;
      }

      .skills-container {
        display: flex;
        flex-wrap: wrap;
        gap: 12rpx;

        .skill-tag {
          background: var(--tag-background, #f5f7fa);
          color: var(--tag-color, #555);
          font-size: 22rpx;
          padding: 6rpx 12rpx;
          border-radius: 6rpx;
          border: 1rpx solid var(--tag-border-color, #e4e7ed);
        }
      }
    }
  }

  // 卡片布局
  .card-layout {
    .experience-card {
      .card-header {
        margin-bottom: 16rpx;

        .company-name {
          font-size: 32rpx;
          font-weight: 600;
          color: var(--company-color, #333);
          display: block;
          margin-bottom: 8rpx;
        }
      }

      .card-meta {
        display: flex;
        justify-content: space-between;
        margin-bottom: 16rpx;
        padding-bottom: 16rpx;
        border-bottom: 1rpx solid var(--border-color, #f0f0f0);

        .duration {
          color: var(--period-color, #666);
          font-size: 24rpx;
        }

        .department {
          color: var(--department-color, #666);
          font-size: 24rpx;
          background: var(--department-bg-color, #f5f7fa);
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
        }
      }

      .card-desc {
        margin-bottom: 16rpx;

        .desc-text {
          color: var(--desc-color, #666);
          font-size: 26rpx;
          line-height: 1.6;
        }
      }

      .card-achievements {
        .achievements-list {
          .achievement-item {
            display: flex;
            align-items: flex-start;
            margin-bottom: 8rpx;

            &:last-child {
              margin-bottom: 0;
            }

            .achievement-bullet {
              color: var(--primary-color, #2c3e50);
              margin-right: 12rpx;
              flex-shrink: 0;
              font-weight: bold;
              margin-top: 4rpx;
            }

            .achievement-text {
              color: var(--text-color-secondary, #555);
              font-size: 24rpx;
              line-height: 1.5;
              flex: 1;
            }
          }
        }
      }

      .card-skills {
        .skills-container {
          display: flex;
          flex-wrap: wrap;
          gap: 12rpx;

          .skill-tag {
            background: var(--tag-background, #f5f7fa);
            color: var(--tag-color, #555);
            font-size: 22rpx;
            padding: 6rpx 12rpx;
            border-radius: 6rpx;
            border: 1rpx solid var(--tag-border-color, #e4e7ed);
          }
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 375px) {
  .work-experience {
    .timeline-layout {
      .company-header {
        .company-info {
          flex-direction: column;

          .duration {
            text-align: left;
            margin-top: 8rpx;
          }
        }
      }
    }

    .card-layout {
      .experience-card {
        .card-meta {
          flex-direction: column;
          gap: 8rpx;
        }
      }
    }
  }
}
</style>