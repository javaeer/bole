<template>
  <view class="page-container resume-list-page">
    <!-- 顶部搜索栏 -->
    <view class="search-header">
      <view class="status-bar"></view>
      <!-- 搜索框 -->
      <view class="search-box">
        <text class="icon">🔍</text>
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

    <!-- 筛选操作栏 -->
    <view class="filter-bar">
      <view class="sort-dropdown" @click="showSortPanel = !showSortPanel">
        <text>{{ currentSort.label }}</text>
        <text class="icon">{{ showSortPanel ? "▲" : "▼" }}</text>
      </view>
    </view>

    <!-- 排序面板 -->
    <view v-if="showSortPanel" class="sort-panel card-container">
      <text class="panel-title">排序方式</text>
      <view class="sort-options">
        <view
          v-for="option in sortOptions"
          :key="option.value"
          :class="['sort-option', { active: sortBy === option.value }]"
          @click="onSortChange(option.value)"
        >
          <text class="option-text">{{ option.label }}</text>
          <text v-if="sortBy === option.value" class="icon">✓</text>
        </view>
      </view>
    </view>

    <!-- 简历列表 -->
    <scroll-view
      class="resume-list"
      scroll-y
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="loading && resumeList.length === 0" class="loading-container">
        <view class="loading-spinner"></view>
        <text>加载中...</text>
      </view>

      <view v-else-if="filteredResumes.length === 0" class="empty-state">
        <text class="icon">📄</text>
        <text class="empty-text">暂无简历</text>
        <text v-if="searchText" class="empty-hint">未找到匹配的简历</text>
      </view>

      <view v-else>
        <view
          v-for="resume in filteredResumes"
          :key="resume.id"
          class="resume-item card-container"
          @click="goToDetail(resume)"
        >
          <view class="resume-header flex-between">
            <view class="resume-title-section">
              <text class="resume-name">{{ resume.name || "未命名简历" }}</text>
              <view class="resume-meta">
                <text class="meta-item">
                  <text class="icon">📅</text>
                  {{ formatDate(resume.createdAt) }}
                </text>
                <text class="meta-item">
                  <text class="icon">🔄</text>
                  {{ formatDate(resume.updatedAt) }}
                </text>
              </view>
            </view>
          </view>

          <view class="resume-content">
            <text class="template-name">
              模板名称：{{resume.template?.name || '未知模板'}}
            </text>

            <text class="section-count">
              包含 {{ resume.components?.length || 0 }} 个模块
            </text>

            <view class="component-tags">
              <text
                v-for="(component, index) in getTopComponents(resume.components, 3)"
                :key="index"
                class="component-tag"
              >
                {{ component.name }}
              </text>
              <text v-if="resume.components?.length > 3" class="more-tag">
                +{{ resume.components.length - 3 }}个
              </text>
            </view>
          </view>

          <view class="resume-actions flex-between">
            <view class="view-stats">
              <text class="stat-item">
                <text class="icon">👁️</text>
                {{ resume.viewCount || 0 }} 次查看
              </text>
              <text class="stat-item">
                <text class="icon">⬇️</text>
                {{ resume.downloadCount || 0 }} 次下载
              </text>
            </view>

            <view class="action-buttons">
              <button
                class="btn btn-secondary"
                size="mini"
                @click.stop="editResume(resume)"
              >
                编辑
              </button>
              <button
                class="btn btn-primary"
                size="mini"
                @click.stop="goToDetail(resume)"
              >
                查看
              </button>
            </view>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="loadingMore" class="load-more">
          <text>加载中...</text>
        </view>

        <view v-if="noMore" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </scroll-view>

    <!-- 创建按钮 -->
    <view class="fab-container">
      <button class="fab-btn btn-primary" @click="createNewResume">
        <text class="icon">➕</text>
        <text>新建简历</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import { ResumesQuery, ResumesResult } from "@/types/resumes";
import ResumesAPI from "@/api/resumes";
import { usePageRefresh } from "@/composables/usePageRefresh";

// 响应式数据
const searchText = ref("");
const isSearchFocused = ref(false);
const showSortPanel = ref(false);
const activeFilter = ref("all");
const sortBy = ref("updatedAt");
const loading = ref(true);
const loadingMore = ref(false);
const refreshing = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const resumeList = ref<ResumesResult[]>([]);
const searchTimer = ref<number | null>(null);

// 排序选项
const sortOptions = [
  { label: "最近更新", value: "updatedAt" },
  { label: "最近创建", value: "createdAt" },
  { label: "名称 A-Z", value: "nameAsc" },
  { label: "名称 Z-A", value: "nameDesc" },
  { label: "查看最多", value: "viewCount" },
  { label: "下载最多", value: "downloadCount" },
];

// 使用页面刷新 composable
const { refreshKey, refreshing: composableRefreshing } = usePageRefresh({
  immediate: true,
  onRefresh: async () => {
    await fetchResumes(1, true);
    uni.showToast({
      title: '简历列表已更新',
      icon: 'success',
      duration: 1500
    });
  }
});

// 当前排序方式
const currentSort = computed(() => {
  return sortOptions.find(option => option.value === sortBy.value) || sortOptions[0];
});

// 过滤后的简历列表
const filteredResumes = computed(() => {
  let filtered = [...resumeList.value];

  // 搜索过滤
  if (searchText.value) {
    filtered = filtered.filter(resume => {
      const searchLower = searchText.value.toLowerCase();
      return (
        resume.name?.toLowerCase().includes(searchLower) ||
        resume.components?.some(comp =>
          comp.name.toLowerCase().includes(searchLower),
        )
      );
    });
  }

  // 状态过滤
  if (activeFilter.value !== "all") {
    filtered = filtered.filter(resume => resume.status === activeFilter.value);
  }

  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case "updatedAt":
        return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
      case "createdAt":
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      case "nameAsc":
        return (a.name || "").localeCompare(b.name || "");
      case "nameDesc":
        return (b.name || "").localeCompare(a.name || "");
      case "viewCount":
        return (b.viewCount || 0) - (a.viewCount || 0);
      case "downloadCount":
        return (b.downloadCount || 0) - (a.downloadCount || 0);
      default:
        return 0;
    }
  });

  return filtered;
});

// 是否没有更多数据
const noMore = computed(() => {
  return resumeList.value.length >= total.value;
});

// 搜索相关方法
const handleInput = (event: any) => {
  searchText.value = event.detail.value;

  // 清除之前的定时器
  if (searchTimer.value) {
    clearTimeout(searchTimer.value);
  }

  // 设置新的定时器（防抖）
  searchTimer.value = setTimeout(() => {
    performSearch();
  }, 500);
};

const handleFocus = () => {
  isSearchFocused.value = true;
};

const handleBlur = () => {
  isSearchFocused.value = false;
};

const handleSearch = () => {
  performSearch();
};

const handleClear = () => {
  searchText.value = "";
  performSearch();
};

const performSearch = () => {
  if (searchTimer.value) {
    clearTimeout(searchTimer.value);
  }
  fetchResumes(1, true);
};

// 获取简历列表
const fetchResumes = async (page = 1, isRefresh = false) => {
  if (page === 1) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }

  try {
    // 构建分页参数
    const pageParam = {
      page: page,
      size: pageSize.value,
    };

    // 构建查询参数
    const query: ResumesQuery = {};

    // 添加搜索关键词
    if (searchText.value.trim()) {
      query.keyword = searchText.value.trim();
    }

    // 添加状态筛选（排除"all"）
    if (activeFilter.value !== "all") {
      query.status = activeFilter.value;
    }

    // 添加排序条件
    switch (sortBy.value) {
      case "updatedAt":
        query.orderBy = "updatedAt";
        query.orderDirection = "DESC";
        break;
      case "createdAt":
        query.orderBy = "createdAt";
        query.orderDirection = "DESC";
        break;
      case "nameAsc":
        query.orderBy = "name";
        query.orderDirection = "ASC";
        break;
      case "nameDesc":
        query.orderBy = "name";
        query.orderDirection = "DESC";
        break;
      case "viewCount":
        query.orderBy = "viewCount";
        query.orderDirection = "DESC";
        break;
      case "downloadCount":
        query.orderBy = "downloadCount";
        query.orderDirection = "DESC";
        break;
    }

    // 调用真实API
    const response = await ResumesAPI.page(pageParam, query);

    // 根据实际API响应结构调整
    const data = response;

    if (data && data.records) {
      const resumes = data.records;

      if (isRefresh || page === 1) {
        resumeList.value = resumes;
      } else {
        resumeList.value.push(...resumes);
      }

      total.value = data.total || resumes.length;
      currentPage.value = page;
    } else {
      // 如果API返回的是数组格式
      const resumes = Array.isArray(data) ? data : [];
      if (isRefresh || page === 1) {
        resumeList.value = resumes;
      } else {
        resumeList.value.push(...resumes);
      }
      total.value = resumes.length;
    }

  } catch (error) {
    console.error("获取简历列表失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
    loadingMore.value = false;
    refreshing.value = false;
  }
};

const onFilterChange = (filter: string) => {
  activeFilter.value = filter;
  showSortPanel.value = false;
  fetchResumes(1, true);
};

const onSortChange = (value: string) => {
  sortBy.value = value;
  showSortPanel.value = false;
  fetchResumes(1, true);
};

const loadMore = () => {
  if (loadingMore.value || noMore.value) return;
  fetchResumes(currentPage.value + 1);
};

const onRefresh = () => {
  refreshing.value = true;
  fetchResumes(1, true);
};

const formatDate = (dateString: string) => {
  if (!dateString) return "-";

  try {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();

    // 今天
    if (diff < 24 * 60 * 60 * 1000) {
      return date.getHours().toString().padStart(2, "0") + ":" +
        date.getMinutes().toString().padStart(2, "0");
    }

    // 今年
    if (date.getFullYear() === now.getFullYear()) {
      return `${(date.getMonth() + 1).toString().padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")}`;
    }

    return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")}`;
  } catch (error) {
    return dateString;
  }
};

const getTopComponents = (components: any[], count: number) => {
  return components?.slice(0, count) || [];
};

const goToDetail = (resume: ResumesResult) => {
  uni.navigateTo({
    url: `/pages/resumes/resumes?resumeId=${resume.id}`,
  });
};

const editResume = (resume: ResumesResult) => {
  uni.navigateTo({
    url: `/pages/resumes/resumes?resumeId=${resume.id}&mode=edit`,
  });
};

const createNewResume = () => {
  uni.navigateTo({
    url: "/pages/template/select",
  });
};

// 监听页面返回事件
const onResumeListRefresh = () => {
  fetchResumes(1, true);
};

// 手动监听事件（作为备选方案）
onMounted(() => {
  // 监听从详情页返回的刷新事件
  uni.$on('refresh:resume:list', onResumeListRefresh);
});

onUnmounted(() => {
  // 清理事件监听
  uni.$off('refresh:resume:list', onResumeListRefresh);
});

// 生命周期
onLoad((options: any) => {
  // 兼容旧的刷新方式
  if (options?.refresh === 'true') {
    fetchResumes(1, true);
  } else {
    fetchResumes();
  }
});

onReachBottom(() => {
  loadMore();
});
</script>

<style lang="scss">
/* 样式部分保持不变 */

// 通用类
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn {
  border: none;
  border-radius: $border-radius-small;
  padding: 0 24rpx;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  line-height: 1;
  transition: all $transition-fast $ease-in-out;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  &:active {
    opacity: 0.8;
    transform: scale(0.98);
  }

  &.btn-primary {
    background: $primary-color;
    color: white;
  }

  &.btn-secondary {
    background: $background-color;
    color: $text-primary;
    border: 1rpx solid $border-color-light;
  }

  &[disabled] {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &[size="mini"] {
    font-size: $font-size-extra-small;
    padding: 8rpx 16rpx;
    height: 50rpx;
  }
}

.resume-list-page {
  padding: 0;
  background: $background-color;
  min-height: 100vh;
}

.search-header {
  position: sticky;
  top: 0;
  z-index: $z-index-base + 100;
  background: $background-color-white;
  padding: 16rpx $padding-base;
  display: flex;
  align-items: center;
  gap: 16rpx;
  border-bottom: 1rpx solid $border-color-light;

  .status-bar {
    height: var(--status-bar-height);
  }

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    background: $background-color;
    border-radius: $border-radius;
    padding: 16rpx 20rpx;
    border: 1rpx solid $border-color-light;
    transition: all $transition-fast $ease-in-out;

    &:focus-within {
      border-color: $primary-color;
      box-shadow: $input-focus-shadow;
    }

    .icon {
      font-size: $font-size-base;
      color: $text-secondary;
      margin-right: 12rpx;
    }

    .search-input {
      flex: 1;
      font-size: $font-size-base;
      color: $text-primary;
      background: transparent;
      height: 40rpx;
      line-height: 40rpx;
    }

    .placeholder {
      color: $text-placeholder;
      font-size: $font-size-base;
    }

    .clear-btn {
      padding: 8rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .clear-icon {
        font-size: 24rpx;
        color: $text-secondary;
        font-weight: bold;
        line-height: 1;
      }
    }
  }

  .search-btn {
    background: $primary-color;
    color: white;
    border: none;
    border-radius: $border-radius;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    padding: 0 24rpx;
    height: 72rpx;
    line-height: 72rpx;
    transition: all $transition-fast $ease-in-out;

    &:disabled {
      background: $background-color;
      color: $text-placeholder;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      background: color.adjust($primary-color, $lightness: -10%);
      transform: scale(0.98);
    }
  }
}

.filter-bar {
  display: flex;
  align-items: center;
  padding: 16rpx $padding-base;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-light;

  .filter-tabs {
    flex: 1;
    overflow: hidden;

    .tab-scroll {
      width: 100%;
      white-space: nowrap;

      .tab-container {
        display: inline-flex;
        gap: 32rpx;

        .tab-item {
          position: relative;
          font-size: $font-size-base;
          color: $text-secondary;
          padding: 8rpx 4rpx;
          white-space: nowrap;
          transition: all $transition-fast $ease-in-out;

          &.active {
            color: $primary-color;
font-weight: $font-weight-medium;

            &::after {
              content: '';
              position: absolute;
              bottom: 0;
              left: 0;
              right: 0;
              height: 4rpx;
              background: $primary-color;
              border-radius: 2rpx;
            }
          }

          .tab-badge {
            position: absolute;
            top: -8rpx;
            right: -8rpx;
            background: $danger-color;
            color: white;
            font-size: $font-size-extra-small;
            padding: 2rpx 6rpx;
            border-radius: 10rpx;
            min-width: 20rpx;
            text-align: center;
          }
        }
      }
    }
  }

  .sort-dropdown {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 8rpx 16rpx;
    background: $background-color;
    border-radius: $border-radius-small;
    font-size: $font-size-small;
    color: $text-regular;
    white-space: nowrap;
  }
}

.sort-panel {
  position: absolute;
  top: 180rpx;
  left: $padding-base;
  right: $padding-base;
  z-index: $z-index-dropdown;
  padding: $padding-base;
  box-shadow: $box-shadow-dark;
  border-radius: $border-radius;
  background: $background-color-white;

  .panel-title {
    display: block;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-primary;
    margin-bottom: $margin-base;
  }

  .sort-options {
    .sort-option {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid $border-color-lighter;

      &:last-child {
        border-bottom: none;
      }

      &.active {
        .option-text {
          color: $primary-color;
          font-weight: $font-weight-medium;
        }
      }

      .option-text {
        font-size: $font-size-base;
        color: $text-regular;
      }
    }
  }
}

.resume-list {
  height: calc(100vh - 300rpx);
  padding: $margin-base;
}

// 加载状态样式
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $margin-base * 2;
  color: $text-placeholder;

  .loading-spinner {
    width: 40rpx;
    height: 40rpx;
    border: 4rpx solid rgba($primary-color, 0.2);
    border-top-color: $primary-color;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: $margin-base;
  }
}

// 空状态样式
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $margin-base * 3;
  color: $text-placeholder;

  .icon {
    font-size: 80rpx;
    margin-bottom: $margin-base;
  }

  .empty-text {
    font-size: $font-size-base;
    margin-bottom: $margin-mini;
  }

  .empty-hint {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.resume-item {
  margin-bottom: $margin-base;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .resume-header {
    margin-bottom: $margin-base;

    .resume-title-section {
      .resume-name {
        display: block;
        font-size: $font-size-large;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: $margin-mini;
        @extend .text-ellipsis;
        max-width: 400rpx;
      }

      .resume-meta {
        display: flex;
        gap: $margin-base;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 4rpx;
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }
    }

    .resume-status {
      .status-badge {
        padding: 4rpx 12rpx;
        font-size: $font-size-extra-small;
        border-radius: $border-radius-small;

        &.status-draft {
          background: $warning-bg;
          color: $warning-color;
          border: 1rpx solid $warning-border;
        }

        &.status-published {
          background: $success-bg;
          color: $success-color;
          border: 1rpx solid $success-border;
        }

        &.status-archived {
          background: $info-bg;
          color: $info-color;
          border: 1rpx solid $info-border;
        }
      }
    }
  }

  .resume-content {
    margin-bottom: $margin-base;

    .template-name {
      display: block;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin-bottom: $margin-mini;
    }

    .section-count {
      display: block;
      font-size: $font-size-small;
      color: $text-secondary;
      margin-bottom: $margin-mini;
    }

    .component-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $margin-mini;

      .component-tag,
      .more-tag {
        padding: 4rpx 12rpx;
        font-size: $font-size-extra-small;
        color: $text-regular;
        background: $background-color;
        border-radius: $border-radius-small;
        border: 1rpx solid $border-color-light;
      }
    }
  }

  .resume-actions {
    .view-stats {
      display: flex;
      gap: $margin-base;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 4rpx;
        font-size: $font-size-extra-small;
        color: $text-secondary;
      }
    }

    .action-buttons {
      display: flex;
      gap: $margin-mini;

      .btn {
        min-width: 100rpx;
        height: 50rpx;
        line-height: 50rpx;
        padding: 0 16rpx;
        font-size: $font-size-small;
        @extend .btn;
        @extend .btn-small;
      }
    }
  }
}

.load-more,
.no-more {
  text-align: center;
  padding: $margin-base;
  color: $text-secondary;
  font-size: $font-size-small;
}

.fab-container {
  position: fixed;
  bottom: calc(env(safe-area-inset-bottom) + 40rpx);
  right: 40rpx;

  .fab-btn {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 16rpx 24rpx;
    border-radius: 50rpx;
    box-shadow: $box-shadow-dark;
    background: $primary-color;
    color: white;
    border: none;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;

    .icon {
      font-size: $font-size-base;
    }
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// 响应式调整
@media (max-width: $screen-md) {
  .search-header {
    padding: 16rpx $margin-small;
  }

  .filter-bar {
    padding: 16rpx $margin-small;
  }

  .sort-panel {
    left: $margin-small;
    right: $margin-small;
  }

  .resume-list {
    padding: $margin-small;
  }
}
</style>