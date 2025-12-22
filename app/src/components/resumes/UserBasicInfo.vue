<template>
  <view :class="['user-basic-info', `layout-${layout}`, `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view v-if="showTitle" class="section-header">
      <text class="section-title">{{ title }}</text>
      <view class="section-divider"></view>
    </view>

    <!-- 卡片布局 -->
    <view v-if="layout === 'card'" class="info-card">
      <!-- 头像和基本信息 -->
      <view class="basic-info-row">
        <!-- 头像 -->
        <view v-if="showAvatar" class="avatar-container">
          <image
            v-if="avatar"
            :src="avatar"
            class="avatar-image"
            mode="aspectFill"
          />
          <view v-else class="avatar-placeholder">
            <text class="avatar-text">{{ nameInitial }}</text>
          </view>
        </view>

        <!-- 基本信息 -->
        <view class="basic-info-content">
          <view class="name-row">
            <text class="name">{{ name }}</text>
            <view v-if="showBadges" class="badges">
              <text v-if="showGender && gender" class="badge gender">
                {{ gender }}
              </text>
              <text v-if="showTitle && jobTitle" class="badge title">
                {{ jobTitle }}
              </text>
            </view>
          </view>

          <view v-if="showContact && (phone || email || location)" class="contact-info">
            <text v-if="phone && showPhone" class="contact-item">
              <text class="icon">📱</text>
              <text class="value">{{ phone }}</text>
            </text>
            <text v-if="email && showEmail" class="contact-item">
              <text class="icon">✉️</text>
              <text class="value">{{ email }}</text>
            </text>
            <text v-if="location && showLocation" class="contact-item">
              <text class="icon">📍</text>
              <text class="value">{{ location }}</text>
            </text>
          </view>
        </view>
      </view>

      <!-- 详细信息 -->
      <view v-if="showDetails" class="detailed-info">
        <view v-if="showWorkYears && workYears" class="info-item">
          <text class="label">工作经验：</text>
          <text class="value">{{ workYears }}年</text>
        </view>
        <view v-if="birthday" class="info-item">
          <text class="label">出生日期：</text>
          <text class="value">{{ birthday }}</text>
        </view>
        <view v-if="website" class="info-item">
          <text class="label">个人网站：</text>
          <text class="value link" @click="openLink(website)">{{ website }}</text>
        </view>
        <view v-if="github" class="info-item">
          <text class="label">GitHub：</text>
          <text class="value">{{ github }}</text>
        </view>
        <view v-if="wechat" class="info-item">
          <text class="label">微信：</text>
          <text class="value">{{ wechat }}</text>
        </view>
      </view>
    </view>

    <!-- 简洁布局 -->
    <view v-else class="info-simple">
      <view class="simple-header">
        <text v-if="showAvatar" class="simple-avatar">
          <image
            v-if="avatar"
            :src="avatar"
            class="avatar-image"
            mode="aspectFill"
          />
          <text v-else class="avatar-text">{{ nameInitial }}</text>
        </text>
        <text class="simple-name">{{ name }}</text>
      </view>
      <view class="simple-contact">
        <text v-if="phone">{{ phone }}</text>
        <text v-if="email"> | {{ email }}</text>
        <text v-if="location"> | {{ location }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  config: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'modern'
  }
})

// 提取配置
const componentProps = computed(() => props.config.props || {})
const componentStyles = computed(() => props.config.styles || {})
const defaultConfig = computed(() => props.config.defaultConfig || {})

// 标题
const title = computed(() => componentProps.value.title || defaultConfig.value.props?.title || '基本信息')

// 用户数据
const name = computed(() => componentProps.value.name || '')
const gender = computed(() => componentProps.value.gender || '')
const birthday = computed(() => componentProps.value.birthday || '')
const phone = computed(() => componentProps.value.phone || '')
const email = computed(() => componentProps.value.email || '')
const location = computed(() => componentProps.value.location || '')
const workYears = computed(() => componentProps.value.workYears || 0)
const website = computed(() => componentProps.value.website || '')
const github = computed(() => componentProps.value.github || '')
const wechat = computed(() => componentProps.value.wechat || '')
const avatar = computed(() => componentProps.value.avatar || '')
const jobTitle = computed(() => componentProps.value.title || '')

// 显示选项
const showTitle = computed(() => componentProps.value.showTitle ?? true)
const showAvatar = computed(() => componentProps.value.showAvatar ?? defaultConfig.value.props?.showAvatar ?? true)
const showGender = computed(() => componentProps.value.showGender ?? defaultConfig.value.props?.showGender ?? true)
const showBirthday = computed(() => componentProps.value.showBirthday ?? defaultConfig.value.props?.showBirthday ?? true)
const showPhone = computed(() => componentProps.value.showPhone ?? defaultConfig.value.props?.showPhone ?? true)
const showEmail = computed(() => componentProps.value.showEmail ?? defaultConfig.value.props?.showEmail ?? true)
const showLocation = computed(() => componentProps.value.showLocation ?? defaultConfig.value.props?.showLocation ?? true)
const showWorkYears = computed(() => componentProps.value.showWorkYears ?? defaultConfig.value.props?.showWorkYears ?? true)
const showContact = computed(() => componentProps.value.showContact ?? true)

// 布局
const layout = computed(() => componentProps.value.layout || componentStyles.value.layout || 'card')

// 姓名首字母
const nameInitial = computed(() => {
  const userName = name.value || ''
  return userName.charAt(0).toUpperCase() || '?'
})

// 样式相关
const computedStyle = computed(() => ({
  '--primary-color': componentStyles.value.primaryColor || '#d4af37',
  '--background-color': componentStyles.value.backgroundColor || '#ffffff',
  '--border-color': componentStyles.value.borderColor || '#f0f0f0'
}))

// 是否显示徽章
const showBadges = computed(() => showGender.value || jobTitle.value)
// 是否显示详细信息
const showDetails = computed(() => birthday.value || workYears.value || website.value || github.value || wechat.value)

// 打开链接
const openLink = (url) => {
  if (url) {
    uni.navigateTo({
      url: `/pages/webview/webview?url=${encodeURIComponent(url)}`
    })
  }
}

// 组件加载日志
console.log('用户基本信息组件加载完成', {
  姓名: name.value,
  邮箱: email.value,
  电话: phone.value,
  配置: props.config
})
</script>

<style lang="scss" scoped>
.user-basic-info {
  margin-bottom: 40rpx;

  &.theme-modern {
    .section-title {
      color: #d4af37;
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, #d4af37, #f7ef8a);
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .info-card {
      background: #ffffff;
      border-radius: 16rpx;
      padding: 30rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid #f0f0f0;
    }

    .name {
      color: #333;
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 8rpx;
    }

    .badge {
      padding: 4rpx 12rpx;
      border-radius: 20rpx;
      font-size: 20rpx;
      margin-left: 12rpx;

      &.gender {
        background-color: rgba(212, 175, 55, 0.1);
        color: #d4af37;
        border: 1rpx solid rgba(212, 175, 55, 0.2);
      }

      &.title {
        background-color: rgba(64, 158, 255, 0.1);
        color: #409eff;
        border: 1rpx solid rgba(64, 158, 255, 0.2);
      }
    }
  }

  .basic-info-row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24rpx;
  }

  .avatar-container {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 24rpx;
    flex-shrink: 0;

    .avatar-image {
      width: 100%;
      height: 100%;
    }

    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #d4af37, #f7ef8a);
      display: flex;
      align-items: center;
      justify-content: center;

      .avatar-text {
        color: white;
        font-size: 36rpx;
        font-weight: 600;
      }
    }
  }

  .basic-info-content {
    flex: 1;
  }

  .name-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 12rpx;
  }

  .contact-info {
    display: flex;
    flex-direction: column;
    gap: 12rpx;

    .contact-item {
      display: flex;
      align-items: center;
      color: #666;
      font-size: 26rpx;

      .icon {
        margin-right: 8rpx;
        font-size: 24rpx;
        width: 32rpx;
        text-align: center;
      }

      .value {
        word-break: break-all;
      }
    }
  }

  .detailed-info {
    border-top: 1rpx solid #f0f0f0;
    padding-top: 20rpx;

    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #666;
        font-size: 24rpx;
        min-width: 120rpx;
      }

      .value {
        color: #333;
        font-size: 24rpx;
        flex: 1;
        word-break: break-all;

        &.link {
          color: #409eff;
          text-decoration: underline;
          cursor: pointer;

          &:active {
            opacity: 0.7;
          }
        }
      }
    }
  }

  // 简洁布局
  .info-simple {
    .simple-header {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      .simple-avatar {
        width: 60rpx;
        height: 60rpx;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 16rpx;
        flex-shrink: 0;
        background: linear-gradient(135deg, #d4af37, #f7ef8a);
        display: flex;
        align-items: center;
        justify-content: center;

        .avatar-image {
          width: 100%;
          height: 100%;
        }

        .avatar-text {
          color: white;
          font-size: 24rpx;
          font-weight: 600;
        }
      }

      .simple-name {
        color: #333;
        font-size: 32rpx;
        font-weight: 600;
      }
    }

    .simple-contact {
      color: #666;
      font-size: 26rpx;
    }
  }
}
</style>