<template>
  <view :class="['company-section', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">公司经历</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasCompanyData" class="empty-state">
      <text class="empty-icon">🏢</text>
      <text class="empty-text">暂无公司经历信息</text>
    </view>

    <!-- 公司经历列表 -->
    <view v-else class="company-list">
      <block v-for="(company, index) in companies" :key="index">
        <view class="company-item" :style="itemStyle">
          <!-- 公司信息 -->
          <view class="company-header">
            <view class="company-main">
              <text class="company-name">{{ company.name || '未知公司' }}</text>
              <view class="position-info">
                <text class="position">{{ company.position || '职位未填写' }}</text>
                <text v-if="company.department" class="department"> · {{ company.department }}</text>
              </view>
            </view>

            <!-- 时间信息 -->
            <view class="company-time">
              <text class="duration">
                {{ formatDate(company.startDate) }} - {{ formatDate(company.endDate) || '至今' }}
              </text>
              <text v-if="company.duration" class="duration-label">({{ company.duration }})</text>
            </view>
          </view>

          <!-- 工作描述 -->
          <view v-if="company.description" class="company-description">
            <text class="description-text">{{ company.description }}</text>
          </view>

          <!-- 工作成就 -->
          <view v-if="showAchievements && company.achievements && company.achievements.length > 0" class="achievements">
            <text class="achievements-title">工作成就：</text>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in company.achievements"
                :key="aIndex"
                class="achievement-item"
              >
                <text class="achievement-icon">🏆</text>
                <text class="achievement-text">{{ achievement }}</text>
              </view>
            </view>
          </view>

          <!-- 所用技能 -->
          <view v-if="showSkills && company.skills && company.skills.length > 0" class="skills-section">
            <text class="skills-title">使用技能：</text>
            <view class="skill-tags">
              <text
                v-for="(skill, skillIndex) in company.skills"
                :key="skillIndex"
                class="skill-tag"
              >
                {{ skill }}
              </text>
            </view>
          </view>
        </view>

        <!-- 分隔线 -->
        <view v-if="index < companies.length - 1" class="item-divider"></view>
      </block>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

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

// 提取配置
const componentProps = computed(() => props.config.props || {})
const componentStyles = computed(() => props.config.styles || {})

// 公司数据
const companies = computed(() => componentProps.value.experiences || [])
const hasCompanyData = computed(() => companies.value.length > 0)

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff'
}))

// 是否显示成就
const showAchievements = computed(() => componentStyles.value.showAchievements !== false)
// 是否显示技能
const showSkills = computed(() => componentStyles.value.showSkills !== false)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('-', '.')
}

// 组件加载日志
console.log('公司经历组件加载完成', {
  公司经历数量: companies.value.length,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.company-section {
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

    .company-item {
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

  // 公司头部信息
  .company-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;

    .company-main {
      flex: 1;

      .company-name {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        display: block;
        margin-bottom: 8rpx;
      }

      .position-info {
        .position {
          color: #d4af37;
          font-size: 26rpx;
          font-weight: 500;
        }

        .department {
          color: #666;
          font-size: 26rpx;
        }
      }
    }

    .company-time {
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

  // 公司描述
  .company-description {
    margin-bottom: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .description-text {
      color: #666;
      font-size: 26rpx;
      line-height: 1.6;
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

  // 技能标签
  .skills-section {
    .skills-title {
      color: #666;
      font-size: 26rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 12rpx;
    }

    .skill-tags {
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

  // 项目分隔线
  .item-divider {
    height: 1rpx;
    background: linear-gradient(90deg, transparent, #f0f0f0, transparent);
    margin: 30rpx 0;
  }
}

// 响应式调整
@media (max-width: 375px) {
  .company-section {
    .company-header {
      flex-direction: column;

      .company-time {
        text-align: left;
        margin-top: 10rpx;
      }
    }
  }
}
</style>