<template>
  <view :class="['basic-info', `layout-${styles.layout}`, `theme-${theme}`]">
    <!-- 卡片布局 -->
    <view v-if="styles.layout === 'card'" class="info-card card-shadow">
      <view class="info-header">
        <view v-if="styles.showAvatar" class="avatar-container">
          <image
            v-if="props.avatar"
            :src="props.avatar"
            class="avatar-image"
            mode="aspectFill"
          />
          <view v-else class="avatar-placeholder">
            <text class="avatar-text">{{ nameInitial }}</text>
          </view>
        </view>

        <view class="info-main">
          <view class="name-row">
            <text class="name">{{ props.name }}</text>
            <view class="badges">
              <text v-if="props.gender" class="badge gender">
                {{ props.gender }}
              </text>
              <text v-if="props.age" class="badge age">
                {{ props.age }}岁
              </text>
              <text v-if="props.workYears" class="badge experience">
                {{ props.workYears }}年经验
              </text>
            </view>
          </view>

          <text v-if="props.location" class="location">
            <text class="icon">📍</text>
            {{ props.location }}
          </text>
        </view>
      </view>

      <view v-if="styles.showContact" class="contact-info">
        <view class="contact-item" v-if="props.phone">
          <text class="icon">📱</text>
          <text class="value">{{ props.phone }}</text>
        </view>

        <view class="contact-item" v-if="props.email">
          <text class="icon">✉️</text>
          <text class="value">{{ props.email }}</text>
        </view>

        <view class="contact-item" v-if="props.wechat">
          <text class="icon">💬</text>
          <text class="value">{{ props.wechat }}</text>
        </view>
      </view>
    </view>

    <!-- 简洁布局 -->
    <view v-else class="info-simple">
      <text class="name">{{ props.name }}</text>
      <view class="contact-row">
        <text v-if="props.phone">{{ props.phone }}</text>
        <text v-if="props.email"> | {{ props.email }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'UserBasicInfo',
  props: {
    config: {
      type: Object,
      default: () => ({})
    },
    theme: {
      type: String,
      default: 'modern'
    }
  },
  computed: {
    props() {
      return this.config.props || {};
    },
    styles() {
      return this.config.styles || {};
    },
    nameInitial() {
      const name = this.props.name || '';
      return name.charAt(0) || '?';
    }
  }
};
</script>

<style lang="scss" scoped>
.basic-info {
  &.theme-modern {
    .info-card {
      background: $background-color-white;
      border-radius: $border-radius-large;
      padding: $padding-large;
    }

    .name {
      color: $text-primary;
      font-size: $font-size-extra-large;
      font-weight: $font-weight-bold;
      margin-bottom: $margin-mini;
    }

    .badge {
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      margin-left: $margin-mini;

      &.gender {
        background-color: rgba($primary-color, 0.1);
        color: $primary-color;
      }

      &.age {
        background-color: $info-bg;
        color: $info-color;
      }

      &.experience {
        background-color: $success-bg;
        color: $success-color;
      }
    }
  }

  .info-header {
    display: flex;
    align-items: center;
    margin-bottom: $margin-base;
  }

  .avatar-container {
    width: 120rpx;
    height: 120rpx;
    border-radius: $border-radius-round;
    overflow: hidden;
    margin-right: $margin-base;

    .avatar-image {
      width: 100%;
      height: 100%;
    }

    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, $primary-color, $secondary-color);
      display: flex;
      align-items: center;
      justify-content: center;

      .avatar-text {
        color: white;
        font-size: $font-size-extra-large;
        font-weight: $font-weight-bold;
      }
    }
  }

  .info-main {
    flex: 1;
  }

  .name-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: $margin-mini;
  }

  .location {
    color: $text-secondary;
    font-size: $font-size-base;

    .icon {
      margin-right: 6rpx;
    }
  }

  .contact-info {
    border-top: 1rpx solid $border-color-light;
    padding-top: $padding-small;

    .contact-item {
      display: flex;
      align-items: center;
      margin-bottom: $margin-mini;

      &:last-child {
        margin-bottom: 0;
      }

      .icon {
        margin-right: 12rpx;
        font-size: $font-size-medium;
        width: 32rpx;
        text-align: center;
      }

      .value {
        color: $text-regular;
        font-size: $font-size-base;
      }
    }
  }

  .info-simple {
    .name {
      color: $text-primary;
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
      display: block;
      margin-bottom: $margin-mini;
    }

    .contact-row {
      color: $text-secondary;
      font-size: $font-size-base;
    }
  }
}

.card-shadow {
  box-shadow: $box-shadow;
}
</style>