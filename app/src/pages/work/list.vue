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
          placeholder="搜索职位名称或描述"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <uni-icons type="clear" size="18" color="#999" />
        </button>
      </view>

      <!-- 筛选行 -->
      <view class="filter-row">
        <!-- 工作状态筛选 -->
        <view class="filter-group">
          <text class="filter-label">工作状态</text>
          <picker
            :value="statusIndex"
            :range="statusOptions"
            @change="onStatusChange"
          >
            <view class="filter-select">
              {{ statusOptions[statusIndex] }}
              <uni-icons type="arrowdown" size="14" color="#999" />
            </view>
          </picker>
        </view>

        <!-- 时间排序 -->
        <view class="filter-group">
          <text class="filter-label">时间排序</text>
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
        <text class="empty-text">暂无工作经历</text>
        <button class="btn btn-primary" @click="addNewWork">添加工作经历</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="item in listData"
          :key="item.id"
          class="work-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 工作头部 -->
          <view class="work-header flex-between">
            <view class="work-title-section">
              <text class="work-position text-truncate">{{ item.position }}</text>
              <view class="work-badges">
                <view v-if="item.isCurrent" class="badge badge-current">
                  在职
                </view>
                <view v-else class="badge badge-past">
                  离职
                </view>
              </view>
            </view>

            <view class="work-company">
              <text class="company-text">公司ID: {{ item.companyId }}</text>
            </view>
          </view>

          <!-- 工作时间 -->
          <view class="work-time">
            <view class="time-line">
              <view class="time-dot"></view>
              <view class="time-range">
                <text class="time-date">{{ dateUtils.format(item.startDate) }}</text>
                <text class="time-separator">至</text>
                <text class="time-date" :class="{ 'current-date': item.isCurrent }">
                  {{ item.isCurrent ? '至今' : dateUtils.format(item.endDate) }}
                </text>
              </view>
              <view class="time-duration">
                <text>{{ calculateDuration(item.startDate, item.endDate, item.isCurrent) }}</text>
              </view>
            </view>
          </view>

          <!-- 工作描述 -->
          <view class="work-content" v-if="item.description">
            <text class="work-desc text-multi-truncate">{{ item.description }}</text>
          </view>

          <!-- 成就 -->
          <view class="work-achievements" v-if="item.achievements">
            <view class="achievements-header">
              <uni-icons type="star-filled" size="16" color="#e6a23c" />
              <text class="achievements-title">主要成就</text>
            </view>
            <text class="achievements-text text-multi-truncate">{{ item.achievements }}</text>
          </view>

          <!-- 操作按钮 -->
          <view class="work-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editWork(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="work-footer">
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
    <button class="add-btn" @click="addNewWork">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { WorkExperienceItem } from "@/types/work-experience";
import { dateUtils } from "../../utils/date";


// 响应式数据
const searchKeyword = ref('')
const statusIndex = ref(0)
const sortIndex = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)

// 模拟数据
const mockData: WorkExperienceItem[] = [
  {
    id: 1,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-18 09:15:30",
    deleted: 0,
    userId: 1,
    companyId: 101,
    position: "软件工程师",
    startDate: "2021-07-01",
    endDate: "2023-06-30",
    isCurrent: false,
    description: "负责核心业务功能开发，参与系统架构设计，主导多个重要模块的开发工作。",
    achievements: "成功交付3个重大项目，优化系统性能提升30%，获得年度优秀员工奖",
    sort: 1
  },
  {
    id: 2,
    createdAt: "2025-12-16 14:20:10",
    updatedAt: "2025-12-17 16:45:20",
    deleted: 0,
    userId: 1,
    companyId: 102,
    position: "高级前端工程师",
    startDate: "2023-07-15",
    endDate: "2024-12-31",
    isCurrent: true,
    description: "负责前端技术架构选型和团队管理，带领5人前端团队完成复杂SPA应用开发。",
    achievements: "引入微前端架构提升团队开发效率50%，主导重构项目提升用户体验评分20%",
    sort: 2
  },
  {
    id: 3,
    createdAt: "2025-12-15 10:30:45",
    updatedAt: "2025-12-16 11:25:35",
    deleted: 0,
    userId: 1,
    companyId: 103,
    position: "全栈开发工程师",
    startDate: "2020-03-01",
    endDate: "2021-06-30",
    isCurrent: false,
    description: "负责前后端全栈开发，参与产品从0到1的完整开发周期。",
    achievements: "独立完成核心业务模块开发，产品上线首月用户突破10万",
    sort: 3
  },
  {
    id: 4,
    createdAt: "2025-12-14 08:45:20",
    updatedAt: "2025-12-15 14:30:15",
    deleted: 0,
    userId: 1,
    companyId: 104,
    position: "技术经理",
    startDate: "2019-01-01",
    endDate: "2020-02-29",
    isCurrent: false,
    description: "负责技术团队管理、项目规划和人员培养，制定技术发展路线。",
    achievements: "团队规模从3人扩展到15人，成功交付10+个项目，客户满意度95%",
    sort: 4
  }
]

const listData = ref<WorkExperienceItem[]>([])
const filteredData = ref<WorkExperienceItem[]>([])

// 筛选选项
const statusOptions = ['全部状态', '在职', '离职']
const sortOptions = ['时间倒序', '时间正序', '创建时间', '更新时间']

// 计算工作持续时间
const calculateDuration = (startDate: string, endDate: string | null, isCurrent: boolean): string => {
  const start = new Date(startDate)
  const end = isCurrent ? new Date() : new Date(endDate || new Date())

  const years = end.getFullYear() - start.getFullYear()
  const months = end.getMonth() - start.getMonth()

  let totalMonths = years * 12 + months
  if (end.getDate() < start.getDate()) {
    totalMonths--
  }

  if (totalMonths < 0) totalMonths = 0

  const yearsPart = totalMonths >= 12 ? Math.floor(totalMonths / 12) + '年' : ''
  const monthsPart = totalMonths % 12 > 0 ? (totalMonths % 12) + '个月' : ''

  return `${yearsPart}${monthsPart}`.trim() || '0个月'
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
const onStatusChange = (e: any) => {
  statusIndex.value = e.detail.value
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
    case 0: // 时间倒序
      filteredData.value.sort((a, b) =>
        new Date(b.startDate).getTime() - new Date(a.startDate).getTime()
      )
      break
    case 1: // 时间正序
      filteredData.value.sort((a, b) =>
        new Date(a.startDate).getTime() - new Date(b.startDate).getTime()
      )
      break
    case 2: // 创建时间
      filteredData.value.sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      )
      break
    case 3: // 更新时间
      filteredData.value.sort((a, b) =>
        new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
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
        item.description.toLowerCase().includes(keyword) ||
        (item.achievements && item.achievements.toLowerCase().includes(keyword))
      )
    }

    // 状态筛选
    if (statusIndex.value > 0) {
      const status = statusIndex.value === 1 // 1: 在职, 2: 离职
      filtered = filtered.filter(item => item.isCurrent === status)
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
    url: `/pages/work/detail?id=${id}`
  })
}

const editWork = (id: number) => {
  uni.navigateTo({
    url: `/pages/work/detail?id=${id}&edit=true`
  })
}

const addNewWork = () => {
  uni.navigateTo({
    url: '/pages/work/detail'
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

.work-item {
  margin-bottom: $margin-base;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .work-header {
    margin-bottom: $margin-base;

    .work-title-section {
      flex: 1;

      .work-position {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: 8rpx;
        display: block;
      }

      .work-badges {
        display: flex;
        gap: 8rpx;

        .badge {
          padding: 4rpx 12rpx;
          border-radius: $border-radius-small;
          font-size: $font-size-extra-small;
          font-weight: $font-weight-medium;

          &-current {
            background: $success-bg;
            color: $success-color;
            border: 1rpx solid $success-border;
          }

          &-past {
            background: $info-bg;
            color: $info-color;
            border: 1rpx solid $info-border;
          }
        }
      }
    }

    .work-company {
      .company-text {
        font-size: $font-size-small;
        color: $text-secondary;
        background: $background-color;
        padding: 4rpx 12rpx;
        border-radius: $border-radius-small;
      }
    }
  }

  .work-time {
    position: relative;
    padding: 0 0 $margin-base 40rpx;
    margin-bottom: $margin-base;

    &::before {
      content: '';
      position: absolute;
      left: 16rpx;
      top: 0;
      bottom: 0;
      width: 2rpx;
      background: $border-color-light;
    }

    .time-line {
      position: relative;

      .time-dot {
        position: absolute;
        left: -30rpx;
        top: 50%;
        transform: translateY(-50%);
        width: 12rpx;
        height: 12rpx;
        border-radius: 50%;
        background: $primary-color;
        border: 2rpx solid $background-color-white;
        box-shadow: 0 0 0 2rpx $primary-color;
      }

      .time-range {
        display: flex;
        align-items: center;
        gap: 12rpx;
        margin-bottom: 8rpx;

        .time-date {
          font-size: $font-size-base;
          font-weight: $font-weight-medium;
          color: $text-primary;

          &.current-date {
            color: $success-color;
            font-weight: $font-weight-bold;
          }
        }

        .time-separator {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .time-duration {
        font-size: $font-size-small;
        color: $text-secondary;
        background: $background-color;
        padding: 4rpx 12rpx;
        border-radius: $border-radius-small;
        display: inline-block;
      }
    }
  }

  .work-content {
    margin-bottom: $margin-base;

    .work-desc {
      font-size: $font-size-small;
      color: $text-regular;
      line-height: 1.6;
      background: $background-color;
      padding: 16rpx;
      border-radius: $border-radius-small;
    }
  }

  .work-achievements {
    margin-bottom: $margin-base;
    padding: 16rpx;
    background: $warning-light;
    border-radius: $border-radius-small;
    border: 1rpx solid $warning-border;

    .achievements-header {
      display: flex;
      align-items: center;
      gap: 8rpx;
      margin-bottom: 12rpx;

      .achievements-title {
        font-size: $font-size-small;
        font-weight: $font-weight-medium;
        color: $warning-color;
      }
    }

    .achievements-text {
      font-size: $font-size-small;
      color: $text-regular;
      line-height: 1.5;
    }
  }

  .work-actions {
    display: flex;
    gap: $margin-mini;
    margin: $margin-base 0;

    .action-btn {
      flex: 1;
      padding: 16rpx;
      font-size: $font-size-small;
    }
  }

  .work-footer {
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

  .work-item {
    .work-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12rpx;
    }

    .work-actions {
      flex-direction: column;
    }

    .work-footer {
      flex-direction: column;
      gap: 8rpx;
    }
  }
}
</style>