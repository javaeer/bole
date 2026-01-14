<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :show-header="showTitle"
    :custom-title="title"
	:responsive-center="true"
	:max-width="maxWidth" 
  >
    <template #default="{ styles }">
      <view class="user-basic-info" :style="containerStyle">
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
              <text class="name" :style="nameStyle">{{ displayName }}</text>
              <text v-if="showTitle && userInfo.title" class="title-tag" :style="titleTagStyle">
                {{ userInfo.title }}
              </text>
            </view>

            <view class="contact-info">
              <view v-if="showPhone && userInfo.phone" class="contact-item">
                <text class="icon">📱</text>
                <text class="contact-text">{{ formatPhone(userInfo.phone) }}</text>
              </view>

              <view v-if="showEmail && userInfo.email" class="contact-item">
                <text class="icon">📧</text>
                <text class="contact-text">{{ userInfo.email }}</text>
              </view>

              <view v-if="showLocation && userInfo.location" class="contact-item">
                <text class="icon">📍</text>
                <text class="contact-text">{{ userInfo.location }}</text>
              </view>

              <view v-if="showWorkYears && userInfo.workYears" class="contact-item">
                <text class="icon">👔</text>
                <text class="contact-text">{{ userInfo.workYears }}年工作经验</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 其他信息 -->
        <view v-if="hasOtherInfo" class="other-info-section" :style="otherInfoStyle">
          <view v-if="showGender && userInfo.gender !== undefined" class="info-item">
            <text class="label">性别：</text>
            <text class="value">{{ getGenderText(userInfo.gender) }}</text>
          </view>

          <view v-if="showBirthday && userInfo.birthday" class="info-item">
            <text class="label">生日：</text>
            <text class="value">{{ formatDate(userInfo.birthday) }}</text>
          </view>

          <view v-if="showGithub && userInfo.github" class="info-item">
            <text class="label">GitHub：</text>
            <text class="value">{{ userInfo.github }}</text>
          </view>

          <view v-if="showWechat && userInfo.wechat" class="info-item">
            <text class="label">微信：</text>
            <text class="value">{{ userInfo.wechat }}</text>
          </view>

          <view v-if="showWebsite && userInfo.website" class="info-item">
            <text class="label">个人网站：</text>
            <text class="value">{{ userInfo.website }}</text>
          </view>
        </view>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import BaseComponent from './BaseComponent.vue';

interface Props {
  componentData: any;
  globalStyle?: any;
  // 新增：是否启用居中布局
  responsiveCenter?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  responsiveCenter: false
});

// ===================== 计算属性 =====================

// 用户信息
const userInfo = computed(() => props.componentData?.props || {});

// 配置选项（从defaultConfig.props中获取）
const configProps = computed(() => props.componentData?.defaultConfig?.props || {});

// 显示控制
const showAvatar = computed(() => userInfo.value.showAvatar ?? configProps.value.showAvatar ?? true);
const showTitle = computed(() => configProps.value.showTitle ?? true);
const showName = computed(() => configProps.value.showName ?? true);
const showPhone = computed(() => configProps.value.showPhone ?? true);
const showEmail = computed(() => configProps.value.showEmail ?? true);
const showGender = computed(() => configProps.value.showGender ?? true);
const showBirthday = computed(() => configProps.value.showBirthday ?? true);
const showLocation = computed(() => configProps.value.showLocation ?? true);
const showWorkYears = computed(() => configProps.value.showWorkYears ?? true);
const showGithub = computed(() => configProps.value.showGithub ?? true);
const showWechat = computed(() => configProps.value.showWechat ?? true);
const showWebsite = computed(() => configProps.value.showWebsite ?? true);

// 组件标题
const title = computed(() => configProps.value.title || '基本信息');

// 显示名称
const displayName = computed(() => {
  if (!showName.value) return '';
  return userInfo.value.name || '张三';
});

// 头像URL
const avatarUrl = computed(() => userInfo.value.avatar);

// 是否有其他信息
const hasOtherInfo = computed(() => {
  return (showGender.value && userInfo.value.gender !== undefined) ||
    (showBirthday.value && userInfo.value.birthday) ||
    (showGithub.value && userInfo.value.github) ||
    (showWechat.value && userInfo.value.wechat) ||
    (showWebsite.value && userInfo.value.website);
});

// 头像尺寸
const avatarSize = computed(() => {
  const size = configProps.value.avatarSize || 'medium';
  const sizes: Record<string, string> = {
    small: '60px',
    medium: '80px',
    large: '100px'
  };
  return sizes[size] || sizes.medium;
});

// 是否启用居中布局
const shouldCenter = computed(() => {
  // 优先使用传入的props，否则从配置中读取
  if (props.responsiveCenter !== undefined) return props.responsiveCenter;
  return configProps.value.responsiveCenter ?? false;
});

// ===================== 样式计算 =====================

// 容器样式
const containerStyle = computed(() => ({
  width: '100%'
}));

// 基本部分样式
const basicSectionStyle = computed(() => ({
  display: 'flex',
  alignItems: 'flex-start',
  gap: '20px',
  marginBottom: '16px'
}));

// 其他信息部分样式
const otherInfoStyle = computed(() => ({
  display: 'flex',
  flexWrap: 'wrap',
  gap: '16px 24px',
  paddingTop: '16px',
  borderTop: `1px dashed var(--base-secondary-color, #eee)`
}));

// 头像样式
const avatarStyle = computed(() => {
  const border = configProps.value.avatarBorder || `2px solid var(--base-secondary-color, #f0f0f0)`;
  return {
    width: avatarSize.value,
    height: avatarSize.value,
    borderRadius: '50%',
    border: border,
    objectFit: 'cover'
  };
});

// 姓名样式
const nameStyle = computed(() => ({
  fontSize: '20px',
  fontWeight: 'bold',
  color: 'var(--base-text-color, #333)',
  marginRight: '12px'
}));

// 职称标签样式
const titleTagStyle = computed(() => ({
  color: 'var(--base-primary-color, #5ac8fa)',
  fontSize: '12px',
  backgroundColor: `color-mix(in srgb, var(--base-primary-color, #5ac8fa) 15%, transparent)`,
  padding: '2px 10px',
  borderRadius: '10px',
  fontWeight: '500'
}));

// ===================== 工具函数 =====================

// 格式化电话号码
const formatPhone = (phone: string) => {
  if (!phone) return '';
  // 简单的手机号脱敏处理
  if (phone.length === 11) {
    return `${phone.slice(0, 3)}****${phone.slice(7)}`;
  }
  return phone;
};

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 获取性别文本
const getGenderText = (gender: number) => {
  switch (gender) {
    case 0: return '女';
    case 1: return '男';
    default: return '未填写';
  }
};
</script>

<style scoped lang="scss">
.user-basic-info {
  width: 100%;
}

.basic-section {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 16px;
  
  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 16px;
  }
}

.avatar-section {
  flex-shrink: 0;
  
  @media (max-width: 768px) {
    display: flex;
    justify-content: center;
    width: 100%;
  }
}

.info-section {
  flex: 1;
  min-width: 0; // 防止内容溢出
  
  @media (max-width: 768px) {
    width: 100%;
    text-align: center;
  }
}

.name-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
  
  @media (max-width: 768px) {
    justify-content: center;
  }
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
  
  @media (max-width: 768px) {
    justify-content: center;
  }
}

.icon {
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.contact-text {
  color: var(--base-text-color, #666);
  font-size: 14px;
  word-break: break-word;
}

.other-info-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 24px;
  padding-top: 16px;
  border-top: 1px dashed var(--base-secondary-color, #eee);
  
  @media (max-width: 768px) {
    justify-content: center;
    text-align: center;
    gap: 12px;
  }
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
  
  @media (max-width: 768px) {
    justify-content: center;
  }
}

.label {
  color: var(--base-secondary-color, #999);
  font-size: 13px;
  flex-shrink: 0;
}

.value {
  color: var(--base-text-color, #666);
  font-size: 13px;
  word-break: break-word;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--base-secondary-color, #f5f5f5);
  font-size: 32px;
  color: var(--base-text-color, #999);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .avatar,
  .avatar-placeholder {
    width: 80px !important;
    height: 80px !important;
  }
  
  .name {
    font-size: 18px !important;
  }
  
  .title-tag {
    font-size: 11px !important;
    padding: 2px 8px !important;
  }
  
  .contact-text,
  .label,
  .value {
    font-size: 13px !important;
  }
}

/* 打印样式 */
@media print {
  .user-basic-info {
    break-inside: avoid;
  }
  
  .avatar-placeholder {
    background-color: #f0f0f0 !important;
  }
  
  .contact-text,
  .label,
  .value {
    color: #333 !important;
  }
  
  /* 确保打印时内容居中 */
  .basic-section,
  .other-info-section {
    justify-content: center !important;
  }
}
</style>