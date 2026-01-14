<!-- subpackages/pages/work/work.vue -->
<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left">
        <text class="header-title">
          {{ isEditMode ? (detailData.id ? "编辑工作经历" : "添加工作经历") : "工作经历详情" }}
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
        <button class="btn btn-primary" :disabled="saving" @click="handleSave">
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
            <view v-if="!isEditMode" class="status-badge">
              <view :class="['work-status', detailData.isCurrent ? 'status-current' : 'status-past']">
                {{ detailData.isCurrent ? "在职" : "离职" }}
              </view>
            </view>
          </view>

          <view class="form-container">
            <!-- 公司名称 -->
            <view class="form-group">
              <text class="form-label required">公司名称</text>
              <input
                v-model.number="formData.company"
                class="form-input"
                :class="{ 'error': errors.company }"
                :disabled="!isEditMode"
                placeholder="请输入公司名称"
                @blur="validateField('company')"
              />
              <text v-if="errors.company" class="error-text">{{ errors.company }}</text>
            </view>

            <!-- 职位 -->
            <view class="form-group">
              <text class="form-label required">职位</text>
              <input
                v-model="formData.position"
                class="form-input"
                :class="{ 'error': errors.position }"
                :disabled="!isEditMode"
                placeholder="请输入职位名称"
                @blur="validateField('position')"
              />
              <text v-if="errors.position" class="error-text">{{ errors.position }}</text>
            </view>

            <!-- 开始时间 -->
            <view class="form-group">
              <text class="form-label required">开始时间</text>
              <picker
                mode="date"
                :value="formData.startDate"
                :disabled="!isEditMode"
                @change="onStartDateChange"
              >
                <view class="form-input" :class="{ 'error': errors.startDate }">
                  {{ formData.startDate || "请选择开始时间" }}
                </view>
              </picker>
              <text v-if="errors.startDate" class="error-text">{{ errors.startDate }}</text>
            </view>

            <!-- 结束时间 -->
            <view class="form-group">
              <text class="form-label" :class="{ 'required': !formData.isCurrent }">结束时间</text>
              <view class="date-input-group">
                <picker
                  v-if="!formData.isCurrent"
                  mode="date"
                  :value="formData.endDate"
                  :disabled="!isEditMode"
                  @change="onEndDateChange"
                >
                  <view class="form-input" :class="{ 'error': errors.endDate }">
                    {{ formData.endDate || "请选择结束时间" }}
                  </view>
                </picker>
                <view v-else class="form-input disabled-input">
                  至今
                </view>
                <view class="checkbox-container">
                  <label class="checkbox-label">
                    <checkbox
                      :checked="formData.isCurrent"
                      @change="onCurrentChange"
                      :disabled="!isEditMode"
                      style="transform: scale(0.8); margin-right: 10rpx;"
                    />
                    <text>至今在职</text>
                  </label>
                </view>
              </view>
              <text v-if="errors.endDate" class="error-text">{{ errors.endDate }}</text>
            </view>

            <!-- 工作时长 -->
            <view v-if="formData.startDate && (formData.endDate || formData.isCurrent)"
                  class="form-group duration-display">
              <text class="duration-label">工作时长</text>
              <text class="duration-value">{{ calculateDurationDisplay() }}</text>
            </view>
          </view>
        </view>

        <!-- 工作描述卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">工作描述</text>
          </view>

          <view class="form-group">
            <text class="form-label">工作职责描述</text>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              :disabled="!isEditMode"
              placeholder="请输入工作职责描述"
              maxlength="1000"
            />
            <view class="textarea-count">
              {{ formData.description.length }}/1000
            </view>
          </view>
        </view>

        <!-- 工作成就卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">工作成就</text>
          </view>

          <view class="form-group">
            <text class="form-label">主要成就</text>

            <!-- 编辑模式下的成就列表 -->
            <view v-if="isEditMode" class="achievements-edit-container">
              <view
                v-for="(achievement, index) in formData.achievements"
                :key="index"
                class="achievement-item-edit"
              >
                <input
                  v-model="formData.achievements[index]"
                  class="achievement-input"
                  :placeholder="`成就 ${index + 1}`"
                  maxlength="200"
                />
                <button
                  v-if="formData.achievements.length > 1"
                  class="btn-remove-achievement"
                  @click="removeAchievement(index)"
                >
                  ×
                </button>
              </view>

              <button class="btn-add-achievement" @click="addAchievement">
                + 添加成就
              </button>

              <view class="achievements-count">
                共 {{ formData.achievements.length }} 项成就
              </view>
            </view>

            <!-- 只读模式下的成就列表 -->
            <view v-else class="achievements-readonly-container">
              <view
                v-for="(achievement, index) in detailData.achievements"
                :key="index"
                class="achievement-item-readonly"
              >
                <view class="achievement-index">{{ index + 1 }}.</view>
                <view class="achievement-content">{{ achievement }}</view>
              </view>

              <view v-if="!detailData.achievements || detailData.achievements.length === 0" class="no-achievements">
                暂无成就信息
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
              <text class="info-value">{{ formatDateTime(detailData.createdAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ formatDateTime(detailData.updatedAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">排序序号</text>
              <text class="info-value">{{ detailData.sort }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏（编辑模式下） -->
    <view v-if="isEditMode && detailData.id" class="detail-footer">
      <button class="btn btn-danger btn-block" @click="handleDelete" :disabled="deleting">
        {{ deleting ? "删除中..." : "删除" }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import type { WorkExperienceForm, WorkExperienceResult } from "@/types/work-experience";
import WorkExperienceAPI from "@/subpackages/api/work-experience";
import { useSaveAndBack } from "@/composables/useSaveAndBack";
import { useDeleteAndBack } from "@/composables/useDeleteAndBack";

interface FormErrors {
  company?: string;
  position?: string;
  startDate?: string;
  endDate?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<WorkExperienceResult & { achievements?: string[] }>({
  id: null,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  company: "",
  position: "",
  startDate: "",
  endDate: "",
  isCurrent: false,
  description: "",
  achievements: [],
  sort: 0,
});

const formData = reactive<WorkExperienceForm>({
  id: null,
  company: "",
  position: "",
  startDate: "",
  endDate: "",
  isCurrent: false,
  description: "",
  achievements: [],
});

const errors = reactive<FormErrors>({});
const isEditMode = ref(false);

// 使用 composable
const { saving, saveAndBack } = useSaveAndBack()
const { deleting, deleteAndBack } = useDeleteAndBack()

// 监听当前状态变化
watch(() => formData.isCurrent, (newVal) => {
  if (newVal) {
    formData.endDate = "";
    delete errors.endDate;
  }
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

// 添加成就
const addAchievement = () => {
  formData.achievements.push("");
};

// 删除成就
const removeAchievement = (index: number) => {
  if (formData.achievements.length > 1) {
    formData.achievements.splice(index, 1);
  }
};

// 计算工作时长显示
const calculateDurationDisplay = () => {
  if (!formData.startDate || (!formData.endDate && !formData.isCurrent)) return "";

  const start = new Date(formData.startDate);
const end = formData.isCurrent ? new Date() : new Date(formData.endDate || new Date());

  const years = end.getFullYear() - start.getFullYear();
  const months = end.getMonth() - start.getMonth();

  let totalMonths = years * 12 + months;
  if (end.getDate() < start.getDate()) {
    totalMonths--;
  }

  if (totalMonths < 0) totalMonths = 0;

  const yearsPart = totalMonths >= 12 ? Math.floor(totalMonths / 12) + "年" : "";
  const monthsPart = totalMonths % 12 > 0 ? (totalMonths % 12) + "个月" : "";

  return `${yearsPart}${monthsPart}`.trim() || "0个月";
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // API请求
      const result = await WorkExperienceAPI.getById(id);

      if (result) {
        // 处理achievements数据：如果是字符串，转换为数组
        let achievementsArray: string[] = [];
        if (result.achievements) {
          if (typeof result.achievements === "string") {
            // 按换行符分割并过滤空行
            achievementsArray = result.achievements
              .split("\n")
              .filter(item => item.trim() !== "")
              .map(item => item.trim());
          } else if (Array.isArray(result.achievements)) {
            // 已经是数组
            achievementsArray = result.achievements;
          }
        }

        // 更新detailData
        detailData.value = {
          ...result,
          achievements: achievementsArray,
        };

        // 填充表单数据
        formData.id = result.id;
        formData.company = result.company || 0;
        formData.position = result.position || "";
        formData.startDate = result.startDate || "";
        formData.endDate = result.endDate || "";
        formData.isCurrent = result.isCurrent || false;
        formData.description = result.description || "";
        formData.achievements = achievementsArray;

        // 确保至少有一个成就输入框
        if (formData.achievements.length === 0 && isEditMode.value) {
          formData.achievements.push("");
        }
      }
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: "",
        updatedAt: "",
        deleted: 0,
        userId: 1,
        company: "",
        position: "",
        startDate: "",
        endDate: "",
        isCurrent: false,
        description: "",
        achievements: [],
        sort: 0,
      };

      isEditMode.value = true;

      // 新增模式下，默认给一个空的成就输入框
      formData.achievements.push("");
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "none",
    });
  }
};

// 表单验证
const validateField = (field: keyof FormErrors) => {
  const value = formData[field as keyof WorkExperienceForm];

  switch (field) {
    case "company":
      if (!value?.toString().trim()) {
        errors.company = "请输入有效的公司名称";
      } else {
        delete errors.company;
      }
      break;

    case "position":
      if (!value?.toString().trim()) {
        errors.position = "请输入职位名称";
      } else {
        delete errors.position;
      }
      break;

    case "startDate":
      if (!value) {
        errors.startDate = "请选择开始时间";
      } else {
        delete errors.startDate;
      }
      break;

    case "endDate":
      if (!formData.isCurrent) {
        if (!value) {
          errors.endDate = "请选择结束时间";
        } else if (formData.startDate && new Date(value) < new Date(formData.startDate)) {
          errors.endDate = "结束时间不能早于开始时间";
        } else {
          delete errors.endDate;
        }
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("company");
  validateField("position");
  validateField("startDate");
  if (!formData.isCurrent) {
    validateField("endDate");
  }

  return Object.keys(errors).length === 0;
};

// 表单事件处理
const onStartDateChange = (e: any) => {
  formData.startDate = e.detail.value;
  validateField("startDate");
  if (formData.endDate) {
    validateField("endDate");
}
};

const onEndDateChange = (e: any) => {
  formData.endDate = e.detail.value;
  validateField("endDate");
};

const onCurrentChange = (e: any) => {
  formData.isCurrent = e.detail.value;
  if (e.detail.value) {
    formData.endDate = "";
    delete errors.endDate;
  } else {
    // 如果从"至今"切换到"离职"，需要重新验证结束时间
    validateField("endDate");
  }
};

// 保存数据
const handleSave = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请填写完整信息",
      icon: "none",
    });
    return;
  }

  // 准备提交数据：过滤空白的成就项
  const submitData = {
    ...formData,
    achievements: formData.achievements.filter(item => item.trim() !== ""),
  };

  const saveFunction = detailData.value.id
    ? () => WorkExperienceAPI.update(submitData)
    : () => WorkExperienceAPI.add(submitData);

  await saveAndBack({
    saveFn: saveFunction,
    successMessage: "保存成功",
    successCallback: () => {
      isEditMode.value = false;
    }
  });
};

// 删除项目
const handleDelete = async () => {
  await deleteAndBack({
    deleteFn: () => WorkExperienceAPI.delete(detailData.value.id!),
    confirmMessage: "确定要删除这份工作经历吗？删除后不可恢复！",
    successMessage: "删除成功",
  });
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true;

  // 确保在编辑模式下有至少一个成就输入框
  if (formData.achievements.length === 0) {
    formData.achievements.push("");
  }
};

const cancelEdit = () => {
  if (detailData.value.id) {
    // 恢复原始数据
    formData.company = detailData.value.company || 0;
    formData.position = detailData.value.position || "";
    formData.startDate = detailData.value.startDate || "";
    formData.endDate = detailData.value.endDate || "";
    formData.isCurrent = detailData.value.isCurrent || false;
    formData.description = detailData.value.description || "";
    formData.achievements = detailData.value.achievements ? [...detailData.value.achievements] : [];

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
</script>

<style lang="scss">
.detail-header {
  @extend .card-container;
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx $padding-base;
  border-radius: 0 0 $border-radius $border-radius;

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
      @extend .btn;
      @extend .btn-small;
      min-width: 80rpx;
    }
  }

  .status-badge {
    .work-status {
      @extend .status-badge;
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;

      &.status-current {
        @extend .status-current;
      }

      &.status-past {
        @extend .status-past;
      }
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
  @extend .card-container;
  margin-bottom: $margin-base;

  .card-header {
    @extend .flex-between;
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
    @extend .form-group;
    margin-bottom: $margin-base;

    .form-label {
      @extend .form-label;
      &.required::after {
        content: '*';
        color: $danger-color;
        margin-left: 4rpx;
      }
    }

    .form-input {
      @extend .form-input;
      width: 100%;
      min-height: 80rpx;
      box-sizing: border-box;

      &.error {
        @extend .error;
      }

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }

      &.disabled-input {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .date-input-group {
      display: flex;
      gap: $margin-mini;

      .form-input {
        flex: 1;
      }

      .checkbox-container {
        flex-shrink: 0;
        display: flex;
        align-items: center;

        .checkbox-label {
          display: flex;
          align-items: center;
          gap: 8rpx;
          font-size: $font-size-small;
          color: $text-primary;
          white-space: nowrap;
        }
      }
    }

    .duration-display {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16rpx;
      background: $background-color;
      border-radius: $border-radius;

      .duration-label {
        font-size: $font-size-small;
        color: $text-secondary;
      }

      .duration-value {
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $primary-color;
      }
    }

    .form-textarea {
      @extend .form-textarea;
      width: 100%;
      min-height: 160rpx;
      line-height: 1.5;
      box-sizing: border-box;
      resize: vertical;

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .textarea-count {
      text-align: right;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      margin-top: 8rpx;
    }

    .error-text {
      display: block;
      font-size: $font-size-extra-small;
      color: $danger-color;
      margin-top: 8rpx;
    }
  }
}

// 成就列表样式
.achievements-edit-container {
  .achievement-item-edit {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;

    .achievement-input {
      flex: 1;
      padding: 20rpx 24rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;

      &:focus {
        border-color: $primary-color;
        outline: none;
      }
    }

    .btn-remove-achievement {
      width: 60rpx;
      height: 60rpx;
      margin-left: 12rpx;
      background: $danger-color;
      color: white;
      border: none;
      border-radius: $border-radius-small;
      font-size: 28rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        opacity: 0.8;
      }
    }
  }

  .btn-add-achievement {
    width: 100%;
    padding: 20rpx;
    background: $primary-color-light;
    color: $primary-color;
    border: 2rpx dashed $primary-color;
    border-radius: $border-radius;
    font-size: $font-size-base;
    margin-top: 8rpx;

    &:active {
      background: $primary-color-light;
    }
  }

  .achievements-count {
    text-align: right;
    font-size: $font-size-extra-small;
    color: $text-secondary;
    margin-top: 8rpx;
  }
}

.achievements-readonly-container {
  .achievement-item-readonly {
    display: flex;
    align-items: flex-start;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .achievement-index {
      width: 40rpx;
      color: $text-secondary;
      font-weight: $font-weight-medium;
    }

    .achievement-content {
      flex: 1;
      color: $text-regular;
      line-height: 1.5;
    }
  }

  .no-achievements {
    padding: 40rpx 0;
    text-align: center;
    color: $text-placeholder;
    font-size: $font-size-base;
  }
}

.system-info {
  .info-row {
    @extend .flex-between;
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

  .form-container {
    .form-group {
      .date-input-group {
        flex-direction: column;
        gap: 12rpx;
      }
    }
  }

  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>