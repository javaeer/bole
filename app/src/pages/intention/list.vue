<template>
  <view class="page-container">
    <!-- 搜索和筛选栏 -->
    <view class="filter-container card-container">
      <!-- 搜索框 -->
      <view class="search-box">
        <view class="search-icon">🔍</view>
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="搜索职位或城市"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <view class="clear-icon">×</view>
        </button>
      </view>

      <!-- 筛选行 -->
      <view class="filter-row">
        <!-- 工作类型筛选 -->
        <view class="filter-group">
          <text class="filter-label">工作类型</text>
          <picker
            :value="jobTypeIndex"
            :range="jobTypeOptions"
            @change="onJobTypeChange"
          >
            <view class="filter-select">
              {{ jobTypeOptions[jobTypeIndex] }}
              <view class="arrow-icon">▼</view>
            </view>
          </picker>
        </view>

        <!-- 排序 -->
        <view class="filter-group">
          <text class="filter-label">排序</text>
          <picker
            :value="sortIndex"
            :range="sortOptions"
            @change="onSortChange"
          >
            <view class="filter-select">
              {{ sortOptions[sortIndex] }}
              <view class="arrow-icon">▼</view>
            </view>
          </picker>
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

      <view v-else-if="!loading && listData.length === 0" class="empty-state">
        <view class="empty-icon">💼</view>
        <text class="empty-text">暂无求职意向</text>
        <button class="btn btn-primary" @click="addNewIntention">添加求职意向</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="item in listData"
          :key="item.id"
          class="intention-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 职位头部 -->
          <view class="intention-header flex-between">
            <view class="intention-title-section">
              <text class="intention-position text-truncate">{{ item.position }}</text>
              <view class="intention-type">
                <view :class="['type-tag', getJobTypeClass(item.jobType)]">
                  {{ item.jobType }}
                </view>
              </view>
            </view>

            <view class="intention-salary">
              <text class="salary-text">{{ formatSalary(item.salary) }}</text>
            </view>
          </view>

          <!-- 城市和工作类型 -->
          <view class="intention-info">
            <view class="info-row">
              <view class="location-icon">📍</view>
              <text class="info-text">{{ item.city || '未指定城市' }}</text>
              <text class="info-separator">|</text>
              <view class="time-icon">🕒</view>
              <text class="info-text">{{ item.jobType }}</text>
            </view>
          </view>

          <!-- 期望薪资详情 -->
          <view class="salary-details" v-if="item.salary">
            <view class="salary-header">
              <text class="salary-label">期望薪资</text>
              <text class="salary-value">{{ formatSalary(item.salary) }}</text>
            </view>
            <view class="salary-analysis">
              <text class="analysis-text">{{ getSalaryAnalysis(item.salary) }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="intention-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editIntention(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="intention-footer">
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
    <button class="add-btn" @click="addNewIntention">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import type { JobIntentionResult } from "@/types/job-intention";
import JobIntentionAPI from "@/api/job-intention";

// 响应式数据
const searchKeywords = ref('')
const jobTypeIndex = ref(0)
const sortIndex = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)
const listData = ref<JobIntentionResult[]>([])

// 筛选选项
const jobTypeOptions = ['全部类型', '全职', '兼职', '实习', '远程']
const sortOptions = ['创建时间', '更新时间', '薪资降序', '薪资升序']

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

// 获取工作类型样式
const getJobTypeClass = (jobType: string) => {
  switch(jobType) {
    case '全职': return 'type-fulltime'
    case '兼职': return 'type-parttime'
    case '实习': return 'type-intern'
    case '远程': return 'type-remote'
    default: return 'type-default'
  }
}

// 格式化薪资
const formatSalary = (salary: string): string => {
  if (!salary) return '面议'

  const num = parseInt(salary)
  if (isNaN(num)) return '面议'

  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万/月`
  } else {
    return `${num}元/月`
  }
}

// 薪资分析
const getSalaryAnalysis = (salary: string): string => {
  if (!salary) return '薪资面议'

  const num = parseInt(salary)
  if (isNaN(num)) return '薪资面议'

  if (num >= 30000) {
    return '高级职位薪资范围'
  } else if (num >= 15000) {
    return '中级职位薪资范围'
  } else {
    return '初级职位薪资范围'
  }
}

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

// 筛选处理
const onJobTypeChange = (e: any) => {
  jobTypeIndex.value = e.detail.value
  currentPage.value = 1
  loadData(true)
}

const onSortChange = (e: any) => {
  sortIndex.value = e.detail.value
  currentPage.value = 1
  loadData(true)
}

// 构建查询参数
const buildQueryParams = () => {
  const query: any = {};

  // 添加搜索条件
  if (searchKeywords.value) {
    query.keyword = searchKeywords.value;
  }

  // 添加工作类型筛选
  if (jobTypeIndex.value > 0) {
    const selectedType = jobTypeOptions[jobTypeIndex.value];
    query.jobType = selectedType;
  }

  // 添加排序条件
  if (sortIndex.value === 0) {
    query.orderBy = 'createdAt';
    query.orderDirection = 'DESC';
  } else if (sortIndex.value === 1) {
    query.orderBy = 'updatedAt';
    query.orderDirection = 'DESC';
  } else if (sortIndex.value === 2) {
    query.orderBy = 'salary';
    query.orderDirection = 'DESC';
  } else if (sortIndex.value === 3) {
    query.orderBy = 'salary';
    query.orderDirection = 'ASC';
  }

  return query;
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

    const query = buildQueryParams();

    // 调用API
    const response = await JobIntentionAPI.page(pageParam, query);

    if (response) {
      const { records = [], total = 0 } = response;

      if (reset) {
        listData.value = records;
      } else {
        listData.value = [...listData.value, ...records];
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
    url: `/pages/intention/intention?id=${id}`
  })
}

const editIntention = (id: number) => {
  uni.navigateTo({
    url: `/pages/intention/intention?id=${id}&edit=true`
  })
}

const addNewIntention = () => {
  uni.navigateTo({
    url: '/pages/intention/intention'
  })
}

// 生命周期
onMounted(() => {
  loadData(true)
})

onLoad((options) => {
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

.filter-container {
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  border-radius: 0 0 $border-radius $border-radius;
  box-shadow: $box-shadow;
  padding: $padding-small;
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

.filter-row {
  display: flex;
  gap: $margin-small;
  justify-content: space-between;

  .filter-group {
    flex: 1;

    .filter-label {
      display: block;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      margin-bottom: 8rpx;
    }

    .filter-select {
      padding: 16rpx;
      background: $background-color;
      border-radius: $border-radius-small;
      font-size: $font-size-small;
      color: $text-primary;
      border: 1rpx solid $border-color-light;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .arrow-icon {
        font-size: 18rpx;
        color: $text-secondary;
      }
    }
  }
}

.list-scroll {
  height: calc(100vh - 220rpx);
  padding: $padding-small;
}

.intention-item {
  margin-bottom: $margin-base;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .intention-header {
    margin-bottom: $margin-base;

    .intention-title-section {
      flex: 1;

      .intention-position {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: 8rpx;
        display: block;
      }

      .intention-type {
        .type-tag {
          padding: 4rpx 12rpx;
          border-radius: $border-radius-small;
          font-size: $font-size-extra-small;
          font-weight: $font-weight-medium;

          &.type-fulltime {
            background: $success-bg;
            color: $success-color;
            border: 1rpx solid $success-border;
          }

          &.type-parttime {
            background: $warning-bg;
            color: $warning-color;
            border: 1rpx solid $warning-border;
          }

          &.type-intern {
            background: $info-bg;
            color: $info-color;
            border: 1rpx solid $info-border;
          }

          &.type-remote {
            background: $primary-color-light;
            color: $primary-color;
            border: 1rpx solid $primary-border;
          }

          &.type-default {
            background: $background-color;
            color: $text-secondary;
            border: 1rpx solid $border-color-light;
          }
        }
      }
    }

    .intention-salary {
      .salary-text {
        font-size: $font-size-large;
        font-weight: $font-weight-bold;
        color: $primary-color;
      }
    }
  }

  .intention-info {
    margin-bottom: $margin-base;

    .info-row {
      display: flex;
      align-items: center;
      gap: 8rpx;

      .location-icon, .time-icon {
        font-size: 28rpx;
        color: $text-secondary;
      }

      .info-text {
        font-size: $font-size-small;
        color: $text-regular;
      }

      .info-separator {
        color: $border-color;
        margin: 0 8rpx;
      }
    }
  }

  .salary-details {
    margin-bottom: $margin-base;
    padding: 16rpx;
    background: $primary-color-light;
    border-radius: $border-radius-small;
    border: 1rpx solid $primary-border;

    .salary-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8rpx;

      .salary-label {
        font-size: $font-size-small;
        color: $text-secondary;
      }

      .salary-value {
        font-size: $font-size-medium;
        font-weight: $font-weight-bold;
        color: $primary-color;
      }
    }

    .salary-analysis {
      .analysis-text {
        font-size: $font-size-extra-small;
        color: $text-secondary;
      }
    }
  }

  .intention-actions {
    display: flex;
    gap: $margin-mini;
    margin: $margin-base 0;

    .action-btn {
      flex: 1;
      padding: 16rpx;
      font-size: $font-size-small;
    }
  }

  .intention-footer {
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

.placeholder-text {
  color: $text-placeholder;
  font-size: $font-size-base;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: $screen-md) {
  .filter-row {
    flex-direction: column;
    gap: $margin-mini;
  }

  .intention-item {
    .intention-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12rpx;
    }

    .intention-actions {
      flex-direction: column;
    }

    .intention-footer {
      flex-direction: column;
      gap: 8rpx;
    }
  }
}
</style>