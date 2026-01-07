<!-- pages/evaluation/evaluation.vue -->
<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" >
      </view>

      <view v-if="!isEditMode && detailData.id" class="header-actions">
        <button class="btn btn-secondary" @click="toggleEditMode">
          编辑
        </button>
      </view>
      <view v-else-if="isEditMode" class="header-actions">
        <button class="btn btn-secondary" @click="cancelEdit">
          取消
        </button>
        <button class="btn btn-primary" :disabled="savingRef" @click="handleSave">
          {{ savingRef ? "保存中..." : "保存" }}
        </button>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="detail-scroll" scroll-y @scroll="handleScroll" :scroll-top="scrollTop">
      <view class="detail-content">
        <!-- 主要内容卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">评价内容</text>
            <view class="content-stats">
              <text class="stats-text">{{ contentLength }} 字</text>
            </view>
          </view>

          <view class="form-group">
            <textarea
              v-model="formData.content"
              class="content-textarea"
              :class="{ 'error': errors.content }"
              :disabled="!isEditMode"
              placeholder="请输入自我评价内容，建议200-500字"
              maxlength="1000"
              @input="onContentInput"
            />
            <view class="textarea-count">
              {{ contentLength }}/1000
            </view>
            <text v-if="errors.content" class="error-text">{{ errors.content }}</text>
          </view>
        </view>

        <!-- 关键词卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">关键词管理</text>
            <text class="card-subtitle">（按回车或逗号添加）</text>
          </view>

          <view class="form-group">
            <!-- 关键词输入 -->
            <view v-if="isEditMode" class="highlights-input-group">
              <input
                v-model="highlightInputRef"
                class="highlights-input"
                placeholder="输入关键词后按回车或逗号添加"
                @keyup.enter="addHighlight"
                @keyup.space="addHighlight"
                @blur="handleBlurAddHighlight"
              />
              <button
                v-if="highlightInputRef"
                class="add-highlight-btn"
                @click="addHighlight"
              >
                +
              </button>
            </view>

            <!-- 关键词提示 -->
            <view class="highlights-hint">
              <text class="hint-text">建议添加3-5个关键词，便于检索和分类</text>
              <text class="hint-text">已添加 {{ formData.highlights.length }} 个关键词</text>
            </view>

            <!-- 关键词列表 -->
            <view class="highlights-list" v-if="formData.highlights.length > 0">
              <view
                v-for="(highlight, index) in formData.highlights"
                :key="index"
                class="highlight-item"
              >
                <text class="highlight-text">{{ highlight }}</text>
                <view
                  v-if="isEditMode"
                  class="remove-highlight-btn"
                  @click="removeHighlight(index)"
                >
                  ×
                </view>
              </view>
            </view>

            <!-- 清空按钮 -->
            <view class="highlights-actions" v-if="isEditMode && formData.highlights.length > 0">
              <button
                class="clear-all-btn"
                @click="clearAllHighlights"
              >
                清空全部
              </button>
            </view>
          </view>
        </view>

        <!-- 内容分析卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">内容分析</text>
          </view>

          <view class="analysis-content">
            <!-- 字数统计 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <text class="icon-text">A</text>
              </view>
              <view class="analysis-info">
                <text class="analysis-label">字数统计</text>
                <text class="analysis-value">{{ contentLength }} 字</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${Math.min(contentLength / 10, 100)}%` }"
                ></view>
              </view>
            </view>

            <!-- 关键词数量 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <text class="icon-text">🏷</text>
              </view>
              <view class="analysis-info">
                <text class="analysis-label">关键词数量</text>
                <text class="analysis-value">{{ formData.highlights.length }} 个</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${Math.min(formData.highlights.length * 20, 100)}%` }"
                ></view>
              </view>
            </view>

            <!-- 内容质量 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <text class="icon-text">⭐</text>
              </view>
              <view class="analysis-info">
                <text class="analysis-label">内容质量</text>
                <text class="analysis-value">{{ contentQuality }}</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${qualityPercentage}%` }"
                ></view>
              </view>
            </view>
          </view>
        </view>

        <!-- 系统信息卡片（只读） -->
        <view v-if="detailData.id" class="info-card card-container">
          <view class="card-header">
            <text class="card-title">系统信息</text>
          </view>

          <view class="system-info">
            <view class="info-row">
              <text class="info-label">创建时间</text>
              <text class="info-value">{{ formattedCreatedAt }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ formattedUpdatedAt }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏（编辑模式下） -->
    <view v-if="isEditMode && detailData.id" class="detail-footer">
      <button class="btn btn-danger btn-block" @click="handleDelete" :disabled="deletingRef">
        {{ deletingRef ? "删除中..." : "删除" }}
      </button>
    </view>

    <!-- 回到顶部按钮 -->
    <view v-if="showBackToTop" class="back-to-top" @click="scrollToTop">
      <view class="back-to-top-icon">↑</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onUnmounted, reactive, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import type { SelfEvaluationForm, SelfEvaluationResult } from "@/types/self-evaluation";
import SelfEvaluationAPI from "@/api/self-evaluation";
import { useSaveAndBack } from "@/composables/useSaveAndBack";
import { useDeleteAndBack } from "@/composables/useDeleteAndBack";

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<SelfEvaluationResult>({
  id: 0,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  content: "",
  highlights: [],
});

const formData = reactive<SelfEvaluationForm>({
  id: null,
  content: "",
  highlights: [],
});

// 重命名冲突变量
const highlightInputRef = ref("");
const errors = reactive<FormErrors>({});
const isEditMode = ref(false);

// 滚动相关
const scrollTop = ref(0);
const showBackToTop = ref(false);
let scrollTimer: ReturnType<typeof setTimeout> | null = null;

// 使用 composable
const { saving: savingRef, saveAndBack } = useSaveAndBack();
const { deleting: deletingRef, deleteAndBack } = useDeleteAndBack();

// 计算属性
const contentLength = computed(() => {
  return formData.content ? formData.content.length : 0;
});

const headerTitle = computed(() => {
  if (isEditMode.value) {
    return detailData.value.id ? "编辑自我评价" : "添加自我评价";
  }
  return "自我评价详情";
});

const contentQuality = computed(() => {
  const length = contentLength.value;

  if (length === 0) return "待完善";
  if (length < 50) return "简短";
  if (length < 200) return "一般";
  if (length < 500) return "良好";
  return "优秀";
});

const qualityPercentage = computed(() => {
  const length = contentLength.value;

  if (length === 0) return 0;
  if (length < 50) return 25;
  if (length < 200) return 50;
  if (length < 500) return 75;
  return 100;
});

const formattedCreatedAt = computed(() => {
  return formatDateTime(detailData.value.createdAt);
});

const formattedUpdatedAt = computed(() => {
  return formatDateTime(detailData.value.updatedAt);
});

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 内容输入处理（添加防抖）
let contentInputTimer: ReturnType<typeof setTimeout> | null = null;
const onContentInput = () => {
  if (contentInputTimer) {
    clearTimeout(contentInputTimer);
  }
  contentInputTimer = setTimeout(() => {
    validateField("content");
  }, 300);
};

// 滚动处理
const handleScroll = (e: any) => {
  const scrollTopValue = e.detail.scrollTop;
  showBackToTop.value = scrollTopValue > 300;
};

const scrollToTop = () => {
  scrollTop.value = scrollTop.value ? 0 : 1;
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // API请求
      const result = await SelfEvaluationAPI.getById(id);

      if (result) {
        detailData.value = result;

        // 处理highlights数据：确保是数组
        let highlightsArray: string[] = [];
        if (result.highlights) {
          if (Array.isArray(result.highlights)) {
            highlightsArray = result.highlights;
          } else if (typeof result.highlights === "string") {
            highlightsArray = result.highlights.split(",").map(k => k.trim()).filter(k => k);
          }
        }

        // 填充表单数据
        formData.id = result.id;
        formData.content = result.content || "";
        formData.highlights = highlightsArray;
      }
    } else {
      // 新增模式
      detailData.value = {
        id: null,
        createdAt: "",
        updatedAt: "",
        deleted: 0,
        userId: 1,
        content: "",
        highlights: [],
      };

      isEditMode.value = true;
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  }
};

// 表单验证
const validateField = (field: keyof FormErrors) => {
  const value = formData[field as keyof SelfEvaluationForm];

  switch (field) {
    case "content":
      if (!value?.toString().trim()) {
        errors.content = "请输入自我评价内容";
      } else if (value.toString().trim().length < 10) {
        errors.content = "内容太短，建议至少10个字";
      } else if (value.toString().trim().length > 1000) {
        errors.content = "内容不能超过1000字";
      } else {
        delete errors.content;
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("content");
  return Object.keys(errors).length === 0;
};

// 关键词处理
const handleBlurAddHighlight = () => {
  // 延迟处理，避免与其他事件冲突
  setTimeout(() => {
    addHighlight();
  }, 100);
};

const addHighlight = () => {
  if (!highlightInputRef.value.trim()) return;

  const highlights = highlightInputRef.value.split(/[,\s]+/).map(k => k.trim()).filter(k => k);

  highlights.forEach(highlight => {
    if (highlight && !formData.highlights.includes(highlight) && formData.highlights.length < 10) {
      formData.highlights.push(highlight);
    }
  });

  highlightInputRef.value = "";
};

const removeHighlight = (index: number) => {
  formData.highlights.splice(index, 1);
};

const clearAllHighlights = () => {
  formData.highlights = [];
  highlightInputRef.value = "";
};

// 保存数据
const handleSave = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请检查输入内容",
      icon: "error",
    });
    return;
  }

  // 过滤掉空的关键词
  const submitData = {
    id: formData.id,
    content: formData.content.trim(),
    highlights: formData.highlights.filter(highlight => highlight.trim() !== ""),
  };

  const saveFunction = detailData.value.id
    ? () => SelfEvaluationAPI.update(submitData)
    : () => SelfEvaluationAPI.add(submitData);

  await saveAndBack({
    saveFn: saveFunction,
    successMessage: "保存成功",
    successCallback: () => {
      isEditMode.value = false;
    },
  });
};

// 删除项目
const handleDelete = async () => {
  await deleteAndBack({
    deleteFn: () => SelfEvaluationAPI.delete(detailData.value.id),
    confirmMessage: "确定要删除这条自我评价吗？删除后不可恢复！",
    successMessage: "删除成功",
  });
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true;
};

const cancelEdit = () => {
  if (detailData.value.id) {
    // 恢复原始数据
    formData.content = detailData.value.content || "";

    // 恢复highlights数组
    let highlightsArray: string[] = [];
    if (detailData.value.highlights) {
      if (Array.isArray(detailData.value.highlights)) {
        highlightsArray = detailData.value.highlights;
      } else if (typeof detailData.value.highlights === "string") {
        highlightsArray = detailData.value.highlights.split(",").map(k => k.trim()).filter(k => k);
      }
    }
    formData.highlights = highlightsArray;

    highlightInputRef.value = "";
    isEditMode.value = false;

    // 清空错误信息
    Object.keys(errors).forEach(key => delete errors[key as keyof FormErrors]);
  } else {
    // 如果是新增，返回列表页
    uni.navigateBack();
  }
};

// 清理定时器
const cleanupTimers = () => {
  if (contentInputTimer) {
    clearTimeout(contentInputTimer);
    contentInputTimer = null;
  }
  if (scrollTimer) {
    clearTimeout(scrollTimer);
    scrollTimer = null;
  }
};

// 生命周期
onLoad((options: any) => {
  routeParams.value = options;
  const id = options?.id ? parseInt(options.id) : undefined;
  const edit = options?.edit === "true";

  if (id) {
    loadDetailData(id);
    isEditMode.value = edit;
  } else {
    loadDetailData();
  }
});

onShow(() => {
  // 页面显示时的逻辑
});

onUnmounted(() => {
  cleanupTimers();
});
</script>

<style lang="scss">
.page-container {
  min-height: 100vh;
  background-color: $background-color;
  padding-bottom: calc(env(safe-area-inset-bottom) + 120rpx);
}

.detail-header {
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx $padding-base;
  background: $background-color-white;
  border-radius: 0 0 $border-radius $border-radius;
  box-shadow: $box-shadow;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20rpx;

    .back-icon {
      font-size: 40rpx;
      color: $text-primary;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .header-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }
  }

  .header-actions {
    display: flex;
    gap: $margin-mini;

    .btn {
      padding: 12rpx 24rpx;
      font-size: $font-size-small;
      min-width: 80rpx;
    }
  }
}

.detail-scroll {
  height: calc(100vh - 120rpx);
}

.detail-content {
  padding: $padding-small;
}

.info-card {
  margin-bottom: $margin-base;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base;
    padding-bottom: $margin-mini;
    border-bottom: 1rpx solid $border-color-extra-light;

    .card-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }

    .card-subtitle {
      font-size: $font-size-small;
      color: $text-secondary;
    }

    .content-stats {
      .stats-text {
        font-size: $font-size-small;
        color: $text-secondary;
        background: $background-color;
        padding: 4rpx 12rpx;
        border-radius: $border-radius-small;
      }
    }
  }
}

.form-group {
  .content-textarea {
    width: 100%;
    padding: 24rpx;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    font-size: $font-size-base;
    color: $text-primary;
    background: $background-color-white;
    min-height: 300rpx;
    line-height: 1.6;
    box-sizing: border-box;
    transition: all $transition-fast $ease-in-out;

    &:focus {
      border-color: $primary-color;
      box-shadow: $input-focus-shadow;
      outline: none;
    }

    &.error {
      border-color: $danger-color;
      box-shadow: $input-error-shadow;
    }

    &[disabled] {
      background: $background-color;
      color: $text-regular;
      cursor: not-allowed;
    }
  }

  .textarea-count {
    text-align: right;
    font-size: $font-size-extra-small;
    color: $text-secondary;
    margin-top: 8rpx;
  }

  .highlights-input-group {
    display: flex;
    gap: $margin-mini;
    margin-bottom: $margin-mini;

    .highlights-input {
      flex: 1;
      padding: 20rpx 24rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;

      &:focus {
        border-color: $primary-color;
        box-shadow: $input-focus-shadow;
        outline: none;
      }

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .add-highlight-btn {
      padding: 0 24rpx;
      background: $primary-color;
      color: white;
      border: none;
      border-radius: $border-radius;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;

      &:active {
        background: color.adjust($primary-color, $lightness: -10%);
      }
    }
  }

  .highlights-hint {
    margin-bottom: $margin-base;
    display: flex;
    justify-content: space-between;

    .hint-text {
      font-size: $font-size-extra-small;
      color: $text-placeholder;
      display: block;
    }
  }

  .highlights-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: $margin-base;

    .highlight-item {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 8rpx 16rpx;
      background: $primary-color-light;
      border-radius: $border-radius-round;
      font-size: $font-size-small;
      color: $primary-color;
      border: 1rpx solid $primary-border;

      .remove-highlight-btn {
        width: 20rpx;
        height: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-secondary;
        cursor: pointer;
        font-size: 20rpx;
      }
    }
  }

  .highlights-actions {
    display: flex;
    justify-content: flex-end;
    padding-top: $margin-mini;
    border-top: 1rpx solid $border-color-extra-light;

    .clear-all-btn {
      font-size: $font-size-extra-small;
      color: $danger-color;
      background: transparent;
      border: none;
      padding: 8rpx 16rpx;

      &:active {
        background: $background-color;
        border-radius: $border-radius-small;
      }
    }
  }

  .error-text {
    display: block;
    font-size: $font-size-extra-small;
    color: $danger-color;
    margin-top: 8rpx;
  }
}

.analysis-content {
  .analysis-item {
    display: flex;
    align-items: center;
    gap: $margin-small;
    padding: 20rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .analysis-icon {
      width: 40rpx;
      height: 40rpx;
      background: $background-color;
      border-radius: $border-radius-round;
      display: flex;
      align-items: center;
      justify-content: center;

      .icon-text {
        font-size: 20rpx;
      }
    }

    .analysis-info {
      flex: 1;

      .analysis-label {
        display: block;
        font-size: $font-size-small;
        color: $text-secondary;
        margin-bottom: 4rpx;
      }

      .analysis-value {
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
      }
    }

    .analysis-progress {
      width: 100rpx;
      height: 8rpx;
      background: $background-color;
      border-radius: 4rpx;
      overflow: hidden;

      .progress-bar {
        height: 100%;
        background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness: 20%));
        border-radius: 4rpx;
        transition: width $transition-normal $ease-in-out;
      }
    }
  }
}

.system-info {
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .info-label {
      font-size: $font-size-base;
      color: $text-secondary;
    }

    .info-value {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }
  }
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx $padding-base;
  background: $background-color-white;
  border-top: 1rpx solid $border-color-light;
  z-index: $z-index-base;

  .btn-block {
    width: 100%;
  }
}

// 回到顶部按钮
.back-to-top {
  position: fixed;
  right: 30rpx;
  bottom: calc(env(safe-area-inset-bottom) + 120rpx);
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  background: $primary-color;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $box-shadow-dark;
  z-index: $z-index-dropdown;
  opacity: 0.9;
  transition: all $transition-fast $ease-in-out;

  &:active {
    transform: scale(0.95);
    opacity: 1;
  }

  .back-to-top-icon {
    font-size: 24rpx;
  }
}

@media (max-width: $screen-md) {
  .detail-header {
    padding: 16rpx $padding-small;
  }

  .detail-content {
    padding: $padding-mini;
  }

  .form-group {
    .highlights-input-group {
      flex-direction: column;

      .add-highlight-btn {
        width: 100%;
        padding: 16rpx;
      }
    }
  }

  .detail-footer {
    padding: 16rpx $padding-small;
  }

  .back-to-top {
    right: 20rpx;
    bottom: calc(env(safe-area-inset-bottom) + 100rpx);
    width: 50rpx;
    height: 50rpx;
  }
}
</style>