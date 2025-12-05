<template>
  <view class="guide-container">
    <!-- 头部搜索区域 -->
    <view class="guide-header">
      <view class="header-content">
        <text class="header-title">求职指南</text>
        <text class="header-subtitle">助你顺利拿到理想offer</text>
      </view>
      
      <view class="search-section">
        <view class="search-wrapper">
          <text class="search-icon">🔍</text>
          <input 
            class="search-input"
            placeholder="搜索求职问题..."
            placeholder-class="placeholder"
            v-model="searchKeyword"
            @confirm="handleSearch"
          />
          <view v-if="searchKeyword" class="search-clear" @click="searchKeyword = ''">
            <text>✕</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 分类筛选 -->
    <scroll-view class="category-scroll" scroll-x="true" show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(category, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: activeCategory === index }"
          @click="switchCategory(index)"
        >
          <text class="category-text">{{ category.name }}</text>
          <view v-if="activeCategory === index" class="category-indicator"></view>
        </view>
      </view>
    </scroll-view>

    <!-- 主要内容区域 -->
    <scroll-view class="main-content" scroll-y="true" @scrolltolower="loadMore">
      <!-- 热门文章 -->
      <view v-if="activeCategory === 0" class="section">
        <view class="section-header">
          <text class="section-title">热门阅读</text>
          <text class="section-more" @click="viewAllHot">查看更多</text>
        </view>
        
        <view class="hot-articles">
          <view 
            v-for="article in hotArticles" 
            :key="article.id"
            class="hot-article-card"
            @click="viewArticleDetail(article.id)"
          >
            <image 
              class="article-image" 
              :src="article.image" 
              mode="aspectFill"
            />
            <view class="article-info">
              <view class="article-tags">
                <text v-for="(tag, idx) in article.tags.slice(0, 2)" :key="idx" class="article-tag">
                  {{ tag }}
                </text>
              </view>
              <text class="article-title">{{ article.title }}</text>
              <view class="article-meta">
                <text class="meta-item">{{ article.readCount }} 阅读</text>
                <text class="meta-divider">•</text>
                <text class="meta-item">{{ article.time }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 分类内容 -->
      <view class="section" v-for="category in filteredCategories" :key="category.id">
        <view class="section-header">
          <text class="section-title">{{ category.name }}</text>
          <text class="section-more" @click="viewCategoryDetail(category.id)">全部</text>
        </view>
        
        <view class="category-content">
          <view 
            v-for="item in category.items" 
            :key="item.id"
            class="content-item"
            @click="viewContentDetail(item.id, item.type)"
          >
            <view class="item-main">
              <text class="item-title">{{ item.title }}</text>
              <text class="item-desc">{{ item.description }}</text>
              <view class="item-meta">
                <text class="meta-item">{{ item.readCount }} 人看过</text>
                <text class="meta-divider">•</text>
                <text class="meta-item">{{ item.duration }}</text>
                <text v-if="item.type === 'video'" class="item-type">视频</text>
                <text v-if="item.type === 'article'" class="item-type">文章</text>
              </view>
            </view>
            <image 
              v-if="item.image" 
              class="item-image" 
              :src="item.image" 
              mode="aspectFill"
            />
            <text v-else class="item-icon">{{ getTypeIcon(item.type) }}</text>
          </view>
        </view>
      </view>

      <!-- 实用工具 -->
      <view class="section">
        <view class="section-header">
          <text class="section-title">实用工具</text>
        </view>
        
        <view class="tool-grid">
          <view 
            v-for="tool in tools" 
            :key="tool.id"
            class="tool-item"
            @click="useTool(tool.id)"
          >
            <view class="tool-icon">
              <text>{{ tool.icon }}</text>
            </view>
            <text class="tool-name">{{ tool.name }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="loading" class="loading-section">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
      
      <view v-if="noMoreData" class="no-more">
        <text>已经到底了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

// 搜索关键词
const searchKeyword = ref('')

// 当前激活的分类
const activeCategory = ref(0)

// 分类数据
const categories = ref([
  { id: 0, name: '热门' },
  { id: 1, name: '简历制作' },
  { id: 2, name: '面试技巧' },
  { id: 3, name: '薪资谈判' },
  { id: 4, name: '职场发展' },
  { id: 5, name: '行业分析' },
  { id: 6, name: '避坑指南' }
])

// 热门文章
const hotArticles = ref([
  {
    id: 1,
    title: '面试时被问"你有什么缺点"该如何回答？',
    image: '/static/guide/interview.jpg',
    tags: ['面试', '技巧', '常见问题'],
    readCount: 12345,
    time: '2小时前'
  },
  {
    id: 2,
    title: '2024年程序员求职市场趋势分析',
    image: '/static/guide/market.jpg',
    tags: ['行业', '趋势', '程序员'],
    readCount: 8900,
    time: '1天前'
  },
  {
    id: 3,
    title: '简历如何写才能通过HR初筛？',
    image: '/static/guide/resume.jpg',
    tags: ['简历', '技巧', 'HR'],
    readCount: 15600,
    time: '3天前'
  }
])

// 分类内容
const categoryContent = ref([
  {
    id: 1,
    name: '简历制作',
    items: [
      {
        id: 101,
        title: '没有工作经验，简历怎么写？',
        description: '应届生和转行人士必看的简历撰写指南',
        type: 'article',
        readCount: 5400,
        duration: '5分钟阅读',
        image: '/static/guide/resume1.jpg'
      },
      {
        id: 102,
        title: '简历模板选择指南',
        description: '不同行业适用的简历模板分析',
        type: 'article',
        readCount: 3200,
        duration: '3分钟阅读',
        image: ''
      }
    ]
  },
  {
    id: 2,
    name: '面试技巧',
    items: [
      {
        id: 201,
        title: '线上面试注意事项',
        description: '视频面试的准备工作与技巧',
        type: 'video',
        readCount: 7800,
        duration: '8分钟',
        image: '/static/guide/interview1.jpg'
      },
      {
        id: 202,
        title: '行为面试问题回答框架',
        description: 'STAR法则在面试中的应用',
        type: 'article',
        readCount: 4200,
        duration: '6分钟阅读',
        image: ''
      }
    ]
  }
])

// 实用工具
const tools = ref([
  { id: 1, name: '简历优化', icon: '📝' },
  { id: 2, name: '薪资查询', icon: '💰' },
  { id: 3, name: '面试模拟', icon: '🎤' },
  { id: 4, name: '公司查询', icon: '🏢' },
  { id: 5, name: '合同审查', icon: '📄' },
  { id: 6, name: '职级对标', icon: '📊' }
])

// 加载状态
const loading = ref(false)
const noMoreData = ref(false)
const currentPage = ref(1)

// 计算属性
const filteredCategories = computed(() => {
  if (activeCategory.value === 0) {
    return categoryContent.value
  }
  return categoryContent.value.filter(cat => cat.id === activeCategory.value)
})

// 方法
const switchCategory = (index: number) => {
  activeCategory.value = index
  currentPage.value = 1
  noMoreData.value = false
  // 模拟数据加载
  loadCategoryData()
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  
  uni.showLoading({ title: '搜索中...' })
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({
      title: `搜索: ${searchKeyword.value}`,
      icon: 'none'
    })
    // 实际项目中这里应该调用搜索API
  }, 500)
}

const viewArticleDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/guide/article?id=${id}`
  })
}

const viewContentDetail = (id: number, type: string) => {
  if (type === 'video') {
    uni.navigateTo({
      url: `/pages/guide/video?id=${id}`
    })
  } else {
    uni.navigateTo({
      url: `/pages/guide/article?id=${id}`
    })
  }
}

const viewCategoryDetail = (categoryId: number) => {
  uni.navigateTo({
    url: `/pages/guide/category?id=${categoryId}`
  })
}

const viewAllHot = () => {
  uni.navigateTo({
    url: '/pages/guide/hot'
  })
}

const useTool = (toolId: number) => {
  const toolNames: Record<number, string> = {
    1: '简历优化',
    2: '薪资查询',
    3: '面试模拟',
    4: '公司查询',
    5: '合同审查',
    6: '职级对标'
  }
  
  uni.showToast({
    title: `打开${toolNames[toolId]}工具`,
    icon: 'none'
  })
}

const getTypeIcon = (type: string) => {
  return type === 'video' ? '🎬' : '📖'
}

const loadMore = () => {
  if (loading.value || noMoreData.value) return
  
  loading.value = true
  // 模拟加载更多数据
  setTimeout(() => {
    currentPage.value++
    if (currentPage.value > 3) {
      noMoreData.value = true
    }
    loading.value = false
  }, 1000)
}

const loadCategoryData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

onMounted(() => {
  // 页面加载时获取初始数据
  loadCategoryData()
})
</script>

<style scoped lang="scss">
.guide-container {
  background-color: $background-color;
  min-height: 100vh;
}

/* 头部区域 */
.guide-header {
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness:   15%) 100%);
  padding: $padding-base;
  color: $background-color-white;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
}

.header-content {
  margin-bottom: $margin-base;
}

.header-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  margin-bottom: $margin-mini;
}

.header-subtitle {
  display: block;
  font-size: $font-size-small;
  opacity: 0.9;
}

/* 搜索区域 */
.search-section {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-mini $padding-base;
}

.search-wrapper {
  display: flex;
  align-items: center;
  position: relative;
}

.search-icon {
  font-size: $font-size-medium;
  color: $text-placeholder;
  margin-right: $margin-mini;
}

.search-input {
  flex: 1;
  height: 60rpx;
  font-size: $font-size-base;
  color: $text-primary;
}

.search-clear {
  width: 40rpx;
  height: 40rpx;
  border-radius: $border-radius-round;
  background: $border-color-light;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-placeholder;
  font-size: $font-size-small;
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

/* 分类筛选 */
.category-scroll {
  white-space: nowrap;
  padding: $padding-base $padding-base 0;
  background: $background-color-white;
}

.category-list {
  display: inline-flex;
  gap: $margin-base * 2;
}

.category-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-mini 0;
  position: relative;
  
  &.active {
    .category-text {
      color: $primary-color;
      font-weight: $font-weight-bold;
    }
  }
}

.category-text {
  font-size: $font-size-base;
  color: $text-regular;
  transition: color $transition-fast;
}

.category-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: $primary-color;
  border-radius: 2rpx;
}

/* 主要内容 */
.main-content {
  height: calc(100vh - 340rpx);
  padding: $padding-base;
}

/* 通用区块样式 */
.section {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $box-shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.section-more {
  font-size: $font-size-small;
  color: $primary-color;
}

/* 热门文章 */
.hot-articles {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.hot-article-card {
  display: flex;
  background: $background-color;
  border-radius: $border-radius;
  overflow: hidden;
  transition: transform $transition-fast;
  
  &:active {
    transform: scale(0.99);
    background: color.adjust($background-color, $lightness:  - 2%);
  }
}

.article-image {
  width: 200rpx;
  height: 150rpx;
  flex-shrink: 0;
}

.article-info {
  flex: 1;
  padding: $padding-mini $padding-base;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.article-tags {
  display: flex;
  gap: $margin-mini;
  margin-bottom: $margin-mini;
}

.article-tag {
  font-size: $font-size-extra-small;
  color: $primary-color;
  background: $primary-light;
  padding: 2rpx 10rpx;
  border-radius: $border-radius-small;
}

.article-title {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-mini;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: $margin-mini;
}

.meta-item {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.meta-divider {
  color: $text-placeholder;
  font-size: $font-size-extra-small;
}

/* 分类内容 */
.category-content {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.content-item {
  display: flex;
  padding: $padding-base 0;
  border-bottom: 1rpx solid $border-color-extra-light;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    background: $background-color;
  }
}

.item-main {
  flex: 1;
  margin-right: $margin-base;
}

.item-title {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-mini;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.item-desc {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  margin-bottom: $margin-mini;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: $margin-mini;
}

.item-type {
  margin-left: auto;
  font-size: $font-size-extra-small;
  color: $primary-color;
  background: $primary-light;
  padding: 2rpx 8rpx;
  border-radius: $border-radius-small;
}

.item-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: $border-radius-small;
  flex-shrink: 0;
}

.item-icon {
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-extra-large;
  background: $primary-light;
  border-radius: $border-radius-small;
  color: $primary-color;
}

/* 实用工具 */
.tool-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $margin-base;
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base 0;
  
  &:active {
    .tool-icon {
      transform: scale(0.95);
    }
  }
}

.tool-icon {
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, $primary-color, color.adjust($primary-color, $lightness:   15%));
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $margin-mini;
  transition: transform $transition-fast;
  
  text {
    font-size: $font-size-extra-large;
    color: $background-color-white;
  }
}

.tool-name {
  font-size: $font-size-small;
  color: $text-regular;
}

/* 加载状态 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base;
  color: $text-secondary;
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid $border-color;
  border-top-color: $primary-color;
  border-radius: 50%;
  margin-bottom: $margin-mini;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-size-small;
}

.no-more {
  text-align: center;
  padding: $padding-base;
  color: $text-placeholder;
  font-size: $font-size-small;
}
</style>