<template>
  <view
    :class="['education-section', `theme-${theme}`]"
    :style="[containerStyle, additionalStyle]"
  >
    <!-- 区块标题 -->
    <view class="section-header" :style="headerStyle">
      <text class="section-title" :style="titleStyle">{{ title }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasEducationData" class="empty-state" :style="emptyStateStyle">
      <text class="empty-icon">🎓</text>
      <text class="empty-text">暂无教育背景信息</text>
    </view>

    <!-- 教育经历列表 -->
    <view v-else class="education-list">
      <block v-for="(edu, index) in sortedExperiences" :key="edu.id || index">
        <view class="education-item" :style="getItemStyle(index)">
          <!-- 学校信息 -->
          <view class="university-header">
            <view class="university-main">
              <text class="university-name" :style="universityNameStyle">
                {{ edu.university || '未知学校' }}
              </text>
              <view class="degree-info">
                <text class="degree" :style="degreeStyle">{{ edu.degree || '学历未填写' }}</text>
                <text v-if="edu.major" class="major" :style="majorStyle"> · {{ edu.major }}</text>
              </view>
            </view>

            <!-- 时间信息 -->
            <view v-if="showEducationPeriod" class="education-time">
              <text class="duration" :style="durationStyle">
                {{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) || '至今' }}
              </text>
              <text v-if="edu.duration" class="duration-label" :style="durationLabelStyle">
                ({{ edu.duration }})
              </text>
            </view>
          </view>

          <!-- GPA和排名（可选） -->
          <view v-if="showGpa && edu.gpa" class="academic-info" :style="academicInfoStyle">
            <text class="gpa" :style="gpaStyle">
              <text class="info-label" :style="infoLabelStyle">GPA: </text>{{ edu.gpa }}
            </text>
            <text v-if="showRanking && edu.ranking" class="ranking" :style="rankingStyle">
              <text class="info-label" :style="infoLabelStyle">排名: </text>{{ edu.ranking }}
            </text>
          </view>

          <!-- 所学课程（可选） -->
          <view v-if="showCourses && edu.courses && edu.courses.length > 0"
                class="courses-section" :style="coursesSectionStyle">
            <text class="courses-title" :style="coursesTitleStyle">相关课程：</text>
            <view class="course-tags">
              <text
                v-for="(course, courseIndex) in getDisplayCourses(edu.courses)"
                :key="courseIndex"
                class="course-tag"
                :style="courseTagStyle"
              >
                {{ course }}
              </text>
              <text v-if="edu.courses.length > maxCourses" class="more-courses" :style="moreCoursesStyle">
                等{{ edu.courses.length - maxCourses }}门课程
              </text>
            </view>
          </view>

          <!-- 在校成就 -->
          <view v-if="showAchievements && edu.achievements && edu.achievements.length > 0"
                class="achievements" :style="achievementsStyle">
            <text class="achievements-title" :style="achievementsTitleStyle">在校成就：</text>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in edu.achievements"
                :key="aIndex"
                class="achievement-item"
                :style="achievementItemStyle"
              >
                <text class="achievement-icon" :style="achievementIconStyle">🏆</text>
                <text class="achievement-text" :style="achievementTextStyle">{{ achievement }}</text>
              </view>
            </view>
          </view>

          <!-- 详细描述 -->
          <view v-if="edu.description" class="education-description" :style="descriptionStyle">
            <text class="description-text" :style="descriptionTextStyle">{{ edu.description }}</text>
          </view>
        </view>

        <!-- 分隔线（最后一个项目不显示） -->
        <view v-if="index < sortedExperiences.length - 1" class="item-divider" :style="dividerStyle"></view>
      </block>
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
    default: 'classic'
  }
})

const maxCourses = ref(5) // 最多显示的课程数量

// 🚀 修复点1：统一从component中提取配置
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => props.component?.styles || {})
const defaultConfig = computed(() => props.component?.defaultConfig || {})

// 🚀 修复点2：优先使用component.styles，如果没有则使用defaultConfig.styles
const mergedStyles = computed(() => {
  return {
    ...(defaultConfig.value?.styles || {}),
    ...componentStyles.value
  }
})

// 🚀 修复点3：同样合并props
const mergedProps = computed(() => {
  return {
    ...(defaultConfig.value?.props || {}),
    ...componentProps.value
  }
})

// 教育经历数据适配
const experiences = computed(() => {
  return mergedProps.value.experiences || []
})

// 🚀 修复点4：排序教育经历（按毕业时间倒序）
const sortedExperiences = computed(() => {
  const exps = [...experiences.value]
  return exps.sort((a, b) => {
    const dateA = new Date(a.endDate || '1900-01-01')
    const dateB = new Date(b.endDate || '1900-01-01')
    return dateB - dateA // 最新的在前
  }).slice(0, mergedProps.value.maxItems || 10) // 限制显示数量
})

const hasEducationData = computed(() => sortedExperiences.value.length > 0)

// 标题
const title = computed(() => mergedProps.value.title || '教育背景')

// 控制显示哪些部分
const showGpa = computed(() => mergedProps.value.showGPA !== false)
const showRanking = computed(() => mergedProps.value.showRanking !== false)
const showCourses = computed(() => mergedProps.value.showCourses !== false)
const showAchievements = computed(() => mergedProps.value.showAchievements !== false)
const showEducationPeriod = computed(() => mergedProps.value.showEducationPeriod !== false)

// 🚀 修复点5：容器样式 - 使用合并后的样式
const containerStyle = computed(() => {
  const styles = mergedStyles.value

  return {
    padding: styles.padding || '20px',
    backgroundColor: styles.backgroundColor || '#ffffff',
    borderRadius: styles.borderRadius || '8px',
    border: styles.border || 'none',
    borderLeft: styles.borderLeft || `3px solid ${props.globalStyle?.primaryColor || '#52c41a'}`,
    boxShadow: styles.boxShadow || '0 2px 8px rgba(0,0,0,0.1)',
    marginBottom: '24px' // 使用全局间距
  }
})

// 🚀 修复点6：接受外部传入的样式（来自动态渲染器的内联样式）
const additionalStyle = computed(() => {
  // 动态渲染器会传入:style="getSectionStyle(component)"，这里确保接收
  return props.component?.inlineStyle || {}
})

// 🚀 修复点7：各个部分的样式计算
const headerStyle = computed(() => ({
  marginBottom: mergedStyles.value.itemSpacing || '20px'
}))

const titleStyle = computed(() => ({
  color: mergedStyles.value.titleColor || props.globalStyle?.primaryColor || '#333333',
  fontSize: mergedStyles.value.titleFontSize || props.globalStyle?.fontSizes?.h1 || '32px',
  fontWeight: 'bold'
}))

const dividerStyle = computed(() => ({
  backgroundColor: mergedStyles.value.dividerColor || props.globalStyle?.primaryColor || '#52c41a',
  height: '2px',
  width: mergedStyles.value.dividerWidth || '60px',
  marginTop: '8px'
}))

const emptyStateStyle = computed(() => ({
  backgroundColor: mergedStyles.value.emptyBackground || '#fafafa',
  borderColor: mergedStyles.value.emptyBorderColor || '#e0e0e0'
}))

// 单个教育项目的样式
const getItemStyle = (index) => {
  return {
    padding: mergedStyles.value.itemPadding || '20px',
    backgroundColor: mergedStyles.value.itemBackground || 'transparent',
    borderRadius: mergedStyles.value.itemBorderRadius || '4px',
    marginBottom: index < sortedExperiences.value.length - 1 ?
      (mergedStyles.value.itemSpacing || '16px') : '0'
  }
}

// 大学名称样式
const universityNameStyle = computed(() => ({
  color: mergedStyles.value.universityColor || '#333333',
  fontSize: mergedStyles.value.universityFontSize || '18px',
  fontWeight: 'bold'
}))

// 学位样式
const degreeStyle = computed(() => ({
  color: mergedStyles.value.degreeColor || mergedStyles.value.universityColor || props.globalStyle?.primaryColor || '#52c41a',
  fontSize: mergedStyles.value.degreeFontSize || '16px'
}))

const majorStyle = computed(() => ({
  color: mergedStyles.value.majorColor || '#666666',
  fontSize: mergedStyles.value.majorFontSize || '14px'
}))

const durationStyle = computed(() => ({
  color: mergedStyles.value.periodColor || '#999999',
  fontSize: mergedStyles.value.periodFontSize || '12px'
}))

const durationLabelStyle = computed(() => ({
  color: mergedStyles.value.durationLabelColor || '#cccccc',
  fontSize: mergedStyles.value.durationLabelFontSize || '12px'
}))

// GPA相关样式
const academicInfoStyle = computed(() => ({
  marginTop: mergedStyles.value.academicInfoMargin || '10px'
}))

const gpaStyle = computed(() => ({
  backgroundColor: mergedStyles.value.gpaBackground || '#f0f9eb',
  color: mergedStyles.value.gpaColor || '#52c41a',
  borderColor: mergedStyles.value.gpaBorderColor || '#b7eb8f'
}))

const rankingStyle = computed(() => ({
  backgroundColor: mergedStyles.value.rankingBackground || '#f6ffed',
  color: mergedStyles.value.rankingColor || '#73d13d',
  borderColor: mergedStyles.value.rankingBorderColor || '#95de64'
}))

const infoLabelStyle = computed(() => ({
  fontWeight: 'bold'
}))

// 课程相关样式
const coursesSectionStyle = computed(() => ({
  marginTop: mergedStyles.value.coursesMargin || '15px'
}))

const coursesTitleStyle = computed(() => ({
  color: mergedStyles.value.coursesTitleColor || '#666666',
  fontSize: mergedStyles.value.coursesTitleFontSize || '14px',
  fontWeight: '500'
}))

const courseTagStyle = computed(() => ({
  backgroundColor: mergedStyles.value.courseTagBackground || '#f5f7fa',
  color: mergedStyles.value.courseTagColor || '#555555',
  borderColor: mergedStyles.value.courseTagBorderColor || '#e4e7ed'
}))

const moreCoursesStyle = computed(() => ({
  color: mergedStyles.value.moreCoursesColor || '#999999'
}))

// 成就相关样式
const achievementsStyle = computed(() => ({
  marginTop: mergedStyles.value.achievementsMargin || '15px'
}))

const achievementsTitleStyle = computed(() => ({
  color: mergedStyles.value.achievementsTitleColor || '#666666',
  fontSize: mergedStyles.value.achievementsTitleFontSize || '14px',
  fontWeight: '500'
}))

const achievementItemStyle = computed(() => ({
  marginBottom: mergedStyles.value.achievementItemMargin || '8px'
}))

const achievementIconStyle = computed(() => ({
  color: mergedStyles.value.achievementIconColor || props.globalStyle?.primaryColor || '#52c41a'
}))

const achievementTextStyle = computed(() => ({
  color: mergedStyles.value.achievementTextColor || '#555555'
}))

// 描述样式
const descriptionStyle = computed(() => ({
  marginTop: mergedStyles.value.descriptionMargin || '15px',
  paddingTop: mergedStyles.value.descriptionPaddingTop || '15px',
  borderTopColor: mergedStyles.value.descriptionBorderColor || '#f0f0f0'
}))

const descriptionTextStyle = computed(() => ({
  color: mergedStyles.value.descriptionColor || '#666666',
  fontSize: mergedStyles.value.descriptionFontSize || '14px',
  lineHeight: mergedStyles.value.descriptionLineHeight || '1.6'
}))

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`
  } catch {
    return dateStr.replace('-', '.')
  }
}

// 获取显示的课程列表
const getDisplayCourses = (courses) => {
  if (!courses || !Array.isArray(courses)) return []
  if (courses.length <= maxCourses.value) return courses
  return courses.slice(0, maxCourses.value)
}

// 组件加载日志
console.log('教育背景组件加载完成', {
  配置: props.component,
  合并样式: mergedStyles.value,
  合并属性: mergedProps.value,
  教育经历数量: sortedExperiences.value.length
})
</script>

<style lang="scss" scoped>
.education-section {
  margin-bottom: 40rpx;
  transition: all 0.3s ease;

  // 空状态样式
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

  // 学校头部信息
  .university-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;

    .university-main {
      flex: 1;

      .university-name {
        display: block;
        margin-bottom: 8rpx;
      }

      .degree-info {
        .degree {
          font-weight: 500;
        }

        .major {
          font-size: 26rpx;
        }
      }
    }

    .education-time {
      text-align: right;
      min-width: 200rpx;

      .duration {
        display: block;
        margin-bottom: 4rpx;
      }

      .duration-label {
        font-size: 22rpx;
      }
    }
  }

  // 学术信息
  .academic-info {
    display: flex;
    gap: 20rpx;
    margin-bottom: 20rpx;

    .gpa, .ranking {
      padding: 6rpx 12rpx;
      border-radius: 6rpx;
      font-size: 24rpx;
      border: 1rpx solid;

      .info-label {
        font-weight: 500;
      }
    }
  }

  // 课程相关
  .courses-section {
    margin-bottom: 20rpx;

    .courses-title {
      display: block;
      margin-bottom: 12rpx;
    }

    .course-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;

      .course-tag {
        font-size: 22rpx;
        padding: 6rpx 12rpx;
        border-radius: 6rpx;
        border: 1rpx solid;
      }

      .more-courses {
        font-size: 22rpx;
        align-self: center;
      }
    }
  }

  // 成就列表
  .achievements {
    .achievements-title {
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

  // 描述文本
  .education-description {
    margin-top: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid;

    .description-text {
      line-height: 1.6;
    }
  }

  // 项目分隔线
  .item-divider {
    height: 1rpx;
    background: linear-gradient(90deg, transparent, #f0f0f0, transparent);
    margin: 30rpx 0;
  }
}

// 响应式调整
@media (max-width: 375px) {
  .education-section {
    .university-header {
      flex-direction: column;

      .education-time {
        text-align: left;
        margin-top: 10rpx;
        min-width: auto;
      }
    }
  }
}

// 打印样式
@media print {
  .education-section {
    break-inside: avoid;
    box-shadow: none !important;
    border: 1px solid #ddd !important;

    .education-item {
      box-shadow: none !important;
      border: none !important;
    }
  }
}
</style>