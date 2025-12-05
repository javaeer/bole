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

        <view class="resume-card">
          <view class="resume-header">
            <text class="resume-title">{{ applicationData.resumeTitle }}</text>
            <view class="resume-tags">
              <text class="resume-tag">{{ applicationData.workExperience }}年经验</text>
              <text class="resume-tag">{{ applicationData.education }}</text>
              <text class="resume-tag">{{ applicationData.age }}岁</text>
            </view>
          </view>

          <view class="resume-content">
            <text class="resume-position">{{ applicationData.position }}</text>
            <text class="resume-salary">{{ applicationData.salary }}</text>
          </view>

          <view class="resume-skills">
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
.resume-card,
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
.resume-header {
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.resume-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.resume-tags {
  display: flex;
  gap: $margin-mini;
  flex-wrap: wrap;
}

.resume-tag {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  background: $background-color;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-small;
}

.resume-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.resume-position {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.resume-salary {
  font-size: $font-size-base;
  color: $primary-color;
  font-weight: $font-weight-bold;
}

.resume-skills {
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