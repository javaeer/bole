<template>
  <view :class="['work-experience', `layout-${layout}`, `theme-${theme}`]" :style="computedStyle">
    <view class="section-header">
      <text class="section-title">{{ title }}</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasExperienceData" class="empty-state">
      <text class="empty-icon">💼</text>
      <text class="empty-text">暂无工作经历</text>
    </view>

    <!-- 工作经历列表 -->
    <view v-else class="experiences-list">
      <block v-for="(exp, index) in sortedExperiences" :key="exp.id || index">
        <view class="experience-item" :style="itemStyle">
          <!-- 时间线布局 -->
          <view v-if="layout === 'timeline'" class="timeline-layout">
            <view class="timeline-dot"></view>
            <view v-if="index < sortedExperiences.length - 1" class="timeline-line"></view>

            <view class="experience-content">
              <view class="company-header">
                <view class="company-info">
                  <text class="company-name">{{ exp.company }}</text>
                  <text class="duration">
                    {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                    <text v-if="exp.duration"> ({{ exp.duration }})</text>
                  </text>
                </view>
                <text class="position">{{ exp.position }}</text>
              </view>

              <view v-if="exp.department && showDepartment" class="department">
                <text class="department-label">部门：</text>
                <text class="department-value">{{ exp.department }}</text>
              </view>

              <view v-if="exp.description && showWorkContent" class="experience-desc">
                <text class="desc-text">{{ exp.description }}</text>
              </view>

              <!-- 工作成就 -->
              <view v-if="showAchievements && exp.achievements && exp.achievements.length > 0" class="achievements">
                <text class="achievements-title">主要成就：</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, aIndex) in exp.achievements"
                    :key="aIndex"
                    class="achievement-item"
                  >
                    <text class="achievement-bullet">•</text>
                    <text class="achievement-text">{{ achievement }}</text>
                  </view>
                </view>
              </view>

              <!-- 所用技能 -->
              <view v-if="showSkills && exp.skills && exp.skills.length > 0" class="skills-tags">
                <text class="skills-title">使用技能：</text>
                <view class="skills-container">
                  <text
                    v-for="(skill, sIndex) in exp.skills"
                    :key="sIndex"
                    class="skill-tag"
                  >
                    {{ skill }}
                  </text>
                </view>
              </view>
            </view>
          </view>

          <!-- 卡片布局 -->
          <view v-else class="card-layout">
            <view class="experience-card">
              <view class="card-header">
                <text class="company-name">{{ exp.company }}</text>
                <text class="position">{{ exp.position }}</text>
              </view>

              <view class="card-meta">
                <text class="duration">
                  {{ formatDate(exp.startDate) }} - {{ exp.endDate ? formatDate(exp.endDate) : '至今' }}
                </text>
                <text v-if="exp.department && showDepartment" class="department">
                  {{ exp.department }}
                </text>
              </view>

              <view v-if="exp.description && showWorkContent" class="card-desc">
                <text class="desc-text">{{ exp.description }}</text>
              </view>

              <!-- 卡片布局下的成就 -->
              <view v-if="showAchievements && exp.achievements && exp.achievements.length > 0" class="card-achievements">
                <text class="achievements-title">主要成就：</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, aIndex) in exp.achievements"
                    :key="aIndex"
                    class="achievement-item"
                  >
                    <text class="achievement-bullet">•</text>
                    <text class="achievement-text">{{ achievement }}</text>
                  </view>
                </view>
              </view>

              <!-- 卡片布局下的技能 -->
              <view v-if="showSkills && exp.skills && exp.skills.length > 0" class="card-skills">
                <text class="skills-title">使用技能：</text>
                <view class="skills-container">
                  <text
                    v-for="(skill, sIndex) in exp.skills"
                    :key="sIndex"
                    class="skill-tag"
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
  theme: {
    type: String,
    default: 'modern'
  }
})

// 提取配置
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => props.component?.styles || {})
const defaultConfig = computed(() => props.component?.defaultConfig || {})

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

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || componentStyles.value.backgroundColor || '#ffffff'
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

// 组件加载日志
console.log('工作经历组件加载完成', {
  工作经历数量: experiences.value.length,
  排序方式: orderBy.value,
  排序方向: orderDirection.value,
  布局: layout.value
})
</script>

<style lang="scss" scoped>
.work-experience {
  margin-bottom: 40rpx;

  // 布局样式
  &.layout-card, &.layout-default {
    .experience-card {
      background: #ffffff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 24rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid #f0f0f0;

      .card-achievements,
      .card-skills {
        margin-top: 20rpx;
        padding-top: 20rpx;
        border-top: 1rpx solid #f0f0f0;

        .achievements-title,
        .skills-title {
          color: #666;
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
        background-color: #d4af37;
        border-radius: 50%;
        border: 3rpx solid white;
        box-shadow: 0 0 0 2rpx #d4af37;
        z-index: 2;
      }

      .timeline-line {
        position: absolute;
        left: 7rpx;
        top: 26rpx;
        bottom: -30rpx;
        width: 2rpx;
        background-color: #e8e8e8;
        z-index: 1;
      }

      .experience-content {
        background: #ffffff;
        border-radius: 12rpx;
        padding: 24rpx;
        box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
        border: 1rpx solid #f0f0f0;
      }
    }
  }

  // 主题样式
  &.theme-modern {
    .section-title {
      color: #d4af37;
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, #d4af37, #f7ef8a);
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .company-name {
      color: #333;
      font-size: 32rpx;
      font-weight: 600;
    }

    .position {
      color: #d4af37;
      font-size: 28rpx;
      font-weight: 500;
    }
  }

  &.theme-classic {
    .section-title {
      color: #1890ff;
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, #1890ff, #52c41a);
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .position {
      color: #1890ff;
      font-size: 28rpx;
      font-weight: 500;
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
          color: #333;
        }

        .duration {
          color: #999;
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
        color: #666;
        font-size: 24rpx;
        font-weight: 500;
      }

      .department-value {
        color: #666;
        font-size: 24rpx;
      }
    }

    .experience-desc {
      margin-bottom: 16rpx;

      .desc-text {
        color: #666;
        font-size: 26rpx;
        line-height: 1.6;
      }
    }

    .achievements {
      margin-bottom: 16rpx;

      .achievements-title {
        color: #666;
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
            color: #d4af37;
            margin-right: 12rpx;
            flex-shrink: 0;
            font-weight: bold;
            margin-top: 4rpx;
          }

          .achievement-text {
            color: #555;
            font-size: 24rpx;
            line-height: 1.5;
            flex: 1;
          }
        }
      }
    }

    .skills-tags {
      .skills-title {
        color: #666;
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
          background: #f5f7fa;
          color: #555;
          font-size: 22rpx;
          padding: 6rpx 12rpx;
          border-radius: 6rpx;
          border: 1rpx solid #e4e7ed;
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
          color: #333;
          display: block;
          margin-bottom: 8rpx;
        }
      }

      .card-meta {
        display: flex;
        justify-content: space-between;
        margin-bottom: 16rpx;
        padding-bottom: 16rpx;
        border-bottom: 1rpx solid #f0f0f0;

        .duration {
          color: #666;
          font-size: 24rpx;
        }

        .department {
          color: #666;
          font-size: 24rpx;
          background: #f5f7fa;
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
        }
      }

      .card-desc {
        margin-bottom: 16rpx;

        .desc-text {
          color: #666;
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
              color: #d4af37;
              margin-right: 12rpx;
              flex-shrink: 0;
              font-weight: bold;
              margin-top: 4rpx;
            }

            .achievement-text {
              color: #555;
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
            background: #f5f7fa;
            color: #555;
            font-size: 22rpx;
            padding: 6rpx 12rpx;
            border-radius: 6rpx;
            border: 1rpx solid #e4e7ed;
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