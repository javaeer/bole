<template>
  <!-- 模板部分保持不变 -->
  <view class="list-page-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索简历模板名称"
          placeholder-class="search-placeholder"
          v-model="searchKeyword"
          @input="handleSearch"
          @confirm="handleSearch"
        />
        <view v-if="searchKeyword" class="search-clear" @click="clearSearch">
          <text class="clear-icon">×</text>
        </view>
      </view>
      <button class="btn-filter" @click="showFilter = true">
        <text class="filter-icon">筛选</text>
      </button>
    </view>

    <!-- 筛选标签 -->
    <view v-if="activeFilters.length > 0" class="filter-tags">
      <scroll-view class="tags-scroll" scroll-x="true">
        <view class="tags-container">
          <view class="filter-tag" v-for="(filter, index) in activeFilters" :key="index">
            <text class="tag-text">{{ filter.label }}: {{ filter.value }}</text>
            <text class="tag-remove" @click="removeFilter(index)">×</text>
          </view>
          <view class="clear-all" @click="clearAllFilters">
            <text class="clear-all-text">清除全部</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 排序栏 -->
    <view class="sort-bar">
      <view class="sort-tabs">
        <view
          class="sort-tab"
          :class="{ active: sortField === 'updatedAt' }"
          @click="changeSort('updatedAt')"
        >
          <text class="sort-text">最近更新</text>
          <view v-if="sortField === 'updatedAt'" class="sort-arrow">
            <text>{{ sortOrder === "desc" ? "↓" : "↑" }}</text>
          </view>
        </view>
        <view
          class="sort-tab"
          :class="{ active: sortField === 'name' }"
          @click="changeSort('name')"
        >
          <text class="sort-text">名称</text>
          <view v-if="sortField === 'name'" class="sort-arrow">
            <text>{{ sortOrder === "desc" ? "↓" : "↑" }}</text>
          </view>
        </view>
        <view
          class="sort-tab"
          :class="{ active: sortField === 'price' }"
          @click="changeSort('price')"
        >
          <text class="sort-text">价格</text>
          <view v-if="sortField === 'price'" class="sort-arrow">
            <text>{{ sortOrder === "desc" ? "↓" : "↑" }}</text>
          </view>
        </view>
      </view>
      <view class="view-mode">
        <text
          class="view-mode-btn"
          :class="{ active: viewMode === 'list' }"
          @click="viewMode = 'list'"
        >
          ≡
        </text>
        <text
          class="view-mode-btn"
          :class="{ active: viewMode === 'grid' }"
          @click="viewMode = 'grid'"
        >
          ☐
        </text>
      </view>
    </view>

    <!-- 列表内容 -->
    <view v-if="loading" class="loading-state">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="templateList.length === 0" class="empty-state">
      <text class="empty-icon">📄</text>
      <text class="empty-text">暂无简历模板</text>
      <text class="empty-subtext">选择模板开始创建简历吧</text>
      <button class="btn-create" @click="handleCreateTemplate">
        <text class="create-text">+ 创建模板</text>
      </button>
    </view>

    <view v-else class="templates-list" :class="viewMode">
      <!-- 列表视图 -->
      <view v-if="viewMode === 'list'" class="list-view">
        <view
          class="template-item"
          v-for="template in displayList"
          :key="template.id"
          @click="handleViewTemplate(template)"
        >
          <view class="template-item-header">
            <view class="template-avatar" :style="{ backgroundColor: getAvatarColor(template.id) }">
              <text class="avatar-text">{{ getTemplateInitial(template.name) }}</text>
            </view>
            <view class="template-main-info">
              <text class="template-name text-ellipsis">{{ template.name }}</text>
              <text class="template-description text-ellipsis">{{ template.description || "经典简约设计" }}</text>
            </view>
            <view class="template-status">
              <view class="status-badge" :class="template.isActive ? 'active' : 'inactive'">
                <text class="status-text">{{ template.isActive ? "已启用" : "未启用" }}</text>
              </view>
            </view>
          </view>

          <view class="template-item-content">
            <view class="template-meta">
              <view class="meta-item">
                <text class="meta-icon">🔄</text>
                <text class="meta-text">更新: {{ formatDate(template.updatedAt) }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-icon">📊</text>
                <text class="meta-text">v{{ template.version || '1.0.0' }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-icon">👥</text>
                <text class="meta-text">{{ template.users || 0 }}人使用</text>
              </view>
            </view>

            <view class="template-features">
              <text
                class="feature-tag tag"
                v-for="(feature, index) in getTemplateFeatures(template)"
                :key="index"
              >
                {{ feature }}
              </text>
            </view>
          </view>

          <view class="template-item-actions flex-between">
            <view class="price-info">
              <text class="price-label">价格:</text>
              <text class="price-value">¥{{ template.price || 0 }}</text>
            </view>
            <view class="action-buttons">
              <button class="btn-action btn btn-primary" @click.stop="handleSelected(template)">
                <text class="action-text">使用模板</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <!-- 网格视图 -->
      <view v-if="viewMode === 'grid'" class="grid-view">
        <view
          class="template-card card-container"
          v-for="template in displayList"
          :key="template.id"
          @click="handleViewTemplate(template)"
        >
          <view class="card-header flex-between">
            <view class="card-avatar" :style="{ backgroundColor: getAvatarColor(template.id) }">
              <text class="avatar-text">{{ getTemplateInitial(template.name) }}</text>
            </view>
            <view class="card-title">
              <text class="card-name text-ellipsis">{{ template.name }}</text>
              <view class="card-status">
                <view class="status-badge tag" :class="template.isActive ? 'active' : 'inactive'">
                  <text class="status-text">{{ template.isActive ? "启用" : "停用" }}</text>
                </view>
              </view>
            </view>
          </view>

          <view class="card-description">
            <text class="description-text text-multi-truncate">{{ template.description || "经典简约设计" }}</text>
          </view>

          <view class="card-features">
            <text
              class="feature-chip tag"
              v-for="(feature, index) in getTemplateFeatures(template).slice(0, 3)"
              :key="index"
            >
              {{ feature }}
            </text>
          </view>

          <view class="card-footer flex-between">
            <view class="price-info">
              <text class="price-label">价格:</text>
              <text class="price-value text-primary">¥{{ template.price || 0 }}</text>
            </view>
            <text class="update-time">{{ formatRelativeTime(template.updatedAt) }}</text>
          </view>

          <view class="card-actions flex-between">
            <button class="btn-action btn btn-primary" @click.stop="handleSelected(template)">
              <text class="action-text">使用</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && !loading" class="load-more" @click="loadMoreTemplates">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>

    <!-- 创建按钮 -->
    <view class="floating-action">
      <button class="btn-fab" @click="handleCreateTemplate">
        <text class="fab-text">+</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useTemplate } from "@/composables/useTemplate";
import type { TemplateResult } from "@/types/template";

interface Filter {
  label: string;
  value: string;
}

// 使用组合式函数
const {
  templateList,
  loading,
  hasMore,
  loadListTemplates,
  loadMore,
  searchTemplates,
  sortTemplates,
  setCurrentTemplate,
} = useTemplate();

// 响应式状态
const searchKeyword = ref("");
const activeFilters = ref<Filter[]>([]);
const sortField = ref("updatedAt");
const sortOrder = ref<"asc" | "desc">("desc");
const viewMode = ref<"list" | "grid">("list");
const showFilter = ref(false);

// 计算属性
const displayList = computed(() => {
  let list = [...templateList.value];

  // 搜索过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    list = list.filter(
      (template: TemplateResult) =>
        template.name.toLowerCase().includes(keyword) ||
        template.description?.toLowerCase().includes(keyword) ||
        template.code?.toLowerCase().includes(keyword),
    );
  }

  // 排序
  list.sort((a: TemplateResult, b: TemplateResult) => {
    const order = sortOrder.value === "asc" ? 1 : -1;

    if (sortField.value === "updatedAt") {
      const dateA = new Date(a.updatedAt || a.createdAt || 0).getTime();
      const dateB = new Date(b.updatedAt || b.createdAt || 0).getTime();
      return (dateB - dateA) * order;
    }

    if (sortField.value === "name") {
      return (a.name || "").localeCompare(b.name || "") * order;
    }

    if (sortField.value === "price") {
      return ((a.price || 0) - (b.price || 0)) * order;
    }

    return 0;
  });

  return list;
});

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    searchTemplates(searchKeyword.value);
  } else {
    loadListTemplates(1, { isActive: true });
  }
};

const clearSearch = () => {
  searchKeyword.value = "";
  loadListTemplates(1, { isActive: true });
};

// 筛选处理
const removeFilter = (index: number) => {
  activeFilters.value.splice(index, 1);
};

const clearAllFilters = () => {
  activeFilters.value = [];
};

// 排序处理
const changeSort = (field: string) => {
  if (sortField.value === field) {
    sortOrder.value = sortOrder.value === "desc" ? "asc" : "desc";
  } else {
    sortField.value = field;
    sortOrder.value = "desc";
  }

  // 调用排序API
  sortTemplates(sortField.value, sortOrder.value);
};

// 操作处理
const handleViewTemplate = (template: TemplateResult) => {
  uni.navigateTo({
    url: `/pages/template/detail?id=${template.id}`,
  });
};

const handleSelected = (template: TemplateResult) => {
  setCurrentTemplate(template);
  uni.navigateTo({
    url: `/pages/resumes/create?templateId=${template.id}`,
  });
};

const handlePreview = (template: TemplateResult) => {
  uni.navigateTo({
    url: `/pages/template/preview?id=${template.id}`,
  });
};

const handleCreateTemplate = () => {
  uni.navigateTo({
    url: "/pages/template/edit",
  });
};

const loadMoreTemplates = async () => {
  if (!loading.value && hasMore.value) {
    await loadMore();
  }
};

// 工具函数
const getTemplateInitial = (name: string) => {
  if (!name) return "R";
  return name.charAt(0).toUpperCase();
};

const getAvatarColor = (id: number) => {
  const colors = [
    "#d4af37", // 主色调
    "#3498db",
    "#2ecc71",
    "#e74c3c",
    "#f39c12",
    "#9b59b6",
    "#1abc9c",
    "#d35400",
  ];
  return colors[id % colors.length];
};

const getTemplateFeatures = (template: TemplateResult) => {
  const features: string[] = [];

  // 根据布局添加特征
  if (template.globalLayout?.type) {
    const layoutMap: Record<string, string> = {
      'single-column': '单栏',
      'two-column': '双栏',
      'three-column': '三栏',
      'creative': '创意'
    };
    const layoutName = layoutMap[template.globalLayout.type] || template.globalLayout.type;
    features.push(layoutName);
  }

  // 根据全局样式添加特征
  if (template.globalStyle?.theme) {
    const themeMap: Record<string, string> = {
      classic: "经典",
      modern: "现代",
      creative: "创意",
      professional: "专业",
      simple: "简约",
      light: "明亮",
      dark: "深色"
    };
    const themeName = themeMap[template.globalStyle.theme] || template.globalStyle.theme;
    features.push(themeName);
  }

  // 添加组件数量特征
  if (template.components?.length) {
    features.push(`${template.components.length}模块`);
  }

  // 根据分类添加特征
  if (template.category) {
    features.push(template.category);
  }

  return features.slice(0, 3); // 最多显示3个特征
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return "未知";
  try {
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}-${date.getDate()}`;
  } catch {
    return "未知";
  }
};

const formatRelativeTime = (dateStr?: string) => {
  if (!dateStr) return "未知";
  try {
    const date = new Date(dateStr);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return "今天";
    if (diffDays === 1) return "昨天";
    if (diffDays < 7) return `${diffDays}天前`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)}周前`;
    return `${Math.floor(diffDays / 30)}月前`;
  } catch {
    return "未知";
  }
};

// 初始化加载
onMounted(async () => {
  await loadListTemplates(1, { isActive: true });
});
</script>

<style lang="scss">

.list-page-container {
  min-height: 100vh;
  background-color: $background-color;
  padding-bottom: $tabbar-height;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: $padding-small $padding-base;
  background-color: $background-color-white;
  border-bottom: 1px solid $border-color-light;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  background-color: $background-color;
  border-radius: $border-radius-large;
  padding: 12rpx 20rpx;
  margin-right: $margin-small;
}

.search-icon {
  font-size: $font-size-base;
  color: $text-secondary;
  margin-right: 8rpx;
}

.search-input {
  flex: 1;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: $text-placeholder;
  font-size: $font-size-small;
}

.search-clear {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32rpx;
  height: 32rpx;
  background-color: $border-color;
  border-radius: 50%;
  margin-left: 8rpx;
}

.clear-icon {
  font-size: $font-size-large;
  color: $text-secondary;
  line-height: 1;
}

.btn-filter {
  padding: 12rpx 20rpx;
  background-color: $primary-light;
  border: 1px solid $border-color-light;
  border-radius: $border-radius-small;
  font-size: $font-size-small;
  color: $text-primary;
  white-space: nowrap;
}

.filter-tags {
  padding: $padding-mini $padding-base;
  background-color: $background-color-white;
  border-bottom: 1px solid $border-color-light;
}

.tags-scroll {
  width: 100%;
  white-space: nowrap;
}

.tags-container {
  display: inline-flex;
  align-items: center;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 12rpx;
  background-color: $primary-light;
  border-radius: $border-radius-small;
  margin-right: $margin-mini;
  border: 1px solid $border-color-light;
}

.tag-text {
  font-size: $font-size-extra-small;
  color: $text-primary;
  margin-right: 4rpx;
}

.tag-remove {
  font-size: $font-size-large;
  color: $text-secondary;
  line-height: 1;
  margin-left: 4rpx;
}

.clear-all {
  padding: 4rpx 12rpx;
  background-color: $background-color;
  border-radius: $border-radius-small;
  border: 1px solid $border-color-light;
}

.clear-all-text {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-mini $padding-base;
  background-color: $background-color-white;
  border-bottom: 1px solid $border-color-light;
}

.sort-tabs {
  display: flex;
  align-items: center;
}

.sort-tab {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  margin-right: $margin-mini;
  border-radius: $border-radius-small;
  font-size: $font-size-small;
  color: $text-secondary;

  &.active {
    background-color: $primary-light;
    color: $primary-color;
  }
}

.sort-text {
  margin-right: 4rpx;
}

.sort-arrow {
  font-size: $font-size-extra-small;
}

.view-mode {
  display: flex;
  align-items: center;
}

.view-mode-btn {
  padding: 8rpx;
  font-size: $font-size-large;
  color: $text-secondary;
  margin-left: $margin-mini;

  &.active {
    color: $primary-color;
    background-color: $primary-light;
    border-radius: $border-radius-small;
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $padding-large * 2 $padding-base;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: $margin-small;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-size-small;
  color: $text-secondary;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $padding-large * 2 $padding-base;
  text-align: center;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: $margin-base;
}

.empty-text {
  font-size: $font-size-large;
  color: $text-primary;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-mini;
}

.empty-subtext {
  font-size: $font-size-base;
  color: $text-secondary;
  margin-bottom: $margin-base;
}

.btn-create {
  padding: 16rpx 32rpx;
  background-color: $primary-color;
  border-radius: $border-radius;
  font-size: $font-size-base;
  color: white;
  font-weight: $font-weight-medium;
}

.templates-list {
  padding: $padding-base;
}

.list-view {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.template-item {
  background-color: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow-light;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $box-shadow;
  }
}

.template-item-header {
  display: flex;
  align-items: center;
  margin-bottom: $margin-small;
}

.template-avatar {
  width: $avatar-size;
  height: $avatar-size;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $margin-small;
  flex-shrink: 0;
}

.avatar-text {
  font-size: $font-size-large;
  color: white;
  font-weight: $font-weight-medium;
}

.template-main-info {
  flex: 1;
  min-width: 0;
}

.template-name {
  font-size: $font-size-medium;
  color: $text-primary;
  font-weight: $font-weight-medium;
  margin-bottom: 4rpx;
  display: block;
}

.template-description {
  font-size: $font-size-small;
  color: $text-secondary;
  display: block;
}

.template-status {
  flex-shrink: 0;
}

.status-badge {
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;
  font-size: $font-size-extra-small;

  &.active {
    background-color: $success-bg;
    color: $success-color;
    border: 1px solid $success-border;
  }

  &.inactive {
    background-color: $background-color;
    color: $text-secondary;
    border: 1px solid $border-color-light;
  }
}

.status-text {
  font-size: $font-size-extra-small;
  font-weight: $font-weight-medium;
}

.template-item-content {
  margin-bottom: $margin-small;
}

.template-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: $margin-small;
  margin-bottom: $margin-small;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.meta-icon {
  margin-right: 4rpx;
}

.template-features {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
}

.feature-tag {
  font-size: $font-size-extra-small;
}

.template-item-actions {
  padding-top: $padding-small;
  border-top: 1px solid $border-color-light;
}

.price-info {
  display: flex;
  align-items: center;
}

.price-label {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-right: 4rpx;
}

.price-value {
  font-size: $font-size-medium;
  color: $danger-color;
  font-weight: $font-weight-medium;
}

.action-buttons {
  display: flex;
  gap: $margin-mini;
}

.btn-action {
  padding: 12rpx 24rpx;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  min-width: 120rpx;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-base;

  @media (max-width: $screen-md) {
    grid-template-columns: 1fr;
  }
}

.template-card {
  display: flex;
  flex-direction: column;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-4rpx);
    box-shadow: $card-hover-shadow;
  }
}

.card-header {
  margin-bottom: $margin-small;
}

.card-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $margin-small;
  flex-shrink: 0;
}

.card-title {
  flex: 1;
  min-width: 0;
}

.card-name {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
  margin-bottom: 4rpx;
  display: block;
}

.card-description {
  margin-bottom: $margin-small;
  min-height: 80rpx;
}

.description-text {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.4;
}

.card-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-bottom: $margin-small;
}

.feature-chip {
  font-size: $font-size-extra-small;
}

.card-footer {
  margin-bottom: $margin-small;
}

.update-time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.card-actions {
  padding-top: $padding-small;
  border-top: 1px solid $border-color-light;
}

.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $padding-base;
  margin-top: $margin-base;
  background-color: $background-color-white;
  border-radius: $border-radius;
  border: 1px solid $border-color-light;
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.floating-action {
  position: fixed;
  right: $margin-base;
  bottom: calc($tabbar-height + $margin-base);
  z-index: $z-index-dropdown;
}

.btn-fab {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: $primary-color;
  color: white;
  font-size: $font-size-extra-large;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $box-shadow-dark;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: scale(0.95);
    background-color: color.adjust($primary-color, $lightness: -10%);
  }
}
</style>