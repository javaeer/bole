<template>
  <view class="page-container">
    <!-- 标签页切换 -->
    <view class="tab-container">
      <scroll-view class="tab-scroll" scroll-x="true" scroll-with-animation>
        <view class="tab-list">
          <view
            v-for="tab in tabs"
            :key="tab.type"
            class="tab-item"
            :class="{ 'tab-item--active': activeTab === tab.type }"
            @click="handleTabChange(tab.type)"
          >
            <text class="tab-text">{{ tab.label }}</text>
            <view v-if="activeTab === tab.type" class="tab-indicator"></view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="bind-content" scroll-y="true">
      <!-- 手机号绑定 -->
      <view v-if="activeTab === 'phone'" class="bind-section">
        <!-- 已绑定状态 -->
        <view v-if="userStore.userInfo?.phone" class="bind-status">
          <view class="status-icon success">✓</view>
          <text class="status-title">已绑定手机号</text>
          <text class="status-text">{{ formatPhone(userStore.userInfo.phone) }}</text>
          <view class="status-tips">
            绑定手机号可用于登录、找回密码和接收重要通知
          </view>
        </view>

        <!-- 未绑定状态 -->
        <view v-else class="bind-status">
          <view class="status-icon warning">!</view>
          <text class="status-title">未绑定手机号</text>
          <text class="status-text">绑定手机号可增强账号安全</text>
          <view class="status-tips">
            绑定后可用于登录、找回密码和接收重要通知
          </view>
        </view>

        <!-- 绑定/解绑表单 -->
        <view class="bind-form">
          <template v-if="!userStore.userInfo?.phone">
            <!-- 绑定手机表单 -->
            <view class="form-group">
              <text class="form-label">手机号</text>
              <input
                v-model="phoneForm.phone"
                class="form-input"
                type="number"
                placeholder="请输入手机号"
                placeholder-class="placeholder"
                maxlength="11"
              />
              <view v-if="phoneError.phone" class="error-message">
                {{ phoneError.phone }}
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">验证码</text>
              <view class="code-input-wrapper">
                <input
                  v-model="phoneForm.code"
                  class="form-input code-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                />
                <button
                  class="code-btn"
                  :class="{ 'code-btn--disabled': codeCountdown > 0 }"
                  :disabled="codeCountdown > 0"
                  @click="handleSendCode"
                >
                  <text v-if="codeCountdown === 0">获取验证码</text>
                  <text v-else>{{ codeCountdown }}s后重试</text>
                </button>
              </view>
              <view v-if="phoneError.code" class="error-message">
                {{ phoneError.code }}
              </view>
            </view>

            <button
              class="btn-bind"
              :class="{ 'btn-bind--loading': bindingPhone }"
              :disabled="!phoneForm.phone || !phoneForm.code || bindingPhone"
              @click="handleBindPhone"
            >
              <text v-if="!bindingPhone">绑定手机号</text>
              <view v-else class="loading-spinner"></view>
            </button>
          </template>

          <template v-else>
            <!-- 解绑手机表单 -->
            <view class="unbind-notice">
              <text class="unbind-title">解绑手机号</text>
              <text class="unbind-desc">
                解绑后，您将无法使用该手机号登录。建议先绑定其他手机号或邮箱后再解绑。
              </text>
            </view>

            <view class="form-group">
              <text class="form-label">验证码</text>
              <view class="code-input-wrapper">
                <input
                  v-model="phoneForm.code"
                  class="form-input code-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                />
                <button
                  class="code-btn"
                  :class="{ 'code-btn--disabled': codeCountdown > 0 }"
                  :disabled="codeCountdown > 0"
                  @click="handleSendUnbindCode"
                >
                  <text v-if="codeCountdown === 0">获取验证码</text>
                  <text v-else>{{ codeCountdown }}s后重试</text>
                </button>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">登录密码</text>
              <input
                v-model="phoneForm.password"
                class="form-input"
                type="password"
                placeholder="请输入登录密码确认"
                placeholder-class="placeholder"
              />
            </view>

            <view class="btn-group">
              <button
                class="btn-change"
                @click="handleChangePhone"
              >
                更换手机号
              </button>
              <button
                class="btn-unbind"
                :class="{ 'btn-unbind--loading': unbindingPhone }"
                :disabled="unbindingPhone"
                @click="handleUnbindPhone"
              >
                <text v-if="!unbindingPhone">解绑手机号</text>
                <view v-else class="loading-spinner"></view>
              </button>
            </view>
          </template>
        </view>
      </view>

      <!-- 邮箱绑定 -->
      <view v-if="activeTab === 'email'" class="bind-section">
        <!-- 已绑定状态 -->
        <view v-if="userStore.userInfo?.email" class="bind-status">
          <view class="status-icon success">✓</view>
          <text class="status-title">已绑定邮箱</text>
          <text class="status-text">{{ userStore.userInfo.email }}</text>
          <view class="status-tips">
            绑定邮箱可用于登录、找回密码和接收重要通知
          </view>
        </view>

        <!-- 未绑定状态 -->
        <view v-else class="bind-status">
          <view class="status-icon warning">!</view>
          <text class="status-title">未绑定邮箱</text>
          <text class="status-text">绑定邮箱可增强账号安全</text>
          <view class="status-tips">
            绑定后可用于登录、找回密码和接收重要通知
          </view>
        </view>

        <!-- 绑定/解绑表单 -->
        <view class="bind-form">
          <template v-if="!userStore.userInfo?.email">
            <!-- 绑定邮箱表单 -->
            <view class="form-group">
              <text class="form-label">邮箱地址</text>
              <input
                v-model="emailForm.email"
                class="form-input"
                type="text"
                placeholder="请输入邮箱地址"
                placeholder-class="placeholder"
              />
              <view v-if="emailError.email" class="error-message">
                {{ emailError.email }}
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">验证码</text>
              <view class="code-input-wrapper">
                <input
                  v-model="emailForm.code"
                  class="form-input code-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                />
                <button
                  class="code-btn"
                  :class="{ 'code-btn--disabled': emailCodeCountdown > 0 }"
                  :disabled="emailCodeCountdown > 0"
                  @click="handleSendEmailCode"
                >
                  <text v-if="emailCodeCountdown === 0">获取验证码</text>
                  <text v-else>{{ emailCodeCountdown }}s后重试</text>
                </button>
              </view>
              <view v-if="emailError.code" class="error-message">
                {{ emailError.code }}
              </view>
            </view>

            <button
              class="btn-bind"
              :class="{ 'btn-bind--loading': bindingEmail }"
              :disabled="!emailForm.email || !emailForm.code || bindingEmail"
              @click="handleBindEmail"
            >
              <text v-if="!bindingEmail">绑定邮箱</text>
              <view v-else class="loading-spinner"></view>
            </button>
          </template>

          <template v-else>
            <!-- 解绑邮箱表单 -->
            <view class="unbind-notice">
              <text class="unbind-title">解绑邮箱</text>
              <text class="unbind-desc">
                解绑后，您将无法使用该邮箱登录。建议先绑定其他邮箱或手机号后再解绑。
              </text>
            </view>

            <view class="form-group">
              <text class="form-label">验证码</text>
              <view class="code-input-wrapper">
                <input
                  v-model="emailForm.code"
                  class="form-input code-input"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="placeholder"
                  maxlength="6"
                />
                <button
                  class="code-btn"
                  :class="{ 'code-btn--disabled': emailCodeCountdown > 0 }"
                  :disabled="emailCodeCountdown > 0"
                  @click="handleSendUnbindEmailCode"
                >
                  <text v-if="emailCodeCountdown === 0">获取验证码</text>
                  <text v-else>{{ emailCodeCountdown }}s后重试</text>
                </button>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">登录密码</text>
              <input
                v-model="emailForm.password"
                class="form-input"
                type="password"
                placeholder="请输入登录密码确认"
                placeholder-class="placeholder"
              />
            </view>

            <view class="btn-group">
              <button
                class="btn-change"
                @click="handleChangeEmail"
              >
                更换邮箱
              </button>
              <button
                class="btn-unbind"
                :class="{ 'btn-unbind--loading': unbindingEmail }"
                :disabled="unbindingEmail"
                @click="handleUnbindEmail"
              >
                <text v-if="!unbindingEmail">解绑邮箱</text>
                <view v-else class="loading-spinner"></view>
              </button>
            </view>
          </template>
        </view>
      </view>

      <!-- 微信绑定 -->
      <view v-if="activeTab === 'wechat'" class="bind-section">
        <!-- 已绑定状态 -->
        <view v-if="userStore.userInfo?.wechat" class="bind-status">
          <view class="status-icon success">✓</view>
          <text class="status-title">已绑定微信</text>
          <text class="status-text">已通过微信授权绑定</text>
          <view class="status-tips">
            绑定微信可用于快速登录和分享内容
          </view>
        </view>

        <!-- 未绑定状态 -->
        <view v-else class="bind-status">
          <view class="status-icon warning">!</view>
          <text class="status-title">未绑定微信</text>
          <text class="status-text">绑定微信可快速登录</text>
          <view class="status-tips">
            绑定后可使用微信快速登录和分享内容
          </view>
        </view>

        <!-- 绑定/解绑操作 -->
        <view class="bind-form">
          <template v-if="!userStore.userInfo?.wechat">
            <!-- 绑定微信 -->
            <view class="wechat-bind">
              <text class="wechat-desc">
                点击下方按钮，跳转到微信进行授权绑定
              </text>
              <button class="btn-wechat" @click="handleBindWechat">
                <text class="wechat-icon">Ⓦ</text>
                <text class="wechat-text">绑定微信账号</text>
              </button>
            </view>
          </template>

          <template v-else>
            <!-- 解绑微信 -->
            <view class="unbind-notice">
              <text class="unbind-title">解绑微信</text>
              <text class="unbind-desc">
                解绑后，您将无法使用微信快速登录。建议先绑定手机号或邮箱后再解绑。
              </text>
            </view>

            <view class="form-group">
              <text class="form-label">登录密码</text>
              <input
                v-model="wechatForm.password"
                class="form-input"
                type="password"
                placeholder="请输入登录密码确认"
                placeholder-class="placeholder"
              />
            </view>

            <button
              class="btn-unbind"
              :class="{ 'btn-unbind--loading': unbindingWechat }"
              :disabled="!wechatForm.password || unbindingWechat"
              @click="handleUnbindWechat"
            >
              <text v-if="!unbindingWechat">解绑微信</text>
              <view v-else class="loading-spinner"></view>
            </button>
          </template>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useUserStore } from "@/stores/user";
import { onLoad } from "@dcloudio/uni-app";
import CodeAPI from "@/api/code";
import { CodeTemplateKey } from "@/constants/code-template-key";
import { EventKey } from "@/constants/event-key";
import { BindEmailForm, BindPhoneForm } from "@/types/user";
import { EmailSendForm, SmsSendForm } from "@/types/code";

const userStore = useUserStore();

// 页面标题
const pageTitle = ref("账号绑定");

// 标签页配置
const tabs = [
  { type: "phone", label: "手机号" },
  { type: "email", label: "邮箱" },
  { type: "wechat", label: "微信" },
];

// 活动标签页
const activeTab = ref<"phone" | "email" | "wechat">("phone");

// 手机号表单
const phoneForm: BindPhoneForm = reactive({
  phone: "",
  code: "",
  password: "",
});

const phoneError = reactive({
  phone: "",
  code: "",
});

const codeCountdown = ref(0);
const bindingPhone = ref(false);
const unbindingPhone = ref(false);

// 邮箱表单
const emailForm: BindEmailForm = reactive({
  email: "",
  code: "",
  password: "",
});

const emailError = reactive({
  email: "",
  code: "",
});

const emailCodeCountdown = ref(0);
const bindingEmail = ref(false);
const unbindingEmail = ref(false);

// 微信表单
const wechatForm = reactive({
  password: "",
});

const unbindingWechat = ref(false);

// 处理标签页切换
const handleTabChange = (type: "phone" | "email" | "wechat") => {
  activeTab.value = type;
};

// 格式化手机号显示
const formatPhone = (phone: string) => {
  if (!phone) return "";
  return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
};

// 发送验证码
const handleSendCode = () => {
  if (!phoneForm.phone) {
    phoneError.phone = "请输入手机号";
    return;
  }

  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    phoneError.phone = "请输入正确的手机号";
    return;
  }

  // 调用发送验证码接口
  console.log("发送验证码到:", phoneForm.phone);

  // 构建发送参数
  const smsSendForm: SmsSendForm = {
    phone: phoneForm.phone,
    templateId: CodeTemplateKey.TEMPLATE_CHANGE_BIND,
  };

  CodeAPI.sendSms(smsSendForm);

  // 开始倒计时
  codeCountdown.value = 60;
  const timer = setInterval(() => {
    codeCountdown.value--;
    if (codeCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);

  uni.showToast({
    title: "验证码已发送",
    icon: "success",
  });
};

// 发送解绑验证码
const handleSendUnbindCode = () => {
  // 发送解绑验证码逻辑
  CodeAPI.sendUnbindSms();

  codeCountdown.value = 60;
  const timer = setInterval(() => {
    codeCountdown.value--;
    if (codeCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);

  uni.showToast({
    title: "验证码已发送",
    icon: "success",
  });
};

// 发送邮箱验证码
const handleSendEmailCode = () => {
  if (!emailForm.email) {
    emailError.email = "请输入邮箱地址";
    return;
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.email)) {
    emailError.email = "请输入正确的邮箱地址";
    return;
  }

  // 构建发送参数
  const emailSendForm: EmailSendForm = {
    email: emailForm.email,
    templateId: CodeTemplateKey.TEMPLATE_CHANGE_BIND,
  };

  // 调用发送邮箱验证码接口
  console.log("发送验证码到:", emailForm.email);
  CodeAPI.sendEmail(emailSendForm);

  emailCodeCountdown.value = 60;
  const timer = setInterval(() => {
    emailCodeCountdown.value--;
    if (emailCodeCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);

  uni.showToast({
    title: "验证码已发送",
    icon: "success",
  });
};

// 发送解绑邮箱验证码
const handleSendUnbindEmailCode = () => {

  CodeAPI.sendUnbindEmail();

  emailCodeCountdown.value = 60;
  const timer = setInterval(() => {
    emailCodeCountdown.value--;
    if (emailCodeCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);

  uni.showToast({
    title: "验证码已发送",
    icon: "success",
  });
};

// 绑定手机号
const handleBindPhone = async () => {
  bindingPhone.value = true;

  try {
    // 调用绑定手机号接口
    await userStore.bindPhone(phoneForm);

    uni.showToast({
      title: "绑定成功",
      icon: "success",
    });

    // 触发全局事件更新用户信息
    uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
      success: true,
      timestamp: Date.now(),
    });

    // 清空表单
    phoneForm.phone = "";
    phoneForm.code = "";

  } catch (error: any) {
    uni.showToast({
      title: error.message || "绑定失败",
      icon: "error",
    });
  } finally {
    bindingPhone.value = false;
  }
};

// 更换手机号
const handleChangePhone = () => {
  phoneForm.phone = "";
  phoneForm.code = "";
  phoneForm.password = "";
};

// 解绑手机号
const handleUnbindPhone = async () => {
  unbindingPhone.value = true;

  try {
    // 调用解绑手机号接口
    await userStore.unbindPhone(phoneForm);

    uni.showModal({
      title: "解绑成功",
      content: "手机号已成功解绑",
      showCancel: false,
      success: () => {
        // 触发全局事件更新用户信息
        uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
          success: true,
          timestamp: Date.now(),
        });
      },
    });

  } catch (error: any) {
    uni.showToast({
      title: error.message || "解绑失败",
      icon: "error",
    });
  } finally {
    unbindingPhone.value = false;
  }
};

// 绑定邮箱
const handleBindEmail = async () => {
  bindingEmail.value = true;

  try {
    // 调用绑定邮箱接口
    await userStore.bindEmail(emailForm);

    uni.showToast({
      title: "绑定成功",
      icon: "success",
    });

    uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
      success: true,
      timestamp: Date.now(),
    });

    emailForm.email = "";
    emailForm.code = "";

  } catch (error: any) {
    uni.showToast({
      title: error.message || "绑定失败",
      icon: "error",
    });
  } finally {
    bindingEmail.value = false;
  }
};

// 更换邮箱
const handleChangeEmail = () => {
  emailForm.email = "";
  emailForm.code = "";
  emailForm.password = "";
};

// 解绑邮箱
const handleUnbindEmail = async () => {
  unbindingEmail.value = true;

  try {
    // 调用解绑邮箱接口
    await userStore.unbindEmail(emailForm);

    uni.showModal({
      title: "解绑成功",
      content: "邮箱已成功解绑",
      showCancel: false,
      success: () => {
        uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
          success: true,
          timestamp: Date.now(),
        });
      },
    });

  } catch (error: any) {
    uni.showToast({
      title: error.message || "解绑失败",
      icon: "error",
    });
  } finally {
    unbindingEmail.value = false;
  }
};

// 绑定微信
const handleBindWechat = () => {
  uni.showModal({
    title: "绑定微信",
    content: "即将跳转到微信进行授权，确认继续吗？",
    success: (res) => {
      if (res.confirm) {
        // 跳转到微信授权页面
        // uni.navigateToMiniProgram({ appId: 'wx1234567890' });

        uni.showLoading({
          title: "跳转中...",
        });

        // 模拟跳转
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "请在微信中完成授权",
            icon: "success",
          });
        }, 1000);
      }
    },
  });
};

// 解绑微信
const handleUnbindWechat = async () => {
  unbindingWechat.value = true;

  try {
    // 调用解绑微信接口
    // await userStore.unbindWechat(wechatForm);

    uni.showModal({
      title: "解绑成功",
      content: "微信已成功解绑",
      showCancel: false,
      success: () => {
        uni.$emit(EventKey.USER_INFO_UPDATED_EVENT, {
          success: true,
          timestamp: Date.now(),
        });
      },
    });

    wechatForm.password = "";

  } catch (error: any) {
    uni.showToast({
      title: error.message || "解绑失败",
      icon: "error",
    });
  } finally {
    unbindingWechat.value = false;
  }
};

// 页面加载时处理参数
onLoad((options) => {
  if (options.type) {
    const type = options.type as "phone" | "email" | "wechat";
    if (tabs.some(tab => tab.type === type)) {
      activeTab.value = type;
    }
  }
});

onMounted(() => {
  uni.setNavigationBarTitle({
    title: pageTitle.value,
  });
});
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100vh;
  background-color: $background-color;
}

/* 顶部导航 */
.bind-header {
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

.header-left {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    opacity: 0.8;
  }
}

.back-icon {
  font-size: $font-size-extra-large;
  color: $background-color-white;
  font-weight: $font-weight-bold;
}

.header-title {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $background-color-white;
  flex: 1;
  text-align: center;
}

.header-right {
  width: 60rpx;
}

/* 标签页容器 */
.tab-container {
  background: $background-color-white;
  padding: 0 $padding-base;
}

.tab-scroll {
  white-space: nowrap;
}

.tab-list {
  display: inline-flex;
  gap: $margin-base;
}

.tab-item {
  position: relative;
  padding: $padding-small 0;
  min-width: 80rpx;

  &--active {
    .tab-text {
      color: $primary-color;
      font-weight: $font-weight-bold;
    }

    .tab-indicator {
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 40rpx;
      height: 4rpx;
      background: $primary-color;
      border-radius: 2rpx;
    }
  }
}

.tab-text {
  font-size: $font-size-base;
  color: $text-secondary;
  transition: all $transition-fast;
}

/* 绑定内容 */
.bind-content {
  height: calc(100vh - var(--status-bar-height, 44px) - $navigation-bar-height - 80rpx);
}

/* 绑定区域 */
.bind-section {
  padding: $padding-base;
}

/* 绑定状态 */
.bind-status {
  text-align: center;
  padding: $padding-large 0;
  border-bottom: 1rpx solid $border-color-extra-light;
  margin-bottom: $margin-base;
}

.status-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto $margin-small;
  font-size: $font-size-extra-large;

  &.success {
    background: $success-bg;
    color: $success-color;
    border: 2rpx solid $success-border;
  }

  &.warning {
    background: $warning-bg;
    color: $warning-color;
    border: 2rpx solid $warning-border;
  }
}

.status-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.status-text {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
  margin-bottom: $margin-small;
}

.status-tips {
  display: block;
  font-size: $font-size-small;
  color: $text-placeholder;
  line-height: 1.5;
}

/* 绑定表单 */
.bind-form {
  padding-top: $padding-base;
}

.form-group {
  margin-bottom: $margin-base;
}

.form-label {
  display: block;
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-mini;
}

.form-input {
  width: 100%;
  height: $input-height;
  padding: 0 $padding-small;
  font-size: $font-size-base;
  color: $text-primary;
  background: $background-color-white;
  border: 2rpx solid $border-color-light;
  border-radius: $border-radius;
  transition: all $transition-fast;

  &:focus {
    border-color: $primary-color;
    box-shadow: $input-focus-shadow;
    outline: none;
  }

  &.code-input {
    flex: 1;
  }
}

.code-input-wrapper {
  display: flex;
  gap: $margin-mini;
  align-items: center;
}

.code-btn {
  padding: 0 $padding-small;
  height: $input-height;
  background: $primary-light;
  border: 2rpx solid $primary-border;
  border-radius: $border-radius;
  font-size: $font-size-small;
  color: $primary-color;
  font-weight: $font-weight-medium;
  white-space: nowrap;

  &:active {
    background: color.adjust($primary-light, $lightness: -5%);
  }

  &--disabled {
    background: $background-color;
    border-color: $border-color-light;
    color: $text-placeholder;
  }
}

.error-message {
  color: $danger-color;
  font-size: $font-size-small;
  margin-top: calc($margin-mini / 2);
  min-height: 40rpx;
}

/* 按钮样式 */
.btn-bind {
  width: 100%;
  height: $button-height;
  background: $button-primary-bg;
  border: none;
  border-radius: $button-border-radius;
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  margin-top: $margin-base;

  &:disabled {
    background: $button-disabled-bg;
    opacity: $button-disabled-opacity;
  }

  &--loading {
    opacity: 0.8;
  }
}

.btn-unbind {
  width: 100%;
  height: $button-height;
  background: $danger-light;
  border: 2rpx solid $danger-border;
  border-radius: $button-border-radius;
  color: $danger-color;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  margin-top: $margin-base;

  &:disabled {
    opacity: $button-disabled-opacity;
  }

  &--loading {
    opacity: 0.8;
  }
}

.btn-change {
  width: 100%;
  height: $button-height;
  background: $background-color-white;
  border: 2rpx solid $border-color-light;
  border-radius: $button-border-radius;
  color: $text-primary;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-small;
}

.btn-group {
  margin-top: $margin-base;
}

/* 解绑通知 */
.unbind-notice {
  padding: $padding-base;
  background: $warning-bg;
  border: 2rpx solid $warning-border;
  border-radius: $border-radius;
  margin-bottom: $margin-base;
}

.unbind-title {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  color: $warning-color;
  margin-bottom: $margin-mini;
}

.unbind-desc {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.6;
}

/* 微信绑定 */
.wechat-bind {
  text-align: center;
  padding: $padding-base 0;
}

.wechat-desc {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
  margin-bottom: $margin-base;
}

.btn-wechat {
  width: 100%;
  height: $button-height;
  background: $success-color;
  border: none;
  border-radius: $button-border-radius;
  color: $background-color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;
}

.wechat-icon {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
}

.wechat-text {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
}

/* 加载动画 */
.loading-spinner {
  width: 30rpx;
  height: 30rpx;
  border: 3rpx solid rgba($background-color-white, 0.3);
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

/* 底部占位 */
.bottom-space {
  height: $tabbar-height;
}
</style>