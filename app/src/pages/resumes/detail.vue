<template>
  <view class="resume-detail-container">
    <!-- 页面头部 -->
    <view class="page-header">
      <view class="header-left">
        <text class="icon-back" @click="handleBack">←</text>
        <text class="header-title">简历详情</text>
      </view>
      <view class="header-actions">
        <button v-if="resumeData.status === 1" class="share-btn" @click="handleShare">
          <text class="icon-share">📤</text>
          <text class="btn-text">分享</text>
        </button>
        <button class="edit-btn" @click="handleEdit">
          <text class="icon-edit">✏️</text>
          <text class="btn-text">编辑</text>
        </button>
      </view>
    </view>

    <!-- 简历基本信息卡片 -->
    <view class="info-card">
      <view class="card-header">
        <text class="card-title">简历信息</text>
        <view class="status-badge" :class="statusClass">
          {{ getStatusText(resumeData.status) }}
        </view>
      </view>

      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">简历ID</text>
          <text class="info-value">{{ resumeData.id || "未设置" }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">创建时间</text>
          <text class="info-value">{{ formatDateTime(resumeData.createdAt) }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">最后更新</text>
          <text class="info-value">{{ formatDateTime(resumeData.updatedAt) }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">模板ID</text>
          <text class="info-value">{{ resumeData.templateId || "未设置" }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">浏览数</text>
          <text class="info-value">{{ resumeData.viewCount || 0 }} 次</text>
        </view>

        <view class="info-item">
          <text class="info-label">下载数</text>
          <text class="info-value">{{ resumeData.downloadCount || 0 }} 次</text>
        </view>
      </view>
    </view>

    <!-- 样式配置信息 -->
    <view class="config-card">
      <view class="card-header">
        <text class="card-title">样式配置</text>
      </view>

      <view class="config-grid">
        <view class="config-item">
          <text class="config-label">主题风格</text>
          <text class="config-value">{{ getThemeName(resumeData.globalStyle?.theme) }}</text>
        </view>

        <view class="config-item">
          <text class="config-label">布局类型</text>
          <text class="config-value">{{ getLayoutName(resumeData.globalLayout?.type) }}</text>
        </view>

        <view class="config-item" v-if="resumeData.globalStyle?.fontFamily">
          <text class="config-label">字体</text>
          <text class="config-value">{{ resumeData.globalStyle.fontFamily }}</text>
        </view>

        <view class="config-item">
          <text class="config-label">主色调</text>
          <view class="color-preview"
                :style="{ backgroundColor: resumeData.globalStyle?.primaryColor || '#1890ff' }"></view>
          <text class="config-value">{{ resumeData.globalStyle?.primaryColor || "#1890ff" }}</text>
        </view>
      </view>
    </view>

    <!-- 包含的组件 -->
    <view class="components-card">
      <view class="card-header">
        <text class="card-title">包含组件</text>
        <text class="components-count">{{ resumeData.components?.length || 0 }} 个</text>
      </view>

      <view class="components-list">
        <view
          v-for="component in resumeData.components"
          :key="component.id"
          class="component-item"
        >
          <text class="component-icon">{{ getComponentIcon(component.key) }}</text>
          <text class="component-name">{{ component.name }}</text>
          <text class="component-key">{{ component.key }}</text>
        </view>
      </view>
    </view>

    <!-- 简历预览区域 -->
    <view class="preview-section">
      <view class="preview-header">
        <text class="preview-title">简历预览</text>
        <view class="preview-actions">
          <button class="preview-action-btn" @click="refreshPreview">
            <text class="action-icon">🔄</text>
            <text class="action-text">刷新</text>
          </button>
          <button class="preview-action-btn" @click="handleFullscreen">
            <text class="action-icon">🖥️</text>
            <text class="action-text">全屏</text>
          </button>
        </view>
      </view>

      <!-- 动态模板引擎渲染 -->
      <view class="resume-preview">
        <DynamicResumesRenderer
          :resume-data="previewData"
          ref="dynamicResumesRenderer"
        />
      </view>
    </view>

    <!-- 操作按钮区域 -->
    <view class="action-buttons">
      <button class="action-btn delete-btn" @click="handleDelete">
        <text class="action-icon">🗑️</text>
        <text class="action-text">删除</text>
      </button>

      <button class="action-btn copy-btn" @click="handleCopy">
        <text class="action-icon">📋</text>
        <text class="action-text">复制</text>
      </button>

      <button class="action-btn download-btn" @click="handleDownload">
        <text class="action-icon">⬇️</text>
        <text class="action-text">下载</text>
      </button>

      <button
        class="action-btn status-btn"
        :class="{ 'published': resumeData.status === 1 }"
        @click="togglePublishStatus"
      >
        <text class="action-icon">{{ resumeData.status === 1 ? "🔒" : "🌐" }}</text>
        <text class="action-text">
          {{ resumeData.status === 1 ? "取消发布" : "发布简历" }}
        </text>
      </button>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>

    <!-- 分享模态框 -->
    <uni-popup ref="sharePopup" type="bottom">
      <view class="share-modal">
        <view class="modal-header">
          <text class="modal-title">分享简历</text>
          <text class="modal-close" @click="closeSharePopup">✕</text>
        </view>

        <view class="share-options">
          <button class="share-option" @click="shareToWeChat">
            <view class="option-icon wechat">💬</view>
            <text class="option-text">微信好友</text>
          </button>

          <button class="share-option" @click="generateQRCode">
            <view class="option-icon qrcode">📱</view>
            <text class="option-text">生成二维码</text>
          </button>

          <button class="share-option" @click="copyShareLink">
            <view class="option-icon link">🔗</view>
            <text class="option-text">复制链接</text>
          </button>

          <button class="share-option" @click="exportAsImage">
            <view class="option-icon image">🖼️</view>
            <text class="option-text">导出为图片</text>
          </button>
        </view>

        <view v-if="qrcodeUrl" class="qrcode-section">
          <text class="qrcode-title">扫描二维码查看简历</text>
          <image :src="qrcodeUrl" class="qrcode-image" mode="widthFix" />
          <text class="qrcode-hint">该链接有效期7天</text>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import DynamicResumesRenderer from "@/components/DynamicResumesRenderer.vue";
import ResumesAPI from "@/api/resumes";

// 响应式数据
const loading = ref(false);
const resumeData = ref({
  id: null,
  createdAt: null,
  updatedAt: null,
  userId: null,
  templateId: null,
  status: 0,
  viewCount: 0,
  downloadCount: 0,
  globalStyle: {},
  globalLayout: {},
  components: [],
});

const resumeId = ref(null);
const sharePopup = ref(null);
const qrcodeUrl = ref("");
const dynamicResumesRenderer = ref(null);

// 主题映射
const themeMap = {
  "light": "明亮",
  "dark": "深色",
  "modern": "现代",
  "classic": "经典",
  "academic": "学术",
};

// 布局映射
const layoutMap = {
  "single-column": "单列",
  "two-column": "双栏",
  "three-column": "三栏",
  "creative": "创意",
};

// 状态映射
const statusMap = {
  0: "草稿",
  1: "已发布",
  2: "已归档",
};

// 组件图标映射
const componentIconMap = {
  "UserBasicInfo": "👤",
  "JobIntention": "🎯",
  "WorkExperience": "💼",
  "EducationExperience": "🎓",
  "SelfEvaluation": "💭",
  "Skills": "⭐",
  "ProjectExperience": "📁",
  "CompanyExperience": "🏢",
};

// 计算属性
const previewData = computed(() => {
  return {
    id: resumeData.value.id,
    templateId: resumeData.value.templateId,
    globalStyle: resumeData.value.globalStyle || {},
    globalLayout: resumeData.value.globalLayout || {},
    components: (resumeData.value.components || []).map(component => ({
      id: component.id,
      componentId: component.componentId,
      name: component.name,
      key: component.key,
      defaultConfig: component.defaultConfig || {},
      props: component.props || {},
      styles: component.styles || {},
    })),
  };
});

const statusClass = computed(() => {
  const status = resumeData.value.status;
  return {
    "status-draft": status === 0,
    "status-published": status === 1,
    "status-archived": status === 2,
  };
});

// 方法定义

// 加载简历数据
const loadResumeData = async () => {
  if (!resumeId.value) {
    uni.showToast({
      title: "简历ID不存在",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
    return;
  }

  loading.value = true;

  try {
    const response = await ResumesAPI.getById(resumeId.value);
    resumeData.value = response;

    // 增加浏览数
    await ResumesAPI.incrementViewCount(resumeId.value);

    console.log("简历详情加载完成:", resumeData.value);
  } catch (error) {
    console.error("加载简历详情失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  } finally {
    loading.value = false;
  }
};

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return "未知";

  try {
    const date = new Date(dateString);
    return date.toLocaleString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    return dateString;
  }
};

// 获取主题名称
const getThemeName = (theme) => {
  return themeMap[theme] || theme || "默认";
};

// 获取布局名称
const getLayoutName = (layout) => {
  return layoutMap[layout] || layout || "默认";
};

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status] || "未知";
};

// 获取组件图标
const getComponentIcon = (key) => {
  return componentIconMap[key] || "📄";
};

// 返回上一页
const handleBack = () => {
  uni.navigateBack();
};

// 编辑简历
const handleEdit = () => {
  uni.navigateTo({
    url: `/pages/resume/edit?resumeId=${resumeId.value}`,
  });
};

// 删除简历
const handleDelete = () => {
  uni.showModal({
    title: "确认删除",
    content: "确定要删除这份简历吗？删除后无法恢复。",
    confirmText: "删除",
    confirmColor: "#ef4444",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          await ResumesAPI.delete(resumeId.value);
          uni.showToast({
            title: "删除成功",
            icon: "success",
          });

          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } catch (error) {
          console.error("删除失败:", error);
          uni.showToast({
            title: "删除失败",
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

// 复制简历
const handleCopy = () => {
  uni.showModal({
    title: "复制简历",
    content: "确定要复制这份简历吗？",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          const copyData = {
            templateId: resumeData.value.templateId,
            globalStyle: { ...resumeData.value.globalStyle },
            globalLayout: { ...resumeData.value.globalLayout },
            components: resumeData.value.components.map(comp => ({
              componentId: comp.componentId,
              name: comp.name,
              key: comp.key,
              defaultConfig: comp.defaultConfig,
              props: { ...comp.props },
              styles: { ...comp.styles },
            })),
          };

          await ResumesAPI.create(copyData);
          uni.showToast({
            title: "复制成功",
            icon: "success",
          });
        } catch (error) {
          console.error("复制失败:", error);
          uni.showToast({
            title: "复制失败",
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

// 下载简历
const handleDownload = async () => {
  loading.value = true;
  try {
    // 增加下载数
    await ResumesAPI.incrementDownloadCount(resumeId.value);

    // 这里可以调用生成PDF的接口
    uni.showToast({
      title: "开始下载",
      icon: "success",
    });

    // 模拟下载过程
    setTimeout(() => {
      uni.showModal({
        title: "下载提示",
        content: "简历已准备好下载，请选择格式",
        showCancel: true,
        cancelText: "PDF",
        confirmText: "图片",
        success: (res) => {
          if (res.confirm) {
            uni.showToast({
              title: "图片格式下载中",
              icon: "success",
            });
          } else if (res.cancel) {
            uni.showToast({
              title: "PDF格式下载中",
              icon: "success",
            });
          }
        },
      });
    }, 1000);

  } catch (error) {
    console.error("下载失败:", error);
    uni.showToast({
      title: "下载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

// 切换发布状态
const togglePublishStatus = () => {
  const newStatus = resumeData.value.status === 1 ? 0 : 1;
  const action = newStatus === 1 ? "发布" : "取消发布";

  uni.showModal({
    title: `${action}简历`,
    content: newStatus === 1
      ? "确定要发布这份简历吗？发布后其他人可以看到您的简历。"
      : "确定要取消发布这份简历吗？取消后其他人将无法查看。",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          await ResumesAPI.updateStatus(resumeId.value, newStatus);
          resumeData.value.status = newStatus;

          uni.showToast({
            title: `${action}成功`,
            icon: "success",
          });
        } catch (error) {
          console.error(`${action}失败:`, error);
          uni.showToast({
            title: `${action}失败`,
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

// 分享简历
const handleShare = () => {
  if (resumeData.value.status !== 1) {
    uni.showModal({
      title: "简历未发布",
      content: "请先发布简历后才能分享",
      showCancel: false,
      success: (res) => {
        if (res.confirm) {
          togglePublishStatus();
        }
      },
    });
    return;
  }

  sharePopup.value.open();
};

// 关闭分享弹窗
const closeSharePopup = () => {
  sharePopup.value.close();
  qrcodeUrl.value = "";
};

// 分享到微信
const shareToWeChat = () => {
  uni.share({
    provider: "weixin",
    scene: "WXSceneSession",
    type: 0,
    href: `${getBaseUrl()}/resume/share/${resumeId.value}`,
    title: "我的简历",
    summary: "查看我的个人简历",
    success: function(res) {
      console.log("分享成功:", res);
      uni.showToast({
        title: "分享成功",
        icon: "success",
      });
    },
    fail: function(err) {
      console.log("分享失败:", err);
      uni.showToast({
        title: "分享失败",
        icon: "error",
      });
    },
  });
};

// 生成二维码
const generateQRCode = async () => {
  const shareUrl = `${getBaseUrl()}/resume/share/${resumeId.value}`;

  try {
    // 这里可以调用生成二维码的接口
    // 暂时使用模拟数据
    qrcodeUrl.value = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodeURIComponent(shareUrl);

    uni.showToast({
      title: "二维码生成成功",
      icon: "success",
    });
  } catch (error) {
    console.error("生成二维码失败:", error);
    uni.showToast({
      title: "生成失败",
      icon: "error",
    });
  }
};

// 复制分享链接
const copyShareLink = () => {
  const shareUrl = `${getBaseUrl()}/resume/share/${resumeId.value}`;

  uni.setClipboardData({
    data: shareUrl,
    success: () => {
      uni.showToast({
        title: "链接已复制",
        icon: "success",
      });
    },
    fail: () => {
      uni.showToast({
        title: "复制失败",
        icon: "error",
      });
    },
  });
};

// 导出为图片
const exportAsImage = () => {
  uni.showModal({
    title: "导出为图片",
    content: "确定要将简历导出为图片吗？",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: "正在生成图片...",
        });

        // 这里可以调用截图或生成图片的接口
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "图片已保存到相册",
            icon: "success",
          });
        }, 2000);
      }
    },
  });
};

// 刷新预览
const refreshPreview = () => {
  if (dynamicResumesRenderer.value) {
    // 可以添加重新加载逻辑
    uni.showToast({
      title: "预览已刷新",
      icon: "success",
      duration: 1500,
    });
  }
};

// 全屏预览
const handleFullscreen = () => {
  uni.navigateTo({
    url: `/pages/resume/preview?resumeId=${resumeId.value}`,
  });
};

// 生命周期
onLoad((options) => {
  console.log("简历详情页面参数:", options);

  if (options.resumeId) {
    resumeId.value = options.resumeId;
    loadResumeData();
  } else {
    uni.showToast({
      title: "参数错误",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});
</script>

<style lang="scss" scoped>
.resume-detail-container {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 100rpx;
}

/* 页面头部 */
.page-header {
  background: white;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16rpx;

    .icon-back {
      font-size: 36rpx;
      color: #666;
      cursor: pointer;
      padding: 8rpx;
      border-radius: 8rpx;

      &:active {
        background: #f1f5f9;
      }
    }

    .header-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }
  }

  .header-actions {
    display: flex;
    gap: 16rpx;

    .share-btn,
    .edit-btn {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 12rpx 24rpx;
      border-radius: 8rpx;
      font-size: 26rpx;
      font-weight: 500;
      border: none;
      background: #f1f5f9;
      color: #64748b;

      .btn-text {
        font-size: 26rpx;
      }

      &:active {
        opacity: 0.8;
      }
    }

    .edit-btn {
      background: #3b82f6;
      color: white;
    }
  }
}

/* 信息卡片样式 */
.info-card,
.config-card,
.components-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  border: 1rpx solid #e2e8f0;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f1f5f9;

    .card-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
    }

    .status-badge {
      padding: 6rpx 16rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      font-weight: 500;

      &.status-draft {
        background: #fef3c7;
        color: #92400e;
      }

      &.status-published {
        background: #d1fae5;
        color: #065f46;
      }

      &.status-archived {
        background: #e5e7eb;
        color: #374151;
      }
    }

    .components-count {
      font-size: 24rpx;
      color: #64748b;
      background: #f1f5f9;
      padding: 4rpx 12rpx;
      border-radius: 12rpx;
    }
  }
}

/* 信息网格 */
.info-grid,
.config-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;

  .info-item,
  .config-item {
    .info-label,
    .config-label {
      display: block;
      font-size: 26rpx;
      color: #666;
      margin-bottom: 8rpx;
    }

    .info-value,
    .config-value {
      display: block;
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
      word-break: break-all;
    }
  }
}

/* 配置项特殊样式 */
.config-item {
  display: flex;
  align-items: center;
  gap: 12rpx;

  .color-preview {
    width: 24rpx;
    height: 24rpx;
    border-radius: 4rpx;
    border: 1rpx solid #e2e8f0;
  }
}

/* 组件列表 */
.components-list {
  display: grid;
  gap: 16rpx;

  .component-item {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 20rpx;
    background: #f8fafc;
    border-radius: 12rpx;
    border: 1rpx solid #e2e8f0;

    .component-icon {
      font-size: 32rpx;
      width: 40rpx;
      text-align: center;
    }

    .component-name {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
    }

    .component-key {
      font-size: 24rpx;
      color: #64748b;
      background: #f1f5f9;
      padding: 4rpx 12rpx;
      border-radius: 12rpx;
    }
  }
}

/* 预览区域 */
.preview-section {
  background: white;
  margin: 30rpx;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  border: 1rpx solid #e2e8f0;

  .preview-header {
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .preview-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
    }

    .preview-actions {
      display: flex;
      gap: 16rpx;

      .preview-action-btn {
        display: flex;
        align-items: center;
        gap: 8rpx;
        background: #f1f5f9;
        border: 1rpx solid #cbd5e1;
        border-radius: 8rpx;
        padding: 12rpx 20rpx;
        font-size: 24rpx;
        color: #64748b;

        .action-icon {
          font-size: 24rpx;
        }

        &:active {
          background: #e2e8f0;
        }
      }
    }
  }

  .resume-preview {
    height: 800rpx;
    overflow-y: auto;
    padding: 30rpx;
    background: #f8fafc;
  }
}

/* 操作按钮区域 */
.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 20rpx 30rpx;
  display: flex;
  gap: 16rpx;
  border-top: 1rpx solid #e2e8f0;
  z-index: 999;

  .action-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 20rpx;
    border-radius: 12rpx;
    border: none;
    font-size: 24rpx;

    .action-icon {
      font-size: 32rpx;
    }

    .action-text {
      font-size: 22rpx;
    }

    &.delete-btn {
      background: #fef2f2;
      color: #dc2626;

      &:active {
        background: #fee2e2;
      }
    }

    &.copy-btn {
      background: #f0f9ff;
      color: #0284c7;

      &:active {
        background: #e0f2fe;
      }
    }

    &.download-btn {
      background: #f0fdf4;
      color: #16a34a;

      &:active {
        background: #dcfce7;
      }
    }

    &.status-btn {
      background: #fef3c7;
      color: #92400e;

      &.published {
        background: #d1fae5;
        color: #065f46;
      }

      &:active {
        opacity: 0.8;
      }
    }
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.loading-content {
  background: white;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}

/* 分享模态框 */
.share-modal {
  background: white;
  border-radius: 24rpx 24rpx 0 0;
  padding: 40rpx 30rpx;
  max-height: 80vh;
  overflow-y: auto;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f1f5f9;

    .modal-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .modal-close {
      font-size: 32rpx;
      color: #666;
      cursor: pointer;
      padding: 8rpx;
      border-radius: 8rpx;

      &:active {
        background: #f1f5f9;
      }
    }
  }

  .share-options {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;
    margin-bottom: 40rpx;

    .share-option {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12rpx;
      padding: 30rpx;
      background: #f8fafc;
      border: 1rpx solid #e2e8f0;
      border-radius: 16rpx;

      &:active {
        background: #e2e8f0;
      }

      .option-icon {
        font-size: 48rpx;
        width: 80rpx;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        margin-bottom: 12rpx;

        &.wechat {
          background: #07c160;
          color: white;
        }

        &.qrcode {
          background: #1890ff;
          color: white;
        }

        &.link {
          background: #faad14;
          color: white;
        }

        &.image {
          background: #52c41a;
          color: white;
        }
      }

      .option-text {
        font-size: 26rpx;
        color: #333;
        font-weight: 500;
      }
    }
  }

  .qrcode-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20rpx;
    padding: 30rpx;
    background: #f8fafc;
    border-radius: 16rpx;
    border: 1rpx solid #e2e8f0;

    .qrcode-title {
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
    }

    .qrcode-image {
      width: 300rpx;
      height: 300rpx;
    }

    .qrcode-hint {
      font-size: 24rpx;
      color: #64748b;
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-grid,
  .config-grid {
    grid-template-columns: 1fr;
  }

  .share-options {
    grid-template-columns: 1fr !important;
  }

  .action-buttons {
    .action-btn {
      .action-text {
        display: none;
      }
    }
  }
}
</style>