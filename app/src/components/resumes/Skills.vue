<template>
  <view :class="['skills-section', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">技能专长</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasSkillsData" class="empty-state">
      <text class="empty-icon">💻</text>
      <text class="empty-text">暂无技能信息</text>
    </view>

    <!-- 按分类展示技能 -->
    <view v-else-if="groupByCategory" class="skills-by-category">
      <block v-for="(category, catIndex) in skillCategories" :key="catIndex">
        <view class="category-section">
          <text class="category-title">{{ category }}</text>
          <view class="category-skills">
            <block v-for="skill in getSkillsByCategory(category)" :key="skill.id">
              <view class="skill-item" :style="itemStyle">
                <!-- 技能名称和级别 -->
                <view class="skill-header">
                  <text class="skill-name">{{ skill.name }}</text>
                  <view class="skill-meta">
                    <text v-if="showSkillLevel" class="skill-level">{{ skill.level }}</text>
                    <text v-if="showExperienceYears && skill.experienceYears" class="experience-years">
                      · {{ skill.experienceYears }}年
                    </text>
                    <text v-if="skill.isCertified" class="certified-badge">✓ 认证</text>
                  </view>
                </view>

                <!-- 技能熟练度条 -->
                <view v-if="showSkillLevel && skillLevelType === 'progress'" class="skill-progress">
                  <view class="progress-background">
                    <view
                      class="progress-bar"
                      :style="{ width: `${skill.proficiencyPercent || 0}%` }"
                    ></view>
                  </view>
                  <text class="progress-text">{{ skill.proficiencyPercent || 0 }}%</text>
                </view>

                <!-- 标签和描述 -->
                <view v-if="showTags && skill.tags" class="skill-tags">
                  <text
                    v-for="tag in getSkillTags(skill.tags)"
                    :key="tag"
                    class="skill-tag"
                  >
                    {{ tag }}
                  </text>
                </view>

                <view v-if="skill.description" class="skill-description">
                  <text class="description-text">{{ skill.description }}</text>
                </view>

                <!-- 证书信息 -->
                <view v-if="skill.isCertified && skill.certificateName" class="certificate-info">
                  <text class="cert-icon">🏅</text>
                  <text class="cert-text">{{ skill.certificateName }}</text>
                  <text v-if="skill.certificateDate" class="cert-date">
                    ({{ formatDate(skill.certificateDate) }})
                  </text>
                </view>
              </view>
            </block>
          </view>
        </view>
      </block>
    </view>

    <!-- 不分分类展示技能 -->
    <view v-else class="skills-list">
      <block v-for="skill in displaySkills" :key="skill.id">
        <view class="skill-item" :style="itemStyle">
          <view class="skill-header">
            <text class="skill-name">{{ skill.name }}</text>
            <view class="skill-meta">
              <text v-if="skill.category" class="skill-category">{{ skill.category }}</text>
              <text v-if="showSkillLevel" class="skill-level">{{ skill.level }}</text>
              <text v-if="showExperienceYears && skill.experienceYears" class="experience-years">
                · {{ skill.experienceYears }}年
              </text>
            </view>
          </view>

          <view v-if="showSkillLevel && skillLevelType === 'progress'" class="skill-progress">
            <view class="progress-background">
              <view
                class="progress-bar"
                :style="{ width: `${skill.proficiencyPercent || 0}%` }"
              ></view>
            </view>
            <text class="progress-text">{{ skill.proficiencyPercent || 0 }}%</text>
          </view>

          <view v-if="skill.description" class="skill-description">
            <text class="description-text">{{ skill.description }}</text>
          </view>
        </view>
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

// 技能数据
const skills = computed(() => componentProps.value.skills || [])
const hasSkillsData = computed(() => skills.value.length > 0)

// 显示设置
const showSkillLevel = computed(() => componentProps.value.skillLevel !== false)
const skillLevelType = computed(() => componentProps.value.skillLevelType || 'progress')
const groupByCategory = computed(() => componentProps.value.groupByCategory !== false)
const showExperienceYears = computed(() => componentProps.value.showExperienceYears !== false)
const showTags = computed(() => componentProps.value.showTags !== false)
const maxSkillsPerCategory = computed(() => componentProps.value.maxSkillsPerCategory || 8)

// 技能分类
const skillCategories = computed(() => {
  if (componentProps.value.skillCategories && componentProps.value.skillCategories.length > 0) {
    return componentProps.value.skillCategories
  }

  // 自动从技能中提取分类
  const categories = [...new Set(skills.value.map(skill => skill.category || '其他').filter(Boolean))]
  return categories.length > 0 ? categories : ['技能']
})

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff'
}))

// 获取指定分类的技能
const getSkillsByCategory = (category) => {
  const categorySkills = skills.value.filter(skill => (skill.category || '其他') === category)
  return categorySkills.slice(0, maxSkillsPerCategory.value)
}

// 获取技能标签
const getSkillTags = (tags) => {
  if (!tags) return []
  return tags.split(',').map(tag => tag.trim()).filter(tag => tag)
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('-', '.')
}

// 不分分类时显示的技能
const displaySkills = computed(() => {
  return skills.value.slice(0, maxSkillsPerCategory.value * 2)
})

// 组件加载日志
console.log('技能专长组件加载完成', {
  技能数量: skills.value.length,
  分类数量: skillCategories.value.length,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.skills-section {
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

    .skill-item {
      background: #ffffff;
      border-radius: 12rpx;
      padding: 24rpx;
      margin-bottom: 20rpx;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid #f0f0f0;
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

  // 按分类展示
  .skills-by-category {
    .category-section {
      margin-bottom: 30rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .category-title {
        display: block;
        color: #333;
        font-size: 30rpx;
        font-weight: 600;
        margin-bottom: 20rpx;
        padding-bottom: 10rpx;
        border-bottom: 2rpx solid #f0f0f0;
      }
    }
  }

  // 技能项
  .skill-item {
    .skill-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16rpx;

      .skill-name {
        font-size: 28rpx;
        font-weight: 500;
        color: #333;
        flex: 1;
      }

      .skill-meta {
        text-align: right;
        min-width: 180rpx;

        .skill-level {
          color: #d4af37;
          font-size: 24rpx;
          font-weight: 500;
        }

        .skill-category {
          color: #666;
          font-size: 22rpx;
          background: #f5f7fa;
          padding: 4rpx 10rpx;
          border-radius: 4rpx;
          margin-right: 10rpx;
        }

        .experience-years {
          color: #999;
          font-size: 22rpx;
          margin-left: 8rpx;
        }

        .certified-badge {
          display: inline-block;
          background: #f0f9eb;
          color: #67c23a;
          font-size: 20rpx;
          padding: 2rpx 8rpx;
          border-radius: 12rpx;
          margin-left: 10rpx;
          border: 1rpx solid #c2e7b0;
        }
      }
    }

    .skill-progress {
      display: flex;
      align-items: center;
      gap: 20rpx;
      margin-bottom: 16rpx;

      .progress-background {
        flex: 1;
        height: 12rpx;
        background: #f5f7fa;
        border-radius: 6rpx;
        overflow: hidden;

        .progress-bar {
          height: 100%;
          background: linear-gradient(90deg, #d4af37, #f7ef8a);
          border-radius: 6rpx;
          transition: width 0.3s ease;
        }
      }

      .progress-text {
        color: #666;
        font-size: 22rpx;
        min-width: 60rpx;
      }
    }

    .skill-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10rpx;
      margin-bottom: 16rpx;

      .skill-tag {
        background: #f5f7fa;
        color: #666;
        font-size: 20rpx;
        padding: 4rpx 10rpx;
        border-radius: 12rpx;
        border: 1rpx solid #e4e7ed;
      }
    }

    .skill-description {
      margin-bottom: 16rpx;

      .description-text {
        color: #666;
        font-size: 24rpx;
        line-height: 1.5;
      }
    }

    .certificate-info {
      display: flex;
      align-items: center;
      padding-top: 16rpx;
      border-top: 1rpx dashed #f0f0f0;

      .cert-icon {
        font-size: 24rpx;
        margin-right: 10rpx;
        color: #faad14;
      }

      .cert-text {
        color: #555;
        font-size: 22rpx;
        flex: 1;
      }

      .cert-date {
        color: #999;
        font-size: 20rpx;
      }
    }
  }

  // 不分分类的列表
  .skills-list {
    .skill-item {
      .skill-header {
        flex-direction: column;
        align-items: flex-start;

        .skill-meta {
          text-align: left;
          margin-top: 8rpx;
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 375px) {
  .skills-section {
    .skill-item {
      .skill-header {
        flex-direction: column;
        align-items: flex-start;

        .skill-meta {
          text-align: left;
          margin-top: 8rpx;
        }
      }
    }
  }
}
</style>