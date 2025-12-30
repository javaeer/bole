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
              {{ selectedCategory || '全部' }}
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
              {{ selectedLevel || '全部' }}
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
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <view v-else-if="!loading && listData.length === 0" class="empty-state">
        <text class="icon">ℹ️</text>
        <text class="empty-text">暂无技能数据</text>
        <button class="btn btn-primary" @click="addNewSkill">添加新技能</button>
      </view>

      <!-- 列表内容 -->
      <view v-else>
        <view
          v-for="item in listData"
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
            <view class="skill-tags" v-if="item.tags">
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
    <button class="add-btn" @click="addNewSkill">
      <view class="icon-plus">+</view>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { SkillItem } from "@/types/skill";
import { dateUtils } from "../../utils/date";

// 响应式数据
const searchKeywords = ref('')
const selectedCategory = ref('')
const selectedLevel = ref('')
const filterTags = ref<string[]>([])
const sortIndex = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const hasMore = ref(true)

// 模拟数据
const mockData: SkillItem[] = [
  {
    id: 1,
    createdAt: "2025-12-17 20:06:50",
    updatedAt: "2025-12-18 14:30:25",
    deleted: 0,
    userId: 1,
    name: "Java编程",
    level: "高级",
    category: "编程语言",
    description: "熟练掌握Java语言特性，包括集合、多线程、IO等。熟悉Spring框架，有微服务开发经验。",
    proficiencyPercent: 85,
    experienceYears: 5.5,
    isCertified: true,
    certificateName: "Oracle Certified Professional",
    certificateDate: "2022-03-15",
    tags: "Java,后端,编程,Spring",
    isPublic: true,
    sort: 1
  },
  {
    id: 2,
    createdAt: "2025-12-16 09:15:30",
    updatedAt: "2025-12-17 11:20:45",
    deleted: 0,
    userId: 1,
    name: "Python数据分析",
    level: "中级",
    category: "数据分析",
    description: "熟悉Pandas、NumPy等数据处理库，能够进行数据清洗、分析和可视化。",
    proficiencyPercent: 75,
    experienceYears: 3,
    isCertified: false,
    certificateName: null,
    certificateDate: null,
    tags: "Python,数据分析,机器学习,Pandas",
    isPublic: true,
    sort: 2
  },
  {
    id: 3,
    createdAt: "2025-12-15 16:45:20",
    updatedAt: "2025-12-16 10:10:10",
    deleted: 0,
    userId: 1,
    name: "React前端开发",
    level: "高级",
    category: "前端框架",
    description: "精通React及生态，熟悉Hooks、Redux、TypeScript，有大型项目开发经验。",
    proficiencyPercent: 90,
    experienceYears: 4,
    isCertified: true,
    certificateName: "React Developer Certification",
    certificateDate: "2023-08-20",
    tags: "React,前端,TypeScript,Redux",
    isPublic: true,
    sort: 3
  },
  {
    id: 4,
    createdAt: "2025-12-14 13:25:40",
    updatedAt: "2025-12-15 09:45:15",
    deleted: 0,
    userId: 1,
    name: "Docker容器化",
    level: "中级",
    category: "运维部署",
    description: "熟悉Docker容器技术，能够编写Dockerfile，管理容器编排。",
    proficiencyPercent: 70,
    experienceYears: 2,
    isCertified: true,
    certificateName: "Docker Certified Associate",
    certificateDate: "2023-11-10",
    tags: "Docker,容器,运维,CI/CD",
    isPublic: false,
    sort: 4
  },
  {
    id: 5,
    createdAt: "2025-12-13 11:30:50",
    updatedAt: "2025-12-14 15:20:30",
    deleted: 0,
    userId: 1,
    name: "MySQL数据库",
    level: "高级",
    category: "数据库",
    description: "精通MySQL数据库设计、优化和调优，熟悉SQL语句编写和索引优化。",
    proficiencyPercent: 88,
    experienceYears: 6,
    isCertified: true,
    certificateName: "MySQL 8.0 Database Administrator",
    certificateDate: "2024-01-15",
    tags: "MySQL,数据库,SQL,优化",
    isPublic: true,
    sort: 5
  }
]

const listData = ref<SkillItem[]>([])
const filteredData = ref<SkillItem[]>([])

// 筛选选项
const categoryOptions = ['全部', '编程语言', '前端框架', '后端框架', '数据库', '数据分析', '人工智能', '运维部署', '工具软件', '其他']
const levelOptions = ['全部', '初级', '中级', '高级', '专家']
const sortOptions = ['熟练度↓', '熟练度↑', '经验年数↓', '经验年数↑', '创建时间↓', '创建时间↑']

// 索引计算
const categoryIndex = computed(() =>
  selectedCategory.value ? categoryOptions.indexOf(selectedCategory.value) : 0
)

const levelIndex = computed(() =>
  selectedLevel.value ? levelOptions.indexOf(selectedLevel.value) : 0
)

// 获取等级样式
const getLevelClass = (level: string) => {
  switch(level) {
    case '初级': return 'level-beginner'
    case '中级': return 'level-intermediate'
    case '高级': return 'level-advanced'
    case '专家': return 'level-expert'
    default: return 'level-default'
  }
}

// 获取熟练度样式
const getProficiencyClass = (percent: number) => {
  if (percent >= 90) return 'proficiency-expert'
  if (percent >= 70) return 'proficiency-advanced'
  if (percent >= 50) return 'proficiency-intermediate'
  return 'proficiency-beginner'
}

// 解析标签字符串
const getTagsArray = (tagsStr: string): string[] => {
  if (!tagsStr) return []
  return tagsStr.split(',').map(tag => tag.trim()).filter(tag => tag)
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadData(true)
}

const clearSearch = () => {
  searchKeywords.value = ''
  currentPage.value = 1
  loadData(true)
}

// 筛选处理
const onCategoryChange = (e: any) => {
  const index = e.detail.value
  selectedCategory.value = index === 0 ? '' : categoryOptions[index]
  currentPage.value = 1
  loadData(true)
}

const onLevelChange = (e: any) => {
  const index = e.detail.value
  selectedLevel.value = index === 0 ? '' : levelOptions[index]
  currentPage.value = 1
  loadData(true)
}

const onSortChange = (e: any) => {
  sortIndex.value = e.detail.value
  sortData()
}

// 标签筛选处理
const addTagFilter = (tag: string) => {
  if (!filterTags.value.includes(tag)) {
    filterTags.value.push(tag)
    currentPage.value = 1
    loadData(true)
  }
}

const removeTag = (tag: string) => {
  const index = filterTags.value.indexOf(tag)
  if (index > -1) {
    filterTags.value.splice(index, 1)
    currentPage.value = 1
    loadData(true)
  }
}

const clearTags = () => {
  filterTags.value = []
  currentPage.value = 1
  loadData(true)
}

// 排序数据
const sortData = () => {
  switch(sortIndex.value) {
    case 0: // 熟练度↓
      filteredData.value.sort((a, b) => b.proficiencyPercent - a.proficiencyPercent)
      break
    case 1: // 熟练度↑
      filteredData.value.sort((a, b) => a.proficiencyPercent - b.proficiencyPercent)
      break
    case 2: // 经验年数↓
      filteredData.value.sort((a, b) => b.experienceYears - a.experienceYears)
      break
    case 3: // 经验年数↑
      filteredData.value.sort((a, b) => a.experienceYears - b.experienceYears)
      break
    case 4: // 创建时间↓
      filteredData.value.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      break
    case 5: // 创建时间↑
      filteredData.value.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      break
  }

  // 更新分页数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  listData.value = filteredData.value.slice(start, end)
}

// 加载数据
const loadData = (reset = false) => {
  if (loading.value) return

  loading.value = true

  if (reset) {
    currentPage.value = 1
    hasMore.value = true
    listData.value = []
  }

  // 模拟API请求延迟
  setTimeout(() => {
    // 筛选数据
    let filtered = [...mockData]

    // 关键字搜索
    if (searchKeywords.value) {
      const keyword = searchKeywords.value.toLowerCase()
      filtered = filtered.filter(item =>
        item.name.toLowerCase().includes(keyword) ||
        item.category.toLowerCase().includes(keyword) ||
        item.tags.toLowerCase().includes(keyword) ||
        item.description.toLowerCase().includes(keyword)
      )
    }
    // 分类筛选
    if (selectedCategory.value) {
      filtered = filtered.filter(item => item.category === selectedCategory.value)
    }

    // 等级筛选
    if (selectedLevel.value) {
      filtered = filtered.filter(item => item.level === selectedLevel.value)
    }

    // 标签筛选
    if (filterTags.value.length > 0) {
      filtered = filtered.filter(item => {
        const itemTags = getTagsArray(item.tags)
        return filterTags.value.every(tag => itemTags.includes(tag))
      })
    }

    filteredData.value = filtered

    // 排序
    sortData()

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filtered.length
    loading.value = false
    currentPage.value++
  }, 500)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return

  // 模拟API请求延迟
  loading.value = true
  setTimeout(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const pageData = filteredData.value.slice(start, end)

    listData.value = [...listData.value, ...pageData]

    // 更新是否有更多数据
    hasMore.value = listData.value.length < filteredData.value.length
    loading.value = false
    currentPage.value++
  }, 500)
}

// 页面跳转
const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/skill/detail?id=${id}`
  })
}

const editSkill = (id: number) => {
  uni.navigateTo({
    url: `/pages/skill/detail?id=${id}&edit=true`
  })
}

const addNewSkill = () => {
  uni.navigateTo({
    url: '/pages/skill/detail'
  })
}

// 生命周期
onMounted(() => {
  loadData(true)
})

onLoad((options) => {
  const refresh = options?.refresh === 'true'
  if (refresh) {
    loadData(true)
  }
})

onReachBottom(() => {
  loadMore()
})
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

.list-scroll {
  height: calc(100vh - 300rpx);
  padding: $padding-small;
}

.skill-item {
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
        font-size: $font-size-medium;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: 8rpx;
        display: block;
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
            background: $success-bg;
            color: $success-color;
            border: 1rpx solid $success-border;
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
          background: $info-bg;
          color: $info-color;
          border: 1rpx solid $info-border;
        }

        &.level-intermediate {
          background: $success-bg;
          color: $success-color;
          border: 1rpx solid $success-border;
        }

        &.level-advanced {
          background: $warning-bg;
          color: $warning-color;
          border: 1rpx solid $warning-border;
        }

        &.level-expert {
          background: $danger-bg;
          color: $danger-color;
          border: 1rpx solid $danger-border;
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
        padding: 4rpx 12rpx;
        background: $background-color;
        border-radius: $border-radius-small;
        font-size: $font-size-extra-small;
        color: $text-secondary;
        border: 1rpx solid $border-color-light;
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
          background: linear-gradient(90deg, $info-color, color.adjust($info-color, $lightness:  20%));
        }

        &.proficiency-intermediate {
          background: linear-gradient(90deg, $success-color, color.adjust($success-color, $lightness:  20%));
        }

        &.proficiency-advanced {
          background: linear-gradient(90deg, $warning-color, color.adjust($warning-color, $lightness:  20%));
        }

        &.proficiency-expert {
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness:  20%));
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