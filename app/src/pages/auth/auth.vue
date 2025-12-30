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

    <!-- 微信小程序环境：只显示微信登录 -->
    <view v-if="isWechatMiniProgram" class="wechat-auth-container">
      <view class="wechat-welcome">
        <text class="welcome-title">欢迎使用{{ systemName }}</text>
        <text class="welcome-subtitle">请使用微信一键登录</text>
      </view>

      <view class="wechat-login-card">
        <view class="wechat-icon-container">
          <view class="wechat-icon">
            <text class="icon-text">WeChat</text>
          </view>
        </view>

        <view class="login-prompt">
          <text class="prompt-text">点击下方按钮快速登录</text>
          <text class="prompt-desc">登录后可使用完整功能</text>
        </view>

        <button
          class="btn-wechat-login"
          :class="{ 'btn-wechat-login--loading': wechatLoginLoading }"
          :disabled="wechatLoginLoading"
          @click="handleWechatLogin"
          open-type="getUserInfo"
          lang="zh_CN"
        >
          <text v-if="!wechatLoginLoading">微信一键登录</text>
          <view v-else class="loading-spinner-white"></view>
        </button>
      </view>

      <!-- 服务条款提示 -->
      <view class="wechat-agreement">
        <text class="agreement-prompt">
          登录即代表您已同意
          <text class="agreement-link" @click="handleUserAgreement">《用户协议》</text>
          和
          <text class="agreement-link" @click="handlePrivacyPolicy">《隐私政策》</text>
        </text>
      </view>
    </view>

    <!-- 非微信小程序环境：显示完整登录注册表单 -->
    <view v-else class="form-card">
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
        <!-- 登录方式选择 -->
        <view class="login-type-selector">
          <view
            class="login-type-item"
            :class="{ active: loginType === 'password' }"
            @click="loginType = 'password'"
          >
            <text class="login-type-text">密码登录</text>
          </view>
          <view
            class="login-type-item"
            :class="{ active: loginType === 'sms' }"
            @click="loginType = 'sms'"
          >
            <text class="login-type-text">短信登录</text>
          </view>
        </view>

        <!-- 密码登录表单 -->
        <view v-if="loginType === 'password'" class="login-form-section">
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
            @click="handlePasswordLogin"
          >
            <text v-if="!loginLoading">登录</text>
            <view v-else class="loading-spinner"></view>
          </button>
        </view>

        <!-- 短信登录表单 -->
        <view v-if="loginType === 'sms'" class="login-form-section">
          <view class="form-group">
            <text class="form-label">手机号</text>
            <view class="input-wrapper">
              <input
                v-model="smsLoginForm.phone"
                class="form-input"
                type="number"
                placeholder="请输入手机号"
                placeholder-class="placeholder"
                maxlength="11"
                @focus="handleInputFocus('smsPhone')"
                @blur="handleInputBlur('smsPhone')"
              />
            </view>
            <view v-if="loginError.smsPhone" class="error-message">
              {{ loginError.smsPhone }}
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">验证码</text>
            <view class="code-input-wrapper">
              <view class="input-wrapper code-input">
                <input
                  v-model="smsLoginForm.code"
                  class="form-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                  @focus="handleInputFocus('smsCode')"
                  @blur="handleInputBlur('smsCode')"
                />
              </view>
              <view class="code-btn-container">
                <button
                  class="btn-code"
                  :class="{
                    'btn-code--disabled': smsCodeCountdown > 0 || !canSendSmsCode,
                    'btn-code--counting': smsCodeCountdown > 0
                  }"
                  :disabled="smsCodeCountdown > 0 || !canSendSmsCode"
                  @click="handleSendSmsCode"
                  hover-class="btn-code--hover"
                >
                  <view v-if="smsCodeCountdown > 0" class="code-content">
                    <text class="code-count">{{ smsCodeCountdown }}</text>
                    <text class="code-unit">s</text>
                  </view>
                  <text v-else class="code-text">获取验证码</text>
                </button>
              </view>
            </view>
            <view v-if="loginError.smsCode" class="error-message">
              {{ loginError.smsCode }}
            </view>
          </view>

          <button
            class="btn-auth"
            :class="{ 'btn-auth--loading': smsLoginLoading }"
            :disabled="smsLoginLoading"
            @click="handleSmsLogin"
          >
            <text v-if="!smsLoginLoading">短信登录</text>
            <view v-else class="loading-spinner"></view>
          </button>
        </view>

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
            <button class="login-method" @click="handleQuickLogin">
              <text class="method-text">快速体验</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 注册表单 -->
      <view v-if="activeTab === 'register'" class="auth-form">
        <!-- 注册方式选择 -->
        <view class="register-type-selector">
          <view
            class="register-type-item"
            :class="{ active: registerType === 'phone' }"
            @click="registerType = 'phone'"
          >
            <text class="register-type-text">手机注册</text>
          </view>
          <view
            class="register-type-item"
            :class="{ active: registerType === 'email' }"
            @click="registerType = 'email'"
          >
            <text class="register-type-text">邮箱注册</text>
          </view>
        </view>

        <!-- 手机注册表单 -->
        <view v-if="registerType === 'phone'" class="register-form-section">
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
              <view class="code-btn-container">
                <button
                  class="btn-code"
                  :class="{
                    'btn-code--disabled': codeCountdown > 0 || !canSendPhoneCode,
                    'btn-code--counting': codeCountdown > 0
                  }"
                  :disabled="codeCountdown > 0 || !canSendPhoneCode"
                  @click="handleSendPhoneCode"
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
            <view v-if="registerError.code" class="error-message">
              {{ registerError.code }}
            </view>
          </view>
        </view>

        <!-- 邮箱注册表单 -->
        <view v-if="registerType === 'email'" class="register-form-section">
          <view class="form-group">
            <text class="form-label">邮箱地址</text>
            <view class="input-wrapper">
              <input
                v-model="registerForm.email"
                class="form-input"
                type="text"
                placeholder="请输入邮箱地址"
                placeholder-class="placeholder"
                @focus="handleInputFocus('email')"
                @blur="handleInputBlur('email')"
              />
            </view>
            <view v-if="registerError.email" class="error-message">
              {{ registerError.email }}
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">邮箱验证码</text>
            <view class="code-input-wrapper">
              <view class="input-wrapper code-input">
                <input
                  v-model="registerForm.emailCode"
                  class="form-input"
                  type="number"
                  placeholder="请输入邮箱验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                  @focus="handleInputFocus('emailCode')"
                  @blur="handleInputBlur('emailCode')"
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
            <view v-if="registerError.emailCode" class="error-message">
              {{ registerError.emailCode }}
            </view>
          </view>
        </view>

        <!-- 密码设置（共用） -->
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

        <!-- 协议同意 -->
        <view class="agreement-group">
          <view class="checkbox-wrapper" @click.stop="toggleAgreement">
            <view class="checkbox" :class="{ checked: agreed }">
              <text class="checkbox-icon">✓</text>
            </view>
          </view>
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @click.stop="handleUserAgreement">《用户协议》</text>
            和
            <text class="agreement-link" @click.stop="handlePrivacyPolicy">《隐私政策》</text>
          </text>
        </view>

        <!-- 注册按钮 -->
        <button
          class="btn-auth"
          :class="{ 'btn-auth--loading': registerLoading }"
          :disabled="registerLoading || !agreed || !canRegister"
          @click="handleRegister"
        >
          <text v-if="!registerLoading">{{ registerType === "phone" ? "注册" : "邮箱注册" }}</text>
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
import {
  EmailRegisterForm,
  LoginForm,
  PhoneRegisterForm,
  RegisterForm,
  SmsLoginForm,
  WechatLoginForm,
} from "@/types/user";
import { useUserStore } from "@/stores/user";
import { EmailSendForm, SmsSendForm } from "@/types/code";
import CodeAPI from "@/api/code";
import { CodeTemplateKey } from "@/constants/code-template-key";

// 初始配置
const configStore = useConfigStore();
const userStore = useUserStore();
const activeTab = ref("login");
const systemName = configStore.getConfigValue("system.name");

// 环境检测
const isWechatMiniProgram = ref(false);
const wechatLoginLoading = ref(false);

// 登录类型 - 密码登录或短信登录
const loginType = ref<"password" | "sms">("password");

// 密码登录表单
const loginForm = reactive<LoginForm>({
  username: "",
  password: "",
});

const loginError = reactive({
  username: "",
  password: "",
  smsPhone: "",
  smsCode: "",
});

const showLoginPassword = ref(false);
const loginLoading = ref(false);

// 短信登录表单
const smsLoginForm = reactive<SmsLoginForm>({
  phone: "",
  code: "",
});

const smsLoginLoading = ref(false);
const smsCodeCountdown = ref(0);
let smsCodeTimer: any = null;

// 注册类型
const registerType = ref<"phone" | "email">("phone");

// 注册表单
const registerForm = reactive<RegisterForm>({
  areaCode: "86",
  phone: "",
  email: "",
  code: "",
  emailCode: "",
  password: "",
  confirmPassword: "",
});

const registerError = reactive({
  phone: "",
  email: "",
  code: "",
  emailCode: "",
  password: "",
  confirmPassword: "",
});

const showRegisterPassword = ref(false);
const showConfirmPassword = ref(false);
const registerLoading = ref(false);

// 验证码倒计时
const codeCountdown = ref(0);
const emailCodeCountdown = ref(0);
let codeTimer: any = null;
let emailCodeTimer: any = null;

// 协议同意
const agreed = ref(false);

// 计算属性
const canSendPhoneCode = computed(() => {
  return registerForm.phone && /^1[3-9]\d{9}$/.test(registerForm.phone);
});

const canSendEmailCode = computed(() => {
  return registerForm.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email);
});

const canSendSmsCode = computed(() => {
  return smsLoginForm.phone && /^1[3-9]\d{9}$/.test(smsLoginForm.phone);
});

const canRegister = computed(() => {
  if (registerType.value === "phone") {
    return registerForm.phone && registerForm.code && registerForm.password && registerForm.confirmPassword;
  } else {
    return registerForm.email && registerForm.emailCode && registerForm.password && registerForm.confirmPassword;
  }
});

const canSmsLogin = computed(() => {
  return smsLoginForm.phone && smsLoginForm.code;
});

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

// 检查是否为微信小程序环境
const checkEnvironment = () => {
  // #ifdef MP-WEIXIN
  isWechatMiniProgram.value = true;
  console.log("当前环境：微信小程序，启用微信登录");
  // #endif

  // #ifndef MP-WEIXIN
  isWechatMiniProgram.value = false;
  console.log("当前环境：非微信小程序，显示完整登录表单");
  // #endif
};

// 微信登录处理
const handleWechatLogin = async () => {

  if (!isWechatMiniProgram.value) {
    uni.showToast({
      title: "请在微信小程序中打开",
      icon: "none",
    });
    return;
  }

  wechatLoginLoading.value = true;

  try {
    // 1. 获取微信登录code（正确方式）
    const loginRes = await new Promise<any>((resolve, reject) => {
      uni.login({
        provider: "weixin",
        success: resolve,
        fail: reject,
      });
    });

    if (!loginRes.code) {
      throw new Error("获取微信登录code失败");
    }
    console.log("微信code 获取成功:" + loginRes.code);

    // 2. 只使用code进行登录（后端通过code获取openid）
    const loginData: WechatLoginForm = {
      code: loginRes.code,
    };

    // 调用store中的微信登录方法（需要修改后端接口，只接收code）
    await userStore.wechatLogin(loginData);

    uni.showToast({
      title: "登陆成功",
      icon: "success",
    });

    // 切换到首页页并清空表单
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/index/index",
      });
      resetRegisterForm();
    }, 1500);

  } catch (error: any) {
    console.error("微信登录失败:", error);

    let errorMsg = "登录失败，请重试";
    if (error.code === 40029 || error.errMsg?.includes("invalid code")) {
      errorMsg = "登录码无效或已过期";
    } else if (error.code === 45011) {
      errorMsg = "登录频率限制，请稍后再试";
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  } finally {
    wechatLoginLoading.value = false;
  }
};

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
    loginError.smsPhone = "";
    loginError.smsCode = "";
  } else {
    registerError.phone = "";
    registerError.email = "";
    registerError.code = "";
    registerError.emailCode = "";
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
        }
        break;
      case "password":
        if (!loginForm.password) {
          loginError.password = "请输入密码";
        } else if (loginForm.password.length < 6) {
          loginError.password = "密码长度不能少于6位";
        }
        break;
      case "smsPhone":
        if (!smsLoginForm.phone) {
          loginError.smsPhone = "请输入手机号";
        } else if (!/^1[3-9]\d{9}$/.test(smsLoginForm.phone)) {
          loginError.smsPhone = "请输入正确的手机号";
        }
        break;
      case "smsCode":
        if (!smsLoginForm.code) {
          loginError.smsCode = "请输入验证码";
        } else if (!/^\d{6}$/.test(smsLoginForm.code)) {
          loginError.smsCode = "验证码为6位数字";
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
      case "email":
        if (!registerForm.email) {
          registerError.email = "请输入邮箱地址";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
          registerError.email = "请输入正确的邮箱地址";
        }
        break;
      case "code":
        if (!registerForm.code) {
          registerError.code = "请输入验证码";
        } else if (!/^\d{6}$/.test(registerForm.code)) {
          registerError.code = "验证码为6位数字";
        }
        break;
      case "emailCode":
        if (!registerForm.emailCode) {
          registerError.emailCode = "请输入邮箱验证码";
        } else if (!/^\d{6}$/.test(registerForm.emailCode)) {
          registerError.emailCode = "验证码为6位数字";
        }
        break;
      case "password":
        if (!registerForm.password) {
          registerError.password = "请输入密码";
        } else if (registerForm.password.length < 6) {
          registerError.password = "密码长度不能少于6位";
        } else if (!/^(?=.*[a-zA-Z])(?=.*\d)/.test(registerForm.password)) {
          registerError.password = "密码需包含字母和数字";
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

// 密码登录
const handlePasswordLogin = async () => {
  validateField("username");
  validateField("password");

  if (loginError.username || loginError.password) {
    uni.showToast({
      title: "请填写正确的登录信息",
      icon: "none",
    });
    return;
  }

  loginLoading.value = true;

  try {
    await userStore.login(loginForm);
    uni.showToast({
      title: "登录成功",
      icon: "success",
    });

    setTimeout(() => {
      uni.switchTab({
        url: "/pages/index/index",
      });
    }, 1500);
  } finally {
    loginLoading.value = false;
  }
};

// 短信登录
const handleSmsLogin = async () => {
  validateField("smsPhone");
  validateField("smsCode");

  if (loginError.smsPhone || loginError.smsCode) {
    uni.showToast({
      title: "请填写正确的登录信息",
      icon: "none",
    });
    return;
  }

  smsLoginLoading.value = true;

  try {
    // 调用短信登录API
    await userStore.smsLogin(smsLoginForm);

    uni.showToast({
      title: "登录成功",
      icon: "success",
    });

    setTimeout(() => {
      uni.switchTab({
        url: "/pages/index/index",
      });
    }, 1500);
  } finally {
    smsLoginLoading.value = false;
  }
};

// 发送短信登录验证码
const handleSendSmsCode = async () => {
  if (!smsLoginForm.phone || loginError.smsPhone) {
    validateField("smsPhone");
    if (loginError.smsPhone) return;
  }

  try {
    // 构建短信发送表单
    const smsForm: SmsSendForm = {
      areaCode: "86",
      phone: smsLoginForm.phone,
      templateId: CodeTemplateKey.TEMPLATE_LOGIN,  // 登录验证码
    };

    // 调用短信发送接口
    await CodeAPI.sendSms(smsForm);

    // 开始倒计时
    smsCodeCountdown.value = 60;
    uni.showToast({
      title: "验证码已发送到手机",
      icon: "success",
    });

    smsCodeTimer = setInterval(() => {
      if (smsCodeCountdown.value > 0) {
        smsCodeCountdown.value--;
      } else {
        clearInterval(smsCodeTimer);
      }
    }, 1000);
  } catch (error: any) {
    console.error("发送短信验证码失败:", error);

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

// 注册
const handleRegister = async () => {
  // 根据注册类型验证相应字段
  if (registerType.value === "phone") {
    validateField("phone");
    validateField("code");
  } else {
    validateField("email");
    validateField("emailCode");
  }

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

  // 检查是否有错误
  const hasErrors = Object.values(registerError).some(error => error !== "");
  if (hasErrors) {
    uni.showToast({
      title: "请填写正确的注册信息",
      icon: "none",
    });
    return;
  }

  registerLoading.value = true;

  try {
    if (registerType.value === "phone") {

      const phoneRegisterForm: PhoneRegisterForm = {
        phone: registerForm.phone,
        code: registerForm.code,
        password: registerForm.password,
      };
      // 调用手机注册接口
      await userStore.registerWithPhone(phoneRegisterForm);
    } else {

      const emailRegisterForm: EmailRegisterForm = {
        email: registerForm.email,
        code: registerForm.emailCode,
        password: registerForm.password,
      };
      // 调用邮箱注册接口
      await userStore.registerWithEmail(emailRegisterForm);
    }

    uni.showToast({
      title: "注册成功",
      icon: "success",
    });

    // 切换到首页页并清空表单
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/index/index",
      });
      resetRegisterForm();
    }, 1500);
  } catch (error: any) {
    console.error("注册失败:", error);
    let errorMsg = "注册失败，请重试";
    if (error.data && error.data.message) {
      errorMsg = error.data.message;
    }

    uni.showToast({
      title: errorMsg,
      icon: "none",
      duration: 3000,
    });
  } finally {
    registerLoading.value = false;
  }
};

// 重置注册表单
const resetRegisterForm = () => {
  registerForm.phone = "";
  registerForm.code = "";
  registerForm.email = "";
  registerForm.emailCode = "";
  registerForm.password = "";
  registerForm.confirmPassword = "";
  agreed.value = false;
};

// 发送手机注册验证码
const handleSendPhoneCode = async () => {
  // 验证手机号
  if (!registerForm.phone || registerError.phone) {
    validateField("phone");
    if (registerError.phone) return;
  }

  try {
    // 构建短信发送表单
    const smsForm: SmsSendForm = {
      areaCode: "86",
      phone: registerForm.phone,
      templateId: CodeTemplateKey.TEMPLATE_REGISTER,  // 注册验证码
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
    console.error("发送手机验证码失败:", error);

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
  // 验证邮箱
  if (!registerForm.email || registerError.email) {
    validateField("email");
    if (registerError.email) return;
  }

  try {
    // 构建邮件发送参数
    const emailData: EmailSendForm = {
      email: registerForm.email,
      templateId: CodeTemplateKey.TEMPLATE_REGISTER,  // 注册验证码
    };

    // 调用邮箱验证码发送接口
    await CodeAPI.sendEmail(emailData);

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

// 协议切换
const toggleAgreement = () => {
  agreed.value = !agreed.value;
};

// 忘记密码
const handleForgotPassword = () => {
  uni.navigateTo({
    url: "/pages/auth/forgot",
  });
};

// 快速体验登录
const handleQuickLogin = () => {
  uni.showToast({
    title: "快速体验功能开发中",
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
  checkEnvironment();
  console.log("登录页面加载完成", isWechatMiniProgram.value ? "微信小程序环境" : "其他环境");
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (codeTimer) {
    clearInterval(codeTimer);
  }
  if (emailCodeTimer) {
    clearInterval(emailCodeTimer);
  }
  if (smsCodeTimer) {
    clearInterval(smsCodeTimer);
  }
});
</script>

<style lang="scss" scoped>
.auth-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-color-lighter 0%, $background-color 100%);
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
  margin-bottom: $margin-base * 2;
  padding-top: $navigation-bar-height;
  text-align: center;
}

.brand-logo {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: $margin-base;
  display: block;
}

.brand-name {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  letter-spacing: 2rpx;
  display: block;
  text-align: center;
}

/* 微信登录容器 */
.wechat-auth-container {
  position: relative;
  z-index: $z-index-base + 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
  padding-top: $navigation-bar-height;
  animation: fadeInUp 0.5s ease-out;
  text-align: center;
}

/* 欢迎语 */
.wechat-welcome {
  text-align: center;
  margin-bottom: $margin-base * 3;
}

.welcome-title {
  display: block;
  font-size: $font-size-extra-large + 4rpx;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-small;
  letter-spacing: 1rpx;
  text-align: center;
}

.welcome-subtitle {
  display: block;
  font-size: $font-size-medium;
  color: $text-secondary;
  opacity: 0.8;
  text-align: center;
}

/* 微信登录卡片 */
.wechat-login-card {
  background: $background-color-white;
  border-radius: $border-radius * 2;
  padding: $padding-base * 2;
  width: 100%;
  max-width: 500rpx;
  box-shadow: $box-shadow-dark;
  text-align: center;
}

.wechat-icon-container {
  margin-bottom: $margin-base * 1.5;
  display: flex;
  justify-content: center;
}

.wechat-icon {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #09bb07 0%, #07c160 100%);
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.2);
}

.icon-text {
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  letter-spacing: 1rpx;
}

/* 登录提示 */
.login-prompt {
  margin-bottom: $margin-base * 2;
  text-align: center;
}

.prompt-text {
  display: block;
  font-size: $font-size-medium;
  color: $text-regular;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-mini;
  text-align: center;
}

.prompt-desc {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  opacity: 0.7;
  text-align: center;
}

/* 微信登录按钮 */
.btn-wechat-login {
  width: 100%;
  height: $button-height;
  background: linear-gradient(135deg, #09bb07 0%, #07c160 100%);
  color: $background-color-white;
  border: none;
  border-radius: $border-radius * 1.5;
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
    opacity: 0.9;
  }

  &:disabled {
    opacity: $button-disabled-opacity;
    transform: none;
    box-shadow: none;
  }

  &::after {
    border: none;
  }
}

/* 白色加载动画 */
.loading-spinner-white {
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

/* 服务条款 */
.wechat-agreement {
  margin-top: $margin-base * 2;
  padding: $padding-base;
  max-width: 500rpx;
  text-align: center;
}

.agreement-prompt {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.6;
  text-align: center;
}

.agreement-link {
  color: $primary-color;
  text-decoration: none;
  cursor: pointer;

  &:active {
    text-decoration: underline;
  }
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
  text-align: left;
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
}

.tab-text {
  font-size: $font-size-medium;
  color: $text-secondary;
  transition: color $transition-fast;
  display: block;
  text-align: center;
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

/* 登录方式选择器 */
.login-type-selector {
  display: flex;
  align-items: center;
  margin-bottom: $margin-base;
  background: $background-color;
  border-radius: $border-radius;
  padding: 6rpx;
  box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
}

.login-type-item {
  flex: 1;
  text-align: center;
  padding: $padding-small;
  border-radius: $border-radius-small;
  cursor: pointer;
  transition: all $transition-fast;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    background: $background-color-white;
    box-shadow: $box-shadow-light;
    animation: slideInRight 0.3s ease-out;

    .login-type-text {
      color: $primary-color;
      font-weight: $font-weight-bold;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60%;
      height: 3rpx;
      background: $primary-color;
      border-radius: 2rpx;
      animation: underlineSlide 0.3s ease-out;
    }
  }
}

.login-type-text {
  font-size: $font-size-base;
  color: $text-secondary;
  transition: color $transition-fast;
  position: relative;
  z-index: 1;
}

/* 登录表单区域 */
.login-form-section {
  margin-bottom: $margin-base;
  animation: fadeIn $transition-normal;
}

/* 注册类型选择器 */
.register-type-selector {
  display: flex;
  align-items: center;
  margin-bottom: $margin-base;
  background: $background-color;
  border-radius: $border-radius;
  padding: 6rpx;
  box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
}

.register-type-item {
  flex: 1;
  text-align: center;
  padding: $padding-small;
  border-radius: $border-radius-small;
  cursor: pointer;
  transition: all $transition-fast;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    background: $background-color-white;
    box-shadow: $box-shadow-light;
    animation: slideInRight 0.3s ease-out;

    .register-type-text {
      color: $primary-color;
      font-weight: $font-weight-bold;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60%;
      height: 3rpx;
      background: $primary-color;
      border-radius: 2rpx;
      animation: underlineSlide 0.3s ease-out;
    }
  }
}

.register-type-text {
  font-size: $font-size-base;
  color: $text-secondary;
  transition: color $transition-fast;
  position: relative;
  z-index: 1;
}

/* 注册表单区域 */
.register-form-section {
  margin-bottom: $margin-base;
  padding-bottom: $margin-base;
  border-bottom: 1rpx solid $border-color-extra-light;
  animation: fadeIn $transition-normal;
}

/* 验证码输入区域 - 修复居中问题 */
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

/* 验证码按钮容器 - 修复居中 */
.code-btn-container {
  position: relative;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

/* 验证码按钮 - 修复居中问题 */
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
    text-align: left;
  }

  .label-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

/* 协议同意 */
.agreement-group {
  display: flex;
  align-items: center;
  gap: $margin-mini;
  margin: $margin-base 0;
  padding: $padding-mini 0;
}

.checkbox-wrapper {
  padding: $padding-mini;
  margin: -$padding-mini;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $border-color;
  border-radius: $border-radius-small;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-fast;
  background: $background-color-white;
  flex-shrink: 0;

  &.checked {
    background: $primary-color;
    border-color: $primary-color;
    animation: checkmarkPop 0.2s ease-out;

    .checkbox-icon {
      color: $background-color-white;
      font-weight: $font-weight-bold;
      opacity: 1;
      transform: scale(1);
    }
  }
}

.checkbox-icon {
  font-size: $font-size-small;
  color: transparent;
  opacity: 0;
  transform: scale(0.5);
  transition: all $transition-fast;
}

.agreement-text {
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.5;
  flex: 1;
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
  align-items: center;
  gap: $margin-base * 2;
}

.login-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  padding: $padding-mini;
  cursor: pointer;
  transition: transform $transition-fast;

  &:active {
    transform: scale(0.95);
  }

  .method-text {
    font-size: $font-size-small;
    color: $text-secondary;
    margin-top: 6rpx;
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

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20rpx);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes underlineSlide {
  from {
    width: 0;
  }
  to {
    width: 60%;
  }
}

@keyframes checkmarkPop {
  0% {
    transform: scale(0.8);
  }
  70% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
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
    padding: $padding-small;
  }

  .login-methods {
    gap: $margin-base;
  }

  .wechat-login-card {
    padding: $padding-base * 1.5;
    max-width: 450rpx;
  }

  .welcome-title {
    font-size: $font-size-extra-large;
  }

  .welcome-subtitle {
    font-size: $font-size-base;
  }

  .login-type-text,
  .register-type-text {
    font-size: $font-size-small;
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
}

/* 修复所有按钮的基线对齐 */
button {
  vertical-align: middle;
  -webkit-tap-keyword-color: transparent;
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