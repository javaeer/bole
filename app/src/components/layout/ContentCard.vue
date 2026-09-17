<template>
  <view :class="[
    'content-card',
    `content-card--${variant}`,
    {
      'content-card--elevated': elevated,
      'content-card--bordered': bordered,
      'content-card--hoverable': hoverable,
      'content-card--full-width': fullWidth
    }
  ]" :style="cardStyle" @click="handleClick">
    <!-- 标题插槽 -->
    <view v-if="$slots.header" class="content-card__header">
      <slot name="header"></slot>
    </view>

    <!-- 内容插槽 -->
    <view class="content-card__body">
      <slot></slot>
    </view>

    <!-- 底部插槽 -->
    <view v-if="$slots.footer" class="content-card__footer">
      <slot name="footer"></slot>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'info'
  elevated?: boolean
  bordered?: boolean
  hoverable?: boolean
  fullWidth?: boolean
  padding?: string
  margin?: string
  borderRadius?: string
  onClick?: () => void
}

withDefaults(defineProps<Props>(), {
  variant: 'default',
  elevated: true,
  bordered: true,
  hoverable: false,
  fullWidth: false
})

const emit = defineEmits<{
  click: []
}>()

const cardStyle = computed(() => {
  const style: Record<string, string> = {}
  if (props.padding) style.padding = props.padding
  if (props.margin) style.margin = props.margin
  if (props.borderRadius) style.borderRadius = props.borderRadius
  return style
})

const handleClick = () => {
  if (props.onClick) {
    props.onClick()
  }
  emit('click')
}
</script>

<style lang="scss">
@import "@/responsive.scss";

.content-card {
  background: $bg-color-white;
  border-radius: $border-radius;
  overflow: hidden;
  transition: all $transition-fast $ease-in-out;

  &--default {
    background: $bg-color-white;
  }

  &--primary {
    background: $color-primary-light;
    border-left: 4rpx solid $color-primary;
  }

  &--secondary {
    background: $bg-color;
  }

  &--success {
    background: $bg-color-success;
    border-left: 4rpx solid $color-success;
  }

  &--warning {
    background: $bg-color-warning;
    border-left: 4rpx solid $color-warning;
  }

  &--danger {
    background: $bg-color-danger;
    border-left: 4rpx solid $color-danger;
  }

  &--info {
    background: $bg-color-info;
    border-left: 4rpx solid $color-info;
  }

  &--bordered {
    border: 1rpx solid $color-border;
  }

  &--elevated {
    box-shadow: $box-shadow;

    @media (min-width: #{$screen-md}) {
      box-shadow: $shadow-md;
    }
  }

  &--hoverable {
    cursor: pointer;

    &:hover, &:active {
      transform: translateY(-2rpx);
      box-shadow: $box-shadow-dark;
    }
  }

  &--full-width {
    width: 100%;
  }

  &__header {
    padding: $padding-base;
    border-bottom: 1rpx solid $color-border-light;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $color-text-primary;

    @media (min-width: #{$screen-md}) {
      padding: $padding-lg;
      font-size: $font-size-lg;
    }
  }

  &__body {
    padding: $padding-base;

    @media (min-width: #{$screen-md}) {
      padding: $padding-lg;
    }
  }

  &__footer {
    padding: $padding-base;
    border-top: 1rpx solid $color-border-light;
    background: $bg-color;

    @media (min-width: #{$screen-md}) {
      padding: $padding-lg;
    }
  }
}
</style>