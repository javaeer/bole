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

    <!-- 主表单卡片 -->
    <view class="form-card">
      <!-- 页面标题 -->
      <view class="page-header">
        <text class="page-title">忘记密码</text>
        <text class="page-subtitle">请按照以下步骤重置您的密码</text>
      </view>

      <!-- 重置步骤指示器 -->
      <view class="steps-indicator">
        <view class="step-item" :class="{ active: currentStep === 1, completed: currentStep > 1 }">
          <view class="step-number">1</view>
          <text class="step-text">验证身份</text>
        </view>
        <view class="step-line"></view>
        <view class="step-item" :class="{ active: currentStep === 2, completed: currentStep > 2 }">
          <view class="step-number">2</view>
          <text class="step-text">重置密码</text>
        </view>
        <view class="step-line"></view>
        <view class="step-item" :class="{ active: currentStep === 3, completed: currentStep > 3 }">
          <view class="step-number">3</view>
          <text class="step-text">完成</text>
        </view>
      </view>

      <!-- 步骤1：验证身份 -->
      <view v-if="currentStep === 1" class="step-container">
        <view class="form-group">
          <text class="form-label">选择验证方式</text>
          <view class="verify-type-selector">
            <view
              class="verify-type-item"
              :class="{ active: verifyType === 'phone' }"
              @click="verifyType = 'phone'"
            >
              <text class="verify-type-icon">📱</text>
              <text class="verify-type-text">手机验证</text>
            </view>
            <view
              class="verify-type-item"
              :class="{ active: verifyType === 'email' }"
              @click="verifyType = 'email'"
            >
              <text class="verify-type-icon">📧</text>
              <text class="verify-type-text">邮箱验证</text>
            </view>
          </view>
        </view>

        <!-- 手机验证表单 -->
        <view v-if="verifyType === 'phone'" class="verify-form-section">
          <view class="form-group">
            <text class="form-label">手机号</text>
            <view class="input-wrapper">
              <input
                v-model="forgotForm.phone"
                class="form-input"
                type="number"
                placeholder="请输入注册时使用的手机号"
                placeholder-class="placeholder"
                maxlength="11"
                @focus="handleInputFocus('phone')"
                @blur="handleInputBlur('phone')"
              />
            </view>
            <view v-if="formError.phone" class="error-message">
              {{ formError.phone }}
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">验证码</text>
            <view class="code-input-wrapper">
              <view class="input-wrapper code-input">
                <input
                  v-model="forgotForm.code"
                  class="form-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                  @focus="handleInputFocus('code')"
                  @blur="handleInputBlur('code')"
                />
              </view>
              <view class="code-btn-container">
                <button
                  class="btn-code"
                  :class="{ 
                    'btn-code--disabled': codeCountdown > 0 || !canSendCode,
                    'btn-code--counting': codeCountdown > 0
                  }"
                  :disabled="codeCountdown > 0 || !canSendCode"
                  @click="handleSendCode"
                  hover-class="btn-code--hover"
                >
                  <view v-if="codeCountdown > 0" class="code-content">
                    <text class="code-count">{{ codeCountdown }}</text>
                    <text class="code-unit">s</text>
                  </view>
                  <text v-else class="code-text">获取验证码</text>
                </button>
              </view>
            </view>
            <view v-if="formError.code" class="error-message">
              {{ formError.code }}
            </view>
          </view>
        </view>

        <!-- 邮箱验证表单 -->
        <view v-if="verifyType === 'email'" class="verify-form-section">
          <view class="form-group">
            <text class="form-label">邮箱地址</text>
            <view class="input-wrapper">
              <input
                v-model="forgotForm.email"
                class="form-input"
                type="text"
                placeholder="请输入注册时使用的邮箱地址"
                placeholder-class="placeholder"
                @focus="handleInputFocus('email')"
                @blur="handleInputBlur('email')"
              />
            </view>
            <view v-if="formError.email" class="error-message">
              {{ formError.email }}
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">邮箱验证码</text>
            <view class="code-input-wrapper">
              <view class="input-wrapper code-input">
                <input
                  v-model="forgotForm.code"
                  class="form-input"
                  type="number"
                  placeholder="请输入邮箱验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                  @focus="handleInputFocus('code')"
                  @blur="handleInputBlur('code')"
                />
              </view>
              <view class="code-btn-container">
                <button
                  class="btn-code"
                  :class="{ 
                    'btn-code--disabled': emailCodeCountdown > 0 || !canSendEmailCode,
                    'btn-code--counting': emailCodeCountdown > 0
                  }"
                  :disabled="emailCodeCountdown > 0 || !canSendEmailCode"
                  @click="handleSendEmailCode"
                  hover-class="btn-code--hover"
                >
                  <view v-if="emailCodeCountdown > 0" class="code-content">
                    <text class="code-count">{{ emailCodeCountdown }}</text>
                    <text class="code-unit">s</text>
                  </view>
                  <text v-else class="code-text">获取验证码</text>
                </button>
              </view>
            </view>
            <view v-if="formError.code" class="error-message">
              {{ formError.code }}
            </view>
          </view>
        </view>

        <button
          class="btn-auth"
          :class="{ 'btn-auth--loading': verifyLoading }"
          :disabled="verifyLoading || !canVerify"
          @click="handleVerifyIdentity"
        >
          <text v-if="!verifyLoading">下一步</text>
          <view v-else class="loading-spinner"></view>
        </button>
      </view>

      <!-- 步骤2：重置密码 -->
      <view v-if="currentStep === 2" class="step-container">
        <view class="form-group">
          <text class="form-label">新密码</text>
          <view class="input-wrapper">
            <input
              v-model="passwordForm.newPassword"
              class="form-input"
              :type="showNewPassword ? 'text' : 'password'"
              placeholder="6-20位字母、数字或符号"
              placeholder-class="placeholder"
              @focus="handleInputFocus('newPassword')"
              @blur="handleInputBlur('newPassword')"
            />
            <view class="password-toggle" @click="showNewPassword = !showNewPassword">
              <text class="toggle-icon">{{ showNewPassword ? "👁️" : "👁️‍🗨️" }}</text>
            </view>
          </view>
          <view class="password-strength" :class="strengthClass">
            <view class="strength-bar" :style="{ width: strengthWidth }"></view>
            <text class="strength-text">{{ strengthText }}</text>
          </view>
          <view v-if="formError.newPassword" class="error-message">
            {{ formError.newPassword }}
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">确认新密码</text>
          <view class="input-wrapper">
            <input
              v-model="passwordForm.confirmPassword"
              class="form-input"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="请再次输入新密码"
              placeholder-class="placeholder"
              @focus="handleInputFocus('confirmPassword')"
              @blur="handleInputBlur('confirmPassword')"
            />
            <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <text class="toggle-icon">{{ showConfirmPassword ? "👁️" : "👁️‍🗨️" }}</text>
            </view>
          </view>
          <view v-if="formError.confirmPassword" class="error-message">
            {{ formError.confirmPassword }}
          </view>
        </view>

        <view class="password-tips">
          <text class="tips-title">密码安全提示：</text>
          <view class="tips-list">
            <text class="tip-item">• 密码长度至少6位</text>
            <text class="tip-item">• 建议包含字母、数字和特殊字符</text>
            <text class="tip-item">• 不要使用过于简单的密码</text>
            <text class="tip-item">• 不要使用个人信息作为密码</text>
          </view>
        </view>

        <button
          class="btn-auth"
          :class="{ 'btn-auth--loading': resetLoading }"
          :disabled="resetLoading || !canReset"
          @click="handleResetPassword"
        >
          <text v-if="!resetLoading">重置密码</text>
          <view v-else class="loading-spinner"></view>
        </button>
      </view>

      <!-- 步骤3：完成 -->
      <view v-if="currentStep === 3" class="step-container">
        <view class="success-container">
          <view class="success-icon">
            <text class="icon-text">✓</text>
          </view>
          <text class="success-title">密码重置成功！</text>
          <text class="success-message">您的密码已经成功重置，请牢记您的新密码</text>

          <view class="success-timer">
            <text class="timer-text">{{ countdown }}秒后自动跳转到首页</text>
          </view>

          <button class="btn-secondary" @click="handleBackToHome">
            返回首页
          </button>
        </view>
      </view>

      <!-- 底部链接 -->
      <view class="auth-footer">
        <text class="footer-text">
          想起密码了？
          <text class="footer-link" @click="handleGoToLogin">立即登录</text>
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { useConfigStore } from "@/stores/config";
import { useUserStore } from "@/stores/user";
import CodeAPI from "@/api/code";
import { EmailForm } from "@/types/code";
import { ResetPasswordForm } from "@/types/user";

// 初始配置
const configStore = useConfigStore();
const userStore = useUserStore();
const systemName = configStore.getConfigValue("system.name");

// 当前步骤
const currentStep = ref(1);

// 验证类型
const verifyType = ref<"phone" | "email">("phone");

// 忘记密码表单
const forgotForm = reactive({
  phone: "",
  email: "",
  code: "",
});

const passwordForm = reactive({
  newPassword: "",
  confirmPassword: "",
});

const formError = reactive({
  phone: "",
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
});

// 显示/隐藏密码
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

// 加载状态
const verifyLoading = ref(false);
const resetLoading = ref(false);

// 验证码倒计时
const codeCountdown = ref(0);
const emailCodeCountdown = ref(0);
let codeTimer: any = null;
let emailCodeTimer: any = null;

// 成功页倒计时
const countdown = ref(5);
let successTimer: any = null;

// 计算属性
const canSendCode = computed(() => {
  return forgotForm.phone && /^1[3-9]\d{9}$/.test(forgotForm.phone);
});

const canSendEmailCode = computed(() => {
  return forgotForm.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(forgotForm.email);
});

const canVerify = computed(() => {
  if (verifyType.value === "phone") {
    return forgotForm.phone && forgotForm.code;
  } else {
    return forgotForm.email && forgotForm.code;
  }
});

const canReset = computed(() => {
  return passwordForm.newPassword && passwordForm.confirmPassword;
});

// 密码强度计算
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword;
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

// 字段验证
const validateField = (field: string) => {
  switch (field) {
    case "phone":
      if (!forgotForm.phone) {
        formError.phone = "请输入手机号";
      } else if (!/^1[3-9]\d{9}$/.test(forgotForm.phone)) {
        formError.phone = "请输入正确的手机号";
      } else {
        formError.phone = "";
      }
      break;
    case "email":
      if (!forgotForm.email) {
        formError.email = "请输入邮箱地址";
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(forgotForm.email)) {
        formError.email = "请输入正确的邮箱地址";
      } else {
        formError.email = "";
      }
      break;
    case "code":
      if (!forgotForm.code) {
        formError.code = "请输入验证码";
      } else if (!/^\d{6}$/.test(forgotForm.code)) {
        formError.code = "验证码为6位数字";
      } else {
        formError.code = "";
      }
      break;
    case "newPassword":
      if (!passwordForm.newPassword) {
        formError.newPassword = "请输入新密码";
      } else if (passwordForm.newPassword.length < 6) {
        formError.newPassword = "密码长度不能少于6位";
      } else if (!/^(?=.*[a-zA-Z])(?=.*\d)/.test(passwordForm.newPassword)) {
        formError.newPassword = "密码需包含字母和数字";
      } else {
        formError.newPassword = "";
      }
      break;
    case "confirmPassword":
      if (!passwordForm.confirmPassword) {
        formError.confirmPassword = "请确认密码";
      } else if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        formError.confirmPassword = "两次输入的密码不一致";
      } else {
        formError.confirmPassword = "";
      }
      break;
  }
};

// 输入框聚焦/失焦处理
const handleInputFocus = (field: string) => {
  formError[field as keyof typeof formError] = "";
};

const handleInputBlur = (field: string) => {
  validateField(field);
};

// 发送手机验证码
const handleSendCode = async () => {
  if (!forgotForm.phone || formError.phone) {
    validateField("phone");
    if (formError.phone) return;
  }

  try {
    // 构建短信发送表单
    const smsForm = {
      phone: forgotForm.phone,
      templateId: 3,
    };

    // 调用短信发送接口
    await CodeAPI.sendSms(smsForm);

    // 开始倒计时
    codeCountdown.value = 60;
    uni.showToast({
      title: "验证码已发送到手机",
      icon: "success",
    });

    codeTimer = setInterval(() => {
      if (codeCountdown.value > 0) {
        codeCountdown.value--;
      } else {
        clearInterval(codeTimer);
      }
    }, 1000);
  } catch (error: any) {
    console.error("发送验证码失败:", error);

    let errorMsg = "发送验证码失败，请重试";
    if (error.message) {
      errorMsg = error.message;
    } else if (error.data && error.data.message) {
      errorMsg = error.data.message;
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  }
};

// 发送邮箱验证码
const handleSendEmailCode = async () => {
  if (!forgotForm.email || formError.email) {
    validateField("email");
    if (formError.email) return;
  }

  try {
    // 构建邮件发送参数
    const emailData: EmailForm = {
      email: forgotForm.email,
      templateId: 3,
    };

    // 调用邮箱验证码发送接口
    await CodeAPI.sendEmail(emailData);

    // 模拟发送成功
    await new Promise(resolve => setTimeout(resolve, 500));

    // 开始倒计时
    emailCodeCountdown.value = 60;
    uni.showToast({
      title: "验证码已发送到邮箱",
      icon: "success",
    });

    emailCodeTimer = setInterval(() => {
      if (emailCodeCountdown.value > 0) {
        emailCodeCountdown.value--;
      } else {
        clearInterval(emailCodeTimer);
      }
    }, 1000);
  } catch (error: any) {
    console.error("发送邮箱验证码失败:", error);

    let errorMsg = "发送验证码失败，请重试";
    if (error.message) {
      errorMsg = error.message;
    } else if (error.data && error.data.message) {
      errorMsg = error.data.message;
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  }
};

// 验证身份
const handleVerifyIdentity = async () => {
  // 验证字段
  if (verifyType.value === "phone") {
    validateField("phone");
    validateField("code");

    if (formError.phone || formError.code) {
      uni.showToast({
        title: "请填写正确的验证信息",
        icon: "none",
      });
      return;
    }
  } else {
    validateField("email");
    validateField("code");

    if (formError.email || formError.code) {
      uni.showToast({
        title: "请填写正确的验证信息",
        icon: "none",
      });
      return;
    }
  }

  verifyLoading.value = true;

  try {
    // 模拟验证请求
    await new Promise(resolve => setTimeout(resolve, 1500));

    // 验证成功，进入下一步
    currentStep.value = 2;

    uni.showToast({
      title: "身份验证成功",
      icon: "success",
    });
  } catch (error: any) {
    console.error("身份验证失败:", error);

    let errorMsg = "身份验证失败，请重试";
    if (error.data && error.data.message) {
      errorMsg = error.data.message;
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  } finally {
    verifyLoading.value = false;
  }
};

// 重置密码
const handleResetPassword = async () => {
  // 验证字段
  validateField("newPassword");
  validateField("confirmPassword");

  if (formError.newPassword || formError.confirmPassword) {
    uni.showToast({
      title: "请填写正确的密码信息",
      icon: "none",
    });
    return;
  }

  resetLoading.value = true;

  try {
    // 调用重置密码API
    const resetData: ResetPasswordForm = {
      type: verifyType.value,
      username: verifyType.value === "phone" ? forgotForm.phone : forgotForm.email,
      code: forgotForm.code,
      newPassword: passwordForm.newPassword,
    };
    await userStore.resetPassword(resetData);

    // 重置成功，进入下一步
    currentStep.value = 3;

    uni.showToast({
      title: "密码重置成功",
      icon: "success",
    });

    // 开始成功页倒计时
    startSuccessCountdown();
  } catch (error: any) {
    console.error("密码重置失败:", error);

    let errorMsg = "密码重置失败，请重试";
    if (error.data && error.data.message) {
      errorMsg = error.data.message;
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  } finally {
    resetLoading.value = false;
  }
};

// 开始成功页倒计时
const startSuccessCountdown = () => {
  countdown.value = 5;

  successTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(successTimer);
      uni.switchTab({
        url: "/pages/index/index",
      });
    }
  }, 1000);
};

// 跳转到登录页
const handleGoToLogin = () => {
  uni.redirectTo({
    url: "/pages/auth/auth",
  });
};

// 返回首页
const handleBackToHome = () => {
  uni.switchTab({
    url: "/pages/index/index",
  });
};

onMounted(() => {
  console.log("忘记密码页面加载完成");
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (codeTimer) {
    clearInterval(codeTimer);
  }
  if (emailCodeTimer) {
    clearInterval(emailCodeTimer);
  }
  if (successTimer) {
    clearInterval(successTimer);
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: $margin-base;
  padding-top: $navigation-bar-height;
  text-align: center;
}

.brand-logo {
  width: 100rpx;
  height: 100rpx;
  margin-bottom: $margin-mini;
  display: block;
}

.brand-name {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  letter-spacing: 2rpx;
  display: block;
  text-align: center;
}

/* 返回按钮 */
.back-button-container {
  position: relative;
  z-index: $z-index-base + 1;
  margin-bottom: $margin-base;
}

.btn-back {
  background: transparent;
  border: none;
  padding: $padding-mini 0;
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: $font-size-base;
  color: $text-secondary;
  cursor: pointer;

  .back-icon {
    font-size: $font-size-medium;
  }

  .back-text {
    font-size: $font-size-base;
  }

  &:active {
    opacity: 0.7;
  }
}

/* 表单卡片 */
.form-card {
  position: relative;
  z-index: $z-index-base + 1;
  background: $background-color-white;
  border-radius: $border-radius * 2;
  padding: $padding-base * 1.5;
  box-shadow: $box-shadow-dark;
  margin-top: $margin-base;
  text-align: left;
}

/* 页面标题 */
.page-header {
  margin-bottom: $margin-base * 1.5;
  text-align: center;
}

.page-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
  text-align: center;
}

.page-subtitle {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  text-align: center;
  line-height: 1.5;
}

/* 步骤指示器 */
.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $margin-base * 2;
  padding: 0 $padding-base;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;

  &.active {
    .step-number {
      background: $primary-color;
      color: $background-color-white;
      border-color: $primary-color;
    }

    .step-text {
      color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }

  &.completed {
    .step-number {
      background: $success-color;
      color: $background-color-white;
      border-color: $success-color;
    }

    .step-text {
      color: $success-color;
    }
  }
}

.step-number {
  width: 40rpx;
  height: 40rpx;
  border-radius: $border-radius-round;
  border: 2rpx solid $border-color;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-small;
  font-weight: $font-weight-bold;
  color: $text-secondary;
  margin-bottom: 8rpx;
  transition: all $transition-fast;
}

.step-text {
  font-size: $font-size-small;
  color: $text-secondary;
  white-space: nowrap;
  transition: color $transition-fast;
}

.step-line {
  flex: 1;
  height: 2rpx;
  background: $border-color;
  margin: 0 $margin-small;
  min-width: 40rpx;

  .step-item.completed + & {
    background: $success-color;
  }
}

/* 步骤容器 */
.step-container {
  animation: fadeIn $transition-normal;
}

/* 验证类型选择器 */
.verify-type-selector {
  display: flex;
  gap: $margin-base;
  margin-bottom: $margin-base;
}

.verify-type-item {
  flex: 1;
  background: $background-color;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius;
  padding: $padding-base;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all $transition-fast;

  &.active {
    background: rgba($primary-color, 0.05);
    border-color: $primary-color;

    .verify-type-text {
      color: $primary-color;
      font-weight: $font-weight-medium;
    }
  }

  &:active:not(.active) {
    background: rgba($text-secondary, 0.05);
  }
}

.verify-type-icon {
  font-size: 48rpx;
  margin-bottom: $margin-mini;
  display: block;
}

.verify-type-text {
  font-size: $font-size-base;
  color: $text-regular;
  text-align: center;
  transition: color $transition-fast;
}

/* 验证表单区域 */
.verify-form-section {
  margin-bottom: $margin-base;
  animation: fadeIn $transition-normal;
}

/* 表单样式 */
.form-group {
  margin-bottom: $margin-base;

  .form-label {
    display: block;
    font-size: $font-size-base;
    color: $text-regular;
    margin-bottom: $margin-mini;
    font-weight: $font-weight-medium;
    text-align: left;
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
  line-height: $input-height;
  display: flex;
  align-items: center;

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
  align-items: stretch;
  gap: 16rpx;
  width: 100%;
}

.input-wrapper.code-input {
  flex: 1;
  min-width: 0;
  border-radius: $input-border-radius;
  overflow: hidden;
  border: 2rpx solid $border-color-light;
  transition: all $transition-fast;

  &:focus-within {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
  }
}

/* 验证码按钮容器 */
.code-btn-container {
  position: relative;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

/* 验证码按钮 */
.btn-code {
  min-width: 200rpx;
  height: 100%;
  min-height: $input-height;
  padding: 0 24rpx;
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -8%) 100%);
  color: $background-color-white;
  border: none;
  border-radius: $input-border-radius;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  white-space: nowrap;
  transition: all $transition-fast;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.2);
  position: relative;
  overflow: hidden;
  line-height: normal;
  text-align: center;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.1) 100%);
    opacity: 0;
    transition: opacity $transition-fast;
  }

  .code-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4rpx;
  }

  .code-text {
    display: block;
    text-align: center;
    width: 100%;
  }

  .code-count {
    font-size: $font-size-base;
    font-weight: $font-weight-bold;
    color: inherit;
  }

  .code-unit {
    font-size: $font-size-extra-small;
    color: inherit;
    opacity: 0.9;
  }
}

/* 修复按钮交互状态 */
.btn-code:active,
.btn-code.btn-code--hover {
  transform: translateY(2rpx);
  box-shadow: 0 2rpx 8rpx rgba($primary-color, 0.15);

  &::before {
    opacity: 1;
  }
}

/* 禁用状态 */
.btn-code:disabled,
.btn-code.btn-code--disabled {
  background: linear-gradient(135deg, $border-color-light 0%, color.adjust($border-color-light, $lightness: -5%) 100%);
  color: $text-placeholder;
  box-shadow: none;
  cursor: not-allowed;

  &:active,
  &.btn-code--hover {
    transform: none;
    box-shadow: none;

    &::before {
      opacity: 0;
    }
  }

  .code-text,
  .code-content {
    opacity: 0.7;
  }
}

/* 倒计时状态 */
.btn-code.btn-code--counting {
  background: linear-gradient(135deg, $warning-color 0%, color.adjust($warning-color, $lightness: -8%) 100%);
  box-shadow: 0 4rpx 12rpx rgba($warning-color, 0.2);

  &:active,
  &.btn-code--hover {
    box-shadow: 0 2rpx 8rpx rgba($warning-color, 0.15);
  }
}

/* 错误消息 */
.error-message {
  color: $danger-color;
  font-size: $font-size-small;
  margin-top: 8rpx;
  min-height: 30rpx;
  line-height: 1.4;
  padding-left: 4rpx;
  animation: fadeIn $transition-fast;
  display: block;
  text-align: left;
}

/* 密码强度指示器 */
.password-strength {
  margin-top: $margin-mini;
  height: 6rpx;
  background: $border-color-lighter;
  border-radius: 3rpx;
  overflow: hidden;
  position: relative;
  width: 100%;

  .strength-bar {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    transition: all $transition-normal ease-out;
  }

  .strength-text {
    position: absolute;
    top: 8rpx;
    right: 0;
    font-size: $font-size-extra-small;
    color: $text-secondary;
  }
}

/* 密码强度颜色 */
.strength-weak .strength-bar {
  background: $danger-color;
}

.strength-medium .strength-bar {
  background: $warning-color;
}

.strength-strong .strength-bar {
  background: $success-color;
}

/* 密码切换按钮 */
.password-toggle {
  position: absolute;
  right: 16rpx;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  z-index: 2;
  padding: 8rpx;
  margin-right: -8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  transition: all $transition-fast;

  &:active {
    background: rgba($text-secondary, 0.1);
  }

  .toggle-icon {
    font-size: 32rpx;
    color: $text-secondary;
    opacity: 0.6;
    transition: opacity $transition-fast;
    display: block;
  }

  &:hover .toggle-icon {
    opacity: 1;
  }
}

/* 密码安全提示 */
.password-tips {
  background: rgba($primary-color, 0.05);
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
  border-left: 4rpx solid $primary-color;
}

.tips-title {
  display: block;
  font-size: $font-size-small;
  font-weight: $font-weight-bold;
  color: $primary-color;
  margin-bottom: $margin-mini;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.tip-item {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.5;
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
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;

  &:active:not(:disabled) {
    transform: translateY(2rpx);
    box-shadow: $button-active-shadow;
  }

  &:disabled {
    background: linear-gradient(135deg, $text-placeholder 0%, color.adjust($text-placeholder, $lightness: -10%) 100%);
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
  animation: spin 0.8s linear infinite;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 成功页面样式 */
.success-container {
  text-align: center;
  padding: $margin-base 0;
  animation: fadeIn $transition-normal;
}

.success-icon {
  width: 80rpx;
  height: 80rpx;
  background: $success-color;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto $margin-base;
  box-shadow: 0 8rpx 20rpx rgba($success-color, 0.2);
}

.icon-text {
  color: $background-color-white;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
}

.success-title {
  display: block;
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.success-message {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
  line-height: 1.5;
  margin-bottom: $margin-base * 1.5;
}

.success-timer {
  margin-bottom: $margin-base * 1.5;
}

.timer-text {
  font-size: $font-size-small;
  color: $text-secondary;
  opacity: 0.8;
}

.btn-secondary {
  width: 100%;
  height: $button-height;
  background: transparent;
  color: $primary-color;
  border: 2rpx solid $primary-color;
  border-radius: $border-radius;
  font-size: $font-size-medium;
  font-weight: $font-weight-medium;
  margin-top: $margin-base;
  transition: all $transition-normal;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;

  &:active {
    background: rgba($primary-color, 0.1);
    transform: translateY(2rpx);
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
    display: block;
    text-align: center;
  }

  .footer-link {
    color: $primary-color;
    font-weight: $font-weight-medium;
    margin-left: 6rpx;
    padding: 4rpx 8rpx;
    border-radius: 4rpx;
    transition: all $transition-fast;
    cursor: pointer;

    &:active {
      background: rgba($primary-color, 0.1);
      transform: translateY(1rpx);
    }
  }
}

/* 动画效果 */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 验证码按钮加载动画 */
@keyframes codeBtnPulse {
  0% {
    box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.2);
  }
  50% {
    box-shadow: 0 4rpx 20rpx rgba($primary-color, 0.3);
  }
  100% {
    box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.2);
  }
}

.btn-code:not(:disabled):not(.btn-code--disabled):not(.btn-code--counting) {
  animation: codeBtnPulse 2s infinite ease-in-out;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .form-card {
    padding: $padding-base;
  }

  .verify-type-selector {
    gap: $margin-small;
  }

  .btn-code {
    min-width: 180rpx;
    padding: 0 16rpx;
    font-size: $font-size-extra-small;

    .code-count {
      font-size: $font-size-small;
    }
  }

  .code-text {
    font-size: $font-size-extra-small;
  }

  .code-input-wrapper {
    gap: 12rpx;
  }

  .step-number {
    width: 36rpx;
    height: 36rpx;
  }

  .step-text {
    font-size: $font-size-extra-small;
  }
}

/* 平板和桌面端适配 */
@media (min-width: 768px) {
  .code-input-wrapper {
    gap: 20rpx;
  }

  .btn-code {
    min-width: 220rpx;
    padding: 0 28rpx;
    font-size: $font-size-base;
    border-radius: $input-border-radius * 1.2;

    .code-text {
      font-size: $font-size-base;
    }
  }

  .verify-type-selector {
    gap: $margin-base * 2;
  }
}

/* 修复所有按钮的基线对齐 */
button {
  vertical-align: middle;
  -webkit-tap-highlight-color: transparent;
  padding: 0;
  background: none;
  border: none;
  font-family: inherit;
  font-size: inherit;
  color: inherit;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &::after {
    border: none;
  }
}

/* 修复所有文本元素垂直居中 */
text {
  display: inline-block;
  vertical-align: middle;
}
</style>