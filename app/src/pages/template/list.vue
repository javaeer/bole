<template>
  <view class="list-page-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索简历模板名称、职位或技能"
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
          :class="{ active: sortField === 'version' }"
          @click="changeSort('version')"
        >
          <text class="sort-text">版本</text>
          <view v-if="sortField === 'version'" class="sort-arrow">
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
              <text class="template-name">{{ template.name }}</text>
              <text class="template-description">{{ template.description || "经典简约设计" }}</text>
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
                <text class="meta-text">v{{ template.version }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-icon">📐</text>
                <text class="meta-text">{{ template.layout?.pageSize || "A4" }}</text>
              </view>
            </view>

            <view class="template-features">
              <text
                class="feature-tag"
                v-for="(feature, index) in getTemplateFeatures(template)"
                :key="index"
              >
                {{ feature }}
              </text>
            </view>
          </view>

          <view class="template-item-actions">
            <button class="btn-action primary" @click.stop="handleSelected(template)">
              <text class="action-text">使用模板</text>
            </button>
            <button class="btn-action secondary" @click.stop="handlePreview(template)">
              <text class="action-text">预览</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 网格视图 -->
      <view v-if="viewMode === 'grid'" class="grid-view">
        <view
          class="template-card"
          v-for="template in displayList"
          :key="template.id"
          @click="handleViewTemplate(template)"
        >
          <view class="card-header">
            <view class="card-avatar" :style="{ backgroundColor: getAvatarColor(template.id) }">
              <text class="avatar-text">{{ getTemplateInitial(template.name) }}</text>
            </view>
            <view class="card-title">
              <text class="card-name">{{ template.name }}</text>
              <view class="card-status">
                <view class="status-badge" :class="template.isActive ? 'active' : 'inactive'">
                  <text class="status-text">{{ template.isActive ? "启用" : "停用" }}</text>
                </view>
              </view>
            </view>
          </view>

          <view class="card-description">
            <text class="description-text">{{ template.description || "经典简约设计" }}</text>
          </view>

          <view class="card-features">
            <text
              class="feature-chip"
              v-for="(feature, index) in getTemplateFeatures(template).slice(0, 2)"
              :key="index"
            >
              {{ feature }}
            </text>
          </view>

          <view class="card-footer">
            <text class="update-time">v{{ template.version }} • {{ formatRelativeTime(template.updatedAt) }}</text>
            <view class="card-actions">
              <text class="card-action use" @click.stop="handleSelected(template)">使用</text>
              <text class="card-action preview" @click.stop="handlePreview(template)">预览</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && !loading" class="load-more" @click="loadMore">
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

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { TemplateResult, useTemplate } from "@/composables/useTemplate";

interface Filter {
  label: string;
  value: string;
}

export default defineComponent({
  name: "TemplateList",

  setup() {
    const {
      templateList,
      loading,
      hasMore,
      loadListTemplates,
      loadMore,
      searchTemplates,
      sortTemplates,
      currentTemplate,
      setCurrentTemplate,
    } = useTemplate();

    // 搜索和筛选状态
    const searchKeyword = ref("");
    const activeFilters = ref<Filter[]>([]);
    const sortField = ref("updatedAt");
    const sortOrder = ref<"asc" | "desc">("desc");
    const viewMode = ref<"list" | "grid">("list");
    const showFilter = ref(false);

    // 计算显示列表（包含搜索和排序）
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

        if (sortField.value === "version") {
          const verA = parseVersion(a.version || "0.0.0");
          const verB = parseVersion(b.version || "0.0.0");
          return (verB - verA) * order;
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
        url: "/pages/template/create",
      });
    };

    // 工具函数
    const getTemplateInitial = (name: string) => {
      if (!name) return "R";
      return name.charAt(0).toUpperCase();
    };

    const getAvatarColor = (id: number) => {
      const colors = [
        "#3498db",
        "#2ecc71",
        "#e74c3c",
        "#f39c12",
        "#9b59b6",
        "#1abc9c",
        "#d35400",
        "#c0392b",
      ];
      return colors[id % colors.length];
    };

    const getTemplateFeatures = (template: TemplateResult) => {
      const features: string[] = [];

      // 根据布局添加特征
      if (template.globalLayout) {
        if (template.globalLayout.columns === 2) {
          features.push("双栏");
        } else {
          features.push("单栏");
        }

        if (template.globalLayout.showPhoto) {
          features.push("带照片");
        }

        const orientation = template.globalLayout.orientation === "portrait" ? "竖向" : "横向";
        features.push(orientation);
      }

      // 根据全局样式添加特征
      if (template.globalStyle?.theme) {
        const themeMap: Record<string, string> = {
          classic: "经典",
          modern: "现代",
          creative: "创意",
          professional: "专业",
          simple: "简约",
        };
        const themeName = themeMap[template.globalStyle.theme] || template.globalStyle.theme;
        features.push(themeName);
      }

      // 添加组件数量特征
      if (template.components?.length) {
        features.push(`${template.components.length}模块`);
      }

      return features.slice(0, 4); // 最多显示4个特征
    };

    const formatDate = (dateStr: string) => {
      if (!dateStr) return "未知";
      try {
        const date = new Date(dateStr);
        return `${date.getMonth() + 1}-${date.getDate()}`;
      } catch {
        return "未知";
      }
    };

    const formatRelativeTime = (dateStr: string) => {
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

    const parseVersion = (version: string) => {
      const parts = version.split(".").map(Number);
      return parts[0] * 10000 + (parts[1] || 0) * 100 + (parts[2] || 0);
    };

    // 初始化加载
    onMounted(async () => {
      await loadListTemplates(1, { isActive: true });
    });

    return {
      // 状态
      templateList,
      loading,
      hasMore,
      searchKeyword,
      activeFilters,
      sortField,
      sortOrder,
      viewMode,
      showFilter,
      displayList,

      // 方法
      loadMore,
      handleSearch,
      clearSearch,
      removeFilter,
      clearAllFilters,
      changeSort,
      handleViewTemplate,
      handleSelected,
      handlePreview,
      handleCreateTemplate,

      // 工具函数
      getTemplateInitial,
      getAvatarColor,
      getTemplateFeatures,
      formatDate,
      formatRelativeTime,
    };
  },
});
</script>

<style scoped lang="scss">
.list-page-container {
  background-color: $uni-bg-color-grey;
  min-height: 100vh;
  padding-bottom: $tabbar-height;
}

/* 搜索栏 */
.search-bar {
  background: $uni-bg-color;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  display: flex;
  align-items: center;
  gap: $uni-spacing-col-sm;
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  box-shadow: $box-shadow-light;
  border-bottom: 1rpx solid $border-color-light;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  background: $background-color;
  border-radius: $uni-border-radius-lg;
  border: 1rpx solid $border-color-light;
  display: flex;
  align-items: center;
  padding: 0 $uni-spacing-col-sm;
  height: $input-height;
  transition: all $transition-fast;

  &:focus-within {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
    background: $uni-bg-color;
  }
}

.search-icon {
  font-size: $uni-font-size-base;
  color: $uni-text-color-grey;
  margin-right: $uni-spacing-col-sm;
}

.search-input {
  flex: 1;
  height: $input-height - 10rpx;
  font-size: $uni-font-size-base;
  color: $uni-text-color;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: $uni-text-color-placeholder;
  font-size: $uni-font-size-base;
}

.search-clear {
  width: 36rpx;
  height: 36rpx;
  border-radius: $uni-border-radius-circle;
  background: $border-color;
  @extend .flex-center;
  cursor: pointer;
}

.clear-icon {
  font-size: $uni-font-size-lg;
  color: $uni-text-color-grey;
  font-weight: $font-weight-bold;
}

.btn-filter {
  height: $input-height;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  border-radius: $uni-border-radius-lg;
  padding: 0 $uni-spacing-col-sm;
  font-size: $uni-font-size-base;
  color: $uni-text-color-grey;
  white-space: nowrap;
  @extend .flex-center;
}

.filter-icon {
  font-size: $uni-font-size-base;
}

/* 筛选标签 */
.filter-tags {
  background: $uni-bg-color;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.tags-scroll {
  width: 100%;
  white-space: nowrap;
}

.tags-container {
  display: inline-flex;
  align-items: center;
  gap: $uni-spacing-col-sm;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  background: $primary-light;
  border-radius: $uni-border-radius-sm;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  gap: math.div($uni-spacing-col-sm, 2);
  border: 1rpx solid color.adjust($primary-color, $lightness: 20%);
}

.tag-text {
  font-size: $uni-font-size-sm;
  color: $primary-color;
}

.tag-remove {
  font-size: $uni-font-size-sm;
  color: $primary-color;
  font-weight: $font-weight-bold;
  cursor: pointer;
  padding-left: math.div($uni-spacing-col-sm, 2);
}

.clear-all {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
}

.clear-all-text {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
  cursor: pointer;
}

/* 排序栏 */
.sort-bar {
  background: $uni-bg-color;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.sort-tabs {
  display: flex;
  gap: $uni-spacing-row-lg;
}

.sort-tab {
  display: flex;
  align-items: center;
  gap: math.div($uni-spacing-col-sm, 2);
  cursor: pointer;
  padding: math.div($uni-spacing-col-sm, 2) 0;
  transition: all $transition-fast;

  &.active {
    .sort-text {
      color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }
}

.sort-text {
  font-size: $uni-font-size-base;
  color: $text-regular;
  transition: color $transition-fast;
}

.sort-arrow {
  font-size: $uni-font-size-sm;
  color: $primary-color;
}

.view-mode {
  display: flex;
  gap: $uni-spacing-col-sm;
  background: $background-color;
  border-radius: $uni-border-radius-sm;
  padding: math.div($uni-spacing-col-sm, 2);
}

.view-mode-btn {
  font-size: $uni-font-size-lg;
  color: $text-placeholder;
  cursor: pointer;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  transition: all $transition-fast;

  &.active {
    color: $primary-color;
    background: $uni-bg-color;
    box-shadow: $box-shadow-light;
  }
}

/* 加载状态 */
.loading-state {
  @extend .flex-center;
  flex-direction: column;
  padding: $uni-spacing-row-base * 2 $uni-spacing-row-base;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: $uni-border-radius-circle;
  animation: spin 1s linear infinite;
  margin-bottom: $uni-spacing-col-sm;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $uni-font-size-base;
  color: $uni-text-color-grey;
}

/* 空状态 */
.empty-state {
  @extend .flex-center;
  flex-direction: column;
  padding: $uni-spacing-row-base * 3 $uni-spacing-row-base;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: $uni-spacing-row-base;
  opacity: 0.3;
}

.empty-text {
  font-size: $uni-font-size-lg;
  color: $uni-text-color;
  margin-bottom: $uni-spacing-col-sm;
  font-weight: $font-weight-medium;
}

.empty-subtext {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
  margin-bottom: $uni-spacing-row-base * 2;
}

.btn-create {
  background: $primary-color;
  color: $uni-bg-color;
  border: none;
  border-radius: $uni-border-radius-lg;
  padding: $uni-spacing-col-sm $uni-spacing-row-base * 1.5;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
}

.create-text {
  color: $uni-bg-color;
}

/* 简历模板列表 */
.templates-list {
  padding: $uni-spacing-row-base;
}

/* 列表视图 */
.list-view {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-row-base;
}

.template-item {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $card-padding;
  box-shadow: $card-shadow;
  transition: all $transition-normal;
  border: 1rpx solid transparent;

  &:active {
    background: color.adjust($uni-bg-color, $lightness: -2%);
    box-shadow: $card-hover-shadow;
    border-color: $border-color-light;
    transform: translateY(-2rpx);
  }
}

.template-item-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: $uni-spacing-row-base;
}

.template-avatar {
  width: $avatar-size;
  height: $avatar-size;
  border-radius: $avatar-border-radius;
  @extend .flex-center;
  margin-right: $uni-spacing-col-sm;
  flex-shrink: 0;
  box-shadow: $box-shadow-light;
}

.avatar-text {
  font-size: $uni-font-size-lg;
  color: $uni-text-color-inverse;
  font-weight: $font-weight-bold;
}

.template-main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
  min-width: 0;
}

.template-name {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-medium;
  line-height: 1.4;
  @extend .text-truncate;
}

.template-description {
  font-size: $uni-font-size-base;
  color: $text-regular;
  line-height: 1.4;
  @extend .text-multi-truncate;
}

.template-status {
  margin-left: $uni-spacing-col-sm;
  flex-shrink: 0;
}

.status-badge {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $badge-border-radius;
  font-size: $uni-font-size-sm;
  font-weight: $font-weight-medium;

  &.active {
    background: $success-bg;
    color: $success-color;
    border: 1rpx solid $success-border;
  }

  &.inactive {
    background: $background-color;
    color: $uni-text-color-grey;
    border: 1rpx solid $border-color-light;
  }
}

.status-text {
  font-size: $uni-font-size-sm;
  font-weight: $font-weight-medium;
}

.template-item-content {
  margin-bottom: $uni-spacing-row-base;
}

.template-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-base;
  margin-bottom: $uni-spacing-col-base;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: math.div($uni-spacing-col-sm, 2);
}

.meta-icon {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
}

.meta-text {
  font-size: $uni-font-size-sm;
  color: $text-regular;
}

.template-features {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-sm;
}

.feature-tag {
  background: $background-color;
  border-radius: $uni-border-radius-sm;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  font-size: $uni-font-size-sm;
  color: $text-regular;
  border: 1rpx solid $border-color-light;
}

.template-item-actions {
  display: flex;
  gap: $uni-spacing-col-sm;
  padding-top: $uni-spacing-row-base;
  border-top: 1rpx solid $border-color-extra-light;
}

.btn-action {
  flex: 1;
  border-radius: $uni-border-radius-lg;
  padding: $uni-spacing-col-base 0;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;

  &.primary {
    background: $primary-color;
    color: $uni-bg-color;
    border: none;

    &:active {
      background: color.adjust($primary-color, $lightness: -10%);
      opacity: 0.9;
    }
  }

  &.secondary {
    background: $uni-bg-color;
    color: $text-regular;
    border: 1rpx solid $border-color-light;

    &:active {
      background: $background-color;
      border-color: $border-color;
    }
  }
}

.action-text {
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
}

/* 网格视图 */
.grid-view {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $uni-spacing-row-base;
}

.template-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $card-padding;
  box-shadow: $card-shadow;
  transition: all $transition-normal;
  border: 1rpx solid transparent;
  display: flex;
  flex-direction: column;
  height: 320rpx;

  &:active {
    background: color.adjust($uni-bg-color, $lightness: -2%);
    box-shadow: $card-hover-shadow;
    border-color: $border-color-light;
    transform: translateY(-2rpx);
  }
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: $uni-spacing-col-base;
}

.card-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: $avatar-border-radius;
  @extend .flex-center;
  margin-right: $uni-spacing-col-sm;
  flex-shrink: 0;
  box-shadow: $box-shadow-light;
}

.card-title {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
  min-width: 0;
}

.card-name {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-medium;
  @extend .text-truncate;
}

.card-description {
  margin-bottom: $uni-spacing-col-base;
  flex: 1;
}

.description-text {
  font-size: $uni-font-size-base;
  color: $text-regular;
  line-height: 1.4;
  @extend .text-multi-truncate;
}

.card-features {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-sm;
  margin-bottom: $uni-spacing-col-base;
}

.feature-chip {
  background: $background-color;
  border-radius: $uni-border-radius-sm;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  font-size: $uni-font-size-sm;
  color: $text-regular;
  border: 1rpx solid $border-color-light;
  @extend .text-truncate;
  max-width: 120rpx;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $uni-spacing-col-base;
  border-top: 1rpx solid $border-color-extra-light;
  margin-top: auto;
}

.update-time {
  font-size: $uni-font-size-sm;
  color: $uni-text-color-grey;
}

.card-actions {
  display: flex;
  gap: $uni-spacing-col-sm;
}

.card-action {
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  cursor: pointer;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  transition: all $transition-fast;

  &.use {
    color: $primary-color;
    background: $primary-light;

    &:active {
      background: color.adjust($primary-light, $lightness: -5%);
    }
  }

  &.preview {
    color: $text-regular;
    background: $background-color;

    &:active {
      background: color.adjust($background-color, $lightness: -5%);
    }
  }
}

/* 加载更多 */
.load-more {
  @extend .flex-center;
  padding: $uni-spacing-row-base $uni-spacing-row-base;
  margin-top: $uni-spacing-row-base;
}

.load-more-text {
  font-size: $uni-font-size-base;
  color: $primary-color;
  cursor: pointer;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  border: 1rpx solid $primary-color;
  border-radius: $uni-border-radius-lg;
  transition: all $transition-fast;

  &:active {
    background: $primary-light;
    opacity: 0.8;
  }
}

/* 悬浮按钮 */
.floating-action {
  position: fixed;
  bottom: calc($tabbar-height + 40rpx);
  right: $uni-spacing-row-base;
  z-index: $z-index-dropdown;
}

.btn-fab {
  width: 100rpx;
  height: 100rpx;
  border-radius: $uni-border-radius-circle;
  background: $primary-color;
  color: $uni-bg-color;
  border: none;
  box-shadow: $box-shadow-dark;
  @extend .flex-center;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-light;
  transition: all $transition-normal;

  &:active {
    transform: scale(0.95);
    box-shadow: $button-active-shadow;
  }
}

.fab-text {
  color: $uni-bg-color;
  font-size: $font-size-extra-large;
  line-height: 1;
  font-weight: $font-weight-light;
}

/* 响应式调整 */
@media (max-width: $breakpoint-sm) {
  .grid-view {
    grid-template-columns: 1fr;
  }

  .sort-tabs {
    gap: $uni-spacing-row-base;
  }
}

/* 使用通用样式类 */
.flex-center {
  @extend .flex-center;
}

.text-truncate {
  @extend .text-truncate;
}

.text-multi-truncate {
  @extend .text-multi-truncate;
}
</style>