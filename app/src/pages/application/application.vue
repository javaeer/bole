<template>
  <view class="page-container">
    <!-- 顶部标题栏 -->
    <view class="review-header">
      <view class="header-actions">
        <text
          v-if="applicationData.status === 'pending'"
          class="header-action"
          @click="handleQuickReject"
        >
          拒绝
        </text>
      </view>
    </view>

    <scroll-view class="content-container" scroll-y>
      <!-- 申请信息 -->
      <view class="info-section">
        <view class="section-title">申请信息</view>

        <view class="info-card">
          <view class="info-row">
            <text class="info-label">申请人</text>
            <view class="info-value applicant-info">
              <image
                :src="applicationData.avatar || '/static/default-avatar.png'"
                class="applicant-avatar"
              />
              <view class="applicant-details">
                <text class="applicant-name">{{ applicationData.name }}</text>
                <text class="applicant-contact">{{ applicationData.contact }}</text>
              </view>
            </view>
          </view>

          <view class="info-row">
            <text class="info-label">申请时间</text>
            <text class="info-value">{{ formatTime(applicationData.createTime) }}</text>
          </view>

          <view class="info-row">
            <text class="info-label">申请状态</text>
            <view class="info-value">
              <view class="status-badge" :class="`status-${applicationData.status}`">
                {{ getStatusText(applicationData.status) }}
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 简历信息 -->
      <view class="info-section">
        <view class="section-title">简历信息</view>

        <view class="resumes-card">
          <view class="resumes-header">
            <text class="resumes-title">{{ applicationData.resumeTitle }}</text>
            <view class="resumes-tags">
              <text class="resumes-tag">{{ applicationData.workExperience }}年经验</text>
              <text class="resumes-tag">{{ applicationData.education }}</text>
              <text class="resumes-tag">{{ applicationData.age }}岁</text>
            </view>
          </view>

          <view class="resumes-content">
            <text class="resumes-position">{{ applicationData.position }}</text>
            <text class="resumes-salary">{{ applicationData.salary }}</text>
          </view>

          <view class="resumes-skills">
            <text class="skills-label">技能标签：</text>
            <view class="skill-tags">
              <text
                v-for="skill in applicationData.skills"
                :key="skill"
                class="skill-tag"
              >
                {{ skill }}
              </text>
            </view>
          </view>

          <button class="btn-preview" @click="previewResume">
            <text class="preview-icon">👁️</text>
            <text class="preview-text">预览完整简历</text>
          </button>
        </view>
      </view>

      <!-- 申请理由 -->
      <view class="info-section">
        <view class="section-title">申请理由</view>

        <view class="reason-card">
          <text class="reason-content">{{ applicationData.reason }}</text>
          <view class="reason-attachments" v-if="applicationData.attachments?.length">
            <text class="attachments-label">附件：</text>
            <view class="attachment-list">
              <view
                v-for="(file, index) in applicationData.attachments"
                :key="index"
                class="attachment-item"
                @click="previewAttachment(file)"
              >
                <text class="attachment-icon">📎</text>
                <text class="attachment-name">{{ file.name }}</text>
                <text class="attachment-size">{{ formatFileSize(file.size) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 审核区域 -->
      <view v-if="applicationData.status === 'pending'" class="review-section">
        <view class="section-title">审核意见</view>

        <view class="review-form">
          <view class="form-group">
            <text class="form-label">审核结果</text>
            <view class="radio-group">
              <view
                v-for="option in reviewOptions"
                :key="option.value"
                class="radio-option"
                @click="reviewResult = option.value"
              >
                <view class="radio-circle" :class="{ checked: reviewResult === option.value }">
                  <view v-if="reviewResult === option.value" class="radio-inner"></view>
                </view>
                <text class="radio-label">{{ option.label }}</text>
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">审核意见</text>
            <textarea
              v-model="reviewComment"
              class="review-textarea"
              placeholder="请输入审核意见（选填）"
              placeholder-class="textarea-placeholder"
              maxlength="500"
              auto-height
            />
            <view class="textarea-counter">
              <text>{{ reviewComment.length }}/500</text>
            </view>
          </view>

          <view class="review-actions">
            <button class="btn-cancel" @click="goBack">
              取消
            </button>
            <button class="btn-submit" :disabled="!reviewResult" @click="submitReview">
              {{ reviewResult === 'approved' ? '通过申请' : '拒绝申请' }}
            </button>
          </view>
        </view>
      </view>

      <!-- 历史记录 -->
      <view v-if="applicationData.status !== 'pending'" class="history-section">
        <view class="section-title">审核记录</view>

        <view class="history-timeline">
          <view v-for="record in reviewHistory" :key="record.id" class="timeline-item">
            <view class="timeline-dot" :class="`dot-${record.type}`"></view>
            <view class="timeline-content">
              <view class="timeline-header">
                <text class="timeline-title">{{ record.title }}</text>
                <text class="timeline-time">{{ formatTime(record.time) }}</text>
              </view>
              <text v-if="record.comment" class="timeline-comment">
                {{ record.comment }}
              </text>
              <text v-if="record.operator" class="timeline-operator">
                操作人：{{ record.operator }}
              </text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 定义接口类型
interface Attachment {
  name: string
  size: number
  url?: string
}

interface ReviewRecord {
  id: string | number
  type: 'create' | 'review' | 'reject' | 'approved'
  title: string
  time: string
  comment?: string
  operator?: string
}

interface ApplicationData {
  id: string | number
  name: string
  contact: string
  avatar?: string
  createTime: string
  status: 'pending' | 'approved' | 'rejected'
  resumeTitle: string
  workExperience: number
  education: string
  age: number
  position: string
  salary: string
  skills: string[]
  reason: string
  attachments?: Attachment[]
}

interface ReviewOption {
  label: string
  value: 'approved' | 'rejected'
}

// 响应式数据
const applicationData = reactive<ApplicationData>({
  id: '',
  name: '',
  contact: '',
  avatar: '',
  createTime: '',
  status: 'pending',
  resumeTitle: '',
  workExperience: 0,
  education: '',
  age: 0,
  position: '',
  salary: '',
  skills: [],
  reason: '',
  attachments: []
})

const reviewResult = ref<'approved' | 'rejected'>('approved')
const reviewComment = ref('')

// 审核选项
const reviewOptions = ref<ReviewOption[]>([
  { label: '通过', value: 'approved' },
  { label: '拒绝', value: 'rejected' }
])

// 审核历史记录
const reviewHistory = ref<ReviewRecord[]>([])

// 状态文本映射
const statusTextMap = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已拒绝'
}

// 页面加载
onLoad((options) => {
  const id = options?.id
  if (id) {
    fetchApplicationData(id)
  }
})

// 获取申请数据
const fetchApplicationData = async (id: string | number) => {
  try {
    // 这里应该调用实际的API
    // 以下为模拟数据
    const mockData: ApplicationData = {
      id,
      name: '张三',
      contact: '13800138000',
      avatar: '/static/avatar.jpg',
      createTime: '2024-01-15 14:30:00',
      status: 'pending',
      resumeTitle: '前端开发工程师',
      workExperience: 3,
      education: '本科',
      age: 28,
      position: '高级前端开发',
      salary: '20-30K',
      skills: ['Vue.js', 'TypeScript', 'Node.js', '小程序开发'],
      reason: '对贵公司的技术氛围和发展前景非常向往，希望能有机会加入团队共同成长。',
      attachments: [
        { name: '个人作品集.pdf', size: 2048576 },
        { name: '项目案例.zip', size: 5123456 }
      ]
    }

    Object.assign(applicationData, mockData)

    // 生成审核历史记录
    generateReviewHistory()
  } catch (error) {
    console.error('获取申请数据失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'error'
    })
  }
}

// 生成审核历史记录
const generateReviewHistory = () => {
  const history: ReviewRecord[] = [
    {
      id: 1,
      type: 'create',
      title: '提交申请',
      time: applicationData.createTime,
      operator: applicationData.name
    }
  ]

  if (applicationData.status !== 'pending') {
    history.push({
      id: 2,
      type: applicationData.status,
      title: applicationData.status === 'approved' ? '通过申请' : '拒绝申请',
      time: '2024-01-16 10:00:00',
      comment: '符合岗位要求',
      operator: '管理员'
    })
  }

  reviewHistory.value = history
}

// 快速拒绝
const handleQuickReject = () => {
  uni.showModal({
    title: '确认拒绝',
    content: '确定要快速拒绝此申请吗？',
    success: (res) => {
      if (res.confirm) {
        submitReviewDirect('rejected', '快速拒绝')
      }
    }
  })
}

// 格式化时间
const formatTime = (time: string): string => {
  if (!time) return ''

  try {
    const date = new Date(time)
    if (isNaN(date.getTime())) return time

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')

    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch {
    return time
  }
}

// 获取状态文本
const getStatusText = (status: string): string => {
  return statusTextMap[status as keyof typeof statusTextMap] || status
}

// 预览简历
const previewResume = () => {
  // 这里应该实现简历预览逻辑
  uni.showToast({
    title: '打开简历预览',
    icon: 'none'
  })
}

// 预览附件
const previewAttachment = (file: Attachment) => {
  // 这里应该实现附件预览逻辑
  uni.showToast({
    title: `打开附件: ${file.name}`,
    icon: 'none'
  })
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 提交审核
const submitReview = () => {
  if (!reviewResult.value) {
    uni.showToast({
      title: '请选择审核结果',
      icon: 'error'
    })
    return
  }

  const title = reviewResult.value === 'approved' ? '通过申请' : '拒绝申请'
  const content = `确定要${title}吗？`

  uni.showModal({
    title: '确认提交',
    content,
    success: (res) => {
      if (res.confirm) {
        submitReviewDirect(reviewResult.value, reviewComment.value)
      }
    }
  })
}

// 直接提交审核（用于快速拒绝和正常提交）
const submitReviewDirect = (result: 'approved' | 'rejected', comment: string) => {
  // 这里应该调用API提交审核结果
  console.log('提交审核:', { result, comment })

  // 模拟提交成功
  uni.showToast({
    title: result === 'approved' ? '已通过申请' : '已拒绝申请',
    icon: 'success'
  })

  // 更新本地状态
  applicationData.status = result

  // 添加审核记录
  reviewHistory.value.push({
    id: Date.now(),
    type: result,
    title: result === 'approved' ? '通过申请' : '拒绝申请',
    time: new Date().toISOString(),
    comment: comment || undefined,
    operator: '当前用户'
  })

  // 延迟返回上一页
  setTimeout(() => {
    uni.navigateBack()
  }, 1500)
}

// 计算属性：是否显示审核区域
const showReviewSection = computed(() => {
  return applicationData.status === 'pending'
})

// 计算属性：是否显示历史记录
const showHistorySection = computed(() => {
  return applicationData.status !== 'pending'
})
</script>

<style lang="scss" scoped>
.page-container {
  background-color: $background-color;
  min-height: 100vh;
}

/* 顶部标题栏 */
.review-header {
  background: $background-color-white;
  padding: 20rpx $padding-base;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid $border-color-light;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-back {
  width: 80rpx;

  .back-icon {
    font-size: $font-size-large;
    color: $text-primary;
    cursor: pointer;

    &:active {
      opacity: 0.7;
    }
  }
}

.header-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  flex: 1;
  text-align: center;
}

.header-actions {
  width: 80rpx;
  text-align: right;

  .header-action {
    font-size: $font-size-small;
    color: $danger-color;
    cursor: pointer;

    &:active {
      opacity: 0.7;
    }
  }
}

/* 内容容器 */
.content-container {
  height: calc(100vh - 120rpx);
  padding: $padding-base;
}

/* 通用部分样式 */
.info-section {
  margin-bottom: $margin-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-small;
  padding-left: $padding-mini;
  border-left: 6rpx solid $primary-color;
}

/* 信息卡片 */
.info-card,
.resumes-card,
.reason-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.info-row {
  display: flex;
  align-items: center;
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  width: 180rpx;
  font-size: $font-size-base;
  color: $text-secondary;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-size: $font-size-base;
  color: $text-primary;
}

/* 申请人信息 */
.applicant-info {
  display: flex;
  align-items: center;
  gap: $margin-small;
}

.applicant-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-round;
  border: 2rpx solid $border-color-light;
}

.applicant-details {
  display: flex;
  flex-direction: column;
}

.applicant-name {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: 5rpx;
}

.applicant-contact {
  font-size: $font-size-small;
  color: $text-secondary;
}

/* 状态徽章 */
.status-badge {
  display: inline-block;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;

  &.status-pending {
    background: $warning-light;
    color: $warning-color;
  }

  &.status-approved {
    background: rgba($success-color, 0.1);
    color: $success-color;
  }

  &.status-rejected {
    background: $danger-light;
    color: $danger-color;
  }

  &.status-exported {
    background: rgba($info-color, 0.1);
    color: $info-color;
  }
}

/* 简历卡片 */
.resumes-header {
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.resumes-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.resumes-tags {
  display: flex;
  gap: $margin-mini;
  flex-wrap: wrap;
}

.resumes-tag {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  background: $background-color;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;
}

.resumes-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.resumes-position {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.resumes-salary {
  font-size: $font-size-base;
  color: $primary-color;
  font-weight: $font-weight-bold;
}

.resumes-skills {
  margin-bottom: $margin-base;
}

.skills-label {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-right: $margin-mini;
}

.skill-tags {
  display: flex;
  gap: $margin-mini;
  flex-wrap: wrap;
  margin-top: $margin-mini;
}

.skill-tag {
  font-size: $font-size-extra-small;
  color: $text-primary;
  background: $primary-light;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;
}

.btn-preview {
  width: 100%;
  height: 70rpx;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  border-radius: $border-radius-small;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;
  font-size: $font-size-small;
  color: $text-regular;

  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }
}

/* 申请理由 */
.reason-content {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  margin-bottom: $margin-base;
}

.reason-attachments {
  border-top: 1rpx solid $border-color-extra-light;
  padding-top: $padding-small;
}

.attachments-label {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-right: $margin-mini;
}

.attachment-list {
  margin-top: $margin-small;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius-small;
  margin-bottom: $margin-mini;
  cursor: pointer;
  transition: background-color $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.attachment-icon {
  font-size: $font-size-base;
  margin-right: $margin-small;
}

.attachment-name {
  flex: 1;
  font-size: $font-size-small;
  color: $text-primary;
  @extend .text-ellipsis;
  margin-right: $margin-small;
}

.attachment-size {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

/* 审核表单 */
.review-form {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.form-group {
  margin-bottom: $margin-base;
}

.form-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-small;
}

.radio-group {
  display: flex;
  gap: $margin-base;
}

.radio-option {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-circle {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $border-color;
  border-radius: $border-radius-round;
  margin-right: $margin-mini;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-fast;

  &.checked {
    border-color: $primary-color;

    .radio-inner {
      width: 18rpx;
      height: 18rpx;
      background: $primary-color;
      border-radius: 50%;
    }
  }
}

.radio-label {
  font-size: $font-size-base;
  color: $text-regular;
}

/* 文本域 */
.review-textarea {
  width: 100%;
  min-height: 200rpx;
  background: $background-color;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius-small;
  padding: $padding-small;
  font-size: $font-size-base;
  color: $text-primary;
  line-height: 1.4;
  transition: border-color $transition-fast;

  &:focus {
    border-color: $primary-color;
    box-shadow: $input-focus-shadow;
    background: $background-color-white;
  }
}

.textarea-placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

.textarea-counter {
  text-align: right;
  font-size: $font-size-extra-small;
  color: $text-secondary;
  margin-top: $margin-mini;
}

/* 审核操作按钮 */
.review-actions {
  display: flex;
  gap: $margin-base;
  margin-top: $margin-base * 2;

  .btn-cancel,
  .btn-submit {
    flex: 1;
    height: 90rpx;
    border-radius: $border-radius;
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    border: none;
    transition: all $transition-fast;

    &:active {
      transform: translateY(2rpx);
    }

    &:disabled {
      opacity: $button-disabled-opacity;
      transform: none;
    }
  }

  .btn-cancel {
    background: $background-color;
    color: $text-regular;
    border: 1rpx solid $border-color-light;

    &:active {
      background: color.adjust($background-color, $lightness:  - 5%);
    }
  }

  .btn-submit {
    background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness:  -10%) 100%);
    color: $background-color-white;
    box-shadow: $box-shadow;

    &:active {
      box-shadow: $button-active-shadow;
    }
  }
}

/* 历史记录时间线 */
.history-timeline {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.timeline-item {
  display: flex;
  padding: $padding-small 0;
  position: relative;

  &:not(:last-child)::after {
    content: '';
    position: absolute;
    left: 18rpx;
    top: 50rpx;
    bottom: -$padding-small;
    width: 2rpx;
    background: $border-color-light;
  }
}

.timeline-dot {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  margin-right: $margin-base;
  flex-shrink: 0;
  position: relative;
  z-index: 1;

  &.dot-approve {
    background: rgba($success-color, 0.1);
    border: 2rpx solid $success-color;

    &::before {
      content: '✓';
      color: $success-color;
      font-size: $font-size-small;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }
  }

  &.dot-reject {
    background: rgba($danger-color, 0.1);
    border: 2rpx solid $danger-color;

    &::before {
      content: '✕';
      color: $danger-color;
      font-size: $font-size-small;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }
  }

  &.dot-submit {
    background: rgba($primary-color, 0.1);
    border: 2rpx solid $primary-color;

    &::before {
      content: '📝';
      color: $primary-color;
      font-size: $font-size-small;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }
  }
}

.timeline-content {
  flex: 1;
  padding-bottom: $padding-small;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-mini;
}

.timeline-title {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.timeline-time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.timeline-comment {
  display: block;
  font-size: $font-size-small;
  color: $text-regular;
  line-height: 1.4;
  margin-bottom: $margin-mini;
  background: $background-color;
  padding: $padding-mini $padding-small;
  border-radius: $border-radius-small;
  border-left: 3rpx solid $border-color;
}

.timeline-operator {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .content-container {
    padding: $padding-small;
  }

  .radio-group {
    flex-direction: column;
    gap: $margin-small;
  }
}
</style>