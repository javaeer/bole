<template>
  <view :class="['job-intention', `layout-${computedLayout}`, `theme-${theme}`]">
    <view class="section-header">
      <text class="section-title">{{ title }}</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasIntentionData" class="empty-state">
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
            <text class="position">{{ intention.position || '待定' }}</text>
            <view class="meta-info">
              <text v-if="intention.city" class="meta-item">
                <text class="icon">📍</text>
                {{ intention.city }}
              </text>
              <text v-if="showSalary && intention.salary" class="meta-item">
                <text class="icon">💰</text>
                {{ formatSalary(intention.salary) }}
              </text>
              <text class="meta-item">
                <text class="icon">🕐</text>
                {{ formatJobType(intention.jobType) }}
              </text>
            </view>
          </view>

          <!-- 工作类型标签 -->
          <view v-if="intention.jobType" class="job-type-tag">
            <text class="tag-text">{{ formatJobType(intention.jobType) }}</text>
          </view>
        </view>

        <!-- 分隔线 -->
        <view v-if="index < displayIntentions.length - 1" class="item-divider"></view>
      </block>

      <!-- 显示更多/收起按钮 -->
      <view v-if="intentions.length > maxDisplayItems" class="show-more-btn" @click="toggleShowAll">
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
  config: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'modern'
  }
})

const showAll = ref(false)
const maxDisplayItems = ref(2)

// 提取配置
const componentProps = computed(() => props.config.props || {})
const componentStyles = computed(() => props.config.styles || {})
const defaultConfig = computed(() => props.config.defaultConfig || {})

// 标题
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '求职意向')

// 求职意向数据
const intentions = computed(() => {
  const rawIntentions = componentProps.value.intentions || []
  return rawIntentions.sort((a, b) => {
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

// 样式相关
const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff',
  padding: componentStyles.value.padding || '20px'
}))

// 布局类型（修复：确保不会访问 undefined 的 layout）
const computedLayout = computed(() => {
  // 优先使用 styles.layout，然后是 defaultConfig.props?.layout，最后是默认值
  return componentStyles.value.layout ||
    defaultConfig.value.props?.layout ||
    'card'
})

// 显示选项
const showSalary = computed(() => componentProps.value.showSalary !== false)

// 格式化薪资
const formatSalary = (salary) => {
  if (!salary) return ''

  // 如果薪资是数字字符串，格式化为K为单位
  if (/^\d+$/.test(salary)) {
    const num = parseInt(salary)
    if (num >= 10000) {
      return `${(num / 10000).toFixed(1)}万`
    }
    return `${salary}元`
  }
  return salary
}

// 格式化工作类型
const formatJobType = (jobType) => {
  if (!jobType) return '全职'

  const typeMap = {
    '全': '全职',
    '兼': '兼职',
    '实': '实习',
    'remote': '远程',
    'freelance': '自由职业'
  }

  return typeMap[jobType] || jobType
}

// 切换显示全部
const toggleShowAll = () => {
  showAll.value = !showAll.value
}

// 组件加载日志
console.log('求职意向组件加载完成', {
  意向数量: intentions.value.length,
  显示数量: displayIntentions.value.length,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.job-intention {
  margin-bottom: 40rpx;

  // 布局样式
  &.layout-card {
    .intention-item {
      background: #ffffff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 24rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid #f0f0f0;
      position: relative;
    }
  }

  &.layout-simple {
    .intention-item {
      background: #f8fafc;
      border-radius: 8rpx;
      padding: 24rpx;
      margin-bottom: 16rpx;
      border: 1rpx solid #e2e8f0;
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

    .position {
      color: #333;
      font-size: 38rpx;
      font-weight: 600;
      margin-bottom: 20rpx;
      display: block;
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
    text-align: center;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
    }

    .empty-text {
      color: #333;
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
        color: #333;
        font-size: 38rpx;
        font-weight: 600;
        margin-bottom: 20rpx;
        display: block;
      }
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

    .job-type-tag {
      position: absolute;
      top: 30rpx;
      right: 30rpx;

      .tag-text {
        background-color: rgba(212, 175, 55, 0.1);
        color: #d4af37;
        font-size: 22rpx;
        padding: 4rpx 12rpx;
        border-radius: 20rpx;
        border: 1rpx solid rgba(212, 175, 55, 0.2);
      }
    }
  }

  // 分隔线
  .item-divider {
    height: 1rpx;
    background: linear-gradient(90deg, transparent, #f0f0f0, transparent);
    margin: 20rpx 0;
  }

  // 显示更多按钮
  .show-more-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #d4af37;
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