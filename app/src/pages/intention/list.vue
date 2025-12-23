<template>
  <view class="page-container">
    <!-- 搜索和筛选栏 -->
    <view class="filter-container card-container">
      <!-- 搜索框 -->
      <view class="search-box">
        <uni-icons type="search" size="20" color="#999" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索职位或城市"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <uni-icons type="clear" size="18" color="#999" />
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
              <uni-icons type="arrowdown" size="14" color="#999" />
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
              <uni-icons type="arrowdown" size="14" color="#999" />
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
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <view v-else-if="!loading && listData.length === 0" class="empty-state">
        <uni-icons type="briefcase" size="60" color="#c0c4cc" />
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
              <uni-icons type="location" size="16" color="#909399" />
              <text class="info-text">{{ item.city || '未指定城市' }}</text>
              <text class="info-separator">|</text>
              <uni-icons type="time" size="16" color="#909399" />
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
            <text class="time-text">创建：{{ dateUtils.format(item.createdAt) }}</text>
            <text class="time-text">更新：{{ dateUtils.format(item.updatedAt) }}</text>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more">
          <uni-load-more
            :status="loading ? 'loading' : 'more'"
            :content-text="{
              contentdown: '上拉加载更多',
              contentrefresh: '正在加载...',
              contentnomore: '没有更多了'
            }"
          />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { dateUtils } from '@/utils/date'
import { JobIntentionItem } from "@/types/job-intention";


// 响应式数据
const searchKeyword = ref('')
const jobTypeIndex = ref(0)
const sortIndex = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)

// 模拟数据
const mockData: JobIntentionItem[] = [
  {
    id: 1,
    createdAt: "2025-12-22 03:55:22",
    updatedAt: "2025-12-22 10:30:15",
    deleted: 0,
    userId: 1,
    position: "前端开发工程师",
    city: "北京",
    salary: "25000",
    jobType: "全职"
  },
  {
    id: 2,
    createdAt: "2025-12-21 14:20:30",
    updatedAt: "2025-12-22 08:45:20",
    deleted: 0,
    userId: 1,
    position: "Java开发工程师",
    city: "上海",
    salary: "30000",
    jobType: "全职"
  },
  {
    id: 3,
    createdAt: "2025-12-20 09:15:40",
    updatedAt: "2025-12-21 16:30:50",
    deleted: 0,
    userId: 1,
    position: "产品经理",
    city: "深圳",
    salary: "35000",
    jobType: "全职"
  },
  {
    id: 4,
    createdAt: "2025-12-19 11:30:25",
    updatedAt: "2025-12-20 14:25:35",
    deleted: 0,
    userId: 1,
    position: "UI设计师",
    city: "杭州",
    salary: "20000",
    jobType: "兼职"
  },
  {
    id: 5,
    createdAt: "2025-12-18 16:45:10",
    updatedAt: "2025-12-19 09:20:45",
    deleted: 0,
    userId: 1,
    position: "测试工程师",
    city: "广州",
    salary: "18000",
    jobType: "实习"
  }
]

const listData = ref<JobIntentionItem[]>([])
const filteredData = ref<JobIntentionItem[]>([])

// 筛选选项
const jobTypeOptions = ['全部类型', '全职', '兼职', '实习', '远程']
const sortOptions = ['创建时间', '更新时间', '薪资降序', '薪资升序']

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
  searchKeyword.value = ''
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
  sortData()
}

// 排序数据
const sortData = () => {
  switch(sortIndex.value) {
    case 0: // 创建时间
      filteredData.value.sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      )
      break
    case 1: // 更新时间
      filteredData.value.sort((a, b) =>
        new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
      )
      break
    case 2: // 薪资降序
      filteredData.value.sort((a, b) =>
        parseInt(b.salary || '0') - parseInt(a.salary || '0')
      )
      break
    case 3: // 薪资升序
      filteredData.value.sort((a, b) =>
        parseInt(a.salary || '0') - parseInt(b.salary || '0')
      )
      break
  }

  // 更新分页数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  listData.value = filteredData.value.slice(start, end)
}

// 加载数据
const loadData = (reset = false) => {
  if (loading.value) return

  loading.value = true

  if (reset) {
    currentPage.value = 1
    hasMore.value = true
    listData.value = []
  }

  // 模拟API请求延迟
  setTimeout(() => {
    // 筛选数据
    let filtered = [...mockData]

    // 关键字搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(item =>
        item.position.toLowerCase().includes(keyword) ||
        item.city.toLowerCase().includes(keyword)
      )
    }

    // 工作类型筛选
    if (jobTypeIndex.value > 0) {
      const selectedType = jobTypeOptions[jobTypeIndex.value]
      filtered = filtered.filter(item => item.jobType === selectedType)
    }

    filteredData.value = filtered

    // 排序
    sortData()

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filtered.length
    loading.value = false
    currentPage.value++
  }, 500)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return

  // 模拟API请求延迟
  loading.value = true
  setTimeout(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const pageData = filteredData.value.slice(start, end)

    listData.value = [...listData.value, ...pageData]

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filteredData.value.length
    loading.value = false
    currentPage.value++
  }, 500)
}

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/intention/detail?id=${id}`
  })
}

const editIntention = (id: number) => {
  uni.navigateTo({
    url: `/pages/intention/detail?id=${id}&edit=true`
  })
}

const addNewIntention = () => {
  uni.navigateTo({
    url: '/pages/intention/detail'
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
            background: $primary-light;
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
      gap: 12rpx;

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
    background: $primary-light;
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
  padding: $margin-base 0;
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