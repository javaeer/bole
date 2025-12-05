<template>
  <view class="scan-container">
    <!-- 顶部操作栏 -->
    <view class="scan-header">
      <view class="header-title">简历扫描分析</view>
      <view class="header-actions">
        <button class="btn-icon" @click="handleExport">
          <text class="icon">📤</text>
        </button>
        <button class="btn-icon" @click="handleHelp">
          <text class="icon">❓</text>
        </button>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="scan-content" scroll-y>
      <!-- 上传区域 -->
      <view class="upload-section section">
        <view class="upload-card" @click="handleUpload">
          <view class="upload-icon">📄</view>
          <text class="upload-title">上传简历文件</text>
          <text class="upload-subtitle">支持 PDF、Word、图片格式</text>
          <view class="upload-progress" v-if="uploading">
            <view class="progress-bar" :style="{ width: uploadProgress + '%' }"></view>
          </view>
        </view>
        <view class="recent-files" v-if="recentFiles.length">
          <text class="section-subtitle">最近文件</text>
          <view class="file-list">
            <view
              v-for="file in recentFiles"
              :key="file.id"
              class="file-item"
              @click="handleSelectFile(file)"
            >
              <view class="file-icon">{{ file.type === 'pdf' ? '📕' : '📘' }}</view>
              <view class="file-info">
                <text class="file-name">{{ file.name }}</text>
                <text class="file-time">{{ file.time }}</text>
              </view>
              <text class="file-score">{{ file.score }}分</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 分析概览 -->
      <view class="overview-section section" v-if="analysisData">
        <view class="section-header">
          <text class="section-title">分析概览</text>
          <text class="scan-time">扫描时间: {{ analysisData.scanTime }}</text>
        </view>

        <view class="overview-cards">
          <view class="overview-card score-card">
            <text class="score-label">综合评分</text>
            <view class="score-circle" :style="scoreStyle">
              <text class="score-number">{{ analysisData.overallScore }}</text>
            </view>
            <text class="score-level">{{ getScoreLevel(analysisData.overallScore) }}</text>
          </view>

          <view class="overview-grid">
            <view class="metric-card">
              <text class="metric-label">完整性</text>
              <text class="metric-value">{{ analysisData.completeness }}%</text>
              <view class="metric-bar">
                <view
                  class="metric-fill"
                  :style="{ width: analysisData.completeness + '%' }"
                ></view>
              </view>
            </view>

            <view class="metric-card">
              <text class="metric-label">专业性</text>
              <text class="metric-value">{{ analysisData.professionalism }}%</text>
              <view class="metric-bar">
                <view
                  class="metric-fill"
                  :style="{ width: analysisData.professionalism + '%' }"
                ></view>
              </view>
            </view>

            <view class="metric-card">
              <text class="metric-label">关键词匹配</text>
              <text class="metric-value">{{ analysisData.keywordMatch }}%</text>
              <view class="metric-bar">
                <view
                  class="metric-fill"
                  :style="{ width: analysisData.keywordMatch + '%' }"
                ></view>
              </view>
            </view>

            <view class="metric-card">
              <text class="metric-label">格式规范</text>
              <text class="metric-value">{{ analysisData.format }}%</text>
              <view class="metric-bar">
                <view
                  class="metric-fill"
                  :style="{ width: analysisData.format + '%' }"
                ></view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 详细分析 -->
      <view class="analysis-section section" v-if="analysisData">
        <view class="section-header">
          <text class="section-title">详细分析</text>
          <view class="analysis-tabs">
            <text
              v-for="tab in tabs"
              :key="tab.id"
              class="tab-item"
              :class="{ active: activeTab === tab.id }"
              @click="activeTab = tab.id"
            >
              {{ tab.label }}
            </text>
          </view>
        </view>

        <!-- 优势分析 -->
        <view v-if="activeTab === 'strengths'" class="tab-content">
          <view class="strength-list">
            <view
              v-for="(strength, index) in analysisData.strengths"
              :key="index"
              class="strength-item"
            >
              <view class="strength-icon">✅</view>
              <view class="strength-content">
                <text class="strength-title">{{ strength.title }}</text>
                <text class="strength-desc">{{ strength.description }}</text>
                <view class="strength-tags">
                  <text
                    v-for="tag in strength.tags"
                    :key="tag"
                    class="tag"
                  >
                    {{ tag }}
                  </text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 改进建议 -->
        <view v-if="activeTab === 'improvements'" class="tab-content">
          <view class="improvement-list">
            <view
              v-for="(improvement, index) in analysisData.improvements"
              :key="index"
              class="improvement-item"
            >
              <view class="improvement-icon">💡</view>
              <view class="improvement-content">
                <view class="improvement-header">
                  <text class="improvement-title">{{ improvement.title }}</text>
                  <text class="improvement-priority" :class="getPriorityClass(improvement.priority)">
                    {{ improvement.priority }}
                  </text>
                </view>
                <text class="improvement-desc">{{ improvement.description }}</text>
                <view class="improvement-actions">
                  <button
                    v-for="action in improvement.actions"
                    :key="action"
                    class="btn-action"
                    @click="handleQuickFix(improvement, action)"
                  >
                    {{ action }}
                  </button>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 关键词分析 -->
        <view v-if="activeTab === 'keywords'" class="tab-content">
          <view class="keyword-cloud">
            <text
              v-for="(keyword, index) in analysisData.keywords"
              :key="index"
              class="keyword-item"
              :style="{ fontSize: getKeywordSize(keyword.weight) }"
            >
              {{ keyword.text }}
            </text>
          </view>
          <view class="keyword-stats">
            <view class="stat-item">
              <text class="stat-number">{{ analysisData.keywords.length }}</text>
              <text class="stat-label">关键词总数</text>
            </view>
            <view class="stat-item">
              <text class="stat-number">{{ analysisData.industryKeywords }}</text>
              <text class="stat-label">行业关键词</text>
            </view>
            <view class="stat-item">
              <text class="stat-number">{{ analysisData.skillKeywords }}</text>
              <text class="stat-label">技能关键词</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 一键优化 -->
      <view class="optimize-section section" v-if="analysisData">
        <view class="section-header">
          <text class="section-title">一键优化</text>
        </view>
        <view class="optimize-options">
          <button
            v-for="option in optimizeOptions"
            :key="option.id"
            class="btn-optimize"
            :class="{ 'btn-optimize--active': option.active }"
            @click="toggleOptimizeOption(option.id)"
          >
            <text class="optimize-icon">{{ option.icon }}</text>
            <text class="optimize-text">{{ option.text }}</text>
          </button>
        </view>
        <button class="btn-primary" @click="handleOptimize" :disabled="optimizing">
          <text v-if="!optimizing">立即优化</text>
          <view v-else class="loading-spinner"></view>
        </button>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="scan-footer">
      <button class="btn-secondary" @click="handleRescan">
        <text class="btn-icon">🔄</text>
        <text class="btn-text">重新扫描</text>
      </button>
      <button class="btn-primary" @click="handleSave">
        <text class="btn-icon">💾</text>
        <text class="btn-text">保存报告</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

// 状态管理
const uploading = ref(false)
const uploadProgress = ref(0)
const activeTab = ref('strengths')
const optimizing = ref(false)
const analysisData = ref<any>(null)

// 模拟数据
const recentFiles = ref([
  { id: 1, name: '前端开发工程师简历.pdf', type: 'pdf', time: '2023-12-15', score: 85 },
  { id: 2, name: '产品经理简历.docx', type: 'word', time: '2023-12-10', score: 92 },
  { id: 3, name: 'UI设计师简历.pdf', type: 'pdf', time: '2023-12-05', score: 78 }
])

const tabs = ref([
  { id: 'strengths', label: '优势分析' },
  { id: 'improvements', label: '改进建议' },
  { id: 'keywords', label: '关键词' }
])

const optimizeOptions = ref([
  { id: 'format', icon: '🎨', text: '格式优化', active: true },
  { id: 'keywords', icon: '🔑', text: '关键词优化', active: true },
  { id: 'content', icon: '📝', text: '内容优化', active: false },
  { id: 'structure', icon: '🏗️', text: '结构优化', active: true }
])

// 计算属性
const scoreStyle = computed(() => {
  if (!analysisData.value) return {}
  const score = analysisData.value.overallScore
  let color = '#f56c6c' // 红色
  if (score >= 80) color = '#67c23a' // 绿色
  else if (score >= 60) color = '#e6a23c' // 橙色

  return {
    background: `conic-gradient(${color} ${score}%, #eee ${score}% 100%)`
  }
})

// 方法
const handleUpload = () => {
  uploading.value = true
  uploadProgress.value = 0

  const interval = setInterval(() => {
    uploadProgress.value += 10
    if (uploadProgress.value >= 100) {
      clearInterval(interval)
      uploading.value = false
      // 模拟分析完成
      setTimeout(() => {
        loadAnalysisData()
      }, 500)
    }
  }, 200)
}

const loadAnalysisData = () => {
  analysisData.value = {
    scanTime: '2023-12-20 14:30',
    overallScore: 85,
    completeness: 92,
    professionalism: 88,
    keywordMatch: 76,
    format: 81,
    strengths: [
      {
        title: '教育背景突出',
        description: '毕业于知名高校，专业对口，学历层次符合岗位要求',
        tags: ['985院校', '硕士学历', '专业匹配']
      },
      {
        title: '项目经验丰富',
        description: '参与过多个大型项目，具备完整的项目开发经验',
        tags: ['大型项目', '全流程', '团队协作']
      },
      {
        title: '技术栈全面',
        description: '掌握岗位要求的核心技术栈，学习能力强',
        tags: ['Vue3', 'TypeScript', 'Node.js']
      }
    ],
    improvements: [
      {
        title: '工作经历描述不够量化',
        description: '建议使用具体数据和成果来展示工作成效',
        priority: '高',
        actions: ['添加量化指标', '突出成果', '使用STAR法则']
      },
      {
        title: '技能描述不够具体',
        description: '建议详细描述掌握程度和应用场景',
        priority: '中',
        actions: ['分级描述', '添加案例', '说明熟练度']
      },
      {
        title: '格式布局可以优化',
        description: '简历排版可以更加清晰，提高可读性',
        priority: '低',
        actions: ['调整间距', '优化字体', '增强层次']
      }
    ],
    keywords: [
      { text: 'Vue.js', weight: 5 },
      { text: 'React', weight: 3 },
      { text: 'TypeScript', weight: 4 },
      { text: 'Node.js', weight: 4 },
      { text: 'Webpack', weight: 2 },
      { text: '性能优化', weight: 3 },
      { text: '团队协作', weight: 2 },
      { text: '项目管理', weight: 3 }
    ],
    industryKeywords: 12,
    skillKeywords: 18
  }
}

const getScoreLevel = (score: number) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '合格'
  return '待改进'
}

const getPriorityClass = (priority: string) => {
  switch (priority) {
    case '高': return 'priority-high'
    case '中': return 'priority-medium'
    case '低': return 'priority-low'
    default: return ''
  }
}

const getKeywordSize = (weight: number) => {
  const base = 24
  const increment = 6
  return `${base + weight * increment}rpx`
}

const handleQuickFix = (improvement: any, action: string) => {
  console.log('快速修复:', improvement.title, action)
  // 这里可以触发具体的优化操作
}

const toggleOptimizeOption = (id: string) => {
  const option = optimizeOptions.value.find(opt => opt.id === id)
  if (option) {
    option.active = !option.active
  }
}

const handleOptimize = () => {
  optimizing.value = true
  setTimeout(() => {
    optimizing.value = false
    uni.showToast({
      title: '优化完成',
      icon: 'success'
    })
  }, 2000)
}

const handleExport = () => {
  uni.showToast({
    title: '导出功能开发中',
    icon: 'none'
  })
}

const handleHelp = () => {
  uni.showToast({
    title: '查看帮助文档',
    icon: 'none'
  })
}

const handleRescan = () => {
  analysisData.value = null
  uploading.value = false
  uploadProgress.value = 0
}

const handleSave = () => {
  uni.showToast({
    title: '报告已保存',
    icon: 'success'
  })
}

const handleSelectFile = (file: any) => {
  console.log('选择文件:', file)
  // 加载对应文件的分析数据
  loadAnalysisData()
}

onMounted(() => {
  // 页面加载时无分析数据
})
</script>

<style scoped lang="scss">
.scan-container {
  background-color: $background-color;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.scan-header {
  background: $background-color-white;
  padding: 20rpx $padding-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: $box-shadow-light;
  position: sticky;
  top: 0;
  z-index: $z-index-base + 1;
}

.header-title {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.header-actions {
  display: flex;
  gap: $margin-small;
}

.btn-icon {
  background: transparent;
  border: none;
  padding: $padding-mini;
  font-size: $font-size-medium;
}

.scan-content {
  flex: 1;
  padding: $padding-base;
}

/* 上传区域 */
.upload-section {
  margin-bottom: $margin-base;
}

.upload-card {
  background: linear-gradient(135deg, $primary-lighter 0%, color.adjust($primary-lighter, $lightness:  5%) 100%);
  border: 2rpx dashed $primary-color;
  border-radius: $border-radius;
  padding: $padding-base * 1.5;
  text-align: center;
  margin-bottom: $margin-base;
  cursor: pointer;
  transition: all $transition-duration;
  position: relative;

  &:active {
    transform: scale(0.98);
    background: color.adjust($primary-lighter, $lightness:  -5%);
  }
}

.upload-icon {
  font-size: $font-size-extra-large * 2;
  margin-bottom: $margin-base;
}

.upload-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.upload-subtitle {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
}

.upload-progress {
  margin-top: $margin-base;
  height: 8rpx;
  background: $border-color-light;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: $primary-color;
  transition: width $transition-duration;
}

.recent-files {
  .section-subtitle {
    display: block;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-regular;
    margin-bottom: $margin-base;
  }
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
}

.file-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius-small;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }
}

.file-icon {
  font-size: $font-size-extra-large;
  margin-right: $margin-small;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.file-name {
  font-size: $font-size-base;
  color: $text-primary;
  margin-bottom: calc($margin-mini / 2);
  @extend .text-ellipsis;
}

.file-time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.file-score {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $primary-color;
  padding: $padding-mini;
  background: $primary-lighter;
  border-radius: $border-radius-small;
}

/* 分析概览 */
.overview-section {
  .section-header {
    margin-bottom: $margin-base * 1.5;
  }
}

.scan-time {
  font-size: $font-size-small;
  color: $text-secondary;
}

.overview-cards {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.score-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
}

.score-label {
  font-size: $font-size-base;
  color: $text-regular;
  margin-bottom: $margin-base;
}

.score-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $margin-base;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 10rpx;
    left: 10rpx;
    right: 10rpx;
    bottom: 10rpx;
    background: $background-color-white;
    border-radius: 50%;
  }
}

.score-number {
  font-size: $font-size-extra-large * 1.5;
  font-weight: $font-weight-bold;
  color: $text-primary;
  position: relative;
  z-index: 1;
}

.score-level {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-regular;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-small;
}

.metric-card {
  background: $background-color;
  padding: $padding-base;
  border-radius: $border-radius;
}

.metric-label {
  display: block;
  font-size: $font-size-small;
  color: $text-regular;
  margin-bottom: $margin-mini;
}

.metric-value {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-base;
}

.metric-bar {
  height: 8rpx;
  background: $border-color-light;
  border-radius: 4rpx;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  background: $primary-color;
  transition: width $transition-duration;
}

/* 详细分析 */
.analysis-section {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: $margin-base;
  }
}

.analysis-tabs {
  display: flex;
  background: $background-color;
  padding: $padding-mini;
  border-radius: $border-radius;
  width: 100%;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: $padding-small;
  font-size: $font-size-base;
  color: $text-secondary;
  transition: all $transition-fast;
  border-radius: $border-radius-small;

  &.active {
    background: $background-color-white;
    color: $primary-color;
    font-weight: $font-weight-medium;
    box-shadow: $box-shadow-light;
  }
}

.tab-content {
  margin-top: $margin-base;
}

.strength-list, .improvement-list {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.strength-item, .improvement-item {
  display: flex;
  gap: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
}

.strength-icon, .improvement-icon {
  font-size: $font-size-medium;
  flex-shrink: 0;
}

.strength-content, .improvement-content {
  flex: 1;
}

.strength-title, .improvement-title {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.improvement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-mini;
}

.improvement-priority {
  font-size: $font-size-extra-small;
  font-weight: $font-weight-bold;
  padding: calc($padding-mini / 2) $padding-mini;
  border-radius: $border-radius-small;

  &.priority-high {
    background: $danger-light;
    color: $danger-color;
  }

  &.priority-medium {
    background: $warning-light;
    color: $warning-color;
  }

  &.priority-low {
    background: $primary-lighter;
    color: $primary-color;
  }
}

.strength-desc, .improvement-desc {
  display: block;
  font-size: $font-size-small;
  color: $text-regular;
  line-height: 1.5;
  margin-bottom: $margin-base;
}

.strength-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
}

.tag {
  font-size: $font-size-extra-small;
  color: $primary-color;
  background: $primary-lighter;
  padding: calc($padding-mini / 2) $padding-mini;
  border-radius: $border-radius-small;
}

.improvement-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
}

.btn-action {
  background: $background-color-white;
  border: 1rpx solid $border-color;
  border-radius: $border-radius-small;
  padding: $padding-mini $padding-small;
  font-size: $font-size-extra-small;
  color: $text-regular;

  &:active {
    background: $background-color;
  }
}

.keyword-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-base;
  justify-content: center;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
  margin-bottom: $margin-base;
}

.keyword-item {
  color: $primary-color;
  font-weight: $font-weight-medium;
  padding: $padding-mini $padding-small;
  background: $primary-lighter;
  border-radius: $border-radius;
  transition: all $transition-fast;

  &:active {
    transform: scale(1.1);
  }
}

.keyword-stats {
  display: flex;
  justify-content: space-around;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .stat-number {
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    color: $primary-color;
    margin-bottom: calc($margin-mini / 2);
  }

  .stat-label {
    font-size: $font-size-extra-small;
    color: $text-secondary;
  }
}

/* 一键优化 */
.optimize-section {
  margin-bottom: $margin-base;
}

.optimize-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-small;
  margin-bottom: $margin-base;
}

.btn-optimize {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base;
  background: $background-color;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius;
  transition: all $transition-fast;

  &:active {
    transform: scale(0.98);
  }

  &--active {
    background: $primary-lighter;
    border-color: $primary-color;
  }
}

.optimize-icon {
  font-size: $font-size-large;
  margin-bottom: $margin-mini;
}

.optimize-text {
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.btn-primary {
  width: 100%;
  height: $button-height;
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness:  -10%) 100%);
  color: $background-color-white;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;

  &:disabled {
    opacity: $button-disabled-opacity;
  }
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid rgba($background-color-white, 0.3);
  border-top-color: $background-color-white;
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 底部操作栏 */
.scan-footer {
  background: $background-color-white;
  padding: $padding-small $padding-base;
  display: flex;
  gap: $margin-base;
  box-shadow: $box-shadow-light;
  position: sticky;
  bottom: 0;
}

.btn-secondary {
  flex: 1;
  height: $button-height;
  background: $background-color-white;
  border: 2rpx solid $border-color;
  border-radius: $border-radius;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;

  &:active {
    background: $background-color;
  }
}

.btn-text {
  font-size: $font-size-base;
}
</style>