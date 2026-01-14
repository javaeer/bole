<template>
  <view class="task-list-container">
    <!-- 任务列表 -->
    <scroll-view
      class="task-list-content"
      scroll-y="true"
      refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <!-- 空状态 -->
      <view v-if="!loading && taskList.length === 0" class="empty-state">
        <view class="empty-icon">📄</view>
        <text class="empty-title">暂无导出任务</text>
        <text class="empty-desc">快去生成你的第一份简历导出文件吧！</text>
        <button class="empty-action-btn" @click="goToResumeList">
          返回简历列表
        </button>
      </view>

      <!-- 任务卡片列表 -->
      <view v-else class="task-items">
        <view
          v-for="task in taskList"
          :key="task.id"
          class="task-item"
          :class="getTaskStatusClass(task.status)"
        >
          <view class="task-header">
            <view class="task-title-section">
              <text class="task-title">{{ getDocumentTypeLabel(task.documentType) }}</text>
              <view class="task-status-badge" :class="getStatusBadgeClass(task.status)">
                {{ getStatusText(task.status) }}
              </view>
            </view>
            <text class="task-time">{{ formatTime(task.createdAt) }}</text>
          </view>

          <view class="task-content">
            <view class="task-info">
              <view class="info-item">
                <text class="info-label">简历ID：</text>
                <text class="info-value">{{ task.resumesId }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">文件名称：</text>
                <text class="info-value">{{ task.fileName || "--" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">文件大小：</text>
                <text class="info-value">{{ task.fileSize ? formatFileSize(task.fileSize) : "--" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">生成耗时：</text>
                <text class="info-value">{{ formatDuration(task.startAt, task.endAt) }}</text>
              </view>
            </view>

            <!-- 错误信息 -->
            <view v-if="task.status === TaskStatus.FAILED && task.errorMessage" class="task-error">
              <text class="error-icon">⚠️</text>
              <text class="error-text">{{ task.errorMessage }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="task-actions">
            <button
              v-if="task.status === TaskStatus.SUCCESS && task.fileUrl"
              class="action-btn download-action"
              @click="handleDownload(task)"
            >
              <text class="action-icon">⬇️</text>
              <text>下载文件</text>
            </button>

            <button
              v-if="task.status === TaskStatus.SUCCESS && task.fileUrl && isPreviewable(task.documentType)"
              class="action-btn preview-action"
              @click="handlePreview(task)"
            >
              <text class="action-icon">👁️</text>
              <text>在线预览</text>
            </button>

            <button
              v-if="task.status === TaskStatus.FAILED"
              class="action-btn retry-action"
              @click="handleRetry(task)"
            >
              <text class="action-icon">🔄</text>
              <text>重新生成</text>
            </button>

            <button
              v-if="task.status !== TaskStatus.PROCESSING"
              class="action-btn delete-action"
              @click="handleDeleteTask(task)"
            >
              <text class="action-icon">🗑️</text>
              <text>删除</text>
            </button>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="loadingMore" class="load-more">
          <view class="loading-spinner-mini"></view>
          <text>加载更多...</text>
        </view>

        <view v-if="noMoreData && taskList.length > 0" class="no-more-data">
          <text>没有更多任务了</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <button class="action-btn primary-btn" @click="goToCreateTask" v-if="resumeId">
        <text class="icon-add">+</text>
        <text>新建导出任务</text>
      </button>
      <button class="action-btn secondary-btn" @click="clearCompletedTasks">
        清理已完成任务
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import type { TaskResult, TaskStatus } from "@/subpackages/types/task";
import {
  downloadFileCrossPlatform,
  formatDuration,
  formatFileSize,
  formatTime,
  getDocumentTypeLabel,
  getPreviewPagePath,
  getStatusBadgeClass,
  getStatusText,
  getTaskStatusClass,
  isPreviewable,
} from "@/subpackages/utils/task-utils";

// API 导入（假设已经用 TypeScript 重写）
import TaskAPI from "@/api/task";

// 响应式数据
const resumeId = ref<number | null>(null);
const taskList = ref<TaskResult[]>([]);
const loading = ref(false);
const loadingMore = ref(false);
const refreshing = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

// 计算属性
const noMoreData = computed(() => {
  return taskList.value.length >= totalCount.value && totalCount.value > 0;
});

// 任务状态枚举（从类型导入）
const TaskStatus = {
  SUCCESS: 'SUCCESS' as TaskStatus,
  FAILED: 'FAILED' as TaskStatus,
  PROCESSING: 'PROCESSING' as TaskStatus,
};

// 加载任务列表
const loadTaskList = async (page = 1, isRefresh = false): Promise<void> => {
  // 防止重复加载
  if (loading.value || loadingMore.value) return;

  if (isRefresh) {
    refreshing.value = true;
  } else if (page === 1) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }

  try {
    // 构建查询参数
    const pageParam = {
      page: page,
      size: pageSize.value,
    };

    const query: Record<string, any> = {};

    // 如果传入了resumeId，只查询该简历的任务
    if (resumeId.value) {
      query.resumesId = resumeId.value;
    }

    const result = await TaskAPI.getPage(pageParam, query);

    if (result) {
      const { records, total, size, current } = result;

      if (page === 1) {
        taskList.value = records || [];
      } else {
        taskList.value = [...taskList.value, ...(records || [])];
      }

      totalCount.value = total || 0;
      currentPage.value = current || page;

      // 如果有处理中的任务，开始轮询
      const hasProcessingTask = taskList.value.some(task => task.status === TaskStatus.PROCESSING);
      if (hasProcessingTask) {
        startPolling();
      }
    }
  } catch (error) {
    console.error('加载任务列表失败:', error);
    uni.showToast({
      title: '加载失败',
      icon: 'error',
    });
  } finally {
    loading.value = false;
    loadingMore.value = false;
    refreshing.value = false;
  }
};

// 下拉刷新
const onRefresh = (): void => {
  loadTaskList(1, true);
};

// 加载更多
const loadMore = (): void => {
  if (loadingMore.value || loading.value || noMoreData.value) return;
  loadTaskList(currentPage.value + 1);
};

// 刷新任务列表
const refreshTaskList = (): void => {
  loadTaskList(1, true);
};

// 处理下载
const handleDownload = async (task: TaskResult): Promise<void> => {
  if (!task.fileUrl) {
    uni.showToast({
      title: '下载链接不存在',
      icon: 'error',
    });
    return;
  }

  uni.showLoading({
    title: '下载中...',
    mask: true,
  });

  try {
    const result = await downloadFileCrossPlatform(task);

    if (result.success) {
      uni.showToast({
        title: '下载成功',
        icon: 'success',
        duration: 2000,
      });
    } else {
      uni.showToast({
        title: result.error || '下载失败',
        icon: 'error',
      });
    }
  } catch (error) {
    console.error('下载失败:', error);
    uni.showToast({
      title: '下载失败',
      icon: 'error',
    });
  } finally {
    uni.hideLoading();
  }
};

// 在线预览
const handlePreview = (task: TaskResult): void => {
  if (!task.fileUrl) {
    uni.showToast({
      title: '预览链接不存在',
      icon: 'error',
    });
    return;
  }

  const previewPath = getPreviewPagePath(task);
  uni.navigateTo({
    url: previewPath,
  });
};

// 重新生成任务
const handleRetry = async (task: TaskResult): Promise<void> => {
  uni.showModal({
    title: '重新生成确认',
    content: `确定要重新生成${getDocumentTypeLabel(task.documentType)}文件吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({
            title: '提交中...',
            mask: true,
          });

          const newTask = {
            resumesId: task.resumesId,
            documentType: task.documentType,
          };

          const result = await TaskAPI.addTask(newTask);
          uni.hideLoading();

          if (result) {
            uni.showToast({
              title: '已重新提交生成任务',
              icon: 'success',
            });
            // 刷新列表
            refreshTaskList();
          } else {
            uni.showToast({
              title: result.message || '重新生成失败',
              icon: 'error',
            });
          }
        } catch (error) {
          uni.hideLoading();
          console.error('重新生成失败:', error);
          uni.showToast({
            title: '重新生成失败',
            icon: 'error',
          });
        }
      }
    },
  });
};

// 删除任务
const handleDeleteTask = async (task: TaskResult): Promise<void> => {
  uni.showModal({
    title: '删除确认',
    content: '确定要删除这个任务记录吗？删除后无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({
            title: '删除中...',
            mask: true,
          });

          const result = await TaskAPI.deleteTask(task.id);
          uni.hideLoading();

          if (result) {
            uni.showToast({
              title: '删除成功',
              icon: 'success',
            });
            // 从列表中移除
            const index = taskList.value.findIndex(t => t.id === task.id);
            if (index !== -1) {
              taskList.value.splice(index, 1);
              totalCount.value -= 1;
            }
          } else {
            uni.showToast({
              title: result.message || '删除失败',
              icon: 'error',
            });
          }
        } catch (error) {
          uni.hideLoading();
          console.error('删除任务失败:', error);
          uni.showToast({
            title: '删除失败',
            icon: 'error',
          });
        }
      }
    },
  });
};

// 清理已完成任务
const clearCompletedTasks = (): void => {
  uni.showModal({
    title: '清理确认',
    content: '确定要清理所有已完成的任务记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({
            title: '清理中...',
            mask: true,
          });

          const result = await TaskAPI.clearCompletedTasks();
          uni.hideLoading();

          if (result) {
            uni.showToast({
              title: '清理成功',
              icon: 'success',
            });
            // 刷新列表
            refreshTaskList();
          } else {
            uni.showToast({
              title: result.message || '清理失败',
              icon: 'error',
            });
          }
        } catch (error) {
          uni.hideLoading();
          console.error('清理任务失败:', error);
          uni.showToast({
            title: '清理失败',
            icon: 'error',
          });
        }
      }
    },
  });
};

// 跳转到简历列表
const goToResumeList = (): void => {
  uni.switchTab({
    url: '/pages/resumes/list',
  });
};

// 跳转到创建新任务
const goToCreateTask = (): void => {
  if (resumeId.value) {
    uni.navigateTo({
      url: `/pages/resumes/detail?resumeId=${resumeId.value}&mode=view`,
    });
  } else {
    uni.showToast({
      title: '请先选择简历',
      icon: 'none',
    });
  }
};

// 轮询任务状态（针对处理中的任务）
let pollingTimer: number | null = null;
const startPolling = (): void => {
  // 停止之前的轮询
  if (pollingTimer) clearInterval(pollingTimer);

  // 每5秒检查一次处理中的任务
  pollingTimer = setInterval(() => {
    const hasProcessingTask = taskList.value.some(task => task.status === TaskStatus.PROCESSING);
    if (hasProcessingTask) {
      refreshTaskList();
    } else {
      // 没有处理中的任务时停止轮询
      if (pollingTimer) {
        clearInterval(pollingTimer);
        pollingTimer = null;
      }
    }
  }, 5000) as unknown as number;
};

// 页面加载
onLoad((options: Record<string, any>) => {
  console.log('任务列表页面参数:', options);

  if (options.resumeId) {
    resumeId.value = parseInt(options.resumeId);
  }
});

// 页面显示
onShow(() => {
  refreshTaskList();
});

// 组件卸载时清理定时器
onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer);
    pollingTimer = null;
  }
});

// 初始化加载
onMounted(() => {
  loadTaskList();
});
</script>

<style lang="scss" scoped>
.task-list-container {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 任务列表内容 */
.task-list-content {
  flex: 1;
  padding: 20rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 20rpx;
  text-align: center;

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 20rpx;
    color: #999999;
  }

  .empty-title {
    font-size: 32rpx;
    color: #333333;
    font-weight: 600;
    margin-bottom: 16rpx;
  }

  .empty-desc {
    font-size: 28rpx;
    color: #999999;
    margin-bottom: 20rpx;
  }

  .empty-action-btn {
    background: #007AFF;
    color: #ffffff;
    border: none;
    border-radius: 8rpx;
    padding: 20rpx 40rpx;
    font-size: 28rpx;
    font-weight: 500;
  }
}

/* 任务项样式 */
.task-items {
  .task-item {
    background: #ffffff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
    border-left: 8rpx solid #e0e0e0;

    &.task-completed {
      border-left-color: #07C160;
    }

    &.task-failed {
      border-left-color: #FA5151;
    }

    &.task-processing {
      border-left-color: #007AFF;
    }

    .task-header {
      padding: 20rpx 24rpx;
      border-bottom: 1rpx solid #f0f0f0;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .task-title-section {
        display: flex;
        align-items: center;
        gap: 12rpx;

        .task-title {
          font-size: 28rpx;
          font-weight: 600;
          color: #333333;
        }

        .task-status-badge {
          font-size: 22rpx;
          padding: 4rpx 12rpx;
          border-radius: 20rpx;
          font-weight: 500;

          &.status-completed {
            background: rgba(7, 193, 96, 0.1);
            color: #07C160;
          }

          &.status-failed {
            background: rgba(250, 81, 81, 0.1);
            color: #FA5151;
          }

          &.status-processing {
            background: rgba(0, 122, 255, 0.1);
            color: #007AFF;
          }
        }
      }

      .task-time {
        font-size: 24rpx;
        color: #999999;
      }
    }

    .task-content {
      padding: 24rpx;

      .task-info {
        .info-item {
          display: flex;
          margin-bottom: 12rpx;
          font-size: 24rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .info-label {
            color: #666666;
            min-width: 120rpx;
          }

          .info-value {
            color: #333333;
            flex: 1;
          }
        }
      }

      .task-error {
        display: flex;
        align-items: flex-start;
        gap: 8rpx;
        margin-top: 16rpx;
        padding: 12rpx;
        background: rgba(250, 81, 81, 0.05);
        border-radius: 8rpx;
        border-left: 4rpx solid #FA5151;

        .error-icon {
          font-size: 28rpx;
          color: #FA5151;
          flex-shrink: 0;
        }

        .error-text {
          font-size: 24rpx;
          color: #FA5151;
          flex: 1;
        }
      }
    }

    .task-actions {
      display: flex;
      padding: 16rpx 24rpx;
      border-top: 1rpx solid #f0f0f0;
      gap: 12rpx;

      .action-btn {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8rpx;
        padding: 16rpx;
        border-radius: 8rpx;
        font-size: 24rpx;
        font-weight: 500;
        border: none;
        transition: all 0.2s ease;

        &:active {
          opacity: 0.8;
        }

        &.download-action {
          background: rgba(7, 193, 96, 0.1);
          color: #07C160;
        }

        &.preview-action {
          background: rgba(0, 122, 255, 0.1);
          color: #007AFF;
        }

        &.retry-action {
          background: rgba(255, 149, 0, 0.1);
          color: #FF9500;
        }

        &.delete-action {
          background: rgba(250, 81, 81, 0.1);
          color: #FA5151;
        }
      }
    }
  }
}

/* 加载更多 */
.load-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  color: #999999;

  .loading-spinner-mini {
    width: 40rpx;
    height: 40rpx;
    border: 4rpx solid #f0f0f0;
    border-top-color: #007AFF;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 8rpx;
  }
}

.no-more-data {
  text-align: center;
  padding: 20rpx;
  color: #999999;
  font-size: 24rpx;
}

/* 底部操作栏 */
.bottom-actions {
  background: #ffffff;
  padding: 16rpx 20rpx;
  display: flex;
  gap: 12rpx;
  border-top: 1rpx solid #e0e0e0;

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
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

    .icon-add {
      font-size: 32rpx;
      font-weight: 700;
    }
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .task-actions {
    flex-wrap: wrap;

    .action-btn {
      min-width: calc(50% - 6rpx);
    }
  }

  .bottom-actions {
    flex-direction: column;

    .action-btn {
      width: 100%;
    }
  }
}
</style>