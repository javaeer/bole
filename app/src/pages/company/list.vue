<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-container">
      <view class="search-bar" :class="{ active: showSearch }">
        <text class="icon">🔍</text>
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="搜索公司名称"
          placeholder-class="placeholder"
          @confirm="handleSearch"
          @blur="showSearch = false"
          @focus="showSearch = true"
        />
        <view v-if="searchKeywords" class="search-actions">
          <text class="icon" @click="resetSearch">×</text>
        </view>
      </view>
      <view class="search-btn" @click="handleSearch">
        搜索
      </view>
    </view>

    <!-- 排序栏 -->
    <view class="sort-container">
      <scroll-view class="sort-scroll" scroll-x>
        <view class="sort-tags">
          <view
            v-for="option in sortOptions"
            :key="option.value"
            class="sort-tag"
            :class="{
              active: listParams.sortField === option.value,
              desc: listParams.sortOrder === 'desc'
            }"
            @click="toggleSort(option.value as SortField)"
          >
            <text>{{ option.label }}</text>
            <text
              v-if="listParams.sortField === option.value"
              class="icon"
            >
              {{ listParams.sortOrder === 'asc' ? '▲' : '▼' }}
            </text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 列表区域 -->
    <scroll-view
      class="list-container"
      scroll-y
      @scrolltolower="loadMore"
    >
      <!-- 列表项 -->
      <view
        v-for="company in companyList"
        :key="company.id"
        class="company-card"
        @click="navigateToDetail(company.id)"
      >
        <view class="card-header">
          <view class="company-info">
            <view class="company-name">
              {{ company.name }}
            </view>
            <view class="company-status" :style="{ color: getStatusColor(company.deleted) }">
              {{ getStatusText(company.deleted) }}
            </view>
          </view>
          <view class="card-actions">
            <view class="follow-btn" @click.stop="toggleFollow(company)">
              <text class="icon" :style="{ color: company.isFollowed ? '$danger-color' : '$text-secondary' }">
                {{ company.isFollowed ? '❤️' : '♡' }}
              </text>
            </view>
          </view>
        </view>

        <view class="card-content">
          <view class="company-basic">
            <view class="info-row">
              <text class="icon">👤</text>
              <text class="info-text">负责人：{{ company.holder }}</text>
            </view>
            <view class="info-row">
              <text class="icon">📍</text>
              <text class="info-text">所在地：{{ company.location }}</text>
            </view>
            <view class="info-row">
              <text class="icon">✉️</text>
              <text class="info-text">{{ company.email }}</text>
            </view>
          </view>

          <view class="company-stats">
            <view class="stat-item">
              <view class="stat-label">关注者</view>
              <view class="stat-value">{{ company.followers }}</view>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <view class="stat-label">粉丝</view>
              <view class="stat-value">{{ company.fans }}</view>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <view class="stat-label">点赞</view>
              <view class="stat-value">{{ company.likes }}</view>
            </view>
          </view>
        </view>

        <view class="card-footer">
          <view class="time-info">
            <text class="time-label">创建：{{ formatDate(company.createdAt) }}</text>
            <text class="time-label">更新：{{ formatDate(company.updatedAt) }}</text>
          </view>
          <view class="detail-btn" @click.stop="navigateToDetail(company.id)">
            查看详情
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="loading-container">
        <view v-if="loading" class="loading">
          <uni-load-more status="loading" />
        </view>
        <view v-else-if="!hasMore && companyList.length > 0" class="no-more">
          <text>没有更多数据了</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && companyList.length === 0" class="empty-container">
        <text class="icon">👤</text>
        <text class="empty-text">暂无数据</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { onPullDownRefresh, onReachBottom } from "@dcloudio/uni-app";
import CompanyAPI from "@/api/company";
import { CompanyQuery, CompanyResult } from "@/types/company";

// 响应式数据
const loading = ref(false);
const refreshing = ref(false);
const hasMore = ref(true);
const searchKeywords = ref("");
const showSearch = ref(false);

// 列表参数
const listParams = reactive<PageParam>({});

const bodyParams = ref<CompanyQuery>(
  {
    keyField:"" as string,
    keyWords: "" as string,
    sortBy: "createdAt" as SortBy,
    sortOrder: "asc" as SortOrder,
  },
);

// 公司列表数据
const companyList = ref<CompanyResult[]>([]);

// 排序选项
const sortOptions = [
  { label: "创建时间", value: "createdAt" },
  { label: "修改时间", value: "updatedAt" },
  { label: "关注数", value: "followers" },
  { label: "点赞数", value: "likes" },
];

// 搜索公司名称
const handleSearch = () => {
  bodyParams.keyField = "name"
  bodyParams.keyWords = searchKeywords.value.trim();
  listParams.page = 1;
  companyList.value = [];
  loadCompanyList();
};

// 重置搜索
const resetSearch = () => {
  searchKeywords.value = "";
  bodyParams.kayword = "";
  listParams.page = 1;
  companyList.value = [];
  loadCompanyList();
};

// 切换排序方式
const toggleSort = (field: SortBy) => {
  if (bodyParams.sortBy === field) {
    bodyParams.sortOrder = bodyParams.sortOrder === "asc" ? "desc" : "asc";
  } else {
    bodyParams.sortBy = field;
    bodyParams.sortOrder = "desc";
  }
  listParams.page = 1;
  companyList.value = [];
  loadCompanyList();
};

// 获取公司列表
const loadCompanyList = async (isRefresh = false) => {
  if (loading.value && !isRefresh) return;

  loading.value = true;
  try {
    const { records, total } = await CompanyAPI.page(listParams, bodyParams);

    if (isRefresh) {
      companyList.value = records;
    } else {
      companyList.value = [...companyList.value, ...records];
    }

    // 判断是否还有更多数据
    hasMore.value = companyList.value.length < total;
    if (hasMore.value) {
      listParams.page++;
    }
  } catch (error) {
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
    if (refreshing.value) {
      refreshing.value = false;
      uni.stopPullDownRefresh();
    }
  }
};

// 上拉加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadCompanyList();
  }
};

// 下拉刷新
const handleRefresh = () => {
  refreshing.value = true;
  listParams.page = 1;
  loadCompanyList(true);
};

// 跳转到详情页
const navigateToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/company/company?id=${id}`,
  });
};

// 格式化时间
const formatDate = (dateStr: string) => {
  return dateStr.split(" ")[0];
};

// 状态颜色
const getStatusColor = (deleted: number) => {
  return deleted === 0 ? "$success-color" : "$danger-color";
};

// 状态文本
const getStatusText = (deleted: number) => {
  return deleted === 0 ? "正常" : "已删除";
};

// 初始化加载
onMounted(() => {
  loadCompanyList();
});

// 监听上拉触底
onReachBottom(() => {
  loadMore();
});

// 监听下拉刷新
onPullDownRefresh(() => {
  handleRefresh();
});
</script>

<style lang="scss">
.search-container {
  display: flex;
  align-items: center;
  padding: $padding-base;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-lighter;

  .search-bar {
    flex: 1;
    display: flex;
    align-items: center;
    background: $background-color;
    border-radius: $border-radius;
    padding: 12rpx 20rpx;
    border: 2rpx solid transparent;
    transition: all $transition-fast;

    &.active {
      border-color: $primary-color;
      background: $background-color-white;
    }

    .icon {
      margin-right: 12rpx;
      font-size: 20rpx;
    }

    .search-input {
      flex: 1;
      height: 40rpx;
      font-size: $font-size-base;
      color: $text-primary;

      .placeholder {
        color: $text-placeholder;
        font-size: $font-size-base;
      }
    }

    .search-actions {
      display: flex;
      align-items: center;
      
      .icon {
        font-size: 18rpx;
        color: $text-placeholder;
      }
    }
  }

  .search-btn {
    margin-left: 20rpx;
    padding: 12rpx 24rpx;
    background: $primary-color;
    color: white;
    border-radius: $border-radius-small;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
  }
}

.sort-container {
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-lighter;
  padding: $padding-small $padding-base;

  .sort-scroll {
    width: 100%;
    white-space: nowrap;

    .sort-tags {
      display: inline-flex;
      gap: 16rpx;

      .sort-tag {
        display: inline-flex;
        align-items: center;
        padding: 8rpx 20rpx;
        background: $background-color;
        border-radius: $border-radius-small;
        font-size: $font-size-small;
        color: $text-secondary;
        border: 1rpx solid $border-color-light;
        transition: all $transition-fast;

        &.active {
          background: $primary-color-light;
          color: $primary-color;
          border-color: $primary-color;

          &.desc {
            .icon {
              transform: rotate(180deg);
            }
          }
        }

        .icon {
          margin-left: 4rpx;
          transition: transform $transition-fast;
          font-size: 14rpx;
        }
      }
    }
  }
}

.list-container {
  height: calc(100vh - 240rpx);
  padding: $padding-base;

  .company-card {
    background: $background-color-white;
    border-radius: $card-border-radius;
    margin-bottom: $margin-base;
    box-shadow: $card-shadow;
    overflow: hidden;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-base;
      border-bottom: 1rpx solid $border-color-lighter;

      .company-info {
        flex: 1;

        .company-name {
          font-size: $font-size-medium;
          font-weight: $font-weight-semibold;
          color: $text-primary;
          margin-bottom: 8rpx;
        }

        .company-status {
          font-size: $font-size-small;
        }
      }

      .card-actions {
        .follow-btn {
          width: 60rpx;
          height: 60rpx;
          @extend .flex-center;
          border-radius: $border-radius-round;
          background: $background-color;
          
          .icon {
            font-size: 20rpx;
          }
        }
      }
    }

    .card-content {
      padding: $padding-base;

      .company-basic {
        .info-row {
          display: flex;
          align-items: center;
          margin-bottom: 12rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .icon {
            margin-right: 12rpx;
            font-size: 16rpx;
            color: $text-secondary;
          }

          .info-text {
            font-size: $font-size-small;
            color: $text-regular;
          }
        }
      }

      .company-stats {
        display: flex;
        align-items: center;
        margin-top: $margin-small;
        padding-top: $padding-small;
        border-top: 1rpx solid $border-color-lighter;

        .stat-item {
          flex: 1;
          text-align: center;

          .stat-label {
            font-size: $font-size-extra-small;
            color: $text-secondary;
            margin-bottom: 4rpx;
          }

          .stat-value {
            font-size: $font-size-medium;
            font-weight: $font-weight-semibold;
            color: $text-primary;
          }
        }

        .stat-divider {
          width: 1rpx;
          height: 40rpx;
          background: $border-color-lighter;
        }
      }
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-small $padding-base;
      background: $background-color;
      border-top: 1rpx solid $border-color-lighter;

      .time-info {
        display: flex;
        flex-direction: column;
        gap: 4rpx;

        .time-label {
          font-size: $font-size-extra-small;
          color: $text-secondary;
        }
      }

      .detail-btn {
        padding: 8rpx 20rpx;
        background: $primary-color;
        color: white;
        border-radius: $border-radius-small;
        font-size: $font-size-small;
        font-weight: $font-weight-medium;
      }
    }
  }

  .loading-container {
    padding: $padding-large 0;

    .loading, .no-more {
      text-align: center;
      color: $text-secondary;
      font-size: $font-size-small;
    }
  }

  .empty-container {
    @extend .flex-center;
    flex-direction: column;
    padding: 100rpx 0;

    .icon {
      font-size: 80rpx;
      color: $empty-text-color;
    }

    .empty-text {
      margin-top: 20rpx;
      font-size: $font-size-base;
      color: $empty-text-color;
    }
  }
}

// 图标样式
.icon {
  display: inline-block;
  line-height: 1;
}
</style>