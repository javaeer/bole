<template>
  <view class="pdf-container">
    <!-- 顶部导航栏 -->
    <view class="nav-header">
      <view class="nav-left">
        <button class="nav-back-btn" @click="handleBack">
          <text class="icon-back">←</text>
          <text>返回</text>
        </button>
      </view>
      <view class="nav-title">{{ pageTitle }}</view>
      <view class="nav-right">
        <button class="nav-action-btn" @click="downloadPdf" v-if="url">
          <text class="icon-download">⬇️</text>
        </button>
      </view>
    </view>

    <!-- 小程序PDF预览 -->
    <!-- #ifdef MP-WEIXIN -->
    <view class="pdf-content">
      <web-view :src="pdfViewUrl" v-if="pdfViewUrl"></web-view>
      <view v-else class="pdf-error">
        <text class="error-text">PDF预览暂不可用</text>
        <button class="action-btn" @click="openExternal">在浏览器中打开</button>
      </view>
    </view>
    <!-- #endif -->

    <!-- H5和App PDF预览 -->
    <!-- #ifdef H5 || APP-PLUS -->
    <web-view :src="url" class="pdf-webview" v-if="url"></web-view>
    <view v-else class="pdf-error">
      <text class="error-text">PDF链接无效</text>
    </view>
    <!-- #endif -->

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载PDF...</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 响应式数据
const url = ref('');
const pageTitle = ref('PDF预览');
const loading = ref(true);

// 计算属性 - 小程序PDF预览URL
const pdfViewUrl = computed(() => {
  if (!url.value) return '';
  
  // 小程序中使用腾讯文档预览服务
  // 注意：需要将PDF链接进行编码
  const encodedUrl = encodeURIComponent(url.value);
  return `https://mozilla.github.io/pdf.js/web/viewer.html?file=${encodedUrl}`;
});

// 页面加载
onLoad((options) => {
  console.log('PDF页面参数:', options);
  
  if (options.url) {
    url.value = decodeURIComponent(options.url);
  }
  
  if (options.title) {
    pageTitle.value = decodeURIComponent(options.title);
  }
  
  uni.setNavigationBarTitle({
    title: pageTitle.value
  });
  
  // 模拟加载
  setTimeout(() => {
    loading.value = false;
  }, 1500);
});

// 返回上一页
const handleBack = () => {
  uni.navigateBack();
};

// 下载PDF
const downloadPdf = () => {
  if (!url.value) return;
  
  // 使用之前实现的下载功能
  uni.showModal({
    title: '下载PDF',
    content: '是否下载此PDF文件？',
    success: (res) => {
      if (res.confirm) {
        // 这里可以调用下载函数
        uni.showToast({
          title: '开始下载',
          icon: 'success'
        });
      }
    }
  });
};

// 在外部浏览器中打开
const openExternal = () => {
  if (!url.value) return;
  
  // #ifdef MP-WEIXIN
  uni.setClipboardData({
    data: url.value,
    success: () => {
      uni.showModal({
        title: '提示',
        content: '链接已复制，请在浏览器中打开',
        showCancel: false,
        confirmText: '确定'
      });
    }
  });
  // #endif
  
  // #ifdef H5
  window.open(url.value, '_blank');
  // #endif
  
  // #ifdef APP-PLUS
  plus.runtime.openWeb(url.value);
  // #endif
};

// 组件挂载
onMounted(() => {
  console.log('PDF组件挂载，URL:', url.value);
});
</script>

<style lang="scss" scoped>
.pdf-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* 导航栏样式 */
.nav-header {
  background: #ffffff;
  padding: 0 20rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 1000;

  .nav-left {
    .nav-back-btn {
      display: flex;
      align-items: center;
      gap: 8rpx;
      background: transparent;
      border: none;
      font-size: 28rpx;
      color: #333333;
      padding: 16rpx;
      border-radius: 8rpx;

      &:active {
        background: #f5f5f5;
      }

      .icon-back {
        font-size: 32rpx;
      }
    }
  }

  .nav-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
    max-width: 300rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .nav-right {
    .nav-action-btn {
      background: transparent;
      border: none;
      padding: 16rpx;
      border-radius: 8rpx;

      &:active {
        background: #f5f5f5;
      }

      .icon-download {
        font-size: 32rpx;
        color: #333333;
      }
    }
  }
}

/* PDF内容区域 */
.pdf-content {
  flex: 1;
  width: 100%;
  height: calc(100vh - 88rpx);
}

/* PDF WebView */
.pdf-webview {
  flex: 1;
  width: 100%;
  height: calc(100vh - 88rpx);
}

/* PDF错误状态 */
.pdf-error {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  text-align: center;

  .error-text {
    font-size: 32rpx;
    color: #666666;
    margin-bottom: 40rpx;
  }

  .action-btn {
    padding: 24rpx 48rpx;
    background: #007AFF;
    color: #ffffff;
    border: none;
    border-radius: 8rpx;
    font-size: 28rpx;
    font-weight: 500;
  }
}

/* 加载遮罩层 */
.loading-overlay {
  position: absolute;
  top: 88rpx;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;

  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20rpx;

    .loading-spinner {
      width: 60rpx;
      height: 60rpx;
      border: 4rpx solid #f0f0f0;
      border-top-color: #007AFF;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .loading-text {
      font-size: 28rpx;
      color: #666666;
    }
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .nav-header {
    .nav-title {
      max-width: 200rpx;
    }
  }
}
</style>