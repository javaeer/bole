<template>
  <view class="about-container">
    <!-- 顶部品牌展示 -->
    <view class="brand-header">
      <image src="/static/logo.png" class="brand-logo" mode="aspectFit" />
      <text class="brand-name">{{ appName }}</text>
      <text class="brand-slogan">专业简历制作与职业发展平台</text>
    </view>

    <!-- 应用信息卡片 -->
    <view class="info-card">
      <view class="card-header">
        <text class="card-title">应用信息</text>
      </view>

      <view class="info-list">
        <view class="info-item">
          <text class="info-label">版本号</text>
          <text class="info-value">{{ version }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">更新时间</text>
          <text class="info-value">{{ updateTime }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">开发者</text>
          <text class="info-value">{{ developer }}</text>
        </view>
      </view>
    </view>

    <!-- 功能介绍 -->
    <view class="features-section">
      <view class="section-header">
        <text class="section-title">核心功能</text>
      </view>

      <view class="features-grid">
        <view class="feature-item" v-for="(feature, index) in features" :key="index">
          <view class="feature-icon">
            <text>{{ feature.icon }}</text>
          </view>
          <text class="feature-title">{{ feature.title }}</text>
          <text class="feature-desc">{{ feature.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 支持与反馈 -->
    <view class="support-section">
      <view class="section-header">
        <text class="section-title">支持与反馈</text>
      </view>

      <view class="support-list">
        <view class="menu-item" @click="handleUserAgreement">
          <view class="menu-left">
            <text class="menu-icon">📄</text>
            <text class="menu-text">用户协议</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="handlePrivacyPolicy">
          <view class="menu-left">
            <text class="menu-icon">🔒</text>
            <text class="menu-text">隐私政策</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="handleContactUs">
          <view class="menu-left">
            <text class="menu-icon">📧</text>
            <text class="menu-text">联系我们</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 版权信息 -->
    <view class="footer">
      <text class="copyright">© {{ currentYear }} {{ companyName }}</text>
      <text class="footer-text">让每个人的职业之路更精彩</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const appName = '简历大师'
const version = 'V2.1.0'
const updateTime = '2024-01-15'
const developer = 'CareerTech Team'
const companyName = 'CareerTech 职业科技'

const currentYear = new Date().getFullYear()

const features = [
  { icon: '📝', title: '智能简历', desc: 'AI智能优化，专业模板' },
  { icon: '🎨', title: '精美设计', desc: '多种风格，个性定制' },
  { icon: '📊', title: '简历分析', desc: '专业评估，优化建议' },
  { icon: '🚀', title: '职业发展', desc: '职业规划，技能提升' },
  { icon: '🔒', title: '数据安全', desc: '加密存储，隐私保护' },
  { icon: '📱', title: '多端同步', desc: '云端存储，随时编辑' }
]

const handleUserAgreement = () => {
  uni.navigateTo({
    url: '/pages/agreement/user'
  })
}

const handlePrivacyPolicy = () => {
  uni.navigateTo({
    url: '/pages/agreement/privacy'
  })
}

const handleContactUs = () => {
  uni.makePhoneCall({
    phoneNumber: '1861088XXXX'
  })
}

</script>

<style scoped lang="scss">
.about-container {
  background-color: $background-color;
  min-height: 100vh;
  padding: $padding-base;
}

/* 品牌头部 */
.brand-header {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  border-radius: $border-radius * 2;
  padding: $padding-base * 2 $padding-base;
  margin-bottom: $margin-base;
  text-align: center;
  color: $background-color-white;
  box-shadow: $box-shadow;
}

.brand-logo {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: $margin-base;
}

.brand-name {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  margin-bottom: $margin-mini;
}

.brand-slogan {
  display: block;
  font-size: $font-size-base;
  opacity: 0.9;
}

/* 信息卡片 */
.info-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $box-shadow;
}

.card-header {
  border-bottom: 1rpx solid $border-color-lighter;
  padding-bottom: $padding-small;
  margin-bottom: $padding-base;
}

.card-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: $padding-small;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-mini 0;
}

.info-label {
  font-size: $font-size-base;
  color: $text-regular;
}

.info-value {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

/* 功能特色 */
.features-section {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $box-shadow;
}

.section-header {
  border-bottom: 1rpx solid $border-color-lighter;
  padding-bottom: $padding-small;
  margin-bottom: $padding-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $padding-base;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.feature-icon {
  width: 60rpx;
  height: 60rpx;
  background: $primary-light;
  border-radius: $border-radius-round;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $margin-small;

  text {
    font-size: $font-size-medium;
  }
}

.feature-title {
  font-size: $font-size-small;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: calc($margin-mini / 2);
}

.feature-desc {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  line-height: 1.3;
}

/* 支持与反馈 */
.support-section {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base * 2;
  box-shadow: $box-shadow;
}

.support-list {
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

  &:active {
    background: $background-color;
  }

  &:last-child {
    border-bottom: none;
  }
}

.menu-left {
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: $font-size-medium;
  margin-right: $padding-base;
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

/* 页脚 */
.footer {
  text-align: center;
  padding: $padding-base 0;
}

.copyright {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  margin-bottom: $margin-mini;
}

.footer-text {
  display: block;
  font-size: $font-size-small;
  color: $text-placeholder;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .brand-header {
    padding: $padding-base;
  }
}
</style>