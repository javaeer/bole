<!-- components/resumes/ShareModal.vue - 优化版本 -->
<template>
  <ModalPopup
    v-model="visible"
    title="分享简历"
    subtitle="选择分享方式"
    size="medium"
    :show-footer="false"
    :overlay-closable="true"
    @close="handleClose"
  >
    <view class="share-options">
      <!-- 微信分享 -->
      <button
        class="share-option wechat"
        @click="shareToWeChat"
      >
        <view class="option-icon">
          <view class="icon-bg">
            <text class="icon">💬</text>
          </view>
        </view>
        <view class="option-content">
          <text class="option-title">微信好友</text>
          <text class="option-desc">分享给微信好友或群聊</text>
        </view>
        <view class="option-arrow">›</view>
      </button>

      <!-- 二维码 -->
      <button
        class="share-option qrcode"
        @click="generateQRCode"
      >
        <view class="option-icon">
          <view class="icon-bg">
            <text class="icon">📱</text>
          </view>
        </view>
        <view class="option-content">
          <text class="option-title">二维码</text>
          <text class="option-desc">生成分享二维码图片</text>
        </view>
        <view class="option-arrow">›</view>
      </button>

      <!-- 复制链接 -->
      <button
        class="share-option link"
        @click="copyShareLink"
      >
        <view class="option-icon">
          <view class="icon-bg">
            <text class="icon">🔗</text>
          </view>
        </view>
        <view class="option-content">
          <text class="option-title">复制链接</text>
          <text class="option-desc">复制简历分享链接</text>
        </view>
        <view class="option-arrow">›</view>
      </button>
    </view>

    <!-- 二维码显示区域 -->
    <view v-if="qrcodeUrl" class="qrcode-section">
      <view class="qrcode-header">
        <text class="qrcode-title">扫描二维码查看简历</text>
        <button class="qrcode-refresh" @click="regenerateQRCode">
          <text class="refresh-icon">🔄</text>
          刷新
        </button>
      </view>

      <view class="qrcode-container">
        <image
          :src="qrcodeUrl"
          class="qrcode-image"
          mode="aspectFit"
          @load="onQRCodeLoaded"
        />
        <view class="qrcode-loading" v-if="!qrcodeLoaded">
          <view class="loading-spinner"></view>
          <text>生成二维码中...</text>
        </view>
      </view>

      <view class="qrcode-info">
        <view class="info-item">
          <text class="info-label">有效期：</text>
          <text class="info-value">7天</text>
        </view>
        <view class="info-item">
          <text class="info-label">扫描次数：</text>
          <text class="info-value">{{ scanCount }} 次</text>
        </view>
      </view>
    </view>

    <!-- 分享链接展示 -->
    <view v-if="shareLink && !qrcodeUrl" class="share-link-section">
      <view class="link-header">
        <text class="link-title">分享链接</text>
        <button class="link-copy-btn" @click="copyShareLink">
          <text class="copy-icon">📋</text>
          复制
        </button>
      </view>
      <view class="link-container">
        <text class="link-text">{{ shareLink }}</text>
      </view>
      <view class="link-hint">
        <text class="hint-icon">ℹ️</text>
        <text class="hint-text">链接已复制到剪贴板</text>
      </view>
    </view>
  </ModalPopup>
</template>

<script>
export default {
  name: 'ShareModal',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    resumeId: {
      type: [Number, String],
      required: true
    },
    resumeTitle: {
      type: String,
      default: '我的简历'
    }
  },

  emits: ['update:modelValue', 'share', 'close'],

  data() {
    return {
      qrcodeUrl: '',
      qrcodeLoaded: false,
      scanCount: 0
    };
  },

  computed: {
    visible: {
      get() {
        return this.modelValue;
      },
      set(value) {
        this.$emit('update:modelValue', value);
      }
    },

    shareLink() {
      return `${this.getBaseUrl()}/resume/share/${this.resumeId}`;
    }
  },

  watch: {
    modelValue(newVal) {
      if (!newVal) {
        this.resetState();
      }
    }
  },

  methods: {
    // 微信分享
    shareToWeChat() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        href: this.shareLink,
        title: this.resumeTitle,
        summary: '查看我的个人简历',
        imageUrl: 'https://your-domain.com/resume-cover.jpg',
        success: () => {
          uni.showToast({
            title: '分享成功',
            icon: 'success'
          });
          this.$emit('share', 'wechat');
          this.visible = false;
        },
        fail: () => {
          uni.showToast({
            title: '分享失败',
            icon: 'error'
          });
        }
      });
    },

    // 生成二维码
    generateQRCode() {
      this.qrcodeLoaded = false;

      try {
        // 使用前端生成
        this.qrcodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(this.shareLink)}&format=png&color=2D3748&bgcolor=FFFFFF`;
      } catch (error) {
        console.error('生成二维码失败:', error);
      }
    },

    // 重新生成二维码
    regenerateQRCode() {
      this.qrcodeUrl = '';
      setTimeout(() => {
        this.generateQRCode();
      }, 100);
    },

    // 二维码加载完成
    onQRCodeLoaded() {
      this.qrcodeLoaded = true;
    },

    // 复制分享链接
    copyShareLink() {
      uni.setClipboardData({
        data: this.shareLink,
        success: () => {
          uni.showToast({
            title: '链接已复制',
            icon: 'success',
            duration: 2000
          });
          this.$emit('share', 'link');
        },
        fail: () => {
          uni.showToast({
            title: '复制失败',
            icon: 'error'
          });
        }
      });
    },

    // 弹层关闭
    handleClose() {
      this.resetState();
      this.$emit('close');
    },

    // 重置状态
    resetState() {
      this.qrcodeUrl = '';
      this.qrcodeLoaded = false;
    },

    // 获取基础URL
    getBaseUrl() {
      return 'https://your-domain.com';
    }
  }
};
</script>

<style lang="scss" scoped>
// 分享选项样式
.share-options {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 48rpx;

  .share-option {
    display: flex;
    align-items: center;
    padding: 32rpx;
    background: #ffffff;
    border-radius: 16rpx;
    border: 1rpx solid #f2f2f7;
    transition: all 0.2s ease;

    &:active {
      background: #f5f5f7;
      transform: translateY(-1rpx);
      box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
    }

    .option-icon {
      margin-right: 24rpx;

      .icon-bg {
        width: 80rpx;
        height: 80rpx;
        border-radius: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        .icon {
          font-size: 40rpx;
        }
      }
    }

    &.wechat {
      .icon-bg {
        background: linear-gradient(135deg, #09bb07 0%, #09a306 100%);
      }
    }

    &.qrcode {
      .icon-bg {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    &.link {
      .icon-bg {
        background: linear-gradient(135deg, #f6ad55 0%, #ed8936 100%);
      }
    }

    .option-content {
      flex: 1;
      text-align: left;

      .option-title {
        display: block;
        font-size: 32rpx;
        font-weight: 600;
        color: #1d1d1f;
        margin-bottom: 8rpx;
      }

      .option-desc {
        display: block;
        font-size: 26rpx;
        color: #8e8e93;
      }
    }

    .option-arrow {
      font-size: 36rpx;
      color: #c7c7cc;
      font-weight: 300;
      transform: scale(1.5, 2);
      margin-left: 16rpx;
    }
  }
}

// 二维码区域
.qrcode-section {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  margin-top: 32rpx;
  border: 1rpx solid #e2e8f0;

  .qrcode-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32rpx;

    .qrcode-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #2d3748;
    }

    .qrcode-refresh {
      background: transparent;
      border: 1rpx solid #cbd5e0;
      border-radius: 20rpx;
      padding: 12rpx 24rpx;
      font-size: 26rpx;
      color: #4a5568;
      display: flex;
      align-items: center;
      gap: 8rpx;

      &:active {
        background: #edf2f7;
      }

      .refresh-icon {
        font-size: 24rpx;
      }
    }
  }

  .qrcode-container {
    position: relative;
    width: 300rpx;
    height: 300rpx;
    margin: 0 auto 32rpx;
    background: white;
    border-radius: 16rpx;
    padding: 24rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
    border: 1rpx solid #e2e8f0;

    .qrcode-image {
      width: 100%;
      height: 100%;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .qrcode-loading {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 16rpx;

      .loading-spinner {
        width: 48rpx;
        height: 48rpx;
        border: 3rpx solid #e2e8f0;
        border-top-color: #667eea;
        border-radius: 50%;
        animation: spin 1s linear infinite;
      }

      text {
        font-size: 26rpx;
        color: #718096;
      }
    }
  }

  .qrcode-info {
    display: flex;
    justify-content: space-around;
    padding-top: 24rpx;
    border-top: 1rpx solid #e2e8f0;

    .info-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .info-label {
        font-size: 26rpx;
        color: #718096;
        margin-bottom: 4rpx;
      }

      .info-value {
        font-size: 28rpx;
        font-weight: 600;
        color: #4a5568;
      }
    }
  }
}

// 分享链接区域
.share-link-section {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  margin-top: 32rpx;
  border: 1rpx solid #e2e8f0;

  .link-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;

    .link-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #2d3748;
    }

    .link-copy-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 20rpx;
      padding: 12rpx 24rpx;
      font-size: 26rpx;
      color: white;
      display: flex;
      align-items: center;
      gap: 8rpx;
      box-shadow: 0 4rpx 16rpx rgba(102, 126, 234, 0.3);

      &:active {
        transform: translateY(1rpx);
        box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.3);
      }
    }
  }

  .link-container {
    background: white;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    border: 1rpx solid #cbd5e0;
    overflow: hidden;

    .link-text {
      font-size: 26rpx;
      color: #4a5568;
      word-break: break-all;
      line-height: 1.6;
    }
  }

  .link-hint {
    display: flex;
    align-items: center;
    gap: 12rpx;
    padding: 16rpx;
    background: rgba(102, 126, 234, 0.1);
    border-radius: 12rpx;
    border-left: 4rpx solid #667eea;

    .hint-icon {
      font-size: 24rpx;
    }

    .hint-text {
      font-size: 26rpx;
      color: #4a5568;
    }
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 响应式调整
@media (max-width: 480px) {
  .share-option {
    padding: 24rpx !important;

    .option-icon .icon-bg {
      width: 64rpx !important;
      height: 64rpx !important;
    }
  }

  .qrcode-container,
  .qrcode-section,
  .share-link-section {
    padding: 32rpx !important;
  }
}
</style>