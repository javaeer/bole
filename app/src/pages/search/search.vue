<template>
  <view class="search-page">
    <!-- 顶部搜索栏 -->
    <view class="search-header">
      <!-- 搜索框 -->
      <view class="search-box">
        <view class="search-icon">🔍</view>
        <input
          class="search-input"
          :value="searchText"
          placeholder="搜索简历模板、职位、技能..."
          placeholder-class="placeholder"
          @input="handleInput"
          @focus="handleFocus"
          @blur="handleBlur"
          confirm-type="search"
          @confirm="handleSearch"
        />
        <view v-if="searchText" class="clear-btn" @click="handleClear">
          <text class="clear-icon">×</text>
        </view>
      </view>

      <!-- 搜索按钮 -->
      <button class="search-btn" @click="handleSearch" :disabled="!searchText">
        搜索
      </button>
    </view>

    <!-- 搜索内容区域 -->
    <scroll-view
      class="search-content"
      scroll-y
      :refresher-enabled="false"
      refresher-background="transparent"
    >
      <!-- 搜索历史 -->
      <view v-if="!searchText && searchHistory.length > 0" class="section">
        <view class="section-header">
          <text class="section-title">搜索历史</text>
          <view class="clear-history" @click="handleClearHistory">
            <text class="clear-history-icon">🗑️</text>
            <text class="clear-history-text">清空</text>
          </view>
        </view>
        <view class="history-list">
          <view
            v-for="(item, index) in searchHistory"
            :key="index"
            class="history-item"
            @click="handleHistoryClick(item)"
          >
            <text class="history-text">{{ item }}</text>
            <view class="remove-history" @click.stop="handleRemoveHistory(index)">
              <text class="remove-icon">×</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view v-if="!searchText" class="section">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="hot-tags">
          <view
            v-for="(tag, index) in hotTags"
            :key="index"
            class="tag-item"
            :style="getTagStyle(index)"
            @click="handleTagClick(tag.text)"
          >
            <text class="tag-rank">{{ index + 1 }}</text>
            <text class="tag-text">{{ tag.text }}</text>
            <text v-if="tag.hot" class="hot-badge">🔥</text>
          </view>
        </view>
      </view>

      <!-- 搜索建议 -->
      <view v-if="searchText && suggestions.length > 0" class="section">
        <view class="section-header">
          <text class="section-title">搜索建议</text>
        </view>
        <view class="suggestion-list">
          <view
            v-for="(item, index) in suggestions"
            :key="index"
            class="suggestion-item"
            @click="handleSuggestionClick(item)"
          >
            <view class="suggestion-left">
              <view class="suggestion-icon">🔍</view>
              <text class="suggestion-text">{{ item }}</text>
            </view>
            <view class="suggestion-arrow">→</view>
          </view>
        </view>
      </view>

      <!-- 搜索结果 -->
      <view v-if="searchText && searchResults.length > 0" class="section">
        <view class="results-header">
          <text class="results-title">搜索结果</text>
          <text class="results-count">共{{ searchResults.length }}条</text>
        </view>
        <view class="result-list">
          <view
            v-for="(item, index) in searchResults"
            :key="index"
            class="result-item"
            @click="handleResultClick(item)"
          >
            <view class="result-main">
              <text class="result-title">{{ item.title }}</text>
              <text class="result-desc">{{ item.description }}</text>
              <view class="result-tags">
                <view
                  v-for="(tag, tagIndex) in item.tags"
                  :key="tagIndex"
                  class="result-tag"
                >
                  {{ tag }}
                </view>
              </view>
            </view>
            <view class="result-meta">
              <text class="result-time">{{ item.time }}</text>
              <text class="result-type">{{ item.type }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="searchText && searchResults.length === 0" class="empty-state">
        <view class="empty-icon">🔍</view>
        <text class="empty-title">未找到相关结果</text>
        <text class="empty-tip">尝试更换关键词或筛选条件</text>
      </view>

      <!-- 搜索提示 -->
      <view v-if="!searchText" class="search-tips">
        <view class="tip-header">
          <text class="tip-title">搜索技巧</text>
        </view>
        <view class="tip-list">
          <view class="tip-item">
            <text class="tip-icon">✨</text>
            <text class="tip-text">使用空格分隔多个关键词</text>
          </view>
          <view class="tip-item">
            <text class="tip-icon">✨</text>
            <text class="tip-text">尝试使用更具体的关键词</text>
          </view>
          <view class="tip-item">
            <text class="tip-icon">✨</text>
            <text class="tip-text">使用"+"连接必须包含的词</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 搜索筛选浮层 -->
    <view v-if="showFilter" class="filter-overlay" @click="showFilter = false">
      <view class="filter-panel" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <view class="filter-close" @click="showFilter = false">
            <text class="close-icon">×</text>
          </view>
        </view>
        <view class="filter-content">
          <!-- 筛选内容 -->
        </view>
        <view class="filter-actions">
          <button class="btn-reset" @click="handleResetFilter">重置</button>
          <button class="btn-confirm" @click="handleConfirmFilter">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'

// 搜索状态
const searchText = ref('')
const isFocused = ref(false)
const showFilter = ref(false)

// 搜索历史
const searchHistory = ref<string[]>([
  '前端开发',
  'Java工程师',
  '产品经理简历',
  '设计师作品集'
])

// 热门标签
const hotTags = ref([
  { text: 'Java开发', hot: true },
  { text: '前端工程师', hot: true },
  { text: '产品经理', hot: false },
  { text: 'UI设计师', hot: true },
  { text: 'Python', hot: false },
  { text: '测试工程师', hot: false },
  { text: '运营简历', hot: false },
  { text: '项目经理', hot: true }
])

// 搜索建议
const suggestions = computed(() => {
  if (!searchText.value) return []
  const sug = [
    `${searchText.value}简历模板`,
    `${searchText.value}岗位要求`,
    `${searchText.value}技能要求`,
    `${searchText.value}面试经验`
  ]
  return sug.filter(s => s.includes(searchText.value))
})

// 搜索结果
const searchResults = computed(() => {
  if (!searchText.value) return []
  // 模拟搜索结果
  return [
    {
      id: 1,
      title: '高级前端开发工程师简历模板',
      description: '包含Vue.js、React、TypeScript等现代前端技术栈',
      tags: ['前端', 'Vue', 'React'],
      time: '2023-11-15',
      type: '简历模板'
    },
    {
      id: 2,
      title: 'Java后端开发最佳实践',
      description: 'Spring Boot、微服务架构、数据库设计',
      tags: ['Java', '后端', 'Spring'],
      time: '2023-11-10',
      type: '技术文章'
    }
  ]
})

// 获取标签样式
const getTagStyle = (index: number) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff', '#5f27cd']
  return {
    backgroundColor: `${colors[index % colors.length]}15`,
    color: colors[index % colors.length]
  }
}

// 事件处理
const handleBack = () => {
  uni.navigateBack()
}

const handleInput = (e: any) => {
  searchText.value = e.detail.value
}

const handleFocus = () => {
  isFocused.value = true
}

const handleBlur = () => {
  setTimeout(() => {
    isFocused.value = false
  }, 200)
}

const handleClear = () => {
  searchText.value = ''
}

const handleSearch = () => {
  if (!searchText.value.trim()) return

  // 添加到搜索历史
  if (!searchHistory.value.includes(searchText.value)) {
    searchHistory.value.unshift(searchText.value)
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
  }

  uni.showLoading({
    title: '搜索中...'
  })

  // 模拟搜索请求
  setTimeout(() => {
    uni.hideLoading()
  }, 1000)
}

const handleHistoryClick = (text: string) => {
  searchText.value = text
  handleSearch()
}

const handleRemoveHistory = (index: number) => {
  searchHistory.value.splice(index, 1)
}

const handleClearHistory = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        searchHistory.value = []
      }
    }
  })
}

const handleTagClick = (text: string) => {
  searchText.value = text
  handleSearch()
}

const handleSuggestionClick = (text: string) => {
  searchText.value = text
  handleSearch()
}

const handleResultClick = (item: any) => {
  uni.navigateTo({
    url: `/pages/detail/${item.id}`
  })
}

const handleResetFilter = () => {
  // 重置筛选逻辑
  showFilter.value = false
}

const handleConfirmFilter = () => {
  // 确认筛选逻辑
  showFilter.value = false
  handleSearch()
}

onMounted(() => {
  console.log('搜索页面加载完成')
})
</script>

<style scoped lang="scss">
.search-page {
  background-color: $background-color;
  min-height: 100vh;
}

/* 顶部搜索栏 */
.search-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding: var(--status-bar-height, 0) $padding-base $padding-base;
  display: flex;
  align-items: center;
  gap: $margin-small;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-box {
  flex: 1;
  background: $background-color-white;
  border-radius: $border-radius;
  padding: 0 $padding-small;
  display: flex;
  align-items: center;
  height: $input-height;
  position: relative;
  box-shadow: $box-shadow-light;

  .search-icon {
    margin-right: $margin-mini;
    font-size: $font-size-base;
    color: $text-placeholder;
    flex-shrink: 0;
  }

  .search-input {
    flex: 1;
    height: 100%;
    font-size: $font-size-base;
    color: $text-primary;
    background: transparent;
    border: none;
    outline: none;

    &::placeholder {
      color: $text-placeholder;
    }
  }

  .clear-btn {
    width: 32rpx;
    height: 32rpx;
    @extend .flex-center;
    background: $border-color-light;
    border-radius: $border-radius-round;
    flex-shrink: 0;
    margin-left: $margin-mini;

    .clear-icon {
      font-size: $font-size-medium;
      color: $text-placeholder;
      font-weight: bold;
    }
  }
}

.search-btn {
  height: $input-height;
  min-width: 80rpx;
  background: rgba($background-color-white, 0.2);
  color: $background-color-white;
  border: 2rpx solid $background-color-white;
  border-radius: $border-radius;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  flex-shrink: 0;

  &:disabled {
    opacity: 0.5;
  }
}

/* 内容区域 */
.search-content {
  height: calc(100vh - var(--status-bar-height, 0) - 140rpx);
  padding: $padding-base 0;
}

/* 通用区块样式 */
.section {
  background: $background-color-white;
  margin: 0 $margin-base $margin-base;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;

  .section-title {
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

/* 搜索历史 */
.clear-history {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: $padding-mini;

  .clear-history-icon {
    font-size: $font-size-small;
    margin-right: 4rpx;
  }

  .clear-history-text {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-small $padding-base;
  background: $background-color;
  border-radius: $border-radius-small;
  border-left: 4rpx solid $primary-color;

  .history-text {
    font-size: $font-size-base;
    color: $text-regular;
    @extend .text-ellipsis;
    flex: 1;
  }

  .remove-history {
    width: 32rpx;
    height: 32rpx;
    @extend .flex-center;
    flex-shrink: 0;
    margin-left: $margin-mini;

    .remove-icon {
      font-size: $font-size-large;
      color: $text-placeholder;
      font-weight: bold;
    }
  }
}

/* 热门标签 */
.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-small;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: $padding-mini $padding-base;
  border-radius: $border-radius * 2;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  gap: $margin-mini;

  .tag-rank {
    font-size: $font-size-extra-small;
    font-weight: $font-weight-bold;
  }

  .tag-text {
    flex: 1;
    @extend .text-ellipsis;
  }

  .hot-badge {
    font-size: $font-size-small;
  }
}

/* 搜索建议 */
.suggestion-list {
  display: flex;
  flex-direction: column;
}

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }

  .suggestion-left {
    display: flex;
    align-items: center;
    gap: $margin-mini;
    flex: 1;

    .suggestion-icon {
      font-size: $font-size-small;
      color: $text-placeholder;
      flex-shrink: 0;
    }

    .suggestion-text {
      font-size: $font-size-base;
      color: $text-regular;
      @extend .text-ellipsis;
      flex: 1;
    }
  }

  .suggestion-arrow {
    font-size: $font-size-medium;
    color: $text-placeholder;
    margin-left: $margin-mini;
    flex-shrink: 0;
  }
}

/* 搜索结果 */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-light;

  .results-title {
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .results-count {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.result-item {
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
  border-left: 4rpx solid $primary-color;

  .result-main {
    margin-bottom: $margin-small;

    .result-title {
      display: block;
      font-size: $font-size-base;
      font-weight: $font-weight-bold;
      color: $text-primary;
      margin-bottom: $margin-mini;
      @extend .text-ellipsis;
    }

    .result-desc {
      display: block;
      font-size: $font-size-small;
      color: $text-secondary;
      line-height: 1.5;
      margin-bottom: $margin-small;
    }

    .result-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $margin-mini;

      .result-tag {
        padding: 4rpx 8rpx;
        background: rgba($primary-color, 0.1);
        color: $primary-color;
        font-size: $font-size-extra-small;
        border-radius: $border-radius-small;
      }
    }
  }

  .result-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: $font-size-extra-small;
    color: $text-placeholder;
  }
}

/* 空状态 */
.empty-state {
  @extend .flex-center;
  flex-direction: column;
  padding: $padding-base * 2 $padding-base;
  text-align: center;

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: $margin-base;
    opacity: 0.3;
  }

  .empty-title {
    font-size: $font-size-medium;
    color: $text-secondary;
    margin-bottom: $margin-mini;
  }

  .empty-tip {
    font-size: $font-size-small;
    color: $text-placeholder;
  }
}

/* 搜索提示 */
.search-tips {
  margin: 0 $margin-base;

  .tip-header {
    margin-bottom: $margin-base;

    .tip-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }
  }

  .tip-list {
    display: flex;
    flex-direction: column;
    gap: $margin-base;
  }

  .tip-item {
    display: flex;
    align-items: center;
    gap: $margin-small;

    .tip-icon {
      font-size: $font-size-base;
      color: $primary-color;
      flex-shrink: 0;
    }

    .tip-text {
      font-size: $font-size-small;
      color: $text-secondary;
      flex: 1;
    }
  }
}

/* 筛选浮层 */
.filter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: $z-index-modal;
  @extend .flex-center;
}

.filter-panel {
  background: $background-color-white;
  border-radius: $border-radius;
  width: 80vw;
  max-height: 70vh;
  display: flex;
  flex-direction: column;

  .filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $padding-base;
    border-bottom: 1rpx solid $border-color-light;

    .filter-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .filter-close {
      width: 40rpx;
      height: 40rpx;
      @extend .flex-center;

      .close-icon {
        font-size: $font-size-large;
        color: $text-placeholder;
        font-weight: bold;
      }
    }
  }

  .filter-content {
    flex: 1;
    padding: $padding-base;
    overflow-y: auto;
  }

  .filter-actions {
    display: flex;
    gap: $margin-small;
    padding: $padding-base;
    border-top: 1rpx solid $border-color-light;

    .btn-reset,
    .btn-confirm {
      flex: 1;
      height: $button-height;
      border-radius: $border-radius;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
    }

    .btn-reset {
      background: $background-color;
      color: $text-regular;
      border: 2rpx solid $border-color;
    }

    .btn-confirm {
      background: $primary-color;
      color: $background-color-white;
      border: none;
    }
  }
}

/* 适配安全区域 */
:global(page) {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>