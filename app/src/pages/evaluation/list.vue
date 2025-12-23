<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-container card-container">
      <view class="search-box">
        <uni-icons type="search" size="20" color="#999" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索评价内容或关键词"
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
        <uni-icons type="edit" size="60" color="#c0c4cc" />
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

              <!-- 关键词 -->
              <view class="keywords-section" v-if="getKeywordsArray(item.keywords).length > 0">
                <view class="keywords-label">
                  <uni-icons type="tags" size="14" color="#909399" />
                  <text class="label-text">关键词</text>
                </view>
                <view class="keywords-list">
                  <view
                    v-for="keyword in getKeywordsArray(item.keywords)"
                    :key="keyword"
                    class="keyword-tag"
                    @click.stop="searchByKeyword(keyword)"
                  >
                    <text>{{ keyword }}</text>
                  </view>
                </view>
              </view>

              <!-- 内容统计 -->
              <view class="content-stats">
                <view class="stat-item">
                  <uni-icons type="font-size" size="12" color="#909399" />
                  <text class="stat-text">{{ item.content.length }}字</text>
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
    <button class="add-btn" @click="addNewEvaluation">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import { dateUtils } from "@/utils/date";
import { SelfEvaluationItem } from "@/types/self-evaluation";


// 响应式数据
const searchKeyword = ref('')
const sortBy = ref<'createdAt' | 'updatedAt'>('createdAt')
const sortOrder = ref<'asc' | 'desc'>('desc')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)

// 模拟数据
const mockData: SelfEvaluationItem[] = [
  {
    id: 1,
    createdAt: "2025-12-22 03:59:05",
    updatedAt: "2025-12-22 10:30:15",
    deleted: 0,
    userId: 1,
    content: "作为一名资深软件工程师，我拥有超过5年的全栈开发经验。熟练掌握Java、Python、JavaScript等多种编程语言，对微服务架构、云原生技术有深入理解。在工作中，我注重代码质量，擅长性能优化和系统架构设计。具备良好的团队协作能力和项目管理经验，能够带领团队高效完成复杂项目。",
    keywords: "资深工程师,全栈开发,微服务,架构设计,团队协作"
  },
  {
    id: 2,
    createdAt: "2025-12-21 14:20:30",
    updatedAt: "2025-12-22 08:45:20",
    deleted: 0,
    userId: 1,
    content: "具备良好的沟通能力和团队协作精神，能够快速适应新环境。在多个项目中担任技术负责人，成功带领团队完成多个大型项目的开发与部署。注重技术创新，持续学习新技术，保持在技术前沿。",
    keywords: "沟通能力,团队协作,技术负责人,项目管理,学习能力"
  },
  {
    id: 3,
    createdAt: "2025-12-20 09:15:40",
    updatedAt: "2025-12-21 16:30:50",
    deleted: 0,
    userId: 1,
    content: "在解决问题时，我善于从多角度思考，能够快速定位问题的根本原因并提出有效的解决方案。对技术充满热情，喜欢探索新技术，并将其应用于实际项目中以提升工作效率。",
    keywords: "问题解决,创新思维,技术热情,效率提升"
  },
  {
    id: 4,
    createdAt: "2025-12-19 11:30:25",
    updatedAt: "2025-12-20 14:25:35",
    deleted: 0,
    userId: 1,
    content: "拥有丰富的产品开发经验，能够从用户需求出发，设计出既美观又实用的产品。注重用户体验，善于通过数据分析优化产品功能。具备良好的跨部门沟通协调能力。",
    keywords: "产品开发,用户体验,数据分析,跨部门协作"
  },
  {
    id: 5,
    createdAt: "2025-12-18 16:45:10",
    updatedAt: "2025-12-19 09:20:45",
    deleted: 0,
    userId: 1,
    content: "具备较强的学习能力和适应能力，能够快速掌握新技术。在工作中，我始终保持积极的态度，勇于接受挑战，能够在压力下保持高效工作。注重细节，追求卓越，对工作质量有严格的要求。",
    keywords: "学习能力,适应能力,抗压能力,注重细节,追求卓越"
  }
]

const listData = ref<SelfEvaluationItem[]>([])
const filteredData = ref<SelfEvaluationItem[]>([])

// 排序选项
const sortOptions = [
  { label: '创建时间', value: 'createdAt' },
  { label: '更新时间', value: 'updatedAt' }
]

// 颜色变量（从uni.scss中提取）
const primaryColor = '#d4af37'
const successColor = '#67c23a'
const dangerColor = '#f56c6c'

// 解析关键词字符串
const getKeywordsArray = (keywordsStr: string | null): string[] => {
  if (!keywordsStr) return []
  return keywordsStr.split(',').map(keyword => keyword.trim()).filter(keyword => keyword)
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

// 按关键词搜索
const searchByKeyword = (keyword: string) => {
  searchKeyword.value = keyword
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
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(item =>
        item.content.toLowerCase().includes(keyword) ||
        (item.keywords && item.keywords.toLowerCase().includes(keyword))
      )
    }

    filteredData.value = filtered

    // 排序
    sortData()

    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const pageData = filteredData.value.slice(start, end)

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
    url: `/pages/evaluation/detail?id=${id}`
  })
}

const editEvaluation = (id: number) => {
  uni.navigateTo({
    url: `/pages/evaluation/detail?id=${id}&edit=true`
  })
}

const addNewEvaluation = () => {
  uni.navigateTo({
    url: '/pages/evaluation/detail'
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
    background: $primary-light;
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

      .keywords-section {
        margin-bottom: $margin-small;

        .keywords-label {
          display: flex;
          align-items: center;
          gap: 8rpx;
          margin-bottom: 12rpx;

          .label-text {
            font-size: $font-size-extra-small;
            color: $text-secondary;
            font-weight: $font-weight-medium;
          }
        }

        .keywords-list {
          display: flex;
          flex-wrap: wrap;
          gap: 8rpx;

          .keyword-tag {
            padding: 4rpx 12rpx;
            background: $primary-light;
            border-radius: $border-radius-round;
            font-size: $font-size-extra-small;
            color: $primary-color;
            border: 1rpx solid $primary-border;
          }
        }
      }

      .content-stats {
        display: flex;
        justify-content: flex-end;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4rpx;

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
  }
}
</style>