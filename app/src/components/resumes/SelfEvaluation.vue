<template>
  <view :class="['self-evaluation', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">{{ title }}</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasEvaluationData" class="empty-state">
      <text class="empty-icon">💭</text>
      <text class="empty-text">暂无自我评价</text>
      <text class="empty-hint">请添加您的个人优势、工作态度或职业目标</text>
    </view>

    <!-- 多评价内容 -->
    <view v-else class="evaluations-list">
      <block v-for="(evaluation, index) in displayEvaluations" :key="evaluation.id || index">
        <view class="evaluation-item" :style="itemStyle">
          <!-- 评价内容 -->
          <view v-if="evaluation.content" class="evaluation-content">
            <text class="content-text">{{ evaluation.content }}</text>

            <!-- 评价时间（可选） -->
            <view v-if="showCreateTime && evaluation.createdAt" class="evaluation-time">
              <text class="time-icon">📅</text>
              <text class="time-text">{{ formatDate(evaluation.createdAt) }}</text>
            </view>
          </view>
        </view>

        <!-- 分隔线 -->
        <view v-if="index < displayEvaluations.length - 1" class="item-divider"></view>
      </block>

      <!-- 显示更多/收起按钮 -->
      <view v-if="evaluations.length > maxDisplayItems" class="show-more-btn" @click="toggleShowAll">
        <text class="btn-text">
          {{ showAll ? '收起' : `查看更多（${evaluations.length - maxDisplayItems}项）` }}
        </text>
        <text class="btn-icon">{{ showAll ? '↑' : '↓' }}</text>
      </view>

      <!-- 默认配置中的关键词 -->
      <view v-if="showCharacterTraits && characterTraits.length > 0" class="default-traits">
        <text class="traits-title">性格特点</text>
        <view class="traits-container">
          <text
            v-for="(trait, index) in characterTraits"
            :key="index"
            :class="['trait-tag', `trait-${index % 4}`]"
          >
            {{ trait }}
          </text>
        </view>
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
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '自我评价')

// 评价数据
const evaluations = computed(() => {
  const rawEvaluations = componentProps.value.evaluations || []
  return rawEvaluations.sort((a, b) => {
    // 按ID降序排列，显示最新的评价
    return (b.id || 0) - (a.id || 0)
  })
})

const hasEvaluationData = computed(() => evaluations.value.length > 0)

// 显示的评价列表
const displayEvaluations = computed(() => {
  if (showAll.value) {
    return evaluations.value
  }
  return evaluations.value.slice(0, maxDisplayItems.value)
})

// 默认配置中的性格特点
const characterTraits = computed(() => {
  return defaultConfig.value.props?.characterTraits || []
})

// 显示选项
const showCharacterTraits = computed(() => defaultConfig.value.props?.showCharacterTraits !== false)
const showCreateTime = computed(() => componentProps.value.showCreateTime !== false)

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37'
}))

const itemStyle = computed(() => ({
  background: componentStyles.value.cardBackground || '#ffffff',
  padding: componentStyles.value.padding || '20px'
}))

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''

  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}.${month}.${day}`
  } catch (error) {
    // 如果解析失败，尝试简单的格式化
    const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/)
    if (match) {
      return `${match[1]}.${match[2]}.${match[3]}`
    }
    return dateStr
  }
}

// 切换显示全部
const toggleShowAll = () => {
  showAll.value = !showAll.value
}

// 组件加载日志
console.log('自我评价组件加载完成', {
  评价数量: evaluations.value.length,
  显示数量: displayEvaluations.value.length,
  性格特点数量: characterTraits.value.length,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.self-evaluation {
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

    .evaluation-item {
      background: #ffffff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 24rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
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

  // 评价项
  .evaluation-item {
    .evaluation-content {
      .content-text {
        color: #444;
        font-size: 28rpx;
        line-height: 1.8;
        display: block;
        margin-bottom: 16rpx;
        white-space: pre-wrap;
        word-break: break-word;
      }

      .evaluation-time {
        display: flex;
        align-items: center;
        color: #999;
        font-size: 22rpx;
        padding-top: 16rpx;
        border-top: 1rpx dashed #f0f0f0;

        .time-icon {
          margin-right: 8rpx;
          font-size: 20rpx;
        }
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

  // 默认性格特点
  .default-traits {
    margin-top: 30rpx;
    padding: 20rpx;
    background: #fafafa;
    border-radius: 12rpx;
    border: 1rpx solid #f0f0f0;

    .traits-title {
      color: #666;
      font-size: 28rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 20rpx;
    }

    .traits-container {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;

      .trait-tag {
        display: inline-flex;
        align-items: center;
        padding: 8rpx 16rpx;
        border-radius: 20rpx;
        font-size: 24rpx;
        border: 1rpx solid transparent;

        // 不同颜色的标签
        &.trait-0 {
          background: rgba(212, 175, 55, 0.1);
          color: #d4af37;
          border-color: rgba(212, 175, 55, 0.2);
        }

        &.trait-1 {
          background: rgba(103, 194, 58, 0.1);
          color: #67c23a;
          border-color: rgba(103, 194, 58, 0.2);
        }

        &.trait-2 {
          background: rgba(64, 158, 255, 0.1);
          color: #409eff;
          border-color: rgba(64, 158, 255, 0.2);
        }

        &.trait-3 {
          background: rgba(230, 162, 60, 0.1);
          color: #e6a23c;
          border-color: rgba(230, 162, 60, 0.2);
        }
      }
    }
  }
}
</style>