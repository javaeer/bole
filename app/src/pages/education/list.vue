<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-container card-container">
      <view class="search-box">
        <uni-icons type="search" size="20" color="#999" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="请输入学校名称搜索"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <uni-icons type="clear" size="18" color="#999" />
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
          <uni-icons
            v-if="sortBy === option.value"
            :type="sortOrder === 'asc' ? 'arrowup' : 'arrowdown'"
            size="14"
            :color="sortOrder === 'asc' ? successColor : dangerColor"
          />
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

      <!-- 列表为空 -->
      <view v-else-if="!loading && listData.length === 0" class="empty-state">
        <uni-icons type="info" size="60" color="#c0c4cc" />
        <text class="empty-text">暂无数据</text>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="item in listData"
          :key="item.id"
          class="list-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 主要内容 -->
          <view class="item-main">
            <view class="item-header flex-between">
              <text class="item-title text-truncate">{{ item.school }}</text>
              <view class="item-status">
                <view :class="['status-tag', getStatusClass(item)]">
                  {{ getStatusText(item) }}
                </view>
              </view>
            </view>

            <view class="item-content">
              <view class="item-info">
                <uni-icons type="person" size="16" color="#909399" />
                <text class="info-text">{{ item.major }} · {{ item.degree }}</text>
              </view>

              <view class="item-info">
                <uni-icons type="calendar" size="16" color="#909399" />
                <text class="info-text">{{ dateUtils.format(item.startDate) }} - {{ dateUtils.format(item.endDate) }}</text>
              </view>

              <view v-if="item.description" class="item-desc text-multi-truncate">
                {{ item.description }}
              </view>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="item-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editItem(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="item-footer">
            <text class="time-text">创建：{{ dateUtils.format(item.createdAt) }}</text>
            <text class="time-text">更新：{{ dateUtils.format((item.updatedAt))}}</text>
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
    <button class="add-btn" @click="addNewItem">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import { EducationExperienceItem } from "@/types/component";
import DateUtils, { dateUtils } from "../../utils/date";

// 响应式数据
const searchKeyword = ref('')
const sortBy = ref<'createdAt' | 'updatedAt' | 'startDate' | 'endDate'>('createdAt')
const sortOrder = ref<'asc' | 'desc'>('desc')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)

// 模拟数据
const mockData: EducationExperienceItem[] = [
  {
    id: 1,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-17 20:06:50",
    deleted: 0,
    userId: 1,
    school: "清华大学",
    major: "计算机科学与技术",
    degree: "本科",
    startDate: "2014-09-01",
    endDate: "2018-06-30",
    isHighest: 1,
    description: "主修计算机相关课程，包括数据结构、算法、操作系统等核心课程",
    achievements: "获得国家奖学金，参与国家级科研项目",
    sort: 1
  },
  {
    id: 2,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-18 10:30:25",
    deleted: 0,
    userId: 1,
    school: "北京大学",
    major: "软件工程",
    degree: "硕士",
    startDate: "2018-09-01",
    endDate: "2021-06-30",
    isHighest: 0,
    description: "深入研究软件工程理论与方法，参与多个大型项目开发",
    achievements: "发表SCI论文一篇，获得优秀毕业生称号",
    sort: 2
  },
  {
    id: 3,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-17 20:06:50",
    deleted: 0,
    userId: 1,
    school: "复旦大学",
    major: "人工智能",
    degree: "博士",
    startDate: "2021-09-01",
    endDate: "2025-06-30",
    isHighest: 1,
    description: "研究方向为机器学习与深度学习，参与多个国家级AI项目",
    achievements: "获得博士学位，发表多篇高水平论文",
    sort: 3
  }
]

const listData = ref<EducationExperienceItem[]>([])
const filteredData = ref<EducationExperienceItem[]>([])

// 排序选项
const sortOptions = [
  { label: '创建时间', value: 'createdAt' },
  { label: '更新时间', value: 'updatedAt' },
  { label: '开始时间', value: 'startDate' },
  { label: '结束时间', value: 'endDate' }
]

// 颜色变量（从uni.scss中提取）
const primaryColor = '#d4af37'
const successColor = '#67c23a'
const warningColor = '#e6a23c'
const dangerColor = '#f56c6c'

// 获取状态文本和样式
const getStatusText = (item: EducationExperienceItem) => {
  const now = new Date()
  const endDate = new Date(item.endDate)

  if (now < endDate) {
    return '在读'
  } else if (item.isHighest === 1) {
    return '最高学历'
  } else {
    return '已毕业'
  }
}

const getStatusClass = (item: EducationExperienceItem) => {
  const now = new Date()
  const endDate = new Date(item.endDate)

  if (now < endDate) {
    return 'status-warning'
  } else if (item.isHighest === 1) {
    return 'status-success'
  } else {
    return 'status-info'
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

  sortData()
}

// 排序数据
const sortData = () => {
  filteredData.value.sort((a: any, b: any) => {
    const aValue = a[sortBy.value]
    const bValue = b[sortBy.value]

    if (sortOrder.value === 'asc') {
      return aValue.localeCompare(bValue)
    } else {
      return bValue.localeCompare(aValue)
    }
  })
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

    if (searchKeyword.value) {
      filtered = filtered.filter(item =>
        item.school.includes(searchKeyword.value) ||
        item.major.includes(searchKeyword.value)
      )
    }

    // 排序
    filtered.sort((a: any, b: any) => {
      const aValue = a[sortBy.value]
      const bValue = b[sortBy.value]

      if (sortOrder.value === 'asc') {
        return aValue.localeCompare(bValue)
      } else {
        return bValue.localeCompare(aValue)
      }
    })

    filteredData.value = filtered

    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const pageData = filtered.slice(start, end)

    if (reset) {
      listData.value = pageData
    } else {
      listData.value = [...listData.value, ...pageData]
    }

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filtered.length
    loading.value = false
    currentPage.value++
  }, 500)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  loadData()
}

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/education/detail?id=${id}`
  })
}

const editItem = (id: number) => {
  uni.navigateTo({
    url: `/pages/education/detail?id=${id}&edit=true`
  })
}

const addNewItem = () => {
  uni.navigateTo({
    url: '/pages/education/detail'
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
      background: $primary-light;
      color: $primary-color;
      border-color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }
}

.list-scroll {
  height: calc(100vh - 200rpx);
  padding: $padding-small;
}

.list-item {
  margin-bottom: $margin-base;
  position: relative;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .item-main {
    .item-header {
      margin-bottom: $margin-mini;

      .item-title {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
        max-width: 60%;
      }

      .item-status {
        .status-tag {
          padding: 4rpx 16rpx;
          border-radius: $border-radius-small;
          font-size: $font-size-extra-small;
          font-weight: $font-weight-medium;
        }
      }
    }

    .item-content {
      .item-info {
        display: flex;
        align-items: center;
        gap: 8rpx;
        margin-bottom: 12rpx;

        .info-text {
          font-size: $font-size-small;
          color: $text-regular;
        }
      }

      .item-desc {
        font-size: $font-size-small;
        color: $text-secondary;
        line-height: 1.5;
        margin-top: $margin-mini;
        padding: 12rpx;
        background: $background-color;
        border-radius: $border-radius-small;
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

  .empty-text {
    font-size: $font-size-base;
    color: $empty-text-color;
    margin-top: $margin-base;
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
  .list-item {
    .item-actions {
      flex-direction: column;
    }
  }
}
</style>