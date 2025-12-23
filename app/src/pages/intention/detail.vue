<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
        <text class="header-title">{{ isEditMode ? (detailData.id ? "编辑求职意向" : "添加求职意向") : "求职意向详情"
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
        <!-- 基本信息卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">基本信息</text>
            <view v-if="!isEditMode" class="type-tag" :class="getJobTypeClass(detailData.jobType)">
              {{ detailData.jobType }}
            </view>
          </view>

          <view class="form-container">
            <!-- 期望职位 -->
            <view class="form-group">
              <text class="form-label required">期望职位</text>
              <input
                v-model="formData.position"
                class="form-input"
                :class="{ 'error': errors.position }"
                :disabled="!isEditMode"
                placeholder="请输入期望职位"
                @blur="validateField('position')"
              />
              <text v-if="errors.position" class="error-text">{{ errors.position }}</text>
            </view>

            <!-- 工作城市 -->
            <view class="form-group">
              <text class="form-label required">工作城市</text>
              <picker
                mode="region"
                :value="cityArray"
                :disabled="!isEditMode"
                @change="onCityChange"
              >
                <view class="form-input" :class="{ 'error': errors.city }">
                  {{ formData.city || "请选择工作城市" }}
                </view>
              </picker>
              <text v-if="errors.city" class="error-text">{{ errors.city }}</text>
            </view>

            <!-- 工作类型 -->
            <view class="form-group">
              <text class="form-label required">工作类型</text>
              <picker
                :value="jobTypeIndex"
                :range="jobTypeOptions"
                :disabled="!isEditMode"
                @change="onJobTypeChange"
              >
                <view class="form-input" :class="{ 'error': errors.jobType }">
                  {{ formData.jobType || "请选择工作类型" }}
                </view>
              </picker>
              <text v-if="errors.jobType" class="error-text">{{ errors.jobType }}</text>
            </view>

            <!-- 期望薪资 -->
            <view class="form-group">
              <text class="form-label">期望薪资</text>
              <view class="salary-input-group">
                <input
                  v-model="formData.salary"
                  type="number"
                  class="salary-input"
                  :class="{ 'error': errors.salary }"
                  :disabled="!isEditMode"
                  placeholder="请输入期望月薪"
                  @blur="validateField('salary')"
                />
                <text class="salary-unit">元/月</text>
              </view>
              <text v-if="errors.salary" class="error-text">{{ errors.salary }}</text>
              <view class="salary-preview" v-if="formData.salary">
                <text class="preview-text">{{ formatSalary(formData.salary) }}</text>
                <text class="preview-analysis">{{ getSalaryAnalysis(formData.salary) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 薪资分析卡片 -->
        <view class="info-card card-container" v-if="formData.salary">
          <view class="card-header">
            <text class="card-title">薪资分析</text>
          </view>

          <view class="analysis-content">
            <!-- 薪资水平 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <uni-icons type="money" size="16" color="#d4af37" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">薪资水平</text>
                <text class="analysis-value">{{ getSalaryLevel(formData.salary) }}</text>
              </view>
              <view class="analysis-progress">
                <view
                  class="progress-bar"
                  :style="{ width: `${getSalaryPercentage(formData.salary)}%` }"
                ></view>
              </view>
            </view>

            <!-- 市场对比 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <uni-icons type="stats-bars" size="16" color="#67c23a" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">市场对比</text>
                <text class="analysis-value">{{ getMarketComparison(formData.salary) }}</text>
              </view>
            </view>

            <!-- 建议 -->
            <view class="analysis-item">
              <view class="analysis-icon">
                <uni-icons type="info" size="16" color="#e6a23c" />
              </view>
              <view class="analysis-info">
                <text class="analysis-label">建议</text>
                <text class="analysis-value">{{ getSalarySuggestion(formData.salary) }}</text>
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
        content="确定要删除这份求职意向吗？删除后不可恢复！"
        :before-close="true"
        @confirm="deleteItem"
        @close="closeDeletePopup"
      />
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { dateUtils } from "@/utils/date";
import { JobIntentionItem } from "@/types/job-intention";


interface FormErrors {
  position?: string;
  city?: string;
  jobType?: string;
  salary?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<JobIntentionItem>({
  id: 0,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  position: "",
  city: "",
  salary: "",
  jobType: "",
});

const formData = reactive({
  position: "",
  city: "",
  salary: "",
  jobType: "",
});

const cityArray = ref<string[]>([]);
const errors = reactive<FormErrors>({});
const isEditMode = ref(false);
const saving = ref(false);
const deletePopup = ref();

// 工作类型选项
const jobTypeOptions = ["全职", "兼职", "实习", "远程"];

// 计算属性
const jobTypeIndex = computed(() => {
  const index = jobTypeOptions.findIndex(opt => opt === formData.jobType);
  return index >= 0 ? index : 0;
});

// 获取工作类型样式
const getJobTypeClass = (jobType: string) => {
  switch (jobType) {
    case "全职":
      return "type-fulltime";
    case "兼职":
      return "type-parttime";
    case "实习":
      return "type-intern";
    case "远程":
      return "type-remote";
    default:
      return "type-default";
  }
};

// 格式化薪资
const formatSalary = (salary: string): string => {
  if (!salary) return "面议";

  const num = parseInt(salary);
  if (isNaN(num)) return "面议";

  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万/月`;
  } else {
    return `${num}元/月`;
  }
};

// 薪资分析
const getSalaryAnalysis = (salary: string): string => {
  if (!salary) return "";

  const num = parseInt(salary);
  if (isNaN(num)) return "";

  if (num >= 30000) {
    return "高级职位薪资范围";
  } else if (num >= 15000) {
    return "中级职位薪资范围";
  } else {
    return "初级职位薪资范围";
  }
};

// 薪资水平
const getSalaryLevel = (salary: string): string => {
  if (!salary) return "未设置";

  const num = parseInt(salary);
  if (isNaN(num)) return "未设置";

  if (num >= 50000) return "资深专家";
  if (num >= 30000) return "高级水平";
  if (num >= 15000) return "中级水平";
  if (num >= 8000) return "初级水平";
  return "入门水平";
};

// 薪资百分比（用于进度条）
const getSalaryPercentage = (salary: string): number => {
  if (!salary) return 0;

  const num = parseInt(salary);
  if (isNaN(num)) return 0;

  // 假设10万为最高薪资
  const maxSalary = 100000;
  const percentage = (num / maxSalary) * 100;
  return Math.min(Math.max(percentage, 0), 100);
};

// 市场对比
const getMarketComparison = (salary: string): string => {
  if (!salary) return "未设置";

  const num = parseInt(salary);
  if (isNaN(num)) return "未设置";

  if (num >= 40000) return "高于市场平均水平";
  if (num >= 20000) return "市场平均水平";
  return "低于市场平均水平";
};

// 薪资建议
const getSalarySuggestion = (salary: string): string => {
  if (!salary) return "请设置期望薪资";

  const num = parseInt(salary);
  if (isNaN(num)) return "请设置有效的期望薪资";

  if (num < 5000) {
    return "建议适当提高期望薪资";
  } else if (num > 80000) {
    return "薪资期望较高，建议结合自身能力调整";
  } else {
    return "薪资期望合理";
  }
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: JobIntentionItem = {
        id: id,
        createdAt: "2025-12-22 03:55:22",
        updatedAt: "2025-12-22 10:30:15",
        deleted: 0,
        userId: 1,
        position: "前端开发工程师",
        city: "北京",
        salary: "25000",
        jobType: "全职",
      };

      detailData.value = mockData;
      Object.assign(formData, {
        position: mockData.position,
        city: mockData.city,
        salary: mockData.salary,
        jobType: mockData.jobType,
      });

      // 初始化城市数组
      cityArray.value = mockData.city ? [mockData.city] : [];
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: new Date().toISOString().slice(0, 19).replace("T", " "),
        updatedAt: new Date().toISOString().slice(0, 19).replace("T", " "),
        deleted: 0,
        userId: 1,
        position: "",
        city: "",
        salary: "",
        jobType: "",
      };

      isEditMode.value = true;
      cityArray.value = [];
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
    case "position":
      if (!value?.toString().trim()) {
        errors.position = "请输入期望职位";
      } else {
        delete errors.position;
      }
      break;

    case "city":
      if (!value) {
        errors.city = "请选择工作城市";
      } else {
        delete errors.city;
      }
      break;

    case "jobType":
      if (!value) {
        errors.jobType = "请选择工作类型";
      } else {
        delete errors.jobType;
      }
      break;

    case "salary":
      if (value) {
        const num = parseInt(value);
        if (isNaN(num) || num < 0) {
          errors.salary = "请输入有效的薪资数字";
        } else if (num > 1000000) {
          errors.salary = "薪资不能超过100万";
        } else {
          delete errors.salary;
        }
      } else {
        delete errors.salary; // 薪资可选，可以为空
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("position");
  validateField("city");
  validateField("jobType");
  validateField("salary");

  return Object.keys(errors).length === 0;
};

// 表单事件处理
const onCityChange = (e: any) => {
  const value = e.detail.value;
  if (value && value.length > 0) {
    formData.city = value[value.length - 1]; // 取最后一级（城市）
    cityArray.value = value;
  }
  validateField("city");
};

const onJobTypeChange = (e: any) => {
  const index = e.detail.value;
  formData.jobType = jobTypeOptions[index];
  validateField("jobType");
};

// 保存数据
const saveData = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请填写完整信息",
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
        url: "/pages/job-intention-list/index?refresh=true",
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
        url: "/pages/job-intention-list/index?refresh=true",
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
      position: detailData.value.position,
      city: detailData.value.city,
      salary: detailData.value.salary,
      jobType: detailData.value.jobType,
    });

    // 恢复城市数组
    cityArray.value = detailData.value.city ? [detailData.value.city] : [];

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

  .type-tag {
    padding: 4rpx 12rpx;
    border-radius: $border-radius-small;
    font-size: $font-size-extra-small;
    font-weight: $font-weight-medium;

    &.type-fulltime {
      background: $success-bg;
      color: $success-color;
      border: 1rpx solid $success-border;
    }

    &.type-parttime {
      background: $warning-bg;
      color: $warning-color;
      border: 1rpx solid $warning-border;
    }

    &.type-intern {
      background: $info-bg;
      color: $info-color;
      border: 1rpx solid $info-border;
    }

    &.type-remote {
      background: $primary-light;
      color: $primary-color;
      border: 1rpx solid $primary-border;
    }

    &.type-default {
      background: $background-color;
      color: $text-secondary;
      border: 1rpx solid $border-color-light;
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
  }
}

.form-container {
  .form-group {
    margin-bottom: $margin-base;

    .form-label {
      display: block;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin-bottom: $margin-mini;

      &.required::after {
        content: '*';
        color: $danger-color;
        margin-left: 4rpx;
      }
    }

    .form-input {
      width: 100%;
      padding: 20rpx 24rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;
      transition: all $transition-fast $ease-in-out;
      min-height: 80rpx;
      box-sizing: border-box;

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
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .salary-input-group {
      position: relative;

      .salary-input {
        width: 100%;
        padding: 20rpx 24rpx;
        padding-right: 120rpx;
        border: 2rpx solid $border-color-lighter;
        border-radius: $border-radius;
        font-size: $font-size-base;
        color: $text-primary;
        background: $background-color-white;
        transition: all $transition-fast $ease-in-out;
        box-sizing: border-box;

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
          color: $text-secondary;
          cursor: not-allowed;
        }
      }

      .salary-unit {
        position: absolute;
        right: 24rpx;
        top: 50%;
        transform: translateY(-50%);
        color: $text-secondary;
        font-size: $font-size-base;
      }
    }

    .salary-preview {
      margin-top: 12rpx;
      padding: 16rpx;
      background: $primary-light;
      border-radius: $border-radius-small;
      border: 1rpx solid $primary-border;

      .preview-text {
        display: block;
        font-size: $font-size-medium;
        font-weight: $font-weight-bold;
        color: $primary-color;
        margin-bottom: 4rpx;
      }

      .preview-analysis {
        font-size: $font-size-extra-small;
        color: $text-secondary;
      }
    }

    .error-text {
      display: block;
      font-size: $font-size-extra-small;
      color: $danger-color;
      margin-top: 8rpx;
    }
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

@media (max-width: $screen-md) {
  .detail-header {
    padding: 16rpx $padding-small;
  }

  .detail-content {
    padding: $padding-mini;
  }

  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>