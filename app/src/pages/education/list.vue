<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-container card-container">
      <view class="search-box">
        <uni-icons type="search" size="20" color="#999" />
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="请输入学校名称搜索"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
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
              <text class="item-title text-truncate">{{ item.university }}</text>
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
                <text class="info-text">{{ formatDate(item.startDate) }} - {{ formatDate(item.endDate) }}
                </text>
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
            <text class="time-text">创建：{{ formatDateTime(item.createdAt) }}</text>
            <text class="time-text">更新：{{ formatDateTime(item.updatedAt) }}</text>
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
import { ref, onMounted } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import EducationExperienceAPI from "@/api/education-experience";
import type { EducationExperienceResult } from "@/types/education-experience";

// 响应式数据
const searchKeywords = ref("");
const sortBy = ref<"createdAt" | "updatedAt" | "startDate" | "endDate">("createdAt");
const sortOrder = ref<"asc" | "desc">("desc");
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const hasMore = ref(true);
const listData = ref<EducationExperienceResult[]>([]);

// 排序选项
const sortOptions = [
  { label: "创建时间", value: "createdAt" },
  { label: "更新时间", value: "updatedAt" },
  { label: "开始时间", value: "startDate" },
  { label: "结束时间", value: "endDate" },
];

// 颜色变量
const successColor = "#67c23a";
const dangerColor = "#f56c6c";

// 获取状态文本和样式
const getStatusText = (item: EducationExperienceResult) => {
  const now = new Date();
  const endDate = new Date(item.endDate);

  if (now < endDate) {
    return "在读";
  } else if (item.isHighest === 1) {
    return "最高学历";
  } else {
    return "已毕业";
  }
};

const getStatusClass = (item: EducationExperienceResult) => {
  const now = new Date();
  const endDate = new Date(item.endDate);

  if (now < endDate) {
    return "status-warning";
  } else if (item.isHighest === 1) {
    return "status-success";
  } else {
    return "status-info";
  }
};

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  loadData(true);
};

const clearSearch = () => {
  searchKeywords.value = "";
  currentPage.value = 1;
  loadData(true);
};

// 排序处理
const changeSort = (field: any) => {
  if (sortBy.value === field) {
    // 切换排序顺序
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    // 切换到新字段，默认降序
    sortBy.value = field;
    sortOrder.value = "desc";
  }

  currentPage.value = 1;
  loadData(true);
};

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
      query.university = searchKeywords.value;
      // 如果还需要搜索专业，可以添加：query.major = searchKeywords.value;
    }

    // 添加排序条件
    if (sortBy.value && sortOrder.value) {
      query.orderBy = sortBy.value;
      query.orderDirection = sortOrder.value === 'asc' ? 'ASC' : 'DESC';
    }

    // 调用API
    const response = await EducationExperienceAPI.page(pageParam, query);

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
    url: `/pages/education/education?id=${id}`,
  });
};

const editItem = (id: number) => {
  uni.navigateTo({
    url: `/pages/education/education?id=${id}&edit=true`,
  });
};

const addNewItem = () => {
  uni.navigateTo({
    url: "/pages/education/education",
  });
};

// 生命周期
onMounted(() => {
  loadData(true);
});

onLoad((options) => {
  // 从详情页返回时刷新数据
  const refresh = options?.refresh === "true";
  if (refresh) {
    loadData(true);
  }
});

onReachBottom(() => {
  loadMore();
});
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
      background: $primary-color-light;
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

// 状态标签样式
.status-success {
  background-color: $success-bg;
  color: $success-color;
}

.status-warning {
  background-color: $warning-bg;
  color: $warning-color;
}

.status-info {
  background-color: $info-bg;
  color: $info-color;
}

// 工具类
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-multi-truncate {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: $screen-md) {
  .list-item {
    .item-actions {
      flex-direction: column;
    }
  }
}
</style>