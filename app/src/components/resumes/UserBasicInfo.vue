<template>
  <view :class="['user-basic-info', `layout-${layout}`, `theme-${theme}`]" :style="mergedStyle">
    <!-- 区块标题 -->
    <view v-if="showTitle" class="section-header">
      <text class="section-title" :style="titleStyle">{{ title }}</text>
      <view class="section-divider" :style="dividerStyle"></view>
    </view>

    <!-- 卡片布局 -->
    <view v-if="layout === 'card'" class="info-card" :style="cardStyle">
      <!-- 头像和基本信息 -->
      <view class="basic-info-row">
        <!-- 头像 -->
        <view v-if="showAvatar" class="avatar-container" :style="avatarContainerStyle">
          <image
            v-if="avatar"
            :src="avatar"
            class="avatar-image"
            mode="aspectFill"
            :style="avatarImageStyle"
          />
          <view v-else class="avatar-placeholder" :style="avatarPlaceholderStyle">
            <text class="avatar-text">{{ nameInitial }}</text>
          </view>
        </view>

        <!-- 基本信息 -->
        <view class="basic-info-content">
          <view class="name-row">
            <text class="name" :style="nameStyle">{{ name }}</text>
            <view v-if="showBadges" class="badges">
              <text v-if="showGender && gender" class="badge gender" :style="genderBadgeStyle">
                {{ gender }}
              </text>
              <text v-if="jobTitle" class="badge title" :style="titleBadgeStyle">
                {{ jobTitle }}
              </text>
            </view>
          </view>

          <view v-if="showContact && (phone || email || location)" class="contact-info">
            <text v-if="phone && showPhone" class="contact-item">
              <text class="icon">📱</text>
              <text class="value" :style="contactValueStyle">{{ phone }}</text>
            </text>
            <text v-if="email && showEmail" class="contact-item">
              <text class="icon">✉️</text>
              <text class="value" :style="contactValueStyle">{{ email }}</text>
            </text>
            <text v-if="location && showLocation" class="contact-item">
              <text class="icon">📍</text>
              <text class="value" :style="contactValueStyle">{{ location }}</text>
            </text>
          </view>
        </view>
      </view>

      <!-- 详细信息 -->
      <view v-if="showDetails" class="detailed-info" :style="detailedInfoStyle">
        <view v-if="showWorkYears && workYears" class="info-item">
          <text class="label" :style="labelStyle">工作经验：</text>
          <text class="value" :style="valueStyle">{{ workYears }}年</text>
        </view>
        <view v-if="birthday" class="info-item">
          <text class="label" :style="labelStyle">出生日期：</text>
          <text class="value" :style="valueStyle">{{ formatDate(birthday) }}</text>
        </view>
        <view v-if="website" class="info-item">
          <text class="label" :style="labelStyle">个人网站：</text>
          <text class="value link" :style="linkStyle" @click="openLink(website)">{{ formatUrl(website) }}</text>
        </view>
        <view v-if="github" class="info-item">
          <text class="label" :style="labelStyle">GitHub：</text>
          <text class="value" :style="valueStyle">{{ github }}</text>
        </view>
        <view v-if="wechat" class="info-item">
          <text class="label" :style="labelStyle">微信：</text>
          <text class="value" :style="valueStyle">{{ wechat }}</text>
        </view>
      </view>
    </view>

    <!-- 简洁布局 -->
    <view v-else class="info-simple" :style="simpleStyle">
      <view class="simple-header">
        <view v-if="showAvatar" class="simple-avatar" :style="simpleAvatarStyle">
          <image
            v-if="avatar"
            :src="avatar"
            class="avatar-image"
            mode="aspectFill"
            :style="avatarImageStyle"
          />
          <text v-else class="avatar-text">{{ nameInitial }}</text>
        </view>
        <text class="simple-name" :style="simpleNameStyle">{{ name }}</text>
      </view>
      <view class="simple-contact">
        <text v-if="phone" :style="contactValueStyle">{{ phone }}</text>
        <text v-if="email" :style="contactValueStyle"> | {{ email }}</text>
        <text v-if="location" :style="contactValueStyle"> | {{ location }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, watch } from 'vue'

const props = defineProps({
  component: {
    type: Object,
    default: () => ({})
  },
  globalStyle: {
    type: Object,
    default: () => ({})
  },
  theme: {
    type: String,
    default: 'modern'
  }
})

// 🚀 从props中提取样式配置
const componentStyles = computed(() => props.component?.styles || {})
const defaultConfig = computed(() => props.component?.defaultConfig || {})
const defaultProps = computed(() => defaultConfig.value?.props || {})
const defaultStyles = computed(() => defaultConfig.value?.styles || {})

// 合并后的样式（自定义样式 > 默认样式）
const mergedStyles = computed(() => ({
  ...defaultStyles.value,
  ...componentStyles.value
}))

// 用户数据
const name = computed(() => props.component?.props?.name || '')
const gender = computed(() => {
  const genderValue = props.component?.props?.gender
  if (genderValue === 1) return '男'
  if (genderValue === 0) return '女'
  return genderValue || ''
})
const birthday = computed(() => props.component?.props?.birthday || '')
const phone = computed(() => props.component?.props?.phone || '')
const email = computed(() => props.component?.props?.email || '')
const location = computed(() => props.component?.props?.location || '')
const workYears = computed(() => props.component?.props?.workYears || 0)
const website = computed(() => props.component?.props?.website || '')
const github = computed(() => props.component?.props?.github || '')
const wechat = computed(() => props.component?.props?.wechat || '')
const avatar = computed(() => props.component?.props?.avatar || '')
const jobTitle = computed(() => props.component?.props?.title || props.component?.props?.jobTitle || '')

// 🚀 显示选项 - 合并默认配置和组件配置
const showTitle = computed(() =>
  props.component?.props?.showTitle ?? defaultProps.value?.showTitle ?? true
)
const showAvatar = computed(() =>
  props.component?.props?.showAvatar ?? defaultProps.value?.showAvatar ?? true
)
const showGender = computed(() =>
  props.component?.props?.showGender ?? defaultProps.value?.showGender ?? true
)
const showBirthday = computed(() =>
  props.component?.props?.showBirthday ?? defaultProps.value?.showBirthday ?? true
)
const showPhone = computed(() =>
  props.component?.props?.showPhone ?? defaultProps.value?.showPhone ?? true
)
const showEmail = computed(() =>
  props.component?.props?.showEmail ?? defaultProps.value?.showEmail ?? true
)
const showLocation = computed(() =>
  props.component?.props?.showLocation ?? defaultProps.value?.showLocation ?? true
)
const showWorkYears = computed(() =>
  props.component?.props?.showWorkYears ?? defaultProps.value?.showWorkYears ?? true
)
const showContact = computed(() =>
  props.component?.props?.showContact ?? defaultProps.value?.showContact ?? true
)

// 标题
const title = computed(() =>
  props.component?.props?.title || defaultProps.value?.title || '基本信息'
)

// 🚀 布局 - 从样式或默认配置中获取
const layout = computed(() => {
  return mergedStyles.value.layout ||
    defaultProps.value?.layout ||
    'card'
})

// 姓名首字母
const nameInitial = computed(() => {
  const userName = name.value || ''
  return userName.charAt(0).toUpperCase() || '?'
})

// 🚀 样式计算
const mergedStyle = computed(() => {
  const baseStyle = {
    marginBottom: props.globalStyle?.spacing?.sectionMargin || '24px'
  }

  // 应用合并后的组件样式
  const componentStyle = { ...mergedStyles.value }

  // 移除可能在子元素中使用的样式
  delete componentStyle.layout
  delete componentStyle.titleColor
  delete componentStyle.fieldColor
  delete componentStyle.avatarBorder
  delete componentStyle.borderRadius
  delete componentStyle.backgroundColor

  return {
    ...baseStyle,
    ...componentStyle
  }
})

// 🚀 卡片样式
const cardStyle = computed(() => ({
  padding: mergedStyles.value.padding || '20px',
  borderRadius: mergedStyles.value.borderRadius || '8px',
  backgroundColor: mergedStyles.value.backgroundColor || '#FFFFFF',
  boxShadow: mergedStyles.value.boxShadow || '0 2rpx 8rpx rgba(0,0,0,0.1)'
}))

// 🚀 标题样式
const titleStyle = computed(() => ({
  color: mergedStyles.value.titleColor ||
    props.globalStyle?.primaryColor ||
    '#2c3e50',
  fontSize: props.globalStyle?.fontSizes?.h1 ?
    `${props.globalStyle.fontSizes.h1}px` :
    '36rpx',
  fontWeight: 'bold',
  marginBottom: '16rpx',
  display: 'block'
}))

// 🚀 分隔线样式
const dividerStyle = computed(() => ({
  height: '2rpx',
  background: `linear-gradient(90deg, ${props.globalStyle?.primaryColor || '#2c3e50'}, ${
    props.globalStyle?.accentColor || '#2c3e50'
  })`,
  marginBottom: '30rpx',
  width: '80rpx'
}))

// 🚀 头像容器样式
const avatarContainerStyle = computed(() => ({
  width: '120rpx',
  height: '120rpx',
  borderRadius: '50%',
  overflow: 'hidden',
  marginRight: '24rpx',
  flexShrink: 0,
  border: mergedStyles.value.avatarBorder || '2rpx solid #e8e8e8'
}))

// 🚀 头像图片样式
const avatarImageStyle = computed(() => ({
  width: '100%',
  height: '100%',
  objectFit: 'cover'
}))

// 🚀 头像占位符样式
const avatarPlaceholderStyle = computed(() => ({
  width: '100%',
  height: '100%',
  background: `linear-gradient(135deg, ${props.globalStyle?.primaryColor || '#2c3e50'}, ${
    props.globalStyle?.accentColor || '#2c3e50'
  })`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
}))

// 🚀 姓名样式
const nameStyle = computed(() => ({
  color: '#333',
  fontSize: '36rpx',
  fontWeight: '600',
  marginBottom: '8rpx'
}))

// 🚀 性别徽章样式
const genderBadgeStyle = computed(() => ({
  padding: '4rpx 12rpx',
  borderRadius: '20rpx',
  fontSize: '20rpx',
  marginLeft: '12rpx',
  backgroundColor: `rgba(${hexToRgb(props.globalStyle?.primaryColor || '#2c3e50')}, 0.1)`,
  color: props.globalStyle?.primaryColor || '#2c3e50',
  border: `1rpx solid rgba(${hexToRgb(props.globalStyle?.primaryColor || '#2c3e50')}, 0.2)`
}))

// 🚀 职位徽章样式
const titleBadgeStyle = computed(() => ({
  padding: '4rpx 12rpx',
  borderRadius: '20rpx',
  fontSize: '20rpx',
  marginLeft: '12rpx',
  backgroundColor: `rgba(${hexToRgb(props.globalStyle?.accentColor || '#2c3e50')}, 0.1)`,
  color: props.globalStyle?.accentColor || '#2c3e50',
  border: `1rpx solid rgba(${hexToRgb(props.globalStyle?.accentColor || '#2c3e50')}, 0.2)`
}))

// 🚀 联系信息样式
const contactValueStyle = computed(() => ({
  color: mergedStyles.value.fieldColor || '#666',
  fontSize: props.globalStyle?.fontSizes?.body ?
    `${props.globalStyle.fontSizes.body}px` :
    '26rpx'
}))

// 🚀 详细信息容器样式
const detailedInfoStyle = computed(() => ({
  borderTop: '1rpx solid #f0f0f0',
  paddingTop: '20rpx',
  marginTop: '20rpx'
}))

// 🚀 标签样式
const labelStyle = computed(() => ({
  color: '#666',
  fontSize: '24rpx',
  minWidth: '120rpx'
}))

// 🚀 值样式
const valueStyle = computed(() => ({
  color: mergedStyles.value.fieldColor || '#333',
  fontSize: '24rpx',
  flex: 1,
  wordBreak: 'break-all'
}))

// 🚀 链接样式
const linkStyle = computed(() => ({
  color: props.globalStyle?.primaryColor || '#409eff',
  textDecoration: 'underline',
  cursor: 'pointer',
  '&:active': { opacity: 0.7 }
}))

// 🚀 简洁布局样式
const simpleStyle = computed(() => ({
  padding: mergedStyles.value.padding || '15px',
  backgroundColor: mergedStyles.value.backgroundColor || '#FFFFFF',
  borderRadius: mergedStyles.value.borderRadius || '8px'
}))

// 🚀 简洁头像样式
const simpleAvatarStyle = computed(() => ({
  width: '60rpx',
  height: '60rpx',
  borderRadius: '50%',
  overflow: 'hidden',
  marginRight: '16rpx',
  flexShrink: 0,
  border: mergedStyles.value.avatarBorder || '1rpx solid #e8e8e8'
}))

// 🚀 简洁姓名样式
const simpleNameStyle = computed(() => ({
  color: mergedStyles.value.titleColor || '#333',
  fontSize: props.globalStyle?.fontSizes?.h1 ?
    `${props.globalStyle.fontSizes.h1}px` :
    '32rpx',
  fontWeight: '600'
}))

// 是否显示徽章
const showBadges = computed(() => (showGender.value && gender.value) || jobTitle.value)
// 是否显示详细信息
const showDetails = computed(() => birthday.value || workYears.value || website.value || github.value || wechat.value)

// 🚀 辅助函数：十六进制颜色转RGB
const hexToRgb = (hex) => {
  if (!hex) return '44, 62, 80' // 默认颜色 #2c3e50

  let r = 0, g = 0, b = 0

  // 处理 #abc 格式
  if (hex.length === 4) {
    r = parseInt(hex[1] + hex[1], 16)
    g = parseInt(hex[2] + hex[2], 16)
    b = parseInt(hex[3] + hex[3], 16)
  }
  // 处理 #abcdef 格式
  else if (hex.length === 7) {
    r = parseInt(hex[1] + hex[2], 16)
    g = parseInt(hex[3] + hex[4], 16)
    b = parseInt(hex[5] + hex[6], 16)
  }

  return `${r}, ${g}, ${b}`
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''

  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN')
  } catch (e) {
    return dateStr
  }
}

// 格式化URL
const formatUrl = (url) => {
  if (!url) return ''
  return url.replace(/^https?:\/\//, '')
}

// 打开链接
const openLink = (url) => {
  if (url) {
    // 确保URL有协议
    const fullUrl = url.startsWith('http') ? url : `https://${url}`
    uni.navigateTo({
      url: `/pages/webview/webview?url=${encodeURIComponent(fullUrl)}`
    })
  }
}

// 🚀 调试日志
watch(() => props.component, (newVal) => {
  console.log('UserBasicInfo 组件更新:', {
    组件配置: newVal,
    合并后样式: mergedStyles.value,
    全局样式: props.globalStyle,
    标题样式: titleStyle.value,
    卡片样式: cardStyle.value
  })
}, { immediate: true, deep: true })
</script>


<style lang="scss" scoped>
.user-basic-info {
  margin-bottom: 40rpx;

  // 使用CSS变量
  --primary-color: #2c3e50;
  --accent-color: #2c3e50;
  --text-color: #333333;
  --field-color: #666666;
  --border-color: #f0f0f0;

  &.theme-modern {
    .section-title {
      color: var(--primary-color);
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
      margin-bottom: 30rpx;
      width: 80rpx;
    }

    .info-card {
      background: var(--background-color, #ffffff);
      border-radius: 16rpx;
      padding: 30rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      border: 1rpx solid var(--border-color, #f0f0f0);
    }

    .name {
      color: var(--text-color, #333);
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
        background-color: rgba(var(--primary-color-rgb, 44, 62, 80), 0.1);
        color: var(--primary-color, #2c3e50);
        border: 1rpx solid rgba(var(--primary-color-rgb, 44, 62, 80), 0.2);
      }

      &.title {
        background-color: rgba(var(--accent-color-rgb, 44, 62, 80), 0.1);
        color: var(--accent-color, #2c3e50);
        border: 1rpx solid rgba(var(--accent-color-rgb, 44, 62, 80), 0.2);
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
      background: linear-gradient(135deg, var(--primary-color, #2c3e50), var(--accent-color, #2c3e50));
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
      color: var(--field-color, #666);
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
    border-top: 1rpx solid var(--border-color, #f0f0f0);
    padding-top: 20rpx;

    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: var(--field-color, #666);
        font-size: 24rpx;
        min-width: 120rpx;
      }

      .value {
        color: var(--field-color, #333);
        font-size: 24rpx;
        flex: 1;
        word-break: break-all;

        &.link {
          color: var(--primary-color, #409eff);
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
        background: linear-gradient(135deg, var(--primary-color, #2c3e50), var(--accent-color, #2c3e50));
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
        color: var(--text-color, #333);
        font-size: 32rpx;
        font-weight: 600;
      }
    }

    .simple-contact {
      color: var(--field-color, #666);
      font-size: 26rpx;
    }
  }
}
</style>