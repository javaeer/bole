<!--components/resumes/UserBasicInfo.vue-->
<template>
  <view class="user-basic-info">
    <!-- 头像和基本信息 -->
    <view class="basic-section" :style="basicSectionStyle">
      <!-- 头像 -->
      <view v-if="showAvatar" class="avatar-section">
        <image
          v-if="avatarUrl"
          :src="avatarUrl"
          class="avatar"
          mode="aspectFill"
          :style="avatarStyle"
        />
        <view v-else class="avatar-placeholder" :style="avatarStyle">
          <text>👤</text>
        </view>
      </view>

      <!-- 基本信息 -->
      <view class="info-section">
        <view class="name-row">
          <text class="name" :style="nameStyle">{{ userInfo.name || '张三' }}</text>
          <text v-if="userInfo.title" class="title" :style="titleStyle">
            {{ userInfo.title }}
          </text>
        </view>

        <view class="contact-info">
          <view v-if="userInfo.phone" class="contact-item">
            <text class="icon">📱</text>
            <text class="text">{{ userInfo.phone }}</text>
          </view>

          <view v-if="userInfo.email" class="contact-item">
            <text class="icon">📧</text>
            <text class="text">{{ userInfo.email }}</text>
          </view>

          <view v-if="userInfo.location" class="contact-item">
            <text class="icon">📍</text>
            <text class="text">{{ userInfo.location }}</text>
          </view>

          <view v-if="userInfo.workYears" class="contact-item">
            <text class="icon">👔</text>
            <text class="text">{{ userInfo.workYears }}年工作经验</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 其他信息 -->
    <view v-if="hasOtherInfo" class="other-info-section">
      <view v-if="userInfo.gender !== undefined" class="info-item">
        <text class="label">性别：</text>
        <text class="value">{{ userInfo.gender === 0 ? '女' : '男' }}</text>
      </view>

      <view v-if="userInfo.github" class="info-item">
        <text class="label">GitHub：</text>
        <text class="value">{{ userInfo.github }}</text>
      </view>

      <view v-if="userInfo.wechat" class="info-item">
        <text class="label">微信：</text>
        <text class="value">{{ userInfo.wechat }}</text>
      </view>

      <view v-if="userInfo.website" class="info-item">
        <text class="label">个人网站：</text>
        <text class="value">{{ userInfo.website }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  componentData: any;
  globalStyle?: any;
}

const props = defineProps<Props>();

const userInfo = computed(() => props.componentData.props || {});
const showAvatar = computed(() => userInfo.value.showAvatar !== false);
const avatarUrl = computed(() => userInfo.value.avatar);

const hasOtherInfo = computed(() => {
  return userInfo.value.gender !== undefined ||
    userInfo.value.github ||
    userInfo.value.wechat ||
    userInfo.value.website;
});

const basicSectionStyle = computed(() => ({
  display: 'flex',
  alignItems: 'flex-start',
  gap: '20px',
  marginBottom: '16px'
}));

const avatarStyle = computed(() => ({
  width: '80px',
  height: '80px',
  borderRadius: '50%',
  border: `2px solid ${props.globalStyle.secondaryColor || '#f0f0f0'}`
}));

const nameStyle = computed(() => ({
  fontSize: '20px',
  fontWeight: 'bold',
  color: '#333',
  marginRight: '12px'
}));

const titleStyle = computed(() => ({
  color: props.globalStyle.primaryColor || '#5ac8fa',
  fontSize: '14px',
  backgroundColor: `${props.globalStyle.primaryColor || '#5ac8fa'}15`,
  padding: '4px 12px',
  borderRadius: '12px'
}));
</script>

<style scoped>
.user-basic-info {
  width: 100%;
}

.name-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon {
  width: 16px;
  text-align: center;
}

.text {
  color: #666;
  font-size: 14px;
}

.other-info-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.label {
  color: #999;
  font-size: 13px;
}

.value {
  color: #666;
  font-size: 13px;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  font-size: 36px;
}

/* 移动端响应式优化 */
@media (max-width: 768px) {
  .basic-section {
    flex-direction: column !important;
    align-items: center !important;
    text-align: center !important;
    gap: 16px !important;
  }

  .name-row {
    justify-content: center !important;
  }

  .contact-info {
    align-items: center !important;
  }

  .other-info-section {
    justify-content: center !important;
    text-align: center !important;
  }

  .info-item {
    justify-content: center !important;
  }

  /* 头像调整 */
  .avatar-section {
    display: flex;
    justify-content: center;
  }

  .avatar,
  .avatar-placeholder {
    width: 80px !important;
    height: 80px !important;
  }
}
</style>