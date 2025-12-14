<template>
  <view class="template-select-page">
    <view class="select-header">
      <text class="header-title">选择简历模板</text>
      <text class="header-subtitle">请选择适合您风格的模板</text>
    </view>

    <view class="template-grid">
      <view
        v-for="template in templates"
        :key="template.id"
        class="template-card"
        :class="{ 'template-card-active': selectedTemplate === template.id }"
        @click="selectTemplate(template.id)"
      >
        <view class="template-preview">
          <view class="preview-header">
            <view class="preview-avatar"></view>
            <view class="preview-title">
              <view class="preview-name-line"></view>
              <view class="preview-position-line"></view>
            </view>
          </view>
          <view class="preview-content">
            <view class="preview-section" v-for="n in 3" :key="n"></view>
          </view>
          <view class="template-badge" :class="'badge-' + template.style">
            {{ template.name }}
          </view>
        </view>
      </view>
    </view>

    <view class="action-area">
      <button
        class="next-button"
        :class="{ 'next-button-disabled': !selectedTemplate }"
        :disabled="!selectedTemplate"
        @click="enterFormPage"
      >
        开始创建简历
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      selectedTemplate: null,
      templates: [
        { id: 1, name: '传统风', style: 'traditional' },
        { id: 2, name: '科技风', style: 'tech' },
        { id: 3, name: '简约风', style: 'simple' },
        { id: 4, name: '创意风', style: 'creative' },
        { id: 5, name: '商务风', style: 'business' },
        { id: 6, name: '学术风', style: 'academic' }
      ]
    }
  },

  methods: {
    selectTemplate(id) {
      this.selectedTemplate = id
    },

    enterFormPage() {
      if (this.selectedTemplate) {
        // 跳转到简历创建页，传递选中的模板ID
        uni.navigateTo({
          url: `/pages/resumes/create?templateId=${this.selectedTemplate}`
        })
      }
    }
  }
}

</script>

<style lang="scss" scoped>

/* ==================== 模板选择页面样式 ==================== */
.template-select-page {
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-lighter 0%, $background-color-white 100%);
  padding: $padding-base;
}

.select-header {
  text-align: center;
  margin-bottom: $margin-large;
  padding-top: $navigation-bar-height;
}

.header-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.header-subtitle {
  display: block;
  font-size: $font-size-base;
  color: $text-secondary;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-base;
  margin-bottom: $margin-large;
}

.template-card {
  background: $background-color-white;
  border-radius: $card-border-radius;
  padding: $padding-base;
  box-shadow: $card-shadow;
  transition: all $transition-normal;
  position: relative;
  overflow: hidden;
  cursor: pointer;

  &:hover {
    transform: translateY(-4rpx);
    box-shadow: $card-hover-shadow;
  }

  &-active {
    border: 2px solid $primary-color;
    background: linear-gradient(135deg, $primary-lighter 0%, $background-color-white 100%);
  }
}

.template-preview {
  height: 320rpx;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: $border-radius-small;
  padding: $padding-small;
  position: relative;
}

.preview-header {
  display: flex;
  align-items: center;
  margin-bottom: $margin-mini;
}

.preview-avatar {
  width: 80rpx;
  height: 80rpx;
  background: $border-color;
  border-radius: $border-radius-round;
  margin-right: $margin-small;
}

.preview-title {
  flex: 1;
}

.preview-name-line {
  height: 16rpx;
  background: $text-primary;
  border-radius: 4rpx;
  margin-bottom: 8rpx;
  width: 60%;
}

.preview-position-line {
  height: 12rpx;
  background: $text-secondary;
  border-radius: 4rpx;
  width: 40%;
}

.preview-content {
  .preview-section {
    height: 12rpx;
    background: $border-color-light;
    border-radius: 4rpx;
    margin-bottom: 8rpx;

    &:nth-child(2) {
      width: 80%;
    }

    &:nth-child(3) {
      width: 60%;
    }
  }
}

.template-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: $border-radius-round;
  font-size: $font-size-extra-small;
  font-weight: $font-weight-medium;
  color: $background-color-white;

  &.badge-traditional {
    background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -20%) 100%);
  }

  &.badge-tech {
    background: linear-gradient(135deg, #007aff 0%, #0056cc 100%);
  }

  &.badge-simple {
    background: linear-gradient(135deg, #909399 0%, #606266 100%);
  }

  &.badge-creative {
    background: linear-gradient(135deg, #e6a23c 0%, #b8821e 100%);
  }

  &.badge-business {
    background: linear-gradient(135deg, #303133 0%, #000000 100%);
  }

  &.badge-academic {
    background: linear-gradient(135deg, #67c23a 0%, #459a1c 100%);
  }
}

.action-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $padding-base;
  background: linear-gradient(to top, $background-color-white 80%, transparent);
}

.next-button {
  width: 100%;
  height: $button-height;
  background: $button-primary-bg;
  border-radius: $button-border-radius;
  color: $background-color-white;
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  border: none;
  transition: all $transition-normal;

  &:active {
    transform: scale(0.98);
    box-shadow: $button-active-shadow;
  }

  &-disabled {
    background: $button-disabled-bg;
    opacity: $button-disabled-opacity;
  }
}
</style>