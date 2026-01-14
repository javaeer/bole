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
          placeholder="搜索职位名称或描述"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <view class="clear-icon">×</view>
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
              <view class="arrow-down">▼</view>
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
              <view class="arrow-down">▼</view>
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
        <text class="empty-text">暂无工作经历</text>
        <button class="btn btn-primary" @click="addNewWork">添加工作经历</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="(item, index) in listData"
          :key="item.id"
          class="work-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 序号标签 -->
          <view class="item-index">
            <text class="index-number">{{ index + 1 }}</text>
          </view>

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
              <text class="company-text">{{ item.company }}</text>
            </view>
          </view>

          <!-- 工作时间 -->
          <view class="work-time">
            <view class="time-line">
              <view class="time-dot"></view>
              <view class="time-range">
                <text class="time-date">{{ formatDate(item.startDate) }}</text>
                <text class="time-separator">至</text>
                <text class="time-date" :class="{ 'current-date': item.isCurrent }">
                  {{ item.isCurrent ? "至今" : formatDate(item.endDate) }}
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

          <!-- 成就列表 -->
          <view class="work-achievements" v-if="item.achievements && item.achievements.length > 0">
            <view class="achievements-header">
              <view class="star-icon">★</view>
              <text class="achievements-title">主要成就</text>
              <text class="achievements-count">({{ item.achievements.length }})</text>
            </view>
            <view class="achievements-list">
              <view
                v-for="(achievement, aIndex) in item.achievements.slice(0, 3)"
                :key="aIndex"
                class="achievement-item"
              >
                <view class="achievement-index">{{ aIndex + 1 }}.</view>
                <text class="achievement-text text-multi-truncate">{{ achievement }}</text>
              </view>
              <view
                v-if="item.achievements.length > 3"
                class="more-achievements"
                @click.stop="showAllAchievements(item.achievements)"
              >
                <text>查看全部{{ item.achievements.length }}项成就</text>
                <view class="more-arrow">›</view>
              </view>
            </view>
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
            <text class="time-text">创建：{{ formatDateTime(item.createdAt) }}</text>
            <text class="time-text">更新：{{ formatDateTime(item.updatedAt) }}</text>
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
    <button class="add-btn" @click="addNewWork">
      <view class="icon-plus">+</view>
    </button>

    <!-- 成就全量弹窗 -->
    <view v-if="showAchievementsModal" class="achievements-modal" @click="closeAchievementsModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">全部成就</text>
          <button class="modal-close-btn" @click="closeAchievementsModal">×</button>
        </view>
        <view class="modal-body">
          <view class="all-achievements-list">
            <view
              v-for="(achievement, index) in currentAchievements"
              :key="index"
              class="achievement-item-large"
            >
              <view class="achievement-index-large">{{ index + 1 }}.</view>
              <view class="achievement-content-large">{{ achievement }}</view>
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
import type { WorkExperienceQuery, WorkExperienceResult } from "@/types/work-experience";
import WorkExperienceAPI from "@/subpackages/api/work-experience";
import { usePageRefresh } from "@/composables/usePageRefresh";

// 响应式数据
const searchKeywords = ref("");
const statusIndex = ref(0);
const sortIndex = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const hasMore = ref(true);
const listData = ref<WorkExperienceResult[]>([]);

// 成就弹窗相关
const showAchievementsModal = ref(false);
const currentAchievements = ref<string[]>([]);

// 筛选选项
const statusOptions = ["全部状态", "在职", "离职"];
const sortOptions = ["时间倒序", "时间正序", "创建时间", "更新时间"];

// 使用页面刷新 composable
const { refreshKey } = usePageRefresh({
  immediate: true,
  onRefresh: async () => {
    await loadData(true)
    uni.showToast({
      title: '列表已更新',
      icon: 'success',
      duration: 1500
    })
  }
})

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 计算工作持续时间
const calculateDuration = (startDate: string, endDate: string | null, isCurrent: boolean): string => {
  try {
    const start = new Date(startDate);
    const end = isCurrent ? new Date() : new Date(endDate || new Date());

    const years = end.getFullYear() - start.getFullYear();
    const months = end.getMonth() - start.getMonth();

    let totalMonths = years * 12 + months;
    if (end.getDate() < start.getDate()) {
      totalMonths--;
    }

    if (totalMonths < 0) totalMonths = 0;

    const yearsPart = totalMonths >= 12 ? Math.floor(totalMonths / 12) + "年" : "";
    const monthsPart = totalMonths % 12 > 0 ? (totalMonths % 12) + "个月" : "";

    return `${yearsPart}${monthsPart}`.trim() || "0个月";
  } catch {
    return "0个月";
  }
};

// 显示全部成就
const showAllAchievements = (achievements: string[]) => {
  currentAchievements.value = achievements;
  showAchievementsModal.value = true;
};

// 关闭成就弹窗
const closeAchievementsModal = () => {
  showAchievementsModal.value = false;
  currentAchievements.value = [];
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

// 筛选处理
const onStatusChange = (e: any) => {
  statusIndex.value = e.detail.value;
  currentPage.value = 1;
  loadData(true);
};

const onSortChange = (e: any) => {
  sortIndex.value = e.detail.value;
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
    const pageParam: PageParam = {
      page: currentPage.value,
      size: pageSize.value,
    };

    const query: WorkExperienceQuery = {};

    // 添加搜索条件
    if (searchKeywords.value) {
      query.keyword = searchKeywords.value;
    }

    // 添加状态筛选条件
    if (statusIndex.value > 0) {
      query.isCurrent = statusIndex.value === 1;
    }

    // 添加排序条件
    let orderBy = "createdAt";
    let orderDirection = "DESC";

    switch (sortIndex.value) {
      case 0: // 时间倒序
        orderBy = "startDate";
        orderDirection = "DESC";
        break;
      case 1: // 时间正序
        orderBy = "startDate";
        orderDirection = "ASC";
        break;
      case 2: // 创建时间
        orderBy = "createdAt";
        orderDirection = "DESC";
        break;
      case 3: // 更新时间
        orderBy = "updatedAt";
        orderDirection = "DESC";
        break;
    }

    query.orderBy = orderBy;
    query.orderDirection = orderDirection;

    // 调用API
    const response = await WorkExperienceAPI.page(pageParam, query);

    if (response) {
      const { records = [], total = 0 } = response;

      // 确保achievements是数组格式
      const processedRecords = records.map(record => ({
        ...record,
        achievements: Array.isArray(record.achievements)
          ? record.achievements
          : typeof record.achievements === "string"
            ? record.achievements.split("\n").map(a => a.trim()).filter(a => a)
            : [],
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
    url: `/subpackages/pages/work/work?id=${id}`,
  });
};

const editWork = (id: number) => {
  uni.navigateTo({
    url: `/subpackages/pages/work/work?id=${id}&edit=true`,
  });
};

const addNewWork = () => {
  uni.navigateTo({
    url: "/subpackages/pages/work/work",
  });
};

// 生命周期
onMounted(() => {
  loadData(true);
});

onLoad((options) => {
  // 兼容旧代码，如果通过参数传递refresh，则刷新
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

      .arrow-down {
        font-size: 20rpx;
        color: $text-secondary;
      }
    }
  }
}

.list-scroll {
  height: calc(100vh - 220rpx);
  padding: $padding-small;
}

.work-item {
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

      .star-icon {
        font-size: 20rpx;
        color: $warning-color;
      }

      .achievements-title {
        font-size: $font-size-small;
        font-weight: $font-weight-medium;
        color: $warning-color;
      }

      .achievements-count {
        font-size: $font-size-extra-small;
        color: $text-secondary;
      }
    }

    .achievements-list {
      .achievement-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 12rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .achievement-index {
          width: 30rpx;
          color: $warning-color;
          font-weight: $font-weight-medium;
          font-size: $font-size-extra-small;
        }

        .achievement-text {
          flex: 1;
          font-size: $font-size-small;
          color: $text-regular;
          line-height: 1.5;
          -webkit-line-clamp: 2;
        }
      }

      .more-achievements {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8rpx 0;
        color: $primary-color;
        font-size: $font-size-extra-small;
        border-top: 1rpx solid $border-color-extra-light;
        margin-top: 8rpx;
        cursor: pointer;

        .more-arrow {
          font-size: 24rpx;
        }
      }
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

// 成就弹窗样式
.achievements-modal {
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
    max-width: 700rpx;
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

      .all-achievements-list {
        .achievement-item-large {
          display: flex;
          align-items: flex-start;
          padding: 16rpx 0;
          border-bottom: 1rpx solid $border-color-extra-light;

          &:last-child {
            border-bottom: none;
          }

          .achievement-index-large {
            width: 40rpx;
            color: $warning-color;
            font-weight: $font-weight-medium;
            font-size: $font-size-small;
          }

          .achievement-content-large {
            flex: 1;
            font-size: $font-size-small;
            color: $text-regular;
            line-height: 1.5;
          }
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
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: $screen-md) {
  .filter-row {
    flex-direction: column;
    gap: $margin-mini;
  }

  .work-item {
    padding-left: 60rpx;

    .item-index {
      left: 10rpx;
      width: 30rpx;
      height: 30rpx;

      .index-number {
        font-size: $font-size-extra-small;
      }
    }

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

  .achievements-modal {
    .modal-content {
      max-width: 90vw;
    }
  }
}
</style>