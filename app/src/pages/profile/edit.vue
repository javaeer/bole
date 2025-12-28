<template>
  <view class="page-container">
    <!-- 顶部导航 -->
    <view class="edit-header">
      <view class="header-content">
        <text class="header-title"></text>
        <view class="header-actions">
          <button
            class="btn-save"
            :class="{ 'btn-save--loading': saving }"
            :disabled="!isFormChanged || saving || uploadingAvatar"
            @click="handleSave"
          >
            <text v-if="!saving">保存</text>
            <view v-else class="loading-spinner"></view>
          </button>
        </view>
      </view>
    </view>

    <!-- 表单内容 -->
    <scroll-view class="edit-content" scroll-y="true">
      <!-- 头像上传 -->
      <view class="avatar-section form-section">
        <view class="avatar-container" @click="handleAvatarUpload">
          <image
            v-if="formData.avatar"
            :src="formData.avatar"
            class="avatar-image"
            mode="aspectFill"
          />
          <view v-else class="avatar-placeholder">
            <text class="avatar-icon">+</text>
            <text class="avatar-text">上传头像</text>
          </view>

          <!-- 上传遮罩层 -->
          <view v-if="uploadingAvatar" class="upload-mask">
            <view class="upload-progress">
              <view class="progress-circle">
                <view class="circle-bg"></view>
                <view class="circle-fill" :style="{ transform: `rotate(${uploadProgress * 3.6}deg)` }"></view>
              </view>
              <text class="progress-text">{{ uploadProgress }}%</text>
            </view>
          </view>
        </view>
        <text class="avatar-tips">建议尺寸 200×200 像素，支持 JPG/PNG</text>
      </view>

      <!-- 基本信息 -->
      <view class="form-section">
        <text class="section-label">基本信息</text>

        <view class="form-item">
          <text class="item-label">姓名</text>
          <input
            v-model="formData.name"
            class="form-input"
            type="text"
            placeholder="请输入姓名"
            placeholder-class="placeholder"
            maxlength="20"
            @input="handleInputChange('name')"
          />
          <view v-if="formError.name" class="error-message">
            {{ formError.name }}
          </view>
        </view>

        <view class="form-item">
          <text class="item-label">职称/职位</text>
          <input
            v-model="formData.title"
            class="form-input"
            type="text"
            placeholder="请输入职称或职位"
            placeholder-class="placeholder"
            maxlength="50"
            @input="handleInputChange('title')"
          />
          <view v-if="formError.title" class="error-message">
            {{ formError.title }}
          </view>
        </view>

        <view class="form-item">
          <text class="item-label">个人简介</text>
          <textarea
            v-model="formData.bio"
            class="form-textarea"
            placeholder="请简单介绍一下自己..."
            placeholder-class="placeholder"
            maxlength="200"
            auto-height
            @input="handleInputChange('bio')"
          />
          <view class="word-count">
            <text :class="['count-text', { 'count-warning': formData.bio?.length > 180 }]">
              {{ formData.bio?.length || 0 }}/200
            </text>
          </view>
        </view>
      </view>

      <!-- 联系信息 -->
      <view class="form-section">
        <text class="section-label">联系信息</text>

        <!-- 账号名 -->
        <view class="form-item">
          <text class="item-label">账号名</text>
          <input
            v-model="formData.username"
            class="form-input"
            type="text"
            placeholder="请输入账号名"
            placeholder-class="placeholder"
            maxlength="20"
            @input="handleInputChange('username')"
          />
          <view v-if="formError.username" class="error-message">
            {{ formError.username }}
          </view>
        </view>

        <!-- 手机号 - 改为绑定状态显示 -->
        <view class="form-item bind-item">
          <text class="item-label">手机号</text>
          <view class="bind-content">
            <text class="bind-text">
              {{ formData.phone || "未绑定" }}
            </text>
            <view class="bind-action" @click="handleGoToBind('phone')">
              <text class="bind-btn">
                {{ formData.phone ? "更换" : "绑定" }}
              </text>
              <text class="bind-arrow">›</text>
            </view>
          </view>
        </view>

        <!-- 邮箱 - 改为绑定状态显示 -->
        <view class="form-item bind-item">
          <text class="item-label">邮箱</text>
          <view class="bind-content">
            <text class="bind-text">
              {{ formData.email || "未绑定" }}
            </text>
            <view class="bind-action" @click="handleGoToBind('email')">
              <text class="bind-btn">
                {{ formData.email ? "更换" : "绑定" }}
              </text>
              <text class="bind-arrow">›</text>
            </view>
          </view>
        </view>

        <!-- 所在城市 -->
        <view class="form-item">
          <text class="item-label">所在城市</text>
          <picker
            mode="region"
            :value="formData.region"
            @change="handleRegionChange"
          >
            <view class="picker-input">
              <text :class="['picker-text', { 'placeholder': !formData.region?.length }]">
                {{ formData.region?.length ? formData.region.join(" ") : "请选择省市区" }}
              </text>
              <text class="picker-arrow">›</text>
            </view>
          </picker>
        </view>

        <!-- 微信 - 改为绑定状态显示 -->
        <view class="form-item bind-item">
          <text class="item-label">微信号</text>
          <view class="bind-content">
            <text class="bind-text">
              {{ formData.wechat ? "已绑定" : "未绑定" }}
            </text>
            <view class="bind-action" @click="handleGoToBind('wechat')">
              <text class="bind-btn">
                {{ formData.wechat ? "解绑" : "绑定" }}
              </text>
              <text class="bind-arrow">›</text>
            </view>
          </view>
        </view>

        <!-- GitHub -->
        <view class="form-item">
          <text class="item-label">GitHub</text>
          <input
            v-model="formData.github"
            class="form-input"
            type="text"
            placeholder="GitHub用户名"
            placeholder-class="placeholder"
            @input="handleInputChange('github')"
          />
        </view>

        <!-- 个人网站 -->
        <view class="form-item">
          <text class="item-label">个人网站</text>
          <input
            v-model="formData.website"
            class="form-input"
            type="text"
            placeholder="https://"
            placeholder-class="placeholder"
            @input="handleInputChange('website')"
          />
        </view>

        <!-- 所在公司 -->
        <view class="form-item">
          <text class="item-label">所在公司</text>
          <input
            v-model="formData.company"
            class="form-input"
            type="text"
            placeholder="请输入公司名称"
            placeholder-class="placeholder"
            maxlength="50"
            @input="handleInputChange('company')"
          />
        </view>
      </view>

      <!-- 危险操作区 -->
      <view class="form-section danger-section">
        <text class="section-label danger-label">危险操作</text>
        <view class="danger-item" @click="handleAccountDelete">
          <text class="danger-text danger-delete">注销账号</text>
          <text class="danger-arrow">›</text>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from "vue";
import { useUserStore } from "@/stores/user";
import { getUserInfo } from "@/utils/store";
import FileAPI from "@/api/file";
import { FileResult } from "@/types/file";
import { UploadOptions } from "@/types/request";
import { EventKey } from "@/constants/event-key";

const pageTitle = ref("编辑资料");
const saving = ref(false);
const uploadingAvatar = ref(false);
const uploadProgress = ref(0);
const userStore = useUserStore();

// 表单数据
const info = getUserInfo();
const originalData = {
  avatar: info?.avatar || "",
  username: info?.username || "",
  name: info?.name || "",
  title: info?.title || "",
  bio: info?.bio || "",
  phone: info?.phone || "",
  email: info?.email || "",
  location: info?.location || "",
  region: info?.location?.split(" ") || [],
  github: info?.github || "",
  wechat: info?.wechat || "",
  website: info?.website || "",
  company: info?.company || "",
};

const formData = reactive({ ...originalData });

// 表单错误
const formError = reactive({
  username: "",
  name: "",
  title: "",
  phone: "",
  email: "",
});

// 表单是否修改
const isFormChanged = computed(() => {
  return JSON.stringify(formData) !== JSON.stringify(originalData);
});

// 输入变化处理
const handleInputChange = (field: string) => {
  // 清除该字段的错误信息
  if (formError[field]) {
    formError[field] = "";
  }

  // 验证逻辑
  if (field === "name" && formData.name) {
    if (formData.name.length < 2) {
      formError.name = "姓名至少2个字符";
    }
  }

  if (field === "username" && formData.username) {
    if (formData.username.length < 3) {
      formError.username = "账号名至少3个字符";
    }
  }
};

// 地区选择
const handleRegionChange = (event: any) => {
  const value = event.detail.value;
  formData.region = value;
  formData.location = value.join(" ");
};

// 跳转到绑定页面
const handleGoToBind = (type: "phone" | "email" | "wechat") => {
  uni.navigateTo({
    url: `/pages/profile/bind?type=${type}`,
    success: () => {
      console.log(`跳转到${type}绑定页面`);
    },
    fail: (error) => {
      console.error("跳转失败:", error);
      uni.showToast({
        title: "跳转失败，请稍后重试",
        icon: "error",
      });
    },
  });
};

// 头像上传
const handleAvatarUpload = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ["compressed", "original"],
    sourceType: ["album", "camera"],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0];
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

      uploadingAvatar.value = true;
      uploadProgress.value = 0;

      try {
        uni.showLoading({
          title: "上传中...",
          mask: true,
        });

        // 构建上传参数
        const uploadOptions: UploadOptions = {
          filePath: tempFilePath,
          name: "avatar",
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

        // 上传成功，更新头像URL
        formData.avatar = result.accessUrl;

        uni.hideLoading();
        uni.showToast({
          title: "头像上传成功",
          icon: "success",
        });

      } catch (error: any) {
        console.error("头像上传失败:", error);
        uni.hideLoading();

        let errorMsg = "头像上传失败";
        if (error.code === "NETWORK_ERROR") {
          errorMsg = "网络错误，请检查网络连接";
        } else if (error.code === "UPLOAD_FAILED") {
          errorMsg = "上传失败，请稍后重试";
        } else if (error.message) {
          errorMsg = error.message;
        }

        uni.showToast({
          title: errorMsg,
          icon: "error",
          duration: 3000,
        });
      } finally {
        uploadingAvatar.value = false;
        uploadProgress.value = 0;
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

// 验证表单
const validateForm = (): boolean => {
  let isValid = true;

  // 验证必填字段
  if (!formData.name) {
    formError.name = "姓名不能为空";
    isValid = false;
  } else if (formData.name.length < 2) {
    formError.name = "姓名至少2个字符";
    isValid = false;
  }

  return isValid;
};

// 准备更新数据
const prepareUpdateData = () => {
  const updateData: Record<string, any> = {};

  // 只提交有变化的字段（排除绑定字段）
  const nonBindFields = ["avatar", "username", "name", "title", "bio", "location", "github", "website", "company"];

  nonBindFields.forEach(key => {
    if (formData[key] !== originalData[key]) {
      updateData[key] = formData[key];
    }
  });

  // 处理特殊字段
  if (updateData.region) {
    updateData.location = updateData.region.join(" ");
    delete updateData.region;
  }

  return updateData;
};

// 保存数据
const handleSave = async () => {
  // 验证表单
  if (!validateForm()) {
    uni.showToast({
      title: "请修正表单错误",
      icon: "none",
    });
    return;
  }

  saving.value = true;

  try {
    // 准备更新数据
    const updateData = prepareUpdateData();

    // 如果没有数据需要更新
    if (Object.keys(updateData).length === 0) {
      uni.showToast({
        title: "没有数据需要更新",
        icon: "none",
      });
      saving.value = false;
      return;
    }

    // 调用 store 方法更新用户信息
    await userStore.updateProfile(updateData);

    // 更新原始数据
    Object.assign(originalData, { ...formData });

    // 触发全局事件，通知其他页面更新用户信息
    uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
      success: true,
      timestamp: Date.now(),
      data: updateData,
    });

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

    // 保存成功后返回上一页
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);

  } catch (error: any) {
    console.error("保存失败:", error);
    uni.showToast({
      title: error.message || "保存失败，请稍后重试",
      icon: "error",
    });
  } finally {
    saving.value = false;
  }
};

// 注销账号
const handleAccountDelete = () => {
  uni.showModal({
    title: "危险操作",
    content: "账号注销后将无法恢复，所有数据将被永久删除，确定继续吗？",
    confirmColor: "$danger-color",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: "注销中...",
        });
        userStore.closure();
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "账号已注销",
            icon: "success",
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/auth/auth",
            });
          }, 1500);
        }, 2000);
      }
    },
  });
};

onMounted(() => {
  uni.setNavigationBarTitle({
    title: pageTitle.value,
  });
});

// 清理事件监听
onUnmounted(() => {
  // 移除本页面可能监听的事件
  // uni.$off(EventKey.USER_INFO_UPDATED_EVENT);
});
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100vh;
  background-color: $background-color;
}

/* 顶部导航 */
.edit-header {
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);
  padding-top: var(--status-bar-height, 44px);
  padding-bottom: $padding-mini;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $padding-base;
  height: $navigation-bar-height;
}

.header-title {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $background-color-white;
}

.header-actions {
  width: 120rpx;
}

.btn-save {
  background: transparent;
  border: none;
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  padding: 0;
  height: 60rpx;
  line-height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:disabled {
    opacity: $button-disabled-opacity;
  }

  &--loading {
    opacity: 0.8;
  }
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid rgba($background-color-white, 0.3);
  border-top-color: $background-color-white;
  border-radius: $border-radius-round;
  margin: 0 auto;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 表单内容 */
.edit-content {
  height: calc(100vh - var(--status-bar-height, 44px) - $navigation-bar-height - 20rpx);
}

/* 头像区域 */
.avatar-section {
  text-align: center;
  padding: $padding-large $padding-base;
  background: $background-color-white;
  margin: $margin-small 0;
}

.avatar-container {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: $border-radius-round;
  background: $background-color-white;
  border: 4rpx solid $border-color-light;
  margin: 0 auto $margin-mini;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    opacity: 0.9;
  }
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 48rpx;
  color: $primary-color;
  margin-bottom: $uni-spacing-col-sm;
}

.avatar-text {
  font-size: $font-size-extra-small;
  color: $text-placeholder;
}

.avatar-tips {
  font-size: $font-size-extra-small;
  color: $text-placeholder;
}

/* 上传遮罩层 */
.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: $uni-bg-color-mask;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-circle {
  position: relative;
  width: 60rpx;
  height: 60rpx;
  margin-bottom: $uni-spacing-col-sm;
}

.circle-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4rpx solid rgba($background-color-white, 0.3);
  border-radius: $border-radius-round;
}

.circle-fill {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4rpx solid $background-color-white;
  border-radius: $border-radius-round;
  clip: rect(0, 30rpx, 60rpx, 0);
  transform-origin: center;
}

.progress-text {
  font-size: $font-size-small;
  color: $background-color-white;
  font-weight: $font-weight-medium;
}

/* 表单区块 */
.form-section {
  background: $background-color-white;
  margin: $margin-small 0;
  padding: 0 $padding-base;
  border-radius: $border-radius;
}

.section-label {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
  padding: $padding-base 0 $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

/* 表单项 */
.form-item {
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }
}

.item-label {
  display: block;
  font-size: $font-size-base;
  color: $text-primary;
  margin-bottom: $margin-mini;
  font-weight: $font-weight-medium;
}

.form-input {
  width: 100%;
  height: $input-height;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;

  &::placeholder {
    color: $text-placeholder;
  }
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

/* 绑定项样式 */
.bind-item {
  .item-label {
    margin-bottom: 0;
  }
}

.bind-content {
  @extend .flex-between;
  padding: $padding-mini 0;
}

.bind-text {
  font-size: $font-size-base;
  color: $text-primary;

  &:empty:before {
    content: '未绑定';
    color: $text-placeholder;
  }
}

.bind-action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: $padding-mini;
  border-radius: $border-radius-small;

  &:active {
    background: $background-color;
  }
}

.bind-btn {
  font-size: $font-size-small;
  color: $primary-color;
  font-weight: $font-weight-medium;
}

.bind-arrow {
  font-size: $font-size-medium;
  color: $text-placeholder;
  transform: rotate(90deg);
}

/* 文本域 */
.form-textarea {
  width: 100%;
  min-height: 160rpx;
  font-size: $font-size-base;
  color: $text-primary;
  background: transparent;
  border: none;
  outline: none;
  line-height: 1.6;
  padding: $padding-mini 0;
}

.word-count {
  text-align: right;
  margin-top: $margin-mini;
}

.count-text {
  font-size: $font-size-small;
  color: $text-secondary;

  &.count-warning {
    color: $warning-color;
  }
}

/* 选择器样式 */
.picker-input {
  width: 100%;
  height: $input-height;
  @extend .flex-between;
  cursor: pointer;
}

.picker-text {
  font-size: $font-size-base;
  color: $text-primary;

  &.placeholder {
    color: $text-placeholder;
  }
}

.picker-arrow {
  font-size: $font-size-medium;
  color: $text-placeholder;
  transform: rotate(90deg);
}

/* 危险操作区 */
.danger-section {
  margin-bottom: $margin-base;
}

.danger-label {
  color: $danger-color;
}

.danger-item {
  @extend .flex-between;
  padding: $padding-base 0;
  border-bottom: 1rpx solid $border-color-extra-light;
  cursor: pointer;

  &:active {
    background: $danger-bg;
  }

  &:last-child {
    border-bottom: none;
  }
}

.danger-text {
  font-size: $font-size-base;
  color: $text-primary;

  &.danger-delete {
    color: $danger-color;
  }
}

.danger-arrow {
  font-size: $font-size-medium;
  color: $text-placeholder;
}

/* 错误信息 */
.error-message {
  color: $danger-color;
  font-size: $font-size-small;
  margin-top: $uni-spacing-col-sm;
  min-height: 40rpx;
}

/* 底部占位 */
.bottom-space {
  height: $tabbar-height;
}

/* 响应式调整 */
@media (max-width: $screen-sm) {
  .form-section {
    padding: 0 $padding-small;
  }

  .header-content {
    padding: 0 $padding-small;
  }
}
</style>