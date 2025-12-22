<template>
  <view :class="['education-section', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">教育背景</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasEducationData" class="empty-state">
      <text class="empty-icon">🎓</text>
      <text class="empty-text">暂无教育背景信息</text>
    </view>

    <!-- 教育经历列表 -->
    <view v-else class="education-list">
      <block v-for="(edu, index) in experiences" :key="index">
        <view class="education-item" :style="itemStyle">
          <!-- 学校信息 -->
          <view class="school-header">
            <view class="school-main">
              <text class="school-name">{{ edu.school || '未知学校' }}</text>
              <view class="degree-info">
                <text class="degree">{{ edu.degree || '学历未填写' }}</text>
                <text v-if="edu.major" class="major"> · {{ edu.major }}</text>
              </view>
            </view>

            <!-- 时间信息 -->
            <view class="education-time">
              <text class="duration">
                {{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) || '至今' }}
              </text>
              <text v-if="edu.duration" class="duration-label">({{ edu.duration }})</text>
            </view>
          </view>

          <!-- GPA和排名（可选） -->
          <view v-if="showGpa && edu.gpa" class="academic-info">
            <text class="gpa">
              <text class="info-label">GPA: </text>{{ edu.gpa }}
            </text>
            <text v-if="showRanking && edu.ranking" class="ranking">
              <text class="info-label">排名: </text>{{ edu.ranking }}
            </text>
          </view>

          <!-- 所学课程（可选） -->
          <view v-if="showCourses && edu.courses && edu.courses.length > 0" class="courses-section">
            <text class="courses-title">相关课程：</text>
            <view class="course-tags">
              <text
                v-for="(course, courseIndex) in getDisplayCourses(edu.courses)"
                :key="courseIndex"
                class="course-tag"
              >
                {{ course }}
              </text>
              <text v-if="edu.courses.length > maxCourses" class="more-courses">
                等{{ edu.courses.length - maxCourses }}门课程
              </text>
            </view>
          </view>

          <!-- 在校成就 -->
          <view v-if="showAchievements && edu.achievements && edu.achievements.length > 0" class="achievements">
            <text class="achievements-title">在校成就：</text>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in edu.achievements"
                :key="aIndex"
                class="achievement-item"
              >
                <text class="achievement-icon">🏆</text>
                <text class="achievement-text">{{ achievement }}</text>
              </view>
            </view>
          </view>

          <!-- 详细描述 -->
          <view v-if="edu.description" class="education-description">
            <text class="description-text">{{ edu.description }}</text>
          </view>
        </view>

        <!-- 分隔线（最后一个项目不显示） -->
        <view v-if="index < experiences.length - 1" class="item-divider"></view>
      </block>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  config: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'modern'
  }
})

const maxCourses = ref(5) // 最多显示的课程数量

// 提取配置
const componentProps = computed(() => props.config.props || {})
const componentStyles = computed(() => props.config.styles || {})

const experiences = computed(() => componentProps.value.experiences || [])
const hasEducationData = computed(() => experiences.value.length > 0)

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff'
}))

// 是否显示GPA
const showGpa = computed(() => componentProps.value.showGPA !== false)
// 是否显示排名
const showRanking = computed(() => componentProps.value.showRanking !== false)
// 是否显示课程
const showCourses = computed(() => componentProps.value.showCourses !== false)
// 是否显示成就
const showAchievements = computed(() => componentProps.value.showAchievements !== false)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('-', '.')
}

// 获取显示的课程列表
const getDisplayCourses = (courses) => {
  if (!courses || !Array.isArray(courses)) return []
  if (courses.length <= maxCourses.value) return courses
  return courses.slice(0, maxCourses.value)
}

// 组件加载日志
console.log('教育背景组件加载完成', {
  教育经历数量: experiences.value.length,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.education-section {
  margin-bottom: 40rpx;

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

    .education-item {
      background: #ffffff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 24rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid #f0f0f0;
    }
  }

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
  .school-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;

    .school-main {
      flex: 1;

      .school-name {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        display: block;
        margin-bottom: 8rpx;
      }

      .degree-info {
        .degree {
          color: #d4af37;
          font-size: 26rpx;
          font-weight: 500;
        }

        .major {
          color: #666;
          font-size: 26rpx;
        }
      }
    }

    .education-time {
      text-align: right;
      min-width: 200rpx;

      .duration {
        color: #666;
        font-size: 24rpx;
        display: block;
        margin-bottom: 4rpx;
      }

      .duration-label {
        color: #999;
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
      background: #fef9ed;
      padding: 6rpx 12rpx;
      border-radius: 6rpx;
      font-size: 24rpx;

      .info-label {
        color: #d4af37;
        font-weight: 500;
      }
    }
  }

  // 课程相关
  .courses-section {
    margin-bottom: 20rpx;

    .courses-title {
      color: #666;
      font-size: 26rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 12rpx;
    }

    .course-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;

      .course-tag {
        background: #f5f7fa;
        color: #555;
        font-size: 22rpx;
        padding: 6rpx 12rpx;
        border-radius: 6rpx;
        border: 1rpx solid #e4e7ed;
      }

      .more-courses {
        color: #999;
        font-size: 22rpx;
        align-self: center;
      }
    }
  }

  // 成就列表
  .achievements {
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
          color: #555;
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
    border-top: 1rpx solid #f0f0f0;

    .description-text {
      color: #666;
      font-size: 26rpx;
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
    .school-header {
      flex-direction: column;

      .education-time {
        text-align: left;
        margin-top: 10rpx;
      }
    }
  }
}
</style>