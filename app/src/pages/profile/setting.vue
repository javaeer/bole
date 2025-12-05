<template>
  <view class="page-container">
    <!-- 顶部导航 -->
    <view class="edit-header">
      <view class="header-content">
        <text class="header-title">{{ pageTitle }}</text>
        <view class="header-actions">
          <button
            class="btn-save"
            :class="{ 'btn-save--loading': saving }"
            :disabled="!isFormChanged || saving"
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
      <view class="form-section">
        <text class="section-label">头像</text>
        <view class="avatar-upload">
          <view class="avatar-preview" @click="handleAvatarUpload">
            <image
              :src="formData.avatar || '/static/default-avatar.png'"
              class="avatar-image"
              mode="aspectFill"
            />
            <view class="avatar-overlay">
              <text class="upload-icon">📷</text>
              <text class="upload-text">点击更换</text>
            </view>
          </view>
        </view>
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
            <text :class="['count-text', { 'count-warning': formData.bio.length > 180 }]">
              {{ formData.bio.length }}/200
            </text>
          </view>
        </view>
      </view>

      <!-- 联系信息 -->
      <view class="form-section">
        <text class="section-label">联系信息</text>

        <view class="form-item">
          <text class="item-label">手机号</text>
          <input
            v-model="formData.phone"
            class="form-input"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="placeholder"
            maxlength="11"
            @input="handleInputChange('phone')"
          />
          <view v-if="formError.phone" class="error-message">
            {{ formError.phone }}
          </view>
        </view>

        <view class="form-item">
          <text class="item-label">邮箱</text>
          <input
            v-model="formData.email"
            class="form-input"
            type="text"
            placeholder="请输入邮箱地址"
            placeholder-class="placeholder"
            @input="handleInputChange('email')"
          />
          <view v-if="formError.email" class="error-message">
            {{ formError.email }}
          </view>
        </view>

        <view class="form-item">
          <text class="item-label">所在城市</text>
          <picker
            mode="region"
            :value="formData.region"
            @change="handleRegionChange"
          >
            <view class="picker-input">
              <text :class="['picker-text', { 'placeholder': !formData.region.length }]">
                {{ formData.region.length ? formData.region.join(' ') : '请选择省市区' }}
              </text>
              <text class="picker-arrow">›</text>
            </view>
          </picker>
        </view>
      </view>

      <!-- 隐私设置 -->
      <view class="form-section">
        <text class="section-label">隐私设置</text>

        <view class="form-item">
          <view class="switch-item">
            <text class="switch-label">简历公开状态</text>
            <switch
              :checked="formData.resumePublic"
              color="$primary-color"
              @change="handleSwitchChange('resumePublic', $event)"
            />
          </view>
          <text class="switch-hint">开启后，其他用户可以查看您的公开简历</text>
        </view>

        <view class="form-item">
          <view class="switch-item">
            <text class="switch-label">接收职位推荐</text>
            <switch
              :checked="formData.receiveJobRecommend"
              color="$primary-color"
              @change="handleSwitchChange('receiveJobRecommend', $event)"
            />
          </view>
          <text class="switch-hint">根据您的简历内容为您推荐合适职位</text>
        </view>
      </view>

      <!-- 危险操作区 -->
      <view class="form-section danger-section">
        <text class="section-label danger-label">危险操作</text>

        <view class="danger-item" @click="handleAccountLogout">
          <text class="danger-text">退出登录</text>
          <text class="danger-arrow">›</text>
        </view>

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
import { ref, reactive, computed, onMounted } from 'vue'

const pageTitle = ref('编辑资料')
const saving = ref(false)

// 表单数据
const originalData = {
  avatar: '/static/default-avatar.png',
  name: '张三',
  title: '前端开发工程师',
  bio: '专注前端开发5年，精通Vue/React技术栈',
  phone: '13800138000',
  email: 'zhangsan@example.com',
  region: ['广东省', '深圳市', '南山区'],
  resumePublic: true,
  receiveJobRecommend: true
}

const formData = reactive({ ...originalData })

// 表单错误
const formError = reactive({
  name: '',
  title: '',
  phone: '',
  email: ''
})

// 表单是否修改
const isFormChanged = computed(() => {
  return JSON.stringify(formData) !== JSON.stringify(originalData)
})

// 输入变化处理
const handleInputChange = (field: string) => {
  // 清除该字段的错误信息
  if (formError[field]) {
    formError[field] = ''
  }

  // 验证手机号
  if (field === 'phone' && formData.phone) {
    if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
      formError.phone = '请输入正确的手机号'
    }
  }

  // 验证邮箱
  if (field === 'email' && formData.email) {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      formError.email = '请输入正确的邮箱地址'
    }
  }

  // 验证姓名
  if (field === 'name' && formData.name) {
    if (formData.name.length < 2) {
      formError.name = '姓名至少2个字符'
    }
  }
}

// 地区选择
const handleRegionChange = (event: any) => {
  const value = event.detail.value
  formData.region = value
}

// 开关切换
const handleSwitchChange = (field: string, event: any) => {
  formData[field] = event.detail.value
}

// 头像上传
const handleAvatarUpload = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0]
      // 这里应该上传到服务器，这里只是本地预览
      formData.avatar = tempFilePath
    }
  })
}

// 保存数据
const handleSave = async () => {
  // 验证表单
  const hasError = Object.values(formError).some(error => error)
  if (hasError) {
    uni.showToast({
      title: '请修正表单错误',
      icon: 'none'
    })
    return
  }

  saving.value = true

  // 模拟保存请求
  setTimeout(() => {
    saving.value = false
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })

    // 更新原始数据
    Object.assign(originalData, { ...formData })
  }, 1500)
}

// 返回
const handleBack = () => {
  if (isFormChanged.value) {
    uni.showModal({
      title: '提示',
      content: '您有未保存的更改，确定要返回吗？',
      success: (res) => {
        if (res.confirm) {
          uni.navigateBack()
        }
      }
    })
  } else {
    uni.navigateBack()
  }
}

// 退出登录
const handleAccountLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出当前账号吗？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({
          title: '退出成功',
          icon: 'success'
        })
        // 实际应用中这里应该清理用户状态并跳转到登录页
        setTimeout(() => {
          uni.reLaunch({
            url: '/pages/auth/login'
          })
        }, 1500)
      }
    }
  })
}

// 注销账号
const handleAccountDelete = () => {
  uni.showModal({
    title: '危险操作',
    content: '账号注销后将无法恢复，所有数据将被永久删除，确定继续吗？',
    confirmColor: '$danger-color',
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: '注销中...'
        })
        // 模拟注销请求
        setTimeout(() => {
          uni.hideLoading()
          uni.showToast({
            title: '账号已注销',
            icon: 'success'
          })
          // 跳转到登录页
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/auth/login'
            })
          }, 1500)
        }, 2000)
      }
    }
  })
}

onMounted(() => {
  // 可以在这里加载用户数据
})
</script>


<style scoped lang="scss">

.custom-switch:deep(switch) {
  // 在微信小程序中可能需要更具体的选择器，例如：
  // .custom-switch:deep(.wx-switch-input.wx-switch-input-checked) [citation:7]
  color: $primary-color !important;
}

.page-container {
  background-color: $background-color;
  min-height: 100vh;
}

/* 顶部导航 */
.edit-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding-top: calc(var(--status-bar-height) + 20rpx);
  padding-bottom: $padding-small;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $padding-base;
  height: 88rpx;
}

.header-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $background-color-white;
}

.header-actions {
  width: 60rpx;
}

.btn-save {
  background: transparent;
  border: none;
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  padding: 0;

  &:disabled {
    opacity: $button-disabled-opacity;
  }

  &--loading {
    opacity: 0.8;
  }
}

.loading-spinner {
  width: 30rpx;
  height: 30rpx;
  border: 3rpx solid rgba($background-color-white, 0.3);
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

/* 表单内容 */
.edit-content {
  height: calc(100vh - var(--status-bar-height) - 128rpx);
}

/* 表单区块 */
.form-section {
  background: $background-color-white;
  margin: $margin-base 0;
  padding: 0 $padding-base;
}

.section-label {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
  padding: $padding-base 0 $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

/* 头像上传 */
.avatar-upload {
  @extend .flex-center;
  padding: $padding-base 0;
}

.avatar-preview {
  position: relative;
  width: 150rpx;
  height: 150rpx;
  border-radius: $border-radius-round;
  overflow: hidden;
  cursor: pointer;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($text-primary, 0.6);
  @extend .flex-center;
  flex-direction: column;
  opacity: 0;
  transition: opacity $transition-fast;

  .avatar-preview:active & {
    opacity: 1;
  }
}

.upload-icon {
  font-size: $font-size-large;
  color: $background-color-white;
  margin-bottom: $margin-mini;
}

.upload-text {
  font-size: $font-size-small;
  color: $background-color-white;
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
  color: $text-regular;
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

/* 文本域 */
.form-textarea {
  width: 100%;
  min-height: 120rpx;
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
  font-size: $font-size-extra-small;
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

/* 开关项 */
.switch-item {
  @extend .flex-between;
  margin-bottom: $margin-mini;
}

.switch-label {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.switch-hint {
  display: block;
  font-size: $font-size-extra-small;
  color: $text-secondary;
  line-height: 1.5;
}

/* 危险操作区 */
.danger-section {
  margin-bottom: $margin-base * 2;
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
    background: rgba($danger-color, 0.05);
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
  margin-top: calc($margin-mini / 2);
  min-height: 20rpx;
}

/* 底部占位 */
.bottom-space {
  height: 50rpx;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .form-section {
    padding: 0 $padding-small;
  }
}
</style>