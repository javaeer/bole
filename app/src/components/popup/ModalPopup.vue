<template>
  <view
    class="modal-overlay"
    :class="{ show: modelValue, 'blur-backdrop': blur }"
    @click="handleOverlayClick"
    :style="overlayStyle"
    v-if="modelValue || showOverlay"
  >
    <!-- 弹层容器 -->
    <view
      class="modal-container"
      :class="[size, position, customClass]"
      @click.stop
      :style="containerStyle"
    >
      <!-- 关闭按钮（右上角） -->
      <button
        v-if="showClose"
        class="modal-close-btn"
        @click="handleClose"
      >
        <view class="close-icon">×</view>
      </button>

      <!-- 标题区域 -->
      <view v-if="title" class="modal-header">
        <view class="header-content">
          <text class="modal-title">{{ title }}</text>
          <text v-if="subtitle" class="modal-subtitle">{{ subtitle }}</text>
        </view>
      </view>

      <!-- 内容区域 -->
      <view class="modal-body">
        <slot></slot>
      </view>

      <!-- 底部按钮 -->
      <view v-if="showFooter" class="modal-footer">
        <slot name="footer">
          <button
            v-if="cancelText"
            class="footer-btn cancel-btn"
            @click="handleCancel"
          >
            {{ cancelText }}
          </button>
          <button
            v-if="confirmText"
            class="footer-btn confirm-btn"
            :disabled="confirmDisabled"
            @click="handleConfirm"
          >
            {{ confirmText }}
          </button>
        </slot>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ModalPopup',
  props: {
    // 显示控制 - 使用 modelValue 以支持 v-model
    modelValue: {
      type: Boolean,
      default: false
    },

    // 弹层内容
    title: String,
    subtitle: String,

    // 弹层尺寸
    size: {
      type: String,
      default: 'medium',
      validator: (val) => ['small', 'medium', 'large', 'full'].includes(val)
    },

    // 弹层位置
    position: {
      type: String,
      default: 'center',
      validator: (val) => ['center', 'bottom', 'top'].includes(val)
    },

    // 蒙层配置
    overlayClosable: {
      type: Boolean,
      default: true
    },
    blur: {
      type: Boolean,
      default: false
    },
    overlayOpacity: {
      type: Number,
      default: 0.5
    },

    // 关闭按钮
    showClose: {
      type: Boolean,
      default: true
    },

    // 底部按钮
    showFooter: {
      type: Boolean,
      default: false
    },
    cancelText: String,
    confirmText: String,
    confirmDisabled: Boolean,

    // 样式覆盖
    customClass: String,
    overlayStyle: Object,
    containerStyle: Object,

    // 是否保持蒙层显示（用于动画）
    keepOverlay: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      showOverlay: false
    };
  },

  watch: {
    modelValue: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.showOverlay = true;
        } else {
          // 延迟隐藏蒙层，用于动画效果
          setTimeout(() => {
            this.showOverlay = false;
          }, 300);
        }
      }
    }
  },

  methods: {
    // 关闭弹层
    handleClose() {
      this.$emit('update:modelValue', false);
      this.$emit('close');
    },

    // 点击蒙层
    handleOverlayClick() {
      if (this.overlayClosable) {
        this.handleClose();
      }
    },

    // 取消按钮
    handleCancel() {
      this.handleClose();
      this.$emit('cancel');
    },

    // 确认按钮
    handleConfirm() {
      this.$emit('confirm');
    }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay.show {
  opacity: 1;
  visibility: visible;
}

.modal-overlay.blur-backdrop {
  backdrop-filter: blur(4px);
}

.modal-container {
  background: white;
  border-radius: 8px;
  max-width: 90%;
  max-height: 80%;
  overflow: hidden;
  transform: translateY(20px);
  transition: transform 0.3s ease;
}

.modal-overlay.show .modal-container {
  transform: translateY(0);
}

/* 根据不同尺寸调整 */
.modal-container.small {
  width: 300px;
}

.modal-container.medium {
  width: 500px;
}

.modal-container.large {
  width: 700px;
}

.modal-container.full {
  width: 90%;
  height: 90%;
}

/* 位置调整 */
.modal-container.center {
  margin: auto;
}

.modal-container.bottom {
  align-self: flex-end;
  margin-bottom: 20px;
}

.modal-container.top {
  align-self: flex-start;
  margin-top: 20px;
}

.modal-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  max-height: 60vh;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>