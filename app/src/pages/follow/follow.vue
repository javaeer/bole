<template>
  <view class="page-container">
    <!-- 模板列表 -->
    <view class="content">
      <!-- 加载状态 -->
      <view v-if="templateStore.loading && templateList.length === 0" class="loading-container">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 正常状态 -->
      <template v-else>
        <!-- 下拉刷新区域 -->
        <scroll-view
          scroll-y
          class="scroll-view"
          @scrolltolower="handleLoadMore"
          refresher-enabled
          :refresher-triggered="refreshing"
          @refresherrefresh="handleRefresh"
        >
          <view class="template-grid">
            <view
              class="template-item card-container"
              v-for="template in templateList"
              :key="template.id"
              @click="handleViewDetail(template)"
            >
              <!-- 卡片内容区域 -->
              <view class="template-content">
                <image
                  :src="template.previewImage"
                  class="template-cover"
                  mode="aspectFill"
                  @error="handleImageError(template)"
                />
                <view class="template-info">
                  <text class="template-name text-truncate">{{ template.name }}</text>
                  <view class="template-meta">
                    <text class="template-type tag">{{ template.type }}</text>
                    <text v-if="template.downloads" class="template-stats">
                      <text class="stat-icon">👁️</text>
                      <text class="stat-count">{{ template.downloads }}次使用</text>
                    </text>
                  </view>
                </view>
              </view>

              <!-- 操作按钮区域 -->
              <view class="template-actions flex-between" @click.stop="handleActionClick">
                <button
                  class="btn-icon btn-icon-danger"
                  @click.stop="handleUnfavoriteTemplate(template.id)"
                >
                  <text class="btn-text hidden-sm">取消收藏</text>
                  <text class="btn-text-sm visible-sm">取消</text>
                </button>
                <button
                  class="btn btn-primary"
                  @click.stop="handleUseTemplate(template.id)"
                >
                  <text class="btn-text hidden-sm">使用模板</text>
                  <text class="btn-text-sm visible-sm">使用</text>
                </button>
              </view>
            </view>
          </view>

          <!-- 加载更多 -->
          <view v-if="templateStore.hasMore && templateList.length > 0" class="load-more">
            <text v-if="!loadingMore" class="load-more-text">上拉加载更多</text>
            <view v-else class="loading-more">
              <view class="loading-dots"></view>
              <text>加载中...</text>
            </view>
          </view>

          <!-- 没有更多 -->
          <view v-if="!templateStore.hasMore && templateList.length > 0" class="no-more">
            <text class="no-more-text">没有更多模板了</text>
          </view>
        </scroll-view>

        <!-- 空状态 -->
        <view v-if="templateList.length === 0 && !templateStore.loading" class="empty-state">
          <text class="empty-icon">📄</text>
          <text class="empty-text">还没有收藏任何模板</text>
        </view>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, computed } from "vue";
import { useAuthGuard } from "@/composables/useAuthGaurd";
import { useTemplateStore } from "@/stores/template";
import { onShow } from "@dcloudio/uni-app";
import { EventKey } from "@/constants/event-key";
import TemplateAPI from "@/api/template";
import type { TemplateQuery, TemplateResult } from "@/types/template";

const auth = useAuthGuard();
const templateStore = useTemplateStore();

// 状态变量
const loadingMore = ref(false);
const refreshing = ref(false);

// 计算属性获取模板列表
const templateList = computed(() => templateStore.templateList);

// 重置分页参数
const resetPageParams = () => {
  templateStore.pageParams.page = 1;
  templateStore.pageParams.total = 0;
  templateStore.hasMore = true;
};

// 图片加载失败处理
const handleImageError = (template: any) => {
  console.warn(`模板 ${template.name} 封面图片加载失败`);
  template.previewImage = "/static/template/default.jpg";
};

// 查看模板详情
const handleViewDetail = (template: TemplateResult) => {
  uni.navigateTo({
    url: `/pages/template/detail?id=${template.id}`,
  });
};

// 操作按钮点击处理（防止冒泡）
const handleActionClick = (e: Event) => {
  e.stopPropagation();
};

// 取消收藏模板
const handleUnfavoriteTemplate = async (id: number) => {
  uni.showModal({
    title: "确认操作",
    content: "确定要取消收藏这个模板吗？",
    success: async (res) => {
      if (res.confirm) {
        try {
          // 调用取消收藏 API
          await TemplateAPI.unfavorite(id);

          // 从列表中移除该模板
          const index = templateList.value.findIndex(template => template.id === id);
          if (index !== -1) {
            // 更新 store 中的列表
            templateStore.templateList.splice(index, 1);
          }

          // 如果列表为空，重新加载第一页
          if (templateList.value.length === 0) {
            resetPageParams();
            await loadCollectedTemplates();
          }

          uni.showToast({
            title: "取消收藏成功",
            icon: "success",
          });
        } catch (error) {
          console.error("取消收藏失败:", error);
          uni.showToast({
            title: "操作失败，请重试",
            icon: "error",
          });
        }
      }
    },
  });
};

// 使用模板
const handleUseTemplate = (id: number) => {
  uni.navigateTo({
    url: `/pages/resumes/edit?templateId=${id}`,
  });
};

// 加载收藏的模板 - 复用 store 的方法
const loadCollectedTemplates = async (reset = false) => {
  try {
    if (reset) {
      resetPageParams();
    }

    // 构建查询条件：只获取收藏的模板
    const query: TemplateQuery = { collected: true };

    // 直接使用 store 的 loadTemplateList 方法
    await templateStore.loadTemplateList(query);

  } catch (error: any) {
    console.error("加载收藏模板失败:", error);
    uni.showToast({
      title: error.message || "加载失败，请稍后重试",
      icon: "error",
    });
  } finally {
    loadingMore.value = false;
    refreshing.value = false;
  }
};

// 加载更多
const handleLoadMore = async () => {
  if (loadingMore.value || !templateStore.hasMore) return;

  loadingMore.value = true;

  // 增加页码
  templateStore.pageParams.page++;

  try {
    const query: TemplateQuery = { collected: true };
    await templateStore.loadTemplateList(query);
  } catch (error) {
    console.error("加载更多失败:", error);
    // 如果加载失败，回退页码
    templateStore.pageParams.page--;
  } finally {
    loadingMore.value = false;
  }
};

// 下拉刷新
const handleRefresh = async () => {
  refreshing.value = true;
  await loadCollectedTemplates(true);
};

// 初始化加载
onMounted(async () => {
  await loadCollectedTemplates(true);
});

// 页面显示时刷新数据
onShow(() => {
  // 如果页面参数被重置，则重新加载
  if (templateStore.pageParams.page === 1) {
    loadCollectedTemplates(true);
  }
});

// 监听模板更新事件
onMounted(() => {
  uni.$on(EventKey.TEMPLATE_UPDATED_EVENT, () => {
    loadCollectedTemplates(true);
  });
});

onUnmounted(() => {
  uni.$off(EventKey.TEMPLATE_UPDATED_EVENT);
});
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100vh;
  background-color: $background-color;
}

.content {
  padding: $padding-base;
  min-height: 400rpx;
  height: 100vh;
  box-sizing: border-box;

  /* 响应式间距 */
  @media (max-width: $breakpoint-sm) {
    padding: $padding-small;
  }
}

.scroll-view {
  height: 100%;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  height: 100%;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: $border-radius-round;
  margin-bottom: $margin-base;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: $font-size-base;
  color: $text-secondary;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 模板网格 - 响应式设计 */
.template-grid {
  display: grid;
  gap: $margin-base;

  /* 默认：小屏幕 - 1列 */
  grid-template-columns: 1fr;

  /* 中等屏幕 - 2列 */
  @media (min-width: $screen-sm) {
    grid-template-columns: repeat(2, 1fr);
  }

  /* 大屏幕 - 3列 */
  @media (min-width: $screen-lg) {
    grid-template-columns: repeat(3, 1fr);
  }

  /* 超大屏幕 - 4列 */
  @media (min-width: $screen-xl) {
    grid-template-columns: repeat(4, 1fr);
  }

  /* 响应式间距 */
  @media (max-width: $breakpoint-sm) {
    gap: $margin-small;
  }
}

.template-item {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  overflow: hidden;
  transition: transform $transition-fast $ease-in-out,
  box-shadow $transition-fast $ease-in-out;
  min-height: 0; /* 确保flex布局正确 */

  &:active {
    transform: translateY(2rpx);
    box-shadow: $box-shadow-light !important;
  }

  /* 响应式高度调整 */
  @media (max-width: $breakpoint-sm) {
    min-height: 320rpx;
  }
}

.template-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 重要：防止内容溢出 */
}

.template-cover {
  width: 100%;
  height: 240rpx;
  background: linear-gradient(135deg, $primary-color-light 0%, transparent 100%);
  object-fit: cover;
  border-radius: $border-radius $border-radius 0 0;

  /* 响应式高度调整 */
  @media (max-width: $breakpoint-sm) {
    height: 200rpx;
  }

  @media (min-width: $screen-lg) {
    height: 280rpx;
  }
}

.template-info {
  padding: $padding-small;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */

  /* 响应式内边距 */
  @media (max-width: $breakpoint-sm) {
    padding: 12rpx 16rpx;
  }
}

.template-name {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-mini;

  /* 响应式字体大小 */
  @media (max-width: $breakpoint-sm) {
    font-size: $font-size-small;
    margin-bottom: 8rpx;
  }

  @media (min-width: $screen-lg) {
    font-size: $font-size-medium;
  }
}

.template-meta {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-top: auto; /* 将元信息推到底部 */
}

.template-type {
  align-self: flex-start;
  font-size: $font-size-small;
  background: $background-color;
  border: 1px solid $border-color-light;
  padding: 4rpx 12rpx;

  @media (max-width: $breakpoint-sm) {
    font-size: $font-size-extra-small;
    padding: 2rpx 8rpx;
  }
}

.template-stats {
  display: flex;
  align-items: center;
  font-size: $font-size-extra-small;
  color: $text-secondary;

  .stat-count {
    font-size: inherit;
  }

  @media (max-width: $breakpoint-sm) {
    font-size: 18rpx;
  }
}

.stat-icon {
  margin-right: 4rpx;
}

.template-actions {
  padding: $padding-mini $padding-small $padding-small;
  border-top: 1px solid $border-color-lighter;
  gap: $margin-mini;

  /* 响应式布局调整 */
  @media (max-width: $breakpoint-sm) {
    flex-direction: row;
    padding: 12rpx;
    gap: 8rpx;
  }

  /* 在大屏幕上提供更多空间 */
  @media (min-width: $screen-lg) {
    padding: 16rpx 20rpx;
  }
}

/* 响应式显示/隐藏工具类 */
.hidden-sm {
  @media (max-width: $breakpoint-sm) {
    display: none !important;
  }
}

.visible-sm {
  display: none !important;

  @media (max-width: $breakpoint-sm) {
    display: inline-block !important;
  }
}

/* 按钮样式 - 完全响应式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 16rpx 24rpx;
  border-radius: $border-radius;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  transition: all $transition-fast $ease-in-out;
  border: none;
  cursor: pointer;
  flex: 1;
  min-height: 60rpx;
  text-align: center;
  line-height: 1.2;

  /* 响应式调整 */
  @media (max-width: $breakpoint-sm) {
    padding: 12rpx 16rpx;
    min-height: 52rpx;
    font-size: $font-size-extra-small;
  }

  @media (min-width: $screen-lg) {
    padding: 18rpx 28rpx;
    min-height: 64rpx;
    font-size: $font-size-base;
  }

  &-primary {
    background: $button-primary-bg;
    color: $background-color-white;
    box-shadow: $box-shadow-light;

    &:hover {
      background: color.adjust($primary-color, $lightness: -10%);
    }

    &:active {
      transform: scale(0.98);
      box-shadow: $button-active-shadow;
    }

    /* 在小屏幕上更紧凑 */
    @media (max-width: $breakpoint-sm) {
      font-weight: $font-weight-bold;
    }
  }

  &-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 12rpx 20rpx;
    border-radius: $border-radius;
    font-size: $font-size-small;
    font-weight: $font-weight-medium;
    transition: all $transition-fast $ease-in-out;
    border: 1px solid $border-color-light;
    background: $background-color-white;
    cursor: pointer;
    gap: 6rpx;
    min-height: 60rpx;
    flex: 1;

    /* 响应式调整 */
    @media (max-width: $breakpoint-sm) {
      padding: 10rpx 12rpx;
      min-height: 52rpx;
      font-size: $font-size-extra-small;
      gap: 4rpx;
    }

    @media (min-width: $screen-lg) {
      padding: 14rpx 24rpx;
      min-height: 64rpx;
    }

    &-danger {
      color: $danger-color;
      border-color: rgba($danger-color, 0.3);
      background: $danger-bg;

      &:active {
        background: rgba($danger-color, 0.15);
        transform: scale(0.98);
      }

      /* 在小屏幕上调整颜色对比度 */
      @media (max-width: $breakpoint-sm) {
        color: color.adjust($danger-color, $lightness: -10%);
      }
    }
  }

  &-text {
    line-height: 1.2;
    white-space: nowrap;

    &-sm {
      font-size: $font-size-extra-small;
      font-weight: $font-weight-bold;
    }
  }
}

/* 加载更多样式 */
.load-more,
.no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;

  @media (max-width: $breakpoint-sm) {
    padding: 30rpx 0;
  }
}

.load-more-text,
.no-more-text {
  color: $text-secondary;
  font-size: $font-size-small;

  @media (max-width: $breakpoint-sm) {
    font-size: $font-size-extra-small;
  }
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.loading-dots {
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background-color: $primary-color;
  position: relative;
  animation: pulse 1.5s infinite ease-in-out;

  &:before,
  &:after {
    content: '';
    position: absolute;
    width: 8rpx;
    height: 8rpx;
    border-radius: 50%;
    background-color: $primary-color;
  }

  &:before {
    left: -12rpx;
    animation: pulse 1.5s infinite ease-in-out -0.3s;
  }

  &:after {
    right: -12rpx;
    animation: pulse 1.5s infinite ease-in-out 0.3s;
  }

  /* 响应式调整 */
  @media (max-width: $breakpoint-sm) {
    width: 6rpx;
    height: 6rpx;

    &:before,
    &:after {
      width: 6rpx;
      height: 6rpx;
    }

    &:before {
      left: -10rpx;
    }

    &:after {
      right: -10rpx;
    }
  }
}

@keyframes pulse {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.6;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx $padding-base;
  min-height: 60vh;

  @media (max-width: $breakpoint-sm) {
    padding: 80rpx $padding-small;
    min-height: 50vh;
  }
}

.empty-icon {
  font-size: $empty-icon-size;
  color: $empty-text-color;
  margin-bottom: $margin-base;
  opacity: 0.6;

  @media (max-width: $breakpoint-sm) {
    font-size: 80rpx;
    margin-bottom: $margin-small;
  }
}

.empty-text {
  font-size: $empty-text-font-size;
  color: $empty-text-color;
  margin-bottom: $margin-base * 2;
  text-align: center;

  @media (max-width: $breakpoint-sm) {
    font-size: $font-size-small;
    margin-bottom: $margin-base;
    padding: 0 20rpx;
  }

  @media (min-width: $screen-lg) {
    font-size: $font-size-medium;
  }
}

/* 平台特定样式 */
// #ifdef H5
@media (hover: hover) {
  .template-item:hover {
    transform: translateY(-4rpx);
    box-shadow: $box-shadow-dark !important;
  }

  .btn-primary:hover {
    transform: translateY(-1px);
  }
}
// #endif

/* 设备适配 */
// #ifdef MP-WEIXIN
.template-grid {
  /* 微信小程序上可能需要更保守的布局 */
  @media (max-width: $screen-md) {
    grid-template-columns: repeat(2, 1fr);
  }
}
// #endif

// #ifdef APP
.template-item {
  /* App上可以有更大的触摸区域 */
  min-height: 340rpx;

  @media (max-width: $breakpoint-sm) {
    min-height: 300rpx;
  }
}
// #endif
</style>