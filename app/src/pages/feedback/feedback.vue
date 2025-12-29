<template>
  <view class="feedback-container">
    <!-- 页面标题栏 -->
    <view class="page-header">
      <text class="page-title">意见反馈</text>
      <text class="page-subtitle">您的建议对我们非常重要</text>
    </view>

    <!-- 反馈表单卡片 -->
    <view class="form-card">
      <!-- 反馈类型选择 -->
      <view class="form-section">
        <text class="section-label">反馈类型</text>
        <view class="type-grid">
          <view
            v-for="(type, index) in feedbackTypes"
            :key="index"
            class="type-item"
            :class="{ active: selectedType === type.value }"
            @click="selectType(type.value)"
          >
            <text class="type-icon">{{ type.icon }}</text>
            <text class="type-text">{{ type.label }}</text>
          </view>
        </view>
      </view>

      <!-- 反馈内容 -->
      <view class="form-section">
        <text class="section-label">反馈内容</text>
        <view class="input-wrapper">
          <textarea
            v-model="feedbackContent"
            class="content-input"
            placeholder="请详细描述您遇到的问题或建议..."
            placeholder-class="placeholder"
            maxlength="500"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <view class="char-count">{{ feedbackContent.length }}/500</view>
        </view>
      </view>

      <!-- 图片上传 -->
      <view class="form-section">
        <text class="section-label">相关截图（可选）</text>
        <text class="section-hint">最多上传3张图片</text>
        <view class="upload-grid">
          <!-- 已上传图片 -->
          <view
            v-for="(image, index) in uploadedImages"
            :key="index"
            class="image-preview"
          >
            <image :src="image" class="preview-image" mode="aspectFill" />
            <view class="image-delete" @click="removeImage(index)">×</view>
          </view>

          <!-- 上传按钮 -->
          <view
            v-if="uploadedImages.length < 3"
            class="upload-button"
            @click="chooseImage"
          >
            <text class="upload-icon">+</text>
            <text class="upload-text">添加图片</text>
          </view>
        </view>
      </view>

      <!-- 联系方式 -->
      <view class="form-section">
        <text class="section-label">联系方式</text>
        <text class="section-hint">方便我们与您进一步沟通</text>
        <view class="contact-inputs">
          <view class="input-wrapper">
            <input
              v-model="contactInfo.email"
              class="form-input"
              type="text"
              placeholder="邮箱（选填）"
              placeholder-class="placeholder"
              @focus="handleInputFocus"
              @blur="handleInputBlur"
            />
          </view>
          <view class="input-wrapper">
            <input
              v-model="contactInfo.phone"
              class="form-input"
              type="number"
              placeholder="手机号（选填）"
              placeholder-class="placeholder"
              maxlength="11"
              @focus="handleInputFocus"
              @blur="handleInputBlur"
            />
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button
        class="submit-button"
        :class="{ 'submit-button--disabled': !canSubmit }"
        :disabled="!canSubmit || submitting"
        @click="submitFeedback"
      >
        <text v-if="!submitting">提交反馈</text>
        <view v-else class="loading-spinner"></view>
      </button>

      <!-- 提交成功提示 -->
      <view v-if="submitSuccess" class="success-message">
        <text class="success-icon">✓</text>
        <text class="success-text">反馈提交成功！感谢您的宝贵意见</text>
        <text class="success-hint">我们会在3个工作日内回复您</text>
      </view>
    </view>

    <!-- 底部提示 -->
    <view class="footer-tips">
      <text class="tips-text">我们非常重视每一位用户的反馈</text>
      <text class="tips-text">您的建议将帮助我们做得更好</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import FeedbackAPI from "@/api/feedback";
import { FeedbackForm, FeedbackResult } from "@/types/feedback";
import { UploadOptions } from "@/types/request";
import { FileResult } from "@/types/file";
import FileAPI from "@/api/file";

// 反馈类型数据
const feedbackTypes = [
  { value: "bug", label: "Bug反馈", icon: "🐛" },
  { value: "suggestion", label: "功能建议", icon: "💡" },
  { value: "experience", label: "体验问题", icon: "🌟" },
  { value: "other", label: "其他", icon: "📝" },
];

// 表单数据
const formData: FeedbackForm = {
  type: "bug",
  contact: "",
  images: "",
  content: "",
};
const selectedType = ref("bug");
const feedbackContent = ref("");
const uploadedImages = ref<string[]>([]);
const contactInfo = reactive({
  email: "",
  phone: "",
});

// 状态
const submitting = ref(false);
const submitSuccess = ref(false);

// 计算属性
const canSubmit = computed(() => {
  return feedbackContent.value.trim().length >= 10 && !submitting.value;
});

// 选择反馈类型
const selectType = (type: string) => {
  selectedType.value = type;
};

// 输入框焦点处理
const handleInputFocus = () => {
  // 可以添加动画效果
};

const handleInputBlur = () => {
  // 可以添加验证逻辑
};

// 选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 3 - uploadedImages.value.length,
    sizeType: ["compressed"],
    sourceType: ["album", "camera"],
    success: (res) => {
      const tempFilePaths = res.tempFilePaths;
      const fileSize = res.tempFiles[0].size;

      // 检查文件大小（限制为5MB）
      const maxSize = 5 * 1024 * 1024;
      if (fileSize > maxSize) {
        uni.showToast({
          title: "图片大小不能超过5MB",
          icon: "error",
        });
        return;
      }

      uploadedImages.value = [...uploadedImages.value, ...tempFilePaths];

      try {
        uni.showLoading({
          title: "上传中...",
          mask: true,
        });

        // 构建上传参数
        const uploadOptions: UploadOptions = {
          filePath: tempFilePath,
          name: "image",
          compress: true,
          maxWidth: 800,
          maxHeight: 800,
          quality: 0.8,
          showProgress: true,
          onProgress: (progress: number) => {
            uploadProgress.value = progress;
            uni.showLoading({
              title: `上传中 ${progress}%`,
              mask: true,
            });
          },
        };

        // 调用FileAPI上传
        const result: FileResult = await FileAPI.upload(uploadOptions);

        uni.hideLoading();
        uni.showToast({
          title: "上传成功",
          icon: "success",
        });

      } catch (error: any) {
        console.error("上传失败:", error);
        uni.hideLoading();

        uni.showToast({
          title: errorMsg,
          icon: "error",
          duration: 3000,
        });
      } finally {
        ///
      }
    },
    fail: (error) => {
      console.error("选择图片失败:", error);
      if (error.errMsg?.includes("cancel")) {
        return;
      }
      uni.showToast({
        title: "选择图片失败",
        icon: "error",
      });
    },
  });
};

// 删除图片
const removeImage = (index: number) => {
  uploadedImages.value.splice(index, 1);
  uni.showToast({
    title: "图片已删除",
    icon: "success",
  });
};

// 提交反馈
const submitFeedback = () => {
  if (!canSubmit.value) return;

  submitting.value = true;

  // 提交过程

  const result: FeedbackResult = await FeedbackAPI.add();

  setTimeout(() => {
    submitting.value = false;
    submitSuccess.value = true;

    // 3秒后重置表单
    setTimeout(() => {
      resetForm();
    }, 3000);

    uni.showToast({
      title: "提交成功",
      icon: "success",
    });
  }, 2000);
};

// 重置表单
const resetForm = () => {
  selectedType.value = "bug";
  feedbackContent.value = "";
  uploadedImages.value = [];
  contactInfo.email = "";
  contactInfo.phone = "";
  submitSuccess.value = false;
};
</script>

<style lang="scss" scoped>
.feedback-container {
  background-color: $background-color;
  min-height: 100vh;
  padding: 0 $padding-base $padding-base * 2;
}

/* 页面标题 */
.page-header {
  padding: $padding-base * 2 $padding-base $padding-base;
  text-align: center;
}

.page-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.page-subtitle {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
}

/* 表单卡片 */
.form-card {
  background: $background-color-white;
  border-radius: $border-radius * 2;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

/* 表单区块 */
.form-section {
  margin-bottom: $margin-base * 1.5;
}

.section-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.section-hint {
  display: block;
  font-size: $font-size-small;
  color: $text-placeholder;
  margin-bottom: $margin-mini;
}

/* 类型选择网格 */
.type-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-small;
  margin-top: $margin-mini;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius;
  border: 2rpx solid $border-color-light;
  transition: all $transition-fast;

  &.active {
    border-color: $primary-color;
    background: rgba($primary-color, 0.1);
    box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.1);

    .type-text {
      color: $primary-color;
      font-weight: $font-weight-medium;
    }

    .type-icon {
      transform: scale(1.1);
    }
  }

  &:active:not(.active) {
    background: color.adjust($background-color, $lightness: - 5%);
    transform: translateY(2rpx);
  }
}

.type-icon {
  font-size: $font-size-extra-large;
  margin-bottom: $margin-mini;
  transition: transform $transition-fast;
}

.type-text {
  font-size: $font-size-small;
  color: $text-regular;
}

/* 反馈内容输入 */
.input-wrapper {
  position: relative;
  background: $background-color;
  border-radius: $border-radius;
  border: 2rpx solid $border-color-light;
  transition: all $transition-fast;

  &:focus-within {
    border-color: $primary-color;
    box-shadow: 0 0 0 2rpx rgba($primary-color, 0.1);
  }
}

.content-input {
  width: 100%;
  min-height: 200rpx;
  padding: $padding-base;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;
  resize: none;

  &::placeholder {
    color: $text-placeholder;
  }
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

.char-count {
  position: absolute;
  bottom: $padding-mini;
  right: $padding-mini;
  font-size: $font-size-extra-small;
  color: $text-secondary;
  background: rgba($background-color-white, 0.9);
  padding: 2rpx 8rpx;
  border-radius: $border-radius-small;
}

/* 图片上传 */
.upload-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $margin-small;
  margin-top: $margin-mini;
}

.image-preview {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: $border-radius;
  overflow: hidden;
  box-shadow: $box-shadow-light;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.image-delete {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 36rpx;
  height: 36rpx;
  background: $danger-color;
  color: $background-color-white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  cursor: pointer;
  transition: transform $transition-fast;

  &:active {
    transform: scale(0.9);
  }
}

.upload-button {
  width: 100%;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: $background-color;
  border: 2rpx dashed $border-color;
  border-radius: $border-radius;
  color: $text-secondary;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness: - 5%);
    border-color: $primary-color;
    color: $primary-color;
  }
}

.upload-icon {
  font-size: $font-size-extra-large;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-bold;
}

.upload-text {
  font-size: $font-size-extra-small;
}

/* 联系方式输入 */
.contact-inputs {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
  margin-top: $margin-mini;

  .input-wrapper {
    margin: 0;
  }
}

.form-input {
  width: 100%;
  height: $input-height;
  padding: 0 $padding-base;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;

  &::placeholder {
    color: $text-placeholder;
  }
}

/* 提交按钮 */
.submit-button {
  width: 100%;
  height: $button-height;
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);
  color: $background-color-white;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  margin-top: $margin-base * 1.5;
  transition: all $transition-normal;

  &:active:not(:disabled) {
    transform: translateY(2rpx);
    box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.3);
  }

  &:disabled {
    opacity: $button-disabled-opacity;
  }

  &--disabled {
    background: linear-gradient(135deg, $text-placeholder 0%, color.adjust($text-placeholder, $lightness: -10%) 100%);
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

/* 成功消息 */
.success-message {
  text-align: center;
  padding: $padding-base;
  margin-top: $margin-base;
  background: rgba($success-color, 0.1);
  border-radius: $border-radius;
  border: 2rpx solid rgba($success-color, 0.3);
}

.success-icon {
  display: block;
  font-size: 60rpx;
  color: $success-color;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-bold;
}

.success-text {
  display: block;
  font-size: $font-size-base;
  color: $text-primary;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-medium;
}

.success-hint {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
}

/* 底部提示 */
.footer-tips {
  text-align: center;
  margin-top: $margin-base * 2;
  padding: $padding-base;
}

.tips-text {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.6;

  &:first-child {
    font-weight: $font-weight-medium;
  }
}

/* 响应式调整 */
@media (max-width: 375px) {
  .type-grid {
    grid-template-columns: 1fr;
  }

  .upload-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>