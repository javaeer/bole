<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
                <text class="header-title">{{ isEditMode ? (detailData.id ? "编辑自我评价" : "添加自我评价") : "自我评价详情"
          }}
        </text>
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
        <button class="btn btn-primary" :disabled="saving" @click="saveData">
          {{ saving ? "保存中..." : "保存" }}
        </button>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="detail-scroll" scroll-y>
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
              :show-count="isEditMode"
              @input="onContentInput"
            />
            <text v-if="errors.content" class="error-text">{{ errors.content }}</text>
          </view>
        </view>

        <!-- 关键词卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">关键词管理</text>
            <text class="card-subtitle">（用逗号分隔）</text>
          </view>

          <view class="form-group">
            <view class="keywords-input-group">
              <input
                v-model="keywordInput"
                class="keywords-input"
                :disabled="!isEditMode"
                placeholder="输入关键词后按回车或逗号添加"
                @keyup.enter="addKeyword"
                @keyup.space="addKeyword"
                @blur="addKeyword"
              />
              <button
                v-if="isEditMode && keywordInput"
                class="add-keyword-btn"
                @click="addKeyword"
              >
                <uni-icons type="plus" size="16" color="#fff" />
              </button>
            </view>

            <!-- 关键词提示 -->
            <view class="keywords-hint">
              <text class="hint-text">建议添加3-5个关键词，便于检索和分类</text>
            </view>

            <!-- 关键词列表 -->
            <view class="keywords-list" v-if="keywordArray.length > 0">
              <view
                v-for="(keyword, index) in keywordArray"
                :key="index"
                class="keyword-item"
              >
                <text class="keyword-text">{{ keyword }}</text>
                <uni-icons
                  v-if="isEditMode"
                  type="close"
                  size="12"
                  color="#999"
                  @click="removeKeyword(index)"
                />
              </view>
            </view>

            <!-- 关键词统计 -->
            <view class="keywords-stats" v-if="keywordArray.length > 0">
              <text class="stats-text">{{ keywordArray.length }} 个关键词</text>
              <button
                v-if="isEditMode && keywordArray.length > 0"
                class="clear-all-btn"
                @click="clearAllKeywords"
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
                <uni-icons type="font-size" size="16" color="#d4af37" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">字数统计</text>
                <text class="analysis-value">{{ contentLength }} 字</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${contentLength / 10}%` }"
                ></view>
              </view>
            </view>

            <!-- 关键词数量 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <uni-icons type="tags" size="16" color="#67c23a" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">关键词数量</text>
                <text class="analysis-value">{{ keywordArray.length }} 个</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${keywordArray.length * 20}%` }"
                ></view>
              </view>
            </view>

            <!-- 内容质量 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <uni-icons type="star-filled" size="16" color="#e6a23c" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">内容质量</text>
                <text class="analysis-value">{{ getContentQuality() }}</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${getQualityPercentage()}%` }"
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
              <text class="info-value">{{ dateUtils.format(detailData.createdAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ dateUtils.format(detailData.updatedAt) }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏（编辑模式下） -->
    <view v-if="isEditMode && detailData.id" class="detail-footer">
      <button class="btn btn-danger btn-block" @click="showDeleteConfirm" :disabled="saving">
        删除
      </button>
    </view>

    <!-- 删除确认弹窗 -->
    <uni-popup ref="deletePopup" type="dialog">
      <uni-popup-dialog
        type="warn"
        title="确认删除"
        content="确定要删除这条自我评价吗？删除后不可恢复！"
        :before-close="true"
        @confirm="deleteItem"
        @close="closeDeletePopup"
      />
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { dateUtils } from "@/utils/date";


interface FormErrors {
  content?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<SelfEvaluationItem>({
  id: 0,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  content: "",
  keywords: null,
});

const formData = reactive({
  content: "",
  keywords: null as string | null,
});

const keywordInput = ref("");
const keywordArray = ref<string[]>([]);
const errors = reactive<FormErrors>({});
const isEditMode = ref(false);
const saving = ref(false);
const deletePopup = ref();

// 计算属性
const contentLength = computed(() => {
  return formData.content ? formData.content.length : 0;
});

// 监听关键字数组变化，更新表单数据
watch(keywordArray, (newKeywords) => {
  formData.keywords = newKeywords.length > 0 ? newKeywords.join(",") : null;
}, { deep: true });

// 获取内容质量评估
const getContentQuality = () => {
  const length = contentLength.value;

  if (length === 0) return "待完善";
  if (length < 50) return "简短";
  if (length < 200) return "一般";
  if (length < 500) return "良好";
  return "优秀";
};

const getQualityPercentage = () => {
  const length = contentLength.value;

  if (length === 0) return 0;
  if (length < 50) return 25;
  if (length < 200) return 50;
  if (length < 500) return 75;
  return 100;
};

// 内容输入处理
const onContentInput = () => {
  validateField("content");
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: SelfEvaluationItem = {
        id: id,
        createdAt: "2025-12-22 03:59:05",
        updatedAt: "2025-12-22 10:30:15",
        deleted: 0,
        userId: 1,
        content: "作为一名资深软件工程师，我拥有超过5年的全栈开发经验。熟练掌握Java、Python、JavaScript等多种编程语言，对微服务架构、云原生技术有深入理解。在工作中，我注重代码质量，擅长性能优化和系统架构设计。具备良好的团队协作能力和项目管理经验，能够带领团队高效完成复杂项目。",
        keywords: "资深工程师,全栈开发,微服务,架构设计,团队协作",
      };

      detailData.value = mockData;
      Object.assign(formData, {
        content: mockData.content,
        keywords: mockData.keywords,
      });

      // 初始化关键词数组
      keywordArray.value = mockData.keywords ? mockData.keywords.split(",").map(k => k.trim()).filter(k => k) : [];
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: new Date().toISOString().slice(0, 19).replace("T", " "),
        updatedAt: new Date().toISOString().slice(0, 19).replace("T", " "),
        deleted: 0,
        userId: 1,
        content: "",
        keywords: null,
      };

      isEditMode.value = true;
      keywordArray.value = [];
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
  const value = formData[field as keyof typeof formData];

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
const addKeyword = () => {
  if (!keywordInput.value.trim()) return;

  const keywords = keywordInput.value.split(/[,\s]+/).map(k => k.trim()).filter(k => k);

  keywords.forEach(keyword => {
    if (keyword && !keywordArray.value.includes(keyword) && keywordArray.value.length < 10) {
      keywordArray.value.push(keyword);
    }
  });

  keywordInput.value = "";
};

const removeKeyword = (index: number) => {
  keywordArray.value.splice(index, 1);
};

const clearAllKeywords = () => {
  keywordArray.value = [];
  keywordInput.value = "";
};

// 保存数据
const saveData = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请检查输入内容",
      icon: "error",
    });
    return;
  }

  saving.value = true;

  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 更新数据
    const now = new Date().toISOString().slice(0, 19).replace("T", " ");

    if (detailData.value.id) {
      // 更新
      detailData.value = {
        ...detailData.value,
        ...formData,
        updatedAt: now,
      };
    } else {
      // 新增
      detailData.value = {
        id: Math.floor(Math.random() * 1000) + 6,
        createdAt: now,
        updatedAt: now,
        deleted: 0,
        userId: 1,
        ...formData,
      };
    }

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

    // 返回列表页并刷新
    setTimeout(() => {
      uni.navigateTo({
        url: "/pages/evaluation-list/index?refresh=true",
      });
    }, 1500);

  } catch (error) {
    console.error("保存失败:", error);
    uni.showToast({
      title: "保存失败",
      icon: "error",
    });
  } finally {
    saving.value = false;
  }
};

// 删除项目
const showDeleteConfirm = () => {
  deletePopup.value.open();
};

const deleteItem = async () => {
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 800));

    uni.showToast({
      title: "删除成功",
      icon: "success",
    });

    // 返回列表页并刷新
    setTimeout(() => {
      uni.navigateTo({
        url: "/pages/evaluation-list/index?refresh=true",
      });
    }, 1000);

  } catch (error) {
    console.error("删除失败:", error);
    uni.showToast({
      title: "删除失败",
      icon: "error",
    });
  }
};

const closeDeletePopup = () => {
  deletePopup.value.close();
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true;
};

const cancelEdit = () => {
  if (detailData.value.id) {
    // 恢复原始数据
    Object.assign(formData, {
      content: detailData.value.content,
      keywords: detailData.value.keywords,
    });

    // 恢复关键词数组
    keywordArray.value = detailData.value.keywords ? detailData.value.keywords.split(",").map(k => k.trim()).filter(k => k) : [];

    isEditMode.value = false;
    // 清空错误信息
    Object.keys(errors).forEach(key => delete errors[key as keyof FormErrors]);
  } else {
    // 如果是新增，返回列表页
    uni.navigateBack();
  }
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
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

  .keywords-input-group {
    display: flex;
    gap: $margin-mini;
    margin-bottom: $margin-mini;

    .keywords-input {
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

    .add-keyword-btn {
      padding: 0 24rpx;
      background: $primary-color;
      color: white;
      border: none;
      border-radius: $border-radius;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        background: color.adjust($primary-color, $lightness: -10%);
      }
    }
  }

  .keywords-hint {
    margin-bottom: $margin-base;

    .hint-text {
      font-size: $font-size-extra-small;
      color: $text-placeholder;
      display: block;
    }
  }

  .keywords-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: $margin-base;

    .keyword-item {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 8rpx 16rpx;
      background: $primary-light;
      border-radius: $border-radius-round;
      font-size: $font-size-small;
      color: $primary-color;
      border: 1rpx solid $primary-border;
    }
  }

  .keywords-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: $margin-mini;
    border-top: 1rpx solid $border-color-extra-light;

    .stats-text {
      font-size: $font-size-small;
      color: $text-secondary;
    }

    .clear-all-btn {
      font-size: $font-size-extra-small;
      color: $danger-color;
      background: transparent;
      border: none;
      padding: 4rpx 8rpx;

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
      width: 32rpx;
      height: 32rpx;
      background: $background-color;
      border-radius: $border-radius-round;
      display: flex;
      align-items: center;
      justify-content: center;
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
        background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness:  20%));
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

@media (max-width: $screen-md) {
  .detail-header {
    padding: 16rpx $padding-small;
  }

  .detail-content {
    padding: $padding-mini;
  }

  .form-group {
    .keywords-input-group {
      flex-direction: column;

      .add-keyword-btn {
        width: 100%;
        padding: 16rpx;
      }
    }
  }

  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>