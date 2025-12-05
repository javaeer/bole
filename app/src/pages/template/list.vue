<template>
  <view class="list-page-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索简历名称、职位或技能"
          placeholder-class="search-placeholder"
          v-model="searchKeyword"
          @input="handleSearch"
        />
        <view v-if="searchKeyword" class="search-clear" @click="clearSearch">
          <text class="clear-icon">×</text>
        </view>
      </view>
      <button class="btn-filter" @click="showFilter = true">
        <text class="filter-icon">筛选</text>
      </button>
    </view>

    <!-- 筛选标签 -->
    <view v-if="activeFilters.length > 0" class="filter-tags">
      <scroll-view class="tags-scroll" scroll-x="true">
        <view class="tags-container">
          <view class="filter-tag" v-for="(filter, index) in activeFilters" :key="index">
            <text class="tag-text">{{ filter.label }}: {{ filter.value }}</text>
            <text class="tag-remove" @click="removeFilter(index)">×</text>
          </view>
          <view class="clear-all" @click="clearAllFilters">
            <text class="clear-all-text">清除全部</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 排序栏 -->
    <view class="sort-bar">
      <view class="sort-tabs">
        <view
          class="sort-tab"
          :class="{ active: sortField === 'updateTime' }"
          @click="changeSort('updateTime')"
        >
          <text class="sort-text">最近更新</text>
          <view v-if="sortField === 'updateTime'" class="sort-arrow">
            <text>{{ sortOrder === 'desc' ? '↓' : '↑' }}</text>
          </view>
        </view>
        <view
          class="sort-tab"
          :class="{ active: sortField === 'name' }"
          @click="changeSort('name')"
        >
          <text class="sort-text">名称</text>
          <view v-if="sortField === 'name'" class="sort-arrow">
            <text>{{ sortOrder === 'desc' ? '↓' : '↑' }}</text>
          </view>
        </view>
        <view
          class="sort-tab"
          :class="{ active: sortField === 'matchScore' }"
          @click="changeSort('matchScore')"
        >
          <text class="sort-text">匹配度</text>
          <view v-if="sortField === 'matchScore'" class="sort-arrow">
            <text>{{ sortOrder === 'desc' ? '↓' : '↑' }}</text>
          </view>
        </view>
      </view>
      <view class="view-mode">
        <text
          class="view-mode-btn"
          :class="{ active: viewMode === 'list' }"
          @click="viewMode = 'list'"
        >
          ≡
        </text>
        <text
          class="view-mode-btn"
          :class="{ active: viewMode === 'grid' }"
          @click="viewMode = 'grid'"
        >
          ☐
        </text>
      </view>
    </view>

    <!-- 列表内容 -->
    <view v-if="loading" class="loading-state">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="resumes.length === 0" class="empty-state">
      <text class="empty-icon">📄</text>
      <text class="empty-text">暂无简历</text>
      <text class="empty-subtext">创建你的第一份简历开始吧</text>
      <button class="btn-create" @click="handleCreateResume">
        <text class="create-text">+ 新建简历</text>
      </button>
    </view>

    <view v-else class="resume-list" :class="viewMode">
      <!-- 列表视图 -->
      <view v-if="viewMode === 'list'" class="list-view">
        <view
          class="resume-item"
          v-for="resume in resumes"
          :key="resume.id"
          @click="handleViewResume(resume)"
        >
          <view class="resume-item-header">
            <view class="resume-avatar">
              <text class="avatar-text">{{ resume.name.charAt(0) }}</text>
            </view>
            <view class="resume-main-info">
              <text class="resume-name">{{ resume.name }}</text>
              <text class="resume-title">{{ resume.title }}</text>
            </view>
            <view class="resume-status">
              <view class="status-badge" :class="resume.status">
                <text class="status-text">{{ getStatusText(resume.status) }}</text>
              </view>
            </view>
          </view>

          <view class="resume-item-content">
            <view class="resume-meta">
              <view class="meta-item">
                <text class="meta-icon">📅</text>
                <text class="meta-text">更新: {{ formatDate(resume.updateTime) }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-icon">👁️</text>
                <text class="meta-text">{{ resume.viewCount }} 浏览</text>
              </view>
            </view>

            <view class="resume-skills">
              <text
                class="skill-tag"
                v-for="(skill, index) in resume.skills.slice(0, 3)"
                :key="index"
              >
                {{ skill }}
              </text>
              <text v-if="resume.skills.length > 3" class="more-skills">
                +{{ resume.skills.length - 3 }}
              </text>
            </view>
          </view>

          <view class="resume-item-actions">
            <button class="btn-action" @click.stop="handleEdit(resume)">
              <text class="action-text">编辑</text>
            </button>
            <button class="btn-action" @click.stop="handleShare(resume)">
              <text class="action-text">分享</text>
            </button>
            <button class="btn-action" @click.stop="handleDuplicate(resume)">
              <text class="action-text">复制</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 网格视图 -->
      <view v-if="viewMode === 'grid'" class="grid-view">
        <view
          class="resume-card"
          v-for="resume in resumes"
          :key="resume.id"
          @click="handleViewResume(resume)"
        >
          <view class="card-header">
            <view class="card-avatar">
              <text class="avatar-text">{{ resume.name.charAt(0) }}</text>
            </view>
            <view class="card-title">
              <text class="card-name">{{ resume.name }}</text>
              <text class="card-job">{{ resume.title }}</text>
            </view>
          </view>

          <view class="card-status">
            <view class="status-badge" :class="resume.status">
              <text class="status-text">{{ getStatusText(resume.status) }}</text>
            </view>
          </view>

          <view class="card-skills">
            <text
              class="skill-chip"
              v-for="(skill, index) in resume.skills.slice(0, 2)"
              :key="index"
            >
              {{ skill }}
            </text>
          </view>

          <view class="card-footer">
            <text class="update-time">更新于 {{ formatRelativeTime(resume.updateTime) }}</text>
            <view class="card-actions">
              <text class="card-action" @click.stop="handleEdit(resume)">✏️</text>
              <text class="card-action" @click.stop="handleShare(resume)">↗️</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && !loading" class="load-more" @click="loadMore">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>

    <!-- 创建按钮 -->
    <view class="floating-action">
      <button class="btn-fab" @click="handleCreateResume">
        <text class="fab-text">+</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'

// 搜索和筛选状态
const searchKeyword = ref('')
const activeFilters = ref<Array<{label: string, value: string}>>([])
const sortField = ref('updateTime')
const sortOrder = ref('desc')
const viewMode = ref('list')
const showFilter = ref(false)

// 数据状态
const loading = ref(false)
const resumes = ref<any[]>([
  {
    id: 1,
    name: '高级前端工程师简历',
    title: '高级前端开发工程师',
    status: 'published',
    updateTime: '2024-01-15',
    viewCount: 245,
    skills: ['Vue.js', 'TypeScript', 'Node.js', 'Webpack', 'React']
  },
  {
    id: 2,
    name: '全栈开发工程师简历',
    title: '全栈开发工程师',
    status: 'draft',
    updateTime: '2024-01-14',
    viewCount: 89,
    skills: ['JavaScript', 'Python', 'Django', 'Vue.js', 'MySQL']
  },
  {
    id: 3,
    name: 'UI设计师作品集',
    title: 'UI/UX设计师',
    status: 'published',
    updateTime: '2024-01-12',
    viewCount: 156,
    skills: ['Figma', 'Sketch', 'UI设计', '用户体验', '原型设计']
  },
  {
    id: 4,
    name: '产品经理简历',
    title: '高级产品经理',
    status: 'archived',
    updateTime: '2024-01-10',
    viewCount: 312,
    skills: ['产品规划', '用户研究', '数据分析', '项目管理']
  }
])

const hasMore = ref(true)

// 搜索处理
const handleSearch = () => {
  // 实际项目中这里会调用API
  console.log('搜索关键词:', searchKeyword.value)
}

const clearSearch = () => {
  searchKeyword.value = ''
  handleSearch()
}

// 筛选处理
const removeFilter = (index: number) => {
  activeFilters.value.splice(index, 1)
}

const clearAllFilters = () => {
  activeFilters.value = []
}

// 排序处理
const changeSort = (field: string) => {
  if (sortField.value === field) {
    sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  } else {
    sortField.value = field
    sortOrder.value = 'desc'
  }
  // 实际项目中这里会重新排序数据
}

// 操作处理
const handleViewResume = (resume: any) => {
  uni.navigateTo({
    url: `/pages/resume/detail?id=${resume.id}`
  })
}

const handleEdit = (resume: any) => {
  uni.navigateTo({
    url: `/pages/resume/edit?id=${resume.id}`
  })
}

const handleShare = (resume: any) => {
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  })
}

const handleDuplicate = (resume: any) => {
  uni.showToast({
    title: '已复制简历',
    icon: 'success'
  })
}

const handleCreateResume = () => {
  uni.navigateTo({
    url: '/pages/resume/create'
  })
}

const loadMore = () => {
  loading.value = true
  // 模拟加载更多数据
  setTimeout(() => {
    const newResumes = [
      {
        id: resumes.value.length + 1,
        name: `新增简历 ${resumes.value.length + 1}`,
        title: '软件工程师',
        status: 'published',
        updateTime: '2024-01-08',
        viewCount: 45,
        skills: ['Java', 'Spring Boot', 'MySQL']
      }
    ]
    resumes.value.push(...newResumes)
    hasMore.value = resumes.value.length < 10
    loading.value = false
  }, 1000)
}

// 工具函数
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    archived: '已归档'
  }
  return statusMap[status] || '未知'
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const formatRelativeTime = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '昨天'
  if (diffDays < 7) return `${diffDays}天前`
  return `${Math.floor(diffDays / 7)}周前`
}

onMounted(() => {
  console.log('列表页面加载完成')
})
</script>

<style scoped lang="scss">
.list-page-container {
  background-color: $background-color;
  min-height: 100vh;
  padding-bottom: $tabbar-height;
}

/* 搜索栏 */
.search-bar {
  background: $background-color-white;
  padding: $padding-small $padding-base;
  display: flex;
  align-items: center;
  gap: $margin-small;
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  box-shadow: $box-shadow-light;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  background: $background-color;
  border-radius: $border-radius;
  border: 2rpx solid $border-color-light;
  display: flex;
  align-items: center;
  padding: 0 $padding-small;
  transition: all $transition-fast;
  
  &:focus-within {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
  }
}

.search-icon {
  font-size: $font-size-base;
  color: $text-placeholder;
  margin-right: $margin-mini;
}

.search-input {
  flex: 1;
  height: $input-height - 20rpx;
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
  width: 40rpx;
  height: 40rpx;
  border-radius: $border-radius-round;
  background: $border-color;
  @extend .flex-center;
  cursor: pointer;
}

.clear-icon {
  font-size: $font-size-medium;
  color: $text-secondary;
  font-weight: $font-weight-bold;
}

.btn-filter {
  height: $input-height - 20rpx;
  background: $background-color;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius;
  padding: 0 $padding-small;
  font-size: $font-size-base;
  color: $text-regular;
  white-space: nowrap;
}

.filter-icon {
  font-size: $font-size-small;
}

/* 筛选标签 */
.filter-tags {
  background: $background-color-white;
  padding: $padding-mini $padding-base;
  border-bottom: 2rpx solid $border-color-extra-light;
}

.tags-scroll {
  width: 100%;
  white-space: nowrap;
}

.tags-container {
  display: inline-flex;
  align-items: center;
  gap: $margin-mini;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  background: $primary-light;
  border-radius: $border-radius-small;
  padding: calc($padding-mini / 2) $padding-small;
  gap: calc($margin-mini / 2);
}

.tag-text {
  font-size: $font-size-extra-small;
  color: $primary-color;
}

.tag-remove {
  font-size: $font-size-small;
  color: $primary-color;
  font-weight: $font-weight-bold;
  cursor: pointer;
}

.clear-all {
  padding: calc($padding-mini / 2) $padding-small;
}

.clear-all-text {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  cursor: pointer;
}

/* 排序栏 */
.sort-bar {
  background: $background-color-white;
  padding: $padding-small $padding-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2rpx solid $border-color-extra-light;
}

.sort-tabs {
  display: flex;
  gap: $margin-base * 2;
}

.sort-tab {
  display: flex;
  align-items: center;
  gap: calc($margin-mini / 2);
  cursor: pointer;
  padding: calc($padding-mini / 2) 0;
  
  &.active {
    .sort-text {
      color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }
}

.sort-text {
  font-size: $font-size-base;
  color: $text-regular;
}

.sort-arrow {
  font-size: $font-size-small;
  color: $primary-color;
}

.view-mode {
  display: flex;
  gap: $margin-small;
}

.view-mode-btn {
  font-size: $font-size-medium;
  color: $text-placeholder;
  cursor: pointer;
  padding: calc($padding-mini / 2) $padding-mini;
  
  &.active {
    color: $primary-color;
    background: $primary-light;
    border-radius: $border-radius-small;
  }
}

/* 加载状态 */
.loading-state {
  @extend .flex-center;
  flex-direction: column;
  padding: $padding-base * 2;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: $border-radius-round;
  animation: spin 1s linear infinite;
  margin-bottom: $margin-small;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-size-base;
  color: $text-secondary;
}

/* 空状态 */
.empty-state {
  @extend .flex-center;
  flex-direction: column;
  padding: $padding-base * 3 $padding-base;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: $margin-base;
  opacity: 0.3;
}

.empty-text {
  font-size: $font-size-medium;
  color: $text-regular;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-medium;
}

.empty-subtext {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-bottom: $margin-base * 2;
}

.btn-create {
  background: $primary-color;
  color: $background-color-white;
  border: none;
  border-radius: $border-radius;
  padding: $padding-small $padding-base * 1.5;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
}

.create-text {
  color: $background-color-white;
}

/* 简历列表 */
.resume-list {
  padding: $padding-base;
}

/* 列表视图 */
.list-view {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.resume-item {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
  transition: all $transition-fast;
  
  &:active {
    background: color.adjust($background-color-white, $lightness:  -2%);
    box-shadow: $box-shadow-dark;
  }
}

.resume-item-header {
  display: flex;
  align-items: center;
  margin-bottom: $margin-small;
}

.resume-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-round;
  background: linear-gradient(135deg, $primary-color, $secondary-color);
  @extend .flex-center;
  margin-right: $margin-small;
}

.avatar-text {
  font-size: $font-size-medium;
  color: $background-color-white;
  font-weight: $font-weight-bold;
}

.resume-main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: calc($margin-mini / 2);
}

.resume-name {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.resume-title {
  font-size: $font-size-small;
  color: $text-regular;
}

.resume-status {
  margin-left: $margin-small;
}

.status-badge {
  padding: calc($padding-mini / 2) $padding-small;
  border-radius: $border-radius-small;
  font-size: $font-size-extra-small;
  
  &.draft {
    background: $warning-light;
    color: $warning-color;
  }
  
  &.published {
    background: color.adjust($success-color, $lightness:  40%);
    color: $success-color;
  }
  
  &.archived {
    background: $background-color;
    color: $text-secondary;
  }
}

.status-text {
  font-weight: $font-weight-medium;
}

.resume-item-content {
  margin-bottom: $margin-small;
}

.resume-meta {
  display: flex;
  gap: $margin-base;
  margin-bottom: $margin-small;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: calc($margin-mini / 2);
}

.meta-icon {
  font-size: $font-size-small;
  color: $text-secondary;
}

.meta-text {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.resume-skills {
  display: flex;
  flex-wrap: wrap;
  gap: calc($margin-mini / 2);
}

.skill-tag {
  background: $background-color;
  border-radius: $border-radius-small;
  padding: calc($padding-mini / 2) $padding-mini;
  font-size: $font-size-extra-small;
  color: $text-regular;
}

.more-skills {
  font-size: $font-size-extra-small;
  color: $text-placeholder;
}

.resume-item-actions {
  display: flex;
  gap: $margin-small;
  padding-top: $margin-small;
  border-top: 1rpx solid $border-color-extra-light;
}

.btn-action {
  flex: 1;
  background: $background-color;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius-small;
  padding: calc($padding-mini / 2);
  font-size: $font-size-small;
  color: $text-regular;
  
  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }
}

.action-text {
  font-size: $font-size-extra-small;
}

/* 网格视图 */
.grid-view {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-base;
}

.resume-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
  transition: all $transition-fast;
  
  &:active {
    background: color.adjust($background-color-white, $lightness:  -2%);
    box-shadow: $box-shadow-dark;
  }
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: $margin-small;
}

.card-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: $border-radius-round;
  background: linear-gradient(135deg, $primary-color, $secondary-color);
  @extend .flex-center;
  margin-right: $margin-small;
}

.card-title {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: calc($margin-mini / 2);
}

.card-name {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
  @extend .text-ellipsis;
}

.card-job {
  font-size: $font-size-extra-small;
  color: $text-regular;
  @extend .text-ellipsis;
}

.card-status {
  margin-bottom: $margin-small;
}

.card-skills {
  display: flex;
  flex-direction: column;
  gap: calc($margin-mini / 2);
  margin-bottom: $margin-small;
}

.skill-chip {
  background: $background-color;
  border-radius: $border-radius-small;
  padding: calc($padding-mini / 2) $padding-mini;
  font-size: $font-size-extra-small;
  color: $text-regular;
  @extend .text-ellipsis;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $margin-small;
  border-top: 1rpx solid $border-color-extra-light;
}

.update-time {
  font-size: $font-size-extra-small;
  color: $text-placeholder;
}

.card-actions {
  display: flex;
  gap: $margin-mini;
}

.card-action {
  font-size: $font-size-small;
  color: $text-secondary;
  cursor: pointer;
  padding: calc($padding-mini / 2);
}

/* 加载更多 */
.load-more {
  @extend .flex-center;
  padding: $padding-base;
  margin-top: $margin-base;
}

.load-more-text {
  font-size: $font-size-base;
  color: $primary-color;
  cursor: pointer;
  padding: $padding-small $padding-base;
  border: 2rpx solid $primary-color;
  border-radius: $border-radius;
}

/* 悬浮按钮 */
.floating-action {
  position: fixed;
  bottom: calc($tabbar-height + 40rpx);
  right: $padding-base;
  z-index: $z-index-dropdown;
}

.btn-fab {
  width: 100rpx;
  height: 100rpx;
  border-radius: $border-radius-round;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  box-shadow: $box-shadow-dark;
  @extend .flex-center;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
}

.fab-text {
  color: $background-color-white;
  font-size: $font-size-extra-large;
  line-height: 1;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .grid-view {
    grid-template-columns: 1fr;
  }
  
  .sort-tabs {
    gap: $margin-base;
  }
}
</style>