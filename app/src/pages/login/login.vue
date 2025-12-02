<template>
  <view class="login-container">
    <view class="login-header">
      <image src="/static/logo.png" class="w160rpx h160rpx" />
    </view>

    <view class="login-form">
    </view>

    <view class="login-footer">
      <view class="text-center">
      </view>
      <view class="text-center mt-20rpx text-sm">
        <text class="text-gray">登录即同意</text>
        <text @click="navigateToUserAgreement">《用户协议》</text>
        <text class="text-gray">和</text>
        <text @click="navigateToPrivacy">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { useUserStore } from "@/stores/user";
import { useDictStore } from "@/stores/dict";
import { LoginForm } from "@/types/user";
const loginFormRef = ref();

const loginFormData = ref<LoginForm>({
  username: "admin",
  password: "123456",
});

const userStore = useUserStore();
const dictStore = useDictStore();

// 登录处理
const handleLogin = () => {
  loginFormRef.value.validate().then(async ({ valid }: { valid: boolean }) => {
    if (valid) {
      try {
        await userStore.login(loginFormData.value);
        await userStore.getInfo();
        uni.showToast({ title: "登录成功", icon: "success" });

        // 检查是否有上一页
        const pages = getCurrentPages();
        console.log("pages", pages.length);
        if (pages.length > 1) {
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index",
            });
          }, 1500);
        }
      } catch (error: any) {
        console.log("登录失败", error.message);
      }
    }
  });
};

const navigateToUserAgreement = () => {
  uni.navigateTo({
    url: "/pages/agreement/user-agreement",
  });
};
const navigateToPrivacy = () => {
  uni.navigateTo({
    url: "/pages/agreement/privacy-agreement",
  });
};

// 微信登录处理
const handleWechatLogin = async () => {
  try {
    // 获取微信登录的临时 code
    const { code } = await uni.login({
      provider: "weixin",
    });

    // 调用后端接口进行登录认证
    const result = await userStore.loginByWechat(code);

    if (result) {
      // 获取用户信息
      await userStore.getInfo();

      uni.showToast({
        title: "登录成功",
        icon: "success",
      });

      const pages = getCurrentPages();

      if (pages.length > 1) {
        uni.navigateBack();
      } else {
        uni.reLaunch({
          url: "/pages/index/index",
        });
      }
    }
  } catch (error: any) {
    console.error("微信登录失败", error);
    uni.showToast({
      title: error.message || "微信登录失败",
      icon: "none",
    });
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  height: 100vh;
  background: #fff;
  .login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 160rpx 0;
    background: url("@/static/images/login-bg.png") no-repeat center center;
    background-size: 100% 100%;
  }
  .login-form {
    width: 80%;
    margin: 80rpx auto;
  }

  .login-footer {
    position: absolute;
    bottom: 40rpx;
    width: 100%;
  }
}
</style>
