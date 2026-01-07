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
          placeholder="搜索项目名称或描述"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <view class="clear-icon">×</view>
        </button>
      </view>

      <!-- 筛选行 -->
      <view class="filter-row">
        <!-- 状态筛选 -->
        <view class="filter-group">
          <text class="filter-label">状态</text>
          <picker
            :value="statusIndex"
            :range="statusOptions"
            @change="onStatusChange"
          >
            <view class="filter-select">
              {{ statusOptions[statusIndex] }}
              <view class="arrow-icon">▼</view>
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
        <view class="empty-icon">📊</view>
        <text class="empty-text">暂无项目经历</text>
        <button class="btn btn-primary" @click="addNewProject">添加项目</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="item in listData"
          :key="item.id"
          class="project-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 项目头部 -->
          <view class="project-header">
            <view class="project-title-section">
              <text class="project-name text-truncate">{{ item.name }}</text>
              <view class="project-badges">
                <view :class="['status-badge', getStatusClass(item.status)]">
                  {{ getStatusText(item.status) }}
                </view>
              </view>
            </view>

            <view class="project-time">
              <text class="time-text">{{ formatDate(item.startDate) }} - {{ formatDate(item.endDate) }}</text>
            </view>
          </view>

          <!-- 项目描述 -->
          <view class="project-desc text-multi-truncate">
            {{ item.description }}
          </view>

          <!-- 时间进度条 -->
          <view class="project-progress">
            <view class="progress-header">
              <text class="progress-label">项目时间进度</text>
              <text class="progress-value">{{ calculateProgress(item.startDate, item.endDate) }}%</text>
            </view>
            <view class="progress-bar">
              <view
                class="progress-fill"
                :class="getProgressClass(item.startDate, item.endDate)"
                :style="{ width: `${calculateProgress(item.startDate, item.endDate)}%` }"
              ></view>
            </view>
            <view class="progress-labels">
              <text class="progress-date">{{ formatDate(item.startDate) }}</text>
              <text class="progress-date">{{ formatDate(item.endDate) }}</text>
            </view>
          </view>

          <!-- 成就列表 -->
          <view class="project-achievements" v-if="item.achievements && item.achievements.length > 0">
            <view class="achievements-header">
              <view class="star-icon">★</view>
              <text class="achievements-title">项目成就</text>
              <text class="achievements-count">{{ item.achievements.length }}个</text>
            </view>
            <view class="achievements-list">
              <view
                v-for="(achievement, index) in item.achievements.slice(0, 3)"
                :key="index"
                class="achievement-item"
              >
                <view class="achievement-index">{{ index + 1 }}.</view>
                <text class="achievement-text text-multi-truncate">{{ achievement }}</text>
              </view>
              <view
                v-if="item.achievements.length > 3"
                class="more-achievements"
                @click.stop="showAllAchievements(item.achievements, item.name)"
              >
                <text>查看更多成就（共{{ item.achievements.length }}个）</text>
                <view class="arrow-right">→</view>
              </view>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="project-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editProject(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="project-footer">
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
    <button class="add-btn" @click="addNewProject">
      <view class="icon-plus">+</view>
    </button>

    <!-- 成就详情弹窗 -->
    <view v-if="showAchievementsModal" class="achievements-modal" @click="closeAchievementsModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ currentProjectName }} - 项目成就</text>
          <button class="modal-close-btn" @click="closeAchievementsModal">×</button>
        </view>
        <view class="modal-body">
          <view class="all-achievements-list">
            <view
              v-for="(achievement, index) in currentAchievements"
              :key="index"
              class="achievement-item-modal"
            >
              <view class="achievement-index-modal">{{ index + 1 }}.</view>
              <text class="achievement-text-modal">{{ achievement }}</text>
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
import type { ProjectExperienceQuery, ProjectExperienceResult } from "@/types/project-experience";
import ProjectExperienceAPI from "@/api/project-experience";
import { usePageRefresh } from "@/composables/usePageRefresh";

// 响应式数据
const searchKeywords = ref("");
const statusIndex = ref(0);
const sortIndex = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const hasMore = ref(true);
const listData = ref<ProjectExperienceResult[]>([]);

// 成就弹窗相关
const showAchievementsModal = ref(false);
const currentAchievements = ref<string[]>([]);
const currentProjectName = ref("");

// 筛选选项
const statusOptions = ["全部状态", "未开始", "进行中", "已完成", "已暂停"];
const sortOptions = ["时间倒序", "时间正序", "创建时间", "更新时间"];

// 使用页面刷新 composable
const { refreshKey } = usePageRefresh({
  immediate: true,
  onRefresh: async () => {
    await loadListData(true);
    uni.showToast({
      title: "列表已更新",
      icon: "success",
      duration: 1500,
    });
  },
});

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

// 获取状态文本
const getStatusText = (status: number): string => {
  switch (status) {
    case 0:
      return "未开始";
    case 1:
      return "进行中";
    case 2:
      return "已完成";
    case 3:
      return "已暂停";
    default:
      return "未知";
  }
};

// 获取状态样式类
const getStatusClass = (status: number): string => {
  switch (status) {
    case 0:
      return "status-pending";
    case 1:
      return "status-in-progress";
    case 2:
      return "status-completed";
    case 3:
      return "status-paused";
    default:
      return "status-default";
  }
};

// 计算项目进度百分比
const calculateProgress = (startDate: string, endDate: string): number => {
  const start = new Date(startDate).getTime();
  const end = new Date(endDate).getTime();
  const now = new Date().getTime();

  if (now <= start) return 0;
  if (now >= end) return 100;

  const total = end - start;
  const passed = now - start;
  return Math.round((passed / total) * 100);
};

// 获取进度条样式类
const getProgressClass = (startDate: string, endDate: string): string => {
  const progress = calculateProgress(startDate, endDate);
  if (progress >= 100) return "progress-completed";
  if (progress >= 70) return "progress-late";
  if (progress >= 30) return "progress-middle";
  return "progress-early";
};

// 显示全部成就
const showAllAchievements = (achievements: string[], projectName: string) => {
  currentAchievements.value = achievements;
  currentProjectName.value = projectName;
  showAchievementsModal.value = true;
};

// 关闭成就弹窗
const closeAchievementsModal = () => {
  showAchievementsModal.value = false;
  currentAchievements.value = [];
  currentProjectName.value = "";
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  loadListData(true);
};

const clearSearch = () => {
  searchKeywords.value = "";
  currentPage.value = 1;
  loadListData(true);
};

// 筛选处理
const onStatusChange = (e: any) => {
  statusIndex.value = e.detail.value;
  currentPage.value = 1;
  loadListData(true);
};

const onSortChange = (e: any) => {
  sortIndex.value = e.detail.value;
  currentPage.value = 1;
  loadListData(true);
};

// 加载数据
const loadListData = async (reset = false) => {
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

    const query: ProjectExperienceQuery = {};

    // 添加搜索条件
    if (searchKeywords.value) {
      query.keyWords = searchKeywords.value;
    }

    // 添加状态筛选条件
    if (statusIndex.value > 0) {
      const status = statusIndex.value - 1; // 0: 未开始, 1: 进行中, 2: 已完成, 3: 已暂停
      query.status = status;
    }

    // 添加排序条件
    if (sortIndex.value >= 0) {
      let orderBy = "startDate";
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

      query.sortBy = orderBy;
      query.sortOrder = orderDirection;
    }

    // 调用API
    const response = await ProjectExperienceAPI.page(pageParam, query);

    if (response) {
      const { records = [], total = 0 } = response;

      // 确保achievements是数组格式（如果是字符串就转换为数组）
      const processedRecords = records.map(record => ({
        ...record,
        achievements: Array.isArray(record.achievements)
          ? record.achievements
          : typeof record.achievements === "string"
            ? record.achievements.split(",").map(a => a.trim()).filter(a => a)
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
  loadListData();
};

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/project/project?id=${id}`,
  });
};

const editProject = (id: number) => {
  uni.navigateTo({
    url: `/pages/project/project?id=${id}&edit=true`,
  });
};

const addNewProject = () => {
  uni.navigateTo({
    url: "/pages/project/project",
  });
};

// 生命周期
onMounted(() => {
  loadListData(true);
});

onLoad((options) => {
  // 兼容旧代码，如果通过参数传递refresh，则刷新
    loadListData(true);
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

.project-item {
  margin-bottom: $margin-base;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .project-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $margin-mini;

    .project-title-section {
      flex: 1;

      .project-name {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: 8rpx;
        display: block;
      }

      .project-badges {
        .status-badge {
          padding: 4rpx 12rpx;
          border-radius: $border-radius-small;
          font-size: $font-size-extra-small;
          font-weight: $font-weight-medium;

          &.status-pending {
            background: $info-bg;
            color: $info-color;
            border: 1rpx solid $info-border;
          }

          &.status-in-progress {
            background: $primary-color-light;
            color: $primary-color;
            border: 1rpx solid $primary-border;
          }

          &.status-completed {
            background: $success-bg;
            color: $success-color;
            border: 1rpx solid $success-border;
          }

          &.status-paused {
            background: $warning-bg;
            color: $warning-color;
            border: 1rpx solid $warning-border;
          }

          &.status-default {
            background: $background-color;
            color: $text-secondary;
            border: 1rpx solid $border-color-light;
          }
        }
      }
    }

    .project-time {
      flex-shrink: 0;
      margin-left: $margin-mini;

      .time-text {
        font-size: $font-size-small;
        color: $text-secondary;
        white-space: nowrap;
      }
    }
  }

  .project-desc {
    font-size: $font-size-small;
    color: $text-regular;
    line-height: 1.5;
    margin-bottom: $margin-base;
    padding: 12rpx;
    background: $background-color;
    border-radius: $border-radius-small;
  }

  .project-progress {
    margin-bottom: $margin-base;

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12rpx;

      .progress-label {
        font-size: $font-size-small;
        color: $text-secondary;
      }

      .progress-value {
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
      }
    }

    .progress-bar {
      height: 16rpx;
      background: $background-color;
      border-radius: 8rpx;
      overflow: hidden;
      margin-bottom: 8rpx;

      .progress-fill {
        height: 100%;
        border-radius: 8rpx;
        transition: width $transition-normal $ease-in-out;

        &.progress-early {
          background: linear-gradient(90deg, $success-color, color.adjust($success-color, $lightness: 20%));
        }

        &.progress-middle {
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness: 20%));
        }

        &.progress-late {
          background: linear-gradient(90deg, $warning-color, color.adjust($warning-color, $lightness: 20%));
        }

        &.progress-completed {
          background: linear-gradient(90deg, $info-color, color.adjust($info-color, $lightness: 20%));
        }
      }
    }

    .progress-labels {
      display: flex;
      justify-content: space-between;
      font-size: $font-size-extra-small;
      color: $text-placeholder;
    }
  }

  .project-achievements {
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
        color: $warning-color;
        background: rgba($warning-color, 0.1);
        padding: 2rpx 8rpx;
        border-radius: 10rpx;
        margin-left: auto;
      }
    }

    .achievements-list {
      .achievement-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 8rpx;

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
          line-height: 1.4;
          -webkit-line-clamp: 2;
        }
      }

      .more-achievements {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12rpx;
        background: rgba($warning-color, 0.05);
        border-radius: $border-radius-small;
        border: 1rpx dashed $warning-border;
        margin-top: 8rpx;
        cursor: pointer;

        text {
          font-size: $font-size-small;
          color: $warning-color;
        }

        .arrow-right {
          font-size: 20rpx;
          color: $warning-color;
        }
      }
    }
  }

  .project-actions {
    display: flex;
    gap: $margin-mini;
    margin: $margin-base 0;

    .action-btn {
      flex: 1;
      padding: 16rpx;
      font-size: $font-size-small;
    }
  }

  .project-footer {
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
    max-height: 80vh;
    overflow: hidden;
    box-shadow: $box-shadow-dark;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-base;
      border-bottom: 1rpx solid $border-color-extra-light;
      background: $warning-light;

      .modal-title {
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $warning-color;
      }

      .modal-close-btn {
        background: transparent;
        border: none;
        font-size: 32rpx;
        color: $warning-color;
        width: 40rpx;
        height: 40rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .modal-body {
      padding: $padding-base;
      max-height: 60vh;
      overflow-y: auto;

      .all-achievements-list {
        .achievement-item-modal {
          display: flex;
          align-items: flex-start;
          padding: 16rpx 0;
          border-bottom: 1rpx solid $border-color-extra-light;

          &:last-child {
            border-bottom: none;
          }

          .achievement-index-modal {
            width: 40rpx;
            color: $warning-color;
            font-weight: $font-weight-medium;
            font-size: $font-size-small;
          }

          .achievement-text-modal {
            flex: 1;
            font-size: $font-size-base;
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

  .project-item {
    .project-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12rpx;
    }

    .project-actions {
      flex-direction: column;
    }

    .project-footer {
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