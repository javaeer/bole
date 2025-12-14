<template>
  <view class="page-container">
    <!-- 搜索和筛选区域 -->
    <view class="search-section">
      <view class="search-bar">
        <view class="search-input-wrapper">
          <text class="search-icon">🔍</text>
          <input
            v-model="searchKeyword"
            class="search-input"
            placeholder="搜索申请人或简历名称"
            placeholder-class="search-placeholder"
          />
          <view v-if="searchKeyword" class="search-clear" @click="clearSearch">
            ✕
          </view>
        </view>
      </view>

      <view class="filter-tabs">
        <view
          v-for="tab in statusTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ active: activeStatus === tab.value }"
          @click="changeStatus(tab.value)"
        >
          <text class="filter-tab-text">{{ tab.label }}</text>
          <view v-if="tab.count" class="filter-tab-count">{{ tab.count }}</view>
        </view>
      </view>
    </view>

    <!-- 申请列表 -->
    <scroll-view class="list-container" scroll-y @scrolltolower="loadMore">
      <view class="application-list">
        <view
          v-for="item in applicationList"
          :key="item.id"
          class="application-card"
          @click="goToDetail(item.id)"
        >
          <view class="card-header">
            <view class="applicant-info">
              <image
                :src="item.avatar || '/static/default-avatar.png'"
                class="applicant-avatar"
              />
              <view class="applicant-details">
                <text class="applicant-name">{{ item.name }}</text>
                <text class="applicant-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
            <view class="application-status" :class="`status-${item.status}`">
              {{ getStatusText(item.status) }}
            </view>
          </view>

          <view class="card-content">
            <view class="resumes-info">
              <text class="resumes-title">{{ item.resumeTitle }}</text>
              <text class="resumes-position">{{ item.position }}</text>
            </view>
            <view class="application-reason">
              <text class="reason-label">申请理由：</text>
              <text class="reason-content">{{ item.reason }}</text>
            </view>
          </view>

          <view class="card-footer">
            <view class="action-buttons">
              <button
                v-if="item.status === 'pending'"
                class="btn-action btn-review"
                @click.stop="reviewItem(item.id)"
              >
                立即审核
              </button>
              <button
                class="btn-action btn-view"
                @click.stop="viewResume(item.resumeId)"
              >
                查看简历
              </button>
              <button
                v-if="item.status === 'approved'"
                class="btn-action btn-export"
                @click.stop="exportResume(item)"
              >
                导出
              </button>
            </view>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="loading" class="loading-more">
          <view class="loading-spinner"></view>
          <text class="loading-text">加载中...</text>
        </view>

        <view v-if="!hasMore && applicationList.length" class="no-more">
          <text>没有更多数据了</text>
        </view>

        <!-- 空状态 -->
        <view v-if="!applicationList.length && !loading" class="empty-state">
          <image src="/static/empty-export.png" class="empty-image" />
          <text class="empty-text">暂无导出申请</text>
          <text class="empty-subtext">当有用户申请导出时会显示在这里</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import type { Ref } from 'vue'

// ============ 类型定义 ============
interface ApplicationItem {
  id: number
  avatar?: string
  name: string
  createTime: string | number
  resumeTitle: string
  position: string
  reason: string
  resumeId: number
  status: 'pending' | 'approved' | 'rejected' | 'exported'
}

interface StatusTab {
  label: string
  value: string
  count?: number
}

// ============ 响应式数据 ============
const searchKeyword = ref('')
const activeStatus = ref('all')
const applicationList: Ref<ApplicationItem[]> = ref([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 20

// 状态选项卡配置
const statusTabs: Ref<StatusTab[]> = ref([
  { label: '全部', value: 'all', count: 0 },
  { label: '待审核', value: 'pending', count: 0 },
  { label: '已通过', value: 'approved', count: 0 },
  { label: '已拒绝', value: 'rejected', count: 0 },
  { label: '已导出', value: 'exported', count: 0 }
])

// ============ 计算属性 ============
const filteredList = computed(() => {
  let list = applicationList.value

  // 按状态筛选
  if (activeStatus.value !== 'all') {
    list = list.filter(item => item.status === activeStatus.value)
  }

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      item.name.toLowerCase().includes(keyword) ||
      item.resumeTitle.toLowerCase().includes(keyword) ||
      item.position.toLowerCase().includes(keyword)
    )
  }

  return list
})

// ============ 生命周期钩子 ============
onMounted(() => {
  initData()
})

// ============ 方法定义 ============

// 初始化数据
const initData = () => {
  loading.value = true
  // 模拟API请求
  setTimeout(() => {
    // 模拟数据
    applicationList.value = generateMockData(15)
    updateTabCounts()
    loading.value = false
  }, 500)
}

// 生成模拟数据
const generateMockData = (count: number): ApplicationItem[] => {
  const mockData: ApplicationItem[] = []
  const statuses: ApplicationItem['status'][] = ['pending', 'approved', 'rejected', 'exported']
  const positions = ['前端工程师', '后端开发', 'UI设计师', '产品经理', '测试工程师', '运维工程师']
  const reasons = [
    '需要简历用于面试准备',
    '公司内部招聘需要',
    '个人档案整理',
    '岗位匹配度高，希望深入了解',
    '投递其他公司需要',
    '个人发展参考'
  ]

  for (let i = 1; i <= count; i++) {
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    mockData.push({
      id: i,
      name: `用户${i}`,
      avatar: i % 3 === 0 ? undefined : `/static/mock/avatar${i % 5 + 1}.png`,
      createTime: Date.now() - i * 3600000,
      resumeTitle: `高级${positions[i % positions.length]}简历`,
      position: positions[i % positions.length],
      reason: reasons[i % reasons.length],
      resumeId: i,
      status: status
    })
  }

  return mockData
}

// 更新选项卡计数
const updateTabCounts = () => {
  const counts: Record<string, number> = {
    all: applicationList.value.length,
    pending: 0,
    approved: 0,
    rejected: 0,
    exported: 0
  }

  applicationList.value.forEach(item => {
    if (item.status === 'pending') counts.pending++
    else if (item.status === 'approved') counts.approved++
    else if (item.status === 'rejected') counts.rejected++
    else if (item.status === 'exported') counts.exported++
  })

  statusTabs.value = statusTabs.value.map(tab => ({
    ...tab,
    count: counts[tab.value]
  }))
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
}

// 切换状态
const changeStatus = (status: string) => {
  activeStatus.value = status
  currentPage.value = 1
  hasMore.value = true
  // 这里可以根据需要重新加载数据
}

// 加载更多
const loadMore = () => {
  if (loading.value || !hasMore.value) return

  loading.value = true
  // 模拟API请求
  setTimeout(() => {
    const newData = generateMockData(10)
    applicationList.value = [...applicationList.value, ...newData]
    updateTabCounts()

    // 模拟没有更多数据的情况
    if (applicationList.value.length >= 50) {
      hasMore.value = false
    }

    currentPage.value++
    loading.value = false
  }, 800)
}

// 跳转到详情页
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/application/application?id=${id}`
  })
}

// 审核项目
const reviewItem = (id: number) => {
  uni.showModal({
    title: '审核申请',
    content: '请确认是否通过此申请？',
    success: (res) => {
      if (res.confirm) {
        // 模拟审核操作
        const item = applicationList.value.find(app => app.id === id)
        if (item) {
          item.status = 'approved'
          updateTabCounts()
          uni.showToast({
            title: '审核通过',
            icon: 'success'
          })
        }
      }
    }
  })
}

// 查看简历
const viewResume = (resumeId: number) => {
  uni.navigateTo({
    url: `/pages/resumes/preview?id=${resumeId}`
  })
}

// 导出简历
const exportResume = (item: ApplicationItem) => {
  uni.showLoading({ title: '导出中...' })

  // 模拟导出操作
  setTimeout(() => {
    uni.hideLoading()

    // 更新状态为已导出
    item.status = 'exported'
    updateTabCounts()

    uni.showModal({
      title: '导出成功',
      content: '简历已成功导出到您的设备',
      showCancel: false,
      success: () => {
        // 可以在这里触发下载或分享
        uni.showToast({
          title: '导出成功',
          icon: 'success'
        })
      }
    })
  }, 1500)
}

// 格式化时间
const formatTime = (time: string | number) => {
  const date = new Date(Number(time))
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 今天以内
  if (diff < 24 * 60 * 60 * 1000) {
    if (diff < 60 * 60 * 1000) {
      return `${Math.floor(diff / (60 * 1000))}分钟前`
    }
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }

  // 昨天
  if (diff < 48 * 60 * 60 * 1000) {
    return '昨天'
  }

  // 一周内
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }

  // 超过一周显示具体日期
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 获取状态文本
const getStatusText = (status: ApplicationItem['status']) => {
  const statusMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    exported: '已导出'
  }
  return statusMap[status] || '未知状态'
}

// 页面卸载时清理
onUnmounted(() => {
  // 清理数据
  searchKeyword.value = ''
  applicationList.value = []
})

// ============ 暴露给模板使用 ============
defineExpose({
  searchKeyword,
  activeStatus,
  applicationList: filteredList,
  loading,
  hasMore,
  statusTabs,
  clearSearch,
  changeStatus,
  loadMore,
  goToDetail,
  reviewItem,
  viewResume,
  exportResume,
  formatTime,
  getStatusText
})
</script>

<style lang="scss" scoped>
.page-container {
  background-color: $background-color;
  min-height: 100vh;
}

/* 搜索区域 */
.search-section {
  background: $background-color-white;
  padding: $padding-base;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1rpx solid $border-color-light;
}

.search-bar {
  margin-bottom: $padding-base;
}

.search-input-wrapper {
  position: relative;
  background: $background-color;
  border-radius: $border-radius * 2;
  padding: 0 $padding-base;
  display: flex;
  align-items: center;
  border: 2rpx solid $border-color-light;
  transition: border-color $transition-fast;

  &:focus-within {
    border-color: $primary-color;
    box-shadow: $input-focus-shadow;
  }
}

.search-icon {
  font-size: $font-size-base;
  color: $text-placeholder;
  margin-right: $padding-mini;
}

.search-input {
  flex: 1;
  height: 80rpx;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

.search-clear {
  font-size: $font-size-small;
  color: $text-placeholder;
  padding: $padding-mini;
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: $margin-small;
  overflow-x: auto;
  padding-bottom: 5rpx;

  &::-webkit-scrollbar {
    display: none;
  }
}

.filter-tab {
  flex-shrink: 0;
  padding: $padding-mini $padding-base;
  border-radius: $border-radius-small;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  display: flex;
  align-items: center;
  gap: $margin-mini;
  transition: all $transition-fast;

  &.active {
    background: $primary-color;
    border-color: $primary-color;

    .filter-tab-text {
      color: $background-color-white;
      font-weight: $font-weight-medium;
    }

    .filter-tab-count {
      background: rgba($background-color-white, 0.2);
      color: $background-color-white;
    }
  }
}

.filter-tab-text {
  font-size: $font-size-small;
  color: $text-regular;
}

.filter-tab-count {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  background: $border-color-lighter;
  border-radius: 10rpx;
  padding: 2rpx 8rpx;
  min-width: 30rpx;
  text-align: center;
}

/* 列表容器 */
.list-container {
  height: calc(100vh - 240rpx);
}

.application-list {
  padding: 0 $padding-base $padding-base;
}

/* 申请卡片 */
.application-card {
  background: $background-color-white;
  border-radius: $border-radius;
  margin-bottom: $margin-base;
  padding: $padding-base;
  box-shadow: $box-shadow;
  transition: transform $transition-fast;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $box-shadow-dark;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.applicant-info {
  display: flex;
  align-items: center;
  gap: $margin-small;
}

.applicant-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-round;
  border: 2rpx solid $border-color-light;
}

.applicant-details {
  display: flex;
  flex-direction: column;
}

.applicant-name {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: 5rpx;
}

.applicant-time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.application-status {
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  padding: $padding-mini $padding-small;
  border-radius: $border-radius-small;

  &.status-pending {
    background: $warning-light;
    color: $warning-color;
  }

  &.status-approved {
    background: rgba($success-color, 0.1);
    color: $success-color;
  }

  &.status-rejected {
    background: $danger-light;
    color: $danger-color;
  }

  &.status-exported {
    background: rgba($info-color, 0.1);
    color: $info-color;
  }
}

/* 卡片内容 */
.card-content {
  margin-bottom: $margin-base;
}

.resumes-info {
  margin-bottom: $margin-base;
}

.resumes-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.resumes-position {
  font-size: $font-size-small;
  color: $text-secondary;
  background: $background-color;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;
  display: inline-block;
}

.application-reason {
  display: flex;
  align-items: flex-start;
}

.reason-label {
  font-size: $font-size-small;
  color: $text-secondary;
  flex-shrink: 0;
  margin-right: $margin-mini;
}

.reason-content {
  flex: 1;
  font-size: $font-size-small;
  color: $text-regular;
  line-height: 1.4;
}

/* 卡片底部 */
.card-footer {
  border-top: 1rpx solid $border-color-extra-light;
  padding-top: $padding-small;
}

.action-buttons {
  display: flex;
  gap: $margin-small;

  .btn-action {
    flex: 1;
    height: 70rpx;
    border-radius: $border-radius-small;
    font-size: $font-size-small;
    font-weight: $font-weight-medium;
    border: none;
    transition: all $transition-fast;

    &:active {
      transform: scale(0.98);
    }
  }

  .btn-review {
    background: $primary-color;
    color: $background-color-white;

    &:active {
      background: color.adjust($primary-color, $lightness:  -10%);
    }
  }

  .btn-view {
    background: $background-color;
    color: $text-regular;
    border: 1rpx solid $border-color-light;

    &:active {
      background: color.adjust($background-color, $lightness:  - 5%);
    }
  }

  .btn-export {
    background: $success-color;
    color: $background-color-white;

    &:active {
      background: color.adjust($success-color, $lightness:  -10%);
    }
  }
}

/* 加载状态 */
.loading-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base;
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: $margin-small;
}

.no-more {
  text-align: center;
  padding: $padding-base;
  font-size: $font-size-small;
  color: $text-placeholder;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx $padding-base;
  text-align: center;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: $margin-base;
  opacity: 0.6;
}

.empty-text {
  font-size: $font-size-medium;
  color: $text-secondary;
  margin-bottom: $margin-small;
}

.empty-subtext {
  font-size: $font-size-small;
  color: $text-placeholder;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>