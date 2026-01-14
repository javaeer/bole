<!-- components/resumes/ExportModal.vue - 优化版本 -->
<template>
  <ModalPopup
    v-model="visible"
    :title="exporting ? '正在导出...' : '选择导出格式'"
    :subtitle="exporting ? `正在生成${currentFormat}文件` : '选择您需要的简历格式'"
    size="medium"
    :show-footer="false"
    :overlay-closable="!exporting"
    :show-close="!exporting"
    @close="handleClose"
  >
    <!-- 格式选择 -->
    <view v-if="!exporting" class="export-format-selector">
      <!-- 格式选项 -->
      <view class="format-grid">
        <button
          v-for="format in exportFormats"
          :key="format.value"
          class="format-option"
          :class="{ 'recommended': format.recommended }"
          @click="handleSelectFormat(format.value)"
        >
          <view class="format-icon" :style="{ background: format.color }">
            <text class="icon">{{ format.icon }}</text>
            <view v-if="format.recommended" class="recommended-badge">推荐</view>
          </view>
          <view class="format-info">
            <text class="format-name">{{ format.name }}</text>
            <text class="format-desc">{{ format.description }}</text>
            <view class="format-tags">
              <text v-for="tag in format.tags" :key="tag" class="tag">
                {{ tag }}
              </text>
            </view>
          </view>
          <view class="format-arrow">›</view>
        </button>
      </view>

      <!-- 导出设置 -->
      <view class="export-settings">
        <view class="settings-header">
          <text class="settings-title">导出设置</text>
          <switch
            :checked="includeWatermark"
            @change="onSettingsChange"
            color="#667eea"
          />
        </view>
        <view class="settings-options">
          <view class="settings-option">
            <text class="option-label">包含水印</text>
            <text class="option-hint">在简历底部添加"简历模板"水印</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 导出进度 -->
    <view v-else-if="!exportSuccess" class="export-progress">
      <!-- 进度指示器 -->
      <view class="progress-indicator">
        <view class="progress-track">
          <view
            class="progress-bar"
            :style="{ width: `${progress}%` }"
          ></view>
          <view class="progress-dots">
            <view
              v-for="step in 5"
              :key="step"
              class="progress-dot"
              :class="{ 'active': progress >= (step * 20) }"
            ></view>
          </view>
        </view>
        <text class="progress-text">{{ progress }}%</text>
      </view>

      <!-- 状态信息 -->
      <view class="status-info">
        <view class="status-item">
          <text class="status-label">当前状态：</text>
          <text class="status-value">{{ statusText }}</text>
        </view>
        <view class="status-item">
          <text class="status-label">预计时间：</text>
          <text class="status-value">{{ estimatedTime }}</text>
        </view>
        <view class="status-item">
          <text class="status-label">文件大小：</text>
          <text class="status-value">{{ fileSize }}</text>
        </view>
      </view>

      <!-- 进度动画 -->
      <view class="progress-animation">
        <view class="document-icon">
          <text class="icon">📄</text>
        </view>
        <view class="animation-dots">
          <view
            v-for="dot in 3"
            :key="dot"
            class="animation-dot"
            :style="{
              animationDelay: `${dot * 0.2}s`
            }"
          ></view>
        </view>
      </view>

      <!-- 提示信息 -->
      <view class="progress-hint">
        <text class="hint-icon">💡</text>
        <text class="hint-text">导出完成后将自动跳转到下载页面</text>
      </view>
    </view>

    <!-- 导出成功提示 -->
    <view v-else class="export-success">
      <view class="success-icon">
        <text class="icon">🎉</text>
      </view>
      <text class="success-title">导出成功！</text>
      <text class="success-message">{{ currentFormat }}文件已生成完成</text>
      <view class="success-actions">
        <button class="action-btn view-btn" @click="goToTaskList">
          查看下载
        </button>
        <button class="action-btn share-btn" @click="shareExportFile">
          分享文件
        </button>
      </view>
    </view>
  </ModalPopup>
</template>

<script>
export default {
  name: 'ExportModal',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    resumeId: {
      type: [Number, String],
      required: true
    }
  },

  emits: ['update:modelValue', 'export', 'success', 'close'],

  data() {
    return {
      // 导出格式配置
      exportFormats: [
        {
          value: 'PDF',
          name: 'PDF格式',
          icon: '📄',
          description: '高品质打印格式，适合投递',
          tags: ['打印', '正式', '通用'],
          color: 'linear-gradient(135deg, #f56565 0%, #ed8936 100%)',
          recommended: true
        },
        {
          value: 'WORD',
          name: 'Word格式',
          icon: '📝',
          description: '可编辑文档，方便修改',
          tags: ['可编辑', 'Office', '修改'],
          color: 'linear-gradient(135deg, #4299e1 0%, #667eea 100%)'
        },
        {
          value: 'HTML',
          name: 'HTML格式',
          icon: '🌐',
          description: '网页格式，在线查看',
          tags: ['网页', '在线', '响应式'],
          color: 'linear-gradient(135deg, #48bb78 0%, #38a169 100%)'
        },
        {
          value: 'TXT',
          name: '文本格式',
          icon: '📃',
          description: '纯文本，简单轻量',
          tags: ['简洁', '轻量', '文本'],
          color: 'linear-gradient(135deg, #a0aec0 0%, #718096 100%)'
        }
      ],

      // 导出状态
      exporting: false,
      exportSuccess: false,
      currentFormat: '',
      progress: 0,
      includeWatermark: false,
      exportTimer: null
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

    statusText() {
      if (this.progress < 20) return '正在准备文件...';
      if (this.progress < 40) return '正在生成内容...';
      if (this.progress < 60) return '正在格式化布局...';
      if (this.progress < 80) return '正在应用样式...';
      if (this.progress < 100) return '正在生成文件...';
      return '导出完成！';
    },

    estimatedTime() {
      const remaining = Math.max(0, 100 - this.progress);
      return `${Math.ceil(remaining / 5)}秒`;
    },

    fileSize() {
      const baseSize = {
        'PDF': '1.2 MB',
        'WORD': '800 KB',
        'HTML': '500 KB',
        'TXT': '200 KB'
      }[this.currentFormat] || '--';
      return baseSize;
    }
  },

  watch: {
    modelValue(newVal) {
      if (!newVal) {
        this.resetState();
      }
    }
  },

  beforeUnmount() {
    this.clearTimer();
  },

  methods: {
    // 选择格式
    async handleSelectFormat(format) {
      this.currentFormat = format;
      this.exporting = true;

      // 开始模拟导出进度
      this.startExportProgress();

      try {
        const taskForm = {
          resumesId: this.resumeId,
          documentType: format,
          includeWatermark: this.includeWatermark
        };

        // 模拟API调用
        setTimeout(() => {
          this.exportSuccess = true;
          this.$emit('export', { format, taskForm });
          this.$emit('success', { format, taskForm });
        }, 3000);

      } catch (error) {
        console.error('导出失败:', error);
        uni.showToast({
          title: '导出失败',
          icon: 'error'
        });
        this.visible = false;
      }
    },

    // 模拟导出进度
    startExportProgress() {
      this.progress = 0;
      this.clearTimer();

      this.exportTimer = setInterval(() => {
        if (this.progress < 100) {
          this.progress += 5;
        } else {
          this.clearTimer();
        }
      }, 200);
    },

    // 清除定时器
    clearTimer() {
      if (this.exportTimer) {
        clearInterval(this.exportTimer);
        this.exportTimer = null;
      }
    },

    // 设置变更
    onSettingsChange(e) {
      this.includeWatermark = e.detail.value;
    },

    // 跳转到任务列表
    goToTaskList() {
      this.visible = false;
      uni.navigateTo({
        url: `/pages/tasks/taskList?resumeId=${this.resumeId}`
      });
    },

    // 分享导出文件
    shareExportFile() {
      uni.showToast({
        title: '分享功能开发中',
        icon: 'none'
      });
    },

    // 弹层关闭
    handleClose() {
      if (!this.exporting) {
        this.resetState();
        this.$emit('close');
      }
    },

    // 重置状态
    resetState() {
      this.exporting = false;
      this.exportSuccess = false;
      this.progress = 0;
      this.currentFormat = '';
      this.clearTimer();
    }
  }
};
</script>

<style lang="scss" scoped>
// 格式选择器
.export-format-selector {
  .format-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24rpx;
    margin-bottom: 48rpx;

    .format-option {
      display: flex;
      align-items: center;
      padding: 32rpx;
      background: #ffffff;
      border-radius: 20rpx;
      border: 2rpx solid #f2f2f7;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;

      &:active {
        transform: translateY(-2rpx);
        box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.1);
        border-color: #667eea;
      }

      &.recommended {
        border-color: #667eea;
        background: linear-gradient(to right, rgba(102, 126, 234, 0.05), transparent);

        &::before {
          content: '';
          position: absolute;
          top: -1rpx;
          left: -1rpx;
          right: -1rpx;
          height: 4rpx;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 4rpx 4rpx 0 0;
        }
      }

      .format-icon {
        width: 96rpx;
        height: 96rpx;
        border-radius: 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 32rpx;
        position: relative;

        .icon {
          font-size: 48rpx;
          color: white;
        }

        .recommended-badge {
          position: absolute;
          top: -12rpx;
          right: -12rpx;
          background: linear-gradient(135deg, #f56565 0%, #ed8936 100%);
          color: white;
          font-size: 20rpx;
          padding: 4rpx 12rpx;
          border-radius: 20rpx;
          font-weight: 600;
        }
      }

      .format-info {
        flex: 1;

        .format-name {
          display: block;
          font-size: 32rpx;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 8rpx;
        }

        .format-desc {
          display: block;
          font-size: 26rpx;
          color: #718096;
          margin-bottom: 12rpx;
          line-height: 1.4;
        }

        .format-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8rpx;

          .tag {
            font-size: 22rpx;
            padding: 4rpx 12rpx;
            background: #edf2f7;
            color: #4a5568;
            border-radius: 12rpx;
          }
        }
      }

      .format-arrow {
        font-size: 40rpx;
        color: #cbd5e0;
        font-weight: 300;
        transform: scale(1.5, 2);
        margin-left: 16rpx;
      }
    }
  }

  // 导出设置
  .export-settings {
    background: #f8fafc;
    border-radius: 20rpx;
    padding: 32rpx;
    border: 1rpx solid #e2e8f0;

    .settings-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;

      .settings-title {
        font-size: 32rpx;
        font-weight: 600;
        color: #2d3748;
      }
    }

    .settings-options {
      .settings-option {
        display: flex;
        flex-direction: column;
        padding: 24rpx 0;
        border-bottom: 1rpx solid #e2e8f0;

        &:last-child {
          border-bottom: none;
        }

        .option-label {
          font-size: 28rpx;
          font-weight: 500;
          color: #4a5568;
          margin-bottom: 8rpx;
        }

        .option-hint {
          font-size: 24rpx;
          color: #a0aec0;
        }
      }
    }
  }
}

// 导出进度
.export-progress {
  padding: 32rpx 0;

  // 进度指示器
  .progress-indicator {
    margin-bottom: 48rpx;

    .progress-track {
      position: relative;
      height: 8rpx;
      background: #e2e8f0;
      border-radius: 4rpx;
      margin-bottom: 24rpx;
      overflow: hidden;

      .progress-bar {
        height: 100%;
        background: linear-gradient(90deg, #667eea, #764ba2);
        border-radius: 4rpx;
        transition: width 0.3s ease;
      }

      .progress-dots {
        position: absolute;
        top: -4rpx;
        left: 0;
        right: 0;
        height: 16rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 8rpx;

        .progress-dot {
          width: 16rpx;
          height: 16rpx;
          background: white;
          border: 2rpx solid #cbd5e0;
          border-radius: 50%;
          transition: all 0.3s ease;

          &.active {
            background: #667eea;
            border-color: #667eea;
            transform: scale(1.2);
          }
        }
      }
    }

    .progress-text {
      display: block;
      text-align: center;
      font-size: 48rpx;
      font-weight: 700;
      color: #2d3748;
      font-feature-settings: "tnum";
    }
  }

  // 状态信息
  .status-info {
    background: #f8fafc;
    border-radius: 20rpx;
    padding: 32rpx;
    margin-bottom: 48rpx;
    border: 1rpx solid #e2e8f0;

    .status-item {
      display: flex;
      justify-content: space-between;
      padding: 16rpx 0;
      border-bottom: 1rpx solid #e2e8f0;

      &:last-child {
        border-bottom: none;
      }

      .status-label {
        font-size: 28rpx;
        color: #718096;
      }

      .status-value {
        font-size: 28rpx;
        font-weight: 600;
        color: #4a5568;
      }
    }
  }

  // 进度动画
  .progress-animation {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 32rpx;
    margin-bottom: 48rpx;

    .document-icon {
      width: 120rpx;
      height: 120rpx;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 30rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      animation: float 2s ease-in-out infinite;

      .icon {
        font-size: 60rpx;
        color: white;
        animation: bounce 1s ease-in-out infinite;
      }
    }

    .animation-dots {
      display: flex;
      gap: 16rpx;

      .animation-dot {
        width: 12rpx;
        height: 12rpx;
        background: #667eea;
        border-radius: 50%;
        animation: pulse 1.5s ease-in-out infinite;
      }
    }
  }

  // 提示信息
  .progress-hint {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    padding: 24rpx;
    background: rgba(102, 126, 234, 0.1);
    border-radius: 16rpx;

    .hint-icon {
      font-size: 28rpx;
    }

    .hint-text {
      font-size: 26rpx;
      color: #4a5568;
    }
  }
}

// 导出成功
.export-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 0;
  text-align: center;

  .success-icon {
    width: 120rpx;
    height: 120rpx;
    background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32rpx;
    animation: scaleIn 0.6s ease-out;

    .icon {
      font-size: 60rpx;
      color: white;
      animation: bounce 1s ease-out;
    }
  }

  .success-title {
    font-size: 40rpx;
    font-weight: 700;
    color: #2d3748;
    margin-bottom: 16rpx;
  }

  .success-message {
    font-size: 28rpx;
    color: #718096;
    margin-bottom: 48rpx;
    max-width: 80%;
  }

  .success-actions {
    display: flex;
    gap: 24rpx;
    width: 100%;

    .action-btn {
      flex: 1;
      height: 88rpx;
      border-radius: 12rpx;
      font-size: 32rpx;
      font-weight: 500;
      border: none;
      transition: all 0.2s ease;

      &.view-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);

        &:active {
          transform: translateY(2rpx);
          box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
        }
      }

      &.share-btn {
        background: #ffffff;
        color: #667eea;
        border: 2rpx solid #667eea;

        &:active {
          background: rgba(102, 126, 234, 0.1);
        }
      }
    }
  }
}

// 动画定义
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20rpx); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10rpx); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1); }
}

@keyframes scaleIn {
  0% { transform: scale(0); opacity: 0; }
  70% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

// 响应式调整
@media (max-width: 480px) {
  .format-option {
    padding: 24rpx !important;

    .format-icon {
      width: 72rpx !important;
      height: 72rpx !important;
      margin-right: 24rpx !important;
    }
  }

  .export-settings,
  .status-info {
    padding: 24rpx !important;
  }
}
</style>