<template>
  <view class="webview-container">
    <!-- 顶部导航栏 -->
    <view class="nav-header" v-if="showNav">
      <view class="nav-left">
        <button class="nav-back-btn" @click="handleBack">
          <text class="icon-back">←</text>
          <text>返回</text>
        </button>
      </view>
      <view class="nav-title">{{ pageTitle }}</view>
      <view class="nav-right">
        <button class="nav-action-btn" @click="refreshPage">
          <text class="icon-refresh">↻</text>
        </button>
        <button class="nav-action-btn" @click="openInBrowser" v-if="canOpenInBrowser">
          <text class="icon-external">↗</text>
        </button>
      </view>
    </view>

    <!-- WebView组件 -->
    <web-view 
      v-if="url"
      :src="url" 
      class="webview-content"
      @message="onWebViewMessage"
      @onPostMessage="onPostMessage"
    ></web-view>

    <!-- 加载状态 -->
    <view v-if="loading && url" class="loading-overlay">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>

    <!-- 错误状态 -->
    <view v-if="!url || loadError" class="error-state">
      <view class="error-icon">⚠️</view>
      <text class="error-title">页面加载失败</text>
      <text class="error-desc" v-if="!url">未提供有效的预览链接</text>
      <text class="error-desc" v-else>请检查网络连接或链接有效性</text>
      
      <view class="error-actions">
        <button class="action-btn primary-btn" @click="retryLoad" v-if="url && loadError">
          重新加载
        </button>
        <button class="action-btn secondary-btn" @click="handleBack">
          返回
        </button>
        <button class="action-btn tertiary-btn" @click="copyUrl" v-if="url">
          复制链接
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { onLoad, onShow, onHide } from '@dcloudio/uni-app';

// 响应式数据
const url = ref('');
const pageTitle = ref('文件预览');
const loading = ref(true);
const loadError = ref(false);
const showNav = ref(true);
const webViewContext = ref(null);

// 计算属性
const canOpenInBrowser = computed(() => {
  // 判断是否可以在浏览器中打开
  const platform = uni.getSystemInfoSync().platform;
  return platform === 'h5' || platform === 'app';
});

// 页面加载
onLoad((options) => {
  console.log('WebView页面参数:', options);
  
  // 获取传入的参数
  if (options.url) {
    url.value = decodeURIComponent(options.url);
  }
  
  if (options.title) {
    pageTitle.value = decodeURIComponent(options.title);
  }
  
  if (options.hideNav) {
    showNav.value = false;
  }
  
  // 设置页面标题
  uni.setNavigationBarTitle({
    title: pageTitle.value
  });
  
  // 监听页面加载事件
  setTimeout(() => {
    loading.value = false;
  }, 2000);
});

// 页面显示
onShow(() => {
  // 可以在这里重新加载页面
});

// 页面隐藏
onHide(() => {
  // 清理资源
});

// WebView消息处理
const onWebViewMessage = (e) => {
  console.log('收到WebView消息:', e);
  // 处理来自WebView的消息
};

// WebView postMessage处理
const onPostMessage = (e) => {
  console.log('收到postMessage:', e);
};

// 返回上一页
const handleBack = () => {
  uni.navigateBack();
};

// 刷新页面
const refreshPage = () => {
  loading.value = true;
  loadError.value = false;
  
  // 延迟重置加载状态
  setTimeout(() => {
    loading.value = false;
  }, 1000);
};

// 在浏览器中打开
const openInBrowser = () => {
  if (!url.value) return;
  
  const platform = uni.getSystemInfoSync().platform;
  
  if (platform === 'h5') {
    // H5环境
    window.open(url.value, '_blank');
  } else if (platform === 'app') {
    // App环境
    plus.runtime.openWeb(url.value);
  } else {
    // 其他环境
    uni.showModal({
      title: '提示',
      content: '请手动复制链接到浏览器中打开',
      showCancel: false,
      confirmText: '复制链接'
    });
  }
};

// 重新加载
const retryLoad = () => {
  loadError.value = false;
  refreshPage();
};

// 复制链接
const copyUrl = () => {
  if (!url.value) {
    uni.showToast({
      title: '链接为空',
      icon: 'error'
    });
    return;
  }
  
  uni.setClipboardData({
    data: url.value,
    success: () => {
      uni.showToast({
        title: '链接已复制',
        icon: 'success'
      });
    },
    fail: () => {
      uni.showToast({
        title: '复制失败',
        icon: 'error'
      });
    }
  });
};

// 组件挂载
onMounted(() => {
  // 可以在这里初始化WebView上下文
  // #ifdef APP-PLUS
  if (url.value) {
    webViewContext.value = plus.webview.create(url.value, 'webview', {
      top: showNav.value ? '44px' : '0px',
      bottom: '0px'
    });
  }
  // #endif
});

// 组件卸载
onUnmounted(() => {
  // 清理WebView
  // #ifdef APP-PLUS
  if (webViewContext.value) {
    webViewContext.value.close();
  }
  // #endif
});
</script>

<style lang="scss" scoped>
.webview-container {
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
    display: flex;
    align-items: center;
    gap: 8rpx;

    .nav-action-btn {
      background: transparent;
      border: none;
      padding: 16rpx;
      border-radius: 8rpx;

      &:active {
        background: #f5f5f5;
      }

      .icon-refresh,
      .icon-external {
        font-size: 32rpx;
        color: #333333;
      }
    }
  }
}

/* WebView内容区域 */
.webview-content {
  flex: 1;
  width: 100%;
}

/* 加载遮罩层 */
.loading-overlay {
  position: absolute;
  top: 0;
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

/* 错误状态 */
.error-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  text-align: center;

  .error-icon {
    font-size: 100rpx;
    margin-bottom: 30rpx;
    color: #FA5151;
  }

  .error-title {
    font-size: 36rpx;
    color: #333333;
    font-weight: 600;
    margin-bottom: 16rpx;
  }

  .error-desc {
    font-size: 28rpx;
    color: #666666;
    margin-bottom: 40rpx;
    line-height: 1.5;
  }

  .error-actions {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    width: 100%;
    max-width: 400rpx;

    .action-btn {
      padding: 24rpx;
      border-radius: 8rpx;
      font-size: 28rpx;
      font-weight: 500;
      border: none;
      transition: all 0.2s ease;

      &:active {
        opacity: 0.9;
      }

      &.primary-btn {
        background: #007AFF;
        color: #ffffff;
      }

      &.secondary-btn {
        background: #f5f5f5;
        color: #333333;
        border: 1rpx solid #e0e0e0;
      }

      &.tertiary-btn {
        background: transparent;
        color: #007AFF;
        border: 1rpx solid #007AFF;
      }
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
  
  .error-actions {
    .action-btn {
      width: 100%;
    }
  }
}
</style>