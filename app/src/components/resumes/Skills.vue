<template>
  <view
    :class="['skills-section', `theme-${theme}`, `layout-${layout}`]"
    :style="[containerStyle, customStyles]"
  >
    <!-- 区块标题 -->
    <view class="section-header" :style="headerStyle">
      <text class="section-title" :style="titleStyle">技能专长</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasSkillsData" class="empty-state" :style="emptyStateStyle">
      <text class="empty-icon">💻</text>
      <text class="empty-text">暂无技能信息</text>
    </view>

    <!-- 按分类展示技能 -->
    <view v-else-if="groupByCategory" class="skills-by-category">
      <block v-for="(category, catIndex) in skillCategories" :key="catIndex">
        <view class="category-section">
          <text class="category-title" :style="categoryTitleStyle">{{ category }}</text>
          <view class="category-skills">
            <block v-for="skill in getSkillsByCategory(category)" :key="skill.id">
              <view class="skill-item" :style="getSkillItemStyle(skill)">
                <!-- 技能名称和级别 -->
                <view class="skill-header">
                  <text class="skill-name" :style="skillNameStyle">{{ skill.name }}</text>
                  <view class="skill-meta">
                    <text v-if="showSkillLevel" class="skill-level" :style="skillLevelStyle(skill)">
                      {{ skill.level }}
                    </text>
                    <text v-if="showExperienceYears && skill.experienceYears" class="experience-years">
                      · {{ skill.experienceYears }}年
                    </text>
                    <text v-if="skill.isCertified" class="certified-badge" :style="certifiedBadgeStyle">
                      ✓ 认证
                    </text>
                  </view>
                </view>

                <!-- 技能熟练度条 -->
                <view v-if="showSkillLevel && skillLevelType === 'progress'" class="skill-progress">
                  <view class="progress-background">
                    <view
                      class="progress-bar"
                      :style="getProgressBarStyle(skill)"
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
                    :style="skillTagStyle"
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
        <view class="skill-item" :style="getSkillItemStyle(skill)">
          <view class="skill-header">
            <text class="skill-name" :style="skillNameStyle">{{ skill.name }}</text>
            <view class="skill-meta">
              <text v-if="skill.category" class="skill-category">{{ skill.category }}</text>
              <text v-if="showSkillLevel" class="skill-level" :style="skillLevelStyle(skill)">
                {{ skill.level }}
              </text>
              <text v-if="showExperienceYears && skill.experienceYears" class="experience-years">
                · {{ skill.experienceYears }}年
              </text>
            </view>
          </view>

          <view v-if="showSkillLevel && skillLevelType === 'progress'" class="skill-progress">
            <view class="progress-background">
              <view
                class="progress-bar"
                :style="getProgressBarStyle(skill)"
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
  component: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'modern'
  },
  globalStyle: {
    type: Object,
    default: () => ({})
  },
  style: {
    type: Object,
    default: () => ({})
  }
})

// 提取配置
const componentProps = computed(() => props.component?.props || {})
const componentStyles = computed(() => props.component?.styles || {})
const defaultStyles = computed(() => props.component?.defaultConfig?.styles || {})

// 布局类型
const layout = computed(() => {
  // 可以从全局样式或组件样式获取布局
  return componentProps.value.layout || 'single'
})

// 🚀 修复：合并样式配置
const mergedStyles = computed(() => {
  // 合并优先级：内联样式 > 组件自定义样式 > 默认样式 > 全局样式
  return {
    ...defaultStyles.value,    // 默认样式
    ...componentStyles.value,  // 组件自定义样式
    ...props.style,           // 内联样式（从动态渲染器传递）
    // 全局样式作为CSS变量传递
  }
})

// 容器样式
const containerStyle = computed(() => {
  const style = mergedStyles.value

  return {
    // CSS变量，用于继承全局样式
    '--primary-color': props.globalStyle?.primaryColor || style.primaryColor || '#2c3e50',
    '--secondary-color': props.globalStyle?.secondaryColor || style.secondaryColor || '#2c3e50',
    '--accent-color': props.globalStyle?.accentColor || style.accentColor || '#2c3e50',
    '--background-color': props.globalStyle?.backgroundColor || style.backgroundColor || '#f8f9fa',
    '--text-color': props.globalStyle?.textColor || style.textColor || '#333333',
    '--font-family': props.globalStyle?.fontFamily || style.fontFamily || "'Times New Roman', serif",

    // 内联样式
    backgroundColor: style.backgroundColor || props.globalStyle?.backgroundColor || '#f8f9fa',
    color: style.textColor || props.globalStyle?.textColor || '#333333',
    fontFamily: style.fontFamily || props.globalStyle?.fontFamily || "'Times New Roman', serif",
    fontSize: style.fontSize || props.globalStyle?.fontSizes?.body ? `${props.globalStyle.fontSizes.body}px` : '16px',
    padding: style.padding || props.globalStyle?.spacing?.padding || '20px',
    marginBottom: style.marginBottom || props.globalStyle?.spacing?.sectionMargin || '24px',
    borderRadius: style.borderRadius || '8px',
    boxShadow: style.boxShadow || '0 2px 8px rgba(0,0,0,0.1)'
  }
})

// 自定义样式（从合并样式提取）
const customStyles = computed(() => {
  const style = mergedStyles.value
  // 排除已处理的样式和CSS变量
  const excludeProps = [
    'primaryColor', 'secondaryColor', 'accentColor', 'backgroundColor',
    'textColor', 'fontFamily', 'fontSize', 'padding', 'marginBottom',
    'borderRadius', 'boxShadow'
  ]

  const custom = {}
  Object.keys(style).forEach(key => {
    if (!excludeProps.includes(key)) {
      custom[key] = style[key]
    }
  })

  return custom
})

// 标题样式
const headerStyle = computed(() => ({
  marginBottom: '30rpx'
}))

const titleStyle = computed(() => {
  const style = mergedStyles.value
  return {
    color: style.titleColor || 'var(--primary-color)',
    fontSize: style.titleFontSize || props.globalStyle?.fontSizes?.h1 ? `${props.globalStyle.fontSizes.h1}px` : '36px',
    fontWeight: style.titleFontWeight || '600',
    display: 'block'
  }
})

const dividerStyle = computed(() => {
  const style = mergedStyles.value
  return {
    height: '2px',
    background: style.dividerColor || `linear-gradient(90deg, var(--primary-color), var(--accent-color))`,
    width: style.dividerWidth || '80px',
    marginTop: '10px'
  }
})

// 空状态样式
const emptyStateStyle = computed(() => ({
  backgroundColor: mergedStyles.value.emptyBackgroundColor || '#fafafa',
  borderRadius: mergedStyles.value.emptyBorderRadius || '16px'
}))

// 分类标题样式
const categoryTitleStyle = computed(() => ({
  color: mergedStyles.value.categoryTitleColor || '#333',
  fontSize: mergedStyles.value.categoryTitleFontSize || '30px',
  fontWeight: mergedStyles.value.categoryTitleFontWeight || '600'
}))

// 技能项样式
const getSkillItemStyle = (skill) => {
  const style = mergedStyles.value
  return {
    backgroundColor: style.cardBackground || style.backgroundColor || '#ffffff',
    borderRadius: style.itemBorderRadius || '12px',
    padding: style.itemPadding || '24px',
    marginBottom: style.itemMarginBottom || '20px',
    boxShadow: style.itemBoxShadow || '0 2px 8px rgba(0, 0, 0, 0.05)',
    border: style.itemBorder || '1px solid #f0f0f0'
  }
}

// 技能名称样式
const skillNameStyle = computed(() => ({
  color: mergedStyles.value.skillNameColor || '#333',
  fontSize: mergedStyles.value.skillNameFontSize || '28px',
  fontWeight: mergedStyles.value.skillNameFontWeight || '500'
}))

// 技能级别样式
const skillLevelStyle = (skill) => ({
  color: mergedStyles.value.skillLevelColor || 'var(--primary-color)',
  fontSize: mergedStyles.value.skillLevelFontSize || '24px'
})

// 认证徽章样式
const certifiedBadgeStyle = computed(() => ({
  backgroundColor: mergedStyles.value.certifiedBackgroundColor || '#f0f9eb',
  color: mergedStyles.value.certifiedTextColor || '#67c23a',
  borderColor: mergedStyles.value.certifiedBorderColor || '#c2e7b0'
}))

// 进度条样式
const getProgressBarStyle = (skill) => {
  const style = mergedStyles.value
  const percent = skill.proficiencyPercent || 0

  return {
    width: `${percent}%`,
    background: style.progressBarColor || `linear-gradient(90deg, var(--primary-color), var(--accent-color))`,
    borderRadius: style.progressBarBorderRadius || '6px'
  }
}

// 技能标签样式
const skillTagStyle = computed(() => ({
  backgroundColor: mergedStyles.value.tagBackgroundColor || '#f5f7fa',
  color: mergedStyles.value.tagTextColor || '#666',
  borderColor: mergedStyles.value.tagBorderColor || '#e4e7ed'
}))

// 技能数据适配
const skills = computed(() => {
  const rawSkills = componentProps.value.skills || []
  return rawSkills.map(skill => ({
    id: skill.id || Date.now(),
    name: skill.name || '技能名称',
    category: skill.category || '其他',
    proficiencyPercent: skill.proficiencyPercent || 0,
    level: skill.level || '初级',
    experienceYears: skill.experienceYears || 0,
    description: skill.description || '',
    tags: skill.tags || '',
    isCertified: skill.isCertified || false,
    certificateName: skill.certificateName || '',
    certificateDate: skill.certificateDate || ''
  }))
})

const hasSkillsData = computed(() => skills.value.length > 0)

// 显示设置
const showSkillLevel = computed(() => componentProps.value.skillLevel ?? true)
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

// 获取指定分类的技能
const getSkillsByCategory = (category) => {
  const categorySkills = skills.value.filter(skill => (skill.category || '其他') === category)
  return categorySkills.slice(0, maxSkillsPerCategory.value)
}

// 获取技能标签
const getSkillTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  return tags.split(',').map(tag => tag.trim()).filter(tag => tag)
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''

  const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/)
  if (match) {
    return `${match[1]}.${match[2]}.${match[3]}`
  }
  return dateStr
}

// 不分分类时显示的技能
const displaySkills = computed(() => {
  return skills.value.slice(0, maxSkillsPerCategory.value * 2)
})

// 🚀 调试样式
console.log('技能专长组件样式配置:', {
  全局样式: props.globalStyle,
  组件样式: componentStyles.value,
  默认样式: defaultStyles.value,
  内联样式: props.style,
  合并后样式: mergedStyles.value
})
</script>

<style lang="scss" scoped>
.skills-section {
  margin-bottom: 40rpx;
  background-color: var(--background-color);
  color: var(--text-color);
  font-family: var(--font-family);

  &.theme-modern {
    .section-title {
      color: var(--primary-color);
    }

    .section-divider {
      background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
    }
  }

  &.theme-classic {
    .section-title {
      color: var(--primary-color);
      font-weight: bold;
    }

    .section-divider {
      background: var(--primary-color);
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 30rpx;
    border-radius: 16rpx;
    border: 1rpx dashed #e0e0e0;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
      color: var(--primary-color);
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
        color: var(--text-color);
        font-weight: 600;
        margin-bottom: 20rpx;
        padding-bottom: 10rpx;
        border-bottom: 2rpx solid #f0f0f0;
      }
    }
  }

  // 技能项
  .skill-item {
    background: var(--background-color, #ffffff);
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
    border: 1rpx solid #f0f0f0;

    .skill-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16rpx;

      .skill-name {
        font-size: 28rpx;
        font-weight: 500;
        color: var(--text-color);
        flex: 1;
      }

      .skill-meta {
        text-align: right;
        min-width: 180rpx;

        .skill-level {
          color: var(--primary-color);
          font-size: 24rpx;
          font-weight: 500;
        }

        .skill-category {
          color: var(--secondary-color, #666);
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
          background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
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
        color: var(--accent-color, #faad14);
      }

      .cert-text {
        color: var(--text-color, #555);
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

// 打印样式
@media print {
  .skills-section {
    background-color: #ffffff !important;
    box-shadow: none !important;

    .skill-item {
      background-color: #ffffff !important;
      box-shadow: none !important;
      border: 1rpx solid #e0e0e0 !important;
      page-break-inside: avoid;
    }
  }
}
</style>