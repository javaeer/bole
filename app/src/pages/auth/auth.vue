<template>
  <view class="auth-container">
    <!-- 背景装饰 -->
    <view class="background-decorations">
      <view class="decoration-circle circle-1"></view>
      <view class="decoration-circle circle-2"></view>
      <view class="decoration-circle circle-3"></view>
      <view class="background-overlay"></view>
    </view>

    <!-- 顶部品牌 -->
    <view class="brand-header">
      <image src="/static/logo.png" class="brand-logo" mode="aspectFit" />
      <text class="brand-name">{{ systemName }}</text>
    </view>

    <!-- 表单卡片 -->
    <view class="form-card">
      <!-- 切换选项卡 -->
      <view class="auth-tabs">
        <view 
          class="tab-item" 
          :class="{ active: activeTab === 'login' }"
          @click="switchTab('login')"
        >
          <text class="tab-text">登录</text>
          <view v-if="activeTab === 'login'" class="tab-indicator"></view>
        </view>
        <view 
          class="tab-item" 
          :class="{ active: activeTab === 'register' }"
          @click="switchTab('register')"
        >
          <text class="tab-text">注册</text>
          <view v-if="activeTab === 'register'" class="tab-indicator"></view>
        </view>
      </view>

      <!-- 登录表单 -->
      <view v-if="activeTab === 'login'" class="auth-form">
        <view class="form-group">
          <text class="form-label">手机号/邮箱</text>
          <view class="input-wrapper">
            <input 
              v-model="loginForm.account"
              class="form-input"
              type="text"
              placeholder="请输入手机号或邮箱"
              placeholder-class="placeholder"
              @focus="handleInputFocus('account')"
              @blur="handleInputBlur('account')"
            />
          </view>
          <view v-if="loginError.account" class="error-message">
            {{ loginError.account }}
          </view>
        </view>

        <view class="form-group">
          <view class="label-row">
            <text class="form-label">密码</text>
            <text class="forgot-password" @click="handleForgotPassword">忘记密码？</text>
          </view>
          <view class="input-wrapper">
            <input 
              v-model="loginForm.password"
              class="form-input"
              :type="showLoginPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              placeholder-class="placeholder"
              @focus="handleInputFocus('password')"
              @blur="handleInputBlur('password')"
            />
            <view class="password-toggle" @click="showLoginPassword = !showLoginPassword">
              <text class="toggle-icon">{{ showLoginPassword ? '👁️' : '👁️‍🗨️' }}</text>
            </view>
          </view>
          <view v-if="loginError.password" class="error-message">
            {{ loginError.password }}
          </view>
        </view>

        <button 
          class="btn-auth" 
          :class="{ 'btn-auth--loading': loginLoading }"
          :disabled="loginLoading"
          @click="handleLogin"
        >
          <text v-if="!loginLoading">登录</text>
          <view v-else class="loading-spinner"></view>
        </button>

        <!-- 其他登录方式 -->
        <view class="other-login">
          <view class="divider">
            <view class="divider-line"></view>
            <text class="divider-text">其他登录方式</text>
            <view class="divider-line"></view>
          </view>
          <view class="login-methods">
            <button class="login-method" @click="handleWechatLogin">
              <text class="method-icon">💬</text>
              <text class="method-text">微信登录</text>
            </button>
            <button class="login-method" @click="handleSmsLogin">
              <text class="method-icon">📱</text>
              <text class="method-text">短信验证</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 注册表单 -->
      <view v-if="activeTab === 'register'" class="auth-form">
        <view class="form-group">
          <text class="form-label">手机号</text>
          <view class="input-wrapper">
            <input 
              v-model="registerForm.phone"
              class="form-input"
              type="number"
              placeholder="请输入手机号"
              placeholder-class="placeholder"
              maxlength="11"
              @focus="handleInputFocus('phone')"
              @blur="handleInputBlur('phone')"
            />
          </view>
          <view v-if="registerError.phone" class="error-message">
            {{ registerError.phone }}
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">验证码</text>
          <view class="code-input-wrapper">
            <view class="input-wrapper code-input">
              <input 
                v-model="registerForm.code"
                class="form-input"
                type="number"
                placeholder="请输入验证码"
                placeholder-class="placeholder"
                maxlength="6"
                @focus="handleInputFocus('code')"
                @blur="handleInputBlur('code')"
              />
            </view>
            <button 
              class="btn-code" 
              :disabled="codeCountdown > 0"
              @click="handleSendCode"
            >
              {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '获取验证码' }}
            </button>
          </view>
          <view v-if="registerError.code" class="error-message">
            {{ registerError.code }}
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">设置密码</text>
          <view class="input-wrapper">
            <input 
              v-model="registerForm.password"
              class="form-input"
              :type="showRegisterPassword ? 'text' : 'password'"
              placeholder="6-20位字母、数字或符号"
              placeholder-class="placeholder"
              @focus="handleInputFocus('password')"
              @blur="handleInputBlur('password')"
            />
            <view class="password-toggle" @click="showRegisterPassword = !showRegisterPassword">
              <text class="toggle-icon">{{ showRegisterPassword ? '👁️' : '👁️‍🗨️' }}</text>
            </view>
          </view>
          <view class="password-strength" :class="strengthClass">
            <view class="strength-bar" :style="{ width: strengthWidth }"></view>
            <text class="strength-text">{{ strengthText }}</text>
          </view>
          <view v-if="registerError.password" class="error-message">
            {{ registerError.password }}
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">确认密码</text>
          <view class="input-wrapper">
            <input 
              v-model="registerForm.confirmPassword"
              class="form-input"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="请再次输入密码"
              placeholder-class="placeholder"
              @focus="handleInputFocus('confirmPassword')"
              @blur="handleInputBlur('confirmPassword')"
            />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <text class="toggle-icon">{{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}</text>
            </view>
          </view>
          <view v-if="registerError.confirmPassword" class="error-message">
            {{ registerError.confirmPassword }}
          </view>
        </view>

        <view class="agreement-group">
          <view class="checkbox" @click="agreed = !agreed">
            <text class="checkbox-icon" :class="{ checked: agreed }">✓</text>
          </view>
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @click="handleUserAgreement">《用户协议》</text>
            和
            <text class="agreement-link" @click="handlePrivacyPolicy">《隐私政策》</text>
          </text>
        </view>

        <button 
          class="btn-auth" 
          :class="{ 'btn-auth--loading': registerLoading }"
          :disabled="registerLoading || !agreed"
          @click="handleRegister"
        >
          <text v-if="!registerLoading">注册</text>
          <view v-else class="loading-spinner"></view>
        </button>
      </view>

      <!-- 底部链接 -->
      <view class="auth-footer">
        <text v-if="activeTab === 'login'" class="footer-text">
          还没有账号？
          <text class="footer-link" @click="switchTab('register')">立即注册</text>
        </text>
        <text v-if="activeTab === 'register'" class="footer-text">
          已有账号？
          <text class="footer-link" @click="switchTab('login')">立即登录</text>
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'

const activeTab = ref('login')
const systemName = '简历大师'

// 登录表单
const loginForm = reactive({
  account: '',
  password: ''
})

const loginError = reactive({
  account: '',
  password: ''
})

const showLoginPassword = ref(false)
const loginLoading = ref(false)

// 注册表单
const registerForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const registerError = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)
const registerLoading = ref(false)

// 验证码倒计时
const codeCountdown = ref(0)
let codeTimer: any = null

// 协议同意
const agreed = ref(false)

// 密码强度计算
const passwordStrength = computed(() => {
  const password = registerForm.password
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 6) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  return Math.min(strength, 5)
})

const strengthWidth = computed(() => {
  return `${passwordStrength.value * 20}%`
})

const strengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '请设置密码'
  if (strength <= 2) return '弱'
  if (strength <= 3) return '中'
  return '强'
})

const strengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return 'strength-weak'
  if (strength <= 3) return 'strength-medium'
  return 'strength-strong'
})

// 切换选项卡
const switchTab = (tab: string) => {
  activeTab.value = tab
  clearFormErrors()
}

// 清除表单错误
const clearFormErrors = () => {
  if (activeTab.value === 'login') {
    loginError.account = ''
    loginError.password = ''
  } else {
    registerError.phone = ''
    registerError.code = ''
    registerError.password = ''
    registerError.confirmPassword = ''
  }
}

// 输入框聚焦/失焦处理
const handleInputFocus = (field: string) => {
  clearFormErrors()
}

const handleInputBlur = (field: string) => {
  validateField(field)
}

// 字段验证
const validateField = (field: string) => {
  if (activeTab.value === 'login') {
    switch (field) {
      case 'account':
        if (!loginForm.account.trim()) {
          loginError.account = '请输入手机号或邮箱'
        } else if (!/^1[3-9]\d{9}$/.test(loginForm.account) && 
                   !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.account)) {
          loginError.account = '请输入正确的手机号或邮箱'
        }
        break
      case 'password':
        if (!loginForm.password) {
          loginError.password = '请输入密码'
        } else if (loginForm.password.length < 6) {
          loginError.password = '密码长度不能少于6位'
        }
        break
    }
  } else {
    switch (field) {
      case 'phone':
        if (!registerForm.phone) {
          registerError.phone = '请输入手机号'
        } else if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
          registerError.phone = '请输入正确的手机号'
        }
        break
      case 'code':
        if (!registerForm.code) {
          registerError.code = '请输入验证码'
        } else if (!/^\d{6}$/.test(registerForm.code)) {
          registerError.code = '验证码为6位数字'
        }
        break
      case 'password':
        if (!registerForm.password) {
          registerError.password = '请输入密码'
        } else if (registerForm.password.length < 6) {
          registerError.password = '密码长度不能少于6位'
        }
        break
      case 'confirmPassword':
        if (!registerForm.confirmPassword) {
          registerError.confirmPassword = '请确认密码'
        } else if (registerForm.password !== registerForm.confirmPassword) {
          registerError.confirmPassword = '两次输入的密码不一致'
        }
        break
    }
  }
}

// 登录
const handleLogin = () => {
  // 验证所有字段
  validateField('account')
  validateField('password')
  
  // 如果有错误，不提交
  if (loginError.account || loginError.password) {
    return
  }
  
  loginLoading.value = true
  
  // 模拟登录请求
  setTimeout(() => {
    loginLoading.value = false
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })
    
    // 跳转到首页
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }, 1500)
  }, 2000)
}

// 注册
const handleRegister = () => {
  // 验证所有字段
  validateField('phone')
  validateField('code')
  validateField('password')
  validateField('confirmPassword')
  
  // 检查协议
  if (!agreed.value) {
    uni.showToast({
      title: '请阅读并同意用户协议',
      icon: 'none'
    })
    return
  }
  
  // 如果有错误，不提交
  if (registerError.phone || registerError.code || 
      registerError.password || registerError.confirmPassword) {
    return
  }
  
  registerLoading.value = true
  
  // 模拟注册请求
  setTimeout(() => {
    registerLoading.value = false
    uni.showToast({
      title: '注册成功',
      icon: 'success'
    })
    
    // 切换到登录页
    setTimeout(() => {
      activeTab.value = 'login'
      // 清空注册表单
      registerForm.phone = ''
      registerForm.code = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
    }, 1500)
  }, 2000)
}

// 发送验证码
const handleSendCode = () => {
  if (!registerForm.phone || registerError.phone) {
    validateField('phone')
    if (registerError.phone) return
  }
  
  // 开始倒计时
  codeCountdown.value = 60
  uni.showToast({
    title: '验证码已发送',
    icon: 'success'
  })
  
  codeTimer = setInterval(() => {
    if (codeCountdown.value > 0) {
      codeCountdown.value--
    } else {
      clearInterval(codeTimer)
    }
  }, 1000)
}

// 忘记密码
const handleForgotPassword = () => {
  uni.navigateTo({
    url: '/pages/auth/forgot'
  })
}

// 微信登录
const handleWechatLogin = () => {
  uni.showToast({
    title: '微信登录开发中',
    icon: 'none'
  })
}

// 短信登录
const handleSmsLogin = () => {
  uni.showToast({
    title: '短信验证登录开发中',
    icon: 'none'
  })
}

// 用户协议
const handleUserAgreement = () => {
  uni.navigateTo({
    url: '/pages/agreement/user'
  })
}

// 隐私政策
const handlePrivacyPolicy = () => {
  uni.navigateTo({
    url: '/pages/agreement/privacy'
  })
}

onMounted(() => {
  console.log('登录注册页面加载完成')
})

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (codeTimer) {
    clearInterval(codeTimer)
  }
})
</script>

<style lang="scss" scoped>
.auth-container {
  min-height: 100vh;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: $padding-small $padding-base;
  box-sizing: border-box;
}

.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: $border-radius-round;
  background: rgba($background-color-white, 0.1);
  
  &.circle-1 {
    width: 400rpx;
    height: 400rpx;
    top: -200rpx;
    right: -100rpx;
  }
  
  &.circle-2 {
    width: 300rpx;
    height: 300rpx;
    bottom: -150rpx;
    left: -150rpx;
  }
  
  &.circle-3 {
    width: 200rpx;
    height: 200rpx;
    top: 30%;
    left: -100rpx;
  }
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($background-color-white, 0.05);
  backdrop-filter: blur(20rpx);
}

.brand-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: $margin-base * 2;
  margin-bottom: $margin-base;
  position: relative;
  z-index: $zindex-normal;
}

.brand-logo {
  width: 120rpx;
  height: 120rpx;
  border-radius: $border-radius-small;
  margin-bottom: $margin-medium;
  box-shadow: $box-shadow-dark;
}

.brand-name {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $background-color-white;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.form-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-medium;
  box-shadow: $box-shadow-heavy;
  position: relative;
  z-index: $zindex-normal;
  margin-top: $margin-base;
  box-sizing: border-box;
  width: 100%;
}

.auth-tabs {
  display: flex;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-lighter;
  margin-bottom: $margin-medium;
  width: 100%;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: $padding-medium 0;
  position: relative;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:active {
    background-color: $background-color-light;
  }
}

.tab-text {
  font-size: $font-size-medium;
  font-weight: $font-weight-medium;
  color: $text-secondary;
  transition: all $transition-duration-fast $transition-timing-function-ease;
}

.tab-item.active .tab-text {
  color: $primary-color;
  font-weight: $font-weight-bold;
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: $primary-color;
  border-radius: 2rpx;
  animation: slideIn $transition-duration $transition-timing-function-ease;
}

@keyframes slideIn {
  from {
    transform: translateX(-50%) scaleX(0);
  }
  to {
    transform: translateX(-50%) scaleX(1);
  }
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: $margin-medium;
  width: 100%;
}

.form-group {
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

.form-label {
  display: block;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  color: $text-regular;
  margin-bottom: $margin-xs;
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-xs;
  width: 100%;
}

.forgot-password {
  font-size: $font-size-small;
  color: $primary-color;
  font-weight: $font-weight-medium;
  
  &:active {
    opacity: 0.8;
  }
}

.input-wrapper {
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

.form-input {
  width: 100%;
  height: $input-height;
  padding: 0 $padding-small;
  border: 1rpx solid $border-color;
  border-radius: $border-radius-small;
  font-size: $font-size-base;
  color: $text-primary;
  background: $background-color-white;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:focus {
    border-color: $primary-color;
    box-shadow: 0 0 0 2rpx $primary-color-transparent;
  }
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

.code-input-wrapper {
  display: flex;
  gap: $margin-small;
  width: 100%;
  box-sizing: border-box;
}

.code-input {
  flex: 1;
}

.btn-code {
  height: $input-height;
  padding: 0 $padding-medium;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  border-radius: $border-radius-small;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  flex-shrink: 0;
  white-space: nowrap;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:active:not(:disabled) {
    background: $primary-color-active;
    transform: translateY(2rpx);
  }
  
  &:disabled {
    background: $disabled-bg-color;
    color: $disabled-text-color;
    cursor: not-allowed;
  }
}

.password-toggle {
  position: absolute;
  right: $padding-small;
  top: 50%;
  transform: translateY(-50%);
  width: $icon-size-medium;
  height: $icon-size-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: $zindex-normal;
  
  &:active {
    opacity: 0.7;
  }
}

.toggle-icon {
  font-size: $icon-size-base;
  color: $text-placeholder;
}

.error-message {
  font-size: $font-size-extra-small;
  color: $danger-color;
  margin-top: $margin-xs;
  animation: fadeIn $transition-duration-fast $transition-timing-function-ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.password-strength {
  height: 6rpx;
  background: $background-color-light;
  border-radius: 3rpx;
  margin-top: $margin-xs;
  position: relative;
  overflow: hidden;
  width: 100%;
  
  &.strength-weak .strength-bar {
    background: $danger-color;
  }
  
  &.strength-medium .strength-bar {
    background: $warning-color;
  }
  
  &.strength-strong .strength-bar {
    background: $success-color;
  }
}

.strength-bar {
  height: 100%;
  border-radius: 3rpx;
  transition: all $transition-duration $transition-timing-function-ease;
}

.strength-text {
  position: absolute;
  right: 0;
  top: -18rpx;
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.agreement-group {
  display: flex;
  align-items: flex-start;
  gap: $margin-xs;
  margin: $margin-small 0;
  width: 100%;
  flex-wrap: wrap;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid $border-color;
  border-radius: $border-radius-mini;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:active {
    border-color: $primary-color;
  }
}

.checkbox-icon {
  font-size: $font-size-small;
  color: transparent;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  
  &.checked {
    color: $primary-color;
  }
}

.agreement-text {
  flex: 1;
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: $line-height-base;
  min-width: 200rpx;
}

.agreement-link {
  color: $primary-color;
  font-weight: $font-weight-medium;
  
  &:active {
    opacity: 0.8;
  }
}

.btn-auth {
  width: 100%;
  height: $button-height;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  border-radius: $border-radius-small;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  margin-top: $margin-medium;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:active:not(:disabled) {
    background: $primary-color-active;
    transform: translateY(2rpx);
  }
  
  &:disabled {
    background: $disabled-bg-color;
    color: $disabled-text-color;
    cursor: not-allowed;
  }
  
  &--loading {
    opacity: 0.8;
  }
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 3rpx solid rgba($background-color-white, 0.3);
  border-top-color: $background-color-white;
  border-radius: $border-radius-round;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.other-login {
  margin-top: $margin-base;
  width: 100%;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: $margin-medium;
  width: 100%;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background: $border-color-lighter;
}

.divider-text {
  padding: 0 $padding-small;
  font-size: $font-size-small;
  color: $text-placeholder;
  white-space: nowrap;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: $margin-base;
  width: 100%;
  flex-wrap: wrap;
}

.login-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: transparent;
  border: none;
  padding: $padding-mini;
  min-width: 120rpx;
  transition: all $transition-duration-fast $transition-timing-function-ease;
  box-sizing: border-box;
  
  &:active {
    transform: scale(0.95);
    opacity: 0.8;
  }
}

.method-icon {
  font-size: $icon-size-extra-large;
  margin-bottom: $margin-xs;
}

.method-text {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.auth-footer {
  text-align: center;
  margin-top: $margin-medium;
  padding-top: $padding-medium;
  border-top: 1rpx solid $border-color-extra-light;
  width: 100%;
}

.footer-text {
  font-size: $font-size-small;
  color: $text-secondary;
}

.footer-link {
  color: $primary-color;
  font-weight: $font-weight-medium;
  margin-left: $margin-xs;
  
  &:active {
    opacity: 0.8;
  }
}

/* 响应式优化 - 修复输入框超出问题 */
@media (max-width: 480rpx) {
  .auth-container {
    padding: $padding-xs $padding-small;
  }
  
  .form-card {
    padding: $padding-small;
  }
  
  .brand-logo {
    width: 100rpx;
    height: 100rpx;
  }
  
  .brand-name {
    font-size: $font-size-large;
  }
  
  .code-input-wrapper {
    flex-direction: column;
    gap: $margin-xs;
  }
  
  .btn-code {
    width: 100%;
    margin-top: 0;
  }
  
  .login-methods {
    flex-direction: row;
    justify-content: space-around;
  }
  
  .login-method {
    min-width: 140rpx;
  }
  
  .agreement-group {
    flex-direction: row;
    align-items: center;
  }
  
  .agreement-text {
    font-size: $font-size-extra-small;
  }
}

/* 修复输入框在iOS上的问题 */
input {
  -webkit-appearance: none;
  appearance: none;
  border-radius: $border-radius-small;
}

/* 确保所有元素不会超出容器 */
* {
  max-width: 100%;
  box-sizing: border-box;
}

/* 针对验证码输入框的特殊处理 */
.code-input .form-input {
  width: 100%;
}

/* 平板优化 */
@media (min-width: 768rpx) {
  .auth-container {
    justify-content: center;
    padding: $padding-base * 2;
  }
  
  .form-card {
    max-width: 600rpx;
    margin: 0 auto;
    padding: $padding-base;
  }
  
  .brand-header {
    margin-bottom: $margin-base * 2;
  }
}
</style>