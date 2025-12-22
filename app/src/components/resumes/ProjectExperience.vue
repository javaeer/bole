<template>
  <view :class="['project-section', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">项目经历</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasProjectData" class="empty-state">
      <text class="empty-icon">📁</text>
      <text class="empty-text">暂无项目经历信息</text>
    </view>

    <!-- 项目经历列表 -->
    <view v-else class="project-list">
      <block v-for="(project, index) in projects" :key="index">
        <view class="project-item" :style="itemStyle">
          <!-- 项目信息 -->
          <view class="project-header">
            <view class="project-main">
              <text class="project-name">{{ project.name || '未知项目' }}</text>
              <view class="role-info">
                <text class="role">{{ project.role || '角色未填写' }}</text>
                <text v-if="project.company" class="company"> · {{ project.company }}</text>
              </view>
            </view>

            <!-- 时间信息 -->
            <view class="project-time">
              <text class="duration">
                {{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) || '至今' }}
              </text>
              <text v-if="project.duration" class="duration-label">({{ project.duration }})</text>
            </view>
          </view>

          <!-- 项目描述 -->
          <view v-if="project.description" class="project-description">
            <text class="description-text">{{ project.description }}</text>
          </view>

          <!-- 技术栈 -->
          <view v-if="showTechnologies && project.technologies && project.technologies.length > 0" class="technologies-section">
            <text class="technologies-title">技术栈：</text>
            <view class="technology-tags">
              <text
                v-for="(tech, techIndex) in project.technologies"
                :key="techIndex"
                class="technology-tag"
              >
                {{ tech }}
              </text>
            </view>
          </view>

          <!-- 项目职责 -->
          <view v-if="showResponsibilities && project.responsibilities && project.responsibilities.length > 0" class="responsibilities">
            <text class="responsibilities-title">我的职责：</text>
            <view class="responsibilities-list">
              <view
                v-for="(responsibility, rIndex) in project.responsibilities"
                :key="rIndex"
                class="responsibility-item"
              >
                <text class="responsibility-icon">✅</text>
                <text class="responsibility-text">{{ responsibility }}</text>
              </view>
            </view>
          </view>

          <!-- 项目成果 -->
          <view v-if="showAchievements && project.achievements && project.achievements.length > 0" class="achievements">
            <text class="achievements-title">项目成果：</text>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in project.achievements"
                :key="aIndex"
                class="achievement-item"
              >
                <text class="achievement-icon">🎯</text>
                <text class="achievement-text">{{ achievement }}</text>
              </view>
            </view>
          </view>

          <!-- 项目链接 -->
          <view v-if="project.link" class="project-link">
            <text class="link-icon">🔗</text>
            <text class="link-text" @click="openLink(project.link)">查看项目</text>
          </view>
        </view>

        <!-- 分隔线 -->
        <view v-if="index < projects.length - 1" class="item-divider"></view>
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
const componentProps = computed(() => props.component.props || {})
const componentStyles = computed(() => props.component.styles || {})

// 项目数据
// 项目数据适配
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
    link: project.link || ''
  }))
})
const hasProjectData = computed(() => projects.value.length > 0)

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff'
}))

// 是否显示技术栈
const showTechnologies = computed(() => componentStyles.value.showTechnologies !== false)
// 是否显示职责
const showResponsibilities = computed(() => componentStyles.value.showResponsibilities !== false)
// 是否显示成就
const showAchievements = computed(() => componentStyles.value.showAchievements !== false)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('-', '.')
}

// 打开链接
const openLink = (url) => {
  if (url) {
    uni.navigateTo({
      url: `/pages/webview/webview?url=${encodeURIComponent(url)}`
    })
  }
}

// 组件加载日志
console.log('项目经历组件加载完成', {
  项目经历数量: projects.value.length,
  配置: props.component
})
</script>

<style lang="scss" scoped>
.project-section {
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

    .project-item {
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
        color: #333;
        display: block;
        margin-bottom: 8rpx;
      }

      .role-info {
        .role {
          color: #d4af37;
          font-size: 26rpx;
          font-weight: 500;
        }

        .company {
          color: #666;
          font-size: 26rpx;
        }
      }
    }

    .project-time {
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

  // 项目描述
  .project-description {
    margin-bottom: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .description-text {
      color: #666;
      font-size: 26rpx;
      line-height: 1.6;
    }
  }

  // 技术栈标签
  .technologies-section {
    margin-bottom: 20rpx;

    .technologies-title {
      color: #666;
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
        background: #e8f4ff;
        color: #409eff;
        font-size: 22rpx;
        padding: 6rpx 12rpx;
        border-radius: 6rpx;
        border: 1rpx solid #b3d8ff;
      }
    }
  }

  // 职责列表
  .responsibilities {
    margin-bottom: 20rpx;

    .responsibilities-title {
      color: #666;
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
          color: #52c41a;
        }

        .responsibility-text {
          color: #555;
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
          color: #faad14;
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

  // 项目链接
  .project-link {
    display: flex;
    align-items: center;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .link-icon {
      margin-right: 8rpx;
      font-size: 24rpx;
      color: #1890ff;
    }

    .link-text {
      color: #1890ff;
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
    background: linear-gradient(90deg, transparent, #f0f0f0, transparent);
    margin: 30rpx 0;
  }
}

// 响应式调整
@media (max-width: 375px) {
  .project-section {
    .project-header {
      flex-direction: column;

      .project-time {
        text-align: left;
        margin-top: 10rpx;
      }
    }
  }
}
</style>