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
              v-model="loginForm.username"
              class="form-input"
              type="text"
              placeholder="请输入手机号或邮箱"
              placeholder-class="placeholder"
              @focus="handleInputFocus('username')"
              @blur="handleInputBlur('username')"
            />
          </view>
          <view v-if="loginError.username" class="error-message">
            {{ loginError.username }}
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
              <text class="toggle-icon">{{ showLoginPassword ? "👁️" : "👁️‍🗨️" }}</text>
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
              <text class="method-text">微信登录</text>
            </button>
            <button class="login-method" @click="handleSmsLogin">
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
              {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : "获取验证码" }}
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
              <text class="toggle-icon">{{ showRegisterPassword ? "👁️" : "👁️‍🗨️" }}</text>
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
              <text class="toggle-icon">{{ showConfirmPassword ? "👁️" : "👁️‍🗨️" }}</text>
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
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { useConfigStore } from "@/stores/config";
import { LoginForm, RegisterForm } from "@/types/user";
import { useUserStore } from "@/stores/user";

//初始配置
const configStore = useConfigStore();
const userStore = useUserStore();
const activeTab = ref("login");
const systemName = configStore.getConfigValue("system.name");

// 登录表单
const loginForm = reactive<LoginForm>({
  username: "",
  password: "",
});

const loginError = reactive({
  username: "",
  password: "",
});

const showLoginPassword = ref(false);
const loginLoading = ref(false);

// 注册表单
const registerForm = reactive<RegisterForm>({
  phone: "",
  code: "",
  password: "",
  confirmPassword: "",
});

const registerError = reactive({
  phone: "",
  code: "",
  password: "",
  confirmPassword: "",
});

const showRegisterPassword = ref(false);
const showConfirmPassword = ref(false);
const registerLoading = ref(false);

// 验证码倒计时
const codeCountdown = ref(0);
let codeTimer: any = null;

// 协议同意
const agreed = ref(false);

// 密码强度计算
const passwordStrength = computed(() => {
  const password = registerForm.password;
  if (!password) return 0;

  let strength = 0;
  if (password.length >= 6) strength++;
  if (/[a-z]/.test(password)) strength++;
  if (/[A-Z]/.test(password)) strength++;
  if (/[0-9]/.test(password)) strength++;
  if (/[^a-zA-Z0-9]/.test(password)) strength++;

  return Math.min(strength, 5);
});

const strengthWidth = computed(() => {
  return `${passwordStrength.value * 20}%`;
});

const strengthText = computed(() => {
  const strength = passwordStrength.value;
  if (strength === 0) return "请设置密码";
  if (strength <= 2) return "弱";
  if (strength <= 3) return "中";
  return "强";
});

const strengthClass = computed(() => {
  const strength = passwordStrength.value;
  if (strength <= 2) return "strength-weak";
  if (strength <= 3) return "strength-medium";
  return "strength-strong";
});

// 切换选项卡
const switchTab = (tab: string) => {
  activeTab.value = tab;
  clearFormErrors();
};

// 清除表单错误
const clearFormErrors = () => {
  if (activeTab.value === "login") {
    loginError.username = "";
    loginError.password = "";
  } else {
    registerError.phone = "";
    registerError.code = "";
    registerError.password = "";
    registerError.confirmPassword = "";
  }
};

// 输入框聚焦/失焦处理
const handleInputFocus = (field: string) => {
  clearFormErrors();
};

const handleInputBlur = (field: string) => {
  validateField(field);
};

// 字段验证
const validateField = (field: string) => {
  if (activeTab.value === "login") {
    switch (field) {
      case "username":
        if (!loginForm.username.trim()) {
          loginError.username = "请输入手机号或邮箱";
          // } else if (!/^1[3-9]\d{9}$/.test(loginForm.username) &&
          //   !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.username)) {
          //   loginError.username = "请输入正确的手机号或邮箱";
        }
        break;
      case "password":
        if (!loginForm.password) {
          loginError.password = "请输入密码";
        } else if (loginForm.password.length < 6) {
          loginError.password = "密码长度不能少于6位";
        }
        break;
    }
  } else {
    switch (field) {
      case "phone":
        if (!registerForm.phone) {
          registerError.phone = "请输入手机号";
        } else if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
          registerError.phone = "请输入正确的手机号";
        }
        break;
      case "code":
        if (!registerForm.code) {
          registerError.code = "请输入验证码";
        } else if (!/^\d{6}$/.test(registerForm.code)) {
          registerError.code = "验证码为6位数字";
        }
        break;
      case "password":
        if (!registerForm.password) {
          registerError.password = "请输入密码";
        } else if (registerForm.password.length < 6) {
          registerError.password = "密码长度不能少于6位";
        }
        break;
      case "confirmPassword":
        if (!registerForm.confirmPassword) {
          registerError.confirmPassword = "请确认密码";
        } else if (registerForm.password !== registerForm.confirmPassword) {
          registerError.confirmPassword = "两次输入的密码不一致";
        }
        break;
    }
  }
};

// 登录
// 登录处理函数 - 完善版
const handleLogin = async () => {
  // 验证所有字段
  validateField("username");
  validateField("password");

  // 如果有错误，不提交
  if (loginError.username || loginError.password) {
    uni.showToast({
      title: "请填写正确的登录信息",
      icon: "none",
    });
    return;
  }

  loginLoading.value = true;

  try {
    // 调用登录API
    await userStore.login(loginForm);
    uni.showToast({
      title: "登录成功",
      icon: "success",
    });

    // 跳转到首页
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/index/index",
      });
    }, 1500);
  } finally {
    loginLoading.value = false;
  }
};

// 注册
const handleRegister = () => {
  // 验证所有字段
  validateField("phone");
  validateField("code");
  validateField("password");
  validateField("confirmPassword");

  // 检查协议
  if (!agreed.value) {
    uni.showToast({
      title: "请阅读并同意用户协议",
      icon: "none",
    });
    return;
  }

  // 如果有错误，不提交
  if (registerError.phone || registerError.code ||
    registerError.password || registerError.confirmPassword) {
    return;
  }

  registerLoading.value = true;

  // 模拟注册请求
  setTimeout(() => {
    registerLoading.value = false;
    uni.showToast({
      title: "注册成功",
      icon: "success",
    });

    // 切换到登录页
    setTimeout(() => {
      activeTab.value = "login";
      // 清空注册表单
      registerForm.phone = "";
      registerForm.code = "";
      registerForm.password = "";
      registerForm.confirmPassword = "";
    }, 1500);
  }, 2000);
};

// 发送验证码
const handleSendCode = () => {
  if (!registerForm.phone || registerError.phone) {
    validateField("phone");
    if (registerError.phone) return;
  }

  // 开始倒计时
  codeCountdown.value = 60;
  uni.showToast({
    title: "验证码已发送",
    icon: "success",
  });

  codeTimer = setInterval(() => {
    if (codeCountdown.value > 0) {
      codeCountdown.value--;
    } else {
      clearInterval(codeTimer);
    }
  }, 1000);
};

// 忘记密码
const handleForgotPassword = () => {
  uni.navigateTo({
    url: "/pages/auth/forgot",
  });
};

// 微信登录
const handleWechatLogin = () => {
  uni.showToast({
    title: "微信登录开发中",
    icon: "none",
  });
};

// 短信登录
const handleSmsLogin = () => {
  uni.showToast({
    title: "短信验证登录开发中",
    icon: "none",
  });
};

// 用户协议
const handleUserAgreement = () => {
  uni.navigateTo({
    url: "/pages/agreement/user",
  });
};

// 隐私政策
const handlePrivacyPolicy = () => {
  uni.navigateTo({
    url: "/pages/agreement/privacy",
  });
};

onMounted(() => {
  console.log("登录注册页面加载完成");
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (codeTimer) {
    clearInterval(codeTimer);
  }
});
</script>

<style lang="scss" scoped>
.auth-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-lighter 0%, $background-color 100%);
  padding: $padding-base;
  overflow: hidden;
}

/* 背景装饰 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: $z-index-base;
}

.decoration-circle {
  position: absolute;
  border-radius: $border-radius-round;
  background: radial-gradient(circle, rgba($primary-color, 0.1) 0%, transparent 70%);

  &.circle-1 {
    width: 400rpx;
    height: 400rpx;
    top: -100rpx;
    right: -100rpx;
  }

  &.circle-2 {
    width: 300rpx;
    height: 300rpx;
    bottom: 20%;
    left: -100rpx;
  }

  &.circle-3 {
    width: 200rpx;
    height: 200rpx;
    top: 30%;
    left: 10%;
  }
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($background-color-white, 0.8);
}

/* 品牌头部 */
.brand-header {
  position: relative;
  z-index: $z-index-base + 1;
  @extend .flex-center;
  flex-direction: column;
  margin-bottom: $margin-base * 2;
  padding-top: $navigation-bar-height;
}

.brand-logo {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: $margin-base;
}

.brand-name {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  letter-spacing: 2rpx;
}

/* 表单卡片 */
.form-card {
  position: relative;
  z-index: $z-index-base + 1;
  background: $background-color-white;
  border-radius: $border-radius * 2;
  padding: $padding-base;
  box-shadow: $box-shadow-dark;
  margin-top: $margin-base;
}

/* 切换选项卡 */
.auth-tabs {
  display: flex;
  margin-bottom: $margin-base * 1.5;
  border-bottom: 2rpx solid $border-color-light;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: $padding-small 0;
  position: relative;
  cursor: pointer;

  &.active {
    .tab-text {
      color: $primary-color;
      font-weight: $font-weight-bold;
    }
  }
}

.tab-text {
  font-size: $font-size-medium;
  color: $text-secondary;
  transition: color $transition-fast;
}

.tab-indicator {
  position: absolute;
  bottom: -2rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 80rpx;
  height: 4rpx;
  background: $primary-color;
  border-radius: 2rpx;
}

/* 表单样式 */
.auth-form {
  .form-group {
    margin-bottom: $margin-base;
  }

  .form-label {
    display: block;
    font-size: $font-size-base;
    color: $text-regular;
    margin-bottom: $margin-mini;
    font-weight: $font-weight-medium;
  }

  .label-row {
    @extend .flex-between;
    margin-bottom: $margin-mini;
  }

  .forgot-password {
    font-size: $font-size-small;
    color: $primary-color;
    text-decoration: none;
  }
}

.input-wrapper {
  position: relative;
  background: $background-color;
  border-radius: $input-border-radius;
  border: 2rpx solid $border-color-light;
  transition: all $transition-fast;

  &:focus-within {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
    background: $background-color-white;
  }

  &.has-error {
    border-color: $error-border-color;
    box-shadow: $input-error-shadow;
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

.placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

/* 验证码输入区域 */
.code-input-wrapper {
  display: flex;
  gap: $margin-small;

  .code-input {
    flex: 1;
  }
}

.btn-code {
  min-width: 200rpx;
  height: $input-height;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  border-radius: $input-border-radius;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  white-space: nowrap;

  &:disabled {
    background: $text-placeholder;
    opacity: $button-disabled-opacity;
  }
}

/* 密码强度指示器 */
.password-strength {
  margin-top: $margin-mini;
  height: 6rpx;
  background: $border-color-lighter;
  border-radius: 3rpx;
  overflow: hidden;
  position: relative;

  .strength-bar {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    transition: width $transition-normal;

    .strength-weak & {
      background: $danger-color;
    }

    .strength-medium & {
      background: $warning-color;
    }

    .strength-strong & {
      background: $success-color;
    }
  }

  .strength-text {
    position: absolute;
    top: 8rpx;
    right: 0;
    font-size: $font-size-extra-small;
    color: $text-secondary;
  }
}

/* 密码切换按钮 */
.password-toggle {
  position: absolute;
  right: $padding-small;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;

  .toggle-icon {
    font-size: $font-size-medium;
    opacity: 0.6;

    &:hover {
      opacity: 1;
    }
  }
}

/* 错误消息 */
.error-message {
  color: $danger-color;
  font-size: $font-size-small;
  margin-top: calc($margin-mini / 2);
  min-height: 20rpx;
}

/* 按钮样式 */
.btn-auth {
  width: 100%;
  height: $button-height;
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);
  color: $background-color-white;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  margin-top: $margin-base;
  transition: all $transition-normal;

  &:active {
    transform: translateY(2rpx);
    box-shadow: $button-active-shadow;
  }

  &:disabled {
    opacity: $button-disabled-opacity;
    transform: none;
    box-shadow: none;
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
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 协议同意 */
.agreement-group {
  display: flex;
  align-items: center;
  gap: $margin-mini;
  margin: $margin-base 0;
  padding: $padding-mini 0;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $border-color;
  border-radius: $border-radius-small;
  @extend .flex-center;
  cursor: pointer;
  flex-shrink: 0;
  transition: all $transition-fast;

  &.checked {
    background: $primary-color;
    border-color: $primary-color;

    .checkbox-icon {
      color: $background-color-white;
      font-weight: $font-weight-bold;
    }
  }
}

.checkbox-icon {
  font-size: $font-size-small;
  color: transparent;
  transition: color $transition-fast;
}

.agreement-text {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.5;
}

.agreement-link {
  color: $primary-color;
  text-decoration: none;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

/* 其他登录方式 */
.other-login {
  margin-top: $margin-base * 1.5;
  padding-top: $margin-base;
  border-top: 2rpx solid $border-color-lighter;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: $margin-base;

  .divider-line {
    flex: 1;
    height: 1rpx;
    background: $border-color;
  }

  .divider-text {
    padding: 0 $padding-small;
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: $margin-base * 2;
}

.login-method {
  @extend .flex-center;
  flex-direction: column;
  background: transparent;
  border: none;
  padding: $padding-mini;
  cursor: pointer;
  transition: transform $transition-fast;

  &:active {
    transform: scale(0.95);
  }

  .method-icon {
    font-size: $font-size-extra-large;
    margin-bottom: $margin-mini;
  }

  .method-text {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

/* 底部链接 */
.auth-footer {
  text-align: center;
  margin-top: $margin-base;
  padding-top: $margin-base;
  border-top: 1rpx solid $border-color-extra-light;

  .footer-text {
    font-size: $font-size-base;
    color: $text-secondary;
  }

  .footer-link {
    color: $primary-color;
    font-weight: $font-weight-medium;
    margin-left: $margin-mini;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* 响应式调整 */
@media (max-width: 375px) {
  .form-card {
    padding: $padding-small;
  }

  .login-methods {
    gap: $margin-base;
  }
}
</style>