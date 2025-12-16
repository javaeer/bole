<template>
  <view :class="['job-intention', `layout-${styles.layout}`, `theme-${theme}`]">
    <view class="section-header">
      <text class="section-title">求职意向</text>
      <view class="section-divider"></view>
    </view>

    <view class="intention-content">
      <view class="primary-info">
        <text class="position">{{ props.position }}</text>
        <view class="meta-info">
          <text class="meta-item">
            <text class="icon">📍</text>
            {{ props.city || "城市不限" }}
          </text>
          <text class="meta-item" v-if="styles.showSalary && props.salary">
            <text class="icon">💰</text>
            {{ props.salary }}
          </text>
          <text class="meta-item">
            <text class="icon">🕐</text>
            {{ props.jobType || "全职" }}
          </text>
        </view>
      </view>

      <view class="secondary-info" v-if="props.industry || props.onboardTime">
        <text v-if="props.industry" class="info-item">
          <text class="label">期望行业：</text>
          {{ props.industry }}
        </text>
        <text v-if="props.onboardTime" class="info-item">
          <text class="label">到岗时间：</text>
          {{ props.onboardTime }}
        </text>
      </view>

      <!-- 技能标签布局 -->
      <view v-if="styles.layout === 'tag' && props.skills" class="skill-tags">
        <text
          v-for="(skill, index) in props.skills"
          :key="index"
          class="skill-tag"
        >
          {{ skill }}
        </text>
      </view>

      <!-- 列表布局 -->
      <view v-else-if="props.skills" class="skill-list">
        <text class="list-label">相关技能：</text>
        <text class="list-content">{{ props.skills.join('、') }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JobIntention',
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
    }
  }
};
</script>

<style lang="scss" scoped>
.job-intention {
  &.theme-modern {
    .section-title {
      color: $primary-color;
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
      margin-bottom: $margin-mini;
      display: block;
    }

    .position {
      color: $text-primary;
      font-size: $font-size-extra-large;
      font-weight: $font-weight-bold;
      margin-bottom: $margin-small;
      display: block;
    }
  }

  .section-divider {
    height: 2rpx;
    background: linear-gradient(90deg, $primary-color, $secondary-color);
    margin-bottom: $margin-base;
  }

  .meta-info {
    display: flex;
    flex-wrap: wrap;
    gap: $margin-base;
    margin-bottom: $margin-base;

    .meta-item {
      display: flex;
      align-items: center;
      color: $text-regular;
      font-size: $font-size-base;

      .icon {
        margin-right: 6rpx;
      }
    }
  }

  .secondary-info {
    background-color: $primary-light;
    border-radius: $border-radius-small;
    padding: $padding-small;
    margin-bottom: $margin-base;

    .info-item {
      display: block;
      color: $text-regular;
      font-size: $font-size-base;
      margin-bottom: $margin-mini;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: $text-secondary;
        font-weight: $font-weight-medium;
      }
    }
  }

  .skill-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $margin-mini;

    .skill-tag {
      padding: 8rpx 16rpx;
      background-color: $primary-lighter;
      border-radius: $border-radius-small;
      color: $text-regular;
      font-size: $font-size-small;
      border: 1rpx solid rgba($primary-color, 0.2);
    }
  }

  .skill-list {
    .list-label {
      color: $text-secondary;
      font-weight: $font-weight-medium;
      margin-right: $margin-mini;
    }

    .list-content {
      color: $text-regular;
      font-size: $font-size-base;
    }
  }
}
</style>