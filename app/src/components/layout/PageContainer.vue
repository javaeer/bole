<template>
  <view :class="[
    'page-container',
    `container${size ? '-' + size : ''}`,
    {
      'centered': centered,
      'full-width': fullWidth,
      'with-safe-area': withSafeArea
    }
  ]" :style="containerStyle">
    <slot></slot>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'xxl'
  centered?: boolean
  fullWidth?: boolean
  withSafeArea?: boolean
  padding?: string
  backgroundColor?: string
  maxWidth?: string
}

withDefaults(defineProps<Props>(), {
  size: 'md',
  centered: true,
  fullWidth: false,
  withSafeArea: true,
  padding: '',
  backgroundColor: '',
  maxWidth: ''
})

const containerStyle = computed(() => {
  const style: Record<string, string> = {}

  if (props.padding) style.padding = props.padding
  if (props.backgroundColor) style.backgroundColor = props.backgroundColor
  if (props.maxWidth) style.maxWidth = props.maxWidth

  if (props.withSafeArea) {
    style.paddingTop = `calc(${style.paddingTop || '0px'} + var(--safe-area-inset-top, 0px))`
    style.paddingBottom = `calc(${style.paddingBottom || '0px'} + var(--safe-area-inset-bottom, 0px))`
    style.paddingLeft = `calc(${style.paddingLeft || '0px'} + var(--safe-area-inset-left, 0px))`
    style.paddingRight = `calc(${style.paddingRight || '0px'} + var(--safe-area-inset-right, 0px))`
  }

  return style
})
</script>

<style lang="scss">
@import "@/responsive.scss";

.page-container {
  min-height: 100vh;
  background-color: $background-color;
  transition: all $transition-fast $ease-in-out;

  &.centered:not(.full-width) {
    margin-left: auto;
    margin-right: auto;
  }

  &.with-safe-area {
    padding-top: env(safe-area-inset-top);
    padding-bottom: env(safe-area-inset-bottom);
    padding-left: env(safe-area-inset-left);
    padding-right: env(safe-area-inset-right);
  }
}

@media (min-width: #{$screen-md}) {
  .page-container {
    &.centered:not(.full-width) {
      border-radius: $border-radius;
      box-shadow: $shadow-md;
      margin-top: $margin-base;
      margin-bottom: $margin-base;
    }
  }
}

@media (min-width: #{$screen-lg}) {
  .page-container {
    &.centered:not(.full-width) {
      margin-top: $margin-large;
      margin-bottom: $margin-large;
    }
  }
}
</style>