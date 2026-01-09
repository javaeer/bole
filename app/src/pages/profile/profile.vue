<template>
  <view class="page-container">
    <!-- 用户信息头部 -->
    <view class="user-header">
      <!-- 背景装饰元素 -->
      <view class="background-decoration"></view>

      <view class="user-avatar-section">
        <view class="avatar-wrapper">
          <image
            :src="userInfo.avatar || '/static/logo.png'"
            class="user-avatar"
            mode="aspectFit"
          />
        </view>

        <view class="user-info-wrapper">
          <view class="user-info">
            <text class="user-name">{{ userInfo.name || "未设置" }}</text>
            <text class="user-title">{{ userInfo.title || "未设置" }}</text>
          </view>

          <!-- 编辑按钮 -->
          <view class="edit-icon-wrapper" @click="handleEditProfile">
            <view class="btn-content">
              <text class="btn-icon">✏️</text>
              <text class="btn-text">编辑资料</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 管理卡片区域 -->
    <view class="section management-section">
      <view class="section-header">
        <text class="section-title">信息管理</text>
        <text class="section-subtitle">完善信息，可提升简历质量</text>
      </view>

      <view class="stats">
        <!-- 简历管理 -->
<!--        <view class="stat-card" @click="handleResumes">-->
<!--          <view class="stat-card-inner">-->
<!--            <text class="stat-icon">📝</text>-->
<!--            <text class="stat-title">简历管理</text>-->
<!--          </view>-->
<!--        </view>-->

        <!-- 学历管理 -->
        <view class="stat-card" @click="handleEducationExperiences">
          <view class="stat-card-inner">
            <text class="stat-icon">🎓</text>
            <text class="stat-title">学习经历</text>
          </view>
        </view>

        <!-- 求职意向 -->
        <view class="stat-card" @click="handleJobIntentions">
          <view class="stat-card-inner">
            <text class="stat-icon">🎯</text>
            <text class="stat-title">求职意向</text>
          </view>
        </view>

        <!-- 工作经历 -->
        <view class="stat-card" @click="handleWorkExperiences">
          <view class="stat-card-inner">
            <text class="stat-icon">💼</text>
            <text class="stat-title">工作经历</text>
          </view>
        </view>

<!--        &lt;!&ndash; 公司里程 &ndash;&gt;-->
<!--        <view class="stat-card" @click="handleCompanyExperiences">-->
<!--          <view class="stat-card-inner">-->
<!--            <text class="stat-icon">🏢</text>-->
<!--            <text class="stat-title">公司里程</text>-->
<!--          </view>-->
<!--        </view>-->

        <!-- 项目经验 -->
        <view class="stat-card" @click="handleProjectExperiences">
          <view class="stat-card-inner">
            <text class="stat-icon">📂</text>
            <text class="stat-title">项目经验</text>
          </view>
        </view>

        <!-- 技能专长 -->
        <view class="stat-card" @click="handleSkills">
          <view class="stat-card-inner">
            <text class="stat-icon">🛠️</text>
            <text class="stat-title">技能专长</text>
          </view>
        </view>

        <!-- 自我评价 -->
        <view class="stat-card" @click="handleSelfEvaluations">
          <view class="stat-card-inner">
            <text class="stat-icon">👤</text>
            <text class="stat-title">自我评价</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 设置入口 -->
    <view class="section settings-section">
      <view class="section-header">
        <text class="section-title">设置</text>
      </view>

      <view class="menu-list">

        <view class="menu-item" @click="handleFollow">
          <view class="menu-left">
            <view class="menu-icon-wrapper" style="background: rgba(236,226,32,0.65);">
              <text class="menu-icon" style="color: #ECE220FF;">✨</text>
            </view>
            <text class="menu-text">收藏管理</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleFeedback">
          <view class="menu-left">
            <view class="menu-icon-wrapper" style="background: rgba(33, 150, 243, 0.1);">
              <text class="menu-icon" style="color: #2196F3;">💬</text>
            </view>
            <text class="menu-text">意见反馈</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleAbout">
          <view class="menu-left">
            <view class="menu-icon-wrapper" style="background: rgba(76, 175, 80, 0.1);">
              <text class="menu-icon" style="color: #4CAF50;">ℹ️</text>
            </view>
            <text class="menu-text">关于我们</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleLogout">
          <view class="menu-left">
            <view class="menu-icon-wrapper" style="background: rgba(244, 67, 54, 0.1);">
              <text class="menu-icon" style="color: #F44336;">🚪</text>
            </view>
            <text class="menu-text">退出登录</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useUserStore } from "@/stores/user";
import { onUnload } from "@dcloudio/uni-app";
import { EventKey } from "@/constants/event-key";

const userStore = useUserStore();

// 使用计算属性获取用户信息，确保响应式
const userInfo = computed(() => {
  const storeInfo = userStore.userInfo || {};
  return {
    name: storeInfo.name || "未设置",
    title: storeInfo.title || "未设置",
    avatar: storeInfo.avatar || "/static/logo.png",
    followers: storeInfo.followers || 0,
    fans: storeInfo.fans || 0,
    likes: storeInfo.likes || 0,
  };
});

// 简历统计
const resumeStats = ref({
  total: 3,
  viewed: 12,
  downloaded: 5,
});

// 页面状态
const pageState = reactive({
  lastRefreshTime: 0,
  refreshFlag: false,
  lastUpdateTimestamp: 0,
});

// 处理用户信息更新事件
const handleUserInfoUpdated = (data: any) => {
  if (data && data.success) {
    console.log("收到用户信息更新事件，准备刷新数据");
    // 设置刷新标志
    pageState.refreshFlag = true;
    pageState.lastUpdateTimestamp = data.timestamp || Date.now();

    // 可以在这里立即刷新
    loadUserInfo();
  }
};

// 加载用户信息
const loadUserInfo = async () => {
  try {
    console.log("开始加载用户信息...");
    await userStore.userInfo
    pageState.lastRefreshTime = Date.now();
    pageState.refreshFlag = false;
    console.log("用户信息加载完成");
  } catch (error) {
    console.error("刷新用户信息失败:", error);
    uni.showToast({
      title: "获取用户信息失败",
      icon: "error",
    });
  }
};

// 事件处理函数
const handleEditProfile = () => {
  uni.navigateTo({
    url: "/pages/profile/edit",
  });
};

const handleResumes = () => {
  uni.navigateTo({
    url: "/pages/resumes/list",
  });
};

const handleEducationExperiences = () => {
  uni.navigateTo({
    url: "/pages/education/list",
  });
};

const handleJobIntentions = () => {
  uni.navigateTo({
    url: "/pages/intention/list",
  });
};

const handleWorkExperiences = () => {
  uni.navigateTo({
    url: "/pages/work/list",
  });
};

const handleCompanyExperiences = () => {
  uni.navigateTo({
    url: "/subpackages/pages/company/list",
  });
};

const handleProjectExperiences = () => {
  uni.navigateTo({
url: "/pages/project/list",
  });
};

const handleSkills = () => {
  uni.navigateTo({
    url: "/pages/skill/list",
  });
};

const handleSelfEvaluations = () => {
  uni.navigateTo({
    url: "/pages/evaluation/list",
  });
};

const handleFeedback = () => {
  uni.navigateTo({
    url: "/subpackages/pages/feedback/feedback",
  });
};

const handleFollow = () => {
  uni.navigateTo({
    url: "/pages/follow/follow",
  });
};

const handleAbout = () => {
  uni.navigateTo({
    url: "/subpackages/pages/about/about",
  });
};

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: "确认退出",
    content: "确定要退出当前账号吗？",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: "退出中...",
        });
        userStore.logout({ callApi: true });
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "退出成功",
            icon: "success",
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index",
            });
          }, 1500);
        }, 1000);
      }
    },
  });
};

// 页面生命周期
onMounted(() => {
  console.log("我的页面加载完成");

  // 初始加载用户信息
  loadUserInfo();

  // 监听全局事件
  uni.$on(EventKey.USER_INFO_UPDATED_EVENT, handleUserInfoUpdated);
});

// 页面显示时刷新
const onShow = () => {
  // 页面显示时检查是否需要刷新
  const now = Date.now();
  // 如果上次刷新时间超过10秒，或者有刷新标志，则刷新
  if (pageState.refreshFlag || now - pageState.lastRefreshTime > 10000) {
    console.log("页面显示，触发刷新检查");
    loadUserInfo();
  }
};

// 页面卸载时清理事件监听
onUnload(() => {
  // 移除事件监听
  uni.$off(EventKey.USER_INFO_UPDATED_EVENT, handleUserInfoUpdated);
});

// 暴露生命周期方法给uni-app框架
defineExpose({
  onShow,
});
</script>

<style scoped lang="scss">
.page-container {
  background: linear-gradient(to bottom, #f5f7fa 0%, #f5f7fa 60%, #ffffff 100%);
  min-height: 100vh;
  padding-bottom: 80rpx;
}

// 用户头部样式
.user-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding: 80rpx $padding-base 40rpx;
  color: $background-color-white;
  position: relative;
  overflow: hidden;

  .background-decoration {
    position: absolute;
    top: 0;
    right: 0;
    width: 200rpx;
    height: 200rpx;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
    transform: translate(30%, -30%);
    z-index: 0;
  }
}

.user-avatar-section {
  display: flex;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.avatar-wrapper {
  position: relative;
  margin-right: 30rpx;
  flex-shrink: 0;

  .user-avatar {
    width: 140rpx;
    height: 140rpx;
    border-radius: $border-radius-round;
    border: 4rpx solid rgba(255, 255, 255, 0.8);
    background: rgba(255, 255, 255, 0.1);
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2);
    transition: transform 0.2s ease;

    &:active {
      transform: scale(0.95);
    }
  }
}

.user-info-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 44rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.user-title {
  font-size: 28rpx;
  opacity: 0.9;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.9);
}

// 编辑按钮
.edit-icon-wrapper {
  .btn-content {
    display: inline-flex;
    align-items: center;
    gap: 12rpx;
    background: rgba(255, 255, 255, 0.15);
    padding: 16rpx 28rpx;
    border-radius: 50rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.2);
    transition: all 0.2s ease;

    &:active {
      background: rgba(255, 255, 255, 0.25);
      transform: scale(0.96);
    }

    .btn-icon {
      font-size: 28rpx;
    }

    .btn-text {
      font-size: 26rpx;
      font-weight: 500;
      color: white;
    }
  }
}

// 管理卡片区域
.management-section {
  margin: -20rpx $margin-base $margin-base;
  padding: 40rpx 30rpx;
  border-radius: 24rpx;
  background: $background-color-white;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.08);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 40rpx;
  }

  .section-title {
    font-size: 36rpx;
    font-weight: 700;
    color: $text-primary;
  }

  .section-subtitle {
    font-size: 24rpx;
    color: $text-secondary;
    opacity: 0.8;
  }
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.stat-card {
  .stat-card-inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30rpx 0;
    background: $background-color;
    border-radius: 16rpx;
    transition: all 0.2s ease;
    border: 1rpx solid rgba(0, 0, 0, 0.05);

    &:active {
      background: color.adjust($background-color, $lightness: -5%);
      transform: translateY(-2rpx);
    }
  }

  .stat-icon {
    font-size: 48rpx;
    margin-bottom: 16rpx;
    display: block;
  }

  .stat-title {
    font-size: 24rpx;
    color: $text-primary;
    font-weight: 500;
    text-align: center;
    line-height: 1.3;
  }
}

// 设置区域
.settings-section {
  margin: 0 $margin-base;
  padding: 40rpx 30rpx;
  border-radius: 24rpx;
  background: $background-color-white;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.08);

  .section-header {
    margin-bottom: 30rpx;
  }

  .section-title {
    font-size: 36rpx;
    font-weight: 700;
    color: $text-primary;
  }
}

.menu-list {
  display: flex;
  flex-direction: column;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 16rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background: $background-color-white;
  border-bottom: 1rpx solid rgba(0, 0, 0, 0.05);
  transition: background-color 0.2s ease;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }

  &:last-child {
    border-bottom: none;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.menu-icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon {
  font-size: 32rpx;
}

.menu-text {
  font-size: 30rpx;
  color: $text-primary;
  font-weight: 500;
}

.menu-arrow {
  font-size: 40rpx;
  color: rgba(0, 0, 0, 0.3);
  font-weight: 300;
}

// 响应式调整
@media (max-width: 400rpx) {
  .user-avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .avatar-wrapper {
    margin-right: 0;
    margin-bottom: 20rpx;
  }

  .user-info-wrapper {
    align-items: center;
  }

  .stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;
  }
}
</style>