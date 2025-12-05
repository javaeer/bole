<template>
  <view class="company-detail-container">
    <!-- 公司头部信息 -->
    <view class="company-header">
      <view class="company-basic-info">
        <image :src="companyInfo.logo" class="company-logo" mode="aspectFit" />
        <view class="company-text-info">
          <view class="company-name-rating">
            <text class="company-name">{{ companyInfo.name }}</text>
            <view class="rating-stars">
              <text v-for="n in 5" :key="n" class="star-icon"
                    :class="{ 'active': n <= Math.floor(companyInfo.rating) }">
                ★
              </text>
              <text class="rating-score">{{ companyInfo.rating.toFixed(1) }}</text>
            </view>
          </view>
          <text class="company-industry">{{ companyInfo.industry }} · {{ companyInfo.scale }}</text>
          <view class="company-tags">
            <text v-for="tag in companyInfo.tags" :key="tag" class="tag">{{ tag }}</text>
          </view>
        </view>
      </view>

      <!-- 关键指标 -->
      <view class="key-indicators">
        <view class="indicator-item">
          <text class="indicator-value">{{ companyInfo.evaluationCount }}</text>
          <text class="indicator-label">评价</text>
        </view>
        <view class="divider"></view>
        <view class="indicator-item">
          <text class="indicator-value">{{ companyInfo.riskLevel }}</text>
          <text class="indicator-label">风险等级</text>
        </view>
        <view class="divider"></view>
        <view class="indicator-item">
          <text class="indicator-value">{{ companyInfo.interviewRate }}%</text>
          <text class="indicator-label">面试邀约率</text>
        </view>
      </view>
    </view>

    <!-- 风险提示条 -->
    <view class="risk-alert" :class="'risk-level-' + companyInfo.riskLevel">
      <text class="risk-icon">⚠️</text>
      <text class="risk-text">{{ riskAlertText }}</text>
    </view>

    <!-- 公司简介 -->
    <view class="section company-intro">
      <text class="section-title">公司简介</text>
      <text class="intro-content">{{ companyInfo.introduction }}</text>
      <view class="company-details">
        <view class="detail-item">
          <text class="detail-label">成立时间</text>
          <text class="detail-value">{{ companyInfo.foundedDate }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">所在地</text>
          <text class="detail-value">{{ companyInfo.location }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">融资阶段</text>
          <text class="detail-value">{{ companyInfo.financingStage }}</text>
        </view>
      </view>
    </view>

    <!-- 风险评估详情 -->
    <view class="section risk-assessment">
      <view class="section-header">
        <text class="section-title">风险评估</text>
        <text class="risk-score">{{ companyInfo.riskScore }}/100</text>
      </view>

      <view class="risk-details">
        <view class="risk-item" v-for="(item, index) in riskAssessment" :key="index">
          <view class="risk-item-header">
            <text class="risk-item-name">{{ item.name }}</text>
            <text class="risk-item-score">{{ item.score }}/{{ item.maxScore }}</text>
          </view>
          <view class="score-bar">
            <view class="score-progress" :style="{ width: (item.score / item.maxScore * 100) + '%' }"></view>
          </view>
          <text v-if="item.comment" class="risk-comment">{{ item.comment }}</text>
        </view>
      </view>
    </view>

    <!-- 评价列表 -->
    <view class="section evaluations-section">
      <view class="section-header">
        <text class="section-title">用户评价</text>
        <view class="rating-summary">
          <text class="average-rating">{{ companyInfo.rating.toFixed(1) }}</text>
          <text class="total-evaluations">({{ companyInfo.evaluationCount }}条)</text>
        </view>
      </view>

      <!-- 评价筛选 -->
      <scroll-view class="evaluation-filters" scroll-x>
        <view class="filter-tags">
          <text class="filter-tag active">全部</text>
          <text class="filter-tag" v-for="filter in evaluationFilters" :key="filter">{{ filter }}</text>
        </view>
      </scroll-view>

      <!-- 评价列表 -->
      <view class="evaluations-list">
        <view class="evaluation-item" v-for="(evaluation, index) in evaluations" :key="index">
          <view class="evaluation-header">
            <image :src="evaluation.avatar" class="user-avatar" mode="aspectFit" />
            <view class="user-info">
              <text class="user-name">{{ evaluation.userName }}</text>
              <view class="evaluation-meta">
                <text class="position">{{ evaluation.position }}</text>
                <text class="time">{{ evaluation.time }}</text>
              </view>
            </view>
            <view class="evaluation-rating">
              <text class="rating-number">{{ evaluation.rating }}</text>
              <text class="star-icon">★</text>
            </view>
          </view>

          <text class="evaluation-content">{{ evaluation.content }}</text>

          <!-- 评价标签 -->
          <view class="evaluation-tags" v-if="evaluation.tags && evaluation.tags.length">
            <text class="tag" v-for="tag in evaluation.tags" :key="tag">{{ tag }}</text>
          </view>

          <!-- 评价互动 -->
          <view class="evaluation-actions">
            <view class="action-item">
              <text class="action-icon">👍</text>
              <text class="action-count">{{ evaluation.likes }}</text>
            </view>
            <view class="action-item">
              <text class="action-icon">💬</text>
              <text class="action-count">{{ evaluation.comments }}</text>
            </view>
            <view class="action-item">
              <text class="action-icon">🔗</text>
              <text class="action-text">分享</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" @click="loadMoreEvaluations">
        <text class="load-more-text" v-if="!loadingMore && hasMore">查看更多评价</text>
        <view v-if="loadingMore" class="loading-spinner"></view>
        <text v-if="!hasMore" class="no-more-text">没有更多评价了</text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <button class="btn-secondary" @click="collectCompany">
        <text class="action-icon">{{ isCollected ? '★' : '☆' }}</text>
        <text class="action-text">{{ isCollected ? '已收藏' : '收藏' }}</text>
      </button>
      <button class="btn-primary" @click="writeEvaluation">
        <text class="action-icon">✍️</text>
        <text class="action-text">写评价</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'

// 公司信息
const companyInfo = reactive({
  id: 1,
  name: '深圳市创新科技有限公司',
  logo: '/static/company-logo.png',
  rating: 4.3,
  industry: '互联网/科技',
  scale: '1000-9999人',
  tags: ['上市公司', '弹性工作', '五险一金', '年终奖'],
  evaluationCount: 1289,
  riskLevel: 2, // 1-5级，1最低，5最高
  riskScore: 78,
  interviewRate: 85,
  introduction: '我们是一家专注于人工智能技术研发的高科技企业，致力于通过技术创新推动行业发展。公司成立于2015年，已获得多轮融资，拥有完善的员工福利体系和职业发展通道。',
  foundedDate: '2015-03-15',
  location: '深圳·南山区',
  financingStage: 'C轮'
})

// 风险评估数据
const riskAssessment = reactive([
  { name: '法律风险', score: 8, maxScore: 10, comment: '无重大法律纠纷记录' },
  { name: '财务风险', score: 7, maxScore: 10, comment: '融资状态良好，现金流稳定' },
  { name: '经营风险', score: 9, maxScore: 10, comment: '业务持续增长，市场占有率高' },
  { name: '声誉风险', score: 6, maxScore: 10, comment: '近期有少量负面评价' },
  { name: '就业风险', score: 8, maxScore: 10, comment: '员工离职率低于行业平均' }
])

// 评价数据
const evaluations = ref([
  {
    id: 1,
    userName: '张明',
    avatar: '/static/avatar1.png',
    position: '前端工程师',
    time: '2天前',
    rating: 4.5,
    content: '公司技术氛围很好，同事都很专业，福利待遇也不错。就是加班稍微有点多，不过也能理解，毕竟互联网行业。',
    tags: ['技术氛围好', '薪资满意', '加班较多'],
    likes: 24,
    comments: 8
  },
  {
    id: 2,
    userName: '李晓红',
    avatar: '/static/avatar2.png',
    position: '产品经理',
    time: '1周前',
    rating: 5,
    content: '非常棒的公司文化，领导很开明，给员工充分的发挥空间。晋升机制透明，只要有能力就能得到认可。',
    tags: ['公司文化好', '晋升透明', '领导开明'],
    likes: 56,
    comments: 12
  },
  {
    id: 3,
    userName: '王刚',
    avatar: '/static/avatar3.png',
    position: 'Java开发',
    time: '2周前',
    rating: 3,
    content: '项目压力比较大，经常需要加班。不过能学到很多东西，技术成长很快。希望公司能多关注员工生活平衡。',
    tags: ['技术成长快', '项目压力大', '加班文化'],
    likes: 18,
    comments: 5
  }
])

// 筛选标签
const evaluationFilters = ref(['好评', '中评', '差评', '薪资相关', '面试体验', '工作环境'])

// 加载状态
const loadingMore = ref(false)
const hasMore = ref(true)
const isCollected = ref(false)

// 计算风险提示文本
const riskAlertText = computed(() => {
  const level = companyInfo.riskLevel
  const texts = [
    '风险极低，可放心求职',
    '风险较低，建议重点关注',
    '风险中等，建议仔细评估',
    '风险较高，建议谨慎考虑',
    '风险极高，建议避开'
  ]
  return texts[level - 1] || '风险信息未知'
})

// 加载更多评价
const loadMoreEvaluations = () => {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true

  // 模拟加载更多数据
  setTimeout(() => {
    const newEvaluations = Array.from({ length: 3 }, (_, i) => ({
      id: evaluations.value.length + i + 1,
      userName: `用户${evaluations.value.length + i + 1}`,
      avatar: `/static/avatar${(i % 3) + 1}.png`,
      position: ['前端工程师', '产品经理', 'Java开发'][i % 3],
      time: `${i + 1}周前`,
      rating: [4.5, 5, 3][i % 3],
      content: '这是一条模拟的评论内容，用于测试加载更多功能。',
      tags: ['标签1', '标签2'],
      likes: Math.floor(Math.random() * 50),
      comments: Math.floor(Math.random() * 20)
    }))

    evaluations.value.push(...newEvaluations)
    loadingMore.value = false

    // 模拟没有更多数据的情况
    if (evaluations.value.length >= 15) {
      hasMore.value = false
    }
  }, 1000)
}

// 收藏公司
const collectCompany = () => {
  isCollected.value = !isCollected.value
  uni.showToast({
    title: isCollected.value ? '收藏成功' : '已取消收藏',
    icon: 'success'
  })
}

// 写评价
const writeEvaluation = () => {
  uni.navigateTo({
    url: `/pages/evaluation/write?companyId=${companyInfo.id}`
  })
}

onMounted(() => {
  // 模拟初始化数据加载
  console.log('公司详情页面加载完成')
})
</script>

<style scoped lang="scss">
.company-detail-container {
  padding-bottom: 120rpx;
}

/* 公司头部 */
.company-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding: $padding-base * 2 $padding-base $padding-base;
  color: $background-color-white;
}

.company-basic-info {
  display: flex;
  align-items: center;
  margin-bottom: $margin-base;
}

.company-logo {
  width: 120rpx;
  height: 120rpx;
  border-radius: $border-radius;
  border: 4rpx solid rgba($background-color-white, 0.3);
  background: $background-color-white;
  margin-right: $margin-base;
}

.company-text-info {
  flex: 1;
}

.company-name-rating {
  display: flex;
  align-items: center;
  margin-bottom: $margin-mini;
}

.company-name {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  margin-right: $margin-small;
}

.rating-stars {
  display: flex;
  align-items: center;
}

.star-icon {
  color: rgba($background-color-white, 0.4);
  font-size: $font-size-medium;
  margin-right: 4rpx;

  &.active {
    color: #FFD700;
  }
}

.rating-score {
  margin-left: $margin-mini;
  font-size: $font-size-small;
}

.company-industry {
  display: block;
  font-size: $font-size-base;
  opacity: 0.9;
  margin-bottom: $margin-mini;
}

.company-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
  margin-top: $margin-small;
}

.tag {
  background: rgba($background-color-white, 0.2);
  padding: 6rpx $margin-mini;
  border-radius: $border-radius-small;
  font-size: $font-size-extra-small;
}

/* 关键指标 */
.key-indicators {
  display: flex;
  justify-content: space-around;
  background: rgba($background-color-white, 0.1);
  border-radius: $border-radius;
  padding: $padding-base 0;
  margin-top: $margin-base;
}

.indicator-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.indicator-value {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  margin-bottom: 5rpx;
}

.indicator-label {
  font-size: $font-size-small;
  opacity: 0.8;
}

.divider {
  width: 1rpx;
  background: rgba($background-color-white, 0.3);
}

/* 风险提示 */
.risk-alert {
  margin: $margin-base;
  padding: $padding-small $padding-base;
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  background: $primary-light;

  &.risk-level-1 { background: #e8f5e9; }
  &.risk-level-2 { background: $primary-light; }
  &.risk-level-3 { background: $warning-light; }
  &.risk-level-4 { background: #fff3e0; }
  &.risk-level-5 { background: $danger-light; }
}

.risk-icon {
  margin-right: $margin-mini;
  font-size: $font-size-medium;
}

.risk-text {
  flex: 1;
  font-size: $font-size-base;
  color: $text-regular;
}

/* 通用区块样式 */
.section {
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

/* 公司简介 */
.intro-content {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  margin-bottom: $margin-base;
}

.company-details {
  border-top: 1rpx solid $border-color-lighter;
  padding-top: $padding-small;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: $padding-mini 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }
}

.detail-label {
  font-size: $font-size-base;
  color: $text-secondary;
}

.detail-value {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

/* 风险评估 */
.risk-score {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $primary-color;
}

.risk-details {
  .risk-item {
    margin-bottom: $margin-base;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.risk-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: $margin-mini;
}

.risk-item-name {
  font-size: $font-size-base;
  color: $text-primary;
}

.risk-item-score {
  font-size: $font-size-small;
  color: $text-secondary;
}

.score-bar {
  height: 8rpx;
  background: $border-color-lighter;
  border-radius: 4rpx;
  overflow: hidden;
  margin-bottom: $margin-mini;
}

.score-progress {
  height: 100%;
  background: $primary-color;
  border-radius: 4rpx;
  transition: width $transition-duration;
}

.risk-comment {
  font-size: $font-size-small;
  color: $text-secondary;
}

/* 评价部分 */
.rating-summary {
  display: flex;
  align-items: baseline;
}

.average-rating {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $primary-color;
}

.total-evaluations {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-left: 4rpx;
}

.evaluation-filters {
  margin-bottom: $margin-base;
  white-space: nowrap;
}

.filter-tags {
  display: flex;
  gap: $margin-small;
}

.filter-tag {
  display: inline-block;
  padding: $padding-mini $padding-small;
  background: $background-color;
  border-radius: $border-radius * 2;
  font-size: $font-size-small;
  color: $text-secondary;

  &.active {
    background: $primary-color;
    color: $background-color-white;
  }
}

/* 评价列表 */
.evaluations-list {
  .evaluation-item {
    border-bottom: 1rpx solid $border-color-extra-light;
    padding-bottom: $padding-base;
    margin-bottom: $margin-base;

    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }
  }
}

.evaluation-header {
  display: flex;
  align-items: center;
  margin-bottom: $margin-small;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-round;
  margin-right: $margin-small;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: 4rpx;
}

.evaluation-meta {
  display: flex;
  gap: $margin-mini;
}

.position,
.time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.evaluation-rating {
  display: flex;
  align-items: center;
  background: rgba($primary-color, 0.1);
  padding: 6rpx $margin-mini;
  border-radius: $border-radius-small;
}

.rating-number {
  font-size: $font-size-small;
  color: $primary-color;
  font-weight: $font-weight-bold;
  margin-right: 4rpx;
}

.evaluation-content {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  margin-bottom: $margin-small;
}

.evaluation-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
  margin-bottom: $margin-small;
}

.evaluation-actions {
  display: flex;
  gap: $margin-base;
}

.action-item {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.action-icon {
  margin-right: 6rpx;
  font-size: $font-size-medium;
}

.action-count,
.action-text {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

/* 加载更多 */
.load-more {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100rpx;
  margin-top: $margin-base;
}

.load-more-text {
  font-size: $font-size-base;
  color: $primary-color;
  padding: $padding-mini $padding-small;
  border: 1rpx solid $primary-color;
  border-radius: $border-radius;
}

.no-more-text {
  font-size: $font-size-small;
  color: $text-secondary;
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid rgba($primary-color, 0.3);
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $background-color-white;
  border-top: 1rpx solid $border-color-light;
  padding: $padding-small $padding-base;
  display: flex;
  gap: $margin-small;
  z-index: 1000;
}

.btn-secondary,
.btn-primary {
  flex: 1;
  height: 80rpx;
  border: none;
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;

  .action-icon {
    margin-right: $margin-mini;
  }
}

.btn-secondary {
  background: $background-color;
  color: $primary-color;
  border: 1rpx solid $primary-color;
}

.btn-primary {
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness:  -10%) 100%);
  color: $background-color-white;
}
</style>