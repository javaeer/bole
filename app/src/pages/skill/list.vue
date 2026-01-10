<!-- pages/skill/list.vue -->
<template>
  <view class="page-container">
    <!-- 搜索和筛选栏 -->
    <view class="filter-container card-container">
      <!-- 搜索框 -->
      <view class="search-box">
        <text class="icon">🔍</text>
        <input
          v-model="searchKeywords"
          class="search-input"
          placeholder="搜索技能名称、分类或标签"
          placeholder-class="placeholder-text"
          @input="handleSearch"
        />
        <button v-if="searchKeywords" class="clear-btn" @click="clearSearch">
          <text class="icon">×</text>
        </button>
      </view>

      <!-- 筛选行 -->
      <view class="filter-row">
        <!-- 分类筛选 -->
        <view class="filter-group">
          <text class="filter-label">分类</text>
          <picker
            :value="categoryIndex"
            :range="categoryOptions"
            @change="onCategoryChange"
          >
            <view class="filter-select">
              {{ selectedCategory || "全部" }}
              <text class="icon">▼</text>
            </view>
          </picker>
        </view>

        <!-- 等级筛选 -->
        <view class="filter-group">
          <text class="filter-label">等级</text>
          <picker
            :value="levelIndex"
            :range="levelOptions"
            @change="onLevelChange"
          >
            <view class="filter-select">
              {{ selectedLevel || "全部" }}
              <text class="icon">▼</text>
            </view>
          </picker>
        </view>

        <!-- 排序 -->
        <view class="filter-group">
          <text class="filter-label">排序</text>
          <picker
            :value="sortIndex"
            :range="sortOptions"
            @change="onSortChange"
          >
            <view class="filter-select">
              {{ sortOptions[sortIndex] }}
              <text class="icon">▼</text>
            </view>
          </picker>
        </view>
      </view>
    </view>

    <!-- 标签筛选 -->
    <scroll-view class="tags-scroll" scroll-x v-if="filterTags.length > 0">
      <view class="tags-container">
        <view class="tag-filter" v-for="tag in filterTags" :key="tag">
          <text class="tag-text">{{ tag }}</text>
          <text class="icon" @click="removeTag(tag)">×</text>
        </view>
        <view class="tag-clear" v-if="filterTags.length > 0" @click="clearTags">
          <text>清除标签</text>
        </view>
      </view>
    </scroll-view>

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
        <text class="icon">ℹ️</text>
        <text class="empty-text">暂无技能数据</text>
        <button class="btn btn-primary" @click="addNewSkill">添加新技能</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="(item, index) in listData"
          :key="item.id"
          class="skill-item card-container"
          @click="goToDetail(item.id)"
        >
          <!-- 技能头部 -->
          <view class="skill-header">
            <view class="skill-title-section">
              <text class="skill-name text-truncate">{{ item.name }}</text>
              <view class="skill-badges">
                <view v-if="item.isPublic" class="badge badge-public">
                  公开
                </view>
                <view v-if="item.isCertified" class="badge badge-certified">
                  已认证
                </view>
              </view>
            </view>

            <view class="skill-level">
              <view :class="['level-tag', getLevelClass(item.level)]">
                {{ item.level }}
              </view>
            </view>
          </view>

          <!-- 技能信息 -->
          <view class="skill-info">
            <view class="info-row">
              <text class="icon">📁</text>
              <text class="info-text">{{ item.category }}</text>
              <text class="info-separator">|</text>
              <text class="icon">📅</text>
              <text class="info-text">{{ item.experienceYears }} 年经验</text>
            </view>

            <!-- 标签 -->
            <view class="skill-tags" v-if="item.tags && getTagsArray(item.tags).length > 0">
              <view
                v-for="tag in getTagsArray(item.tags)"
                :key="tag"
                class="skill-tag"
                @click.stop="addTagFilter(tag)"
              >
                <text>{{ tag }}</text>
              </view>
            </view>
          </view>

          <!-- 熟练度 -->
          <view class="proficiency-section">
            <view class="proficiency-header">
              <text class="proficiency-label">熟练度</text>
              <text class="proficiency-value">{{ item.proficiencyPercent }}%</text>
            </view>
            <view class="progress-bar">
              <view
                class="progress-fill"
                :class="getProficiencyClass(item.proficiencyPercent)"
                :style="{ width: `${item.proficiencyPercent}%` }"
              ></view>
            </view>
            <view class="progress-labels">
              <text>新手</text>
              <text>精通</text>
            </view>
          </view>

          <!-- 描述预览 -->
          <view v-if="item.description" class="skill-desc text-multi-truncate">
            {{ item.description }}
          </view>

          <!-- 操作按钮 -->
          <view class="skill-actions">
            <button class="btn btn-secondary action-btn" @click.stop="goToDetail(item.id)">
              查看详情
            </button>
            <button class="btn btn-primary action-btn" @click.stop="editSkill(item.id)">
              编辑
            </button>
          </view>

          <!-- 时间信息 -->
          <view class="skill-footer">
            <text class="time-text">创建：{{ formatDate(item.createdAt) }}</text>
            <text class="time-text">更新：{{ formatDate(item.updatedAt) }}</text>
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
    <button class="add-btn" @click="addNewSkill">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import type { SkillResult } from "@/types/skill";
import SkillAPI from "@/api/skill";
import { usePageRefresh } from "@/composables/usePageRefresh";

// 响应式数据
const searchKeywords = ref("");
const selectedCategory = ref("");
const selectedLevel = ref("");
const filterTags = ref<string[]>([]);
const sortIndex = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const hasMore = ref(true);
const listData = ref<SkillResult[]>([]);

// 筛选选项
const categoryOptions = ["全部", "编程语言", "前端框架", "后端框架", "数据库", "数据分析", "人工智能", "运维部署", "工具软件", "其他"];
const levelOptions = ["全部", "初级", "中级", "高级", "专家"];
const sortOptions = ["熟练度↓", "熟练度↑", "经验年数↓", "经验年数↑", "创建时间↓", "创建时间↑"];

// 使用页面刷新 composable
const { refreshKey } = usePageRefresh({
  immediate: true, // 页面显示时立即刷新
  onRefresh: async () => {
    await loadListData(true);
    uni.showToast({
      title: "列表已更新",
      icon: "success",
      duration: 1500,
    });
  },
});

// 索引计算
const categoryIndex = computed(() =>
  selectedCategory.value ? categoryOptions.indexOf(selectedCategory.value) : 0,
);

const levelIndex = computed(() =>
  selectedLevel.value ? levelOptions.indexOf(selectedLevel.value) : 0,
);

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

// 获取等级样式
const getLevelClass = (level: string) => {
  switch (level) {
    case "初级":
      return "level-beginner";
    case "中级":
      return "level-intermediate";
    case "高级":
      return "level-advanced";
    case "专家":
      return "level-expert";
    default:
      return "level-default";
  }
};

// 获取熟练度样式
const getProficiencyClass = (percent: number) => {
  if (percent >= 90) return "proficiency-expert";
  if (percent >= 70) return "proficiency-advanced";
  if (percent >= 50) return "proficiency-intermediate";
  return "proficiency-beginner";
};

// 解析标签字符串
const getTagsArray = (tagsStr: string | null): string[] => {
  if (!tagsStr) return [];
  return tagsStr.split(",").map(tag => tag.trim()).filter(tag => tag);
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
const onCategoryChange = (e: any) => {
  const index = e.detail.value;
  selectedCategory.value = index === 0 ? "" : categoryOptions[index];
  currentPage.value = 1;
  loadListData(true);
};

const onLevelChange = (e: any) => {
  const index = e.detail.value;
  selectedLevel.value = index === 0 ? "" : levelOptions[index];
  currentPage.value = 1;
  loadListData(true);
};

const onSortChange = (e: any) => {
  sortIndex.value = e.detail.value;
  currentPage.value = 1;
  loadListData(true);
};

// 标签筛选处理
const addTagFilter = (tag: string) => {
  if (!filterTags.value.includes(tag)) {
    filterTags.value.push(tag);
    currentPage.value = 1;
    loadListData(true);
  }
};

const removeTag = (tag: string) => {
  const index = filterTags.value.indexOf(tag);
  if (index > -1) {
    filterTags.value.splice(index, 1);
    currentPage.value = 1;
    loadListData(true);
  }
};

const clearTags = () => {
  filterTags.value = [];
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

    const query: any = {};

    // 添加搜索条件
    if (searchKeywords.value) {
      query.keyword = searchKeywords.value;
    }

    // 添加分类筛选
    if (selectedCategory.value) {
      query.category = selectedCategory.value;
    }

    // 添加等级筛选
    if (selectedLevel.value) {
      query.level = selectedLevel.value;
    }

    // 添加标签筛选
    if (filterTags.value.length > 0) {
      query.tags = filterTags.value;
    }

// 添加排序条件
    if (sortIndex.value !== undefined) {
      switch (sortIndex.value) {
        case 0: // 熟练度↓
          query.orderBy = "proficiencyPercent";
          query.orderDirection = "DESC";
          break;
        case 1: // 熟练度↑
          query.orderBy = "proficiencyPercent";
          query.orderDirection = "ASC";
          break;
        case 2: // 经验年数↓
          query.orderBy = "experienceYears";
          query.orderDirection = "DESC";
          break;
        case 3: // 经验年数↑
          query.orderBy = "experienceYears";
          query.orderDirection = "ASC";
          break;
        case 4: // 创建时间↓
          query.orderBy = "createdAt";
          query.orderDirection = "DESC";
          break;
        case 5: // 创建时间↑
          query.orderBy = "createdAt";
          query.orderDirection = "ASC";
          break;
      }
    }

    // 调用API
    const response = await SkillAPI.page(pageParam, query);

    if (response) {
      const { records = [], total = 0 } = response;

      // 处理数据格式
      const processedRecords = records.map(record => ({
        ...record,
        // 确保tags是字符串格式
        tags: Array.isArray(record.tags)
          ? record.tags.join(",")
          : record.tags || "",
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
    url: `/pages/skill/skill?id=${id}`,
  });
};

const editSkill = (id: number) => {
  uni.navigateTo({
    url: `/pages/skill/skill?id=${id}&edit=true`,
  });
};

const addNewSkill = () => {
  uni.navigateTo({
    url: "/pages/skill/skill",
  });
};

// 生命周期
onMounted(() => {
  // 保留第一次加载，usePageRefresh的immediate=true也会加载，但这里确保第一次加载
  loadListData(true);
});

onLoad((options) => {
  loadListData(true);
});

onReachBottom(() => {
  loadMore();
});
</script>

<style lang="scss">
.page-container {
  @extend .page-container;
}

.filter-container {
  @extend .card-container;
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

  .icon {
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

    .icon {
      font-size: 36rpx;
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
    }
  }
}

.tags-scroll {
  width: 100%;
  white-space: nowrap;
  padding: $padding-mini $padding-small;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-extra-light;

  .tags-container {
    display: flex;
    gap: $margin-mini;

    .tag-filter {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 8rpx 16rpx;
      background: $primary-color-light;
      border-radius: $border-radius-round;
      font-size: $font-size-extra-small;
      color: $primary-color;
      border: 1rpx solid $primary-border;

      .icon {
        font-size: 24rpx;
      }
    }

    .tag-clear {
      padding: 8rpx 16rpx;
      background: $background-color;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      border: 1rpx solid $border-color-light;
      display: flex;
      align-items: center;
    }
  }
}

.skill-item {
  @extend .card-container;
  width: 96%;
  align-items: center;
  margin-bottom: $margin-base;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  .skill-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $margin-mini;

    .skill-title-section {
      flex: 1;

      .skill-name {
        @extend .text-truncate;
        flex: 1;
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: 8rpx;
        display: block;
        @extend .text-truncate;
      }

      .skill-badges {
        display: flex;
        gap: 8rpx;

        .badge {
          padding: 4rpx 12rpx;
          border-radius: $border-radius-small;
          font-size: $font-size-extra-small;
          font-weight: $font-weight-medium;

          &-public {
            @extend .status-success;
          }

          &-certified {
            background: $primary-color-light;
            color: $primary-color;
            border: 1rpx solid $primary-border;
          }
        }
      }
    }

    .skill-level {
      .level-tag {
        padding: 4rpx 16rpx;
        border-radius: $border-radius-small;
        font-size: $font-size-extra-small;
        font-weight: $font-weight-medium;

        &.level-beginner {
          @extend .status-info;
        }

        &.level-intermediate {
          @extend .status-success;
        }

        &.level-advanced {
          @extend .status-warning;
        }

        &.level-expert {
          @extend .status-danger;
        }

        &.level-default {
          background: $background-color;
          color: $text-secondary;
          border: 1rpx solid $border-color-light;
        }
      }
    }
  }

  .skill-info {
    margin-bottom: $margin-base;

    .info-row {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-bottom: $margin-mini;

      .icon {
        font-size: 24rpx;
      }

      .info-text {
        font-size: $font-size-small;
        color: $text-regular;
      }

      .info-separator {
        color: $border-color;
        margin: 0 8rpx;
      }
    }

    .skill-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8rpx;

      .skill-tag {
        @extend .tag;
      }
    }
  }

  .proficiency-section {
    margin-bottom: $margin-base;

    .proficiency-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12rpx;

      .proficiency-label {
        font-size: $font-size-small;
        color: $text-secondary;
      }

      .proficiency-value {
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

        &.proficiency-beginner {
          background: linear-gradient(90deg, $info-color, color.adjust($info-color, $lightness: 20%));
        }

        &.proficiency-intermediate {
          background: linear-gradient(90deg, $success-color, color.adjust($success-color, $lightness: 20%));
        }

        &.proficiency-advanced {
          background: linear-gradient(90deg, $warning-color, color.adjust($warning-color, $lightness: 20%));
        }

        &.proficiency-expert {
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness: 20%));
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

  .skill-desc {
    font-size: $font-size-small;
    color: $text-secondary;
    line-height: 1.5;
    margin-bottom: $margin-base;
    padding: 16rpx;
    background: $background-color;
    border-radius: $border-radius-small;
    @extend .text-multi-truncate;
  }

  .skill-actions {
    display: flex;
    gap: $margin-mini;
    margin: $margin-base 0;

    .action-btn {
      flex: 1;
      padding: 16rpx;
      font-size: $font-size-small;
    }
  }

  .skill-footer {
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

  .loading-text {
    font-size: $font-size-base;
    color: $text-secondary;
  }

  .icon {
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

  .skill-item {
    .skill-actions {
      flex-direction: column;
    }

    .skill-footer {
      flex-direction: column;
      gap: 8rpx;
    }
  }
}
</style>