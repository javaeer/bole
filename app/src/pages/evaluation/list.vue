<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-container card-container">
      <view class="search-box">
        <view class="search-icon">🔍</view>
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="搜索评价内容或关键词"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <view class="clear-icon">×</view>
        </button>
      </view>

      <!-- 排序选项 -->
      <view class="sort-options">
        <view
          v-for="option in sortOptions"
          :key="option.value"
          :class="['sort-item', { 'active': sortBy === option.value }]"
          @click="changeSort(option.value)"
        >
          <text>{{ option.label }}</text>
          <view
            v-if="sortBy === option.value"
            class="sort-icon"
            :style="{ color: sortOrder === 'asc' ? successColor : dangerColor }"
          >
            {{ sortOrder === 'asc' ? '↑' : '↓' }}
          </view>
        </view>
      </view>
    </view>

    <!-- 列表内容 -->
    <scroll-view
      class="list-scroll"
      scroll-y
      @scrolltolower="loadMore"
      :show-scrollbar="false"
    >
      <!-- 空状态 -->
      <view v-if="loading && listData.length === 0" class="empty-state">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 列表为空 -->
      <view v-else-if="!loading && listData.length === 0" class="empty-state">
        <view class="empty-icon">📝</view>
        <text class="empty-text">暂无自我评价</text>
        <button class="btn btn-primary" @click="addNewEvaluation">添加自我评价</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="(item, index) in listData"
          :key="item.id"
          class="evaluation-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 序号标签 -->
          <view class="item-index">
            <text class="index-number">{{ index + 1 }}</text>
          </view>

          <!-- 主要内容 -->
          <view class="item-content">
            <view class="content-wrapper">
              <text class="content-text text-multi-truncate">
                {{ item.content }}
              </text>

              <!-- 关键词列表 -->
              <view class="highlights-section" v-if="item.highlights && item.highlights.length > 0">
                <view class="highlights-label">
                  <view class="tag-icon">🏷</view>
                  <text class="label-text">关键词</text>
                  <text class="count-badge">{{ item.highlights.length }}</text>
                </view>
                <view class="highlights-list">
                  <view
                    v-for="(highlight, highlightIndex) in item.highlights.slice(0, 5)"
                    :key="highlightIndex"
                    class="highlight-tag"
                    @click.stop="searchByHighlight(highlight)"
                  >
                    <text>{{ highlight }}</text>
                  </view>
                  <view
                    v-if="item.highlights.length > 5"
                    class="more-highlights"
                    @click.stop="showAllHighlights(item.highlights)"
                  >
                    <text>+{{ item.highlights.length - 5 }}</text>
                  </view>
                </view>
              </view>

              <!-- 内容统计 -->
              <view class="content-stats">
                <view class="stat-item">
                  <view class="font-size-icon">A</view>
                  <text class="stat-text">{{ item.content.length }}字</text>
                </view>
                <view v-if="item.highlights && item.highlights.length > 0" class="stat-item">
                  <view class="tag-count-icon">🏷</view>
                  <text class="stat-text">{{ item.highlights.length }}个关键词</text>
                </view>
              </view>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="item-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editEvaluation(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="item-footer">
            <text class="time-text">创建：{{ formatDate(item.createdAt) }}</text>
            <text class="time-text">更新：{{ formatDate(item.updatedAt) }}</text>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more">
          <view v-if="loading" class="loading-more">
            <view class="loading-spinner-small"></view>
            <text>加载中...</text>
          </view>
          <view v-else class="load-more-btn" @click="loadMore">
            上拉加载更多
          </view>
        </view>
        <view v-else-if="listData.length > 0" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </scroll-view>

    <!-- 添加按钮 -->
    <button class="add-btn" @click="addNewEvaluation">
      <view class="icon-plus">+</view>
    </button>

    <!-- 关键词全量弹窗 -->
    <view v-if="showHighlightsModal" class="highlights-modal" @click="closeHighlightsModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">全部关键词</text>
          <button class="modal-close-btn" @click="closeHighlightsModal">×</button>
        </view>
        <view class="modal-body">
          <view class="all-highlights-list">
            <view
              v-for="(highlight, index) in currentHighlights"
              :key="index"
              class="highlight-tag-large"
              @click.stop="searchByHighlight(highlight)"
            >
              <text>{{ highlight }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import type { SelfEvaluationResult } from "@/types/self-evaluation";
import SelfEvaluationAPI from "@/api/self-evaluation";

// 响应式数据
const searchKeywords = ref('')
const sortBy = ref<'createdAt' | 'updatedAt'>('createdAt')
const sortOrder = ref<'asc' | 'desc'>('desc')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)
const listData = ref<SelfEvaluationResult[]>([])

// 关键词弹窗相关
const showHighlightsModal = ref(false)
const currentHighlights = ref<string[]>([])

// 排序选项
const sortOptions = [
  { label: '创建时间', value: 'createdAt' },
  { label: '更新时间', value: 'updatedAt' }
]

// 颜色变量
const successColor = '#67c23a'
const dangerColor = '#f56c6c'

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  } catch {
    return dateStr;
  }
};

// 显示全部关键词
const showAllHighlights = (highlights: string[]) => {
  currentHighlights.value = highlights;
  showHighlightsModal.value = true;
};

// 关闭关键词弹窗
const closeHighlightsModal = () => {
  showHighlightsModal.value = false;
  currentHighlights.value = [];
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadData(true)
}

const clearSearch = () => {
  searchKeywords.value = ''
  currentPage.value = 1
  loadData(true)
}

// 按关键词搜索
const searchByHighlight = (highlight: string) => {
  searchKeywords.value = highlight
  currentPage.value = 1
  loadData(true)

  // 关闭弹窗（如果打开）
  closeHighlightsModal();
}

// 排序处理
const changeSort = (field: any) => {
  if (sortBy.value === field) {
    // 切换排序顺序
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    // 切换到新字段，默认降序
    sortBy.value = field
    sortOrder.value = 'desc'
  }

  currentPage.value = 1
  loadData(true)
}

// 加载数据
const loadData = async (reset = false) => {
  if (loading.value) return;

  loading.value = true;

  if (reset) {
    currentPage.value = 1;
    hasMore.value = true;
    listData.value = [];
  }

  try {
    // 构建查询参数
    const pageParam = {
      page: currentPage.value,
      size: pageSize.value,
    };

    const query: any = {};

    // 添加搜索条件
    if (searchKeywords.value) {
      query.highlight = searchKeywords.value;
    }

    // 添加排序条件
    if (sortBy.value && sortOrder.value) {
      query.orderBy = sortBy.value;
      query.orderDirection = sortOrder.value === 'asc' ? 'ASC' : 'DESC';
    }

    // 调用API
    const response = await SelfEvaluationAPI.page(pageParam, query);

    if (response) {
      const { records = [], total = 0 } = response;

      // 确保highlights是数组格式（如果是字符串就转换为数组）
      const processedRecords = records.map(record => ({
        ...record,
        highlights: Array.isArray(record.highlights)
          ? record.highlights
          : typeof record.highlights === 'string'
            ? record.highlights.split(',').map(k => k.trim()).filter(k => k)
            : []
      }));

      if (reset) {
        listData.value = processedRecords;
      } else {
        listData.value = [...listData.value, ...processedRecords];
      }

      // 更新是否有更多数据
      hasMore.value = listData.value.length < total;

      // 如果当前页有数据，且数据条数等于pageSize，说明可能还有下一页
      if (records.length === pageSize.value) {
        currentPage.value++;
      }
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return;
  loadData();
};

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/evaluation/evaluation?id=${id}`
  })
}

const editEvaluation = (id: number) => {
  uni.navigateTo({
    url: `/pages/evaluation/evaluation?id=${id}&edit=true`
  })
}

const addNewEvaluation = () => {
  uni.navigateTo({
    url: '/pages/evaluation/evaluation'
  })
}

// 生命周期
onMounted(() => {
  loadData(true)
})

onLoad((options) => {
  // 从详情页返回时刷新数据
  const refresh = options?.refresh === 'true'
  if (refresh) {
    loadData(true)
  }
})

onReachBottom(() => {
  loadMore()
})
</script>

<style lang="scss">
.page-container {
  min-height: 100vh;
  background-color: $background-color;
  padding-bottom: calc(env(safe-area-inset-bottom) + 100rpx);
}

.search-container {
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  border-radius: 0 0 $border-radius $border-radius;
  box-shadow: $box-shadow;
}

.search-box {
  display: flex;
  align-items: center;
  background: $background-color;
  border-radius: $border-radius;
  padding: 20rpx 24rpx;
  margin-bottom: $margin-small;
  border: 1rpx solid $border-color-light;

  .search-icon {
    font-size: 32rpx;
    color: $text-secondary;
  }

  .search-input {
    flex: 1;
    font-size: $font-size-base;
    color: $text-primary;
    margin-left: 16rpx;
    background: transparent;
  }

  .clear-btn {
    background: transparent;
    border: none;
    padding: 0;
    margin: 0;
    display: flex;
    align-items: center;
    justify-content: center;

    .clear-icon {
      font-size: 36rpx;
      color: $text-secondary;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

.sort-options {
  display: flex;
  gap: $margin-small;
  overflow-x: auto;

  .sort-item {
    padding: 12rpx 24rpx;
    background: $background-color;
    border-radius: $border-radius-small;
    font-size: $font-size-small;
    color: $text-secondary;
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: 8rpx;
    border: 1rpx solid $border-color-light;

    &.active {
      background: $primary-color-light;
      color: $primary-color;
      border-color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }

  .sort-icon {
    font-size: 20rpx;
  }
}

.list-scroll {
  height: calc(100vh - 200rpx);
  padding: $padding-small;
}

.evaluation-item {
  margin-bottom: $margin-base;
  position: relative;
  transition: all $transition-fast $ease-in-out;
  padding-left: 80rpx;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .item-index {
    position: absolute;
    left: 20rpx;
    top: 20rpx;
    width: 40rpx;
    height: 40rpx;
    background: $primary-color-light;
    border-radius: $border-radius-round;
    display: flex;
    align-items: center;
    justify-content: center;

    .index-number {
      font-size: $font-size-small;
      font-weight: $font-weight-bold;
      color: $primary-color;
    }
  }

  .item-content {
    .content-wrapper {
      .content-text {
        font-size: $font-size-base;
        color: $text-primary;
        line-height: 1.6;
        margin-bottom: $margin-small;
        display: block;
        -webkit-line-clamp: 3;
      }

      .highlights-section {
        margin-bottom: $margin-small;

        .highlights-label {
          display: flex;
          align-items: center;
          gap: 8rpx;
          margin-bottom: 12rpx;

          .tag-icon {
            font-size: 24rpx;
          }

          .label-text {
            font-size: $font-size-extra-small;
            color: $text-secondary;
            font-weight: $font-weight-medium;
          }

          .count-badge {
            background: $primary-color-light;
            color: $primary-color;
            font-size: $font-size-extra-small;
            padding: 2rpx 8rpx;
            border-radius: 10rpx;
            font-weight: $font-weight-medium;
          }
        }

        .highlights-list {
          display: flex;
          flex-wrap: wrap;
          gap: 8rpx;
          align-items: center;

          .highlight-tag {
            padding: 6rpx 12rpx;
            background: $primary-color-light;
            border-radius: $border-radius-round;
            font-size: $font-size-extra-small;
            color: $primary-color;
            border: 1rpx solid $primary-border;
            cursor: pointer;
            max-width: 150rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .more-highlights {
            padding: 6rpx 12rpx;
            background: $info-bg;
            border-radius: $border-radius-round;
            font-size: $font-size-extra-small;
            color: $info-color;
            border: 1rpx solid $info-border;
            cursor: pointer;
          }
        }
      }

      .content-stats {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 6rpx;

          .font-size-icon {
            font-size: 18rpx;
            color: $text-secondary;
            font-weight: bold;
          }

          .tag-count-icon {
            font-size: 18rpx;
            color: $text-secondary;
          }

          .stat-text {
            font-size: $font-size-extra-small;
            color: $text-secondary;
          }
        }
      }
    }
  }

  .item-actions {
    display: flex;
    gap: $margin-mini;
    margin: $margin-base 0;

    .action-btn {
      flex: 1;
      padding: 16rpx;
      font-size: $font-size-small;
    }
  }

  .item-footer {
    display: flex;
    justify-content: space-between;
    padding-top: $margin-mini;
    border-top: 1rpx solid $border-color-extra-light;

    .time-text {
      font-size: $font-size-extra-small;
      color: $text-placeholder;
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;

  .loading-spinner {
    width: 60rpx;
    height: 60rpx;
    border: 4rpx solid rgba($primary-color, 0.2);
    border-top-color: $primary-color;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 20rpx;
  }

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 20rpx;
  }

  .empty-text {
    font-size: $font-size-base;
    color: $empty-text-color;
    margin: $margin-base 0;
  }

  .btn {
    margin-top: $margin-base;
    width: 200rpx;
  }
}

.load-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;

  .loading-more {
    display: flex;
    align-items: center;
    gap: 10rpx;
    color: $text-secondary;
    font-size: $font-size-small;

    .loading-spinner-small {
      width: 24rpx;
      height: 24rpx;
      border: 2rpx solid rgba($primary-color, 0.2);
      border-top-color: $primary-color;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }

  .load-more-btn {
    padding: 16rpx 32rpx;
    background: $background-color;
    border-radius: $border-radius;
    color: $text-primary;
    font-size: $font-size-small;
  }
}

.no-more {
  text-align: center;
  padding: 40rpx 0;
  color: $text-secondary;
  font-size: $font-size-small;
}

.add-btn {
  position: fixed;
  right: 30rpx;
  bottom: calc(env(safe-area-inset-bottom) + 30rpx);
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $button-primary-bg;
  color: white;
  box-shadow: $button-active-shadow;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  z-index: $z-index-dropdown;

  &:active {
    transform: scale(0.95);
  }
}

// 关键词弹窗样式
.highlights-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: $z-index-modal;
  padding: $padding-base;

  .modal-content {
    background: $background-color-white;
    border-radius: $border-radius-large;
    width: 100%;
    max-width: 600rpx;
    max-height: 70vh;
    overflow: hidden;
    box-shadow: $box-shadow-dark;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-base;
      border-bottom: 1rpx solid $border-color-extra-light;

      .modal-title {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
      }

      .modal-close-btn {
        background: transparent;
        border: none;
        font-size: 32rpx;
        color: $text-secondary;
        width: 40rpx;
        height: 40rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .modal-body {
      padding: $padding-base;
      max-height: 50vh;
      overflow-y: auto;

      .all-highlights-list {
        display: flex;
        flex-wrap: wrap;
        gap: 12rpx;

        .highlight-tag-large {
          padding: 10rpx 16rpx;
          background: $primary-color-light;
          border-radius: $border-radius;
          font-size: $font-size-small;
          color: $primary-color;
          border: 1rpx solid $primary-border;
          cursor: pointer;
        }
      }
    }
  }
}

.placeholder-text {
  color: $text-placeholder;
  font-size: $font-size-base;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: $screen-md) {
  .evaluation-item {
    padding-left: 60rpx;

    .item-index {
      left: 10rpx;
      width: 30rpx;
      height: 30rpx;

      .index-number {
        font-size: $font-size-extra-small;
      }
    }

    .item-actions {
      flex-direction: column;
    }

    .item-footer {
      flex-direction: column;
      gap: 8rpx;
    }

    .content-stats {
      flex-direction: column;
      align-items: flex-start;
      gap: 8rpx;
    }
  }

  .highlights-modal {
    .modal-content {
      max-width: 90vw;
    }
  }
}
</style>