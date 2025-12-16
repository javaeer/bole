<template>
  <view class="page-container">
    <!-- 用户信息头部 -->
    <view class="user-header">
      <view class="user-avatar-section">
        <image :src="userInfo.avatar" class="user-avatar" mode="aspectFit" @click="handleEditAvatar" />
        <view class="user-info">
          <text class="user-name">{{ userInfo.name }}</text>
          <text class="user-title">{{ userInfo.title }}</text>
          <view class="user-stats">
            <view class="stat-item">
              <text class="stat-number">{{ userInfo.followers }}</text>
              <text class="stat-label">关注</text>
            </view>
            <view class="stat-item">
              <text class="stat-number">{{ userInfo.fans }}</text>
              <text class="stat-label">粉丝</text>
            </view>
            <view class="stat-item">
              <text class="stat-number">{{ userInfo.likes }}</text>
              <text class="stat-label">获赞</text>
            </view>
          </view>
        </view>
      </view>
      <button class="btn-edit" @click="handleEditProfile">编辑资料</button>
    </view>

    <!-- 简历管理 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">简历管理</text>
        <text class="section-more" @click="handleViewAllResumes">查看全部</text>
      </view>
      <view class="resumes-stats">
        <view class="stat-card" @click="handleCreateResume">
          <text class="stat-icon">📝</text>
          <text class="stat-title">创建简历</text>
        </view>
        <view class="stat-card" @click="handleMyResumes">
          <text class="stat-number">{{ resumeStats.total }}</text>
          <text class="stat-title">我的简历</text>
        </view>
        <view class="stat-card" @click="handleViewedResumes">
          <text class="stat-number">{{ resumeStats.viewed }}</text>
          <text class="stat-title">被查看</text>
        </view>
        <view class="stat-card" @click="handleDownloadResumes">
          <text class="stat-number">{{ resumeStats.downloaded }}</text>
          <text class="stat-title">已下载</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="section">
      <view class="menu-list">
        <view class="menu-item" v-for="item in menuList" :key="item.id" @click="handleMenuClick(item)">
          <view class="menu-left">
            <text class="menu-icon">{{ item.icon }}</text>
            <text class="menu-text">{{ item.name }}</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 设置入口 -->
    <view class="section">
      <view class="menu-list">
        <view class="menu-item" @click="handleFeedback">
          <view class="menu-left">
            <text class="menu-icon">💬</text>
            <text class="menu-text">意见反馈</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleAbout">
          <view class="menu-left">
            <text class="menu-icon">ℹ️</text>
            <text class="menu-text">关于我们</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleLogout">
          <view class="menu-left">
            <text class="menu-icon">ℹ️</text>
            <text class="menu-text">退出登录</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import { getUserInfo } from "@/utils/store";
import FileAPI from "@/api/file";

const userStore = useUserStore();

const info = getUserInfo();

// 用户信息
const userInfo = ref({
  name: info.name,
  title: info.title,
  avatar: "/static/avatar/default-avatar.jpg",
  followers: 24,
  fans: 18,
  likes: 156,
});

// 简历统计
const resumeStats = ref({
  total: 3,
  viewed: 12,
  downloaded: 5,
});

// 菜单列表
const menuList = ref([
  { id: 1, name: "模板发布", icon: "📤", path: "/pages/template/create" },
  { id: 2, name: "浏览记录", icon: "👀", path: "/pages/history/history" },
  { id: 3, name: "申请管理", icon: "📅", path: "/pages/application/list" },
]);

// 事件处理
const handleEditAvatar = async () => {
  try {
    // 1. 调用系统接口选择图片
    const chooseRes = await uni.chooseImage({
      count: 1, // 最多选择数量，默认9
      sizeType: ["compressed"], // 指定为压缩图，可选['original', 'compressed']
      sourceType: ["album", "camera"], // 来源：相册和相机
    });

    const tempFilePath = chooseRes.tempFilePaths[0]; // 获取临时路径

    // 2. 调用上传接口
    const uploadResult = await FileAPI.upload({
      filePath: tempFilePath,
      formData: { userId: info.id }, // 额外的表单数据
      onProgress: (progress) => {
        console.log("上传进度:", progress);
      },
    });

    console.log("上传成功，服务器返回:", uploadResult);

    userInfo.value.avatar = uploadResult.accessUrl;
    uni.showToast({
      title: "头像更新成功",
      icon: "success",
    });

  } catch (error) {
    console.error("操作失败:", error);
    uni.showToast({ title: "上传失败", icon: "none" });
  }
};

const handleEditProfile = () => {
  uni.navigateTo({
    url: "/pages/profile/setting",
  });
};

const handleCreateResume = () => {
  uni.navigateTo({
    url: "/pages/resumes/create",
  });
};

const handleMyResumes = () => {
  uni.navigateTo({
    url: "/pages/resumes/list",
  });
};

const handleViewedResumes = () => {
  uni.navigateTo({
    url: "/pages/resumes/viewed",
  });
};

const handleDownloadResumes = () => {
  uni.navigateTo({
    url: "/pages/resumes/downloaded",
  });
};

const handleViewAllResumes = () => {
  uni.navigateTo({
    url: "/pages/resumes/list",
  });
};

const handleMenuClick = (item: any) => {
  uni.navigateTo({
    url: item.path,
  });
};

const handleFeedback = () => {
  uni.navigateTo({
    url: "/pages/feedback/feedback",
  });
};

const handleAbout = () => {
  uni.navigateTo({
    url: "/pages/about/about",
  });
};


// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: "确认退出",
    content: "确定要退出当前账号吗？",
    success: (res) => {
      if (res.confirm) {
        uni.showToast({
          title: "退出成功",
          icon: "success",
        });
        userStore.logout({ callApi: true });
        setTimeout(() => {
          uni.reLaunch({
            url: "/pages/index/index",
          });
        }, 1500);
      }
    },
  });
};

onMounted(() => {
  console.log("我的页面加载完成");
});
</script>

<style scoped lang="scss">
.page-container {
  background-color: $background-color;
  min-height: 100vh;
  padding-bottom: 50rpx;
}

.user-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding: 60rpx $padding-base 40rpx;
  color: $background-color-white;
  position: relative;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: $margin-base;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: $border-radius-round;
  border: 4rpx solid $background-color-white;
  margin-right: $margin-base;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  margin-bottom: 10rpx;
}

.user-title {
  display: block;
  font-size: 26rpx;
  opacity: 0.9;
  margin-bottom: 20rpx;
}

.user-stats {
  display: flex;
  gap: 40rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  margin-bottom: 5rpx;
}

.stat-label {
  font-size: 22rpx;
  opacity: 0.8;
}

.btn-edit {
  position: absolute;
  top: 60rpx;
  right: $padding-base;
  background: rgba($background-color-white, 0.2);
  color: $background-color-white;
  border: 2rpx solid $background-color-white;
  border-radius: 25rpx;
  padding: 12rpx $margin-base;
  font-size: $font-size-extra-small;
}

.section {
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.section-more {
  font-size: 26rpx;
  color: $primary-color;
}

.resumes-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $margin-small;
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base $margin-small;
  background: $background-color;
  border-radius: $border-radius;
  transition: all $transition-duration;
}

.stat-card:active {
  background: color.adjust($background-color, $lightness: - 5%);
  transform: scale(0.95);
}

.stat-icon,
.stat-number {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  margin-bottom: $margin-mini;
  color: $primary-color;
}

.stat-title {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-base 0;
  border-bottom: 1rpx solid $border-color-extra-light;
  transition: all $transition-duration;
}

.menu-item:active {
  background: $background-color;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: $font-size-medium;
  margin-right: 25rpx;
  width: 40rpx;
  text-align: center;
}

.menu-text {
  font-size: $font-size-base;
  color: $text-primary;
}

.menu-arrow {
  font-size: $font-size-medium;
  color: $text-placeholder;
}
</style>