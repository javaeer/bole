<template>
  <view class="template-select-page">
    <view class="select-header">
      <text class="header-title">选择适合您的简历模板</text>
      <text class="header-subtitle">模板将决定简历的整体风格和布局</text>
    </view>

    <!-- 模板分类筛选 -->
    <view class="category-tabs" v-if="categories.length > 0">
      <scroll-view class="tabs-scroll" scroll-x="true" :scroll-left="scrollLeft">
        <view class="tabs-container">
          <view
            class="tab-item"
            :class="{ active: activeCategory === 'all' }"
            @click="changeCategory('all')"
          >
            <text class="tab-text">全部</text>
          </view>
          <view
            class="tab-item"
            v-for="category in categories"
            :key="category"
            :class="{ active: activeCategory === category }"
            @click="changeCategory(category)"
          >
            <text class="tab-text">{{ category }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 模板网格 -->
    <view class="template-grid">
      <view
        v-for="template in filteredTemplates"
        :key="template.id"
        class="template-card"
        :class="{ 'template-card-active': selectedTemplate === template.id }"
        @click="selectTemplate(template.id)"
      >
        <view class="template-preview" :style="getPreviewStyle(template)">
          <!-- 预览头部 -->
          <view class="preview-header">
            <view class="preview-avatar" v-if="showPhotoPreview(template)">
              <text class="avatar-placeholder">👤</text>
            </view>
            <view class="preview-title">
              <view class="preview-name-line"></view>
              <view class="preview-position-line"></view>
            </view>
          </view>

          <!-- 预览内容 -->
          <view class="preview-content">
            <view class="preview-section" v-for="n in 3" :key="n"></view>
          </view>

          <!-- 模板标签 -->
          <view class="template-badge" :class="getBadgeClass(template)">
            {{ template.name }}
          </view>

          <!-- 使用人数 -->
          <view class="template-stats" v-if="(template.users || 0) > 0">
            <text class="stats-icon">👥</text>
            <text class="stats-text">{{ template.users }}人使用</text>
          </view>
        </view>

        <!-- 模板信息 -->
        <view class="template-info">
          <text class="template-name text-truncate">{{ template.name }}</text>
          <text class="template-desc text-multi-truncate">{{ template.description || '经典简约设计' }}</text>
          <view class="template-tags">
            <text class="tag-item" v-if="isTwoColumn(template)">双栏</text>
            <text class="tag-item" v-if="showPhotoPreview(template)">带照片</text>
            <text class="tag-item">{{ template.category || '通用' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore && !loading" class="load-more" @click="handleLoadMore">
      <text class="load-more-text">加载更多</text>
    </view>

    <!-- 底部操作区域 -->
    <view class="action-area">
      <button
        class="next-button"
        :class="{ 'next-button-disabled': !selectedTemplate }"
        :disabled="!selectedTemplate"
        @click="enterFormPage"
      >
        {{ selectedTemplate ? '使用此模板' : '请选择模板' }}
      </button>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay flex-center">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useTemplate } from '@/composables/useTemplate'
import type { TemplateResult } from '@/types/template'

// 使用组合式函数
const {
  templateList,
  loading,
  hasMore,
  loadListTemplates,
  loadMore,
  setCurrentTemplate,
} = useTemplate()

// 响应式状态
const selectedTemplate = ref<number | null>(null)
const activeCategory = ref<string>('all')
const scrollLeft = ref(0)

// 模板分类
const categories = computed(() => {
  const categorySet = new Set<string>()
  templateList.value.forEach(template => {
    if (template.category) {
      categorySet.add(template.category)
    }
  })
  return Array.from(categorySet)
})

// 过滤模板
const filteredTemplates = computed(() => {
  if (activeCategory.value === 'all') {
    return templateList.value
  }
  return templateList.value.filter(template =>
    template.category === activeCategory.value
  )
})

// 分类切换
const changeCategory = (category: string) => {
  activeCategory.value = category
  scrollLeft.value = 0
}

// 选择模板
const selectTemplate = (id: number) => {
  selectedTemplate.value = id
}

// 进入表单页面
const enterFormPage = () => {
  if (selectedTemplate.value) {
    const template = templateList.value.find(t => t.id === selectedTemplate.value)
    if (template) {
      setCurrentTemplate(template)
      uni.navigateTo({
        url: `/pages/resumes/edit?templateId=${selectedTemplate.value}`
      })
    }
  }
}

// 获取预览样式
const getPreviewStyle = (template: TemplateResult) => {
  const style: Record<string, string> = {
    backgroundColor: template.globalStyle?.backgroundColor || '#ffffff'
  }

  if (template.globalStyle?.primaryColor) {
    style.borderColor = template.globalStyle.primaryColor
  }

  return style
}

// 获取徽章类
const getBadgeClass = (template: TemplateResult) => {
  const theme = template.globalStyle?.theme || 'classic'
  return `badge-${theme}`
}

// 检查是否双栏布局
const isTwoColumn = (template: TemplateResult) => {
  return template.globalLayout?.type === 'two-column'
}

// 检查是否显示照片
const showPhotoPreview = (template: TemplateResult) => {
  // 这里可以根据实际需求调整
  return template.globalLayout?.type === 'single-column'
}

// 加载更多
const handleLoadMore = async () => {
  if (!loading.value && hasMore.value) {
    await loadMore()
  }
}

// 初始化加载
onMounted(async () => {
  await loadListTemplates(1, { isActive: true })
})
</script>

<style lang="scss">

.template-select-page {
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-color-lighter 0%, $background-color-white 100%);
  padding-bottom: 160rpx;
}

/* 头部区域 */
.select-header {
  padding: $uni-spacing-row-base;
  text-align: center;
}

.header-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: calc($uni-spacing-col-sm / 2);
}

.header-subtitle {
  display: block;
  font-size: $uni-font-size-base;
  color: $text-secondary;
}

/* 分类标签 */
.category-tabs {
  background: $uni-bg-color;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.tabs-container {
  display: inline-flex;
  align-items: center;
  gap: $uni-spacing-col-base;
}

.tab-item {
  padding: calc($uni-spacing-col-sm / 2) $uni-spacing-col-base;
  border-radius: $uni-border-radius-circle;
  font-size: $uni-font-size-base;
  color: $text-regular;
  background: $background-color;
  cursor: pointer;
  transition: all $transition-fast;
  white-space: nowrap;
  flex-shrink: 0;

  &.active {
    background: $primary-color;
    color: $uni-bg-color;
    font-weight: $font-weight-medium;
  }
}

/* 模板网格 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $uni-spacing-col-base;
  padding: $uni-spacing-row-base;
}

.template-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  overflow: hidden;
  box-shadow: $card-shadow;
  transition: all $transition-normal;
  cursor: pointer;
  border: 2rpx solid transparent;

  &:active {
    transform: translateY(-2rpx);
    box-shadow: $card-hover-shadow;
  }

  &-active {
    border-color: $primary-color;
    background: linear-gradient(135deg, $primary-color-lighter 0%, $uni-bg-color 100%);
  }
}

.template-preview {
  height: 320rpx;
  border-radius: calc($border-radius-large / 2) calc($border-radius-large / 2) 0 0;
  padding: $uni-spacing-col-base;
  position: relative;
  border-bottom: 1rpx solid $border-color-extra-light;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.preview-header {
  display: flex;
  align-items: center;
  margin-bottom: $uni-spacing-col-base;
}

.preview-avatar {
  width: 60rpx;
  height: 60rpx;
  background: $border-color;
  border-radius: $uni-border-radius-circle;
  margin-right: $uni-spacing-col-sm;
  @extend .flex-center;
}

.avatar-placeholder {
  font-size: $uni-font-size-lg;
  color: $text-placeholder;
}

.preview-title {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: calc($uni-spacing-col-sm / 2);
}

.preview-name-line {
  height: 16rpx;
  background: $text-primary;
  border-radius: 4rpx;
  width: 60%;
}

.preview-position-line {
  height: 12rpx;
  background: $text-secondary;
  border-radius: 4rpx;
  width: 40%;
}

.preview-content {
  .preview-section {
    height: 12rpx;
    background: $border-color-light;
    border-radius: 4rpx;
    margin-bottom: 8rpx;

    &:nth-child(2) {
      width: 80%;
    }

    &:nth-child(3) {
      width: 60%;
    }
  }
}

.template-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  padding: calc($uni-spacing-col-sm / 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-circle;
  font-size: $uni-font-size-sm;
  font-weight: $font-weight-medium;
  color: $uni-bg-color;

  &.badge-classic {
    background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -20%) 100%);
  }

  &.badge-modern {
    background: linear-gradient(135deg, #007aff 0%, #0056cc 100%);
  }

  &.badge-creative {
    background: linear-gradient(135deg, #e6a23c 0%, #b8821e 100%);
  }

  &.badge-professional {
    background: linear-gradient(135deg, #303133 0%, #000000 100%);
  }

  &.badge-simple {
    background: linear-gradient(135deg, #909399 0%, #606266 100%);
  }
}

.template-stats {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  gap: calc($uni-spacing-col-sm / 2);
  background: rgba($uni-bg-color, 0.8);
  padding: calc($uni-spacing-col-sm / 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.template-info {
  padding: $uni-spacing-col-base;
  display: flex;
  flex-direction: column;
  gap: calc($uni-spacing-col-sm / 2);
}

.template-desc {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  line-height: 1.4;
}

.template-tags {
  display: flex;
  flex-wrap: wrap;
  gap: calc($uni-spacing-col-sm / 2);
  margin-top: calc($uni-spacing-col-sm / 2);
}

.tag-item {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
  background: $background-color;
  padding: calc($uni-spacing-col-sm / 4) calc($uni-spacing-col-sm / 2);
  border-radius: $uni-border-radius-sm;
  border: 1rpx solid $border-color-light;
}

/* 加载更多 */
.load-more {
  @extend .flex-center;
  padding: $uni-spacing-row-base;
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
    background: $primary-color-light;
    opacity: 0.8;
  }
}

/* 底部操作区域 */
.action-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $uni-spacing-col-base $uni-spacing-row-base;
  background: linear-gradient(to top, $uni-bg-color 80%, transparent);
  z-index: $z-index-dropdown;
}

.next-button {
  width: 100%;
  height: $button-height;
  background: $primary-color;
  border-radius: $uni-border-radius-lg;
  color: $uni-bg-color;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  border: none;
  transition: all $transition-normal;

  &:active {
    transform: scale(0.98);
    box-shadow: $button-active-shadow;
    background: color.adjust($primary-color, $lightness: -10%);
  }

  &-disabled {
    background: $button-disabled-bg;
    opacity: $button-disabled-opacity;
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($uni-bg-color, 0.8);
  z-index: $z-index-modal;
}

.loading-content {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $box-shadow-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $uni-spacing-col-sm;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $uni-font-size-base;
  color: $text-regular;
}

/* 响应式调整 */
@media (max-width: $screen-md) {
  .template-grid {
    grid-template-columns: 1fr;
  }

  .tabs-container {
    gap: $uni-spacing-col-sm;
  }
}
</style>