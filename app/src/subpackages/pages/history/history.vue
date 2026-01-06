<template>
  <view class="page-container">
    <!-- 空状态 -->
    <view class="empty-state" v-if="historyList.length === 0">
      <view class="empty-icon">📊</view>
      <text class="empty-text">暂无浏览记录</text>
      <text class="empty-subtext">浏览过的简历和公司会出现在这里</text>
      <view class="btn-explore" @click="handleGoExplore">
        <text class="btn-explore-text">去发现</text>
      </view>
    </view>

    <!-- 筛选选项卡 -->
    <view class="filter-tabs" v-if="historyList.length > 0">
      <view 
        class="filter-tab"
        :class="{ 'filter-tab--active': filterType === 'all' }"
        @click="filterType = 'all'"
      >
        <text class="filter-tab-text">全部</text>
      </view>
      <view 
        class="filter-tab"
        :class="{ 'filter-tab--active': filterType === 'resumes' }"
        @click="filterType = 'resumes'"
      >
        <text class="filter-tab-text">简历</text>
      </view>
      <view 
        class="filter-tab"
        :class="{ 'filter-tab--active': filterType === 'company' }"
        @click="filterType = 'company'"
      >
        <text class="filter-tab-text">公司</text>
      </view>
    </view>

    <!-- 历史列表 -->
    <scroll-view 
      class="history-list" 
      scroll-y 
      v-if="historyList.length > 0"
    >
      <view 
        class="history-item"
        v-for="item in filteredHistory"
        :key="item.id"
        @click="handleViewDetail(item)"
      >
        <view class="history-item-left">
          <view class="item-type">
            <view class="type-icon">
              <text>{{ item.type === 'resumes' ? '📄' : '🏢' }}</text>
            </view>
            <view class="type-badge" :class="`type-${item.type}`">
              <text class="type-text">{{ item.type === 'resumes' ? '简历' : '公司' }}</text>
            </view>
          </view>
          
          <view class="item-content">
            <text class="item-title">{{ item.title }}</text>
            <view class="item-meta">
              <text class="item-time">{{ formatTime(item.time) }}</text>
              <text class="item-divider">·</text>
              <text class="item-tag">{{ item.tag }}</text>
            </view>
            <view class="item-desc">
              <text class="item-desc-text">{{ item.description }}</text>
            </view>
          </view>
        </view>
        
        <view class="history-item-right">
          <view 
            class="btn-delete"
            @click.stop="handleDeleteItem(item.id)"
          >
            <text class="delete-icon">×</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 操作面板 -->
    <view class="action-panel" v-if="historyList.length > 0">
      <view class="action-stats">
        <text class="stats-text">
          共 {{ historyList.length }} 条记录，
          最近 {{ latestDays }} 天 {{ recentCount }} 条
        </text>
      </view>
      <view class="action-buttons">
        <view 
          class="btn-action btn-action--export"
          @click="handleExportHistory"
        >
          <text class="btn-action-text">导出记录</text>
        </view>
        <view 
          class="btn-action btn-action--refresh"
          @click="handleRefresh"
        >
          <text class="btn-action-text">刷新</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

// 历史记录类型
interface HistoryItem {
  id: string
  type: 'resumes' | 'company'
  title: string
  description: string
  tag: string
  time: Date
  viewedCount: number
}

// 数据状态
const historyList = ref<HistoryItem[]>([
  {
    id: '1',
    type: 'resumes',
    title: '高级前端开发工程师',
    description: '5年经验，精通Vue3、TypeScript，有大型项目经验',
    tag: '互联网',
    time: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2小时前
    viewedCount: 3
  },
  {
    id: '2',
    type: 'company',
    title: '腾讯科技有限公司',
    description: '互联网巨头，提供多元化的互联网增值服务',
    tag: '科技公司',
    time: new Date(Date.now() - 5 * 60 * 60 * 1000), // 5小时前
    viewedCount: 1
  },
  {
    id: '3',
    type: 'resumes',
    title: 'UI/UX设计师',
    description: '专注用户体验设计，熟悉设计系统和交互设计',
    tag: '设计',
    time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), // 1天前
    viewedCount: 2
  },
  {
    id: '4',
    type: 'company',
    title: '字节跳动',
    description: '全球化互联网科技公司，产品覆盖多个领域',
    tag: '互联网',
    time: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), // 2天前
    viewedCount: 1
  },
  {
    id: '5',
    type: 'resumes',
    title: 'Java后端开发工程师',
    description: '精通Spring Cloud、微服务架构，有高并发处理经验',
    tag: '后端开发',
    time: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000), // 3天前
    viewedCount: 1
  }
])

// 筛选状态
const filterType = ref<'all' | 'resumes' | 'company'>('all')

// 计算属性
const filteredHistory = computed(() => {
  if (filterType.value === 'all') {
    return historyList.value.sort((a, b) => b.time.getTime() - a.time.getTime())
  }
  return historyList.value
    .filter(item => item.type === filterType.value)
    .sort((a, b) => b.time.getTime() - a.time.getTime())
})

const recentCount = computed(() => {
  const sevenDaysAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)
  return historyList.value.filter(item => item.time > sevenDaysAgo).length
})

const latestDays = ref(7)

// 格式化时间显示
const formatTime = (time: Date) => {
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
  }
}

// 处理函数
const handleClearAll = () => {
  uni.showModal({
    title: '确认清空',
    content: '确定要清空所有浏览记录吗？此操作不可恢复。',
    success: (res) => {
      if (res.confirm) {
        historyList.value = []
        uni.showToast({
          title: '已清空',
          icon: 'success'
        })
      }
    }
  })
}

const handleDeleteItem = (id: string) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条记录吗？',
    success: (res) => {
      if (res.confirm) {
        historyList.value = historyList.value.filter(item => item.id !== id)
        uni.showToast({
          title: '已删除',
          icon: 'success'
        })
      }
    }
  })
}

const handleViewDetail = (item: HistoryItem) => {
  if (item.type === 'resumes') {
    uni.navigateTo({
      url: `/pages/resumes/resumes?id=${item.id}`
    })
  } else {
    uni.navigateTo({
      url: `/subpackages/pages/company/company?id=${item.id}`
    })
  }
}

const handleGoExplore = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

const handleExportHistory = () => {
  uni.showToast({
    title: '导出功能开发中',
    icon: 'none'
  })
}

const handleRefresh = () => {
  // 模拟刷新数据
  uni.showLoading({
    title: '刷新中'
  })
  
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({
      title: '已刷新',
      icon: 'success'
    })
  }, 1000)
}

onMounted(() => {
  console.log('浏览历史页面加载完成')
})
</script>

<style scoped lang="scss">

.btn-clear {
  padding: $padding-mini $padding-small;
  background: $danger-light;
  border-radius: $border-radius-small;
  cursor: pointer;
}

.btn-clear-text {
  font-size: $font-size-small;
  color: $danger-color;
  font-weight: $font-weight-medium;
}

/* 空状态 */
.empty-state {
  padding: $margin-base * 3 $padding-base;
  text-align: center;
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: $margin-base;
  opacity: 0.3;
}

.empty-text {
  display: block;
  font-size: $font-size-medium;
  color: $text-secondary;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-medium;
}

.empty-subtext {
  display: block;
  font-size: $font-size-small;
  color: $text-placeholder;
  margin-bottom: $margin-base * 2;
}

.btn-explore {
  display: inline-block;
  padding: $padding-mini $padding-base * 2;
  background: $primary-color;
  border-radius: $border-radius;
  cursor: pointer;
}

.btn-explore-text {
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
}

/* 筛选选项卡 */
.filter-tabs {
  display: flex;
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
  padding: $padding-mini;
  position: sticky;
  top: calc($navigation-bar-height + 90rpx);
  z-index: $z-index-base;
}

.filter-tab {
  flex: 1;
  text-align: center;
  padding: $padding-small 0;
  cursor: pointer;
  border-radius: $border-radius-small;
  transition: all $transition-fast;
  
  &--active {
    background: $primary-color;
    
    .filter-tab-text {
      color: $background-color-white;
      font-weight: $font-weight-bold;
    }
  }
}

.filter-tab-text {
  font-size: $font-size-base;
  color: $text-secondary;
  transition: color $transition-fast;
}

/* 历史列表 */
.history-list {
  height: calc(100vh - 400rpx);
  padding: 0 $margin-base;
}

.history-item {
  background: $background-color-white;
  border-radius: $border-radius;
  margin-bottom: $margin-small;
  padding: $padding-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  box-shadow: $box-shadow;
  transition: transform $transition-fast;
  
  &:active {
    transform: scale(0.99);
    box-shadow: $box-shadow-light;
  }
}

.history-item-left {
  flex: 1;
  display: flex;
  align-items: flex-start;
}

.item-type {
  margin-right: $margin-small;
}

.type-icon {
  width: 60rpx;
  height: 60rpx;
  background: $primary-color-light;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-medium;
  margin-bottom: $margin-mini;
}

.type-badge {
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  font-size: $font-size-extra-small;
  text-align: center;
  
  &.type-resumes {
    background: rgba($success-color, 0.1);
    
    .type-text {
      color: $success-color;
    }
  }
  
  &.type-company {
    background: rgba($info-color, 0.1);
    
    .type-text {
      color: $info-color;
    }
  }
}

.item-content {
  flex: 1;
}

.item-title {
  display: block;
  font-size: $font-size-medium;
  color: $text-primary;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-mini;
  @extend .text-ellipsis;
}

.item-meta {
  display: flex;
  align-items: center;
  margin-bottom: $margin-mini;
}

.item-time {
  font-size: $font-size-small;
  color: $text-placeholder;
}

.item-divider {
  margin: 0 8rpx;
  color: $border-color;
}

.item-tag {
  font-size: $font-size-small;
  color: $text-secondary;
  background: $border-color-extra-light;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
}

.item-desc {
  @extend .text-ellipsis;
}

.item-desc-text {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.4;
}

.history-item-right {
  margin-left: $margin-small;
}

.btn-delete {
  width: 40rpx;
  height: 40rpx;
  border-radius: $border-radius-round;
  background: $danger-light;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  
  .delete-icon {
    color: $danger-color;
    font-size: $font-size-large;
    font-weight: $font-weight-bold;
    line-height: 1;
  }
}

/* 操作面板 */
.action-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $background-color-white;
  padding: $padding-base;
  border-top: 1rpx solid $border-color-light;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.action-stats {
  text-align: center;
  margin-bottom: $margin-small;
}

.stats-text {
  font-size: $font-size-small;
  color: $text-secondary;
}

.action-buttons {
  display: flex;
  gap: $margin-small;
}

.btn-action {
  flex: 1;
  text-align: center;
  padding: $padding-small 0;
  border-radius: $border-radius;
  cursor: pointer;
  
  &--export {
    background: $primary-color;
    
    .btn-action-text {
      color: $background-color-white;
    }
  }
  
  &--refresh {
    background: $background-color;
    border: 1rpx solid $border-color;
    
    .btn-action-text {
      color: $text-regular;
    }
  }
}

.btn-action-text {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .history-list {
    padding: 0 $margin-small;
  }
  
  .history-item {
    padding: $padding-small;
  }
}
</style>