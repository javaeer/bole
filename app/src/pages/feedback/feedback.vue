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
            :class="{ active: formData.type === type.value }"
            @click="formData.type = type.value"
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
            v-model="formData.content"
            class="content-input"
            placeholder="请详细描述您遇到的问题或建议..."
            placeholder-class="placeholder"
            maxlength="500"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <view class="char-count">{{ formData.content?.length || 0 }}/500</view>
        </view>
        <view v-if="contentError" class="error-message">
          {{ contentError }}
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
            <image :src="image.url" class="preview-image" mode="aspectFill" />
            <view class="image-delete" @click="removeImage(index)">×</view>
            <view v-if="image.uploading" class="uploading-overlay">
              <view class="loading-spinner small"></view>
              <text class="uploading-text">{{ image.progress }}%</text>
            </view>
          </view>

          <!-- 上传按钮 -->
          <view
            v-if="uploadedImages.length < 3"
            class="upload-button"
            @click="chooseImage"
            :disabled="uploading"
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
              v-model="formData.contact.email"
              class="form-input"
              type="text"
              placeholder="邮箱（选填）"
              placeholder-class="placeholder"
              @focus="handleInputFocus"
              @blur="handleInputBlur"
            />
            <view v-if="emailError" class="error-message">
              {{ emailError }}
            </view>
          </view>
          <view class="input-wrapper">
            <input
              v-model="formData.contact.phone"
              class="form-input"
              type="number"
              placeholder="手机号（选填）"
              placeholder-class="placeholder"
              maxlength="11"
              @focus="handleInputFocus"
              @blur="handleInputBlur"
            />
            <view v-if="phoneError" class="error-message">
              {{ phoneError }}
            </view>
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
import { useUserStore } from "@/stores/user";
import FeedbackAPI from "@/api/feedback";
import type { FeedbackForm } from "@/types/feedback";
import { UploadOptions } from "@/types/request";
import { FileResult } from "@/types/file";
import FileAPI from "@/api/file";

const userStore = useUserStore();

// 反馈类型数据
const feedbackTypes = [
  { value: "bug", label: "Bug反馈", icon: "🐛" },
  { value: "suggestion", label: "功能建议", icon: "💡" },
  { value: "experience", label: "体验问题", icon: "🌟" },
  { value: "other", label: "其他", icon: "📝" },
];

// 表单数据 - 按照接口定义
const formData = reactive<FeedbackForm>({
  type: "bug",
  content: "",
  images: [],
  contact: {
    phone: "",
    email: "",
  },
});

// 图片上传状态
interface ImageItem {
  url: string;
  filePath?: string;
  uploading: boolean;
  progress: number;
  serverUrl?: string;
}

const uploadedImages = ref<ImageItem[]>([]);
const uploading = ref(false);

// 状态
const submitting = ref(false);
const submitSuccess = ref(false);
const contentError = ref("");
const emailError = ref("");
const phoneError = ref("");

// 计算属性
const canSubmit = computed(() => {
  return formData.content && formData.content.trim().length >= 10 && !submitting.value;
});

// 输入框焦点处理
const handleInputFocus = () => {
  // 可以添加动画效果
};

const handleInputBlur = () => {
  // 验证反馈内容
  if (formData.content && formData.content.trim().length > 0 && formData.content.trim().length < 10) {
    contentError.value = "反馈内容至少需要10个字符";
  } else {
    contentError.value = "";
  }

  // 验证邮箱格式
  if (formData.contact.email && !isValidEmail(formData.contact.email)) {
    emailError.value = "请输入有效的邮箱地址";
  } else {
    emailError.value = "";
  }

  // 验证手机号格式
  if (formData.contact.phone && !isValidPhone(formData.contact.phone)) {
    phoneError.value = "请输入有效的手机号";
  } else {
    phoneError.value = "";
  }
};

// 邮箱验证
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 手机号验证
const isValidPhone = (phone: string): boolean => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(phone);
};

// 选择图片
const chooseImage = async () => {
  if (uploading.value) return;

  try {
    const res = await uni.chooseImage({
      count: 3 - uploadedImages.value.length,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    });

    const tempFilePaths = res.tempFilePaths;

    // 检查文件大小
    for (let i = 0; i < tempFilePaths.length; i++) {
      const tempFilePath = tempFilePaths[i];
      const fileSize = res.tempFiles[i].size;
      const maxSize = 5 * 1024 * 1024;

      if (fileSize > maxSize) {
        uni.showToast({
          title: `图片${i + 1}大小不能超过5MB`,
          icon: "error",
        });
        continue;
      }

      // 添加图片到列表，准备上传
      uploadedImages.value.push({
        url: tempFilePath,
        filePath: tempFilePath,
        uploading: true,
        progress: 0,
      });

      // 上传图片
      await uploadImage(uploadedImages.value.length - 1);
    }

  } catch (error: any) {
    console.error("选择图片失败:", error);
    if (error.errMsg?.includes("cancel")) {
      return;
    }
    uni.showToast({
      title: "选择图片失败",
      icon: "error",
    });
  }
};

// 上传单张图片
const uploadImage = async (index: number) => {
  const imageItem = uploadedImages.value[index];
  if (!imageItem.filePath) return;

  try {
    const uploadOptions: UploadOptions = {
      filePath: imageItem.filePath,
      name: "image",
      compress: true,
      maxWidth: 800,
      maxHeight: 800,
      quality: 0.8,
      showProgress: true,
      onProgress: (progress: number) => {
        uploadedImages.value[index].progress = progress;
      },
    };

    const result: FileResult = await FileAPI.upload(uploadOptions);

    // 更新图片状态
    uploadedImages.value[index] = {
      url: result.accessUrl,
      serverUrl: result.accessUrl,
      uploading: false,
      progress: 100,
    };

  } catch (error: any) {
    console.error("图片上传失败:", error);
    uni.showToast({
      title: "图片上传失败",
      icon: "error",
    });

    // 上传失败，移除该图片项
    uploadedImages.value.splice(index, 1);
  }
};

// 删除图片
const removeImage = (index: number) => {
  uploadedImages.value.splice(index, 1);
  uni.showToast({
    title: "图片已删除",
    icon: "success",
  });
};

// 准备表单数据
const prepareFormData = (): FeedbackForm => {
  const submitData: FeedbackForm = {
    type: formData.type,
    content: formData.content?.trim(),
    images: [],
    contact: {
      phone: formData.contact.phone?.trim() || undefined,
      email: formData.contact.email?.trim() || undefined,
    },
  };

  // 如果有用户ID，添加到表单数据
  if (userStore.userInfo?.id) {
    submitData.userId = userStore.userInfo.id;
  }

  // 提取已上传完成的图片URL
  const imageUrls = uploadedImages.value
    .filter(img => !img.uploading && img.serverUrl)
    .map(img => img.serverUrl)
    .filter((url): url is string => url !== undefined);

  if (imageUrls.length > 0) {
    submitData.images = imageUrls;
  }

  // 如果联系方式都为空，则不传contact字段
  if (!submitData.contact?.phone && !submitData.contact?.email) {
    delete submitData.contact;
  }

  return submitData;
};

// 验证表单
const validateForm = (): boolean => {
  let isValid = true;

  // 验证内容
  if (!formData.content || formData.content.trim().length < 10) {
    contentError.value = "反馈内容至少需要10个字符";
    isValid = false;
  } else {
    contentError.value = "";
  }

  // 验证邮箱格式
  if (formData.contact.email && !isValidEmail(formData.contact.email)) {
    emailError.value = "请输入有效的邮箱地址";
    isValid = false;
  } else {
    emailError.value = "";
  }

  // 验证手机号格式
  if (formData.contact.phone && !isValidPhone(formData.contact.phone)) {
    phoneError.value = "请输入有效的手机号";
    isValid = false;
  } else {
    phoneError.value = "";
  }

  return isValid;
};

// 提交反馈
const submitFeedback = async () => {
  if (!canSubmit.value) return;

  // 验证表单
  if (!validateForm()) {
    uni.showToast({
      title: "请检查表单内容",
      icon: "none",
    });
    return;
  }

  // 检查是否还有图片在上传中
  const uploadingImages = uploadedImages.value.filter(img => img.uploading);
  if (uploadingImages.length > 0) {
    uni.showToast({
      title: "请等待图片上传完成",
      icon: "none",
    });
    return;
  }

  submitting.value = true;

  try {
    const submitData = prepareFormData();

    // 调用反馈API
    const result = await FeedbackAPI.add(submitData);

    submitting.value = false;
    submitSuccess.value = true;

    uni.showToast({
      title: "提交成功",
      icon: "success",
    });

    // 3秒后重置表单
    setTimeout(() => {
      resetForm();
    }, 3000);

  } catch (error: any) {
    console.error("提交失败:", error);
    submitting.value = false;

    let errorMessage = "提交失败，请稍后重试";
    if (error.message) {
      errorMessage = error.message;
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message;
    }

    uni.showToast({
      title: errorMessage,
      icon: "error",
    });
  }
};

// 重置表单
const resetForm = () => {
  formData.type = "bug";
  formData.content = "";
  formData.images = [];
  formData.contact = {
    phone: "",
    email: "",
  };
  uploadedImages.value = [];
  contentError.value = "";
  emailError.value = "";
  phoneError.value = "";
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
    background: color.adjust($background-color, $lightness: -5%);
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

/* 错误信息 */
.error-message {
  color: $danger-color;
  font-size: $font-size-small;
  margin-top: calc($margin-mini / 2);
  min-height: 40rpx;
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
  z-index: 2;

  &:active {
    transform: scale(0.9);
  }
}

.uploading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid rgba($background-color-white, 0.3);
  border-top-color: $background-color-white;
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 0.8s linear infinite;

  &.small {
    width: 30rpx;
    height: 30rpx;
    border-width: 3rpx;
    margin-bottom: $margin-mini;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.uploading-text {
  font-size: $font-size-extra-small;
  color: $background-color-white;
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

  &:active:not(:disabled) {
    background: color.adjust($background-color, $lightness: -5%);
    border-color: $primary-color;
    color: $primary-color;
  }

  &:disabled {
    opacity: $button-disabled-opacity;
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