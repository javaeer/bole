<template>
  <view class="page-container">
    <!-- 搜索和筛选栏 -->
    <view class="filter-container card-container">
      <!-- 搜索框 -->
      <view class="search-box">
        <uni-icons type="search" size="20" color="#999" />
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="搜索项目名称或描述"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <uni-icons type="clear" size="18" color="#999" />
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
        <uni-icons type="info" size="60" color="#c0c4cc" />
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

          <!-- 成就 -->
          <view class="project-achievements" v-if="item.achievements">
            <view class="achievements-header">
              <uni-icons type="star-filled" size="16" color="#e6a23c" />
              <text class="achievements-title">项目成就</text>
            </view>
            <text class="achievements-text text-multi-truncate">{{ item.achievements }}</text>
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
    <button class="add-btn" @click="addNewProject">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import { ProjectExperienceItem } from "@/types/project-experience";
import { dateUtils } from "@/utils/date";


// 响应式数据
const searchKeywords = ref("");
const statusIndex = ref(0);
const sortIndex = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const hasMore = ref(true);

// 模拟数据
const mockData: ProjectExperienceItem[] = [
  {
    id: 1,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-18 10:30:25",
    deleted: 0,
    userId: 1,
    name: "分布式消息队列系统",
    status: 2,
    startDate: "2022-01-01",
    endDate: "2022-12-31",
    description: "设计并实现高可用分布式消息队列，支持千万级消息吞吐，保证数据不丢失。",
    achievements: "系统稳定运行一年，处理消息超过10亿条，获得公司技术创新奖",
    sort: 1,
  },
  {
    id: 2,
    createdAt: "2025-12-16 09:15:30",
    updatedAt: "2025-12-17 14:20:45",
    deleted: 0,
    userId: 1,
    name: "智能客服系统",
    status: 1,
    startDate: "2023-03-01",
    endDate: "2023-12-31",
    description: "基于自然语言处理的智能客服系统，实现自动问答和工单流转。",
    achievements: "客服效率提升40%，用户满意度达到95%",
    sort: 2,
  },
  {
    id: 3,
    createdAt: "2025-12-15 16:45:20",
    updatedAt: "2025-12-16 11:10:10",
    deleted: 0,
    userId: 1,
    name: "大数据分析平台",
    status: 2,
    startDate: "2021-06-01",
    endDate: "2022-02-28",
    description: "构建企业级大数据分析平台，集成数据采集、清洗、分析和可视化功能。",
    achievements: "支撑公司10+个业务线的数据分析需求，数据查询效率提升5倍",
    sort: 3,
  },
  {
    id: 4,
    createdAt: "2025-12-14 13:25:40",
    updatedAt: "2025-12-15 09:45:15",
    deleted: 0,
    userId: 1,
    name: "微服务架构重构",
    status: 1,
    startDate: "2023-01-15",
    endDate: "2023-10-31",
    description: "将单体应用拆分为微服务架构，提高系统可维护性和扩展性。",
    achievements: "系统可用性从99.5%提升到99.9%，部署时间从小时级降到分钟级",
    sort: 4,
  },
  {
    id: 5,
    createdAt: "2025-12-13 11:30:50",
    updatedAt: "2025-12-14 15:20:30",
    deleted: 0,
    userId: 1,
    name: "移动端跨平台开发框架",
    status: 3,
    startDate: "2022-08-01",
    endDate: "2023-03-31",
    description: "研发跨平台移动应用开发框架，支持一次编写多端运行。",
    achievements: "框架支持Android和iOS，开发效率提升30%",
    sort: 5,
  },
  {
    id: 6,
    createdAt: "2025-12-12 10:20:30",
    updatedAt: "2025-12-13 14:15:25",
    deleted: 0,
    userId: 1,
    name: "云原生监控系统",
    status: 0,
    startDate: "2024-01-01",
    endDate: "2024-06-30",
    description: "构建面向云原生环境的监控系统，实现资源监控、告警和可视化。",
    achievements: null,
    sort: 6,
  },
];

const listData = ref<ProjectExperienceItem[]>([]);
const filteredData = ref<ProjectExperienceItem[]>([]);

// 筛选选项
const statusOptions = ["全部状态", "未开始", "进行中", "已完成", "已暂停"];
const sortOptions = ["时间倒序", "时间正序", "创建时间", "更新时间"];

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
  sortData();
};

// 排序数据
const sortData = () => {
  switch (sortIndex.value) {
    case 0: // 时间倒序
      filteredData.value.sort((a, b) =>
        new Date(b.startDate).getTime() - new Date(a.startDate).getTime(),
      );
      break;
    case 1: // 时间正序
      filteredData.value.sort((a, b) =>
        new Date(a.startDate).getTime() - new Date(b.startDate).getTime(),
      );
      break;
    case 2: // 创建时间
      filteredData.value.sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
      break;
    case 3: // 更新时间
      filteredData.value.sort((a, b) =>
        new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
      );
      break;
  }

  // 更新分页数据
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  listData.value = filteredData.value.slice(start, end);
};

// 加载数据
const loadData = (reset = false) => {
  if (loading.value) return;

  loading.value = true;

  if (reset) {
    currentPage.value = 1;
    hasMore.value = true;
    listData.value = [];
  }

  // 模拟API请求延迟
  setTimeout(() => {
    // 筛选数据
    let filtered = [...mockData];

    // 关键字搜索
    if (searchKeywords.value) {
      const keyword = searchKeywords.value.toLowerCase();
      filtered = filtered.filter(item =>
        item.name.toLowerCase().includes(keyword) ||
        item.description.toLowerCase().includes(keyword) ||
        (item.achievements && item.achievements.toLowerCase().includes(keyword)),
      );
    }

    // 状态筛选
    if (statusIndex.value > 0) {
      const status = statusIndex.value - 1; // 0: 未开始, 1: 进行中, 2: 已完成, 3: 已暂停
      filtered = filtered.filter(item => item.status === status);
    }

    filteredData.value = filtered;

    // 排序
    sortData();

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filtered.length;
    loading.value = false;
    currentPage.value++;
  }, 500);
};

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return;

  // 模拟API请求延迟
  loading.value = true;
  setTimeout(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    const pageData = filteredData.value.slice(start, end);

    listData.value = [...listData.value, ...pageData];

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filteredData.value.length;
    loading.value = false;
    currentPage.value++;
  }, 500);
};

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/project/detail?id=${id}`,
  });
};

const editProject = (id: number) => {
  uni.navigateTo({
    url: `/pages/project/detail?id=${id}&edit=true`,
  });
};

const addNewProject = () => {
  uni.navigateTo({
    url: "/pages/project/detail",
  });
};

// 生命周期
onMounted(() => {
  loadData(true);
});

onLoad((options) => {
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
          background: linear-gradient(90deg, $success-color, color.adjust($success-color, $lightness:  20%));
        }

        &.progress-middle {
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness:  20%));
        }

        &.progress-late {
          background: linear-gradient(90deg, $warning-color, color.adjust($warning-color, $lightness:  20%));
        }

        &.progress-completed {
          background: linear-gradient(90deg, $info-color, color.adjust($info-color, $lightness:  20%));
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
}
</style>